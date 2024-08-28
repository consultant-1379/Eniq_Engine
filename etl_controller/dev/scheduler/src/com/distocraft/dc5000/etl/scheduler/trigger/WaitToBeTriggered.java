/*
 * Created on 3.2.2005
 *
 */
package com.distocraft.dc5000.etl.scheduler.trigger;

import com.distocraft.dc5000.etl.scheduler.TimeTrigger;

/**
 * @author savinen
 * 
 * does nothing, just waits to be Triggered by direct commnad to the scheduler
 * 
 * 
 * 
 */
public class WaitToBeTriggered extends TimeTrigger {

  /**
   * constructor
   * 
   */

  public WaitToBeTriggered() {
    super();
  }

  /**
   * 
   * 
   * 
   */
  public boolean execute() throws Exception {

    return false;
  }

}
