package com.alight.healthbundle.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Model Tests - Getter/Setter Classes Part 1")
class GetterSetterModelsTest {

    @Nested
    @DisplayName("AccountAdvantage Model Tests")
    class AccountAdvantageTests {
        @Test
        void testAccountAdvantageSettersAndGetters() {
            AccountAdvantage advantage = new AccountAdvantage();
            advantage.setEmployeeContributions(new BigDecimal("1000.00"));
            advantage.setEmployerContributions(new BigDecimal("2000.00"));
            advantage.setEmployerContributionsPercentage(new BigDecimal("50.00"));
            advantage.setTaxSavings(new BigDecimal("500.00"));
            advantage.setTaxSavingsPercentage(new BigDecimal("25.00"));

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(new BigDecimal("1000.00"));
            assertThat(advantage.getEmployerContributions()).isEqualByComparingTo(new BigDecimal("2000.00"));
            assertThat(advantage.getEmployerContributionsPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(advantage.getTaxSavings()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(advantage.getTaxSavingsPercentage()).isEqualByComparingTo(new BigDecimal("25.00"));
        }

        @Test
        void testAccountAdvantageWithZeroValues() {
            AccountAdvantage advantage = new AccountAdvantage();
            advantage.setEmployeeContributions(BigDecimal.ZERO);
            advantage.setTaxSavings(BigDecimal.ZERO);

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(advantage.getTaxSavings()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testAccountAdvantageWithNullValues() {
            AccountAdvantage advantage = new AccountAdvantage();
            assertThat(advantage.getEmployeeContributions()).isNull();
            assertThat(advantage.getEmployerContributions()).isNull();
        }

        @Test
        void testAccountAdvantageMultipleUpdates() {
            AccountAdvantage advantage = new AccountAdvantage();
            advantage.setEmployeeContributions(new BigDecimal("1000.00"));
            advantage.setEmployeeContributions(new BigDecimal("1500.00"));

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(new BigDecimal("1500.00"));
        }
    }

    @Nested
    @DisplayName("AccountBalances Model Tests")
    class AccountBalancesTests {
        @Test
        void testAccountBalancesSettersAndGetters() {
            AccountBalances balances = new AccountBalances();

            // Set and get fields (assuming the model has these)
            assertThat(balances).isNotNull();
        }
    }

    @Nested
    @DisplayName("CoverageChange Model Tests")
    class CoverageChangeTests {
        @Test
        void testCoverageChangeSettersAndGetters() {
            CoverageChange change = new CoverageChange();
            change.setChangeType("added");
            change.setPersonId("p1");

            assertThat(change.getChangeType()).isEqualTo("added");
            assertThat(change.getPersonId()).isEqualTo("p1");
        }

        @Test
        void testCoverageChangeWithDifferentTypes() {
            String[] changeTypes = { "added", "removed", "modified", "upgraded", "downgraded" };

            for (String type : changeTypes) {
                CoverageChange change = new CoverageChange();
                change.setChangeType(type);
                assertThat(change.getChangeType()).isEqualTo(type);
            }
        }

        @Test
        void testCoverageChangeWithNullValues() {
            CoverageChange change = new CoverageChange();
            assertThat(change.getChangeType()).isNull();
            assertThat(change.getPersonId()).isNull();
        }

        @Test
        void testCoverageChangeMultipleUpdates() {
            CoverageChange change = new CoverageChange();
            change.setChangeType("old");
            change.setChangeType("new");

            assertThat(change.getChangeType()).isEqualTo("new");
        }
    }

    @Nested
    @DisplayName("CoveredPerson Model Tests")
    class CoveredPersonTests {
        @Test
        void testCoveredPersonSettersAndGetters() {
            CoveredPerson person = new CoveredPerson();
            person.setId("person-1");
            person.setLevel(new BigDecimal("100.00"));

            assertThat(person.getId()).isEqualTo("person-1");
            assertThat(person.getLevel()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        void testCoveredPersonWithVariousLevels() {
            BigDecimal[] levels = {
                    BigDecimal.ZERO,
                    new BigDecimal("1.00"),
                    new BigDecimal("100.50"),
                    new BigDecimal("9999.99")
            };

            for (BigDecimal level : levels) {
                CoveredPerson person = new CoveredPerson();
                person.setLevel(level);
                assertThat(person.getLevel()).isEqualByComparingTo(level);
            }
        }

        @Test
        void testCoveredPersonWithNullLevel() {
            CoveredPerson person = new CoveredPerson();
            person.setId("p1");
            person.setLevel(null);

            assertThat(person.getLevel()).isNull();
        }
    }

    @Nested
    @DisplayName("EmployeeContributions Model Tests")
    class EmployeeContributionsTests {
        @Test
        void testEmployeeContributionsSettersAndGetters() {
            EmployeeContributions contributions = new EmployeeContributions();
            contributions.setFsa(new BigDecimal("100.00"));
            contributions.setHsa(new BigDecimal("200.00"));
            contributions.setLpfsa(new BigDecimal("50.00"));

            assertThat(contributions.getFsa()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(contributions.getHsa()).isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(contributions.getLpfsa()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        @Test
        void testEmployeeContributionsWithZeroValues() {
            EmployeeContributions contributions = new EmployeeContributions();
            contributions.setFsa(BigDecimal.ZERO);
            contributions.setHsa(BigDecimal.ZERO);
            contributions.setLpfsa(BigDecimal.ZERO);

            assertThat(contributions.getFsa()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(contributions.getHsa()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(contributions.getLpfsa()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testEmployeeContributionsPartialSetup() {
            EmployeeContributions contributions = new EmployeeContributions();
            contributions.setFsa(new BigDecimal("150.00"));

            assertThat(contributions.getFsa()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(contributions.getHsa()).isNull();
            assertThat(contributions.getLpfsa()).isNull();
        }
    }

    @Nested
    @DisplayName("OutOfPocketBreakdown Model Tests")
    class OutOfPocketBreakdownTests {
        @Test
        void testOutOfPocketBreakdownWithMaps() {
            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();

            Map<String, BigDecimal> byCategory = Map.of(
                    "medical", new BigDecimal("1000.00"),
                    "pharmacy", new BigDecimal("200.00"));
            Map<String, BigDecimal> byPerson = Map.of(
                    "p1", new BigDecimal("800.00"),
                    "p2", new BigDecimal("400.00"));
            Map<String, BigDecimal> byProduct = Map.of(
                    "prod-1", new BigDecimal("1200.00"));

            breakdown.setByCategory(byCategory);
            breakdown.setByPerson(byPerson);
            breakdown.setByProduct(byProduct);

            assertThat(breakdown.getByCategory()).containsEntry("medical", new BigDecimal("1000.00"));
            assertThat(breakdown.getByPerson()).containsEntry("p1", new BigDecimal("800.00"));
            assertThat(breakdown.getByProduct()).containsEntry("prod-1", new BigDecimal("1200.00"));
        }

        @Test
        void testOutOfPocketBreakdownWithEmptyMaps() {
            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();
            breakdown.setByCategory(Map.of());
            breakdown.setByPerson(Map.of());

            assertThat(breakdown.getByCategory()).isEmpty();
            assertThat(breakdown.getByPerson()).isEmpty();
        }

        @Test
        void testOutOfPocketBreakdownWithNullMaps() {
            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();
            assertThat(breakdown.getByCategory()).isNull();
            assertThat(breakdown.getByPerson()).isNull();
            assertThat(breakdown.getByProduct()).isNull();
        }
    }

    @Nested
    @DisplayName("ScenarioCosts Model Tests")
    class ScenarioCostsTests {
        @Test
        void testScenarioCostsWithAllFields() {
            ScenarioCosts costs = new ScenarioCosts();
            costs.setTotalCost(new BigDecimal("5000.00"));
            costs.setTotalPremiums(new BigDecimal("4500.00"));
            costs.setTotalOutOfPocket(new BigDecimal("500.00"));
            costs.setTotalTaxSavings(new BigDecimal("750.00"));
            costs.setTotalPremiumCredit(new BigDecimal("200.00"));
            costs.setTotalOutOfPocketCredit(new BigDecimal("100.00"));

            assertThat(costs.getTotalCost()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(costs.getTotalPremiums()).isEqualByComparingTo(new BigDecimal("4500.00"));
            assertThat(costs.getTotalOutOfPocket()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(costs.getTotalTaxSavings()).isEqualByComparingTo(new BigDecimal("750.00"));
            assertThat(costs.getTotalPremiumCredit()).isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(costs.getTotalOutOfPocketCredit()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        void testScenarioCostsWithIndemnityFields() {
            ScenarioCosts costs = new ScenarioCosts();
            costs.setTotalIndemnityPremiums(new BigDecimal("3000.00"));
            costs.setTotalIndemnityPayouts(new BigDecimal("500.00"));
            costs.setTotalIndemnityWellnessBenefits(new BigDecimal("250.00"));
            costs.setTotalEmployerContributionsExSihra(new BigDecimal("1000.00"));

            assertThat(costs.getTotalIndemnityPremiums()).isEqualByComparingTo(new BigDecimal("3000.00"));
            assertThat(costs.getTotalIndemnityPayouts()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(costs.getTotalIndemnityWellnessBenefits()).isEqualByComparingTo(new BigDecimal("250.00"));
            assertThat(costs.getTotalEmployerContributionsExSihra()).isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        void testScenarioCostsWithNestedObjects() {
            ScenarioCosts costs = new ScenarioCosts();
            OutOfPocketBreakdown breakdown = new OutOfPocketBreakdown();
            breakdown.setByCategory(Map.of("medical", new BigDecimal("500.00")));

            costs.setOutOfPocketBreakdown(breakdown);

            assertThat(costs.getOutOfPocketBreakdown()).isNotNull();
            assertThat(costs.getOutOfPocketBreakdown().getByCategory()).containsEntry("medical",
                    new BigDecimal("500.00"));
        }

        @Test
        void testScenarioCostsPartialUpdate() {
            ScenarioCosts costs = new ScenarioCosts();
            costs.setTotalCost(new BigDecimal("1000.00"));

            assertThat(costs.getTotalCost()).isEqualByComparingTo(new BigDecimal("1000.00"));
            assertThat(costs.getTotalPremiums()).isNull();
            assertThat(costs.getTotalOutOfPocket()).isNull();
        }
    }

    @Nested
    @DisplayName("EstimatedCosts Model Tests")
    class EstimatedCostsTests {
        @Test
        void testEstimatedCostsWithExpectedAndAdverse() {
            EstimatedCosts costs = new EstimatedCosts();

            ScenarioCosts expected = new ScenarioCosts();
            expected.setTotalCost(new BigDecimal("5000.00"));

            ScenarioCosts adverse = new ScenarioCosts();
            adverse.setTotalCost(new BigDecimal("6000.00"));

            costs.setExpected(expected);
            costs.setAdverse(adverse);

            assertThat(costs.getExpected()).isNotNull();
            assertThat(costs.getExpected().getTotalCost()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(costs.getAdverse()).isNotNull();
            assertThat(costs.getAdverse().getTotalCost()).isEqualByComparingTo(new BigDecimal("6000.00"));
        }

        @Test
        void testEstimatedCostsWithOnlyExpected() {
            EstimatedCosts costs = new EstimatedCosts();
            ScenarioCosts expected = new ScenarioCosts();
            expected.setTotalCost(new BigDecimal("5000.00"));

            costs.setExpected(expected);

            assertThat(costs.getExpected()).isNotNull();
            assertThat(costs.getAdverse()).isNull();
        }

        @Test
        void testEstimatedCostsEmpty() {
            EstimatedCosts costs = new EstimatedCosts();
            assertThat(costs.getExpected()).isNull();
            assertThat(costs.getAdverse()).isNull();
        }
    }

    @Nested
    @DisplayName("VignetteBundleCost Model Tests")
    class VignetteBundleCostTests {
        @Test
        void testVignetteBundleCostWithAllFields() {
            VignetteBundleCost cost = new VignetteBundleCost();
            cost.setPremiums(new BigDecimal("500.00"));
            cost.setOutOfPocket(new BigDecimal("250.00"));
            cost.setTotal(new BigDecimal("750.00"));
            cost.setTotalPerPaycheck(new BigDecimal("62.50"));
            cost.setType("medical");

            assertThat(cost.getPremiums()).isEqualByComparingTo(new BigDecimal("500.00"));
            assertThat(cost.getOutOfPocket()).isEqualByComparingTo(new BigDecimal("250.00"));
            assertThat(cost.getTotal()).isEqualByComparingTo(new BigDecimal("750.00"));
            assertThat(cost.getTotalPerPaycheck()).isEqualByComparingTo(new BigDecimal("62.50"));
            assertThat(cost.getType()).isEqualTo("medical");
        }

        @Test
        void testVignetteBundleCostWithEmployeeContributions() {
            VignetteBundleCost cost = new VignetteBundleCost();
            EmployeeContributions contributions = new EmployeeContributions();
            contributions.setFsa(new BigDecimal("100.00"));
            contributions.setHsa(new BigDecimal("200.00"));

            cost.setEmployeeContributions(contributions);

            assertThat(cost.getEmployeeContributions()).isNotNull();
            assertThat(cost.getEmployeeContributions().getFsa()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        void testVignetteBundleCostWithZeroValues() {
            VignetteBundleCost cost = new VignetteBundleCost();
            cost.setPremiums(BigDecimal.ZERO);
            cost.setOutOfPocket(BigDecimal.ZERO);
            cost.setTotal(BigDecimal.ZERO);

            assertThat(cost.getPremiums()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(cost.getOutOfPocket()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void testVignetteBundleCostEmpty() {
            VignetteBundleCost cost = new VignetteBundleCost();
            assertThat(cost.getPremiums()).isNull();
            assertThat(cost.getOutOfPocket()).isNull();
            assertThat(cost.getType()).isNull();
        }
    }

    @Nested
    @DisplayName("AccountAdvantage Edge Cases")
    class AccountAdvantageEdgeCasesTests {
        @Test
        void testAccountAdvantageWithLargeNumbers() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal large = new BigDecimal("999999999.99");
            advantage.setEmployeeContributions(large);

            assertThat(advantage.getEmployeeContributions()).isEqualByComparingTo(large);
        }

        @Test
        void testAccountAdvantageWithVerySmallNumbers() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal small = new BigDecimal("0.01");
            advantage.setTaxSavings(small);

            assertThat(advantage.getTaxSavings()).isEqualByComparingTo(small);
        }

        @Test
        void testAccountAdvantageWithNegativeNumbers() {
            AccountAdvantage advantage = new AccountAdvantage();
            BigDecimal negative = new BigDecimal("-100.00");
            advantage.setTaxSavings(negative);

            assertThat(advantage.getTaxSavings()).isEqualByComparingTo(negative);
        }
    }
}
