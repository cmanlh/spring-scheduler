/**
 * Copyright 2016 HongLu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
