package com.alight.healthbundle.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Model package basic POJO tests")
class ModelPackageTest {

    @Test
    void testPersonBuilderAndAccessors() {
        Person p = Person.builder()
                .id("p1")
                .birthdate("1980-01-01")
                .relationship("self")
                .healthcare(Map.of("plan", "A"))
                .build();

        assertThat(p.getId()).isEqualTo("p1");
        assertThat(p.getBirthdate()).isEqualTo("1980-01-01");
        assertThat(p.getRelationship()).isEqualTo("self");
        assertThat(p.getHealthcare()).containsEntry("plan", "A");
    }

    @Test
    void testSubscriberAndProductAndSavviRequest() {
        Subscriber s = Subscriber.builder()
                .id("s1")
                .birthdate("1975-05-05")
                .annualPay(new BigDecimal("75000"))
                .taxState("CA")
                .payPeriodsPlanyear(26)
                .payPeriodsRemaining(10)
                .build();

        Product prod = Product.builder()
                .type("medical")
                .productId("prod-1")
                .planIds(List.of("planA"))
                .isHdhp(Boolean.TRUE)
                .build();

        SAVVIRequest req = SAVVIRequest.builder()
                .planYearStartDate("2025-01-01")
                .planYearEndDate("2025-12-31")
                .enrollmentMode("open")
                .effectiveDate("2025-01-01")
                .subscriber(s)
                .coverablePeople(
                        List.of(Person.builder().id("p1").birthdate("2000-01-01").relationship("child").build()))
                .products(List.of(prod))
                .build();

        assertThat(req.getSubscriber()).isEqualTo(s);
        assertThat(req.getProducts()).hasSize(1);
        assertThat(req.getCoverablePeople()).hasSize(1);
        assertThat(req.getPlanYearStartDate()).isEqualTo("2025-01-01");
    }

    @Test
    void testBundleSelectionSetters() {
        BundleSelection sel = new BundleSelection();
        sel.setEvaluationId("eval-1");
        sel.setBusinessProcessReferenceId("bpr-1");
        sel.setClientId("client-1");
        sel.setPlatformInternalId("platform-1");
        sel.setFeaturedAs("customized");
        sel.setPlanYearBeginDate("2025-01-01");

        assertThat(sel.getEvaluationId()).isEqualTo("eval-1");
        assertThat(sel.getBusinessProcessReferenceId()).isEqualTo("bpr-1");
        assertThat(sel.getClientId()).isEqualTo("client-1");
        assertThat(sel.getPlatformInternalId()).isEqualTo("platform-1");
        assertThat(sel.getPlanYearBeginDate()).isEqualTo("2025-01-01");
    }

    @Test
    void testBundleAndEstimatedCosts() {
        ScenarioCosts sc = new ScenarioCosts();
        sc.setTotalCost(new BigDecimal("1234.56"));

        EstimatedCosts ec = new EstimatedCosts();
        ec.setExpected(sc);

        Bundle b = new Bundle();
        b.setBundleRank(1);
        b.setEstimatedCosts(ec);

        assertThat(b.getBundleRank()).isEqualTo(1);
        assertThat(b.getEstimatedCosts()).isNotNull();
        assertThat(b.getEstimatedCosts().getExpected().getTotalCost()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    void testErrorResponseDto() {
        ErrorResponseDTO dto = new ErrorResponseDTO(400, "Bad request");
        ErrorResponseDTO dto2 = new ErrorResponseDTO("Only message");

        assertThat(dto.getStatusCode()).isEqualTo(400);
        assertThat(dto.getErrorMessage()).isEqualTo("Bad request");
        assertThat(dto2.getErrorMessage()).isEqualTo("Only message");
    }

    @Test
    void testEvaluationInputDates() {
        EvaluationInputRequest r = new EvaluationInputRequest();
        LocalDateTime now = LocalDateTime.now();
        r.setCreatedAt(now);
        r.setUpdatedAt(now.plusDays(1));

        assertThat(r.getCreatedAt()).isEqualTo(now);
        assertThat(r.getUpdatedAt()).isEqualTo(now.plusDays(1));
    }
}
