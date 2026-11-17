# File Processor

A Spring Boot application that processes ZIP files from a configured local input directory.

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
Generate metadata.xml
    |
    v
Create processed ZIP containing extracted files and metadata
    |
    v
Base64 encode processed ZIP
    |
    v
Move original ZIP to Processed/
    |
    v
Delete temporary files
```

If processing fails, partial output files are deleted and the original ZIP is moved to `Error/`.

## Supported File Types

- PDF
- JPEG / JPG

## Directories

```text
file-processing/
|-- Input/
|-- Processed/
|-- Encoded/
|-- Error/
```

_Supported file types and directory paths are configurable._

## Metadata

Each successfully processed ZIP contains a `metadata.xml` file describing the processed contents.

Metadata includes:

- Original ZIP filename
- Processing timestamp
- Number of processed files
- Filename, file type, and size of each processed file

Metadata can be retrieved for a processed file using:

```text
GET /{fileName}/metadata
```

Example response:

```json
{
  "originalFilename": "documents.zip",
  "processedAt": "2026-11-17T10:15:30",
  "count": 1,
  "files": [
    {
      "fileName": "document.pdf",
      "fileType": ".pdf",
      "fileSize": 1024
    }
  ]
}
```

## Example Successful Processing Response

HTTP `200 OK`

```json
{
  "status": "SUCCESS",
  "sourceFile": "documents.zip",
  "processedFile": "documents_processed.zip",
  "encodedFile": "documents_processed_base64.txt"
}
```

## Example Failed Response

HTTP `400 Bad Request`

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