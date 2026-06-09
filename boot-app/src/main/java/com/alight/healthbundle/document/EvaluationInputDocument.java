package com.alight.healthbundle.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MongoDB document for storing evaluation input data.
 * Stores SAVVI request data with subscriber, people, products, and offers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "evaluationInput")
public class EvaluationInputDocument {

    @Id
    private String id;

    @Field("clientId")
    private String clientId;

    @Field("platformInternalId")
    private String platformInternalId;

    @Field("evaluationId")
    private String evaluationId;

    @Field("businessProcessReferenceId")
    private String businessProcessReferenceId;

    @Field("savviRequest")
    private Map<String, Object> savviRequest;

    @Field("timestamp")
    private String timestamp;

    @Field("savedAt")
    private LocalDateTime savedAt;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("documentStatus")
    private String documentStatus;
}
