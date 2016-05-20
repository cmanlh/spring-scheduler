package com.lifeonwalden.springscheduling.task;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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
      if (null != monitor) {
        monitor.notificate(new TaskEvent().setHappendTime(triggerContext.lastActualExecutionTime()).setTaskId(this.id)
            .setType(TaskEventType.START));
      }

      this.status = TaskStatusEnum.RUNNING;
      List<Throwable> failPrintList = doJob(null != param ? param : new WeakHashMap<String, Object>(this.param));
      this.status = TaskStatusEnum.COMPLETED;

      triggerContext.setLastCompletionTime(new Date());
      if (null != monitor) {
        monitor.notificate(new TaskEvent().setHappendTime(triggerContext.lastCompletionTime()).setTaskId(this.id)
            .setType(TaskEventType.COMPELETE).setFailPrintList(failPrintList));
      }
      triggerContext.setLastScheduledExecutionTime(getTrigger().nextExecutionTime(triggerContext));
    } catch (Throwable e) {
      this.status = TaskStatusEnum.FAILED;

      if (null != monitor) {
        monitor.notificate(new TaskEvent().setHappendTime(new Date()).setTaskId(this.id).setType(TaskEventType.FAIL)
            .setFailPrintList(Arrays.asList(e)));
      }
    }
  }

  public abstract List<Throwable> doJob(Map<String, Object> param);

  @Override
  public void run() {
    run(null);
  }
}
