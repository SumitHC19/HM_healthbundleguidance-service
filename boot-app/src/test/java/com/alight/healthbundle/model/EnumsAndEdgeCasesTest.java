package com.alight.healthbundle.model;

import com.alight.healthbundle.model.enums.FeaturedAs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Model Tests - Enums and Edge Cases")
class EnumsAndEdgeCasesTest {

    @Nested
    @DisplayName("FeaturedAs Enum Tests")
    class FeaturedAsEnumTests {
        @Test
        void testAllFeaturedAsEnumValues() {
            FeaturedAs[] values = FeaturedAs.values();
            assertThat(values).isNotEmpty();
            assertThat(values.length).isGreaterThan(0);
        }

        @Test
        void testFeaturedAsValueOf() {
            FeaturedAs balanced = FeaturedAs.valueOf("balanced");
            assertThat(balanced).isEqualTo(FeaturedAs.balanced);
        }

        @Test
        void testFeaturedAsEquality() {
            FeaturedAs balanced1 = FeaturedAs.balanced;
            FeaturedAs balanced2 = FeaturedAs.valueOf("balanced");
            assertThat(balanced1).isEqualTo(balanced2);
        }

        @Test
        void testFeaturedAsLowerPremiums() {
            FeaturedAs lowerPremiums = FeaturedAs.lower_premiums;
            assertThat(lowerPremiums).isNotNull();
            assertThat(lowerPremiums.name()).isEqualTo("lower_premiums");
        }

        @Test
        void testFeaturedAsHDHPVariants() {
            FeaturedAs hdhp = FeaturedAs.balanced_hdhp;
            FeaturedAs hdhpWider = FeaturedAs.balanced_hdhp_wider;

            assertThat(hdhp).isNotNull();
            assertThat(hdhpWider).isNotNull();
            assertThat(hdhp).isNotEqualTo(hdhpWider);
        }

        @Test
        void testFeaturedAsNoSupplementalVariants() {
            FeaturedAs noSupplement = FeaturedAs.balanced_no_supplemental;
            FeaturedAs regular = FeaturedAs.balanced;

            assertThat(noSupplement).isNotNull();
            assertThat(regular).isNotNull();
            assertThat(noSupplement).isNotEqualTo(regular);
        }

        @Test
        void testFeaturedAsEnumCount() {
            FeaturedAs[] values = FeaturedAs.values();
            assertThat(values.length).isGreaterThanOrEqualTo(22);
        }

        @Test
        void testFeaturedAsVariousValues() {
            String[] values = {
                    "balanced", "balanced_wider", "lower_premiums", "lower_adverse",
                    "lower_expected", "balanced_hdhp", "lower_deductible", "fully_loaded"
            };
            for (String value : values) {
                FeaturedAs featured = FeaturedAs.valueOf(value);
                assertThat(featured).isNotNull();
                assertThat(featured.name()).isEqualTo(value);
            }
        }
    }

    @Nested
    @DisplayName("BundleSelection Enum Usage Tests")
    class BundleSelectionEnumUsageTests {

        @Test
        void testBundleSelectionWithDefaultFeaturedAs() {
            BundleSelection bundle = new BundleSelection();
            bundle.setFeaturedAs("balanced");

            assertThat(bundle.getFeaturedAs()).isEqualTo("balanced");
        }
    }

    @Nested
    @DisplayName("Edge Cases - String Fields")
    class EdgeCasesStringFieldsTests {
        @Test
        void testPersonWithEmptyStrings() {
            Person person = Person.builder()
                    .id("")
                    .birthdate("")
                    .relationship("")
                    .build();

            assertThat(person.getId()).isEmpty();
            assertThat(person.getBirthdate()).isEmpty();
            assertThat(person.getRelationship()).isEmpty();
        }

        @Test
        void testPersonWithWhitespaceStrings() {
            Person person = Person.builder()
                    .id("   ")
                    .birthdate("   ")
                    .relationship("   ")
                    .build();

            assertThat(person.getId()).isNotEmpty();
            assertThat(person.getId().isBlank()).isTrue();
        }

        @Test
        void testPersonWithSpecialCharacters() {
            String specialId = "p@#$%^&*()_+-={}[]|:\\;\"'<>,.?/";
            Person person = Person.builder()
                    .id(specialId)
                    .birthdate("1980-01-01")
                    .relationship("self")
                    .build();

            assertThat(person.getId()).isEqualTo(specialId);
        }

        @Test
        void testPersonWithUnicodeCharacters() {
            Person person = Person.builder()
                    .id("p_עברית_日本語")
                    .birthdate("1980-01-01")
                    .relationship("父")
                    .build();

            assertThat(person.getId()).contains("עברית");
            assertThat(person.getRelationship()).isEqualTo("父");
        }

        @Test
        void testProductWithVeryLongStrings() {
            String longString = "A".repeat(10000);
            Product product = Product.builder()
                    .type(longString)
                    .productId("prod-1")
                    .planIds(List.of("plan-1"))
                    .build();

            assertThat(product.getType().length()).isEqualTo(10000);
        }
    }

    @Nested
    @DisplayName("Edge Cases - Numeric Fields")
    class EdgeCasesNumericFieldsTests {
        @Test
        void testBigDecimalMaxValue() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal maxValue = new BigDecimal("999999999999999999.99");
            advantage.setEmployeeContributions(maxValue);

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(maxValue);
        }

        @Test
        void testBigDecimalMinValue() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal minValue = new BigDecimal("0.01");
            advantage.setEmployeeContributions(minValue);

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(minValue);
        }

        @Test
        void testBigDecimalWithNegativeValue() {
            ScenarioCosts costs = new ScenarioCosts();
            costs.setTotalCost(new BigDecimal("-100.00"));

            assertThat(costs.getTotalCost()).isEqualByComparingTo(new BigDecimal("-100.00"));
        }

        @Test
        void testIntegerZeroValue() {
            Subscriber subscriber = Subscriber.builder()
                    .id("sub1")
                    .payPeriodsPlanyear(0)
                    .payPeriodsRemaining(0)
                    .build();

            assertThat(subscriber.getPayPeriodsPlanyear()).isEqualTo(0);
            assertThat(subscriber.getPayPeriodsRemaining()).isEqualTo(0);
        }

        @Test
        void testIntegerMaxValue() {
            Bundle bundle = new Bundle();
            bundle.setBundleRank(Integer.MAX_VALUE);

            assertThat(bundle.getBundleRank()).isEqualTo(Integer.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("Edge Cases - Collections")
    class EdgeCasesCollectionsTests {
        @Test
        void testPersonWithEmptyHealthcareMap() {
            Person person = Person.builder()
                    .id("p1")
                    .birthdate("1980-01-01")
                    .relationship("self")
                    .healthcare(new HashMap<>())
                    .build();

            assertThat(person.getHealthcare()).isEmpty();
        }

        @Test
        void testPersonWithLargeHealthcareMap() {
            Map<String, Object> largeMap = new HashMap<>();
            for (int i = 0; i < 1000; i++) {
                largeMap.put("key" + i, "value" + i);
            }

            Person person = Person.builder()
                    .id("p1")
                    .birthdate("1980-01-01")
                    .relationship("self")
                    .healthcare(largeMap)
                    .build();

            assertThat(person.getHealthcare()).hasSize(1000);
        }

        @Test
        void testProductWithEmptyPlanIds() {
            Product product = Product.builder()
                    .type("medical")
                    .productId("prod-1")
                    .planIds(new ArrayList<>())
                    .build();

            assertThat(product.getPlanIds()).isEmpty();
        }

        @Test
        void testProductWithManyPlanIds() {
            List<String> manyPlans = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                manyPlans.add("plan-" + i);
            }

            Product product = Product.builder()
                    .type("medical")
                    .productId("prod-1")
                    .planIds(manyPlans)
                    .build();

            assertThat(product.getPlanIds()).hasSize(100);
        }

        @Test
        void testBundleWithEmptyProductsList() {
            Bundle bundle = new Bundle();
            bundle.setProductUses(new ArrayList<>());

            assertThat(bundle.getProductUses()).isEmpty();
        }

        @Test
        void testBundleWithManyProducts() {
            List<ProductUse> manyProducts = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                ProductUse product = new ProductUse();
                product.setProductId("prod-" + i);
                manyProducts.add(product);
            }

            Bundle bundle = new Bundle();
            bundle.setProductUses(manyProducts);

            assertThat(bundle.getProductUses()).hasSize(50);
        }

        @Test
        void testOutOfPocketBreakdownWithLargeMaps() {
            Map<String, BigDecimal> largeMap = new HashMap<>();
            for (int i = 0; i < 500; i++) {
                largeMap.put("category-" + i, new BigDecimal(i + ".00"));
            }

            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();
            breakdown.setByCategory(largeMap);

            assertThat(breakdown.getByCategory()).hasSize(500);
        }
    }

    @Nested
    @DisplayName("Edge Cases - DateTime Fields")
    class EdgeCasesDateTimeFieldsTests {
        @Test
        void testEvaluationInputRequestWithCurrentDateTime() {
            LocalDateTime now = LocalDateTime.now();
            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .businessProcessReferenceId("bpr-1")
                    .createdAt(now)
                    .build();

            assertThat(request.getCreatedAt()).isEqualTo(now);
        }

        @Test
        void testEvaluationInputRequestWithFutureDateTimes() {
            LocalDateTime future = LocalDateTime.now().plusYears(1);
            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .businessProcessReferenceId("bpr-1")
                    .updatedAt(future)
                    .build();

            assertThat(request.getUpdatedAt()).isEqualTo(future);
        }

        @Test
        void testEvaluationInputRequestWithPastDateTimes() {
            LocalDateTime past = LocalDateTime.now().minusYears(1);
            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .businessProcessReferenceId("bpr-1")
                    .createdAt(past)
                    .build();

            assertThat(request.getCreatedAt()).isEqualTo(past);
        }

        @Test
        void testEvaluationInputRequestDateSequence() {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime middle = start.plusDays(5);
            LocalDateTime end = start.plusDays(10);

            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .businessProcessReferenceId("bpr-1")
                    .createdAt(start)
                    .savedAt(middle)
                    .updatedAt(end)
                    .build();

            assertThat(request.getCreatedAt()).isBefore(request.getSavedAt());
            assertThat(request.getSavedAt()).isBefore(request.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Edge Cases - Null Handling")
    class EdgeCasesNullHandlingTests {
        @Test
        void testPersonAllFieldsNull() {
            Person person = Person.builder()
                    .id(null)
                    .birthdate(null)
                    .relationship(null)
                    .healthcare(null)
                    .build();

            assertThat(person.getId()).isNull();
            assertThat(person.getBirthdate()).isNull();
            assertThat(person.getRelationship()).isNull();
            assertThat(person.getHealthcare()).isNull();
        }

        @Test
        void testSubscriberAllFieldsNull() {
            Subscriber subscriber = Subscriber.builder()
                    .id(null)
                    .birthdate(null)
                    .annualPay(null)
                    .taxState(null)
                    .payPeriodsPlanyear(null)
                    .payPeriodsRemaining(null)
                    .build();

            assertThat(subscriber.getId()).isNull();
            assertThat(subscriber.getAnnualPay()).isNull();
        }

        @Test
        void testBundleAllFieldsNull() {
            Bundle bundle = new Bundle();
            bundle.setBundleRank(null);
            bundle.setFeaturedAs(null);
            bundle.setIsCustomizable(null);
            bundle.setEstimatedCosts(null);
            bundle.setPlanUses(null);
            bundle.setProductUses(null);

            assertThat(bundle.getBundleRank()).isNull();
            assertThat(bundle.getEstimatedCosts()).isNull();
            assertThat(bundle.getPlanUses()).isNull();
        }

        @Test
        void testSAVVIRequestWithNullSubscriber() {
            SAVVIRequest request = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .subscriber(null)
                    .build();

            assertThat(request.getSubscriber()).isNull();
        }

        @Test
        void testProductWithNullPlanIds() {
            Product product = Product.builder()
                    .type("medical")
                    .productId("prod-1")
                    .planIds(null)
                    .build();

            assertThat(product.getPlanIds()).isNull();
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {
        @Test
        void testCoverageChangeWithEdgeCasePersonIds() {
            String[] edgePersonIds = { "0", "1", "-1", "999", "p_0", "" };

            for (String personId : edgePersonIds) {
                CoverageChange change = new CoverageChange();
                change.setPersonId(personId);
                assertThat(change.getPersonId()).isEqualTo(personId);
            }
        }

        @Test
        void testCoveredPersonWithEdgeLevels() {
            BigDecimal[] edgeLevels = {
                    BigDecimal.ZERO,
                    new BigDecimal("0.001"),
                    new BigDecimal("0.999"),
                    new BigDecimal("1"),
                    new BigDecimal("99999999.99")
            };

            for (BigDecimal level : edgeLevels) {
                CoveredPerson person = new CoveredPerson();
                person.setLevel(level);
                assertThat(person.getLevel()).isEqualByComparingTo(level);
            }
        }

        @Test
        void testGrowthIllustrationWithBoundaryDays() {
            int[] durations = { 0, 1, 10, 30, 365 };

            for (int duration : durations) {
                GrowthIllustration growth = new GrowthIllustration();
                growth.setDurationYears(duration);
                assertThat(growth.getDurationYears()).isEqualTo(duration);
            }
        }
    }

    @Nested
    @DisplayName("Mutation Tests - State Changes")
    class MutationTests {
        @Test
        void testBundleStateChanges() {
            Bundle bundle = new Bundle();

            // Initial state
            assertThat(bundle.getBundleRank()).isNull();

            // Change state
            bundle.setBundleRank(1);
            assertThat(bundle.getBundleRank()).isEqualTo(1);

            // Change again
            bundle.setBundleRank(5);
            assertThat(bundle.getBundleRank()).isEqualTo(5);

            // Set to null
            bundle.setBundleRank(null);
            assertThat(bundle.getBundleRank()).isNull();
        }

        @Test
        void testScenarioCostsMultipleMutations() {
            ScenarioCosts costs = new ScenarioCosts();

            // Mutation 1
            costs.setTotalCost(new BigDecimal("1000.00"));
            assertThat(costs.getTotalCost()).isEqualByComparingTo(new BigDecimal("1000.00"));

            // Mutation 2
            costs.setTotalCost(new BigDecimal("2000.00"));
            assertThat(costs.getTotalCost()).isEqualByComparingTo(new BigDecimal("2000.00"));

            // Mutation 3
            costs.setTotalCost(new BigDecimal("0.00"));
            assertThat(costs.getTotalCost()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testProductUseIndependentMutations() {
            ProductUse use1 = new ProductUse();
            ProductUse use2 = new ProductUse();

            use1.setProductId("prod-1");
            use2.setProductId("prod-2");

            assertThat(use1.getProductId()).isNotEqualTo(use2.getProductId());

            // Mutate use1
            use1.setType("medical");
            assertThat(use1.getType()).isEqualTo("medical");
            assertThat(use2.getType()).isNull();
        }
    }

    @Nested
    @DisplayName("Type Consistency Tests")
    class TypeConsistencyTests {
        @Test
        void testBigDecimalPrecision() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal value = new BigDecimal("123.45");
            advantage.setEmployeeContributions(value);

            assertThat(advantage.getEmployeeContributions().scale()).isEqualTo(2);
            assertThat(advantage.getEmployeeContributions().precision()).isGreaterThanOrEqualTo(5);
        }

        @Test
        void testStringTypePreservation() {
            Product product = Product.builder()
                    .type("medical")
                    .productId("prod-1")
                    .planIds(List.of("plan-1"))
                    .kind("PPO")
                    .build();

            assertThat(product.getType()).isInstanceOf(String.class);
            assertThat(product.getProductId()).isInstanceOf(String.class);
        }

        @Test
        void testListTypePreservation() {
            SAVVIRequest request = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .products(List.of())
                    .build();

            assertThat(request.getProducts()).isInstanceOf(List.class);
        }

        @Test
        void testMapTypePreservation() {
            Person person = Person.builder()
                    .id("p1")
                    .birthdate("1980-01-01")
                    .relationship("self")
                    .healthcare(Map.of("plan", "Medical"))
                    .build();

            assertThat(person.getHealthcare()).isInstanceOf(Map.class);
        }
    }
}
