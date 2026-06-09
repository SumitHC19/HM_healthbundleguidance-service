package com.alight.healthbundle.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data class to hold extracted request context information.
 * Contains clientId and platformInternalId extracted from request headers and
 * tokens.
 */
@Data
@AllArgsConstructor
public class RequestContext {
    private String clientId;
    private String platformInternalId;
}
