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
