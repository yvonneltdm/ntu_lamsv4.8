-- Quartz 2.2.3

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS lams_qtz_blob_triggers;


CREATE TABLE lams_qtz_blob_triggers (
                                        SCHED_NAME varchar(120) NOT NULL,
                                        TRIGGER_NAME varchar(200) NOT NULL,
                                        TRIGGER_GROUP varchar(200) NOT NULL,
                                        BLOB_DATA blob,
                                        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                        KEY SCHED_NAME (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                        CONSTRAINT lams_qtz_BLOB_TRIGGERS_ibfk_1 FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES lams_qtz_triggers (sched_name, trigger_name, trigger_group)
);

DROP TABLE IF EXISTS lams_qtz_calendars;


CREATE TABLE lams_qtz_calendars (
                                    SCHED_NAME varchar(120) NOT NULL,
                                    CALENDAR_NAME varchar(200) NOT NULL,
                                    CALENDAR blob NOT NULL,
                                    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

DROP TABLE IF EXISTS lams_qtz_cron_triggers;


CREATE TABLE lams_qtz_cron_triggers (
                                        SCHED_NAME varchar(120) NOT NULL,
                                        TRIGGER_NAME varchar(200) NOT NULL,
                                        TRIGGER_GROUP varchar(200) NOT NULL,
                                        CRON_EXPRESSION varchar(120) NOT NULL,
                                        TIME_ZONE_ID varchar(80) DEFAULT NULL,
                                        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                        CONSTRAINT lams_qtz_CRON_TRIGGERS_ibfk_1 FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES lams_qtz_triggers (sched_name, trigger_name, trigger_group)
);

DROP TABLE IF EXISTS lams_qtz_fired_triggers;


CREATE TABLE lams_qtz_fired_triggers (
                                         SCHED_NAME varchar(120) NOT NULL,
                                         ENTRY_ID varchar(95) NOT NULL,
                                         TRIGGER_NAME varchar(200) NOT NULL,
                                         TRIGGER_GROUP varchar(200) NOT NULL,
                                         INSTANCE_NAME varchar(200) NOT NULL,
                                         FIRED_TIME bigint NOT NULL,
                                         SCHED_TIME bigint NOT NULL,
                                         PRIORITY int NOT NULL,
                                         STATE varchar(16) NOT NULL,
                                         JOB_NAME varchar(200) DEFAULT NULL,
                                         JOB_GROUP varchar(200) DEFAULT NULL,
                                         IS_NONCONCURRENT varchar(1) DEFAULT NULL,
                                         REQUESTS_RECOVERY varchar(1) DEFAULT NULL,
                                         PRIMARY KEY (SCHED_NAME,ENTRY_ID),
                                         KEY IDX_lams_qtz_FT_TRIG_INST_NAME (SCHED_NAME,INSTANCE_NAME),
                                         KEY IDX_lams_qtz_FT_INST_JOB_REQ_RCVRY (SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY),
                                         KEY IDX_lams_qtz_FT_J_G (SCHED_NAME,JOB_NAME,JOB_GROUP),
                                         KEY IDX_lams_qtz_FT_JG (SCHED_NAME,JOB_GROUP),
                                         KEY IDX_lams_qtz_FT_T_G (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                         KEY IDX_lams_qtz_FT_TG (SCHED_NAME,TRIGGER_GROUP)
);

DROP TABLE IF EXISTS lams_qtz_job_details;


CREATE TABLE lams_qtz_job_details (
                                      SCHED_NAME varchar(120) NOT NULL,
                                      JOB_NAME varchar(200) NOT NULL,
                                      JOB_GROUP varchar(200) NOT NULL,
                                      `DESCRIPTION` varchar(250) DEFAULT NULL,
                                      JOB_CLASS_NAME varchar(250) NOT NULL,
                                      IS_DURABLE varchar(1) NOT NULL,
                                      IS_NONCONCURRENT varchar(1) NOT NULL,
                                      IS_UPDATE_DATA varchar(1) NOT NULL,
                                      REQUESTS_RECOVERY varchar(1) NOT NULL,
                                      JOB_DATA blob,
                                      PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP),
                                      KEY IDX_lams_qtz_J_REQ_RECOVERY (SCHED_NAME,REQUESTS_RECOVERY),
                                      KEY IDX_lams_qtz_J_GRP (SCHED_NAME,JOB_GROUP)
);

DROP TABLE IF EXISTS lams_qtz_locks;


CREATE TABLE lams_qtz_locks (
                                SCHED_NAME varchar(120) NOT NULL,
                                LOCK_NAME varchar(40) NOT NULL,
                                PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

DROP TABLE IF EXISTS lams_qtz_paused_trigger_grps;


CREATE TABLE lams_qtz_paused_trigger_grps (
                                              SCHED_NAME varchar(120) NOT NULL,
                                              TRIGGER_GROUP varchar(200) NOT NULL,
                                              PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

DROP TABLE IF EXISTS lams_qtz_scheduler_state;


CREATE TABLE lams_qtz_scheduler_state (
                                          SCHED_NAME varchar(120) NOT NULL,
                                          INSTANCE_NAME varchar(200) NOT NULL,
                                          LAST_CHECKIN_TIME bigint NOT NULL,
                                          CHECKIN_INTERVAL bigint NOT NULL,
                                          PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

DROP TABLE IF EXISTS lams_qtz_simple_triggers;


CREATE TABLE lams_qtz_simple_triggers (
                                          SCHED_NAME varchar(120) NOT NULL,
                                          TRIGGER_NAME varchar(200) NOT NULL,
                                          TRIGGER_GROUP varchar(200) NOT NULL,
                                          REPEAT_COUNT bigint NOT NULL,
                                          REPEAT_INTERVAL bigint NOT NULL,
                                          TIMES_TRIGGERED bigint NOT NULL,
                                          PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                          CONSTRAINT lams_qtz_SIMPLE_TRIGGERS_ibfk_1 FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES lams_qtz_triggers (sched_name, trigger_name, trigger_group)
);

DROP TABLE IF EXISTS lams_qtz_simprop_triggers;


CREATE TABLE lams_qtz_simprop_triggers (
                                           SCHED_NAME varchar(120) NOT NULL,
                                           TRIGGER_NAME varchar(200) NOT NULL,
                                           TRIGGER_GROUP varchar(200) NOT NULL,
                                           STR_PROP_1 varchar(512) DEFAULT NULL,
                                           STR_PROP_2 varchar(512) DEFAULT NULL,
                                           STR_PROP_3 varchar(512) DEFAULT NULL,
                                           INT_PROP_1 int DEFAULT NULL,
                                           INT_PROP_2 int DEFAULT NULL,
                                           LONG_PROP_1 bigint DEFAULT NULL,
                                           LONG_PROP_2 bigint DEFAULT NULL,
                                           DEC_PROP_1 decimal(13,4) DEFAULT NULL,
                                           DEC_PROP_2 decimal(13,4) DEFAULT NULL,
                                           BOOL_PROP_1 varchar(1) DEFAULT NULL,
                                           BOOL_PROP_2 varchar(1) DEFAULT NULL,
                                           PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                           CONSTRAINT lams_qtz_SIMPROP_TRIGGERS_ibfk_1 FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES lams_qtz_triggers (sched_name, trigger_name, trigger_group)
);

DROP TABLE IF EXISTS lams_qtz_triggers;


CREATE TABLE lams_qtz_triggers (
                                   SCHED_NAME varchar(120) NOT NULL,
                                   TRIGGER_NAME varchar(200) NOT NULL,
                                   TRIGGER_GROUP varchar(200) NOT NULL,
                                   JOB_NAME varchar(200) NOT NULL,
                                   JOB_GROUP varchar(200) NOT NULL,
                                   `DESCRIPTION` varchar(250) DEFAULT NULL,
                                   NEXT_FIRE_TIME bigint DEFAULT NULL,
                                   PREV_FIRE_TIME bigint DEFAULT NULL,
                                   PRIORITY int DEFAULT NULL,
                                   TRIGGER_STATE varchar(16) NOT NULL,
                                   TRIGGER_TYPE varchar(8) NOT NULL,
                                   START_TIME bigint NOT NULL,
                                   END_TIME bigint DEFAULT NULL,
                                   CALENDAR_NAME varchar(200) DEFAULT NULL,
                                   MISFIRE_INSTR smallint DEFAULT NULL,
                                   JOB_DATA blob,
                                   PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                   KEY IDX_lams_qtz_T_J (SCHED_NAME,JOB_NAME,JOB_GROUP),
                                   KEY IDX_lams_qtz_T_JG (SCHED_NAME,JOB_GROUP),
                                   KEY IDX_lams_qtz_T_C (SCHED_NAME,CALENDAR_NAME),
                                   KEY IDX_lams_qtz_T_G (SCHED_NAME,TRIGGER_GROUP),
                                   KEY IDX_lams_qtz_T_STATE (SCHED_NAME,TRIGGER_STATE),
                                   KEY IDX_lams_qtz_T_N_STATE (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE),
                                   KEY IDX_lams_qtz_T_N_G_STATE (SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE),
                                   KEY IDX_lams_qtz_T_NEXT_FIRE_TIME (SCHED_NAME,NEXT_FIRE_TIME),
                                   KEY IDX_lams_qtz_T_NFT_ST (SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME),
                                   KEY IDX_lams_qtz_T_NFT_MISFIRE (SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME),
                                   KEY IDX_lams_qtz_T_NFT_ST_MISFIRE (SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE),
                                   KEY IDX_lams_qtz_T_NFT_ST_MISFIRE_GRP (SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE),
                                   CONSTRAINT lams_qtz_TRIGGERS_ibfk_1 FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP) REFERENCES lams_qtz_job_details (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

INSERT INTO lams_qtz_job_details VALUES ('LAMS','Resend Messages Job','DEFAULT',NULL,'org.lamsfoundation.lams.events.ResendMessagesJob','1','0','0','0',0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787000737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F40000000000010770800000010000000007800);

INSERT INTO lams_qtz_locks VALUES ('LAMS','TRIGGER_ACCESS');

INSERT INTO lams_qtz_simple_triggers VALUES ('LAMS','Resend Messages Job Trigger','DEFAULT',-1,3600000,0);

INSERT INTO lams_qtz_triggers VALUES ('LAMS','Resend Messages Job Trigger','DEFAULT','Resend Messages Job','DEFAULT',NULL,1663667031020,-1,0,'WAITING','SIMPLE',1663667031020,0,NULL,0,'');

SET FOREIGN_KEY_CHECKS=1;