# Camunda Java POC
This project implements a SpringBoot wrapper around the Camunda Service. It provides the following:
- Startup of the Camunda Service and REST API
- JMS Interaction with the Camunda Service
- A custom REST API that is used to access the underlying Java API and provide necessary features for the POC Client
- Unit tests that exercise the features and test workflows used in the POC

## Running the Demo
To start the Service, first make sure an activemq broker is running locally, then run:

`./mvnw spring-boot:run`

## Documentation
Full documentation for the POC can be found at: https://vineti.atlassian.net/wiki/spaces/EN/pages/2033254433/Camunda+API+vs+Rest