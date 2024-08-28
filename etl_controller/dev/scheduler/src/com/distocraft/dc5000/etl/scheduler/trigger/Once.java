package com.distocraft.dc5000.etl.scheduler.trigger;

import java.util.Date;

import com.distocraft.dc5000.etl.scheduler.Scheduler;
import com.distocraft.dc5000.etl.scheduler.TimeTrigger;

/**
 * @author savinen
 * 
 */
public class Once extends TimeTrigger {

  public Once() {
    super();
  }

  public boolean execute() throws Exception {

    update();

    // if last execution time is less or equal to current time

    if (((this.executionTime) <= (new Date()).getTime())
        && (this.status == null || (!this.status.equalsIgnoreCase(Scheduler.STATUS_EXECUTED)))) {
      return true;
    }

    return false;
  }

}
