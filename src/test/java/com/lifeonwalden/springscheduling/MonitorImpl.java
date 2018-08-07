package com.lifeonwalden.springscheduling;

import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;

import java.util.concurrent.atomic.AtomicInteger;

public class MonitorImpl implements Monitor {

    private AtomicInteger counter = new AtomicInteger(0);

    private String name;

    public MonitorImpl(String name) {
        this.name = name;
    }

    @Override
    public void notificate(TaskEvent event) {
        try {
            if (event.getType() == TaskEventType.START) {
                counter.incrementAndGet();
            }
            System.out.printf("%s report: %s Times @ %s to %s\n", this.name, counter.get(), event.getHappendTime(), event.getType());
        } catch (Exception e) {
            System.err.println("Error happened in monitor : ".concat(e.getMessage()));
        }
    }

}
