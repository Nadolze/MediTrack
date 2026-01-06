CREATE TABLE IF NOT EXISTS mt_user (
                                       id            VARCHAR(36)   NOT NULL,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(50),
    PRIMARY KEY (id)
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_user_email ON mt_user(email);

CREATE TABLE IF NOT EXISTS mt_patient (
                                          id         VARCHAR(36)     NOT NULL,
    user_id    VARCHAR(36)     NOT NULL,
    first_name VARCHAR(100)    NOT NULL,
    last_name  VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id)
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_patient_user ON mt_patient(user_id);

CREATE TABLE IF NOT EXISTS mt_medical_staff (
                                                id           VARCHAR(36)     NOT NULL,
    user_id      VARCHAR(36)     NOT NULL,
    display_name VARCHAR(100)    NOT NULL,
    first_name   VARCHAR(100)    NOT NULL DEFAULT '',
    last_name    VARCHAR(100)    NOT NULL DEFAULT '',
    PRIMARY KEY (id)
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_staff_user ON mt_medical_staff(user_id);
