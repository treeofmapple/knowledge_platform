# Knowledge Platform

# Cloudflare R2 Storage Configuration
| **Description**                                          | **Parameter**          |
| -------------------------------------------------------- | ---------------------- |
|`CLOUDFLARE_ENDPOINT=`|`https://your_r2_endpoint.r2.cloudflarestorage.com`|
|`CLOUDFLARE_ACCESS_KEY=`|`your_cloudflare_access_key`|
|`CLOUDFLARE_SECRET_KEY=`|`your_cloudflare_secret_key`|
|`CLOUDFLARE_BUCKET_NAME=`|`your_r2_bucket_name`|

# Database Connection Details
| **Description**                                          | **Parameter**          |
| -------------------------------------------------------- | ---------------------- |
|`DB_HOST=`|`your_database_host`|
|`DB_DATABASE=`|`your_database_name`|
|`DB_USER=``|your_database_username`|
|`DB_PASSWORD=`|`your_database_password`'|

# Application Settings
| **Description**                                          | **Parameter**          |
| -------------------------------------------------------- | ---------------------- |
|`SECRET_KEY=`|`your_application_secret_key_for_jwt_or_other_functions` | 

# Spring Boot Hibernate Settings (optional, defaults are often sufficient for development)
| **Description**                                          | **Parameter**          |
| -------------------------------------------------------- | ---------------------- |
|`DDL_AUTO=update` |`# Use 'update', 'create-drop', or 'validate'`|
|`CLOUDFLARE_DDL_AUTO=update` |`# Use 'update', 'create-drop', or 'validate'`|

# CORS (Cross-Origin Resource Sharing) Configuration
| **Description**                                          | **Parameter**          |
| -------------------------------------------------------- | ---------------------- |
|`ALLOWED_ORIGINS=* `|`# Or specify domains like http://localhost:3000`|
