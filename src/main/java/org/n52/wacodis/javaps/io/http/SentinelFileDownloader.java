/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.wacodis.javaps.io.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.n52.wacodis.javaps.configuration.WacodisBackendConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Helper class for downloading Sentinel image files.
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class SentinelFileDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(SentinelFileDownloader.class);

    private static final String ZIP_EXTENSION = "zip";

    private static final String SAFE_EXTENSION = "SAFE";

    private final Map<String, File> productToFileCache = new HashMap<>();

    private WacodisBackendConfig config;

    private RestTemplate openAccessHubService;

    @Autowired
    public void setConfig(WacodisBackendConfig config) {
        this.config = config;
    }

    @Autowired
    public void setOpenAccessHubService(RestTemplate openAccessHubService) {
        this.openAccessHubService = openAccessHubService;
    }

    /**
     * Downloads a Sentinel-2 image file from the specified URL and writes it to
     * the default working directory and unzips the file.
     *
     * @param url URL for the Sentinel-2 image.
     * @return the file that contains the image
     * @throws IOException if internal file handling fails for some reason
     */
    public File downloadSentinelFile(String url) throws IOException {
        return downloadSentinelFile(url, config.getWorkingDirectory());
    }

    /**
     * Downloads a Sentinel-2 image file from the specified URL, writes it to
     * the specified location and unzips the file.
     *
     * @param url     URL for the Sentinel-2 image.
     * @param outPath Path to the directory to save the image file in
     * @return the file that contains the image
     * @throws IOException if internal file handling fails for some reason
     */
    public File downloadSentinelFile(String url, String outPath) throws IOException {
        return this.downloadSentinelFile(url, outPath, true);
    }

    /**
     * Downloads a Sentinel-2 image file from the specified URL and writes it to
     * the specified location.
     *
     * @param url     URL for the Sentinel-2 image.
     * @param outPath Path to the directory to save the image file in
     * @param unzip   Specify whether to unzip the file or not
     * @return the file that contains the image
     * @throws IOException if internal file handling fails for some reason
     */
    public File downloadSentinelFile(String url, String outPath, boolean unzip) throws IOException {
        if (config.getSentinelTestFile() != null && !config.getSentinelTestFile().isEmpty()) {
            return this.retrieveSentinelTestFile(outPath, unzip);
        }
        LOG.info("Downloading Sentinel product: {}", url);
        File cached = this.resolveProductFromCache(url);
        if (cached != null) {
            LOG.info("Returning cached version of the product: {}", url);
            return cached;
        }

        // Optional Accept header
        RequestCallback callback = (ClientHttpRequest request) -> {
            request.getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        };

        ResponseExtractor<File> responseExtractor = (ClientHttpResponse response) -> {
            if (response.getHeaders().getContentLength() == 0) {
                throw new IOException(String.format("No Sentinel file available for URL '%s' is not available.", url));
            }
            String fileName = response.getHeaders().getContentDisposition().getFilename();
            File imageFile = new File(outPath + "/" + fileName);
            FileUtils.copyInputStreamToFile(response.getBody(), imageFile);
            return imageFile;
        };

        try {
            File imageFile = openAccessHubService.execute(url, HttpMethod.GET, callback, responseExtractor);
            if (unzip && FilenameUtils.getExtension(imageFile.getName()).equals(ZIP_EXTENSION)) {
                imageFile = this.unzipFile(imageFile, outPath, false);
            }

            LOG.info("Downloading of Sentinel product successful: {}", url);

            synchronized (SentinelFileDownloader.this) {
                productToFileCache.put(url, imageFile);
            }

            return imageFile;
        } catch (HttpStatusCodeException ex) {
            LOG.error("GET request for Sentinel file {} returned status code: {}.",
                    ex.getStatusCode());
            throw new IOException(ex);
        } catch (RestClientException ex) {
            LOG.error("Unexpected client error while requesting Sentinel file ", ex.getMessage());
            throw new IOException(ex);
        }
    }

    private synchronized File resolveProductFromCache(String url) {
        if (this.productToFileCache.containsKey(url)) {
            File candidateFile = this.productToFileCache.get(url);
            if (candidateFile != null && candidateFile.exists()) {
                return candidateFile;
            } else {
                // file does not exist any longer, remove it
                this.productToFileCache.remove(url);
            }
        }

        // no match found
        return null;
    }

    /**
     * Unzips a zipped Sentinel image at the specified location
     *
     * @param file    {@link File} to unzip
     * @param outPath Path at which the file will be unzipped
     * @return The unzipped SAFE file
     * @throws IOException
     */
    public File unzipFile(File file, String outPath, boolean keepZip) throws IOException {
        File unzippedFile = null;
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            boolean firstEntry = true;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(outPath, entry.getName());
                if (firstEntry) {
                    unzippedFile = entryDestination;
                    firstEntry = false;
                }
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryDestination)) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
        if (!keepZip) {
            file.delete();
        }
        return unzippedFile;
    }

    private File retrieveSentinelTestFile(String outPath, boolean unzip) throws IOException {
        File testFile = new File(config.getSentinelTestFile());
        if (unzip && FilenameUtils.getExtension(testFile.getName()).equals(ZIP_EXTENSION)) {
            testFile = this.unzipFile(testFile, outPath, true);
        }
        return testFile;
    }

}
