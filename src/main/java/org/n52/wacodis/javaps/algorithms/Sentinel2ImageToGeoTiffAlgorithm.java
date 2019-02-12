/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.wacodis.javaps.algorithms;

import java.io.File;
import org.n52.javaps.algorithm.annotation.Algorithm;
import org.n52.javaps.algorithm.annotation.Execute;
import org.n52.javaps.algorithm.annotation.LiteralInput;
import org.n52.javaps.algorithm.annotation.LiteralOutput;
import org.n52.wacodis.javaps.io.http.SentinelFileDownloader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This example process shows how to use the SNAP Engine and Sentinel-2 Toolbox
 * Java API for reading, resampling and writing different product image types.
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Algorithm(
        identifier = "de.hsbo.wacodis.snap.s2togeotiff",
        title = "Sentinel-2 Download to GeoTiff Process",
        abstrakt = "Perform a Sentinel-2 file download, resample the image and "
        + "write it out as GeoTiff.",
        version = "0.0.1",
        storeSupported = true,
        statusSupported = true)
public class Sentinel2ImageToGeoTiffAlgorithm {

    @Autowired
    private SentinelFileDownloader fileDownloader;

    private String imageUrl;
    private String product;

    @LiteralInput(
            identifier = "SENTINEL-2_URL",
            title = "Sentinel-2 URL",
            abstrakt = "Sentinel-2 URL from Open Access Hub",
            minOccurs = 1,
            maxOccurs = 1
    )
    public void setReferenceData(String value) {
        this.imageUrl = value;
    }

    @Execute
    public void execute() {
        File file = fileDownloader.downloadSentinelFile(imageUrl);
        this.product = file.getPath();

    }

    @LiteralOutput(identifier = "PRODUCT")
    public String getOutput() {
        return this.product;
    }

}