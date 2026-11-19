package com.labflow.labflow_lite.results;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lab_results")
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientRef;

    @Column(nullable = false)
    private String testCode;

    @Column(nullable = false)
    private String resultValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // Optimistic locking: Hibernate adds "WHERE version = ?" to every UPDATE.
    @Version
    private long version;

    protected LabResult() {}

    public LabResult(String patientRef, String testCode, String resultValue, Long createdBy) {
        this.patientRef = patientRef;
        this.testCode = testCode;
        this.resultValue = resultValue;
        this.createdBy = createdBy;
        this.status = ResultStatus.DRAFT;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void moveTo(ResultStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getPatientRef() { return patientRef; }
    public String getTestCode() { return testCode; }
    public String getResultValue() { return resultValue; }
    public ResultStatus getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}