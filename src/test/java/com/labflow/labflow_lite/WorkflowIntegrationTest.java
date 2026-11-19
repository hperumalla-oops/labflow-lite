package com.labflow.labflow_lite;

import com.labflow.labflow_lite.audit.AuditService;
import com.labflow.labflow_lite.auth.AuthUser;
import com.labflow.labflow_lite.auth.UserRepository;
import com.labflow.labflow_lite.common.IllegalTransitionException;
import com.labflow.labflow_lite.common.MakerCheckerException;
import com.labflow.labflow_lite.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.access.AccessDeniedException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class WorkflowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired LabResultService service;
    @Autowired AuditService audit;
    @Autowired UserRepository users;

    AuthUser tech, rev1, rev2;

    @BeforeEach
    void loadUsers() {
        tech = actor("tech1");
        rev1 = actor("reviewer1");
        rev2 = actor("reviewer2");
    }

    private AuthUser actor(String name) {
        var u = users.findByUsername(name).orElseThrow();
        return new AuthUser(u.getId(), u.getUsername(), u.getRole());
    }

    private LabResult newSubmitted(AuthUser creator) {
        LabResult r = service.create(new CreateResultRequest("P-1", "HB", "13.5"), creator);
        return service.transition(r.getId(), TransitionAction.SUBMIT, creator);
    }

    @Test
    void happyPath_createSubmitApprove_writesAuditTrail() {
        LabResult r = newSubmitted(tech);
        LabResult approved = service.transition(r.getId(), TransitionAction.APPROVE, rev1);

        assertThat(approved.getStatus()).isEqualTo(ResultStatus.APPROVED);
        assertThat(audit.history(r.getId())).hasSize(3); // CREATE, SUBMIT, APPROVE
    }

    @Test
    void illegalTransition_approveDraft_isRejected() {
        LabResult r = service.create(new CreateResultRequest("P-2", "WBC", "7.1"), tech);

        assertThatThrownBy(() -> service.transition(r.getId(), TransitionAction.APPROVE, rev1))
                .isInstanceOf(IllegalTransitionException.class);
    }

    @Test
    void technician_cannotApprove() {
        LabResult r = newSubmitted(tech);

        assertThatThrownBy(() -> service.transition(r.getId(), TransitionAction.APPROVE, tech))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void makerChecker_reviewerCannotApproveOwnResult() {
        LabResult r = newSubmitted(rev1);

        assertThatThrownBy(() -> service.transition(r.getId(), TransitionAction.APPROVE, rev1))
                .isInstanceOf(MakerCheckerException.class);
        // different reviewer is fine
        assertThat(service.transition(r.getId(), TransitionAction.APPROVE, rev2).getStatus())
                .isEqualTo(ResultStatus.APPROVED);
    }

    @Test
    void concurrentApprovals_exactlyOneSucceeds() throws Exception {
        LabResult r = newSubmitted(tech);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Boolean> a = () -> attempt(start, r.getId(), rev1);
        Callable<Boolean> b = () -> attempt(start, r.getId(), rev2);
        Future<Boolean> fa = pool.submit(a);
        Future<Boolean> fb = pool.submit(b);
        start.countDown();

        List<Boolean> outcomes = List.of(fa.get(10, TimeUnit.SECONDS), fb.get(10, TimeUnit.SECONDS));
        pool.shutdown();

        assertThat(outcomes.stream().filter(ok -> ok).count()).isEqualTo(1);
        assertThat(service.get(r.getId()).getStatus()).isEqualTo(ResultStatus.APPROVED);
        // one APPROVE audit row only: loser rolled back, no ghost audit
        long approveRows = audit.history(r.getId()).stream()
                .filter(x -> x.getAction().equals("APPROVE")).count();
        assertThat(approveRows).isEqualTo(1);
    }

    private boolean attempt(CountDownLatch start, Long id, AuthUser who) throws InterruptedException {
        start.await();
        try {
            service.transition(id, TransitionAction.APPROVE, who);
            return true;
        } catch (Exception e) { // optimistic lock failure OR illegal transition
            return false;
        }
    }
}