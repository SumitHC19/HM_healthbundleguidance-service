package com.alight.healthbundle.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Model Tests - Complex Getter/Setter Classes")
class ComplexModelsTest {

    @Nested
    @DisplayName("HealthAccountAdvantages Model Tests")
    class HealthAccountAdvantagesTests {
        @Test
        void testHealthAccountAdvantagesWithAllFields() {
            HealthAccountAdvantages advantages = new HealthAccountAdvantages();
            advantages.setAccountTypes("HSA,FSA,LPFSA");

            AccountAdvantage hsaAdvantage = new AccountAdvantage();
            hsaAdvantage.setEmployeeContributions(new BigDecimal("3850.00"));

            AccountAdvantage fsaAdvantage = new AccountAdvantage();
            fsaAdvantage.setEmployeeContributions(new BigDecimal("2850.00"));

            AccountAdvantage lpfsaAdvantage = new AccountAdvantage();
            lpfsaAdvantage.setEmployeeContributions(new BigDecimal("5000.00"));

            AccountAdvantage totalAdvantage = new AccountAdvantage();
            totalAdvantage.setEmployeeContributions(new BigDecimal("11700.00"));

            advantages.setHsa(hsaAdvantage);
            advantages.setFsa(fsaAdvantage);
            advantages.setLpfsa(lpfsaAdvantage);
            advantages.setTotal(totalAdvantage);

            assertThat(advantages.getAccountTypes()).isEqualTo("HSA,FSA,LPFSA");
            assertThat(advantages.getHsa()).isNotNull();
            assertThat(advantages.getFsa()).isNotNull();
            assertThat(advantages.getLpfsa()).isNotNull();
            assertThat(advantages.getTotal()).isNotNull();
        }

        @Test
        void testHealthAccountAdvantagesWithOnlyHSA() {
            HealthAccountAdvantages advantages = new HealthAccountAdvantages();
            advantages.setAccountTypes("HSA");

            AccountAdvantage hsaAdvantage = new AccountAdvantage();
            hsaAdvantage.setEmployeeContributions(new BigDecimal("3850.00"));
            advantages.setHsa(hsaAdvantage);

            assertThat(advantages.getAccountTypes()).isEqualTo("HSA");
            assertThat(advantages.getHsa()).isNotNull();
            assertThat(advantages.getFsa()).isNull();
            assertThat(advantages.getLpfsa()).isNull();
        }

        @Test
        void testHealthAccountAdvantagesEmpty() {
            HealthAccountAdvantages advantages = new HealthAccountAdvantages();
            assertThat(advantages.getAccountTypes()).isNull();
            assertThat(advantages.getHsa()).isNull();
            assertThat(advantages.getFsa()).isNull();
        }
    }

    @Nested
    @DisplayName("GrowthIllustration Model Tests")
    class GrowthIllustrationTests {
        @Test
        void testGrowthIllustrationWithAllFields() {
            GrowthIllustration growth = new GrowthIllustration();
            growth.setMonthlyContribution(new BigDecimal("500.00"));
            growth.setDurationYears(10);
            growth.setIrsLimit(new BigDecimal("3850.00"));
            growth.setInitialBalance(new BigDecimal("1000.00"));
            growth.setIllustrativeMonthlyReturn(new BigDecimal("8.50"));

            assertThat(growth.getMonthlyContribution()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(growth.getDurationYears()).isEqualTo(10);
            assertThat(growth.getIrsLimit()).isEqualByComparingTo(new BigDecimal("3850.00"));
            assertThat(growth.getInitialBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        void testGrowthIllustrationWithLists() {
            GrowthIllustration growth = new GrowthIllustration();
            List<BigDecimal> basis = List.of(
                    new BigDecimal("1000.00"),
                    new BigDecimal("2000.00"),
                    new BigDecimal("3000.00"));
            List<BigDecimal> totalBalance = List.of(
                    new BigDecimal("5000.00"),
                    new BigDecimal("6000.00"),
                    new BigDecimal("7000.00"));

            growth.setBasis(basis);
            growth.setTotalBalance(totalBalance);

            assertThat(growth.getBasis()).hasSize(3);
            assertThat(growth.getTotalBalance()).hasSize(3);
        }

        @Test
        void testGrowthIllustrationWithZeroContribution() {
            GrowthIllustration growth = new GrowthIllustration();
            growth.setMonthlyContribution(BigDecimal.ZERO);

            assertThat(growth.getMonthlyContribution()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testGrowthIllustrationWithNullValues() {
            GrowthIllustration growth = new GrowthIllustration();
            assertThat(growth.getMonthlyContribution()).isNull();
            assertThat(growth.getBasis()).isNull();
            assertThat(growth.getTotalBalance()).isNull();
        }
    }

    @Nested
    @DisplayName("ProductUseChangeVsCurrent Model Tests")
    class ProductUseChangeVsCurrentTests {
        @Test
        void testProductUseChangeVsCurrentWithAllFields() {
            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            change.setPremiumChange(new BigDecimal("100.00"));
            change.setEmployeeContributionChange(new BigDecimal("50.00"));

            CoverageChange coverageChange = new CoverageChange();
            coverageChange.setChangeType("added");
            change.setCoverageChanges(List.of(coverageChange));

            assertThat(change.getPremiumChange()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(change.getEmployeeContributionChange()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(change.getCoverageChanges()).hasSize(1);
        }

        @Test
        void testProductUseChangeVsCurrentWithMultipleCoverageChanges() {
            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            List<CoverageChange> coverageChanges = List.of(
                    new CoverageChange() {
                        {
                            setChangeType("added");
                            setPersonId("p1");
                        }
                    },
                    new CoverageChange() {
                        {
                            setChangeType("removed");
                            setPersonId("p2");
                        }
                    });
            change.setCoverageChanges(coverageChanges);

            assertThat(change.getCoverageChanges()).hasSize(2);
        }

        @Test
        void testProductUseChangeVsCurrentWithNegativeChange() {
            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            change.setPremiumChange(new BigDecimal("-50.00"));
            change.setEmployeeContributionChange(new BigDecimal("-25.00"));

            assertThat(change.getPremiumChange()).isEqualByComparingTo(new BigDecimal("-50.00"));
            assertThat(change.getEmployeeContributionChange()).isEqualByComparingTo(new BigDecimal("-25.00"));
        }

        @Test
        void testProductUseChangeVsCurrentEmpty() {
            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            assertThat(change.getPremiumChange()).isNull();
            assertThat(change.getCoverageChanges()).isNull();
        }
    }

    @Nested
    @DisplayName("ProductUse Model Tests")
    class ProductUseTests {
        @Test
        void testProductUseWithAllFields() {
            ProductUse productUse = new ProductUse();
            productUse.setType("medical");
            productUse.setProductId("prod-1");
            productUse.setPremium(new BigDecimal("500.00"));
            productUse.setTotalPremiums(new BigDecimal("1500.00"));
            productUse.setNumDeductionsPlanyear(12);
            productUse.setNumDeductionsRemaining(6);

            assertThat(productUse.getType()).isEqualTo("medical");
            assertThat(productUse.getProductId()).isEqualTo("prod-1");
            assertThat(productUse.getPremium()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(productUse.getTotalPremiums()).isEqualByComparingTo(new BigDecimal("1500.00"));
            assertThat(productUse.getNumDeductionsPlanyear()).isEqualTo(12);
            assertThat(productUse.getNumDeductionsRemaining()).isEqualTo(6);
        }

        @Test
        void testProductUseWithVsCurrentChange() {
            ProductUse productUse = new ProductUse();
            productUse.setProductId("prod-1");

            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            change.setPremiumChange(new BigDecimal("50.00"));
            productUse.setVsCurrent(change);

            assertThat(productUse.getVsCurrent()).isNotNull();
            assertThat(productUse.getVsCurrent().getPremiumChange()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        void testProductUseWithZeroDeductions() {
            ProductUse productUse = new ProductUse();
            productUse.setNumDeductionsPlanyear(0);
            productUse.setNumDeductionsRemaining(0);

            assertThat(productUse.getNumDeductionsPlanyear()).isEqualTo(0);
            assertThat(productUse.getNumDeductionsRemaining()).isEqualTo(0);
        }

        @Test
        void testProductUsePartialSetup() {
            ProductUse productUse = new ProductUse();
            productUse.setType("dental");
            productUse.setProductId("prod-2");

            assertThat(productUse.getType()).isEqualTo("dental");
            assertThat(productUse.getPremium()).isNull();
            assertThat(productUse.getVsCurrent()).isNull();
        }
    }

    @Nested
    @DisplayName("PlanUse Model Tests")
    class PlanUseTests {
        @Test
        void testPlanUseWithFields() {
            PlanUse planUse = new PlanUse();
            planUse.setType("medical");
            planUse.setPlanId("plan-1");

            assertThat(planUse.getType()).isEqualTo("medical");
            assertThat(planUse.getPlanId()).isEqualTo("plan-1");
        }

        @Test
        void testPlanUseWithEmptyValues() {
            PlanUse planUse = new PlanUse();
            assertThat(planUse.getType()).isNull();
            assertThat(planUse.getPlanId()).isNull();
        }
    }

    @Nested
    @DisplayName("Bundle Model Tests")
    class BundleTests {
        @Test
        void testBundleWithBasicFields() {
            Bundle bundle = new Bundle();
            bundle.setBundleRank(1);
            bundle.setFeaturedAs("recommended");
            bundle.setIsCustomizable(true);

            assertThat(bundle.getBundleRank()).isEqualTo(1);
            assertThat(bundle.getFeaturedAs()).isEqualTo("recommended");
            assertThat(bundle.getIsCustomizable()).isTrue();
        }

        @Test
        void testBundleWithEstimatedCosts() {
            Bundle bundle = new Bundle();
            EstimatedCosts costs = new EstimatedCosts();
            ScenarioCosts expected = new ScenarioCosts();
            expected.setTotalCost(new BigDecimal("5000.00"));
            costs.setExpected(expected);

            bundle.setEstimatedCosts(costs);

            assertThat(bundle.getEstimatedCosts()).isNotNull();
            assertThat(bundle.getEstimatedCosts().getExpected().getTotalCost())
                    .isEqualByComparingTo(new BigDecimal("5000.00"));
        }

        @Test
        void testBundleWithPlans() {
            Bundle bundle = new Bundle();
            PlanUse plan1 = new PlanUse();
            plan1.setType("medical");
            plan1.setPlanId("plan-1");

            PlanUse plan2 = new PlanUse();
            plan2.setType("dental");
            plan2.setPlanId("plan-2");

            bundle.setPlanUses(List.of(plan1, plan2));

            assertThat(bundle.getPlanUses()).hasSize(2);
        }

        @Test
        void testBundleWithProducts() {
            Bundle bundle = new Bundle();
            ProductUse product1 = new ProductUse();
            product1.setProductId("prod-1");
            product1.setType("medical");

            ProductUse product2 = new ProductUse();
            product2.setProductId("prod-2");
            product2.setType("dental");

            bundle.setProductUses(List.of(product1, product2));

            assertThat(bundle.getProductUses()).hasSize(2);
        }

        @Test
        void testBundleWithFeaturedVariants() {
            Bundle bundle = new Bundle();
            bundle.setFeaturedAsVariants(List.of("recommended", "balanced", "economical"));

            assertThat(bundle.getFeaturedAsVariants()).hasSize(3);
            assertThat(bundle.getFeaturedAsVariants()).contains("recommended", "balanced");
        }

        @Test
        void testBundleWithVignettes() {
            Bundle bundle = new Bundle();
            Vignettes vignettes = new Vignettes();
            bundle.setVignettes(vignettes);

            assertThat(bundle.getVignettes()).isNotNull();
        }

        @Test
        void testBundleWithRankOptions() {
            for (int rank = 1; rank <= 5; rank++) {
                Bundle bundle = new Bundle();
                bundle.setBundleRank(rank);
                assertThat(bundle.getBundleRank()).isEqualTo(rank);
            }
        }

        @Test
        void testBundleEmpty() {
            Bundle bundle = new Bundle();
            assertThat(bundle.getBundleRank()).isNull();
            assertThat(bundle.getFeaturedAs()).isNull();
            assertThat(bundle.getIsCustomizable()).isNull();
            assertThat(bundle.getEstimatedCosts()).isNull();
        }
    }

    @Nested
    @DisplayName("Accounts Model Tests")
    class AccountsTests {
        @Test
        void testAccountsWithFields() {
            Accounts accounts = new Accounts();
            assertThat(accounts).isNotNull();
        }
    }

    @Nested
    @DisplayName("BundlePlanCosts Model Tests")
    class BundlePlanCostsTests {
        @Test
        void testBundlePlanCostsWithBaseBundle() {
            BundlePlanCosts bundleCosts = new BundlePlanCosts();
            VignetteBundleCost baseCost = new VignetteBundleCost();
            baseCost.setPremiums(new BigDecimal("500.00"));
            baseCost.setType("medical");

            bundleCosts.setBaseBundle(baseCost);

            assertThat(bundleCosts.getBaseBundle()).isNotNull();
            assertThat(bundleCosts.getBaseBundle().getPremiums()).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        void testBundlePlanCostsEmpty() {
            BundlePlanCosts bundleCosts = new BundlePlanCosts();
            assertThat(bundleCosts.getBaseBundle()).isNull();
        }
    }

    @Nested
    @DisplayName("EvaluationOutput Model Tests")
    class EvaluationOutputTests {
        @Test
        void testEvaluationOutputWithBundles() {
            EvaluationOutput output = new EvaluationOutput();

            Bundle bundle1 = new Bundle();
            bundle1.setBundleRank(1);
            bundle1.setFeaturedAs("recommended");

            Bundle bundle2 = new Bundle();
            bundle2.setBundleRank(2);
            bundle2.setFeaturedAs("balanced");

            output.setBundles(List.of(bundle1, bundle2));

            assertThat(output.getBundles()).hasSize(2);
            assertThat(output.getBundles().get(0).getBundleRank()).isEqualTo(1);
            assertThat(output.getBundles().get(1).getBundleRank()).isEqualTo(2);
        }

        @Test
        void testEvaluationOutputWithSingleBundle() {
            EvaluationOutput output = new EvaluationOutput();
            Bundle bundle = new Bundle();
            bundle.setBundleRank(1);

            output.setBundles(List.of(bundle));

            assertThat(output.getBundles()).hasSize(1);
        }

        @Test
        void testEvaluationOutputWithEmptyBundles() {
            EvaluationOutput output = new EvaluationOutput();
            output.setBundles(List.of());

            assertThat(output.getBundles()).isEmpty();
        }
    }

    @Nested
    @DisplayName("ClosestComparison Model Tests")
    class ClosestComparisonTests {
        @Test
        void testClosestComparisonWithFields() {
            ClosestComparison comparison = new ClosestComparison();
            assertThat(comparison).isNotNull();
        }
    }

    @Nested
    @DisplayName("Vignettes Model Tests")
    class VignettesTests {
        @Test
        void testVignettesWithFields() {
            Vignettes vignettes = new Vignettes();
            assertThat(vignettes).isNotNull();
        }
    }

    @Nested
    @DisplayName("EvaluationResultsResponse Model Tests")
    class EvaluationResultsResponseTests {
        @Test
        void testEvaluationResultsResponseWithOutput() {
            EvaluationResultsResponse response = new EvaluationResultsResponse();
            response.setEventType("evaluation.complete");
            response.setEvaluationId("eval-1");
            response.setEvaluationType("standard");
            response.setEvaluationStatus("success");

            EvaluationOutput output = new EvaluationOutput();
            Bundle bundle = new Bundle();
            bundle.setBundleRank(1);
            output.setBundles(List.of(bundle));

            response.setEvaluationOutput(output);

            assertThat(response.getEvaluationOutput()).isNotNull();
            assertThat(response.getEvaluationOutput().getBundles()).hasSize(1);
        }

        @Test
        void testEvaluationResultsResponseWithMetadata() {
            LocalDateTime now = LocalDateTime.now();
            EvaluationResultsResponse response = new EvaluationResultsResponse();
            response.setSource("test-source");
            response.setCreatedAt(now);
            response.setUpdatedAt(now.plusMinutes(1));

            assertThat(response.getSource()).isEqualTo("test-source");
            assertThat(response.getCreatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Complex Nested Structure Tests")
    class ComplexNestedStructureTests {
        @Test
        void testFullBundleStructure() {
            // Create a complete bundle with all nested structures
            Bundle bundle = new Bundle();
            bundle.setBundleRank(1);
            bundle.setFeaturedAs("recommended");

            // Add estimated costs
            EstimatedCosts costs = new EstimatedCosts();
            ScenarioCosts expected = new ScenarioCosts();
            expected.setTotalCost(new BigDecimal("5000.00"));
            expected.setTotalPremiums(new BigDecimal("4000.00"));

            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();
            breakdown.setByCategory(Map.of("medical", new BigDecimal("500.00")));
            expected.setOutOfPocketBreakdown(breakdown);

            costs.setExpected(expected);
            bundle.setEstimatedCosts(costs);

            // Add product uses
            ProductUse productUse = new ProductUse();
            productUse.setProductId("prod-1");
            productUse.setType("medical");
            productUse.setPremium(new BigDecimal("500.00"));

            ProductUseChangeVsCurrent change = new ProductUseChangeVsCurrent();
            change.setPremiumChange(new BigDecimal("50.00"));
            productUse.setVsCurrent(change);

            bundle.setProductUses(List.of(productUse));

            // Verify the complete structure
            assertThat(bundle.getBundleRank()).isEqualTo(1);
            assertThat(bundle.getEstimatedCosts().getExpected().getTotalCost())
                    .isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(bundle.getProductUses()).hasSize(1);
            assertThat(bundle.getProductUses().get(0).getVsCurrent().getPremiumChange())
                    .isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        void testFullEvaluationOutput() {
            EvaluationOutput output = new EvaluationOutput();

            Bundle bundle = new Bundle();
            bundle.setBundleRank(1);

            EstimatedCosts costs = new EstimatedCosts();
            ScenarioCosts expected = new ScenarioCosts();
            expected.setTotalCost(new BigDecimal("5000.00"));
            costs.setExpected(expected);
            bundle.setEstimatedCosts(costs);

            output.setBundles(List.of(bundle));

            assertThat(output.getBundles()).hasSize(1);
            assertThat(output.getBundles().get(0).getEstimatedCosts().getExpected().getTotalCost())
                    .isEqualByComparingTo(new BigDecimal("5000.00"));
        }
    }
}
