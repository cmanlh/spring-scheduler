package com.lifeonwalden.springscheduling.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;

import com.lifeonwalden.springscheduling.task.Task;
import com.lifeonwalden.springscheduling.task.Work;

public class ThreadPoolTaskSchedulerWithRetry extends ExecutorConfigurationSupport {
  private final static Logger logger = LogManager.getLogger(ThreadPoolTaskSchedulerWithRetry.class);

  private static final long serialVersionUID = -4703424603462186353L;

  private volatile ScheduledThreadPoolExecutor scheduledExecutor;

  private volatile int poolSize = 1;

  private ConcurrentHashMap<String, Task> taskMap = new ConcurrentHashMap<>();

  private ConcurrentHashMap<String, Work> workMap = new ConcurrentHashMap<>();

  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
    this.scheduledExecutor.setCorePoolSize(poolSize);
  }

  public int getPoolSize() {
    if (this.scheduledExecutor == null) {
      // Not initialized yet: assume initial pool size.
      return this.poolSize;
    }
    return getScheduledThreadPoolExecutor().getPoolSize();
  }

  @Override
  protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
      RejectedExecutionHandler rejectedExecutionHandler) {
    this.scheduledExecutor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);

    return this.scheduledExecutor;
  }

  protected ScheduledThreadPoolExecutor createExecutor(int poolSize, ThreadFactory threadFactory,
      RejectedExecutionHandler rejectedExecutionHandler) {
    return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
  }

  public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
    return this.scheduledExecutor;
  }

  public ScheduledFuture<?> schedule(Task task) {
    ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutor();
    task.setExecutor(executor);
    taskMap.put(task.getId(), task);
    return task.schedule();
  }

  public void addWork(Work work) {
    this.workMap.put(work.getId(), work);
  }

  /**
   * only accept work
   * 
   * @param taskId
   * @param param
   * @param async 是否异步执行
   */
  public void execute(String taskId, Map<String, Object> param, boolean async) {
    Work work = null;
    if (null == taskId || taskId.length() == 0 || (work = this.workMap.get(taskId)) == null) {
      logger.warn("Can't find task for {}", taskId);

      return;
    }

    if ( async){
      ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutor();
      work.setParam(param);
      executor.execute(work);
    } else {
      work.doJob(param);
    }
  }
}
