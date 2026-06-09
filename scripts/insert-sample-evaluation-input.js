// Script to insert sample evaluation input data for testing
// Database: personHealthDataDV
// Collection: evaluationInput

// Switch to the database
db = db.getSiblingDB('personHealthDataDV');

// Sample evaluation input document based on OpenAPI spec example
const sampleEvaluationInput = {
    clientId: "19968",
    platformInternalId: "007700057",
    evaluationId: "15bf0d9e765dd08eda3de5169e086c6",
    businessProcessReferenceId: "12345",
    savviRequest: {
        planYearStartDate: "2025-01-01",
        planYearEndDate: "2025-12-31",
        enrollmentMode: "annual",
        effectiveDate: "2025-01-01",
        subscriber: {
            id: "VBAYSGJWFI9GP5CHAGIYGGUMHNZN2QOA",
            birthdate: "1979-02-15",
            annualPay: 80000,
            taxState: "MD",
            payPeriodsPlanyear: 26,
            payPeriodsRemaining: 26,
            healthcare: {
                expectedUsage: {
                    medicalCare: {
                        type: "level",
                        usage: "low"
                    }
                }
            }
        },
        coverablePeople: [
            {
                id: "R0JDJ4IUL8XLXCTUJFCYH7PNYT9FUVA6",
                birthdate: "2016-07-15",
                relationship: "child",
                healthcare: {
                    expectedUsage: {
                        medicalCare: {
                            type: "level",
                            usage: "low"
                        }
                    }
                }
            },
            {
                id: "HUJ85NFTDJGKR1LA9BXE8AZSMPJ7WPP1",
                birthdate: "1981-05-15",
                relationship: "spouse",
                healthcare: {
                    expectedUsage: {
                        medicalCare: {
                            type: "level",
                            usage: "low"
                        }
                    }
                }
            },
            {
                id: "GM0YSSPC8BVB6VFRAEUXKFDSC8WFSRIR",
                birthdate: "2012-11-15",
                relationship: "child",
                healthcare: {
                    expectedUsage: {
                        medicalCare: {
                            type: "level",
                            usage: "low"
                        }
                    }
                }
            }
        ],
        products: [
            {
                type: "medical_plan",
                productId: "VNCLWESSXLN6PQBPGEZMNNMJKZVCPQQT",
                planIds: ["P6cSfI3dH3dOEdrO"],
                inNetwork: {
                    coinsurance: 20,
                    deductibles: {
                        individual: 1600,
                        family: 3200
                    },
                    hasPreventiveCare: true,
                    services: {
                        drugsGeneric: {
                            coinsurance: 20,
                            maxCoinsurance: 10
                        }
                    },
                    outofpocketLimits: {
                        individual: 6000,
                        family: 12000
                    }
                },
                isHdhp: true
            },
            {
                type: "hsa",
                productId: "ER27FUDAHATKOBD3MRU88BJZ49D0I2OQ",
                planIds: ["Ps2iS4L70Kp1ZSSa"],
                isInvestable: true
            }
        ],
        eligibleOffers: [
            {
                offerType: "coverage_choice",
                planId: "P6cSfI3dH3dOEdrO",
                offerId: "KO8CE8BWT0FKK09OFXZJFPFEPSV9IZD4",
                numDeductionsPlanyear: 26,
                numDeductionsRemaining: 26,
                isSection125: true,
                coverageStartDate: "2025-01-01",
                coverageEndDate: "2025-12-31",
                coverageChoices: [
                    {
                        coverageId: "Y094HSEL3X3AEF2JG0DGDG15WFNNT6WM",
                        premium: 37.05,
                        coveredPeople: [
                            {
                                id: "VBAYSGJWFI9GP5CHAGIYGGUMHNZN2QOA"
                            }
                        ]
                    }
                ]
            }
        ],
        customizations: {
            customBundle: {
                plans: [
                    { planId: "PoJoQD6ZLOxgxrA1" },
                    { planId: "Pme3lQX7CnKK1g1H" },
                    { planId: "PabJKFAMCAGjQE2G" },
                    {
                        planId: "PUqXvCYJ5H8Fw0Km",
                        level: 20000,
                        levelSpouse: 10000,
                        levelChild: 2000
                    }
                ]
            }
        }
    },
    timestamp: "2026-01-01-00.00.00",
    savedAt: new Date(),
    createdAt: new Date(),
    updatedAt: new Date()
};

// Insert the sample document
const result = db.evaluationInput.insertOne(sampleEvaluationInput);

print(`Sample evaluation input inserted with ID: ${result.insertedId}`);
print(`\nYou can now test GET operations with:`);
print(`  - businessProcessReferenceId: 12345`);
print(`  - platformInternalId: 007700057`);
print(`  - evaluationId: 15bf0d9e765dd08eda3de5169e086c6`);
print(`\nDocument count in evaluationInput collection: ${db.evaluationInput.countDocuments()}`);
