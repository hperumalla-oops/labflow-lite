package com.labflow.labflow_lite.results;

import com.labflow.labflow_lite.auth.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "result_transitions")
public class ResultTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransitionAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role requiredRole;

    protected ResultTransition() {}

    public ResultStatus getToStatus() { return toStatus; }
    public Role getRequiredRole() { return requiredRole; }
}