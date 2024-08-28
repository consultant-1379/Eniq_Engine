package com.distocraft.dc5000.etl.scheduler.trigger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;

/**
 * Tests for Interval class in com.distocraft.dc5000.etl.scheduler.trigger.<br>
 * <br>
 * Testing set triggering with interval.
 * 
 * @author EJAAVAH
 * 
 */
public class IntervalTest {

  private static Interval objUnderTest;

  private static Meta_schedulings schedulings;

  private static Timestamp tstmp;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    // Meta_schedulings for initializing Interval time
    schedulings = new Meta_schedulings(null);
    schedulings.setInterval_hour(1L);
    schedulings.setInterval_min(1L);

    try {
      objUnderTest = new Interval();
      objUnderTest.init(schedulings, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    schedulings = null;
    tstmp = null;
    objUnderTest = null;
  }

  /**
   * Testing if set is triggered on first time it is run and when the triggering
   * interval (1h) is exceeded. When trigger is released execute method in
   * Interval class returns true, otherwise false.
   */
  @Test
  public void testIntervalExecute() throws Exception {
    // Testing if the trigger is relesed if the last execution time is more than
    // an hour in the future
    tstmp = new Timestamp(new Date().getTime() + 3600050);
    schedulings.setLast_execution_time(tstmp);
    assertEquals(true, objUnderTest.execute());

    // Trigger should not be released when last execute time is less than an
    // hour in the future
    tstmp = new Timestamp(new Date().getTime() + 3599950);
    schedulings.setLast_execution_time(tstmp);
    assertEquals(false, objUnderTest.execute());

    // Testing if interval is triggered on the first execution (last execution =
    // null)
    tstmp = null;
    schedulings.setLast_execution_time(tstmp);
    assertEquals(true, objUnderTest.execute());
  }

  /**
   * Testing a case when Interval hour and minute in schedulings are initialized
   * with 0 value. This will cause ArithmeticException as division by zero is
   * tried.
   */
  @Test
  public void testIntervalException() throws Exception {
    // Initializing schedulings interval hour and minute with 0 value
    schedulings.setInterval_hour(0L);
    schedulings.setInterval_min(0L);
    tstmp = new Timestamp(new Date().getTime());
    schedulings.setLast_execution_time(tstmp);
    try {
      objUnderTest.execute();
      fail("Exception expected - Division by zero in Interval class");
    } catch (ArithmeticException ae) {
      // Test passed - ArithmeticException expected
    } catch (Exception e) {
      fail("Unexpected error occured - ArithmeticException expected\n" + e);
    }
  }

  // Making the test work with ant 1.6.5 and JUnit 4.x
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(IntervalTest.class);
  }
}
