/**
 * Copyright 2016 HongLu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.lifeonwalden.springscheduling.task;

import com.lifeonwalden.springscheduling.BaseTrigger;
import com.lifeonwalden.springscheduling.bean.ExecutionInfo;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

public abstract class Task implements Runnable, ScheduledFuture<Object> {
    private final static Logger logger = LogManager.getLogger(Task.class);
    private final Object triggerContextMonitor = new Object();
    protected String id;
    protected String name;
    protected Map<String, Object> param;
    protected TaskTriggerContext triggerContext;
    protected Monitor monitor;
    protected boolean canRetry = false;
    protected int retryTimes = 0;
    protected int maxRetryTimes = 3;
    protected boolean stopped = false;
    /**
     * always retry from the beginning of worker list
     */
    protected boolean alwaysFromBeginning = false;
    /**
     * retry after 30 Minutes
     */
    protected long retryAfter = 1800;
    protected ScheduledExecutorService executor;
    protected TaskStatusEnum status = TaskStatusEnum.WAITING;
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

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public boolean isCanRetry() {
        return canRetry;
    }

    public void setCanRetry(boolean canRetry) {
        this.canRetry = canRetry;
    }

    public boolean isAlwaysFromBeginning() {
        return alwaysFromBeginning;
    }

    public void setAlwaysFromBeginning(boolean alwaysFromBeginning) {
        this.alwaysFromBeginning = alwaysFromBeginning;
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

    public void setTriggerContext(TaskTriggerContext triggerContext) {
        this.triggerContext = triggerContext;
    }

    public BaseTrigger getTrigger() {
        return triggerContext.getTrigger();
    }

    public ScheduledFuture<?> schedule() {
        if (isStopped()) {
            return null;
        }

        synchronized (this.triggerContextMonitor) {
            Date scheduledExecutionTime = this.triggerContext.getTrigger().nextExecutionTime(this.triggerContext);
            if (scheduledExecutionTime == null) {
                return null;
            }
            long initialDelay = scheduledExecutionTime.getTime() - System.currentTimeMillis();

            logger.info("Schedule task [{}] run @ {}", this.name, scheduledExecutionTime.toString());
            this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }

    private void _run(Map<String, Object> param) {
        ExecutionInfo executionInfo = _execute(param, false);

        synchronized (this.triggerContextMonitor) {
            this.triggerContext.update(executionInfo.getNextExecutionTime(), executionInfo.getActualExecutionTime(), executionInfo.getCompletionTime());
            if (!this.currentFuture.isCancelled()) {
                schedule();
            }
        }
    }

    private ExecutionInfo _execute(Map<String, Object> param, boolean isOneTimeExecution) {
        ExecutionInfo executionInfo = new ExecutionInfo();

        List<String> failPrintList = null;
        TaskEvent startTaskEvent = new TaskEvent();
        final String taskId = this.id;
        try {
            executionInfo.setActualExecutionTime(new Date());

            Map<String, Object> _param = null != param ? param : (null == this.param ? new WeakHashMap<>() : new WeakHashMap<>(this.param));
            if (null != monitor) {
                new Thread(() -> monitor.notificate(startTaskEvent.setHappendTime(executionInfo.getActualExecutionTime()).setTaskId(taskId).setType(TaskEventType.START).setParam(_param))).start();
            }

            this.status = TaskStatusEnum.RUNNING;
            logger.info("Task [{}] Start", this.name);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            failPrintList = doJob(_param, isOneTimeExecution);
            stopWatch.stop();
            logger.info("Task [{}] End, the task cost time :  {}", this.name, stopWatch.getTime());
            this.status = TaskStatusEnum.COMPLETED;
        } catch (Exception e) {
            logger.error("Task failed", e);

            this.status = TaskStatusEnum.FAILED;

            failPrintList = Arrays.asList(e.getMessage());
        }
        executionInfo.setCompletionTime(new Date());
        executionInfo.setNextExecutionTime(getTrigger().nextExecutionTime(triggerContext));
        if (!isOneTimeExecution) {
            if (null != failPrintList && this.canRetry && (0 == this.maxRetryTimes || this.maxRetryTimes > this.retryTimes)) {
                executionInfo.setNextExecutionTime(Date.from(LocalDateTime.now().plus(this.retryAfter, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toInstant()));
                this.retryTimes++;
                logger.info("Task {} going to retry the {} time.", this.name, this.retryTimes);
            } else {
                this.retryTimes = 0;
            }
        }
        executionInfo.setSuccess(TaskStatusEnum.COMPLETED == this.status);

        if (null != monitor) {
            final List<String> _failPrintList = failPrintList;
            new Thread(() -> monitor.notificate(
                    new TaskEvent().setHappendTime(executionInfo.getCompletionTime()).setTaskId(taskId)
                            .setType(executionInfo.isSuccess() ? TaskEventType.COMPELETE : TaskEventType.FAIL)
                            .setFailPrintList(_failPrintList).setNextExecutionTime(executionInfo.getNextExecutionTime())
                            .setStartTime(executionInfo.getActualExecutionTime()).setParam(startTaskEvent.getParam())
            )).start();
        }

        return executionInfo;
    }

    public abstract List<String> doJob(Map<String, Object> param, boolean isOneTimeExecution);

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

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public abstract List<Work> getWorkList();
}
