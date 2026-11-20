# File Processor

A multi-module Spring Boot application (Java 25, Spring Boot 4.1.2) that processes ZIP files from a local input directory and adds a generated `metadata.xml` to each processed ZIP.

## Architecture

```text
Client
  |
  v
api-gateway (8080)                 public API: /api/v1/**
  |
  |-- /api/v1/files/**       -->   file-processing-service (8081)   /internal/v1/files/**
  |                                      |
  |                                      | creates and reads metadata
  |                                      v
  |-- /api/v1/metadata/**    -->   metadata-service (8082)          /internal/v1/metadata/**
```

| Module | Port | Responsibility |
|---|---|---|
| `api-gateway` | 8080 | Public entry point. Routes `/api/v1/**` to the services and rewrites the path to `/internal/v1/**`. |
| `file-processing-service` | 8081 | Validates, extracts and repackages ZIP files. Calls `metadata-service` to create and read `metadata.xml`. |
| `metadata-service` | 8082 | Owns the metadata XML capability. Converts file metadata between JSON and XML. |
| `common-rest` | n/a | Shared library containing `ApiErrorResponse` and the `RestException` types. |
| `file-processor-parent` | n/a | Parent POM. Manages the Spring Boot version, Java version and Lombok annotation processor configuration. |

Clients call `/api/v1/**` through the gateway. Each service exposes its own endpoints under `/internal/v1/**`.

### Repository layout

```text
file-processor/
|-- pom.xml                      parent / aggregator
|-- common-rest/
|-- api-gateway/
|-- file-processing-service/
|-- metadata-service/
|-- bruno/                       Bruno API collection
```

## Endpoints

Called through the gateway on port 8080.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/files` | Process an input ZIP. |
| `GET` | `/api/v1/files?fileType=` | List input files, optionally filtered by type. |
| `GET` | `/api/v1/files/{fileName}/metadata` | Read the metadata of a processed ZIP. |
| `POST` | `/api/v1/metadata/processed-files` | Convert file details (JSON) to `metadata.xml`. |
| `POST` | `/api/v1/metadata/processed-files/unmarshal` | Convert `metadata.xml` to JSON. |

## Process Flow

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
Extract selected files to a temporary directory
    |
    v
Validate file signatures
    |
    v
Create metadata.xml via metadata-service
    |
    v
Create processed ZIP (extracted files + metadata.xml)
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

If processing fails, partial output files are deleted and the original ZIP is moved to `error/`.

## Metadata

`metadata.xml` lists the original file name, processing time and the name, type and size (bytes) of each extracted file.

`GET /api/v1/files/{fileName}/metadata` takes the processed ZIP name from the process response and returns:

```json
{
  "originalFilename": "documents.zip",
  "processedAt": "2026-11-19T10:01:30",
  "count": 1,
  "files": [
    { "name": "a.pdf", "type": ".pdf", "size": 1024 }
  ]
}
```

## Supported File Types

- PDF
- JPEG / JPG

## Directories

```text
file-processing/
|-- input/
|-- processed/
|-- encoded/
|-- error/
```

Paths are relative to the working directory, so `file-processing-service` runs from the repository root. Supported file types and directories are configurable.

## Configuration

| Variable | Default |
|---|---|
| `FILE_PROCESSOR_INPUT_DIR` | `./file-processing/input` |
| `FILE_PROCESSOR_PROCESSED_DIR` | `./file-processing/processed` |
| `FILE_PROCESSOR_ENCODED_DIR` | `./file-processing/encoded` |
| `FILE_PROCESSOR_ERROR_DIR` | `./file-processing/error` |
| `FILE_PROCESSOR_ALLOWED_FILE_TYPES` | `PDF, JPEG, JPG` |
| `METADATA_SERVICE_URL` | `http://localhost:8082` |
| `FILE_PROCESSING_SERVICE_URL` | `http://localhost:8081` |

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

Failures use one shape across all services, for example `400 Bad Request`:

```json
{
  "status": "FAILED",
  "error": "Bad Request",
  "message": "Invalid entries in zip file",
  "details": {
    "unsupportedFiles": ["notes.txt", "image.png"]
  }
}
```
