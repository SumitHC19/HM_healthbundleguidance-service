#!/bin/bash
set -e

# Script to create DocumentDB JKS truststore from AWS global-bundle.pem
# This imports ALL certificates from the bundle into a single JKS file

TRUSTSTORE_FILE="docdb-truststore.jks"
TRUSTSTORE_PASS="changeit"
BUNDLE_URL="https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem"
BUNDLE_FILE="global-bundle.pem"

echo "======================================"
echo "DocumentDB Truststore Creation Script"
echo "======================================"
echo ""

# Download the bundle if not present
if [ ! -f "$BUNDLE_FILE" ]; then
    echo "📥 Downloading AWS RDS/DocumentDB CA bundle..."
    wget -q "$BUNDLE_URL" -O "$BUNDLE_FILE"
    echo "✅ Downloaded $BUNDLE_FILE"
else
    echo "ℹ️  Using existing $BUNDLE_FILE"
fi

# Verify the bundle file
if [ ! -s "$BUNDLE_FILE" ]; then
    echo "❌ Error: $BUNDLE_FILE is empty or not found"
    exit 1
fi

# Count certificates in bundle
cert_count=$(grep -c "BEGIN CERTIFICATE" "$BUNDLE_FILE")
echo "📋 Found $cert_count certificate(s) in $BUNDLE_FILE"
echo ""

# Remove old truststore if exists
if [ -f "$TRUSTSTORE_FILE" ]; then
    echo "🗑️  Removing old truststore..."
    rm "$TRUSTSTORE_FILE"
fi

# Split the bundle into individual certificates
echo "✂️  Splitting certificates..."
csplit -sz -f cert- "$BUNDLE_FILE" '/-----BEGIN CERTIFICATE-----/' '{*}'

# Import each certificate
counter=1
imported=0

for cert_file in cert-*; do
    # Skip empty files
    if [ ! -s "$cert_file" ]; then
        rm "$cert_file"
        continue
    fi

    # Only process files that contain a certificate
    if grep -q "BEGIN CERTIFICATE" "$cert_file"; then
        alias_name="rds-ca-$counter"
        echo "📦 Importing certificate $counter as alias: $alias_name"

        keytool -import -trustcacerts \
            -alias "$alias_name" \
            -file "$cert_file" \
            -keystore "$TRUSTSTORE_FILE" \
            -storepass "$TRUSTSTORE_PASS" \
            -noprompt 2>/dev/null

        if [ $? -eq 0 ]; then
            imported=$((imported + 1))
        else
            echo "⚠️  Warning: Failed to import $cert_file"
        fi

        counter=$((counter + 1))
    fi

    # Clean up temporary file
    rm "$cert_file"
done

echo ""
echo "✅ Successfully imported $imported certificate(s) into $TRUSTSTORE_FILE"
echo ""

# Verify the truststore
echo "🔍 Verifying truststore..."
echo "======================================"
keytool -list -keystore "$TRUSTSTORE_FILE" -storepass "$TRUSTSTORE_PASS" 2>/dev/null
echo "======================================"
echo ""

# Create base64 encoded version
echo "🔐 Creating base64 encoded version..."
base64 -w 0 "$TRUSTSTORE_FILE" > "${TRUSTSTORE_FILE}.b64"
echo "✅ Created ${TRUSTSTORE_FILE}.b64"
echo ""

# Get file sizes
jks_size=$(stat -f%z "$TRUSTSTORE_FILE" 2>/dev/null || stat -c%s "$TRUSTSTORE_FILE" 2>/dev/null)
b64_size=$(stat -f%z "${TRUSTSTORE_FILE}.b64" 2>/dev/null || stat -c%s "${TRUSTSTORE_FILE}.b64" 2>/dev/null)

echo "📊 Summary:"
echo "  - JKS file: $TRUSTSTORE_FILE ($jks_size bytes)"
echo "  - Base64 file: ${TRUSTSTORE_FILE}.b64 ($b64_size bytes)"
echo "  - Certificates imported: $imported"
echo "  - Password: $TRUSTSTORE_PASS"
echo ""
echo "🚀 Next steps:"
echo ""
echo "Option 1: Use as environment variable"
echo "  export DOCDB_TRUSTSTORE_B64=\$(cat ${TRUSTSTORE_FILE}.b64)"
echo ""
echo "Option 2: Store in AWS Secrets Manager (as SecretString)"
echo "  aws secretsmanager create-secret \\"
echo "    --name docdb-truststore \\"
echo "    --description 'DocumentDB JKS truststore base64 encoded' \\"
echo "    --secret-string file://${TRUSTSTORE_FILE}.b64"
echo ""
echo "Option 3: Store in AWS Secrets Manager (as SecretBinary - recommended)"
echo "  aws secretsmanager create-secret \\"
echo "    --name docdb-truststore \\"
echo "    --description 'DocumentDB JKS truststore binary' \\"
echo "    --secret-binary fileb://$TRUSTSTORE_FILE"
echo ""
echo "Option 4: Update existing secret"
echo "  aws secretsmanager update-secret \\"
echo "    --secret-id docdb-truststore \\"
echo "    --secret-string file://${TRUSTSTORE_FILE}.b64"
echo ""
echo "✅ Done!"
