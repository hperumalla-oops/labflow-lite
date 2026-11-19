package com.labflow.labflow_lite.results;

import com.labflow.labflow_lite.auth.AuthUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
public class LabResultController {

    private final LabResultService service;

    public LabResultController(LabResultService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('TECHNICIAN','REVIEWER')")
    public ResultResponse create(@Valid @RequestBody CreateResultRequest req,
                                 @AuthenticationPrincipal AuthUser actor) {
        return ResultResponse.from(service.create(req, actor));
    }

    @GetMapping
    public List<ResultResponse> list() {
        return service.list().stream().map(ResultResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResultResponse get(@PathVariable Long id) {
        return ResultResponse.from(service.get(id));
    }

    @PostMapping("/{id}/submit")
    public ResultResponse submit(@PathVariable Long id, @AuthenticationPrincipal AuthUser actor) {
        return ResultResponse.from(service.transition(id, TransitionAction.SUBMIT, actor));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('REVIEWER')")
    public ResultResponse approve(@PathVariable Long id, @AuthenticationPrincipal AuthUser actor) {
        return ResultResponse.from(service.transition(id, TransitionAction.APPROVE, actor));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('REVIEWER')")
    public ResultResponse reject(@PathVariable Long id, @AuthenticationPrincipal AuthUser actor) {
        return ResultResponse.from(service.transition(id, TransitionAction.REJECT, actor));
    }
}