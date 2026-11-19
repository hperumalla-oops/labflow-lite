package com.labflow.labflow_lite.audit;

import com.labflow.labflow_lite.results.ResultStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resultId;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    private ResultStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus toStatus;

    @Column(nullable = false)
    private Instant createdAt;

    protected AuditLog() {}

    public AuditLog(Long resultId, Long actorId, String action,
                    ResultStatus fromStatus, ResultStatus toStatus) {
        this.resultId = resultId;
        this.actorId = actorId;
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getResultId() { return resultId; }
    public Long getActorId() { return actorId; }
    public String getAction() { return action; }
    public ResultStatus getFromStatus() { return fromStatus; }
    public ResultStatus getToStatus() { return toStatus; }
    public Instant getCreatedAt() { return createdAt; }
}