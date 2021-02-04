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
* _org.n52.wacodis.javaps.algortihms_: Algorithms that serves as WPS processes
* _org.n52.wacodis.javaps.command_: Models for EO-tool execution commands provided by tool configuration files as well
as Docker execution handlers relying on [docker-java](https://github.com/docker-java/docker-java)
* _org.n52.wacodis.javaps.configuration_: Configuration models
* _org.n52.wacodis.javaps.exceptions_: Process related exceptions
* _org.n52.wacodis.javaps.io_: Encodings and decodings for WPS input data formats
* _org.n52.wacodis.javaps.preprocessing_: Several preprocessing routines for satellite and vector data based on
[SNAP Engine](https://github.com/senbox-org/snap-engine) and [GeoTools library](https://geotools.org/)
* _org.n52.wacodis.javaps.utils_: Some utility classes to avoid boilerplate code

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
* __SentiNel Application Platform__  
For Copernicus sattelite data preprocessing, several processes make use of the ESA's SentiNel Application Platform
via Java API. Therefore, the backend depends on [SNAP Engine](https://github.com/senbox-org/snap-engine) as well as on
[Sentinel-1 toolbox](https://github.com/senbox-org/s1tbx) and [Sentinel-2 toolbox](https://github.com/senbox-org/s2tbx). 
* __docker-java__  
The [Java API client for Docker](https://github.com/docker-java/docker-java) for Docker is used for executing dockerized
EO tools.

## Installation / Building Information
### Build from Source
Again, javaPS WaCoDiS Backend serves as an extra module for [javaPS](https://github.com/52North/javaPS) and therefore is
not executable standalone. For building both, an JavaPS WaCoDiS Backend artifact and an executable javaPS WAR file, follow
the steps listed below:
1. Build the JavaPS WaCoDiS Backend with `mvn clean install` from the project root.
2. Checkout https://github.com/52North/javaPS from GitHub. However, we recommend using our [javaPS fork](https://github.com/WaCoDiS/javaPS)
which provides a branch https://github.com/WaCoDiS/javaPS/tree/wacodis-developments that contains some custom adjustments
to work well with our backend.
3. Build the javaPS but omit the webapp module. Just run `mvn clean install -DskipTests -pl !webapp` from the javaPS
project root.
4. Replace the _pom.xml_ inside the javaPS webapp module with the (custom-webapp-pom.xml)[./docker/custom-webapp-pom.xml].
5. Build the javaPS webapp module by running `mvn -f ./webapp/custom-webapp-pom.xml package -DskipTests -Denforcer.skip=true`
from the javaPS project root.
6. Deploy the WAR file, that have been built in the _target_ folder of the javaPS webapp module, within your favorite web
server e.g. Tomcat. 
 
### Build using Docker
For building a javaPS docker image run `docker build -t wacodis/javaps:latest --build-arg CACHE_DATE=$(date) .`.
The build time argument `CACHE_DATE` can be used to invalidate cache in order to only build the changed _javaps-wacodis-backend_.

For running the javaPS WaCoDiS Backend as Docker container, the [deployment section](#run-with-docker) provides some
helpful information

### Configuration
To configure your javaPS instance including the WaCoDiS backend, you have to consider both the configuration related to
javaPS and config parameters that directly refers to the backend.
#### javaPS configuration
You'll find the javaPS configuration file inside the webapp module within _src/main/webapp/WEB-INF/config_. Here you can
set different parameters related to the server. Note, that you especially have to set the correct server URL for the
`service.serviceURL` parameter so that javaPS generates proper download links for its outputs. In addition, all configuration
parameters can be set via environment variables. E.g., `service.serviceURL` can be set via `SERVICE_SERVICE_URL`.  
#### WaCoDiS backend configuration
Configuration parameters meant for the javaPS WaCoDiS Backend can be defined inside the properties file
_src/main/resources/wacodis-javaps.properties_ or as environment variables.  

In order to access satellite data from [Copernicus Open Access Hub](https://scihub.copernicus.eu/) you have to specify
valid credentials:  
* `openaccesshub.username`: ESA Open Access Hub username
* `openaccesshub.password`: ESA Open Access Hub password

You also have to specify different directories to be used as working directory or to look for process related files: 
* `wacodis.javaps.workdir`: Absoulte path to the working directory. All downloaded and preprocessed files will be
stored at this location
* `wacodis.javaps.toolconfigdir`: Absolute path to the directory which contains the config files for executing the EO tools
* `wacodis.javaps.gpfdir`: Absolute path to the directory which contains the SNAP GPF XML-Files for preprocessing Sentinel satellite data.  

Some additional parameters:
* `wacodis.javaps.epsg`: EPSG code representing the reference coordinate system that will be used for reprojecting all
input data.
* `wacodis.javaps.sentineltestfile` (optional): Absolute path to a Sentinel-2 product in SAFE-format that can be used for
 testing purposes in order to prevent downloading Sentinel-2 products from a certain platform, which may be very time-consuming.


## Deployment
### Dependencies
If you choose to build the project [from source](#build-from-source), the deployment of the javaPS WaCoDiS Backend highly
depends on javaPS, since the backend can not be deployed standalone. In addition, a web container is required for deploying
the javaPS including the bundled WaCoDiS Backend.  

If you choose to build the project [with Docker](#build-using-docker), the deployment only depends on the availability
of [Docker Engine](https://docs.docker.com) within your runtime environment. No additional software stacks are required
in this case. 
### Run with Docker
A pre-built Docker image can be pulled from [DockerHub](https://hub.docker.com/r/wacodis/javaps-wacodis-backend). You can
simply start the container with docker-compose. A _docker-compose.yml_ is provided within the [docker directory](docker).
Just run `docker-compose --compatibility up`. The `--compatibility` flag enables memory configurations specified within 
the _docker-compose.yml_.  

If you prefer _docker run_ you can also execute `docker run -p 8080:8080 --env-file ./wacodis.env wacodis/javaps:latest `
or any similar command.

Be sure to overwrite the properties in `wacdodis.env` just to provide the credentials for the Copernicus Open Access Hub
and to set the working directories. The [docker directory](docker) also contains [configuration files used for the execution
of containerized EO-tools](docker/config/tools) as well as [graph XML files for running GPF graphs](docker/config/gpf).
Just copy those files to a new location and point the relevant properties within `wacdodis.env` to this location. 
### Run with Tomcat
To run javaPS including the WaCoDiS Backend within a Tomcat instance, you first have to build the project [from source](#build-from-source).
Be sure, you have set proper configuration parameters for [javaPS](#javaps-configuration) and [javaPS](#wacodis-backend-configuration)
inside the configuration files or via environment variables. You'll find
[configuration files used for the execution containerized EO-tools](docker/config/tools) as well as 
[graph XML files for running GPF graphs](docker/config/gpf) inside the [docker directory](docker). Just point
`wacodis.javaps.toolconfigdir` and `wacodis.javaps.gpfdir` to these locations. Note, that each EO-tool configuration
specifies a Docker Host URL that points to the Unix socket, i.e. `unix:///var/run/docker.sock`. If you aim to run the
javaPS on a Windows system change the Docker Host URL to `tcp://localhost:2376`.

## User Guide
javaPS implements the [OGC WPS 2.0 interface](http://docs.opengeospatial.org/is/14-065/14-065.html). To execute WPS
processes send your requests to the following endpoint: http://localhost:8080/wacodis-javaps/service. For convenience,
you'll find a ready-to-use [Postman](https://www.postman.com/) collection that contains execution requests for all
supported processes inside [docs folder](docs). Just import the collection within Postman, set appropriate values for
the process inputs and send your POST requests to the WPS.

## Developer Information
### How to contribute
#### Create custom processes
You can simply develop your own algorithms as javaPS processes. Just follow the detailed
[javaPS documentation](https://52north.github.io/javaPS/documentation_markdown/site/algorithm_definition/algorithm_definition/#javaps-documentation-adding-new-processesalgorithms).
Of course, you can implement you algorithms however you want and use your favorite library to do so. However, we recommend
following one of the patterns almost all the processes within this repository rely on.  
#### GPF operators
Some processes within this repository utilize the [SNAP Graph Processing Framework](https://senbox.atlassian.net/wiki/spaces/SNAP/pages/70503590/Creating+a+GPF+Graph)
via Java API. Those algorithms rather execute single GPF operators in standalone mode without any complex graph definitions.
Only a small subset of Sentinel-1 or Sentinel-2 toolbox operators are valuable, namely those which produce a
ready-to-use product. Hence, some basic indices-based operators are embedded within separate algorithms so far. You will
find those algorithms inside the _org.n52.wacodis.javaps.algortihms.package_.

If you want to provide you own algorithm that
makes use of SNAP GPF operators, just implement `AbstractSnapAlgorithm` which already provides some boilerplate code
for executing operators via GPF. Make sure, you are confirm with all supported parameters for your operator. E.g., the
parameters for the NDVI operator have been discovered from the [NdviOp class](https://github.com/senbox-org/snap-engine/blob/master/snap-ndvi/src/main/java/org/esa/snap/ndvi/NdviOp.java).  
#### GPF preprocessing
[SNAP Graph Processing Framework](https://senbox.atlassian.net/wiki/spaces/SNAP/pages/70503590/Creating+a+GPF+Graph) can not
only be used for executing operators in standalone mode but also for applying complex preprocessing graphs on satellite data
to prepare large datasets for the [execution via containerized EO-tools](#docker-processes). To do so, it
is best practice to prepare your GPF graph via [SNAP](https://step.esa.int/main/download/snap-download/), export it as 
XML file and add it to the javaPS. Finally, you can make use of `org.n52.wacodis.javaps.preprocessing.gpt.GptExecutor`
to execute your graph as part of the preprocessing within your custom algorithm.
#### Docker tools
To get the best out of your satellite data processing and to generate valuable products, you can provide your processing
algorithms as Docker containers that will be triggered by an WPS process.  
1. Dockerize your EO-tool (e.g. a Python script) in a way that allows its execution and passing by required arguments
at start-up time. Note, that all input and output data must be provided within a single working directory within the container
which well be volume-binded to the javaPS working directory on the host.
2. Provide a tool config file tha describes the execution of the dockerized EO-tool. You will find a template and several
examples inside [docker/config/tools](docker/config/tools). Note that there are two types of command arguments that can
be defined. `static-option` means the provided `value` will be used as is for the execution command. In contrast, 
`wps-process-reference` arguments must explicitly be defined inside the WPS process and its values will be set at runtime.
Have a look on how this is done for [VegetationDensityNdviAlgorithm](https://github.com/WaCoDiS/javaps-wacodis-backend/blob/b8e246cee48ab3357defc0149bccf8eb930bff22/src/main/java/org/n52/wacodis/javaps/algorithms/VegetationDensityNdviAlgorithm.java#L110-L118).
3. Implement `org.n52.wacodis.javaps.algorithms.AbstractAlgorithm` which provides several reusable methods. Simply follow
the way this have been already done for the existing algorithms inside _org.n52.wacodis.javaps.algortihms_.
4. Register your algorithm as a component within [wacodis-backend.xml]src/main/resources/components/wacodis-backend.xml).
5. Have fun with your custom algorithm.

### Branching
The master branch provides sources for stable builds. The _develop_ branch represents the latest (maybe unstable)
state of development.
### License and Third Party Lib POM Plugins
TODO

## Contact
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Sebastian Drost | Bochum University of Applied Sciences | sebastian.drost@hs-bochum.de |
| Arne Vogt | Bochum University of Applied Sciences | arne.vogt@hs-bochum.de |
| Matthes Rieke | 52° North GmbH | m.rieke@52north.org |
| Adrian Klink | EFTAS Fernerkundung Technologietransfer GmbH | adrian.klink@eftas.com |

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