package com.lifeonwalden.springscheduling.monitor;

import java.util.Date;
import java.util.List;

public class TaskEvent {
  private TaskEventType type;

  private String taskId;

  private List<Throwable> failPrintList;

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

  public List<Throwable> getFailPrintList() {
    return failPrintList;
  }

  public TaskEvent setFailPrintList(List<Throwable> failPrintList) {
    this.failPrintList = failPrintList;

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
