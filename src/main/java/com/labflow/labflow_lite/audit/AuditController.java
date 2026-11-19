package com.labflow.labflow_lite.audit;

import com.labflow.labflow_lite.results.ResultStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
public class AuditController {

    public record AuditResponse(Long id, Long actorId, String action,
                                ResultStatus fromStatus, ResultStatus toStatus, Instant createdAt) {}

    private final AuditService audit;

    public AuditController(AuditService audit) { this.audit = audit; }

    @GetMapping("/results/{id}/audit")
    public List<AuditResponse> history(@PathVariable Long id) {
        return audit.history(id).stream()
                .map(a -> new AuditResponse(a.getId(), a.getActorId(), a.getAction(),
                        a.getFromStatus(), a.getToStatus(), a.getCreatedAt()))
                .toList();
    }
}