# Evaluation Input Collection Setup

## Collection Details
- **Database**: `personHealthDataDV`
- **Collection**: `evaluationInput`
- **Documents**: 2

## Indexes Created
1. `_id_` - Default MongoDB index
2. `evaluationId_1` - Single field index on `evaluationId`
3. `businessProcessRefId_platformInternalId_1` - Compound index on `businessProcessReferenceId` and `platformInternalId`

## Sample Test Data
A sample evaluation input document has been inserted with the following identifiers:
- **clientId**: `19968`
- **platformInternalId**: `007700057`
- **evaluationId**: `15bf0d9e765dd08eda3de5169e086c6`
- **businessProcessReferenceId**: `12345`

## Testing Endpoints

### Test GET Endpoint (Retrieve Evaluation Input)

#### Using evaluationId:
```bash
curl -X GET "http://localhost:8080/bundle/evaluationinput?businessProcessReferenceId=12345&evaluationid=15bf0d9e765dd08eda3de5169e086c6" \
  -H "alightRequestHeader: test-header-1234567890" \
  -H "alightPersonSessionToken: test-token-0987654321" \
  -H "platformInternalId: 007700057"
```

#### Using businessProcessReferenceId and platformInternalId only:
```bash
curl -X GET "http://localhost:8080/bundle/evaluationinput?businessProcessReferenceId=12345" \
  -H "alightRequestHeader: test-header-1234567890" \
  -H "alightPersonSessionToken: test-token-0987654321" \
  -H "platformInternalId: 007700057"
```

### Test PUT Endpoint (Save/Update Evaluation Input)

Use the Postman collection at `postman/Bundle-Guidance-API-Local.postman_collection.json` or:

```bash
curl -X PUT "http://localhost:8080/bundle/evaluationinput" \
  -H "alightRequestHeader: test-header-1234567890" \
  -H "alightPersonSessionToken: test-token-0987654321" \
  -H "platformInternalId: 007700057" \
  -H "Content-Type: application/json" \
  -d @- <<'EOF'
{
  "clientId": "19968",
  "platformInternalId": "007700057",
  "evaluationId": "15bf0d9e765dd08eda3de5169e086c6",
  "businessProcessReferenceId": "12345",
  "SAVVIRequest": {
    "planYearStartDate": "2025-01-01",
    "planYearEndDate": "2025-12-31",
    "enrollmentMode": "annual",
    "effectiveDate": "2025-01-01",
    "subscriber": {
      "id": "VBAYSGJWFI9GP5CHAGIYGGUMHNZN2QOA",
      "birthdate": "1979-02-15",
      "annualPay": 80000,
      "taxState": "MD",
      "payPeriodsPlanyear": 26,
      "payPeriodsRemaining": 26,
      "healthcare": {
        "expectedUsage": {
          "medicalCare": {
            "type": "level",
            "usage": "low"
          }
        }
      }
    },
    "coverablePeople": [
      {
        "id": "R0JDJ4IUL8XLXCTUJFCYH7PNYT9FUVA6",
        "birthdate": "2016-07-15",
        "relationship": "child",
        "healthcare": {
          "expectedUsage": {
            "medicalCare": {
              "type": "level",
              "usage": "low"
            }
          }
        }
      }
    ],
    "products": [
      {
        "type": "medical_plan",
        "productId": "VNCLWESSXLN6PQBPGEZMNNMJKZVCPQQT",
        "planIds": ["P6cSfI3dH3dOEdrO"],
        "isHdhp": true
      }
    ],
    "eligibleOffers": [
      {
        "offerType": "coverage_choice",
        "planId": "P6cSfI3dH3dOEdrO",
        "offerId": "KO8CE8BWT0FKK09OFXZJFPFEPSV9IZD4",
        "numDeductionsPlanyear": 26
      }
    ]
  },
  "timestamp": "2026-01-01-00.00.00"
}
EOF
```

## MongoDB Query Examples

### View all documents:
```javascript
db.evaluationInput.find().pretty()
```

### Query by evaluationId:
```javascript
db.evaluationInput.findOne({ evaluationId: "15bf0d9e765dd08eda3de5169e086c6" })
```

### Query by businessProcessReferenceId and platformInternalId:
```javascript
db.evaluationInput.findOne({
  businessProcessReferenceId: "12345",
  platformInternalId: "007700057"
})
```

### Count documents:
```javascript
db.evaluationInput.countDocuments()
```

### Delete test data:
```javascript
db.evaluationInput.deleteMany({ businessProcessReferenceId: "12345" })
```

## Collection Maintenance

### Re-insert sample data:
```bash
mongosh "mongodb://admin:Ch0nge%2312%23@local-mongodb:27017/personHealthDataDV?authSource=admin" scripts/insert-sample-evaluation-input.js
```

### View indexes:
```bash
mongosh "mongodb://admin:Ch0nge%2312%23@local-mongodb:27017/personHealthDataDV?authSource=admin" --eval "db.evaluationInput.getIndexes()"
```
