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
package com.lifeonwalden.springscheduling.monitor;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskEvent {
  private TaskEventType type;

  private String taskId;

  private List<String> failPrintList;

  private Date startTime;

  private Date endTime;

  private Date happendTime;

  private Date nextExecutionTime;

  private Map<String, Object> param;

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

  public List<String> getFailPrintList() {
    return failPrintList;
  }

  public TaskEvent setFailPrintList(List<String> failPrintList) {
    this.failPrintList = failPrintList;

    return this;
  }

  public Date getStartTime() {
    return startTime;
  }

  public TaskEvent setStartTime(Date startTime) {
    this.startTime = startTime;

    return this;
  }

  public Date getEndTime() {
    return endTime;
  }

  public TaskEvent setEndTime(Date endTime) {
    this.endTime = endTime;

    return this;
  }

  public Date getHappendTime() {
    return happendTime;
  }

  public TaskEvent setHappendTime(Date happendTime) {
    this.happendTime = happendTime;

    return this;
  }

  public Date getNextExecutionTime() {
    return nextExecutionTime;
  }

  public TaskEvent setNextExecutionTime(Date nextExecutionTime) {
    this.nextExecutionTime = nextExecutionTime;

    return this;
  }

  public Map<String, Object> getParam() {
    return param;
  }

  public TaskEvent setParam(Map<String, Object> param) {
    this.param = param;

    return this;
  }

}
