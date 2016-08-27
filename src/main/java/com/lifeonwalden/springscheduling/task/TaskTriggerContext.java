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
package com.lifeonwalden.springscheduling.task;

import java.util.Date;

import org.springframework.scheduling.TriggerContext;

import com.lifeonwalden.springscheduling.BaseTrigger;

public class TaskTriggerContext implements TriggerContext {

  private Date lastScheduledExecutionTime;

  private Date lastActualExecutionTime;

  private Date lastCompletionTime;

  private BaseTrigger trigger;

  public TaskTriggerContext(BaseTrigger trigger) {
    this.trigger = trigger;
  }

  /**
   * Update this holder's state with the latest time values.
   * @param lastScheduledExecutionTime last <i>scheduled</i> execution time
   * @param lastActualExecutionTime last <i>actual</i> execution time
   * @param lastCompletionTime last completion time
   */
  public void update(Date lastScheduledExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
      this.lastScheduledExecutionTime = lastScheduledExecutionTime;
      this.lastActualExecutionTime = lastActualExecutionTime;
      this.lastCompletionTime = lastCompletionTime;
  }
  
  public TaskTriggerContext setLastScheduledExecutionTime(Date lastScheduledExecutionTime) {
    this.lastScheduledExecutionTime = lastScheduledExecutionTime;

    return this;
  }

  public TaskTriggerContext setLastActualExecutionTime(Date lastActualExecutionTime) {
    this.lastActualExecutionTime = lastActualExecutionTime;

    return this;
  }

  public TaskTriggerContext setLastCompletionTime(Date lastCompletionTime) {
    this.lastCompletionTime = lastCompletionTime;

    return this;
  }

  public BaseTrigger getTrigger() {
    return trigger;
  }

  @Override
  public Date lastScheduledExecutionTime() {
    return this.lastScheduledExecutionTime;
  }

  @Override
  public Date lastActualExecutionTime() {
    return this.lastActualExecutionTime;
  }

  @Override
  public Date lastCompletionTime() {
    return this.lastCompletionTime;
  }

}
