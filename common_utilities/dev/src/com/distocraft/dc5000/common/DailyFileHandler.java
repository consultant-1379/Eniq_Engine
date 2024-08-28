package com.distocraft.dc5000.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.*;

/**
 * Created on 12.7.2005
 * This class is for rotating log-files. java.util.logging API is used for logging. 
 * @author vesterinen
 */
public class DailyFileHandler {

  private Logger logger = null;
  private StreamHandler handler = new StreamHandler();
  public static final String logName = "engine-";
  
  public DailyFileHandler(String classname){
    logger = Logger.getLogger(classname);
  }
  
  public void setLoggerClassName(String classname){
    logger = Logger.getLogger(classname);
  }
  
  /**
   * Writes log information in log file. java.util.logging is used.
   * @param line
   * @param severity - level of logging. Please refer to class <code>java.util.logging.Level</code>.
   * @param exception - if file isnt found
   */
  public synchronized void writeLine(String line, int severity, Exception exception){

    try {
      BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(selectLog(), true));
      // creating a stream handler
      handler = new StreamHandler(bout, new LogFormatter());
      // setting the handler to the logger
      handler.setLevel(Level.ALL);
      logger.addHandler(handler);
      logger.setLevel(Level.ALL);
      if (Level.WARNING.intValue() == severity)
        logger.warning(line);
      else if (Level.INFO.intValue() == severity)
        logger.info(line);
      else if (Level.SEVERE.intValue() == severity)
        logger.log(Level.SEVERE, line , exception);
      handler.close();
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "file not found." , e);
    }
   }

 
  /**
   * Selects log that is used at current date. Logs are named as angine-yyyy_mm_dd.log
   * @return
   */
  private File selectLog() {

    DateFormatter df = new DateFormatter();
    String file = df.getCurrentDateAndYear("-");
    
    File logFile = new File("C:\\work\\engine\\" + logName + file + ".log");
    return logFile;
  }

  //for testing purposes
  public static void main(String[] args) {
    DailyFileHandler d = new DailyFileHandler("dailyfile");
    d.setLoggerClassName(d.getClass().getName());
 
    d.writeLine("Testi2.. " + Math.random(), Level.WARNING.intValue(), new NullPointerException());
    d.writeLine("Testi1.. " + Math.random(), Level.SEVERE.intValue(), new NullPointerException());
    d.writeLine("Testi3.. " + Math.random(), Level.INFO.intValue(), new NullPointerException());
  }

}
