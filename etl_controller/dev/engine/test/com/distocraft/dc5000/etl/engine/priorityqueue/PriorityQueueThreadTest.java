package com.distocraft.dc5000.etl.engine.priorityqueue;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.common.Share;

/**
 * Tests for PriorityQueueThread com.distocraft.dc5000.etl.engine.priorityqueue.<br>
 * <br>
 * Testing priority queue handling. Sets are handed from priority queue to free
 * execution slot and then executed. Nothing is done if queue is on hold.
 * 
 * @author EJAAVAH
 * 
 */
public class PriorityQueueThreadTest {

  private static PriorityQueueThread objUnderTest;

  private static PriorityQueue pq;

  private static Connection con = null;

  private static Statement stmt;

  private static Logger testLog;

  public int executedSetsCounter = 0;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Setting up database tables to be used in testing
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    stmt = con.createStatement();
    stmt.execute("CREATE TABLE META_EXECUTION_SLOT_PROFILE (PROFILE_NAME VARCHAR(31), PROFILE_ID VARCHAR(31), "
        + "ACTIVE_FLAG VARCHAR(31))");
    stmt.execute("CREATE TABLE META_EXECUTION_SLOT (PROFILE_ID VARCHAR(31), SLOT_NAME VARCHAR(31), "
        + "SLOT_ID VARCHAR(31), ACCEPTED_SET_TYPES VARCHAR(31))");
    stmt.executeUpdate("INSERT INTO META_EXECUTION_SLOT_PROFILE VALUES('profilename', 'profileid', 'y')");
    stmt.executeUpdate("INSERT INTO META_EXECUTION_SLOT VALUES('profileid', 'slotname', '0', 'testset')");

    // Setting up StaticProperties object
    Properties properties = new Properties();
    properties.setProperty("PriorityQueue.maxAmountOfLoadersForSameTypeInQueue", "5");
    properties.setProperty("PriorityQueue.unremovableSetTypes", "all");
    StaticProperties.giveProperties(properties);
      Share.instance().add("execution_profile_max_memory_usage_mb", 512);

    // Initializing logger
    testLog = Logger.getLogger("etlengine.PriorityQueueThread");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stmt.execute("DROP TABLE META_EXECUTION_SLOT_PROFILE");
    stmt.execute("DROP TABLE META_EXECUTION_SLOT");
    // Cleaning up after all tests
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    // Initializing new PriorityQueue and setting poll interval to 0.5s for
    // quicker tests
    pq = new PriorityQueue();
    pq.setPollIntervall(200);

    // Initializing ExecutionSlotProfileHandler and setting a executionSlot for
    // it
    ExecutionSlotProfileHandler esph = new ExecutionSlotProfileHandler("jdbc:hsqldb:mem:testdb", "sa", "",
        "org.hsqldb.jdbcDriver");

    // Initializing PriorityQueueThread
    objUnderTest = new PriorityQueueThread(pq, esph);
  }

  @After
  public void tearDown() throws Exception {

    // Waiting for the WorkerStub threads to finish after each test
    Thread.sleep(1800);

    // Clening up after every test
    objUnderTest = null;
  }

  /**
   * Testing executing of sets in priority queue with one generic set.
   */
  @Test
  public void testRunWithOneGeneralInputSet() throws Exception {

    // Creating test set to be added to the priority queue
    EngineThread testSet = new EngineThread("testset", "testset", 5L, new WorkerStub(this), testLog);
    pq.addSet(testSet);

    // Starting PriorityQueueThread
    objUnderTest.start();

    // Waiting for a while for the set being moved to execution slot and
    // executed
    Thread.sleep(800);

    // Assert that PriorityQueueThread has put a set from the queue to
    // executionslot and EngineThread is running
    assertEquals(true, testSet.isAlive());

    // Waiting for the WorkerStub thread to finish and making sure right amount
    // of sets have been executed
    Thread.sleep(500);
    assertEquals(1, executedSetsCounter);
  }

  /**
   * Testing executing of sets in priority queue with multiple sets. Set with
   * highest priority and accepted type will be executed.
   */
  @Test
  public void testRunWithMultipleSetsRunHighestPriority() throws Exception {

    // Creating different test sets, one with second highest priority (testset2)
    // is executed (NOTE: that testset3 is of wrong type and will not be
    // executed)
    EngineThread testSetOne = new EngineThread("testset1", "testset", 5L, new WorkerStub(this), testLog);
    EngineThread testSetTwo = new EngineThread("testset2", "testset", 8L, new WorkerStub(this), testLog);
    EngineThread testSetThree = new EngineThread("testset3", "NotToBeExecuted", 14L, new WorkerStub(this), testLog);
    EngineThread testSetFour = new EngineThread("testset4", "testset", 4L, new WorkerStub(this), testLog);

    // Adding sets to queue
    pq.addSet(testSetOne);
    pq.addSet(testSetTwo);
    pq.addSet(testSetThree);
    pq.addSet(testSetFour);

    // Starting PriorityQueueThread
    objUnderTest.start();

    // Waiting for a while for the set with highest priority being moved to
    // execution slot and executed
    Thread.sleep(800);

    // Asserting that the set with highest priority is executed
    String actual = "Set 1 = " + testSetOne.isAlive() + ", Set 2 = " + testSetTwo.isAlive() + ", Set 3 = "
        + testSetThree.isAlive() + ", Set 4 = " + testSetFour.isAlive();
    String expected = "Set 1 = false, Set 2 = true, Set 3 = false, Set 4 = false";
    assertEquals(expected, actual);

    // Waiting for the WorkerStub thread to finish and making sure right amount
    // of sets have been executed
    Thread.sleep(500);
    assertEquals(1, executedSetsCounter);
  }

  /**
   * Testing executing of sets in priority queue with multiple sets. Checking if
   * all sets have been executed
   */
  @Test
  public void testRunWithMultipleSets() throws Exception {

    // Creating different test sets
    EngineThread testSetOne = new EngineThread("testset11", "testset", 7L, new WorkerStub(this), testLog);
    EngineThread testSetTwo = new EngineThread("testset22", "testset", 2L, new WorkerStub(this), testLog);
    EngineThread testSetThree = new EngineThread("testset33", "testset", 11L, new WorkerStub(this), testLog);

    // Adding sets to queue
    pq.addSet(testSetOne);
    pq.addSet(testSetTwo);
    pq.addSet(testSetThree);

    // Starting PriorityQueueThread
    objUnderTest.start();

    // Waiting for a while for the set being moved to execution slot and
    // executed
    Thread.sleep(3000);

    // Asserting that all three threads has been executed
    assertEquals(3, executedSetsCounter);
  }

  /**
   * Testing executing of sets in priority queue with one generic set.
   */
  @Test
  public void testRunWhenQueueIsOnHold() throws Exception {

    // Creating test set to be added to the priority queue
    EngineThread testSet = new EngineThread("testset", "testset", 10L, new WorkerStub(this), testLog);
    pq.addSet(testSet);

    // Setting priority queue on hold
    pq.hold();

    // Starting PriorityQueueThread
    objUnderTest.start();

    // Waiting for a while for the set being moved to execution slot and
    // executed
    Thread.sleep(800);

    // Assert that PriorityQueueThread has put a set from the queue to
    // executionslot and EngineThread is running
    assertEquals(false, testSet.isAlive());
    assertEquals(0, executedSetsCounter);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(PriorityQueueThreadTest.class);
  }
}

/**
 * This class is a stub for testing purposes. A set is moved from priority queue
 * to execution slot and instance of this object will be run.
 */
class WorkerStub extends Thread {

  private static PriorityQueueThreadTest counter;

  WorkerStub(PriorityQueueThreadTest pqtt) {
    counter = pqtt;
  }

  public void run() {
    try {
      // Sleeping for a while so assert can see this thread is running
      Thread.sleep(200);
      // Increasing counter every time set is executed
      counter.executedSetsCounter++;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
