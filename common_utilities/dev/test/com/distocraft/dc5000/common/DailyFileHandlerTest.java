package com.distocraft.dc5000.common;

import static org.junit.Assert.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for DailyFileHandler class in com.distrocraft.dc5000.common.
 * <br>
 * <br>
 * Testing if logdata is written into a file with timestamp of current date.
 * 
 * @author EJAAVAH
 * 
 */
public class DailyFileHandlerTest {

  private static final Exception exampleException = null;

  private static DailyFileHandler objUnderTest;

  private static Method selectLog;

  private static Field logName;

  private static File logDir;

  private static File logFile;

  private static String actual = null;

  private static String expected = null;

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM k:mm:ss");

  private static boolean removeCounter = false;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    try {
      objUnderTest = new DailyFileHandler("DFHlogger");
      Class DFH = objUnderTest.getClass();
      selectLog = DFH.getDeclaredMethod("selectLog", new Class[] {});
      logName = DFH.getDeclaredField("logName");
    } catch (Exception e) {
      e.printStackTrace();
    }
    selectLog.setAccessible(true);
    logName.setAccessible(true);

    // Creating the path to the logfile and the logfile itself
    logDir = new File("C:\\work\\engine");
    if (!logDir.exists()) {
      logDir.mkdirs();
      removeCounter = true;
    }
    DateFormatter df = new DateFormatter();
    String file = df.getCurrentDateAndYear("-");
    logFile = new File("C:\\work\\engine\\" + logName.get(logName) + file + ".log");
    if (!logDir.exists() || !logDir.isDirectory() || !logDir.canRead()) {
      throw new Exception("Can not access logDir " + logDir.getName());
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    objUnderTest = null;
    logFile.delete();
    if (removeCounter == true) {
      logDir.delete();
      File removeWorkDir = new File("C://work");
      removeWorkDir.delete();
    }
  }

  /**
   * Test if writeLine() method works with different inputs - writeline() method
   * writes log information to predefined file which is then read and compared
   * to the expected string.
   */
  @Test
  public void testWriteLineGeneralInputs() throws Exception {
    expected = sdf.format(new Date()) + " SEVERE DFHlogger This is an example line";
    objUnderTest.writeLine("This is an example line", 1000, exampleException);
    actual = new HelpClass().readFileToString(logFile);
    assertEquals(expected, actual);

    expected = sdf.format(new Date()) + " WARNING DFHlogger This is another example line";
    objUnderTest.writeLine("This is another example line", 900, exampleException);
    actual = new HelpClass().readFileToString(logFile);
    assertEquals(expected, actual);

    expected = sdf.format(new Date()) + " INFO DFHlogger This is yet another example line";
    objUnderTest.writeLine("This is yet another example line", 800, exampleException);
    actual = new HelpClass().readFileToString(logFile);
    assertEquals(expected, actual);
  }

  /**
   * Test that writeLine() method does not write anything to the logfile if the
   * log level is something else than the accepted values (800, 900 or 1000),
   * the filereader reads the last line of the file so the output should be the
   * last commited log from testWriteLineGeneralInputs() method.
   */
  @Test
  public void testWriteLineIllegalLevel() throws Exception {
    expected = sdf.format(new Date()) + " INFO DFHlogger This is yet another example line";
    objUnderTest.writeLine("This is an different example line", 5000, exampleException);
    actual = new HelpClass().readFileToString(logFile);
    assertEquals(expected, actual);
  }

  /**
   * Test if selectLog() method returns the logfile path in right form and it
   * exists.
   */
  @Test
  public void testSelectLog() throws Exception {
    assertEquals(logFile, selectLog.invoke(objUnderTest, new Object[] {}));
    assertEquals(true, logFile.exists());
  }

  // Making the test work with ant 1.6.5 and JUnit 4.x
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(DailyFileHandlerTest.class);
  }

}
