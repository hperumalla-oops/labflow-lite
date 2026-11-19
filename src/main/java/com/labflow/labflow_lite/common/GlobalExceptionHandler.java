package com.labflow.labflow_lite.common;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Extends the base class so @Valid failures become 400 ProblemDetail automatically.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFound(NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalTransitionException.class)
    ProblemDetail illegalTransition(IllegalTransitionException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MakerCheckerException.class)
    ProblemDetail makerChecker(MakerCheckerException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail accessDenied(AccessDeniedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(BadCredentialsException.class)
    ProblemDetail badCredentials(BadCredentialsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ProblemDetail optimisticLock(OptimisticLockingFailureException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Result was modified by someone else. Reload and retry.");
    }
}