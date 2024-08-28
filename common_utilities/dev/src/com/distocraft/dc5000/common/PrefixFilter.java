package com.distocraft.dc5000.common;

import java.util.logging.Filter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class PrefixFilter implements Filter {

  private String prefix = "";

  public PrefixFilter() {
    
    LogManager manager = LogManager.getLogManager();

    prefix = manager.getProperty(PrefixFilter.class.getName() + ".prefix");
    if(prefix == null)
      prefix = "";
    
  }
  
  public PrefixFilter(String prefix) {
    this.prefix = prefix;
  }
  
  public boolean isLoggable(LogRecord record) {
    if (record != null && record.getLoggerName().startsWith(prefix))
      return true;
    else
      return false;
  }

}
