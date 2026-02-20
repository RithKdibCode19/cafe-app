# Data Transfer Objects (DTO) Flow

The backend DTO design and data flow are extensively documented in the `backend/` directory of this project.

## Reference Documents
Please refer to these files for detailed implementation plans:

* **[DTO Flow Design](../../backend/DTO-FLOW-DESIGN.md)**: The master document outlining the DTO strategy, examples for Orders, and Service layer implementation.
* **[DTO Structure](../../backend/DTO-STRUCTURE.md)**: Defined directory structure for the `dto` package.
* **[DTO Implementation Summary](../../backend/DTO-IMPLEMENTATION-SUMMARY.md)**: Status of DTO migration/implementation.

## Core Principle
**Zero Entity Exposure**: The REST API layer must NEVER return JPA Entities directly. Always map Entity -> ResponseDTO before returning.
