package com.lifeonwalden.springscheduling;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * {@link Trigger} implementation for cron expressions. Wraps a {@link CronSequenceGenerator}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see CronSequenceGenerator
 */
public class CronTrigger extends BaseTrigger {

  private CronSequenceGenerator sequenceGenerator;

  private String expression;


  /**
   * Build a {@link CronTrigger} from the pattern provided in the default time zone.
   * 
   * @param expression a space-separated list of time fields, following cron expression conventions
   */
  public CronTrigger(String id, String name, String expression) {
    this.expression = expression;
    this.sequenceGenerator = new CronSequenceGenerator(expression);
    this.id = id;
    this.name = name;
  }

  /**
   * Build a {@link CronTrigger} from the pattern provided in the given time zone.
   * 
   * @param expression a space-separated list of time fields, following cron expression conventions
   * @param timeZone a time zone in which the trigger times will be generated
   */
  public CronTrigger(String id, String name, String expression, TimeZone timeZone) {
    this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
    this.id = id;
    this.name = name;
  }

  public void updateTrigger(String expression) {
    this.sequenceGenerator = new CronSequenceGenerator(expression);
  }

  public void updateTrigger(String expression, TimeZone timeZone) {
    this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
  }

  /**
   * Return the cron pattern that this trigger has been built with.
   */
  public String getExpression() {
    return this.expression;
  }


  /**
   * Determine the next execution time according to the given trigger context.
   * <p>
   * Next execution times are calculated based on the {@linkplain TriggerContext#lastCompletionTime completion time} of
   * the previous execution; therefore, overlapping executions won't occur.
   */
  @Override
  public Date nextExecutionTime(TriggerContext triggerContext) {
    Date date = triggerContext.lastCompletionTime();
    if (date != null) {
      Date scheduled = triggerContext.lastScheduledExecutionTime();
      if (scheduled != null && date.before(scheduled)) {
        // Previous task apparently executed too early...
        // Let's simply use the last calculated execution time then,
        // in order to prevent accidental re-fires in the same second.
        date = scheduled;
      }
    } else {
      date = new Date();
    }
    return this.sequenceGenerator.next(date);
  }

  @Override
  public boolean equals(Object other) {
    return (this == other
        || (other instanceof CronTrigger && this.sequenceGenerator.equals(((CronTrigger) other).sequenceGenerator)));
  }

  @Override
  public int hashCode() {
    return this.sequenceGenerator.hashCode();
  }

  @Override
  public String toString() {
    return this.sequenceGenerator.toString();
  }

}