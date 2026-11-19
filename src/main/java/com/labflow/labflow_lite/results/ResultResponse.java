package com.labflow.labflow_lite.results;

import java.time.Instant;

public record ResultResponse(Long id, String patientRef, String testCode, String resultValue,
                             ResultStatus status, Long createdBy, Instant createdAt, Instant updatedAt) {

    public static ResultResponse from(LabResult r) {
        return new ResultResponse(r.getId(), r.getPatientRef(), r.getTestCode(), r.getResultValue(),
                r.getStatus(), r.getCreatedBy(), r.getCreatedAt(), r.getUpdatedAt());
    }
}