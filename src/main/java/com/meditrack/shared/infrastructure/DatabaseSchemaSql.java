package com.meditrack.shared.infrastructure;

import java.util.ArrayList;
import java.util.List;

public final class DatabaseSchemaSql {

    private DatabaseSchemaSql() {}

    public static List<String> schemaDdl(boolean mysql) {
        String char36 = mysql ? "CHAR(36)" : "VARCHAR(36)";

        List<String> ddl = new ArrayList<>();

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_user (
                id            %s       NOT NULL,
                name          VARCHAR(100) NOT NULL,
                email         VARCHAR(255) NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role          VARCHAR(50)  NULL,
                PRIMARY KEY (id),
                UNIQUE (email)
            )
            """.formatted(char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_patient (
                id         %s           NOT NULL,
                user_id    %s           NOT NULL,
                first_name VARCHAR(100) NOT NULL,
                last_name  VARCHAR(100) NOT NULL,
                PRIMARY KEY (id),
                UNIQUE (user_id),
                CONSTRAINT fk_mt_patient_user
                    FOREIGN KEY (user_id) REFERENCES mt_user(id)
            )
            """.formatted(char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_medical_staff (
                id           %s           NOT NULL,
                user_id      %s           NOT NULL,
                display_name VARCHAR(100) NOT NULL,
                first_name   VARCHAR(100) NOT NULL,
                last_name    VARCHAR(100) NOT NULL,
                PRIMARY KEY (id),
                UNIQUE (user_id),
                CONSTRAINT fk_mt_staff_user
                    FOREIGN KEY (user_id) REFERENCES mt_user(id)
            )
            """.formatted(char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_medication (
                id            %s           NOT NULL,
                name          VARCHAR(255) NOT NULL,
                description   TEXT         NULL,
                dosage_form   VARCHAR(50)  NULL,
                strength      VARCHAR(50)  NULL,
                PRIMARY KEY (id)
            )
            """.formatted(char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_medication_plan (
                id          %s           NOT NULL,
                patient_id  %s           NOT NULL,
                name        VARCHAR(255) NOT NULL,
                description VARCHAR(500) NULL,
                start_date  DATE         NULL,
                end_date    DATE         NULL,
                active      BOOLEAN      NOT NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_medication_plan_patient
                    FOREIGN KEY (patient_id) REFERENCES mt_patient(id)
            )
            """.formatted(char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_medication_plan_item (
                id              %s           NOT NULL,
                plan_id         %s           NOT NULL,
                medication_id   %s           NULL,
                medication_name VARCHAR(255) NOT NULL,
                dose            VARCHAR(50)  NULL,
                dose_unit       VARCHAR(20)  NULL,
                frequency       VARCHAR(50)  NULL,
                time_of_day     VARCHAR(50)  NULL,
                instructions    TEXT         NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_med_item_plan
                    FOREIGN KEY (plan_id) REFERENCES mt_medication_plan(id),
                CONSTRAINT fk_mt_med_item_medication
                    FOREIGN KEY (medication_id) REFERENCES mt_medication(id)
            )
            """.formatted(char36, char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_vital_reading (
                id                   %s          NOT NULL,
                patient_id           %s          NOT NULL,
                recorded_by_staff_id %s          NOT NULL,
                vital_type           VARCHAR(50)  NOT NULL,
                value                DOUBLE       NOT NULL,
                unit                 VARCHAR(20)  NULL,
                recorded_at          TIMESTAMP    NOT NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_vital_patient
                    FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
                CONSTRAINT fk_mt_vital_staff
                    FOREIGN KEY (recorded_by_staff_id) REFERENCES mt_medical_staff(id)
            )
            """.formatted(char36, char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_vital_threshold (
                id         %s          NOT NULL,
                patient_id %s          NOT NULL,
                vital_type VARCHAR(50)  NOT NULL,
                min_value  DOUBLE       NULL,
                max_value  DOUBLE       NULL,
                unit       VARCHAR(20)  NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_threshold_patient
                    FOREIGN KEY (patient_id) REFERENCES mt_patient(id)
            )
            """.formatted(char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_alert (
                id               %s          NOT NULL,
                patient_id       %s          NOT NULL,
                vital_reading_id %s          NULL,
                alert_type       VARCHAR(50) NOT NULL,
                message          VARCHAR(500) NULL,
                created_at       TIMESTAMP    NOT NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_alert_patient
                    FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
                CONSTRAINT fk_mt_alert_reading
                    FOREIGN KEY (vital_reading_id) REFERENCES mt_vital_reading(id)
            )
            """.formatted(char36, char36, char36).trim());

        ddl.add("""
            CREATE TABLE IF NOT EXISTS mt_notification (
                id         %s          NOT NULL,
                alert_id   %s          NOT NULL,
                channel    VARCHAR(30) NOT NULL,
                status     VARCHAR(30) NOT NULL,
                created_at TIMESTAMP   NOT NULL,
                PRIMARY KEY (id),
                CONSTRAINT fk_mt_notification_alert
                    FOREIGN KEY (alert_id) REFERENCES mt_alert(id)
            )
            """.formatted(char36, char36).trim());

        return ddl;
    }
}
