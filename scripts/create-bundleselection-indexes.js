// MongoDB script to create indexes for bundleSelection collection
// Usage: mongosh -u admin -p 'Ch0nge#12#' --authenticationDatabase admin personHealthDataDV < create-bundleselection-indexes.js

db = db.getSiblingDB('personHealthDataDV');

print("Creating indexes for bundleSelection collection...");

// Compound index for the main query pattern:
// findByClientIdAndPlatformInternalIdAndBusinessProcessReferenceId
//
// Query fields (all required, used together in GET and PUT operations):
// - clientId: Extracted from alightRequestHeader (tenant/client identifier)
// - platformInternalId: Extracted from token if parsable, otherwise from request parameter/body
// - businessProcessReferenceId: Request parameter (enrollment/business process identifier)
//
// Index order optimized for query selectivity:
// 1. clientId - Tenant partition (filters to client's data)
// 2. platformInternalId - User identifier (most selective within client)
// 3. businessProcessReferenceId - Business process/enrollment period
const compoundIndex = db.bundleSelection.createIndex(
    {
        clientId: 1,
        platformInternalId: 1,
        businessProcessReferenceId: 1
    },
    {
        name: "idx_clientId_platformInternalId_businessProcessReferenceId",
        background: true
    }
);

print("✓ Created compound index: idx_clientId_platformInternalId_businessProcessReferenceId");

// Optional: Index on lastModifiedTimeStamp for time-based queries and sorting
const timestampIndex = db.bundleSelection.createIndex(
    {
        lastModifiedTimeStamp: -1
    },
    {
        name: "idx_lastModifiedTimeStamp",
        background: true
    }
);

print("✓ Created timestamp index: idx_lastModifiedTimeStamp");

// Display all indexes on the collection
print("\nAll indexes on bundleSelection collection:");
db.bundleSelection.getIndexes().forEach(function (index) {
    print("  - " + index.name + ": " + JSON.stringify(index.key));
});

print("\nIndex creation complete!");
