package com.lifeonwalden.springscheduling.task;

import java.util.Date;
import java.util.Map;

import com.lifeonwalden.springscheduling.BaseTrigger;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;

public abstract class Task implements Runnable {
  protected String id;

  protected String name;

  protected Map<String, Object> param;

  protected TaskTriggerContext triggerContext;

  protected Monitor monitor;

  protected TaskStatusEnum status = TaskStatusEnum.WAITING;

  public Task(String id, String name, TaskTriggerContext triggerContext) {
    this.id = id;
    this.name = name;
    this.triggerContext = triggerContext;
  }

  public Task(String id, String name, TaskTriggerContext triggerContext, Monitor monitor) {
    this.id = id;
    this.name = name;
    this.monitor = monitor;
    this.triggerContext = triggerContext;
  }

  public Map<String, Object> getParam() {
    return param;
  }

  public Task setParam(Map<String, Object> param) {
    this.param = param;

    return this;
  }

  public TaskStatusEnum getStatus() {
    return status;
  }

  public Task setStatus(TaskStatusEnum status) {
    this.status = status;

    return this;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public TaskTriggerContext getTriggerContext() {
    return triggerContext;
  }

  public BaseTrigger getTrigger() {
    return triggerContext.getTrigger();
  }

  public void run(Map<String, Object> param) {
    try {
      triggerContext.setLastActualExecutionTime(new Date());
      monitor.notificate(new TaskEvent().setHappendTime(triggerContext.lastActualExecutionTime()).setTaskId(this.id)
          .setType(TaskEventType.START));
      this.status = TaskStatusEnum.RUNNING;
      doJob(null != param ? param : this.param);
      triggerContext.setLastCompletionTime(new Date());
      monitor.notificate(new TaskEvent().setHappendTime(triggerContext.lastCompletionTime()).setTaskId(this.id)
          .setType(TaskEventType.COMPELETE));
      triggerContext.setLastScheduledExecutionTime(getTrigger().nextExecutionTime(triggerContext));
      this.status = TaskStatusEnum.COMPLETED;
    } catch (Throwable e) {
      monitor.notificate(
          new TaskEvent().setHappendTime(new Date()).setTaskId(this.id).setType(TaskEventType.FAIL).setFailReason(e));
      this.status = TaskStatusEnum.FAILED;
    }
  }

  public abstract void doJob(Map<String, Object> param);

  @Override
  public void run() {
    run(null);
  }
}
