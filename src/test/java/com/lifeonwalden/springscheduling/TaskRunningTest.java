package com.lifeonwalden.springscheduling;

import com.lifeonwalden.springscheduling.concurrent.ThreadPoolTaskSchedulerWithRetry;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.task.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskRunningTest {
    @Test
    public void runIndependentTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        IndependentTask task5s3Times = new IndependentTask("t5s3Times", "t5s3Times", tc5s, new Work("w", "w", context -> {
            counter5s3Times.incrementAndGet();
            try {
                Thread.sleep(new Double(Math.random() * 5000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, new MonitorImpl("t5s3Times")));
        scheduler.schedule(task5s3Times);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithCrush() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        scheduler.setPoolSize(10);
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        IndependentTask task5s3Times = new IndependentTask("t5s3Times", "t5s3Times", tc5s, new Work("w", "w", context -> {
            counter5s3Times.incrementAndGet();
            try {
                Thread.sleep(new Double(Math.random() * 5000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, new MonitorImpl("t5s3Times")));
        scheduler.schedule(task5s3Times);

        com.lifeonwalden.springscheduling.CronTrigger trigger10s =
                new com.lifeonwalden.springscheduling.CronTrigger("10s", "10s", "0-59/10 * * * * ?");
        TaskTriggerContext tc10s = new TaskTriggerContext(trigger10s);
        AtomicInteger counter10s3Times = new AtomicInteger(0);
        IndependentTask task10s3Times = new IndependentTask("t10s3Times", "t10s3Times", tc10s, new Work("w", "w", context -> {
            counter10s3Times.incrementAndGet();
            try {
                Thread.sleep(new Double(Math.random() * 5000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, new MonitorImpl("t10s3Times")));
        scheduler.schedule(task10s3Times);

        while (counter5s3Times.get() <= 3 || counter10s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        IndependentTask task5s3Times = new IndependentTask("t5s3Times", "t5s3Times", tc5s, new Work("w", "w", context -> {
            counter5s3Times.incrementAndGet();
            if (new Double(Math.random() * 1000).longValue() % 7 != 0) {
                throw new RuntimeException("Bad Boy");
            }
        }, new MonitorImpl("t5s3Times")));
        scheduler.schedule(task5s3Times);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger15s =
                new com.lifeonwalden.springscheduling.CronTrigger("15s", "15s", "0-59/15 * * * * ?");
        TaskTriggerContext tc15s = new TaskTriggerContext(trigger15s);
        AtomicInteger counter15s5Times = new AtomicInteger(0);
        IndependentTask task15s3Times = new IndependentTask("t15s5Times", "t15s5Times", tc15s, new Work("w", "w", context -> {
            counter15s5Times.incrementAndGet();
            if (new Double(Math.random() * 1000).longValue() % 7 != 0) {
                throw new RuntimeException("Bad Boy");
            }
        }, new MonitorImpl("t15s5Times")));
        task15s3Times.setCanRetry(true);
        task15s3Times.setRetryAfter(5);
        task15s3Times.setMaxRetryTimes(0);
        scheduler.schedule(task15s3Times);

        while (counter15s5Times.get() <= 5) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithErrorWithRetryLimitTimes() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger15s =
                new com.lifeonwalden.springscheduling.CronTrigger("15s", "15s", "0-59/15 * * * * ?");
        TaskTriggerContext tc15s = new TaskTriggerContext(trigger15s);
        AtomicInteger counter15s5Times = new AtomicInteger(0);
        IndependentTask task15s3Times = new IndependentTask("t15s5Times", "t15s5Times", tc15s, new Work("w", "w", context -> {
            counter15s5Times.incrementAndGet();
            if (new Double(Math.random() * 1000).longValue() % 7 != 0) {
                throw new RuntimeException("Bad Boy");
            }
        }, new MonitorImpl("t15s5Times")));
        task15s3Times.setCanRetry(true);
        task15s3Times.setRetryAfter(5);
        scheduler.schedule(task15s3Times);

        while (counter15s5Times.get() <= 5) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        IndependentTask task5s3Times = new IndependentTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3TimesTask"), new Work("w", "w", context -> {
            counter5s3Times.incrementAndGet();
            try {
                Thread.sleep(new Double(Math.random() * 5000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, new MonitorImpl("t5s3Times")));
        scheduler.schedule(task5s3Times);

        while (counter5s3Times.get() <= 2) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runIndependentTaskWithMonitorError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        IndependentTask task5s3Times = new IndependentTask("t5s3Times", "t5s3Times", tc5s, event -> {
            throw new RuntimeException("Bad Monitor");
        }, new Work("w", "w", context -> {
            counter5s3Times.incrementAndGet();
            try {
                Thread.sleep(new Double(Math.random() * 5000).longValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, new MonitorImpl("t5s3Times")));
        scheduler.schedule(task5s3Times);

        while (counter5s3Times.get() <= 2) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runChainTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            try {
                Thread.sleep(5600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runChainTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            throw new RuntimeException("Failed to calc");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runChainTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            try {
                Thread.sleep(5600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runChainTaskWithMonitorWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            throw new RuntimeException("Failed to calc");
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runChainTaskWithMonitorWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            if (new Double(Math.random() * 1000).longValue() % 3 == 0) {
                throw new RuntimeException("Failed to calc");
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wNotification", "wNotification", (Map<String, Object> context) -> {
            System.out.println("Sending.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wFetching", "wFetching", (Map<String, Object> context) -> {
            System.out.println("Fetching.");
            if (new Double(Math.random() * 1000).longValue() % 9 != 0) {
                throw new RuntimeException("Failed to fetch");
            }
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);
        task.setCanRetry(true);
        task.setRetryAfter(9);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void runChainTaskWithMonitorWithErrorWithRetryWithFullNew() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            if (new Double(Math.random() * 1000).longValue() % 3 == 0) {
                throw new RuntimeException("Failed to calc");
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wNotification", "wNotification", (Map<String, Object> context) -> {
            System.out.println("Sending.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wFetching", "wFetching", (Map<String, Object> context) -> {
            System.out.println("Fetching.");
            if (new Double(Math.random() * 1000).longValue() % 9 != 0) {
                throw new RuntimeException("Failed to fetch");
            }
        }, (TaskEvent event) -> {
        }));
        ChainTask task = new ChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);
        task.setCanRetry(true);
        task.setRetryAfter(9);
        task.setAlwaysFromBeginning(true);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runDependentChainTask() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            try {
                Thread.sleep(5600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("t5s3Times", "t5s3Times", tc5s, taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runDependentChainTaskWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            throw new RuntimeException("Failed to calc");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("t5s3Times", "t5s3Times", tc5s, taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runDependentChainTaskWithErrorWithRetry() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            if (new Double(Math.random() * 1000).longValue() % 3 == 0) {
                throw new RuntimeException("Failed to calc");
            }
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wNotification", "wNotification", (Map<String, Object> context) -> {
            System.out.println("Sending.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wFetching", "wFetching", (Map<String, Object> context) -> {
            System.out.println("Fetching.");
            if (new Double(Math.random() * 1000).longValue() % 9 != 0) {
                throw new RuntimeException("Failed to fetch");
            }
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);
        task.setCanRetry(true);
        task.setRetryAfter(9);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runDependentChainTaskWithMonitor() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            try {
                Thread.sleep(5600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void runDependentChainTaskWithMonitorWithError() {
        ThreadPoolTaskSchedulerWithRetry scheduler = new ThreadPoolTaskSchedulerWithRetry();
        scheduler.initialize();
        com.lifeonwalden.springscheduling.CronTrigger trigger5s =
                new com.lifeonwalden.springscheduling.CronTrigger("5s", "5s", "0-59/5 * * * * ?");
        TaskTriggerContext tc5s = new TaskTriggerContext(trigger5s);
        AtomicInteger counter5s3Times = new AtomicInteger(0);
        List<Work> taskList = new ArrayList<>();
        taskList.add(new Work("wSync", "wSync", (Map<String, Object> context) -> {
            counter5s3Times.incrementAndGet();
            System.out.println("Data syncing.");
        }, (TaskEvent event) -> {
        }));
        taskList.add(new Work("wCalc", "wCalc", (Map<String, Object> context) -> {
            System.out.println("Calculating.");
            throw new RuntimeException("Failed to calc");
        }, (TaskEvent event) -> {
        }));
        DependentChainTask task = new DependentChainTask("t5s3Times", "t5s3Times", tc5s, new MonitorImpl("t5s3Times"), taskList);
        scheduler.schedule(task);

        while (counter5s3Times.get() <= 3) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
