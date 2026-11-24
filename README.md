# File Processor

A multi-module Spring Boot application built with Java 25 and Spring Boot 4.1.2 for processing ZIP files, generating XML metadata, and exposing the functionality through an API gateway.

The application is split into independently deployable services with shared modules for REST contracts and common web infrastructure.

## Architecture

```text
Client
  |
  |  JWT optional
  |  X-Correlation-ID optional
  v
api-gateway (8080)                    Public API: /api/v1/**
  |
  |-- /api/v1/files/** ----------->  file-processing-service (8081)
  |                                      /internal/v1/files/**
  |                                          |
  |                                          | JWT + X-Correlation-ID
  |                                          v
  |
  |-- /api/v1/metadata/** --------->  metadata-service (8082)
                                         /internal/v1/metadata/**
```

### Modules

| Module | Port | Responsibility |
|---|---:|---|
| `api-gateway` | 8080 | Public entry point. Issues and validates JWTs, propagates correlation IDs, routes `/api/v1/**` requests and rewrites paths to `/internal/v1/**`. |
| `file-processing-service` | 8081 | Validates, extracts and repackages ZIP files. Calls `metadata-service` to create and read `metadata.xml`. |
| `metadata-service` | 8082 | Owns metadata XML functionality and converts processed-file metadata between JSON and XML. |
| `common-rest` | n/a | Shared REST exception and error response contracts. |
| `common-web` | n/a | Shared correlation ID handling, JWT validation, authentication responses, REST exception handling and AOP method logging. |
| `file-processor-parent` | n/a | Parent POM managing Spring Boot, Java and shared Maven configuration. |

Clients normally access the application through `api-gateway`.

The gateway exposes `/api/v1/**`. Application services expose internal endpoints under `/internal/v1/**`.

## Repository Layout

```text
file-processor/
|-- pom.xml
|-- api-gateway/
|-- common-rest/
|-- common-web/
|-- file-processing-service/
|-- metadata-service/
|-- certificates/
|-- file-processing/
|-- bruno/
```

`bruno/` contains the Bruno API collection.

## Security

The application uses RSA-signed JWT bearer tokens.

### Request Flow

```text
Incoming request
       |
       v
API Gateway
       |
       |-- Authorization header present
       |       |
       |       +--> validate supplied JWT
       |
       |-- Authorization header absent
               |
               +--> issue JWT
                       |
                       v
                  validate JWT
                       |
                       v
            forward Authorization header
                       |
                       v
           file-processing-service
                       |
                  validate JWT
                       |
                       v
               metadata-service
                       |
                  validate JWT
```

An invalid client-supplied JWT is rejected. The gateway does not replace an invalid token with a new one.

### Key Ownership

The gateway owns the RSA private key and is the only component capable of signing JWTs.

```text
api-gateway
    |
    |-- private key --> signs JWTs
    |
    +-- public key --> validates JWTs

file-processing-service
    |
    +-- public key --> validates JWTs

metadata-service
    |
    +-- public key --> validates JWTs
```

JWT validation is shared through `common-web`. Validation includes signature, issuer and timestamp validation.

The default trusted issuer is:

```text
api-gateway
```

## Generating the JWT Key Pair

Java `keytool` can be used to create the development RSA key pair.

The commands below assume they are run from the repository root.

### 1. Create the certificates directory

PowerShell:

```powershell
New-Item -ItemType Directory -Force ".\certificates"
```

### 2. Generate the gateway PKCS12 keystore

```powershell
keytool -genkeypair `
    -alias gateway-jwt `
    -keyalg RSA `
    -keysize 2048 `
    -sigalg SHA256withRSA `
    -storetype PKCS12 `
    -keystore ".\certificates\gateway-jwt.p12" `
    -validity 3650
```

`keytool` will prompt for the keystore password and certificate information.

The generated file:

```text
certificates/gateway-jwt.p12
```

contains the private key and public certificate.

### 3. Export the public certificate

Export the public portion of the same key pair:

```powershell
keytool -exportcert `
    -rfc `
    -alias gateway-jwt `
    -keystore ".\certificates\gateway-jwt.p12" `
    -storetype PKCS12 `
    -file ".\certificates\gateway-jwt-public.pem"
```

Enter the same keystore password when prompted.

The resulting file should begin with:

```text
-----BEGIN CERTIFICATE-----
```

The public certificate can be distributed to services that need to validate gateway-issued JWTs.

The resulting development files are:

```text
certificates/
|-- gateway-jwt.p12
|-- gateway-jwt-public.pem
```

## JWT Configuration

### API Gateway

The gateway requires access to the PKCS12 keystore.

```properties
security.jwt.keystore-location=${JWT_KEYSTORE_LOCATION}
security.jwt.keystore-password=${JWT_KEYSTORE_PASSWORD}
security.jwt.key-alias=${JWT_KEY_ALIAS:gateway-jwt}
security.jwt.token-ttl=${JWT_TOKEN_TTL:5m}

security.jwt.validation.issuer=${JWT_ISSUER:api-gateway}
```

Example development environment variables:

```text
JWT_KEYSTORE_LOCATION=file:./certificates/gateway-jwt.p12
JWT_KEYSTORE_PASSWORD=<your-password>
JWT_KEY_ALIAS=gateway-jwt
```

### File Processing Service

```properties
security.jwt.validation.issuer=${JWT_ISSUER:api-gateway}
security.jwt.validation.public-key-location=${JWT_PUBLIC_KEY_LOCATION}
```

Development value:

```text
JWT_PUBLIC_KEY_LOCATION=file:./certificates/gateway-jwt-public.pem
```

### Metadata Service

```properties
security.jwt.validation.issuer=${JWT_ISSUER:api-gateway}
security.jwt.validation.public-key-location=${JWT_PUBLIC_KEY_LOCATION}
```

Development value:

```text
JWT_PUBLIC_KEY_LOCATION=file:./certificates/gateway-jwt-public.pem
```

## Correlation IDs

The application uses:

```text
X-Correlation-ID
```

to correlate logs across services.

If a request already contains `X-Correlation-ID`, the existing value is preserved.

If the header is absent, the application generates a UUID.

```text
Client
  |
  | X-Correlation-ID optional
  v
Gateway
  |
  | establish/preserve correlation ID
  v
File Processing
  |
  | propagate same correlation ID
  v
Metadata
```

The same correlation ID is stored in SLF4J MDC and included in application logs.

For example:

```text
DEBUG [correlationId=1836c00e-cdab-4eba-89ba-3b579a901624] ... Method entered...
```

`common-web` provides:

- inbound correlation ID filtering;
- MDC management;
- response correlation headers;
- outbound `RestClient` correlation propagation.

Spring Cloud Gateway MVC performs its own gateway-specific outbound propagation.

## Method Logging

`common-web` provides a shared Spring AOP aspect for controller and service methods.

Public methods on Spring controllers and services are logged at `DEBUG` level.

Example:

```text
Method entered: method=public ...ProcessingService.process(...)

Method exited: method=public ...ProcessingService.process(...),
outcome=returned,
durationMs=42.17,
exceptionType=null
```

Method arguments and return values are not automatically logged.

This avoids inadvertently logging JWTs, XML documents, file contents or other potentially sensitive payloads.

Example logging configuration:

```properties
logging.pattern.console=%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-5level [correlationId=%X{correlationId:-}] [%thread] %logger{36} - %msg%n

logging.level.root=INFO
logging.level.dev.arunsamra.common.web.logging=DEBUG
```

Each service can additionally enable `DEBUG` for its own package.

For example:

```properties
logging.level.dev.arunsamra.fileprocessor=DEBUG
```

## Endpoints

Clients call these endpoints through the gateway on port `8080`.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/files` | Process an input ZIP. |
| `GET` | `/api/v1/files?fileType=` | List input files, optionally filtered by type. |
| `GET` | `/api/v1/files/{fileName}/metadata` | Read metadata from a processed ZIP. |
| `POST` | `/api/v1/metadata/processed-files` | Convert processed-file details from JSON to XML. |
| `POST` | `/api/v1/metadata/processed-files/unmarshal` | Convert metadata XML to JSON. |

## Processing Flow

```text
API request
    |
    v
Locate and validate input ZIP
    |
    v
Inspect and validate ZIP contents
    |
    v
Extract selected files to temporary storage
    |
    v
Validate file signatures
    |
    v
Call metadata-service
    |
    v
Create metadata.xml
    |
    v
Create processed ZIP
(extracted files + metadata.xml)
    |
    v
Base64 encode processed ZIP
    |
    v
Move original ZIP to processed/
    |
    v
Delete temporary files
```

If processing fails, partial outputs are removed and the original ZIP is moved to `error/`.

## Metadata

`metadata.xml` contains:

- original filename;
- processing timestamp;
- extracted file count;
- filename;
- file type;
- file size in bytes.

`GET /api/v1/files/{fileName}/metadata` reads `metadata.xml` from an existing processed ZIP and returns JSON:

```json
{
  "originalFilename": "documents.zip",
  "processedAt": "2026-11-19T10:01:30",
  "count": 1,
  "files": [
    {
      "name": "a.pdf",
      "type": ".pdf",
      "size": 1024
    }
  ]
}
```

## Supported File Types

By default:

- PDF
- JPEG
- JPG

Supported file types are configurable.

## File Processing Directories

```text
file-processing/
|-- input/
|-- processed/
|-- encoded/
|-- error/
```

Paths are resolved relative to the configured working directory.

When running locally, use the repository root as the working directory.

## Configuration

### File Processing

| Variable | Default |
|---|---|
| `FILE_PROCESSOR_INPUT_DIR` | `./file-processing/input` |
| `FILE_PROCESSOR_PROCESSED_DIR` | `./file-processing/processed` |
| `FILE_PROCESSOR_ENCODED_DIR` | `./file-processing/encoded` |
| `FILE_PROCESSOR_ERROR_DIR` | `./file-processing/error` |
| `FILE_PROCESSOR_ALLOWED_FILE_TYPES` | `PDF,JPEG,JPG` |

### Service URLs

| Variable | Default |
|---|---|
| `FILE_PROCESSING_SERVICE_URL` | `http://localhost:8081` |
| `METADATA_SERVICE_URL` | `http://localhost:8082` |

### Metadata Service Client

The file-processing service uses configurable timeouts when calling `metadata-service` and retries transient connection or timeout failures once by default.

| Variable | Default |
|---|---|
| `METADATA_CONNECT_TIMEOUT` | `2s` |
| `METADATA_READ_TIMEOUT` | `5s` |
| `METADATA_RETRY_MAX_RETRIES` | `1` |
| `METADATA_RETRY_DELAY` | `200ms` |

`METADATA_RETRY_MAX_RETRIES` is the number of retries after the initial request. Set it to `0` to disable retries.

### JWT

| Variable | Used by | Default |
|---|---|---|
| `JWT_KEYSTORE_LOCATION` | Gateway | none |
| `JWT_KEYSTORE_PASSWORD` | Gateway | none |
| `JWT_KEY_ALIAS` | Gateway | `gateway-jwt` |
| `JWT_TOKEN_TTL` | Gateway | `5m` |
| `JWT_PUBLIC_KEY_LOCATION` | Downstream services | none |
| `JWT_ISSUER` | All JWT validators | `api-gateway` |

## Running Locally

Start the applications in this order:

```text
1. metadata-service         :8082
2. file-processing-service  :8081
3. api-gateway              :8080
```

Configure the gateway with:

```text
JWT_KEYSTORE_LOCATION=file:./certificates/gateway-jwt.p12
JWT_KEYSTORE_PASSWORD=<your-password>
JWT_KEY_ALIAS=gateway-jwt
```

Configure both downstream services with:

```text
JWT_PUBLIC_KEY_LOCATION=file:./certificates/gateway-jwt-public.pem
```

All applications should use the repository root as the working directory when relying on these relative paths.

## Build and Test

From the repository root:

```powershell
mvn clean verify
```

To build and install all modules into the local Maven repository:

```powershell
mvn clean install
```

Test resources use a dedicated test signing key and exported public certificate so that tests do not depend on development keys or machine-specific environment variables.

## Example Responses

`POST /api/v1/files` returns `200 OK`:

```json
{
  "status": "SUCCESS",
  "sourceFile": "documents.zip",
  "processedFile": "documents_processed.zip",
  "encodedFile": "documents_processed_base64.txt"
}
```

Failures use a shared error response shape across services.

For example, `400 Bad Request`:

```json
{
  "status": "FAILED",
  "error": "Bad Request",
  "message": "Invalid entries in zip file",
  "details": {
    "unsupportedFiles": [
      "notes.txt",
      "image.png"
    ]
  }
}
```