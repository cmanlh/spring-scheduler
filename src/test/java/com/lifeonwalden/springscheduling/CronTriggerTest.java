package com.lifeonwalden.springscheduling;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

public class CronTriggerTest {
  @Test
  public void test() {
    CronTrigger trigger = new CronTrigger("id", "name", "0 15 12 * * ?");
    Date nextExecutionTime = trigger.nextExecutionTime(new TriggerContext() {

      @Override
      public Date lastScheduledExecutionTime() {
        return new Date(Instant.now().minus(5, ChronoUnit.DAYS).toEpochMilli());
      }

      @Override
      public Date lastCompletionTime() {
        return null;
      }

      @Override
      public Date lastActualExecutionTime() {
        return new Date(Instant.now().toEpochMilli());
      }
    });

    System.out.println(nextExecutionTime);
  }
}
