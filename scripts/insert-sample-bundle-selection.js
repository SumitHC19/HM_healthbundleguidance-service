// MongoDB script to insert sample bundleSelection documents and create indexes
// Usage with auth: docker exec -i local-mongodb mongosh -u admin -p 'Ch0nge#12#' --authenticationDatabase admin personHealthDataDV < insert-sample-bundle-selection.js
// Usage without auth: mongosh personHealthDataDV < insert-sample-bundle-selection.js

db = db.getSiblingDB('personHealthDataDV');

// Drop existing bundleSelection collection (if you want a clean start)
// db.bundleSelection.drop();

print("=== Setting up bundleSelection collection ===\n");

// Create indexes first (before inserting data for optimal performance)
print("1. Creating indexes...");

db.bundleSelection.createIndex(
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
print("   ✓ Compound index created for query optimization");

db.bundleSelection.createIndex(
    {
        lastModifiedTimeStamp: -1
    },
    {
        name: "idx_lastModifiedTimeStamp",
        background: true
    }
);
print("   ✓ Timestamp index created for time-based queries\n");

// Insert sample documents
print("2. Inserting sample bundleSelection documents...");

const sampleData = [
    {
        evaluationId: "eval-12345",
        businessProcessReferenceId: "bp-ref-12345",
        clientId: "19968",
        platformInternalId: "007700057",
        featuredAs: "balanced",
        planYearBeginDate: "2026-01-01",
        lastModifiedTimeStamp: new Date().toISOString()
    },
    {
        evaluationId: "eval-67890",
        businessProcessReferenceId: "bp-ref-67890",
        clientId: "19968",
        platformInternalId: "007700058",
        featuredAs: "savings",
        planYearBeginDate: "2026-01-01",
        lastModifiedTimeStamp: new Date().toISOString()
    },
    {
        evaluationId: "eval-11111",
        businessProcessReferenceId: "bp-ref-11111",
        clientId: "19968",
        platformInternalId: "007700059",
        featuredAs: "coverage",
        planYearBeginDate: "2026-01-01",
        lastModifiedTimeStamp: new Date().toISOString()
    },
    {
        evaluationId: "eval-22222",
        businessProcessReferenceId: "bp-ref-22222",
        clientId: "20000",
        platformInternalId: "007700060",
        featuredAs: "balanced",
        planYearBeginDate: "2025-01-01",
        lastModifiedTimeStamp: new Date().toISOString()
    }
];

const result = db.bundleSelection.insertMany(sampleData);

print(`   ✓ Successfully inserted ${result.insertedIds.length} documents\n`);

// Display collection stats
print("3. Collection Summary:");
print(`   Total documents: ${db.bundleSelection.countDocuments()}`);
print(`   Indexes: ${db.bundleSelection.getIndexes().length}`);

db.bundleSelection.getIndexes().forEach(function(index) {
    print(`      - ${index.name}`);
});

print("\n✓ Setup complete! Use this query to verify:");
print("   db.bundleSelection.find({clientId: '19968'}).pretty()");
print("\nTo check index usage:");
print("   db.bundleSelection.find({clientId: '19968', platformInternalId: '007700057', businessProcessReferenceId: 'bp-ref-12345'}).explain('executionStats')");
