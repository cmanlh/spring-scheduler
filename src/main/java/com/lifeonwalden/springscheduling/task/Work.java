package com.lifeonwalden.springscheduling.task;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lifeonwalden.springscheduling.monitor.Monitor;
import com.lifeonwalden.springscheduling.monitor.TaskEvent;
import com.lifeonwalden.springscheduling.monitor.TaskEventType;

/**
 * 临时性工作，一般手动触发用于解决一些非经常性事务工作
 * 
 * @author HongLu
 *
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

  public Map<String, Object> getParam() {
    return param;
  }

  public void setParam(Map<String, Object> param) {
    this.param = param;
  }

  public void doJob(Map<String, Object> param) {
    setParam(param);
    run();
  }

  @Override
  public void run() {
    if (null != monitor) {
      monitor.notificate(
          new TaskEvent().setHappendTime(new Date()).setTaskId(this.id).setType(TaskEventType.START).setParam(param));
    }

    TaskStatusEnum status = TaskStatusEnum.COMPLETED;
    Throwable error = null;
    try {
      this.worker.doJob(param);
    } catch (Throwable e) {
      logger.error("Work is failed : {} {}", this.name, this.id);

      status = TaskStatusEnum.FAILED;
      error = e;
    }

    if (null != monitor) {
      TaskEvent event = new TaskEvent();
      event.setHappendTime(new Date()).setTaskId(this.id);

      if (TaskStatusEnum.COMPLETED == status) {
        event.setType(TaskEventType.COMPELETE);
      } else {
        event.setType(TaskEventType.FAIL).setFailPrintList(Arrays.asList(error));
      }

      monitor.notificate(event);
    }
  }

}
