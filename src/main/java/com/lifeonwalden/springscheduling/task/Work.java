package com.lifeonwalden.springscheduling.task;

import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 临时性工作，一般手动触发用于解决一些非经常性事务工作
 *
 * @author HongLu
 */
public class Work implements Runnable {
    private final static Logger logger = LogManager.getLogger(Work.class);

    private String id;

    private String name;

    private Worker worker;

    private Monitor monitor;

    private Map<String, Object> param;

    public Work(String id, String name, Worker worker, Monitor monitor) {
        this.id = id;
        this.name = name;
        this.worker = worker;
        this.monitor = monitor;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public Worker getWorker() {
        return worker;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public void doJob(Map<String, Object> param) {
        _run(param);
    }

    @Override
    public void run() {
        _run(null);
    }

    private void _run(Map<String, Object> param) {
        Map<String, Object> _param;
        if (null == param) {
            if (null == this.param) {
                _param = new HashMap<>();
            } else {
                _param = this.param;
            }
        } else {
            _param = param;
        }
        TaskEvent startTaskEvent = new TaskEvent();
        if (null != monitor) {
            monitor.notificate(startTaskEvent.setHappendTime(new Date()).setTaskId(this.id).setType(TaskEventType.START).setParam(_param));
        }

        TaskStatusEnum status = TaskStatusEnum.COMPLETED;
        Throwable error = null;
        try {
            this.worker.doJob(_param);
        } catch (Throwable e) {
            logger.error(new FormattedMessage("Work execute failed - name:{}, id:{}", this.name, this.id), e);

            status = TaskStatusEnum.FAILED;
            error = e;
        }

        if (null != monitor) {
            TaskEvent event = new TaskEvent();
            event.setHappendTime(new Date()).setTaskId(this.id);

            if (TaskStatusEnum.COMPLETED == status) {
                event.setType(TaskEventType.COMPELETE).setStartTime(startTaskEvent.getHappendTime()).setParam(startTaskEvent.getParam());
            } else {
                event.setType(TaskEventType.FAIL).setFailPrintList(Arrays.asList(error.getMessage())).setStartTime(startTaskEvent.getHappendTime())
                        .setParam(startTaskEvent.getParam());
            }

            monitor.notificate(event);
        }

        if (null != error) {
            throw new RuntimeException(error);
        }
    }

}
