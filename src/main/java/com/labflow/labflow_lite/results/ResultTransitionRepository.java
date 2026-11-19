package com.labflow.labflow_lite.results;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultTransitionRepository extends JpaRepository<ResultTransition, Long> {
    List<ResultTransition> findByActionAndFromStatus(TransitionAction action, ResultStatus fromStatus);
}