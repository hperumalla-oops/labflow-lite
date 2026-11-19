package com.labflow.labflow_lite.results;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateResultRequest(
        @NotBlank @Size(max = 50) String patientRef,
        @NotBlank @Size(max = 30) String testCode,
        @NotBlank @Size(max = 100) String resultValue) {}