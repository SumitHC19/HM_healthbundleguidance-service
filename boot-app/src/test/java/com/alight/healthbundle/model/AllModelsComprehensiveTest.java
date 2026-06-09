package com.alight.healthbundle.model;

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

@DisplayName("Comprehensive Model Tests - Builders and POJOs")
class AllModelsComprehensiveTest {

    @Nested
    @DisplayName("Person Model Tests")
    class PersonTests {
        @Test
        void testPersonBuilder() {
            Person person = Person.builder()
                    .id("p1")
                    .birthdate("1980-01-01")
                    .relationship("self")
                    .healthcare(Map.of("plan", "Medical"))
                    .build();

            assertThat(person).isNotNull();
            assertThat(person.getId()).isEqualTo("p1");
            assertThat(person.getBirthdate()).isEqualTo("1980-01-01");
            assertThat(person.getRelationship()).isEqualTo("self");
            assertThat(person.getHealthcare()).containsEntry("plan", "Medical");
        }

        @Test
        void testPersonBuilderWithAllFields() {
            Map<String, Object> healthcare = new HashMap<>();
            healthcare.put("plan", "Premium");
            healthcare.put("coverage", "Family");

            Person person = Person.builder()
                    .id("p2")
                    .birthdate("1985-06-15")
                    .relationship("spouse")
                    .healthcare(healthcare)
                    .build();

            assertThat(person.getId()).isEqualTo("p2");
            assertThat(person.getBirthdate()).isEqualTo("1985-06-15");
            assertThat(person.getRelationship()).isEqualTo("spouse");
            assertThat(person.getHealthcare()).hasSize(2);
        }

        @Test
        void testPersonBuilderWithNullHealthcare() {
            Person person = Person.builder()
                    .id("p3")
                    .birthdate("2000-01-01")
                    .relationship("child")
                    .healthcare(null)
                    .build();

            assertThat(person.getId()).isEqualTo("p3");
            assertThat(person.getHealthcare()).isNull();
        }

        @Test
        void testPersonNoArgsConstructor() {
            Person person = new Person();
            assertThat(person).isNotNull();
            assertThat(person.getId()).isNull();
        }

        @Test
        void testPersonEquality() {
            Person p1 = Person.builder().id("p1").birthdate("1980-01-01").relationship("self").build();
            Person p2 = Person.builder().id("p1").birthdate("1980-01-01").relationship("self").build();

            assertThat(p1).isEqualTo(p2);
        }

        @Test
        void testPersonInequality() {
            Person p1 = Person.builder().id("p1").birthdate("1980-01-01").relationship("self").build();
            Person p2 = Person.builder().id("p2").birthdate("1980-01-01").relationship("self").build();

            assertThat(p1).isNotEqualTo(p2);
        }
    }

    @Nested
    @DisplayName("Subscriber Model Tests")
    class SubscriberTests {
        @Test
        void testSubscriberBuilder() {
            Subscriber subscriber = Subscriber.builder()
                    .id("sub1")
                    .birthdate("1975-01-01")
                    .annualPay(new BigDecimal("75000.00"))
                    .taxState("CA")
                    .payPeriodsPlanyear(26)
                    .payPeriodsRemaining(10)
                    .build();

            assertThat(subscriber).isNotNull();
            assertThat(subscriber.getId()).isEqualTo("sub1");
            assertThat(subscriber.getBirthdate()).isEqualTo("1975-01-01");
            assertThat(subscriber.getAnnualPay()).isEqualByComparingTo(new BigDecimal("75000.00"));
            assertThat(subscriber.getTaxState()).isEqualTo("CA");
            assertThat(subscriber.getPayPeriodsPlanyear()).isEqualTo(26);
            assertThat(subscriber.getPayPeriodsRemaining()).isEqualTo(10);
        }

        @Test
        void testSubscriberWithZeroPayPeriods() {
            Subscriber subscriber = Subscriber.builder()
                    .id("sub2")
                    .birthdate("1980-05-15")
                    .annualPay(new BigDecimal("100000.00"))
                    .taxState("NY")
                    .payPeriodsPlanyear(0)
                    .payPeriodsRemaining(0)
                    .build();

            assertThat(subscriber.getPayPeriodsPlanyear()).isEqualTo(0);
            assertThat(subscriber.getPayPeriodsRemaining()).isEqualTo(0);
        }

        @Test
        void testSubscriberWithNullValues() {
            Subscriber subscriber = Subscriber.builder()
                    .id("sub3")
                    .annualPay(null)
                    .taxState(null)
                    .build();

            assertThat(subscriber.getAnnualPay()).isNull();
            assertThat(subscriber.getTaxState()).isNull();
        }

        @Test
        void testSubscriberEquality() {
            Subscriber s1 = Subscriber.builder().id("s1").birthdate("1975-01-01").build();
            Subscriber s2 = Subscriber.builder().id("s1").birthdate("1975-01-01").build();

            assertThat(s1).isEqualTo(s2);
        }
    }

    @Nested
    @DisplayName("Product Model Tests")
    class ProductTests {
        @Test
        void testProductBuilder() {
            List<String> planIds = List.of("plan-A", "plan-B");
            Map<String, Object> inNetwork = Map.of("coverage", "wide");

            Product product = Product.builder()
                    .type("medical")
                    .productId("prod-1")
                    .planIds(planIds)
                    .kind("PPO")
                    .isHdhp(true)
                    .isInvestable(false)
                    .inNetwork(inNetwork)
                    .build();

            assertThat(product).isNotNull();
            assertThat(product.getType()).isEqualTo("medical");
            assertThat(product.getProductId()).isEqualTo("prod-1");
            assertThat(product.getPlanIds()).hasSize(2).contains("plan-A", "plan-B");
            assertThat(product.getKind()).isEqualTo("PPO");
            assertThat(product.getIsHdhp()).isTrue();
            assertThat(product.getIsInvestable()).isFalse();
            assertThat(product.getInNetwork()).containsEntry("coverage", "wide");
        }

        @Test
        void testProductWithMultipleMaps() {
            Product product = Product.builder()
                    .type("dental")
                    .productId("prod-2")
                    .planIds(List.of("dental-1"))
                    .wellnessBenefit(Map.of("preventive", "covered"))
                    .services(Map.of("cleaning", "2x/year"))
                    .outofpocketLimits(Map.of("individual", "1000"))
                    .build();

            assertThat(product.getWellnessBenefit()).containsEntry("preventive", "covered");
            assertThat(product.getServices()).containsEntry("cleaning", "2x/year");
            assertThat(product.getOutofpocketLimits()).containsEntry("individual", "1000");
        }

        @Test
        void testProductWithNullValues() {
            Product product = Product.builder()
                    .type("vision")
                    .productId("prod-3")
                    .planIds(List.of("vision-1"))
                    .kind(null)
                    .isHdhp(null)
                    .build();

            assertThat(product.getKind()).isNull();
            assertThat(product.getIsHdhp()).isNull();
        }
    }

    @Nested
    @DisplayName("SAVVIRequest Model Tests")
    class SAVVIRequestTests {
        @Test
        void testSAVVIRequestBuilder() {
            Person person = Person.builder().id("p1").birthdate("2000-01-01").relationship("child").build();
            Product product = Product.builder().type("medical").productId("prod-1").planIds(List.of("plan-1")).build();
            Subscriber subscriber = Subscriber.builder().id("s1").birthdate("1975-01-01").build();

            SAVVIRequest request = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .planYearEndDate("2025-12-31")
                    .enrollmentMode("open")
                    .effectiveDate("2025-01-01")
                    .subscriber(subscriber)
                    .coverablePeople(List.of(person))
                    .products(List.of(product))
                    .build();

            assertThat(request).isNotNull();
            assertThat(request.getPlanYearStartDate()).isEqualTo("2025-01-01");
            assertThat(request.getPlanYearEndDate()).isEqualTo("2025-12-31");
            assertThat(request.getEnrollmentMode()).isEqualTo("open");
            assertThat(request.getEffectiveDate()).isEqualTo("2025-01-01");
            assertThat(request.getSubscriber()).isEqualTo(subscriber);
            assertThat(request.getCoverablePeople()).hasSize(1);
            assertThat(request.getProducts()).hasSize(1);
        }

        @Test
        void testSAVVIRequestWithMultiplePeopleAndProducts() {
            List<Person> people = List.of(
                    Person.builder().id("p1").birthdate("2000-01-01").relationship("child").build(),
                    Person.builder().id("p2").birthdate("2005-01-01").relationship("child").build());
            List<Product> products = List.of(
                    Product.builder().type("medical").productId("prod-1").planIds(List.of("plan-1")).build(),
                    Product.builder().type("dental").productId("prod-2").planIds(List.of("plan-2")).build());

            SAVVIRequest request = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .coverablePeople(people)
                    .products(products)
                    .build();

            assertThat(request.getCoverablePeople()).hasSize(2);
            assertThat(request.getProducts()).hasSize(2);
        }

        @Test
        void testSAVVIRequestWithEmptyLists() {
            SAVVIRequest request = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .coverablePeople(new ArrayList<>())
                    .products(new ArrayList<>())
                    .build();

            assertThat(request.getCoverablePeople()).isEmpty();
            assertThat(request.getProducts()).isEmpty();
        }
    }

    @Nested
    @DisplayName("EvaluationInputRequest Model Tests")
    class EvaluationInputRequestTests {
        @Test
        void testEvaluationInputRequestBuilder() {
            SAVVIRequest savviRequest = SAVVIRequest.builder()
                    .planYearStartDate("2025-01-01")
                    .planYearEndDate("2025-12-31")
                    .build();

            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .clientId("client-1")
                    .platformInternalId("platform-1")
                    .evaluationId("eval-1")
                    .businessProcessReferenceId("bpr-1")
                    .savviRequest(savviRequest)
                    .timestamp("2025-01-01T00:00:00Z")
                    .build();

            assertThat(request).isNotNull();
            assertThat(request.getClientId()).isEqualTo("client-1");
            assertThat(request.getPlatformInternalId()).isEqualTo("platform-1");
            assertThat(request.getEvaluationId()).isEqualTo("eval-1");
            assertThat(request.getBusinessProcessReferenceId()).isEqualTo("bpr-1");
            assertThat(request.getSavviRequest()).isEqualTo(savviRequest);
            assertThat(request.getTimestamp()).isEqualTo("2025-01-01T00:00:00Z");
        }

        @Test
        void testEvaluationInputRequestWithDates() {
            LocalDateTime now = LocalDateTime.now();

            EvaluationInputRequest request = EvaluationInputRequest.builder()
                    .businessProcessReferenceId("bpr-1")
                    .createdAt(now)
                    .updatedAt(now.plusDays(1))
                    .build();

            assertThat(request.getCreatedAt()).isEqualTo(now);
            assertThat(request.getUpdatedAt()).isEqualTo(now.plusDays(1));
        }

        @Test
        void testEvaluationInputRequestNoArgsConstructor() {
            EvaluationInputRequest request = new EvaluationInputRequest();
            assertThat(request).isNotNull();
            assertThat(request.getClientId()).isNull();
        }
    }

    @Nested
    @DisplayName("ErrorResponseDTO Model Tests")
    class ErrorResponseDTOTests {
        @Test
        void testErrorResponseDTOWithStatusCode() {
            ErrorResponseDTO dto = new ErrorResponseDTO(400, "Bad request");

            assertThat(dto).isNotNull();
            assertThat(dto.getStatusCode()).isEqualTo(400);
            assertThat(dto.getErrorMessage()).isEqualTo("Bad request");
        }

        @Test
        void testErrorResponseDTOWithOnlyMessage() {
            ErrorResponseDTO dto = new ErrorResponseDTO("Internal Server Error");

            assertThat(dto.getErrorMessage()).isEqualTo("Internal Server Error");
        }

        @Test
        void testErrorResponseDTOWith500Status() {
            ErrorResponseDTO dto = new ErrorResponseDTO(500, "Internal Server Error");

            assertThat(dto.getStatusCode()).isEqualTo(500);
        }

        @Test
        void testErrorResponseDTOWith404Status() {
            ErrorResponseDTO dto = new ErrorResponseDTO(404, "Not Found");

            assertThat(dto.getStatusCode()).isEqualTo(404);
            assertThat(dto.getErrorMessage()).isEqualTo("Not Found");
        }
    }
}
