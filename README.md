# Spring Modulith Demo - Calling Service

A Spring Boot application that demonstrates OAuth2 Client Credentials flow for machine-to-machine communication with a secured REST API.

## Overview

This service acts as an OAuth2 client that calls a protected customer API using the client credentials grant type. It automatically obtains and manages access tokens from Keycloak before making REST API calls.

## Features

- 🔐 OAuth2 Client Credentials Flow for M2M authentication
- 🌐 RESTful API endpoints for customer operations
- 🔄 Automatic token management and refresh
- 🚀 Built with Spring Boot 3.5.7 and Java 25
- 📦 Uses Spring Security OAuth2 Client

## Prerequisites

- Java 25
- Maven 3.x
- Keycloak server running on `http://localhost:8180`
- Target API service running on `http://localhost:8080`

## Project Structure

```
src/main/java/com/paxier/spring_modulith_demo_calling_service/
├── SpringModulithDemoCallingServiceApplication.java  # Main application
└── call/
    ├── CallController.java                            # REST endpoints
    ├── CallRestClient.java                            # HTTP client for external API
    ├── OAuth2TokenService.java                        # Token management service
    ├── OAuth2ClientConfig.java                        # OAuth2 client configuration
    ├── SecurityConfig.java                            # Security configuration
    ├── Customer.java                                  # Customer model
    └── Address.java                                   # Address model
```

## Configuration

### Application Properties (`application.yaml`)

```yaml
spring:
  application:
    name: spring-modulith-demo-calling-service
  security:
    oauth2:
      client:
        registration:
          spring-modulith-call:
            client-id: spring-modulith-demo
            client-secret: <secret>
            authorization-grant-type: client_credentials
            scope: openid
        provider:
          spring-modulith-call:
            token-uri: http://localhost:8180/realms/master/protocol/openid-connect/token

server:
  port: 8081
```

### Environment Variables

You can override the configuration using environment variables:

- `KEYCLOAK_CLIENT_ID` - OAuth2 client ID (default: `spring-modulith-demo`)
- `KEYCLOAK_CLIENT_SECRET` - OAuth2 client secret
- `KEYCLOAK_TOKEN_URI` - Keycloak token endpoint
- `TARGET_API_URL` - Base URL of the target API (default: `http://localhost:8080`)

## OAuth2 Flow Explanation

### How Access Token is Obtained

1. **Lazy Initialization**: The token is obtained **on-demand** when the first REST API call is made (not at application startup)

2. **Token Request Process**:
   ```
   Application → OAuth2TokenService → OAuth2AuthorizedClientManager → Keycloak
   ```
   
3. **Flow Steps**:
   - `CallRestClient` calls `tokenService.getAccessToken()`
   - `OAuth2TokenService` creates an `OAuth2AuthorizeRequest` with anonymous principal
   - `OAuth2AuthorizedClientManager` (configured in `OAuth2ClientConfig`) sends token request to Keycloak
   - Keycloak validates client credentials and returns access token
   - Token is cached and reused until it expires
   - Token automatically refreshes when expired

### Key Components

#### `OAuth2TokenService`
- Centralized service for token management
- Provides reusable `getAccessToken()` method
- Handles token authorization requests with anonymous principal

#### `OAuth2ClientConfig`
- Configures `OAuth2AuthorizedClientManager` bean
- Sets up `ClientCredentialsOAuth2AuthorizedClientProvider`
- Manages token lifecycle (acquisition, caching, refresh)

#### `SecurityConfig`
- Configures Spring Security to allow all requests (no user authentication)
- Required because we're doing M2M communication, not user-based auth

#### `CallRestClient`
- Uses `RestClient` to make HTTP calls
- Injects OAuth2 Bearer token in Authorization header
- Communicates with protected customer API

## Installation & Setup

### 1. Configure Keycloak

1. Start Keycloak server on port 8180
2. Create a client with ID: `spring-modulith-demo`
3. Enable **Client Credentials** grant type
4. Copy the client secret and update `application.yaml`

### 2. Build the Project

```bash
./mvnw clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The service will start on `http://localhost:8081`

## API Endpoints

### Get Customer by ID

```http
GET http://localhost:8081/customers/{customerId}
```

**Example:**
```bash
curl http://localhost:8081/customers/1
```

### Create Customer

```http
POST http://localhost:8081/customers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  }
}
```

**Example:**
```bash
curl -X POST http://localhost:8081/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com"
  }'
```

## Dependencies

- **Spring Boot Starter Web** - REST API support
- **Spring Boot Starter OAuth2 Client** - OAuth2 client functionality
- **Spring Boot Starter Actuator** - Health checks and monitoring
- **Lombok** - Reduces boilerplate code
- **Spring Boot Starter Test** - Testing support

## Troubleshooting

### "Please sign in" Error

This occurs when:
- Keycloak is not running or unreachable
- Client credentials are incorrect
- Token URI is misconfigured

**Solution**: Verify Keycloak configuration and ensure the service is running.

### "Unable to authorize client" Error

This means the `OAuth2AuthorizedClientManager` couldn't obtain a token.

**Causes**:
- Invalid client credentials
- Network connectivity issues
- Keycloak realm/client misconfiguration

**Solution**: Check application logs and verify OAuth2 configuration in `application.yaml`.

### Target API Connection Refused

Ensure the target customer API is running on `http://localhost:8080`.

## Development

### Running Tests

```bash
./mvnw test
```

### HTTP Client Testing

Use the HTTP test file located at `src/test/resources/call_customer.http` for manual testing.

## Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│         Spring Boot Calling Service             │
│              (Port: 8081)                       │
├─────────────────────────────────────────────────┤
│  CallController                                 │
│       ↓                                         │
│  CallRestClient                                 │
│       ↓                                         │
│  OAuth2TokenService ─→ OAuth2ClientConfig      │
└────────────┬──────────────────────┬─────────────┘
             │                      │
             │ OAuth2 Token         │ API Call
             │ Request              │ with Bearer Token
             ↓                      ↓
    ┌────────────────┐    ┌──────────────────┐
    │   Keycloak     │    │   Customer API   │
    │  (Port: 8180)  │    │  (Port: 8080)    │
    └────────────────┘    └──────────────────┘
```

## License

This is a demo project for learning purposes.

## Author

Paxier

## Related Projects

- spring-modulith-demo Service (runs on port 8080)
- Keycloak Authentication Server

---

**Note**: This service uses OAuth2 Client Credentials flow for machine-to-machine authentication. No user login is required as it authenticates using client credentials directly with Keycloak.

