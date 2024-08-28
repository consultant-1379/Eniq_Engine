package com.distocraft.dc5000.etl.engine.priorityqueue;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.main.EngineThread;

/**
 * Tests for PriorityQueue com.distocraft.dc5000.etl.engine.priorityqueue.<br>
 * <br>
 * Testing priority queue handling.
 * 
 * @author EJAAVAH
 * 
 */
public class PriorityQueueTest {

  private static PriorityQueue objUnderTest;

  private static EngineThread engineSet;

  private static Field pollInterval;

  private static Field maxPriorityLevel;

  private static Field priorityQueue;

  private static Field active;

  private static Field isClosed;

  private static Field IDPool;

  private static Field maxAmountOfLoadersForSameType;

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    // Clearing up global variables and objects after all tests are done
    pollInterval = null;
    maxPriorityLevel = null;
    priorityQueue = null;
    active = null;
    isClosed = null;
    IDPool = null;
    maxAmountOfLoadersForSameType = null;
    objUnderTest = null;
  }

  @Before
  public void setUp() throws Exception {

    // Setting up StaticProperties object
    Properties properties = new Properties();
    properties.setProperty("PriorityQueue.maxAmountOfLoadersForSameTypeInQueue", "5");
    properties.setProperty("PriorityQueue.unremovableSetTypes", "setType3,setType5");
    StaticProperties.giveProperties(properties);

    // Initializing often used objects
    objUnderTest = new PriorityQueue();
    engineSet = new EngineThread("testSet", 5L, null, null);

    // Reflecting often used private variables from PriorityQueue
    Class pq = objUnderTest.getClass();
    pollInterval = pq.getDeclaredField("pollInterval");
    maxPriorityLevel = pq.getDeclaredField("maxPriorityLevel");
    priorityQueue = pq.getDeclaredField("priorityQueue");
    active = pq.getDeclaredField("active");
    isClosed = pq.getDeclaredField("isClosed");
    IDPool = pq.getDeclaredField("IDPool");
    maxAmountOfLoadersForSameType = pq.getDeclaredField("maxAmountOfLoadersForSameType");
    pollInterval.setAccessible(true);
    maxPriorityLevel.setAccessible(true);
    priorityQueue.setAccessible(true);
    active.setAccessible(true);
    isClosed.setAccessible(true);
    IDPool.setAccessible(true);
    maxAmountOfLoadersForSameType.setAccessible(true);
  }

  @After
  public void tearDown() throws Exception {

    // Clearing often used objects and variables
    pollInterval = null;
    maxPriorityLevel = null;
    priorityQueue = null;
    active = null;
    isClosed = null;
    IDPool = null;
    maxAmountOfLoadersForSameType = null;
    objUnderTest = null;
  }

  /**
   * Testing priority queue resetting. ResetPriorityQueue() method takes
   * pollInterval and maxPriorityLevel as parameters and changes them
   * accordingly.
   */
  @Test
  public void testResetPriorityQueue() throws Exception {

    // Asserting that pollInterval and maxPriorityLevel variables are reset to
    // given values when resetPriorityQueue() method is called
    objUnderTest.resetPriorityQueue(4L, 2);
    String actual = "pollInterval: " + pollInterval.getLong(objUnderTest) + ", maxPriorityLevel: "
        + maxPriorityLevel.getInt(objUnderTest);
    String expected = "pollInterval: 4, maxPriorityLevel: 2";
    assertEquals(expected, actual);
  }

  /**
   * Testing set adding to priority queue with generic input.
   */
  @Test
  public void testAddSet() throws Exception {

    // Initializing local vector object with actual priorityQueue variable
    Vector actual = (Vector) priorityQueue.get(objUnderTest);

    // Making sure that the actual vector is empty before adding any sets into
    // priority queue
    if (actual.isEmpty()) {

      // Adding testset to queue
      objUnderTest.addSet(engineSet);
      actual = (Vector) priorityQueue.get(objUnderTest);

      // Asserting that testSet is added to priority queue when addSet() is
      // called - size should return 1 element
      assertEquals(1, actual.size());
    } else {
      fail("Test Failed - actual vector already has elements in it");
    }
  }

  /**
   * Testing set adding to priority queue with null object.
   */
  @Test
  public void testAddSetWithNullValues() throws Exception {

    // Initializing engineSet object with null value
    engineSet = null;

    // Checking if exception is catched when null object is given as parameter
    try {
      objUnderTest.addSet(engineSet);
      fail("Test Failed - Exception expected as engineSet object given as parameter was null");
    } catch (Exception e) {
      // Test passed - Exception caught
    }

  }

  /**
   * Testing set removing from priority queue with generic input.
   */
  @Test
  public void testRemoveSet() throws Exception {

    // Adding testset into priorityqueue in order to remove it
    Vector actual = new Vector();
    actual.add(engineSet);
    priorityQueue.set(objUnderTest, actual);

    // Making sure there is exactly one added item in actual vector
    if (actual.size() == 1) {

      // Calling the removeSet() method and asserting that testset has been
      // removed
      objUnderTest.RemoveSet(engineSet);
      assertEquals(0, actual.size());
    } else {
      fail("Test Failed - actual vector is empty or it has more than one set");
    }
  }

  /**
   * Testing set removing from priority queue with null object.
   */
  @Test
  public void testRemoveSetWithNullValue() throws Exception {

    // Initializing engineSet object with null value
    engineSet = null;

    // Checking if exception is catched when null object is given as parameter
    try {
      objUnderTest.RemoveSet(engineSet);
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Testing priority setting with generic input.
   */
  @Test
  public void testChangeSetsPriority() throws Exception {

    // Asserting that priority is changed and the method returns true
    assertEquals(true, objUnderTest.ChangeSetsPriority(engineSet, 10L));
    assertEquals(10L, (long) engineSet.getSetPriority());
  }

  /**
   * Testing priority setting with priority value which is over the allowed max
   * priority value.
   */
  @Test
  public void testChangeSetsPriorityWithOverMaxPriorityParameter() throws Exception {

    // Asserting that priority stays the same (5 as it is when engineSet is
    // initialized) and the method returns false
    assertEquals(false, objUnderTest.ChangeSetsPriority(engineSet, 200L));
    assertEquals(5L,  (long)engineSet.getSetPriority());
  }

  /**
   * Testing priority setting with priority value which is under the allowed min
   * priority value.
   */
  @Test
  public void testChangeSetsPriorityWithUnderMinPriorityParameter() throws Exception {

    // Asserting that priority stays the same (5 as it is when engineSet is
    // initialized) and the method returns false
    assertEquals(false, objUnderTest.ChangeSetsPriority(engineSet, -12L));
    assertEquals(5L, (long) engineSet.getSetPriority());
  }

  /**
   * Testing priority setting with null engineSet object, exception should be
   * thrown.
   */
  @Test
  public void testChangeSetsPriorityWithNullSet() throws Exception {

    // Initializing engineSet object with null value
    engineSet = null;

    // Checking if exception is catched when null object is given as parameter
    try {
      objUnderTest.ChangeSetsPriority(engineSet, 6);
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Test for getting list of all available sets from priority queue.
   */
  @Test
  public void testGetAvailable() throws Exception {

    // Adding active and inactive testsets into priorityqueue in order to get a
    // list of them
    Vector testdata = new Vector();
    for (int i = 0; i < 5; i++) {
      // Initializing new EngineThread object
      engineSet = new EngineThread("testSet" + i, i * 2L, null, null);

      // Setting few inactive sets
      if (i == 2 || i == 4) {
        Class et = engineSet.getClass();
        Field active = et.getDeclaredField("active");
        active.setAccessible(true);
        active.setBoolean(engineSet, false);
      }
      testdata.add(engineSet);
    }
    priorityQueue.set(objUnderTest, testdata);

    // Invoking tested method and asserting that returned enumeration object has
    // all the necessary objects
    Enumeration actualList = objUnderTest.getAvailable();
    int j = 0;
    String[] testSetNames = { "testSet0", "testSet1", "testSet3" };
    while (actualList.hasMoreElements()) {
      EngineThread actual = (EngineThread) actualList.nextElement();
      assertEquals(testSetNames[j], actual.getSetName());
      j++;
    }
  }

  /**
   * Testing if null is returned when priority queue is empty and getAvailable()
   * method is called.
   */
  @Test
  public void testGetAvailableWithEmptyQueue() throws Exception {

    // asserting that null is returnedwhen theres no items in priority queue
    assertEquals(null, objUnderTest.getAvailable());
  }

  /**
   * Testing if exception is thrown when priority queue is initialized with null
   * and getAvailable() method is called.
   */
  @Test
  public void testGetAvailableWithNullPriorityQueue() throws Exception {

    // Initializing priority queue with null
    priorityQueue.set(objUnderTest, null);

    // Checking if exception thrown
    try {
      objUnderTest.getAvailable();
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Test for getting the amount of available sets in priority queue.
   */
  @Test
  public void testGetNumberOfAvailable() throws Exception {

    // Adding active and inactive testsets into priorityqueue in order to get a
    // list of them
    Vector testdata = new Vector();
    for (int i = 0; i < 5; i++) {
      // Initializing new EngineThread object
      engineSet = new EngineThread("testSet" + i, i * 2L, null, null);

      // Setting few inactive sets
      if (i == 2 || i == 4) {
        Class et = engineSet.getClass();
        Field active = et.getDeclaredField("active");
        active.setAccessible(true);
        active.setBoolean(engineSet, false);
      }
      testdata.add(engineSet);
    }
    priorityQueue.set(objUnderTest, testdata);

    // Asserting that correct value is returned
    assertEquals(3, objUnderTest.getNumberOfAvailable());
  }

  /**
   * Testing if 0 is returned when priority queue is empty and
   * getNumberOfAvailable() method is called.
   */
  @Test
  public void testGetNumberOfAvailableWithEmptyQueue() throws Exception {

    // asserting that null is returnedwhen theres no items in priority queue
    assertEquals(0, objUnderTest.getNumberOfAvailable());
  }

  /**
   * Testing if exception is thrown when priority queue is initialized with null
   * and getNumberOfAvailable() method is called.
   */
  @Test
  public void testGetNumberOfAvailableWithNullPriorityQueue() throws Exception {

    // Initializing priority queue with null
    priorityQueue.set(objUnderTest, null);

    // Checking if exception thrown
    try {
      objUnderTest.getNumberOfAvailable();
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Test for getting total amount of sets in priority queue.
   */
  @Test
  public void testGetNumberOfSetsInQueue() throws Exception {

    // Adding active and inactive testsets into priorityqueue in order to get a
    // list of them
    Vector testdata = new Vector();
    for (int i = 0; i < 5; i++) {
      // Initializing new EngineThread object
      engineSet = new EngineThread("testSet" + i, i * 2L, null, null);

      // Setting few inactive sets
      if (i == 2 || i == 4) {
        Class et = engineSet.getClass();
        Field active = et.getDeclaredField("active");
        active.setAccessible(true);
        active.setBoolean(engineSet, false);
      }
      testdata.add(engineSet);
    }
    priorityQueue.set(objUnderTest, testdata);

    // Asserting that correct value is returned
    assertEquals(5, objUnderTest.getNumberOfSetsInQueue());
  }

  /**
   * Testing if 0 is returned when priority queue is empty and
   * getNumberOfAvailable() method is called.
   */
  @Test
  public void testGetNumberOfSetsInQueueWithEmptyQueue() throws Exception {

    // asserting that null is returnedwhen theres no items in priority queue
    assertEquals(0, objUnderTest.getNumberOfSetsInQueue());
  }

  /**
   * Testing if exception is thrown when priority queue is initialized with null
   * and getNumberOfAvailable() method is called.
   */
  @Test
  public void testGetNumberOfSetsInQueueWithNullPriorityQueue() throws Exception {

    // Initializing priority queue with null
    priorityQueue.set(objUnderTest, null);

    // Checking if exception thrown
    try {
      objUnderTest.getNumberOfSetsInQueue();
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Test for getting list of all sets (both available and sets on hold) from
   * priority queue.
   */
  @Test
  public void testGetSetsInQueue() throws Exception {

    // Adding active and inactive testsets into priorityqueue in order to get a
    // list of them
    Vector testdata = new Vector();
    for (int i = 0; i < 5; i++) {
      // Initializing new EngineThread object
      engineSet = new EngineThread("testSet" + i, i * 2L, null, null);

      // Setting few inactive sets
      if (i == 2 || i == 4) {
        Class et = engineSet.getClass();
        Field active = et.getDeclaredField("active");
        active.setAccessible(true);
        active.setBoolean(engineSet, false);
      }
      testdata.add(engineSet);
    }
    priorityQueue.set(objUnderTest, testdata);

    // Invoking tested method and asserting that returned enumeration object has
    // all the necessary objects
    Enumeration actualList = objUnderTest.getSetsInQueue();
    int j = 0;
    while (actualList.hasMoreElements()) {
      EngineThread actual = (EngineThread) actualList.nextElement();
      assertEquals("testSet" + j, actual.getSetName());
      j++;
    }
  }

  /**
   * Testing if null is returned when priority queue is empty and
   * getSetsInQueue() method is called.
   */
  @Test
  public void testGetSetsInQueueWithEmptyQueue() throws Exception {

    // asserting that null is returnedwhen theres no items in priority queue
    assertEquals(null, objUnderTest.getSetsInQueue());
  }

  /**
   * Testing if exception is thrown when priority queue is initialized with null
   * and getSetsInQueue() method is called.
   */
  @Test
  public void testGetSetsInQueueWithNullPriorityQueue() throws Exception {

    // Initializing priority queue with null
    priorityQueue.set(objUnderTest, null);

    // Checking if exception thrown
    try {
      objUnderTest.getSetsInQueue();
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Testing sorting of priority queue. Elements in the queue are sorted in
   * ascending order.
   */
  @Test
  public void testSort() throws Exception {

    // Initializing vector with sets which will be put into priority queue
    Vector testData = new Vector();
    testData.add(new EngineThread("testSet3", 4L, null, null));
    testData.add(new EngineThread("testSet4", 1L, null, null));
    testData.add(new EngineThread("testSet1", 9L, null, null));
    testData.add(new EngineThread("testSet0", 15L, null, null));
    testData.add(new EngineThread("testSet2", 7L, null, null));

    // Adding sets to priorityqueue
    priorityQueue.set(objUnderTest, testData);

    // Sorting the queue
    objUnderTest.sort();

    // Asserting that the queue is in ascending order
    Vector actualVector = (Vector) priorityQueue.get(objUnderTest);
    for (int i = 0; i < actualVector.size(); i++) {
      EngineThread actual = (EngineThread) actualVector.elementAt(i);
      assertEquals("testSet" + i, actual.getSetName());
    }
  }

  /**
   * Testing if exception is thrown when priority queue has null element.
   */
  @Test
  public void testSortwithNullValueInQueue() throws Exception {

    // Initializing vector with sets which will be put into priority queue
    Vector testData = new Vector();
    testData.add(new EngineThread("testSet1", 1L, null, null));
    testData.add(new EngineThread("testSet2", 3L, null, null));
    testData.add(null);

    // Adding sets to priorityqueue
    priorityQueue.set(objUnderTest, testData);

    // Checking if exception is thrown when priority queue has null values
    try {
      objUnderTest.sort();
      fail("Test Failed - NullPointerException expected as priorityQueue has null value");
    } catch (NullPointerException npe) {
      // Test Passed - NullPointerException expected
    } catch (Exception e) {
      fail("Test Failed - Unexpected Exception was thrown \n" + e);
    }
  }

  /**
   * Testing time limit checking. If set has exceeded given time limit its
   * priority will be increased by one.
   */
  @Test
  public void testCheckTimeLimitIncreasePriority() throws Exception {

    // Making sure the set time limit in the queue has been exceeded
    Date date = new Date();
    date.setTime(1000);

    // Setting time value for set and adding it to the priority queue
    engineSet.setChangeDate(date);
    Vector testData = new Vector();
    testData.add(engineSet);
    priorityQueue.set(objUnderTest, testData);

    // Invoking checkTimeLimitIncreasePriority() object and asserting that the
    // priority has been increased by one (from 5 to 6)
    objUnderTest.checkTimeLimit();
    assertEquals(6L, (long) engineSet.getSetPriority());
  }

  /**
   * Testing if set is removed from the priority queue when its priority value
   * is increased over max limit.
   */
  @Test
  public void testCheckTimeLimitRemoveFromQueue() throws Exception {

    // Making sure the set time limit in the queue has been exceeded
    Date date = new Date();
    date.setTime(1000);

    // Setting time value and priority for set and adding it to the priority
    // queue
    engineSet.setChangeDate(date);
    engineSet.setSetPriority(15L);
    Vector testData = new Vector();
    testData.add(engineSet);
    priorityQueue.set(objUnderTest, testData);
    Vector actualQueue = (Vector) priorityQueue.get(objUnderTest);

    // Making sure theres exactly one element in the vector
    if (actualQueue.size() == 1) {
      // Asserting that set is dropped from the queue
      objUnderTest.checkTimeLimit();
      assertEquals(0, actualQueue.size());
    } else {
      fail("Test Failed - there were no sets or more than one in the priority queue");
    }
  }

  /**
   * Testing if set is removed from the priority queue if there is null set in
   * the list or if it has null QueueTimeLimit or priority.
   */
  @Test
  public void testCheckTimeLimitWithNullvalues() throws Exception {

    // Making sure the set time limit in the queue has been exceeded
    Date date = new Date();
    date.setTime(1000);
    
    // Set with general input
    EngineThread testETone = new EngineThread("testSet11", 7L, null, null);
    testETone.setChangeDate(date);
    
    // Null initialized set
    EngineThread testETtwo = null;
    
    // Set with null priority set
    EngineThread testETthree = new EngineThread("testSet33", null, null, null);
    testETthree.setChangeDate(date);
    
    // Set with null queueTimeLimit (reflected)
    EngineThread testETfour = new EngineThread("testSet44", 8L, null, null);
    testETfour.setChangeDate(date);
    Class testET = testETfour.getClass();
    Field queueTimeLimit = testET.getDeclaredField("queueTimeLimit");
    queueTimeLimit.setAccessible(true);
    queueTimeLimit.set(testETfour, null);

    // Adding sets to vector and adding it to queue
    Vector testData = new Vector();
    testData.add(testETone);
    testData.add(testETtwo);
    testData.add(testETthree);
    testData.add(testETfour);
    priorityQueue.set(objUnderTest, testData);

    // Initializing actual queue for testing
    Vector actualQueue = (Vector) priorityQueue.get(objUnderTest);

    // Making sure there is exactly four elements in the priority queue
    if (actualQueue.size() == 4) {
      // asserting that sets with specific null values are removed
      objUnderTest.checkTimeLimit();
      assertEquals(1, actualQueue.size());
    } else {
      fail("Test Failed - There were more or less than four elements in the priority queue");
    }
  }

  /**
   * Testing PollInterval value setting and getting.
   */
  @Test
  public void testSetAndGetPollIntervall() throws Exception {

    // Setting PollInterval value and asserting that get method returns that
    // value
    objUnderTest.setPollIntervall(100L);
    assertEquals(100L, objUnderTest.getPollIntervall());
  }

  /**
   * Testing Active value returning.
   */
  @Test
  public void testIsActive() throws Exception {

    active.setBoolean(objUnderTest, true);
    assertEquals(true, objUnderTest.isActive());

    active.setBoolean(objUnderTest, false);
    assertEquals(false, objUnderTest.isActive());
  }

  /**
   * Testing putting queue on hold (active variable set to false).
   */
  @Test
  public void testHold() throws Exception {

    objUnderTest.hold();
    assertEquals(false, active.getBoolean(objUnderTest));
  }

  /**
   * Testing restarting queue (active variable set to true).
   */
  @Test
  public void testRestart() throws Exception {

    objUnderTest.restart();
    assertEquals(true, active.getBoolean(objUnderTest));
  }

  /**
   * Testing if queue is closed (isClosed variable set to true).
   */
  @Test
  public void testCloseQueue() throws Exception {

    objUnderTest.closeQueue();
    assertEquals(true, isClosed.getBoolean(objUnderTest));
  }

  /**
   * Testing if queue is opened (isClosed variable set to false).
   */
  @Test
  public void testOpenQueue() throws Exception {

    objUnderTest.openQueue();
    assertEquals(false, isClosed.getBoolean(objUnderTest));
  }

  /**
   * Testing isClosed value returning.
   */
  @Test
  public void testIsQueueClosed() throws Exception {

    isClosed.setBoolean(objUnderTest, true);
    assertEquals(true, objUnderTest.isQueueClosed());

    isClosed.setBoolean(objUnderTest, false);
    assertEquals(false, objUnderTest.isQueueClosed());
  }

  /**
   * Testing if shutdownmarker is added to the queue.
   */
  @Test
  public void testAddShutdownMarker() throws Exception {

    // Invoking the tested method and checking if shutdownmarker is added to the
    // queue
    objUnderTest.addShutdownMarker("testShutDownMarker", 2L);
    Vector actualQueue = (Vector) priorityQueue.get(objUnderTest);

    // Making sure theres exactly one element in the vector
    if (actualQueue.size() == 1) {
      EngineThread actualSet = (EngineThread) actualQueue.get(0);
      String actual = "SetName: " + actualSet.getSetName() + ", SetPri: " + actualSet.getSetPriority();
      String expected = "SetName: testShutDownMarker, SetPri: 2";
      assertEquals(expected, actual);
    } else {
      fail("Test Failed - Queue was empty or included more than one element");
    }
  }

  /**
   * Testing if ID value is added to IDPool vector.
   */
  @Test
  public void testPushID() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method pushID = pq.getDeclaredMethod("pushID", new Class[] { Long.class });
    pushID.setAccessible(true);

    // Making sure IDPool vector is empty
    Vector actualPool = (Vector) IDPool.get(objUnderTest);
    if (actualPool.isEmpty()) {
      // Checking if ID is added to the vector when pushID() method is invoked
      pushID.invoke(objUnderTest, new Object[] { 8L });
      assertEquals(1, actualPool.size());
    } else {
      fail("Test Failed - IDPool was not empty");
    }
  }

  /**
   * Testing that doublicate ID values are not added to IDPool vector.
   */
  @Test
  public void testPushIDTwiceWithSameValue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method pushID = pq.getDeclaredMethod("pushID", new Class[] { Long.class });
    pushID.setAccessible(true);

    // Adding ID value to IDPool
    pushID.invoke(objUnderTest, new Object[] { 101L });

    // Making sure IDPool vector has one item
    Vector actualPool = (Vector) IDPool.get(objUnderTest);
    if (actualPool.size() == 1) {
      // Checking that the same ID is not added to the vector twice when
      // pushID() method is invoked
      pushID.invoke(objUnderTest, new Object[] { 101L });
      assertEquals(1, actualPool.size());
    } else {
      fail("Test Failed - IDPool was empty or had more than one element");
    }
  }

  /**
   * Testing if first element is moved from IDPool vector when popID() method is
   * called.
   */
  @Test
  public void testPopID() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method popID = pq.getDeclaredMethod("popID", new Class[] {});
    Method pushID = pq.getDeclaredMethod("pushID", new Class[] { Long.class });
    popID.setAccessible(true);
    pushID.setAccessible(true);

    // Adding ID value to IDPool which will later be removed
    pushID.invoke(objUnderTest, new Object[] { 88L });

    // Making sure IDPool vector has one item
    Vector actualPool = (Vector) IDPool.get(objUnderTest);
    if (actualPool.size() == 1) {
      // Checking that the ID is removed from IDPool
      assertEquals(88L, popID.invoke(objUnderTest, new Object[] {}));
      assertEquals(0, actualPool.size());
    } else {
      fail("Test Failed - IDPool was empty or had more than one element");
    }
  }

  /**
   * Testing element removing from empty IDPool. Nothing is removed but new ID
   * value is created.
   */
  @Test
  public void testPopIDWithEmptyIDPool() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method popID = pq.getDeclaredMethod("popID", new Class[] {});
    popID.setAccessible(true);

    // Making sure IDPool vector is empty
    Vector actualPool = (Vector) IDPool.get(objUnderTest);
    if (actualPool.isEmpty()) {
      // Checking that new ID value is created
      assertEquals(0L, popID.invoke(objUnderTest, new Object[] {}));
      assertEquals(0, actualPool.size());
    } else {
      fail("Test Failed - IDPool was not empty");
    }
  }

  /**
   * Testing if isSetAllowed() method returns true when tested set is not
   * "Loader" type.
   */
  @Test
  public void testIsSetAllowedTrue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method isSetAllowed = pq.getDeclaredMethod("isSetAllowed", new Class[] { EngineThread.class });
    isSetAllowed.setAccessible(true);

    // Asserting that false is returned
    assertEquals(true, isSetAllowed.invoke(objUnderTest, engineSet));
  }

  /**
   * Testing if isSetAllowed() method returns false when tested set is of
   * "Loader" type and maxAmountOfLoadersForSameType value is greater than -1.
   */
  @Test
  public void testIsSetAllowedFalse() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method isSetAllowed = pq.getDeclaredMethod("isSetAllowed", new Class[] { EngineThread.class });
    isSetAllowed.setAccessible(true);

    // Initializing new Loader set and setting maxAmountOfLoadersForSameType
    // varible to 1 (the amount of same type sets allowed)
    EngineThread testSet = new EngineThread("Loader", 9L, null, null);
    maxAmountOfLoadersForSameType.setInt(objUnderTest, 1);

    // Adding testSet to vector which will then be added to priorityqueue
    Vector testData = new Vector();
    testData.add(testSet);
    priorityQueue.set(objUnderTest, testData);

    // Asserting that false is returned
    assertEquals(false, isSetAllowed.invoke(objUnderTest, testSet));
  }

  /**
   * Testing if false is returned when there are no more sets of the same type
   * than allowed.
   */
  @Test
  public void testDoesMaxAmountExceedFalse() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method doesMaxAmountExceed = pq.getDeclaredMethod("doesMaxAmountExceed", new Class[] { EngineThread.class });
    doesMaxAmountExceed.setAccessible(true);

    // Asserting that false is returned
    assertEquals(false, doesMaxAmountExceed.invoke(objUnderTest, engineSet));
  }

  /**
   * Testing if false is returned when there are no more sets of the same type
   * than allowed.
   */
  @Test
  public void testDoesMaxAmountExceedTrue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method doesMaxAmountExceed = pq.getDeclaredMethod("doesMaxAmountExceed", new Class[] { EngineThread.class });
    doesMaxAmountExceed.setAccessible(true);

    // Initializing new Loader set and setting maxAmountOfLoadersForSameType
    // varible to 1 (the amount of same type sets allowed)
    EngineThread testSet = new EngineThread("Loader", 9L, null, null);
    maxAmountOfLoadersForSameType.setInt(objUnderTest, 1);

    // Adding testSet to vector which will then be added to priorityqueue
    Vector testData = new Vector();
    testData.add(testSet);
    priorityQueue.set(objUnderTest, testData);

    // Asserting that false is returned
    assertEquals(true, doesMaxAmountExceed.invoke(objUnderTest, testSet));
  }

  /**
   * Testing if GetMaxAmountOfLoadersForSameType() method returns correct value
   * parsed from static properties.
   */
  @Test
  public void testGetMaxAmountOfLoadersForSameType() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method getMaxAmountOfLoadersForSameType = pq.getDeclaredMethod("getMaxAmountOfLoadersForSameType", new Class[] {});
    getMaxAmountOfLoadersForSameType.setAccessible(true);

    // Asserting that getMaxAmountOfLoadersForSameType variable is initialized
    // with the value stated in static properties (in BeforeClass())
    assertEquals(5, getMaxAmountOfLoadersForSameType.invoke(objUnderTest, new Object[] {}));
  }

  /**
   * Testing if GetMaxAmountOfLoadersForSameType() method returns -1 when
   * maxAmountOfLoadersForSameTypeInQueue variable is 0 in static properties.
   */
  @Test
  public void testGetMaxAmountOfLoadersForSameTypeWithZeroValue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method getMaxAmountOfLoadersForSameType = pq.getDeclaredMethod("getMaxAmountOfLoadersForSameType", new Class[] {});
    getMaxAmountOfLoadersForSameType.setAccessible(true);

    // Setting up StaticProperties object with invalid value
    Properties properties = new Properties();
    properties.setProperty("PriorityQueue.maxAmountOfLoadersForSameTypeInQueue", "0");
    StaticProperties.giveProperties(properties);

    // Asserting that -1 is returned
    assertEquals(-1, getMaxAmountOfLoadersForSameType.invoke(objUnderTest, new Object[] {}));

  }

  /**
   * Testing if exception is thrown when GetMaxAmountOfLoadersForSameType()
   * method tries to read invalid static properties.
   */
  @Test
  public void testGetMaxAmountOfLoadersForSameTypeWithInvalidValue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method getMaxAmountOfLoadersForSameType = pq.getDeclaredMethod("getMaxAmountOfLoadersForSameType", new Class[] {});
    getMaxAmountOfLoadersForSameType.setAccessible(true);

    // Setting up StaticProperties object with invalid value
    Properties properties = new Properties();
    properties.setProperty("PriorityQueue.maxAmountOfLoadersForSameTypeInQueue", "text");
    StaticProperties.giveProperties(properties);

    // Checking if exception is catched when trying to convert string to int
    try {
      getMaxAmountOfLoadersForSameType.invoke(objUnderTest, new Object[] {});
      fail("Test Failed - Exception expected as static properties could not have been parsed");
    } catch (Exception e) {
      // Test Passed - Exception thrown
    }
  }

  /**
   * Testing if unremovable set types are read from static properties.
   */
  @Test
  public void testGetUnremovableSetTypes() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method getUnremovableSetTypes = pq.getDeclaredMethod("getUnremovableSetTypes", new Class[] {});
    getUnremovableSetTypes.setAccessible(true);

    // Initializing testVector with vector returned from
    // getUnremovableSetTypes() method
    Vector actualList = (Vector) getUnremovableSetTypes.invoke(objUnderTest, new Object[] {});

    // Making sure there are exactly two elements in the vector
    if (actualList.size() == 2) {
      String actual = actualList.get(0) + "," + actualList.get(1);
      String expected = "setType3,setType5";
      assertEquals(expected, actual);
    } else {
      fail("Test Failed - There were more or less than two elements in the vector");
    }
  }

  /**
   * Testing if doWeRemove() method returns false when given set is not found
   * from the unRemovableSetTypes list.
   */
  @Test
  public void testDoWeRemoveTrue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method doWeRemove = pq.getDeclaredMethod("doWeRemove", new Class[] { EngineThread.class });
    doWeRemove.setAccessible(true);

    // Invoking the tested method and asserting that true is returned
    assertEquals(true, doWeRemove.invoke(objUnderTest, new Object[] { engineSet }));
  }

  /**
   * Testing if doWeRemove() method returns true when given set is found from
   * the unRemovableSetTypes list.
   */
  @Test
  public void testDoWeRemoveFalse() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method doWeRemove = pq.getDeclaredMethod("doWeRemove", new Class[] { EngineThread.class });
    doWeRemove.setAccessible(true);

    // Creating set with name matching one of those in unremovableSetTypes list
    EngineThread testSet = new EngineThread("setType3", 12L, null, null);

    // Invoking the tested method and asserting that false is returned
    assertEquals(false, doWeRemove.invoke(objUnderTest, new Object[] { testSet }));
  }

  /**
   * Testing if doWeRemove() throws exception when null EngineThread object is
   * given as parameter.
   */
  @Test
  public void testDoWeRemoveNullSet() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method doWeRemove = pq.getDeclaredMethod("doWeRemove", new Class[] { EngineThread.class });
    doWeRemove.setAccessible(true);

    // Creating set with name matching one of those in unremovableSetTypes list
    EngineThread testSet = null;

    // Checking if the exception is catched
    try {
      doWeRemove.invoke(objUnderTest, new Object[] { testSet });
      fail("Test Failed - Exception expected as null EngineThread object was given as parameter");
    } catch (Exception e) {
      // Test Passed - Exception caught
    }
  }

  /**
   * Testing if false is returned when set type is removable.
   */
  @Test
  public void testIsUnremovableSetTypeFalse() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method isUnremovableSetType = pq.getDeclaredMethod("isUnremovableSetType", new Class[] { String.class });
    isUnremovableSetType.setAccessible(true);

    // Asserting that false is returned
    assertEquals(false, isUnremovableSetType.invoke(objUnderTest, new Object[] { "removableSetType" }));
  }

  /**
   * Testing if true is returned when set type is unremovable.
   */
  @Test
  public void testIsUnremovableSetTypeTrue() throws Exception {

    // Reflecting the tested method
    Class pq = objUnderTest.getClass();
    Method isUnremovableSetType = pq.getDeclaredMethod("isUnremovableSetType", new Class[] { String.class });
    isUnremovableSetType.setAccessible(true);

    // Asserting that true is returned
    assertEquals(true, isUnremovableSetType.invoke(objUnderTest, new Object[] { "setType3" }));
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(PriorityQueueTest.class);
  }*/
}
