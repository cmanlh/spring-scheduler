package com.lifeonwalden.springscheduling.concurrent;

import com.lifeonwalden.springscheduling.task.Task;
import com.lifeonwalden.springscheduling.task.Work;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolTaskSchedulerWithRetry extends ExecutorConfigurationSupport {
    private final static Logger logger = LogManager.getLogger(ThreadPoolTaskSchedulerWithRetry.class);

    private static final long serialVersionUID = -4703424603462186353L;

    private volatile ScheduledThreadPoolExecutor scheduledExecutor;

    private volatile int poolSize = 1;

    private ConcurrentHashMap<String, Task> taskMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Work> workMap = new ConcurrentHashMap<>();

    public int getPoolSize() {
        if (this.scheduledExecutor == null) {
            return this.poolSize;
        }
        return getScheduledThreadPoolExecutor().getPoolSize();
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        this.scheduledExecutor.setCorePoolSize(poolSize);
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        this.scheduledExecutor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);

        return this.scheduledExecutor;
    }

    protected ScheduledThreadPoolExecutor createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        return this.scheduledExecutor;
    }

    public ScheduledFuture<?> schedule(Task task) {
        logger.info("Load task [{}]", task.getName());
        if (null != task.getWorkList()) {
            task.getWorkList().forEach(work -> logger.info("Load work for task {} : {}", task.getName(), work.getName()));
        }

        ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutor();
        task.setExecutor(executor);
        taskMap.put(task.getId(), task);
        return task.schedule();
    }

    public Task getTask(String taskId) {
        return taskMap.get(taskId);
    }

    public void addWork(Work work) {
        logger.info("Load work [{}]", work.getName());

        this.workMap.put(work.getId(), work);
    }

    public void removeTask(Task task) {
        logger.info("remove task [{}]", task.getName());

        Task preTask = taskMap.remove(task.getId());
        if (null != preTask) {
            preTask.setStopped(true);
            preTask.cancel(false);
        }
    }

    public void updateTask(Task task) {
        logger.info("update task [{}]", task.getName());

        removeTask(task);
        schedule(task);
    }

    /**
     * only accept work
     *
     * @param workId
     * @param param
     * @param async  是否异步执行
     */
    public void execute(String workId, Map<String, Object> param, boolean async) {
        Work work = null;
        if (null == workId || workId.length() == 0 || (work = this.workMap.get(workId)) == null) {
            logger.error("Can't find work for [{}]", workId);

            return;
        }

        if (async) {
            final Work _work = work;
            new Thread(() -> _work.doJob(param)).start();
        } else {
            work.doJob(param);
        }
    }

    /**
     * only accept task
     *
     * @param taskId
     * @param param
     * @param async  是否异步执行
     */
    public void executeTask(String taskId, Map<String, Object> param, boolean async) {
        Task task = null;
        if (null == taskId || taskId.length() == 0 || (task = this.taskMap.get(taskId)) == null) {
            logger.error("Can't find task for [{}]", taskId);

            return;
        }

        if (async) {
            final Task _task = task;
            new Thread(() -> _task.doJob(param, true)).start();
        } else {
            task.doJob(param, true);
        }
    }

    public Work getWork(String workId) {
        return workMap.get(workId);
    }
}
