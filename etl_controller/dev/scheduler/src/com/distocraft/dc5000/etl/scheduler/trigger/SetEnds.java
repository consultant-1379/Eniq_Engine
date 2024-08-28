/*
 * Created on 3.2.2005
 *
 */
package com.distocraft.dc5000.etl.scheduler.trigger;

import java.util.HashMap;

import com.distocraft.dc5000.etl.engine.common.Tags;
import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.scheduler.EventTrigger;

/**
 * @author savinen
 * 
 */
public class SetEnds extends EventTrigger {

  /**
   * constructor
   * 
   */

  public SetEnds() {
    super();
  }

  /**
   * 
   * 
   * 
   */
  public boolean execute() throws Exception {

    update();
    HashMap tagMap = Tags.GetTagPairs("", "=", this.trigger_command);
    EngineAdmin admin = new EngineAdmin();
    admin.isSetRunning(new Long((String) tagMap.get("techpackID")), new Long((String) tagMap.get("setID")));
    return false;
  }

}
