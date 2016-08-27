package com.lifeonwalden.springscheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.lifeonwalden.springscheduling.task.ChainTask;
import com.lifeonwalden.springscheduling.task.DependentChainTask;
import com.lifeonwalden.springscheduling.task.IndependentTask;
import com.lifeonwalden.springscheduling.task.TaskTriggerContext;
import com.lifeonwalden.springscheduling.task.Worker;

public class TaskRunningTest {
  @Test
  public void runIndependentTask() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    IndependentTask task =
        new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new WorkImpl());
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runIndependentTaskWithCrush() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    IndependentTask task =
        new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Worker() {

          @Override
          public void doJob(Map<String, Object> context) {
            long id = System.currentTimeMillis();
            System.out.println(id + " START @" + new Date());
            System.out.println("worker is running");
            try {
              Thread.sleep(new Double(Math.random() * 50000).longValue());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println(id + " END @" + new Date());
          }
        });
    scheduler.schedule(task, trigger);

    com.lifeonwalden.springscheduling.CronTrigger trigger2 =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger002", "CronTrigger002", "0-59/10 * * 27 8 ?");
    TaskTriggerContext triggerContext2 = new TaskTriggerContext(trigger2);
    IndependentTask task2 =
        new IndependentTask("IndependentTask002", "IndependentTask002", triggerContext2, new Worker() {

          @Override
          public void doJob(Map<String, Object> context) {
            long id = System.currentTimeMillis();
            System.out.println(id + " START @" + new Date());
            System.out.println("worker2 is running");
            try {
              Thread.sleep(new Double(Math.random() * 50000).longValue());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println(id + " END @" + new Date());
          }
        });
    scheduler.schedule(task2, trigger);

    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runIndependentTaskWithError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    IndependentTask task =
        new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Worker() {

          @Override
          public void doJob(Map<String, Object> context) {
            System.out.println("I am bad boy");
            throw new RuntimeException();
          }
        });
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runIndependentTaskWithMonitor() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext,
        new MonitorImpl(), new WorkImpl());
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runIndependentTaskWithMonitorError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext,
        new MonitorImpl(), new Worker() {

          @Override
          public void doJob(Map<String, Object> context) {
            System.out.println("I am bad boy");
            throw new RuntimeException();
          }
        });
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runChainTask() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 001 is running.");

      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runChainTaskWithError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("bad boy is running.");
        throw new RuntimeException();
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runChainTaskWithMonitor() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 001 is running.");
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    ChainTask task =
        new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runChainTaskWithMonitorWithError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("bad boy is running.");
        throw new RuntimeException("bad boy don't finish work.");
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    ChainTask task =
        new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runDependentChainTask() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 001 is running.");

      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    DependentChainTask task =
        new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runDependentChainTaskWithError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("bad boy is running.");
        throw new RuntimeException();
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    DependentChainTask task =
        new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runDependentChainTaskWithMonitor() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 001 is running.");
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    DependentChainTask task =
        new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void runDependentChainTaskWithMonitorWithError() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    com.lifeonwalden.springscheduling.CronTrigger trigger =
        new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * 27 8 ?");
    TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
    List<Worker> taskList = new ArrayList<Worker>();
    taskList.add(new Worker() {

      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("bad boy is running.");
        throw new RuntimeException("bad boy don't finish work.");
      }
    });
    taskList.add(new Worker() {
      @Override
      public void doJob(Map<String, Object> context) {
        System.out.println("worker 002 is running.");

      }
    });
    DependentChainTask task =
        new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
    scheduler.schedule(task, trigger);

    try {
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
