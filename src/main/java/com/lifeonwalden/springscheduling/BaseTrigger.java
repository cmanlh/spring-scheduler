package com.lifeonwalden.springscheduling;

import org.springframework.scheduling.Trigger;

public abstract class BaseTrigger implements Trigger {
  protected String id;

  protected String name;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

}
