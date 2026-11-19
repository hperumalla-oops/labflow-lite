--liquibase formatted sql
--changeset labflow:1-users
CREATE TABLE users (
    id            BIGSERIAL     PRIMARY KEY,
    username      VARCHAR (50)  NOT NULL UNIQUE,
    password_hash VARCHAR (100) NOT NULL,
    role          VARCHAR (20)  NOT NULL
);

--changeset labflow:2-lab-results
CREATE TABLE lab_results (
    id           BIGSERIAL     PRIMARY KEY,
    patient_ref  VARCHAR (50)  NOT NULL,
    test_code    VARCHAR (30)  NOT NULL,
    result_value VARCHAR (100) NOT NULL,
    status       VARCHAR (20)  NOT NULL,
    created_by   BIGINT NOT NULL REFERENCES users(id),
    created_at   TIMESTAMPTZ   NOT NULL,
    updated_at   TIMESTAMPTZ   NOT NULL,
    version      BIGINT        DEFAULT 0 NOT NULL
);

--changeset labflow:3-result-transitions
CREATE TABLE result_transitions (
    id            BIGSERIAL    PRIMARY KEY,
    action        VARCHAR (20) NOT NULL,
    from_status   VARCHAR (20) NOT NULL,
    to_status     VARCHAR (20) NOT NULL,
    required_role VARCHAR (20) NOT NULL,
    UNIQUE (action, from_status, required_role)
);



INSERT  INTO result_transitions (
    action,
    from_status,
    to_status,
    required_role
)
VALUES                          ('SUBMIT', 'DRAFT', 'SUBMITTED', 'TECHNICIAN'),
                                ('SUBMIT', 'DRAFT', 'SUBMITTED', 'REVIEWER'),
                                ('APPROVE', 'SUBMITTED', 'APPROVED', 'REVIEWER'),
                                ('REJECT', 'SUBMITTED', 'REJECTED', 'REVIEWER');

--changeset labflow:4-audit-log
CREATE TABLE audit_log (
    id          BIGSERIAL    PRIMARY KEY,
    result_id   BIGINT      NOT NULL REFERENCES lab_results(id),
    actor_id    BIGINT      NOT NULL REFERENCES users(id),
    action      VARCHAR (20) NOT NULL,
    from_status VARCHAR (20),
    to_status   VARCHAR (20) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_audit_result
    ON audit_log(result_id);