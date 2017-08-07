package com.lifeonwalden.springscheduling;

import com.lifeonwalden.springscheduling.concurrent.ThreadPoolTaskSchedulerWithRetry;
import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.task.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskRunningTest {
    @Test
    public void runIndependentTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        AtomicInteger counter = new AtomicInteger(0);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Work("IndependentWork001", "IndependentWork001", new Worker() {

            @Override
            public void doJob(Map<String, Object> context) {
                counter.incrementAndGet();

            }
        }, new Monitor() {
            @Override
            public void notificate(TaskEvent event) {

            }
        }));
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runIndependentTaskWithCrush() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        scheduler.setPoolSize(10);
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Work("IndependentWork001", "IndependentWork001", new Worker() {

            @Override
            public void doJob(Map<String, Object> context) {
                try {
                    Thread.sleep(new Double(Math.random() * 50000).longValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, new Monitor() {
            @Override
            public void notificate(TaskEvent event) {

            }
        }));
        scheduler.schedule(task);

        com.lifeonwalden.springscheduling.CronTrigger trigger2 =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger002", "CronTrigger002", "0-59/10 * * * * ?");
        TaskTriggerContext triggerContext2 = new TaskTriggerContext(trigger2);
        IndependentTask task2 = new IndependentTask("IndependentTask002", "IndependentTask002", triggerContext2, new Work("IndependentWork002", "IndependentWork002", (Map<String, Object> context) -> {
            try {
                Thread.sleep(new Double(Math.random() * 50000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (TaskEvent event) -> {
        }));
        scheduler.schedule(task2);

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runIndependentTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Work("IndependentWork001", "IndependentWork001", (Map<String, Object> context) -> {
            System.out.println("I am bad boy");
            throw new RuntimeException();
        }, (TaskEvent event) -> {
        }));
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runIndependentTaskWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/15 * * 28 8 ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new Work("IndependentWork001", "IndependentWork001", (Map<String, Object> context) -> {
            System.out.println("START @" + new Date());
            System.out.println("I am bad boy");
            if (Math.random() * 10 > 3) {
                System.out.println("ERROR @" + new Date());
                throw new RuntimeException();
            } else {
                System.out.println("END @" + new Date());
            }
        }, (TaskEvent event) -> {
        }));
        task.setCanRetry(true);
        task.setRetryAfter(10);
        scheduler.schedule(task);

        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runIndependentTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), new Work("IndependentWork001", "IndependentWork001", new WorkImpl(), new MonitorImpl()));
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runIndependentTaskWithMonitorError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        IndependentTask task = new IndependentTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), new Work("IndependentWork001", "IndependentWork001", (Map<String, Object> context) -> {
            System.out.println("I am bad boy");
            throw new RuntimeException();
        }, (TaskEvent event) -> {

        }));
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runChainTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("worker 001 is running.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runChainTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("BadBoy", "BadBoy", (Map<String, Object> context) -> {
            System.out.println("BadBoy is running.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runChainTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("worker 001 is running.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runChainTaskWithMonitorWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("bad boy is running.");
            throw new RuntimeException("bad boy don't finish work.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runChainTaskWithMonitorWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/45 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("START @" + new Date());
            System.out.println("I am bad boy");
            if (Math.random() * 10 > 4) {
                System.out.println("ERROR @" + new Date());
                throw new RuntimeException();
            } else {
                System.out.println("END @" + new Date());
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        task.setCanRetry(true);
        task.setRetryAfter(10);
        scheduler.schedule(task);

        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void runChainTaskWithMonitorWithErrorWithRetryWithFullNew() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/45 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("START @" + new Date());
            System.out.println("I am bad boy");
            if (Math.random() * 10 > 4) {
                System.out.println("ERROR @" + new Date());
                throw new RuntimeException();
            } else {
                System.out.println("END @" + new Date());
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        task.setCanRetry(true);
        task.setRetryAfter(10);
        task.setAlwaysFromBeginning(true);
        scheduler.schedule(task);

        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDependentChainTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("worker 001 is running.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDependentChainTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("bad boy is running.");
            throw new RuntimeException();
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDependentChainTaskWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/45 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("bad boy is running.");
            throw new RuntimeException();
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("BadBoy", "BadBoy", (Map<String, Object> context) -> {
            System.out.println("START @" + new Date());
            System.out.println("I am bad boy");
            if (Math.random() * 10 > 4) {
                System.out.println("ERROR @" + new Date());
                throw new RuntimeException();
            } else {
                System.out.println("END @" + new Date());
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, taskList);
        task.setCanRetry(true);
        task.setRetryAfter(10);
        scheduler.schedule(task);

        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDependentChainTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("Work001 is running.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runDependentChainTaskWithMonitorWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger =
                new com.lifeonwalden.springscheduling.CronTrigger("CronTrigger001", "CronTrigger001", "0-59/5 * * * * ?");
        TaskTriggerContext triggerContext = new TaskTriggerContext(trigger);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("Work001", "Work001", (Map<String, Object> context) -> {
            System.out.println("bad boy is running.");
            throw new RuntimeException();
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("Work002", "Work002", (Map<String, Object> context) -> {
            System.out.println("worker 002 is running.");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("IndependentTask001", "IndependentTask001", triggerContext, new MonitorImpl(), taskList);
        scheduler.schedule(task);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
