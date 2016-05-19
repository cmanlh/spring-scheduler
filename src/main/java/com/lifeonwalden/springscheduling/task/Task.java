package com.lifeonwalden.springscheduling.task;

import java.util.Date;
import java.util.Map;

import com.lifeonwalden.springscheduling.BaseTrigger;

public abstract class Task implements Runnable {
  protected String id;

  protected String name;

  protected String initParam;

  protected BaseTrigger trigger;

  protected Date lastExecutionTime;

  protected Date lastCompletionTime;

  protected TaskStatusEnum status = TaskStatusEnum.WAITING;

  public Task(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getInitParam() {
    return initParam;
  }

  public Task setInitParam(String initParam) {
    this.initParam = initParam;

    return this;
  }

  public BaseTrigger getTrigger() {
    return trigger;
  }

  public Task setTrigger(BaseTrigger trigger) {
    this.trigger = trigger;

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

  public Date getLastExecutionTime() {
    return lastExecutionTime;
  }

  public Date getLastCompletionTime() {
    return lastCompletionTime;
  }

  public abstract void run(Map<String, Object> param);

  @Override
  public void run() {
    run(null);
  }
}
