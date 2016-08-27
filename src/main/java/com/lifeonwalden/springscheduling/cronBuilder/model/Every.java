package com.lifeonwalden.springscheduling.cronBuilder.model;

/**
 * 每
 * 
 * @author luhong
 *
 */
public class Every implements FieldDefinition {
  private FieldDefinition field;

  private int step;

  public Every(Between field, int step) {
    if (step < 2) {
      throw new RuntimeException("Invalid step value");
    }

    this.step = step;
    this.field = field;
  }

  public Every(All field, int step) {
    if (step < 2) {
      throw new RuntimeException("Invalid step value");
    }

    this.step = step;
    this.field = field;
  }

  public Every(When field, int step) {
    if (step < 2) {
      throw new RuntimeException("Invalid step value");
    }

    this.step = step;
    this.field = field;
  }

  @Override
  public String toExpression() {
    return field.toExpression() + "/" + step;
  }

  @Override
  public boolean isValid(Class<?> clazz) {
    return field.isValid(clazz);
  }

}
