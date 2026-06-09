# PKCS12 Truststore Conversion - Completed

## Summary
Successfully converted the DocumentDB truststore from JKS to PKCS12 format and updated AWS Secrets Manager.

## Steps Performed

### 1. Conversion from JKS to PKCS12
```bash
cd /workspaces/HM_healthbundleguidance-service/scripts
keytool -importkeystore \
  -srckeystore docdb-truststore-regional.jks \
  -srcstoretype JKS \
  -srcstorepass changeit \
  -destkeystore docdb-truststore-regional.p12 \
  -deststoretype PKCS12 \
  -deststorepass changeit \
  -noprompt
```

**Result**: 3 entries successfully imported
- rds-ca-useast1-1
- rds-ca-useast1-2
- rds-ca-useast1-3

### 2. Verification
```bash
keytool -list -keystore docdb-truststore-regional.p12 -storepass changeit -storetype PKCS12
```

### 3. File Sizes
- **PKCS12 file**: 3.9KB (docdb-truststore-regional.p12)
- **Base64 encoded**: 5.1KB (docdb-truststore-regional.p12.b64)
- **Original JKS**: 5.1KB (docdb-truststore-regional.jks)

### 4. AWS Secrets Manager Update
```bash
aws secretsmanager update-secret \
  --secret-id dp-health-docdb-truststore \
  --secret-binary fileb://docdb-truststore-regional.p12 \
  --region us-east-1
```

**Result**:
- ARN: `arn:aws:secretsmanager:us-east-1:467880930637:secret:dp-health-docdb-truststore-coj2v3`
- Version ID: `0c7ed826-2e7b-4519-8faf-71bcaccc7461`
- Format: Binary PKCS12
- Size: 3.9KB

### 5. Verification from AWS
Retrieved and verified the truststore from AWS Secrets Manager contains all 3 certificates.

## Infrastructure Configuration Required

The application now expects:

### Environment Variable
```bash
JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStore=/apps/keystores/docdb-truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=PKCS12"
```

### File Location
```
/apps/keystores/docdb-truststore.p12
```

### Terraform Implementation
Terraform must retrieve the binary secret from AWS Secrets Manager and mount it to the container at the specified path.

See `INFRASTRUCTURE_REQUIREMENTS.md` for detailed implementation instructions.

## Benefits of PKCS12 over JKS

1. **Modern Standard**: PKCS12 is the industry standard format
2. **Better Security**: More secure encryption algorithms
3. **Cross-Platform**: Better compatibility across different systems
4. **Java Recommendation**: Oracle recommends PKCS12 over JKS
5. **Smaller Size**: 3.9KB vs 5.1KB (24% reduction)

## Files Generated
- `docdb-truststore-regional.p12` - PKCS12 truststore
- `docdb-truststore-regional.p12.b64` - Base64 encoded version (if needed for text-based secrets)

## Status
✅ **Complete** - AWS Secrets Manager updated with PKCS12 truststore
✅ **Verified** - All 3 certificates present and valid
✅ **Application Ready** - MongoConfig configured to use JVM default SSL context
⏳ **Pending** - Infrastructure team to configure Terraform deployment
