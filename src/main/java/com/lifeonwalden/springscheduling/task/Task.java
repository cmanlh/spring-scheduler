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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.lifeonwalden.springscheduling.BaseTrigger;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;

public abstract class Task implements Runnable, ScheduledFuture<Object> {
  protected String id;

  protected String name;

  protected Map<String, Object> param;

  protected TaskTriggerContext triggerContext;

  protected Monitor monitor;

  protected boolean canRetry = false;

  /** always retry from the beginning of worker list */
  protected boolean alwaysFromBeginning = false;

  /** retry after 30 Minutes */
  protected long retryAfter = 1800;

  protected ScheduledExecutorService executor;

  protected TaskStatusEnum status = TaskStatusEnum.WAITING;

  private final Object triggerContextMonitor = new Object();

  private ScheduledFuture<?> currentFuture;

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

  public boolean isCanRetry() {
    return canRetry;
  }

  public boolean isAlwaysFromBeginning() {
    return alwaysFromBeginning;
  }

  public void setAlwaysFromBeginning(boolean alwaysFromBeginning) {
    this.alwaysFromBeginning = alwaysFromBeginning;
  }

  public void setCanRetry(boolean canRetry) {
    this.canRetry = canRetry;
  }

  public long getRetryAfter() {
    return retryAfter;
  }

  /**
   * retry after {retryAfter} seconds
   * 
   * @param retryAfter
   */
  public void setRetryAfter(long retryAfter) {
    this.retryAfter = retryAfter;
  }

  public ScheduledExecutorService getExecutor() {
    return executor;
  }

  public void setExecutor(ScheduledExecutorService executor) {
    this.executor = executor;
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

  public ScheduledFuture<?> schedule() {
    synchronized (this.triggerContextMonitor) {
      Date scheduledExecutionTime = this.triggerContext.getTrigger().nextExecutionTime(this.triggerContext);
      if (scheduledExecutionTime == null) {
        return null;
      }
      long initialDelay = scheduledExecutionTime.getTime() - System.currentTimeMillis();
      this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
      return this;
    }
  }

  private void _run(Map<String, Object> param) {
    Date nextExecutionTime = null, actualExecutionTime = null, completionTime = null;
    List<String> failPrintList = null;
    TaskEvent startTaskEvent = new TaskEvent();
    try {
      Map<String, Object> _param = null != param ? param
          : (null == this.param ? new WeakHashMap<String, Object>() : new WeakHashMap<String, Object>(this.param));
      actualExecutionTime = new Date();
      if (null != monitor) {
        monitor.notificate(startTaskEvent.setHappendTime(actualExecutionTime).setTaskId(this.id)
            .setType(TaskEventType.START).setParam(_param));
      }

      this.status = TaskStatusEnum.RUNNING;
      failPrintList = doJob(_param);
      this.status = TaskStatusEnum.COMPLETED;
    } catch (Throwable e) {
      this.status = TaskStatusEnum.FAILED;

      failPrintList = Arrays.asList(e.getMessage());
    }
    completionTime = new Date();
    nextExecutionTime = getTrigger().nextExecutionTime(triggerContext);

    if (null != failPrintList && this.canRetry) {
      nextExecutionTime = Date.from(
          LocalDateTime.now().plus(this.retryAfter, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toInstant());
    }
    if (null != monitor) {
      monitor.notificate(new TaskEvent().setHappendTime(new Date()).setTaskId(this.id)
          .setType(TaskStatusEnum.COMPLETED == this.status ? TaskEventType.COMPELETE : TaskEventType.FAIL)
          .setFailPrintList(failPrintList).setNextExecutionTime(nextExecutionTime)
          .setStartTime(startTaskEvent.getHappendTime()).setParam(startTaskEvent.getParam()));
    }

    synchronized (this.triggerContextMonitor) {
      this.triggerContext.update(nextExecutionTime, actualExecutionTime, completionTime);
      if (!this.currentFuture.isCancelled()) {
        schedule();
      }
    }
  }

  public abstract List<String> doJob(Map<String, Object> param);

  @Override
  public void run() {
    _run(null);
  }


  @Override
  public long getDelay(TimeUnit unit) {
    ScheduledFuture<?> curr;
    synchronized (this.triggerContextMonitor) {
      curr = this.currentFuture;
    }
    return curr.getDelay(unit);
  }

  @Override
  public int compareTo(Delayed other) {
    if (this == other) {
      return 0;
    }
    long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
    return (diff == 0 ? 0 : ((diff < 0) ? -1 : 1));
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    synchronized (this.triggerContextMonitor) {
      return this.currentFuture.cancel(mayInterruptIfRunning);
    }
  }

  @Override
  public boolean isCancelled() {
    synchronized (this.triggerContextMonitor) {
      return this.currentFuture.isCancelled();
    }
  }

  @Override
  public boolean isDone() {
    synchronized (this.triggerContextMonitor) {
      return this.currentFuture.isDone();
    }
  }

  @Override
  public Object get() throws InterruptedException, ExecutionException {
    ScheduledFuture<?> curr;
    synchronized (this.triggerContextMonitor) {
      curr = this.currentFuture;
    }
    return curr.get();
  }

  @Override
  public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    ScheduledFuture<?> curr;
    synchronized (this.triggerContextMonitor) {
      curr = this.currentFuture;
    }
    return curr.get(timeout, unit);
  }
}
