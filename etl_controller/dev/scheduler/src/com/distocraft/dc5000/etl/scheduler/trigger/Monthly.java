package com.distocraft.dc5000.etl.scheduler.trigger;

import java.util.Date;
import java.util.GregorianCalendar;

import com.distocraft.dc5000.etl.scheduler.TimeTrigger;

/**
 * Checks for day of the month and execution time.
 * 
 * @author savinen
 */
public class Monthly extends TimeTrigger {

  public Monthly() {
    super();
  }

  public boolean execute() throws Exception {
    boolean first = false;
    long time;
    update();
    

    if (this.lastExecutionTime != 0) {
      // last executed time
      time = this.lastExecutionTime;

    } else {

      // last executed time
      time = this.executionTime;
      first = true;
    }

    GregorianCalendar exCal = new GregorianCalendar();
    exCal.setTimeInMillis(time);

    // current date
    GregorianCalendar curCal = new GregorianCalendar();
    curCal.setTime(new Date());

    GregorianCalendar curMonth = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
        .get(GregorianCalendar.MONTH), 0, 0, 0);

    GregorianCalendar exMonth = new GregorianCalendar(exCal.get(GregorianCalendar.YEAR), exCal
        .get(GregorianCalendar.MONTH), 0, 0, 0);

    GregorianCalendar curDay = new GregorianCalendar(0, 0, curCal.get(GregorianCalendar.DATE), curCal
        .get(GregorianCalendar.HOUR_OF_DAY), curCal.get(GregorianCalendar.MINUTE));

    GregorianCalendar exDay = new GregorianCalendar(0, 0, exCal.get(GregorianCalendar.DATE), exCal
        .get(GregorianCalendar.HOUR_OF_DAY), exCal.get(GregorianCalendar.MINUTE));

    GregorianCalendar curDayPlusHour = (GregorianCalendar) curCal.clone();
    curDayPlusHour.add(GregorianCalendar.HOUR_OF_DAY, 1);

    // if lastExecutionTime is more than one hou in the future release the
    // trigger.
    if (curDayPlusHour.getTimeInMillis() < time) {

      // set last execution time
      GregorianCalendar newExDay = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
          .get(GregorianCalendar.MONTH), curCal.get(GregorianCalendar.DATE), exCal.get(GregorianCalendar.HOUR), exCal
          .get(GregorianCalendar.MINUTE));

      this.lastExecutionTime = newExDay.getTimeInMillis();
      return true;

    }

    // has this trigger been activated this month
    if (exMonth.before(curMonth) || first) {

      if (!exDay.after(curDay)) {

        // set last execution time
        GregorianCalendar newExDay = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
            .get(GregorianCalendar.MONTH), curCal.get(GregorianCalendar.DATE), exCal.get(GregorianCalendar.HOUR), exCal
            .get(GregorianCalendar.MINUTE));

        this.lastExecutionTime = newExDay.getTimeInMillis();

        return true;
      }

    }
    return false;
  }

}
