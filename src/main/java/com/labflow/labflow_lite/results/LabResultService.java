package com.labflow.labflow_lite.results;

import com.labflow.labflow_lite.audit.AuditService;
import com.labflow.labflow_lite.auth.AuthUser;
import com.labflow.labflow_lite.common.IllegalTransitionException;
import com.labflow.labflow_lite.common.MakerCheckerException;
import com.labflow.labflow_lite.common.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabResultService {

    private final LabResultRepository results;
    private final ResultTransitionRepository transitions;
    private final AuditService audit;

    public LabResultService(LabResultRepository results, ResultTransitionRepository transitions,
                            AuditService audit) {
        this.results = results;
        this.transitions = transitions;
        this.audit = audit;
    }

    @Transactional
    public LabResult create(CreateResultRequest req, AuthUser actor) {
        LabResult r = results.save(
                new LabResult(req.patientRef(), req.testCode(), req.resultValue(), actor.id()));
        audit.record(r.getId(), actor.id(), "CREATE", null, ResultStatus.DRAFT);
        return r;
    }

    @Transactional(readOnly = true)
    public List<LabResult> list() { return results.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public LabResult get(Long id) {
        return results.findById(id).orElseThrow(() -> new NotFoundException("Result " + id + " not found"));
    }

    // State change + audit row = ONE transaction. Both commit or both roll back.
    @Transactional
    public LabResult transition(Long id, TransitionAction action, AuthUser actor) {
        LabResult r = get(id);

        // 1. Is this move legal from the current state? (table lookup)
        List<ResultTransition> candidates = transitions.findByActionAndFromStatus(action, r.getStatus());
        if (candidates.isEmpty()) {
            throw new IllegalTransitionException(
                    "Cannot " + action + " a result in status " + r.getStatus());
        }

        // 2. Is this role allowed to make it? (table lookup)
        ResultTransition rule = candidates.stream()
                .filter(c -> c.getRequiredRole() == actor.role())
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException(
                        "Role " + actor.role() + " cannot " + action));

        // 3. Maker-checker: the person who made it cannot review it.
        if (action != TransitionAction.SUBMIT && r.getCreatedBy().equals(actor.id())) {
            throw new MakerCheckerException("You cannot " + action + " your own result");
        }

        ResultStatus from = r.getStatus();
        r.moveTo(rule.getToStatus());
        // saveAndFlush: stale @Version fails right here, not silently at commit.
        results.saveAndFlush(r);
        audit.record(r.getId(), actor.id(), action.name(), from, r.getStatus());
        return r;
    }
}