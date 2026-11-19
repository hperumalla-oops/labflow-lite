package com.labflow.labflow_lite.audit;

import com.labflow.labflow_lite.results.ResultStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) { this.repo = repo; }

    // MANDATORY = must join the caller's transaction. Called alone, it throws.
    // This enforces "audit row commits with the state change" in code.
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(Long resultId, Long actorId, String action,
                       ResultStatus from, ResultStatus to) {
        repo.save(new AuditLog(resultId, actorId, action, from, to));
    }

    @Transactional(readOnly = true)
    public List<AuditLog> history(Long resultId) {
        return repo.findByResultIdOrderByIdAsc(resultId);
    }
}