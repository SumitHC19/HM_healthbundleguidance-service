# Bundle Guidance API - Postman Collections

This directory contains Postman collections and environments for testing the Bundle Guidance API.

## Collections

### 1. Bundle-Guidance-API-Local.postman_collection.json
- **Purpose**: Testing the API in local development environment
- **Base URL**: `http://localhost:8080`
- **Use Case**: Local development and testing

### 2. Bundle-Guidance-API-Dev.postman_collection.json
- **Purpose**: Testing the API in DEV environment
- **Base URL**: Configured in DEV environment file
- **Use Case**: Integration testing in DEV environment

## Environments

### 1. Local-Environment.postman_environment.json
- **Base URL**: `http://localhost:8080`
- **Headers**: Pre-configured test headers for local testing
- **Evaluation ID**: Sample evaluation ID for testing

### 2. DEV-Environment.postman_environment.json
- **Base URL**: DEV service URL (update with actual DEV endpoint)
- **Headers**: DEV environment authentication headers (update with actual values)
- **Evaluation ID**: Sample evaluation ID for DEV testing

## Available Endpoints

### Get Recommendations
- **GET** `/api/bundle/recommendations/{evaluationId}`
- Query Parameters:
  - `useMockData` (optional): Set to `true` to use mock data instead of database

### Save Recommendations
- **PUT** `/api/bundle/recommendations`
- Requires: Request body with evaluation results

### Save Bundle Selection
- **PUT** `/bundles/selection/{evaluationId}`
- Requires: Request body with bundle selection details

### Health Check
- **GET** `/actuator/health`
- Spring Boot Actuator health endpoint

## How to Use

### Import Collections
1. Open Postman
2. Click **Import** button
3. Select the collection file you want to import:
   - For local testing: `Bundle-Guidance-API-Local.postman_collection.json`
   - For DEV testing: `Bundle-Guidance-API-Dev.postman_collection.json`

### Import Environment
1. In Postman, click the gear icon (⚙️) in the top right
2. Click **Import**
3. Select the environment file:
   - For local: `Local-Environment.postman_environment.json`
   - For DEV: `DEV-Environment.postman_environment.json`

### Set Active Environment
1. Click the environment dropdown in the top right
2. Select either **Local Environment** or **DEV Environment**

### Update Environment Variables

#### For Local Environment:
- No changes needed for basic local testing
- Ensure your local service is running on `http://localhost:8080`

#### For DEV Environment:
- **IMPORTANT**: The DEV service URL needs to be configured. See [ENDPOINT-SETUP.md](./ENDPOINT-SETUP.md) for detailed instructions on how to get the correct service endpoint
- Service Name: `dp-com-bundleguidance-dv-service`
- Cluster: `dp-com-dv-cluster`
- Update `baseUrl` with the actual DEV service URL (ALB DNS name or custom domain)
- Update `alightRequestHeader` with valid DEV header value
- Update `alightPersonSessionToken` with valid DEV session token
- Update `evaluationId` with a valid evaluation ID from DEV

## Getting the DEV Service Endpoint

The DEV environment requires the actual AWS service URL. See **[ENDPOINT-SETUP.md](./ENDPOINT-SETUP.md)** for:
- Step-by-step guide to find the service URL in AWS Console
- AWS CLI commands to retrieve the endpoint
- Common URL patterns for Alight services
- Troubleshooting connection issues

## Testing Workflow

### Local Development
1. Start the local service: `./gradlew bootRun` or `make run`
2. Import the **Local** collection and environment
3. Select **Local Environment** from the dropdown
4. Run the **Get Recommendations - Mock Data** request (no database required)
5. For database testing, ensure MongoDB is running and use **Get Recommendations - Real Data**

### DEV Environment Testing
1. Import the **Dev** collection and environment
2. Select **DEV Environment** from the dropdown
3. Update the environment variables with actual DEV credentials
4. Run the requests against the DEV service

## Request Headers

All API requests (except health check) require these headers:
- `alightRequestHeader`: Request tracking header (minimum 10 characters)
- `alightPersonSessionToken`: Session token (minimum 10 characters)

These are pre-configured in the environment files.

## Sample Request Bodies

### Save Recommendations
```json
{
  "eventType": "enrollment",
  "evaluationId": "TEST-EVAL-12345",
  "evaluationType": "bundle",
  "evaluationStatus": "success",
  "evaluationOutput": {
    "bundles": [
      {
        "bundleId": "bundle-001",
        "bundleName": "Health Plus Bundle",
        "plans": [
          {
            "planId": "plan-001",
            "planName": "Health Plan"
          }
        ],
        "estimatedCosts": {
          "totalAnnualCost": 5000.00,
          "employeeContribution": 2000.00,
          "employerContribution": 3000.00
        }
      }
    ]
  }
}
```

### Save Bundle Selection
```json
{
  "clientId": "client-123",
  "platformInternalId": "platform-456",
  "businessProcessReferenceId": "bp-ref-789",
  "selectedBundleId": "bundle-001",
  "selectedBundleName": "Health Plus Bundle",
  "selectedPlans": [
    {
      "planId": "plan-001",
      "planName": "Health Plan",
      "planType": "Medical"
    }
  ]
}
```

## Troubleshooting

### Connection Refused
- Ensure the service is running on the correct port
- For local: Check if the service is running on port 8080
- For DEV: Verify the DEV URL is correct and accessible

### Authentication Errors (401/403)
- Verify the `alightRequestHeader` and `alightPersonSessionToken` values
- Headers must be at least 10 characters long
- For DEV, ensure you're using valid credentials

### 404 Not Found
- Verify the endpoint URL is correct
- Check that the `evaluationId` path parameter is set correctly

### 400 Bad Request
- Verify the request body format matches the expected schema
- Ensure required fields are present
- Check that `evaluationStatus` is set to "success" for save operations

## Notes

- The Local collection is identical to the Dev collection except for the environment configuration
- This allows for seamless switching between environments
- Update the DEV environment file with actual credentials before using in DEV
- Never commit real credentials to version control
