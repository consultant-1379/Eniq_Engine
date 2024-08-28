package com.distocraft.dc5000.etl.scheduler.trigger;

import java.util.Date;
import java.util.GregorianCalendar;

import com.distocraft.dc5000.etl.scheduler.TimeTrigger;

/**
 * Checks for weekday flag and execution time.
 * 
 * @author savinen
 */
public class Weekly extends TimeTrigger {

  public Weekly() {
    super();
  }

  public boolean execute() throws Exception {

    boolean first = false;
    long time;
    update();

    
    // current time
    Date curDate = new Date();

    long currentTime = curDate.getTime();
    
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
    curCal.setTimeInMillis(currentTime);

    GregorianCalendar curDay = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
        .get(GregorianCalendar.MONTH), curCal.get(GregorianCalendar.DATE), 0, 0);

    GregorianCalendar exDay = new GregorianCalendar(exCal.get(GregorianCalendar.YEAR), exCal
        .get(GregorianCalendar.MONTH), exCal.get(GregorianCalendar.DATE), 0, 0);

    GregorianCalendar curTime = new GregorianCalendar(0, 0, 0, curCal.get(GregorianCalendar.HOUR_OF_DAY), curCal
        .get(GregorianCalendar.MINUTE));

    GregorianCalendar exTime = new GregorianCalendar(0, 0, 0, exCal.get(GregorianCalendar.HOUR_OF_DAY), exCal
        .get(GregorianCalendar.MINUTE));

    GregorianCalendar curDayPlusHour = (GregorianCalendar) curCal.clone();
    curDayPlusHour.add(GregorianCalendar.HOUR_OF_DAY, 1);

    // if lastExecutionTime is more than one hour in the future release the
    // trigger.
    if (curDayPlusHour.getTimeInMillis() < time) {

      GregorianCalendar newExDay = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
          .get(GregorianCalendar.MONTH), curCal.get(GregorianCalendar.DATE), exCal.get(GregorianCalendar.HOUR_OF_DAY),
          exCal.get(GregorianCalendar.MINUTE));

      this.lastExecutionTime = newExDay.getTimeInMillis();

      return true;

    }

    // has this trigger been activated today
    if (exDay.before(curDay) || first) {

      // check if the hour and minute are correct
      if (!exTime.after(curTime)) {

        // check for correct day
        boolean day = false;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY
            && schedule.getMon_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.TUESDAY
            && schedule.getTue_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.WEDNESDAY
            && schedule.getWed_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.THURSDAY
            && schedule.getThu_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.FRIDAY
            && schedule.getFri_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY
            && schedule.getSat_flag().equalsIgnoreCase("y"))
          day = true;
        if (curCal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY
            && schedule.getSun_flag().equalsIgnoreCase("y"))
          day = true;

        // day is correct
        if (day) {

          // set last execution time

          GregorianCalendar newExDay = new GregorianCalendar(curCal.get(GregorianCalendar.YEAR), curCal
              .get(GregorianCalendar.MONTH), curCal.get(GregorianCalendar.DATE), exCal
              .get(GregorianCalendar.HOUR_OF_DAY), exCal.get(GregorianCalendar.MINUTE));

          this.lastExecutionTime = newExDay.getTimeInMillis();
          return true;

        }

      }

    }

    return false;
  }

}
