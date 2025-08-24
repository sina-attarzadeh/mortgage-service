# Mortgage Service

A Spring Boot application for managing mortgage rates and feasibility checks.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [How to Build](#how-to-build)
- [How to Test](#how-to-test)
- [How to Run](#how-to-run)
- [How to Release](#how-to-release)
- [How to Build the Docker Image](#how-to-build-the-docker-image)
- [How to Contribute](#how-to-contribute)

## Overview

This project provides REST APIs to:

- Retrieve available mortgage interest rates
- Check mortgage feasibility based on user input

## Requirements

- Java 21+
- Maven 3.8+
- (Optional) Docker

## How to Build

To build the project, run:

```bash
mvn clean package
```

The JAR will be generated in the `target/` directory.

## How to Test

To run all unit and integration tests:

```bash
mvn test
```

## How to Run

To start the application locally:

```bash
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/mortgage-service-*.jar
```

The API will be available at `http://localhost:8080`.

Access the Swagger UI for API documentation at `http://localhost:8080/swagger-ui.html`.

A Postman collection is available in the `postman-collections/` directory for testing the endpoints.

## How to Release

1. Update the version in `pom.xml`.
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Tag the release in git:
   ```bash
   git tag vX.Y.Z
   git push origin vX.Y.Z
   ```
4. Publish the JAR or Docker image as needed.

## How to Build the Docker Image

To build the Docker image for this project, run the following command in the project root:

```bash
docker build -t mortgage-service:latest .
```

Use the following command to run the Docker container locally:

```bash
docker run -d --name mortgage-service -p 8080:8080 --restart=unless-stopped mortgage-service
```

## How to Contribute

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Make your changes and add tests.
4. Run all tests to ensure nothing is broken.
5. Commit and push your branch.
6. Open a pull request with a clear description.

---

For questions or issues, please open an issue in the repository.