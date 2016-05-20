package com.lifeonwalden.springscheduling.monitor;

import java.util.Date;

public class TaskEvent {
  private TaskEventType type;

  private String taskId;

  private Throwable failReason;

  private Date happendTime;

  public TaskEventType getType() {
    return type;
  }

  public TaskEvent setType(TaskEventType type) {
    this.type = type;

    return this;
  }

  public String getTaskId() {
    return taskId;
  }

  public TaskEvent setTaskId(String taskId) {
    this.taskId = taskId;

    return this;
  }

  public Throwable getFailReason() {
    return failReason;
  }

  public TaskEvent setFailReason(Throwable failReason) {
    this.failReason = failReason;

    return this;
  }

  public Date getHappendTime() {
    return happendTime;
  }

  public TaskEvent setHappendTime(Date happendTime) {
    this.happendTime = happendTime;

    return this;
  }

}
