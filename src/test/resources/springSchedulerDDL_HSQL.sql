CREATE table SchedulerCronTrigger (
  id VARCHAR(32) NOT NULL,
  name VARCHAR(256) NOT NULL,
  expression VARCHAR(256) NOT NULL,
  timeZone  VARCHAR(256),
  PRIMARY KEY (id)
 );
 
 CREATE table SchedulerPeriodicTrigger (
  id VARCHAR(32) NOT NULL,
  name VARCHAR(256) NOT NULL,
  period BIGINT NOT NULL,
  timeUnit SMALLINT,
  initialDelay BIGINT,
  fixedRate TINYINT,
  PRIMARY KEY (id)
 );