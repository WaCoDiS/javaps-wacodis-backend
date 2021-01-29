# JavaPS WaCoDiS Backend
![Build](https://github.com/WaCoDiS/javaps-wacodis-backend/workflows/Build/badge.svg)  
Processing backend for javaPS (OGC WPS implementation) containing processes related to the WaCoDiS project for creating
Earth Observation products.

## WaCoDiS Project Information
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/wacodis.png" width="200">
</p>
Climate changes and the ongoing intensification of agriculture effect in increased material inputs in watercourses and dams.
Thus, water industry associations, suppliers and municipalities face new challenges. To ensure an efficient and environmentally
friendly water supply for the future, adjustments on changing conditions are necessary. Hence, the research project WaCoDiS
aims to geo-locate and quantify material outputs from agricultural areas and to optimize models for sediment and material
inputs (nutrient contamination) into watercourses and dams. Therefore, approaches for combining heterogeneous data sources,
existing interoperable web based information systems and innovative domain oriented models will be explored.

### Architecture Overview

For a detailed overview about the WaCoDiS system architecture please visit the 
**[WaCoDiS Core Engine](https://github.com/WaCoDiS/core-engine)** repository.  

## Overview  

### Scope
The JavaPS WaCoDiS Backend aims as backend for the great [javaPS](https://github.com/52North/javaPS) and provides processing
algorithms for generating valuable products (EO-products) from Earth Observation data. Therefore, the provided processes contain basic
preprocessing routines for Copernicus satellite data and encapsulate the execution of dockerized tools with purpose for
running on both, self hosted processing infrastructures and satellite data platforms with embedded processing environments
like [CODE-DE](https://code-de.org/de/).

JavaPS WaCoDiS Backend serves as a processing backend for javaPS and comprises only a single module which contains the
EO processing routines. Therefore, it is not executable as stand-alone application but requires to be executed as part
of javaPS.
### Packages
The section below gives a comprehensive overview about the package structure:
* org.n52.wacodis.javaps.algortihms
Algorithms that serves as WPS processes
* org.n52.wacodis.javaps.command
Models for EO-tool execution commands provided by tool configuration files as well Docker execution handlers relying on
[docker-java](https://github.com/docker-java/docker-java)
* org.n52.wacodis.javaps.configuration
Configuration models
* org.n52.wacodis.javaps.exceptions
Process related exceptions
* org.n52.wacodis.javaps.io
Encodings and decodings for WPS input data formats
* org.n52.wacodis.javaps.preprocessing
Several preprocessing routines for satellite and vector data based on [SNAP Engine](https://github.com/senbox-org/snap-engine)
and [GeoTools library](https://geotools.org/)
* org.n52.wacodis.javaps.utils
Some utility classes to avoid boilerplate code

### Technologies/Dependencies  
* __Java__  
WaCoDiS Metadata Connector is tested with Oracle JDK 8 and OpenJDK 8. Unless stated otherwise later Java versions can be
used as well.
* __Maven__  
This project uses the build-management tool [Apache Maven](https://maven.apache.org/).
* __javaPS__
As a backend for [javaPS](https://github.com/52North/javaPS) the WaCoDiS Backend relies on different javaPS modules. 
Especially, the processing algorithms depends on the javaPS Core Engine module. Furthermore, I/O handler for different
data formats are reused from [javaPS IO-handler](https://github.com/52North/javaps-iohandler) and
[javaPS GeoTools IO-handler](https://github.com/52North/javaps-iohandler-gt).
* __GeoTools__
The [GeoTools library](https://geotools.org/) is used for common vector data preprocessing tasks.
*__SentiNel Application Platform__
For Copernicus sattelite data preprocessing, several processes make use of the ESA's SentiNel Application Platform
via Java API. Therefore, the backend depends on [SNAP Engine](https://github.com/senbox-org/snap-engine) as well as on
[Sentinel-1 toolbox](https://github.com/senbox-org/s1tbx) and [Sentinel-2 toolbox](https://github.com/senbox-org/s2tbx). 
*__docker-java__
The [Java API client for Docker](https://github.com/docker-java/docker-java) for Docker is used for executing dockerized
EO tools.

## Installation / Building Information
### Build from Source
TODO
### Build using Docker
For building a javaPS docker image run `docker build -t wacodis/javaps:latest --build-arg CACHE_DATE=$(date) .`.
The build time argument `CACHE_DATE` can be used to invalidate cache in order to only build the changed _javaps-wacodis-backend_.

### Configuration
Be sure to overwrite the properties in `wacdodis.env` just to provide the credentials for the Copernicus Open Access Hub and to set a working directory.
#### Parameters
TODO
## Deployment
### Dependencies
TODO
### Run with Docker
You can simply run the container with Compose. To enable memory configurations use the following command: `docker-compose --compatibility up`
If you prefer `docker run` you can also use `docker run -p 8080:8080 --env-file ./wacodis.env wacodis/javaps:1.x ` or execute a customized command.
### Run with Tomcat
TODO

## User Guide
To execute WPS processes send your requests to the following endpoint: http://localhost:8080/wacodis-javaps/service.

## Developer Information
### How to contribute
TODO
### Branching
The master branch provides sources for stable builds. The develop branch represents the latest (maybe unstable)
state of development.
### License and Third Party Lib POM Plugins
TODO

## Contact
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Sebastian Drost | Bochum University of Applied Sciences | sebastian.drost@hs-bochum.de |
| Arne Vogt | Bochum University of Applied Sciences | arne.vogt@hs-bochum.de |
| Matthes Rieke | 52° North GmbH | m.rieke@52north.org |

## Credits and Contributing Organizations
- Department of Geodesy, Bochum University of Applied Sciences, Bochum
- 52° North Initiative for Geospatial Open Source Software GmbH, Münster
- Wupperverband, Wuppertal
- EFTAS Fernerkundung Technologietransfer GmbH, Münster

The research project WaCoDiS is funded by the BMVI as part of the [mFund programme](https://www.bmvi.de/DE/Themen/Digitales/mFund/Ueberblick/ueberblick.html)  
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/mfund.jpg" height="100">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/bmvi.jpg" height="100">
</p>