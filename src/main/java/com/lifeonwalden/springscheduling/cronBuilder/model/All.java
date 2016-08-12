package com.lifeonwalden.springscheduling.cronBuilder.model;

/**
 * 所有
 * 
 * @author luhong
 *
 */
public class All implements FieldDefinition {


    @Override
    public String toExpression() {
        return "*";
    }

    @Override
    public boolean isValid(Class<?> clazz) {
        return true;
    }

}
