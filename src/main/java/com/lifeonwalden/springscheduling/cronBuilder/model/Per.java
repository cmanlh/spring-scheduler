package com.lifeonwalden.springscheduling.cronBuilder.model;

/**
 * 每隔
 * 
 * @author luhong
 *
 */
public class Per implements FieldDefinition {
    private FieldDefinition field;

    private int step;

    public Per(Between field, int step) {
        if (step < 2) {
            throw new RuntimeException("Invalid step value");
        }

        this.step = step;
        this.field = field;
    }

    public Per(All field, int step) {
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
