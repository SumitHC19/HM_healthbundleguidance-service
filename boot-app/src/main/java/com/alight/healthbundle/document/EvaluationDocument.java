package com.alight.healthbundle.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.alight.healthbundle.model.EvaluationOutput;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB Document representing package guidance evaluation data.
 * Matches the OpenAPI 3.0.3 swagger specification schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bundleGuidance")
public class EvaluationDocument {

    @Id
    private String id;

    @Field("eventType")
    private String eventType;

    @Indexed(unique = true)
    @Field("evaluationId")
    private String evaluationId;

    @Field("evaluationType")
    private String evaluationType;

    @Field("evaluationStatus")
    private String evaluationStatus;

    @Field("evaluationOutput")
    private EvaluationOutput evaluationOutput;

    @Field("source")
    private String source;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    @Field("savedAt")
    private LocalDateTime savedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    @Field("documentStatus")
    private String documentStatus;
}
