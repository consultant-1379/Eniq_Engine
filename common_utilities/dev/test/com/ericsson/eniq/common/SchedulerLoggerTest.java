package com.ericsson.eniq.common;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.distocraft.dc5000.common.HelpClass;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class SchedulerLoggerTest {

  private static String homeDir;

  private static Method rotateMethod;

  private static SchedulerLogger instance;

  private static LogRecord lR;

  private static String dstamp;

  private static Map<String, String> env = System.getenv();
  
  @BeforeClass
  public static void init() {
    final DateFormat form = new SimpleDateFormat("yyyy_MM_dd");
    final Date dat = new Date(10000);
    dstamp = form.format(dat);
    rotateMethod = null;
    homeDir = env.get("WORKSPACE");
    System.setProperty("LOG_DIR", homeDir);
    System.setProperty("SchedulerLogger.debug", "foobar");
    lR = new LogRecord(Level.parse("1000"), "Message");
    lR.setLoggerName("tp.Logger");
    lR.setMillis(10000);

    try {
      instance = new SchedulerLogger();
      final Class secretClass = instance.getClass();

      rotateMethod = secretClass.getDeclaredMethod("rotate", new Class[] { String.class, String.class, String.class });
      rotateMethod.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  /**
   * Test that log files exists
   * 
   */

  @Test
  public void testPublish() {
 
    instance.publish(lR);
    instance.flush();
    instance.close();

    final File logFile = new File(homeDir, File.separator + "scheduler" + File.separator + "error" + "-" + dstamp + ".log");
    final File logFile2 = new File(homeDir, File.separator + "scheduler" + File.separator + "Logger" + File.separator
        + "scheduler" + "-" + dstamp + ".log");
    
    final String expected = "01.01 01:00:10 10 SEVERE tp.Logger : Message";
    try {
      final String logActual = new HelpClass().readFileToString(logFile);
      final String log2Actual = new HelpClass().readFileToString(logFile2);
      
      assertEquals(expected, logActual);
      assertEquals(expected, log2Actual);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPublish() failed");
    }

    assertEquals(true, logFile.isFile());
    assertEquals(true, logFile2.isFile());

    logFile.delete();
    logFile2.delete();
  }

  @Test
  public void testRotate() {
    try {
      rotateMethod.invoke(instance, new Object[] { "Logger", "scheduler", "timestamp" });
      instance.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRotate() Failed");
    }
    
    final File logFile3 = new File(homeDir, File.separator + "scheduler" + File.separator + "Logger" + File.separator
        + "scheduler" + "-" + "timestamp" + ".log");
    
    assertEquals(true, logFile3.isFile());
    
    logFile3.delete();
  }
  
  @AfterClass
  public static void clean() {
    final File logDir = new File(homeDir, "scheduler");
    final File logDir2 = new File(logDir, "Logger");
    
    logDir2.delete();
    logDir.delete();
  }
  
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(SchedulerLoggerTest.class);
  }
}
