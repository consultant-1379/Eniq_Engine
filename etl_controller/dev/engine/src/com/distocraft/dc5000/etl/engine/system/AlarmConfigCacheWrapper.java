/**
 * 
 */
package com.distocraft.dc5000.etl.engine.system;

import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.etl.alarm.AlarmConfig;
import com.ericsson.eniq.etl.alarm.RockAlarmConfigCache;

/**
 * @author eheijun
 * 
 */
public class AlarmConfigCacheWrapper {

  private static Logger logger = Logger.getLogger("etlengine.AlarmConfigCache");
  
  private static Boolean allowRevalidate = true;

  public static void revalidate(RockFactory dwhrep) {
    if (allowRevalidate) {
      RockAlarmConfigCache.initialize(dwhrep, logger);
      RockAlarmConfigCache.revalidate();
      logger.info("AlarmConfigCache revalidated.");
    }
  }

  public static AlarmConfig getInstance() {
    return RockAlarmConfigCache.getInstance();
  }

  
  public static Boolean getAllowRevalidate() {
    return allowRevalidate;
  }

  public static void setAllowRevalidate(Boolean newValue) {
    allowRevalidate = newValue;
  }
  
}
