package com.lifeonwalden.springscheduling.cronBuilder;

import com.lifeonwalden.springscheduling.cronBuilder.model.All;
import com.lifeonwalden.springscheduling.cronBuilder.model.FieldDefinition;
import com.lifeonwalden.springscheduling.cronBuilder.time.Day;
import com.lifeonwalden.springscheduling.cronBuilder.time.Hour;
import com.lifeonwalden.springscheduling.cronBuilder.time.Minute;
import com.lifeonwalden.springscheduling.cronBuilder.time.Month;
import com.lifeonwalden.springscheduling.cronBuilder.time.Second;
import com.lifeonwalden.springscheduling.cronBuilder.time.Weekday;

public class CronBuilder {
    private FieldDefinition second = new All();
    private FieldDefinition minute = new All();
    private FieldDefinition hour = new All();
    private FieldDefinition day = new All();
    private FieldDefinition month = new All();
    private FieldDefinition weekday = new All();

    public static CronBuilder build() {
        return new CronBuilder();
    }

    public CronBuilder second(FieldDefinition def) {
        if (!def.isValid(Second.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.second = def;

        return this;
    }

    public CronBuilder minute(FieldDefinition def) {
        if (!def.isValid(Minute.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.minute = def;

        return this;
    }

    public CronBuilder hour(FieldDefinition def) {
        if (!def.isValid(Hour.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.hour = def;

        return this;
    }

    public CronBuilder day(FieldDefinition def) {
        if (!def.isValid(Day.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.day = def;

        return this;
    }

    public CronBuilder month(FieldDefinition def) {
        if (!def.isValid(Month.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.month = def;

        return this;
    }

    public CronBuilder weekday(FieldDefinition def) {
        if (!def.isValid(Weekday.class)) {
            throw new RuntimeException("Invalid field definition");
        }
        this.weekday = def;

        return this;
    }

    public String toCronExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(second.toExpression()).append(" ").append(minute.toExpression()).append(" ").append(hour.toExpression()).append(" ")
                        .append(day.toExpression()).append(" ").append(month.toExpression()).append(" ").append(weekday.toExpression());

        return sb.toString();
    }
}
