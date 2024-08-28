package com.distocraft.dc5000.etl.scheduler.trigger;

import java.util.Date;

import com.distocraft.dc5000.etl.scheduler.TimeTrigger;

/**
 * @author savinen
 */
public class Interval extends TimeTrigger {

  public Interval() {
    super();
  }

  public boolean execute() throws Exception {

    long time;
    update();

    // current time
    Date curDate = new Date();

    long curTime = curDate.getTime();
    
    //  interval Calculate milliseconds
    long interval = (this.intervalHour * 60 * 60 * 1000) + (this.intervalMinute * 60 * 1000);

    // if last execution time is zero then this is the first execution
    if (this.lastExecutionTime != 0) {
      // if this is NOT the first execution so the time is retrieved from last
      // executon time

      time = this.lastExecutionTime;
    } else {
      // on first execution time is retrieved from execution time and interval
      // is subtracted form it so that the start time is executionTime (not
      // executionTime + interval)

      time = this.executionTime - interval;
    }

    // if last execution time + interval is less or equal to current time. OR
    // if last execution time is in the future more than 1 hour, release the
    // trigger.

    if (curTime + (60 * 60 * 1000) < time) {
      this.lastExecutionTime = curTime;
      
      return true;
    } else if ((time + interval) <= curTime) {
      //    execution time ( the calculated, not the real) is stored to be put to
      // DB if execution is succesfull
      // this prevents the drift of the execution time */

      final long timeDifference = (curTime - time) % interval;
      this.lastExecutionTime = curTime - timeDifference;
      return true;
    }

    return false;

  }

}
