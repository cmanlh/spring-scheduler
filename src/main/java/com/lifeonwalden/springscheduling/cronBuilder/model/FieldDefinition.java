package com.lifeonwalden.springscheduling.cronBuilder.model;

public interface FieldDefinition {
    String toExpression();

    boolean isValid(Class<?> clazz);
}
