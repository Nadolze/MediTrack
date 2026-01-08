CREATE TABLE IF NOT EXISTS mt_user (
                                       id            CHAR(36)      NOT NULL,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(50)   NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mt_user_email (email)
    );

CREATE TABLE IF NOT EXISTS mt_patient (
                                          id         CHAR(36)     NOT NULL,
    user_id    CHAR(36)     NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mt_patient_user (user_id),
    CONSTRAINT fk_mt_patient_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
    );

-- Wichtig: first_name/last_name DEFAULT '' verhindert deinen MySQL Fehler,
-- wenn du drop+recreate machst. (Wenn alte Tabelle existiert: drop+recreate nutzen)
CREATE TABLE IF NOT EXISTS mt_medical_staff (
                                                id           CHAR(36)     NOT NULL,
    user_id      CHAR(36)     NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL DEFAULT '',
    last_name    VARCHAR(100) NOT NULL DEFAULT '',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mt_staff_user (user_id),
    CONSTRAINT fk_mt_staff_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
    );
