/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.engine.main;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.BaseMock;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.TransferEngine.EngineLicenseCheckTask;
import com.distocraft.dc5000.etl.engine.main.exceptions.InvalidSetParametersRemoteException;
import com.distocraft.dc5000.etl.engine.priorityqueue.PriorityQueue;
import com.distocraft.dc5000.etl.engine.system.AlarmConfigCacheWrapper;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicenseInformation;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingResponse;

/**
 * @author eemecoy
 * 
 */
public class TransferEngineTest extends BaseMock {

  private TransferEngine objToTest;

  EngineThread mockedEngineThread;

  PriorityQueue mockedPriorityQueue;

  ExecutionSlotProfileHandler mockedExecutionSlotProfileHandler;

  RockFactory mockedEtlRepRockFactory;
  
  RockFactory mockedDwhRepRockFactory;

  String expectedCollectionSetName;

  Meta_collection_setsFactory mockedMetaCollectionSetsFactory;

  String expectedSqlQuery = " ORDER BY COLLECTION_SET_NAME DESC;";

  String expectedCollectionName;

  String expectedCollectionQuery = " ORDER BY COLLECTION_NAME DESC;";

  Meta_collectionsFactory mockedMetaCollectionsFactory;
  
  LicensingResponse mockedLicensingResponse;

  Long expectedCollectionSetId;

  @Before
  public void setUp() throws RemoteException {

    resetExpectedFields();

    final Logger mockedLog = context.mock(Logger.class);
    context.checking(new Expectations() {

      {

        allowing(mockedLog).finest(with(any(String.class)));
        allowing(mockedLog).fine(with(any(String.class)));
        allowing(mockedLog).info(with(any(String.class)));
        allowing(mockedLog).severe(with(any(String.class)));
        allowing(mockedLog).log(with(any(Level.class)), with(any(String.class)));
        allowing(mockedLog).warning(with(any(String.class)));
      }
    });

    mockedEngineThread = context.mock(EngineThread.class);
    mockedPriorityQueue = context.mock(PriorityQueue.class);
    mockedExecutionSlotProfileHandler = context.mock(ExecutionSlotProfileHandler.class);
    mockedEtlRepRockFactory = context.mock(RockFactory.class, "EtlRep");
    mockedDwhRepRockFactory = context.mock(RockFactory.class, "DwhRep");
    mockedMetaCollectionSetsFactory = context.mock(Meta_collection_setsFactory.class);
    mockedMetaCollectionsFactory = context.mock(Meta_collectionsFactory.class);
    mockedLicensingResponse = context.mock(LicensingResponse.class);

    objToTest = new StubbedTransferEngine(mockedLog);
  }

  private void resetExpectedFields() {
    expectedCollectionName = null;
    expectedCollectionSetId = null;
    expectedCollectionSetName = null;
  }

  // @Test
  // public void testExecuteWithValidNamesButCollectionSetDisabledThrowsCannotStartSetException1() {
  // final String collectionSetName = "DWH_MONITOR";
  // expectedCollectionSetName = collectionSetName;
  // final String collectionName = "AggregationRuleCopy";
  // expectedCollectionName = collectionName;
  // expectedCollectionSetId = new Long(123);
  //
  //
  // final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();
  // final Meta_collection_sets mockedMetaCollectionSets = context.mock(Meta_collection_sets.class);
  // final Meta_collections mockedMetaCollections = context.mock(Meta_collections.class);
  // final Vector<Meta_collections> collections = new Vector<Meta_collections>();
  // collections.add(mockedMetaCollections);
  //
  // context.checking(new Expectations() {
  // {
  // one(mockedMetaCollectionSets).getEnabled_flag();
  // will(returnValue("n"));
  // one(mockedMetaCollectionSets).getEnabled_flag();
  // will(returnValue("y"));
  // one(mockedMetaCollectionSets).getCollection_set_id();
  // will(returnValue(new Long(123)));
  // one(mockedMetaCollectionsFactory).get();
  // will(returnValue(collections));
  // one(mockedMetaCollections).getEnabled_flag();
  // will(returnValue("y"));
  // }
  // });
  // metaCollectionSetsToReturn.add(mockedMetaCollectionSets);
  // metaCollectionSetsToReturn.add(mockedMetaCollectionSets);
  //
  //
  //
  // expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);
  //
  // try {
  // objToTest.execute(collectionSetName, collectionName, null);
  // } catch (final RemoteException e) {
  // fail("Cannot start set, collection set " + collectionSetName + " is not enabled");
  // }
  // }

  @Test
  public void testExecuteWithValidNamesButCollectionSetDisabledThrowsCannotStartSetException() {
    final String collectionSetName = "DWH_MONITOR";
    expectedCollectionSetName = collectionSetName;
    final String collectionName = "AggregationRuleCopy";
    expectedCollectionName = collectionName;

    final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();
    final Meta_collection_sets mockedMetaCollectionSet = createMockedMetaCollectionSet("n");
    metaCollectionSetsToReturn.add(mockedMetaCollectionSet);

    expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);

    try {
      objToTest.execute(collectionSetName, collectionName, null);
      fail("Exception should have been thrown");
    } catch (final RemoteException e) {
      assertThat(e.getMessage(), is("Cannot start set, collection set " + collectionSetName + " is not enabled"));
    }
  }

  @Test
  public void testExecuteWithValidNamesButCollectionDisabledThrowsCannotStartSetException() {
    final String collectionSetName = "DWH_MONITOR";
    expectedCollectionSetName = collectionSetName;
    final String collectionName = "AggregationRuleCopy";
    expectedCollectionName = collectionName;

    final Long collectionSetId = 38L;
    expectedCollectionSetId = collectionSetId;

    final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();
    final Meta_collection_sets mockedMetaCollectionSet = createMockedMetaCollectionSet("y", collectionSetId);
    metaCollectionSetsToReturn.add(mockedMetaCollectionSet);

    expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);

    final Vector<Meta_collections> metaCollectionsToReturn = new Vector<Meta_collections>();
    final Meta_collections mockedMetaCollections = createMockedMetaCollections("n");
    metaCollectionsToReturn.add(mockedMetaCollections);

    expectGetOnMetaCollectionFactory(metaCollectionsToReturn);

    try {
      objToTest.execute(collectionSetName, collectionName, null);
      fail("Exception should have been thrown");
    } catch (final RemoteException e) {
      assertThat(e.getMessage(), is("Cannot start set, collection " + collectionName + " is not enabled"));
    }
  }

  @Test
  public void testExecuteWithInvalidCollectionNameThrowsCannotStartSetException() throws RemoteException {
    final String collectionSetName = "DWH_MONITOR";
    expectedCollectionSetName = collectionSetName;

    final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();
    final long collectionSetId = 3434L;
    expectedCollectionSetId = collectionSetId;
    final Meta_collection_sets mockedMetaCollectionSet = createMockedMetaCollectionSet("y", collectionSetId);
    metaCollectionSetsToReturn.add(mockedMetaCollectionSet);

    expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);

    final String collectionName = "dummy collection name";
    expectedCollectionName = collectionName;

    final Vector<Meta_collections> metaCollectionsToReturn = new Vector<Meta_collections>();
    expectGetOnMetaCollectionFactory(metaCollectionsToReturn);

    try {
      objToTest.execute(collectionSetName, collectionName, null);
      fail("Exception should have been thrown");
    } catch (final InvalidSetParametersRemoteException e) {
      assertThat(e.getMessage(), is("Cannot start set, collection " + collectionName
          + " doesn't exist, or incorrect Tech Pack was supplied"));
    }
  }

  private Meta_collections createMockedMetaCollections(final String enabledStatus) {
    final Meta_collections mockedMetaCollections = context.mock(Meta_collections.class);
    context.checking(new Expectations() {

      {
        one(mockedMetaCollections).getEnabled_flag();
        will(returnValue(enabledStatus));

      }
    });
    return mockedMetaCollections;
  }

  @Test
  public void testExecuteWithInvalidCollectionSetNameThrowsCannotStartSetException() {
    final String collectionSetName = "Invalid collection set name";
    expectedCollectionSetName = collectionSetName;
    final String collectionName = "AggregationRuleCopy";

    final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();

    expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);
    try {
      objToTest.execute(collectionSetName, collectionName, null);
      fail("Exception should have been thrown");
    } catch (final Exception e) {
      assertThat(e.getMessage(), is("Cannot start set, collection set " + collectionSetName + " doesn't exist"));
    }
  }

  @Test
  public void testExecuteWithValidCollectionSetNameAndValidCollectionName() throws RemoteException {
    expectAddSetToPriorityQueue();
    final String collectionSetName = "DWH_MONITOR";
    expectedCollectionSetName = collectionSetName;
    final String collectionName = "AggregationRuleCopy";
    expectedCollectionName = collectionName;

    final Vector<Meta_collection_sets> metaCollectionSetsToReturn = new Vector<Meta_collection_sets>();
    final long collectionSetId = 3434L;
    expectedCollectionSetId = collectionSetId;
    final Meta_collection_sets mockedMetaCollectionSet = createMockedMetaCollectionSet("y", collectionSetId);
    metaCollectionSetsToReturn.add(mockedMetaCollectionSet);

    expectGetOnMetaCollectionSetFactory(metaCollectionSetsToReturn);

    final Vector<Meta_collections> metaCollectionsToReturn = new Vector<Meta_collections>();
    final Meta_collections mockedMetaCollections = createMockedMetaCollections("y");
    metaCollectionsToReturn.add(mockedMetaCollections);

    expectGetOnMetaCollectionFactory(metaCollectionsToReturn);

    objToTest.execute(collectionSetName, collectionName, null);
  }

  @Test
  public void testRunMax_query_parallelismSetting_CALCULATE() throws Exception {

    Properties prop = new Properties();
    prop.setProperty("aaah", "1");

    try {
      StaticProperties.giveProperties(prop);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Property set up failed");
    }
    
    ((StubbedTransferEngine) objToTest).setNumberOfCPUCores(20);

    final Connection mockedConnection = context.mock(Connection.class);
    final PreparedStatement mockedPreparedStatement = context.mock(PreparedStatement.class);

    context.checking(new Expectations() {
      {
        allowing(mockedExecutionSlotProfileHandler).getNumberOfAggregatorSlots();
        will(returnValue(4));

        allowing(mockedEtlRepRockFactory).getConnection();
        will(returnValue(mockedConnection));

        allowing(mockedConnection).prepareStatement("SET OPTION PUBLIC.max_query_parallelism = '5.0';");
        will(returnValue(mockedPreparedStatement));

        oneOf(mockedPreparedStatement).executeUpdate();
        will(returnValue(0));

        allowing(mockedPreparedStatement).close();

      }
    });

    Method parallelismMethod = TransferEngine.class.getDeclaredMethod("runMax_query_parallelismSetting",
        RockFactory.class);
    parallelismMethod.setAccessible(true);

    parallelismMethod.invoke(objToTest, mockedEtlRepRockFactory);
  }

  @Test
  public void testRunMax_query_parallelismSetting_CALCULATE_For_Zero_Aggslots() throws Exception {

    Properties prop = new Properties();
    prop.setProperty("aaah", "1");

    try {
      StaticProperties.giveProperties(prop);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Property set up failed");
    }

    context.checking(new Expectations() {
      {
        allowing(mockedExecutionSlotProfileHandler).getNumberOfAggregatorSlots();
        will(returnValue(0));
      }
    });

    try {
      Method parallelismMethod = TransferEngine.class.getDeclaredMethod("runMax_query_parallelismSetting",
          RockFactory.class);
      parallelismMethod.setAccessible(true);

      parallelismMethod.invoke(objToTest, mockedEtlRepRockFactory);
    } catch (ArithmeticException e) {
      fail("Something unexpected happened");
    }

  }

  @Test
  public void testRunMax_query_parallelismSetting_FROM_STATIC_FILE() throws Exception {

    Properties prop = new Properties();
    prop.setProperty("sybaseiq.option.public.max_query_parallelism", "8");

    try {
      StaticProperties.giveProperties(prop);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Property set up failed");
    }

    final Connection mockedConnection = context.mock(Connection.class);
    final PreparedStatement mockedPreparedStatement = context.mock(PreparedStatement.class);

    context.checking(new Expectations() {
      {
        allowing(mockedExecutionSlotProfileHandler).getNumberOfAggregatorSlots();
        will(returnValue(4));

        allowing(mockedEtlRepRockFactory).getConnection();
        will(returnValue(mockedConnection));

        allowing(mockedConnection).prepareStatement("SET OPTION PUBLIC.max_query_parallelism = '8';");
        will(returnValue(mockedPreparedStatement));

        oneOf(mockedPreparedStatement).executeUpdate();
        will(returnValue(0));

        allowing(mockedPreparedStatement).close();

      }
    });

    Method parallelismMethod = TransferEngine.class.getDeclaredMethod("runMax_query_parallelismSetting",
        RockFactory.class);
    parallelismMethod.setAccessible(true);

    parallelismMethod.invoke(objToTest, mockedEtlRepRockFactory);
  }

  /**
   * Test method for {@link com.distocraft.dc5000.etl.engine.main.TransferEngine#reloadAlarmConfigCache()}.
   * 
   * @throws RemoteException
   * @throws SQLException 
   */
  @Test
  public void testReloadAlarmConfigCache() throws RemoteException, SQLException {
    
    final Connection mockedConnection = context.mock(Connection.class);
    
    context.checking(new Expectations() {
      {
        allowing(mockedDwhRepRockFactory).getConnection();
        will(returnValue(mockedConnection));

        allowing(mockedConnection).close();

      }
    });
    
    AlarmConfigCacheWrapper.setAllowRevalidate(false);
    objToTest.reloadAlarmConfigCache();
  }
   
  @Test
  public void testCheckStarterLicenseForEniq12License() throws Exception {

    final LicensingCache licensingCache = context.mock(LicensingCache.class);
    expectGetExpiryDate(licensingCache, TransferEngine.ENIQ_12_STARTER_LICENSE, 10);

    context.checking(new Expectations() {
      {
        oneOf(licensingCache).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));
        
        exactly(2).of(mockedLicensingResponse).isValid();
        will(returnValue(true));

        oneOf(mockedLicensingResponse).getMessage();
        will(returnValue("License is valid"));
      }
    });

    Method starterMethod = TransferEngine.class.getDeclaredMethod("checkStarterLicense", LicensingCache.class,
        String.class, String.class);
    starterMethod.setAccessible(true);

    final boolean result = (Boolean) starterMethod.invoke(objToTest, licensingCache,
        TransferEngine.ENIQ_12_STARTER_LICENSE, "Eniq 12 starter license");
    Assert.assertTrue("checkStarterLicense method should return true for Eniq 12 license", result);
  }
  
  @Test
  public void testCheckStarterLicenseForEniq12LicenseNoLicense() throws Exception {
    final LicensingCache licensingCache = context.mock(LicensingCache.class);

    context.checking(new Expectations() {
      {
        oneOf(licensingCache).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));
        
        oneOf(mockedLicensingResponse).isValid();
        will(returnValue(false));
        
        oneOf(mockedLicensingResponse).getResponseType();
        will(returnValue(LicensingResponse.LICENSE_MISSING));
      }
    });

    Method starterMethod = TransferEngine.class.getDeclaredMethod("checkStarterLicense", LicensingCache.class,
        String.class, String.class);
    starterMethod.setAccessible(true);

    final boolean result = (Boolean) starterMethod.invoke(objToTest, licensingCache,
        TransferEngine.ENIQ_12_STARTER_LICENSE, "Eniq 12 starter license");
    Assert.assertFalse("checkStarterLicense method should return false for Eniq 12 license if license not valid", result);
  }
    
  @Test
  public void testCheckCapacityLicenseEniq12() throws Exception {
    final LicensingCache licensingCacheMock = context.mock(LicensingCache.class);

    context.checking(new Expectations() {
      {
        oneOf(licensingCacheMock).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));
        
        exactly(2).of(mockedLicensingResponse).isValid();
        will(returnValue(true));

        oneOf(mockedLicensingResponse).getMessage();
        will(returnValue("License is valid"));            
      }
    });

    Method licenseMethod = TransferEngine.class.getDeclaredMethod("checkCapacityLicense", LicensingCache.class,
        String.class, String.class);
    licenseMethod.setAccessible(true);
    // Set test values for the number of physical CPUs and number of cores:
    ((StubbedTransferEngine) objToTest).setNumberOfPhysicalCPUs(5);
    ((StubbedTransferEngine) objToTest).setNumberOfCPUCores(5);

    final boolean result = (Boolean) licenseMethod.invoke(objToTest, licensingCacheMock,
        TransferEngine.ENIQ_12_CAPACITY_LICENSE, "Eniq 12 capacity license");
    Assert.assertTrue("checkCapacityLicense should return true for Eniq 12 license if capacity is ok", result);
  }
  
  @Test
  public void testCheckCapacityLicenseEniq12CheckFails() throws Exception {
    final LicensingCache licensingCacheMock = context.mock(LicensingCache.class);

    context.checking(new Expectations() {
      {
        oneOf(licensingCacheMock).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));        
        
        // Capacity is not licensed:
        oneOf(mockedLicensingResponse).isValid();
        will(returnValue(false));
        
        oneOf(mockedLicensingResponse).getMessage();
        will(returnValue("Insufficient capacity!"));   
      }
    });

    Method licenseMethod = TransferEngine.class.getDeclaredMethod("checkCapacityLicense", LicensingCache.class,
        String.class, String.class);
    licenseMethod.setAccessible(true);
    // Set test values for the number of physical CPUs and number of cores:
    ((StubbedTransferEngine) objToTest).setNumberOfPhysicalCPUs(5);
    ((StubbedTransferEngine) objToTest).setNumberOfCPUCores(5);

    final boolean result = (Boolean) licenseMethod.invoke(objToTest, licensingCacheMock,
        TransferEngine.ENIQ_12_CAPACITY_LICENSE, "Eniq 12 capacity license");
    Assert.assertFalse("checkCapacityLicense should return false for Eniq 12 license if capacity check fails", result);
  }
  
  @Test
  public void testCheckCapacityLicenseEniq12ExceptionCheckingLicense() throws Exception {
    final LicensingCache licensingCacheMock = context.mock(LicensingCache.class);

    context.checking(new Expectations() {
      {
        oneOf(licensingCacheMock).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(throwException(new Exception("Failed checking capacity license")));
      }
    });

    Method licenseMethod = TransferEngine.class.getDeclaredMethod("checkCapacityLicense", LicensingCache.class,
        String.class, String.class);
    licenseMethod.setAccessible(true);
    // Set test values for the number of physical CPUs and number of cores:
    ((StubbedTransferEngine) objToTest).setNumberOfPhysicalCPUs(5);
    ((StubbedTransferEngine) objToTest).setNumberOfCPUCores(5);

    final boolean result = (Boolean) licenseMethod.invoke(objToTest, licensingCacheMock,
        TransferEngine.ENIQ_12_CAPACITY_LICENSE, "Eniq 12 capacity license");
    Assert.assertFalse("checkCapacityLicense should return false for Eniq 12 license if capacity check throws an exception", result);
  }
   
  @Test
  public void testStatus() throws RemoteException, Exception{
  	
    	context.checking(new Expectations() {{			
  		allowing(mockedExecutionSlotProfileHandler).getActiveExecutionProfile();
  		will(returnValue(4));
 		
  	}});
    	
    	try{
    	}catch(Exception e){
        	Assert.assertSame("Engine initialization has not been completed yet", e.getMessage());     		
    	}
  }
  
  @Test
  public void testGetNumberOfCPUCoresWithException(){
	  
	  StubbedTransferEngine objToStubbedTest = null;
	  
	  try{
		  
		  objToStubbedTest =  new StubbedTransferEngine(null);
		  objToStubbedTest.setNumberOfCPUCores(-1);
		  int cores = objToStubbedTest.getNumberOfCPUCores();
	  }
	  catch(Exception e){
		  
		  Assert.assertSame("Error getting number of CPU Cores.", e.getMessage());
	  }
  }
  
  @Test
  public void testGetNumberOfCPUCoresWithOutException(){
	  
	  StubbedTransferEngine objToStubbedTest = null;
	  
	  try{
		  
		  objToStubbedTest =  new StubbedTransferEngine(null);
		  objToStubbedTest.setNumberOfCPUCores(2);
		  int cores = objToStubbedTest.getNumberOfCPUCores();
		  Assert.assertEquals(2, cores);
	  }
	  catch(Exception e){
		  
		  e.printStackTrace();

	  }
  }
  
  @Test
  public void testGetNumberOfCPUsWithException(){
	  
	  StubbedTransferEngine objToStubbedTest = null;
	  
	  try{
		  
		  objToStubbedTest =  new StubbedTransferEngine(null);
		  objToStubbedTest.setNumberOfPhysicalCPUs(-1);
		  int physicalCpus = objToStubbedTest.getNumberOfPhysicalCPUs();
	  }
	  catch(Exception e){
		  
		  Assert.assertSame("Error getting number of Physical CPUs.", e.getMessage());
	  }
  }
  
  @Test
  public void testGetNumberOfCPUsWithOutException(){
	  
	  StubbedTransferEngine objToStubbedTest = null;
	  
	  try{
		  
		  objToStubbedTest =  new StubbedTransferEngine(null);
		  objToStubbedTest.setNumberOfPhysicalCPUs(2);
		  int physicalCpus = objToStubbedTest.getNumberOfPhysicalCPUs();
		  Assert.assertEquals(2, physicalCpus);
	  }
	  catch(Exception e){
		  
		  e.printStackTrace();

	  }
  }
  
  /**
   * doLicenseCheck() should return true if license response is valid.
   * @throws Exception
   */
  @Test
  public void testEngineLicenseCheckTask() throws Exception {
    
    // Set up mock objects:
    final LicensingCache licenseCacheMock = context.mock(LicensingCache.class);
    
    // Set up active starter license as the correct one:
    objToTest.setActiveStarterlicense(TransferEngine.ENIQ_12_STARTER_LICENSE);
    
    // Set up expectations so that the license response will be valid: 
    context.checking(new Expectations() {
      {        
        oneOf(licenseCacheMock).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));
        
        exactly(2).of(mockedLicensingResponse).isValid();
        will(returnValue(true));
        
        oneOf(mockedLicensingResponse).getMessage();
        will(returnValue("Valid license"));
      }
    });
    
    // Set up test instance and call:
    EngineLicenseCheckTask taskInstance = objToTest.new EngineLicenseCheckTask();        
    final boolean result = taskInstance.doLicenseCheck(licenseCacheMock);
    Assert.assertEquals("doLicenseCheck() should return true if license response is valid", result, true);
  }
  
  /**
   * doLicenseCheck() should return false if license response is invalid.
   * @throws Exception
   */
  @Test
  public void testEngineLicenseCheckTaskInvalidLicense() throws Exception {
    
    // Set up mock objects:
    final LicensingCache licenseCacheMock = context.mock(LicensingCache.class);
    
    // Set up active starter license as the correct one:
    objToTest.setActiveStarterlicense(TransferEngine.ENIQ_12_STARTER_LICENSE);
    
    // Set up expectations so that the license response will not be valid: 
    context.checking(new Expectations() {
      {        
        oneOf(licenseCacheMock).checkCapacityLicense(with(any(LicenseDescriptor.class)), with(any(Integer.class)));
        will(returnValue(mockedLicensingResponse));
        
        exactly(2).of(mockedLicensingResponse).isValid();
        will(returnValue(false));
        
        oneOf(mockedLicensingResponse).getMessage();
        will(returnValue("Invalid license"));
      }
    });
    
    // Set up test instance and call:
    EngineLicenseCheckTask taskInstance = objToTest.new EngineLicenseCheckTask();        
    final boolean result = taskInstance.doLicenseCheck(licenseCacheMock);
    Assert.assertEquals("doLicenseCheck() should return true if license response is valid", result, false);
  }
  
  /**
   * doLicenseCheck() should return false if the active license does not match the Eniq 12 license.
   * @throws Exception
   */
  @Test
  public void testEngineLicenseCheckTaskIncorrectLicense() throws Exception {
    
    // Set up mock objects:
    final LicensingCache licenseCacheMock = context.mock(LicensingCache.class);
    
    // Set up active starter license as a license that does not match:
    objToTest.setActiveStarterlicense("incorrect value");
    
    // Set up test instance and call:
    EngineLicenseCheckTask taskInstance = objToTest.new EngineLicenseCheckTask();        
    final boolean result = taskInstance.doLicenseCheck(licenseCacheMock);
    Assert.assertEquals("doLicenseCheck() should return true if license response is valid", result, false);
  }
      
  /**
   * 
   * @param cacheMock
   * @param license
   * @throws Exception
   */
  private void expectGetExpiryDate(final LicensingCache cacheMock, final String license, 
      final long deathDayValue) throws Exception {
    final LicenseInformation liMock = context.mock(LicenseInformation.class);
    final Vector<LicenseInformation> testVector = new Vector<LicenseInformation>();
    testVector.add(liMock);

    context.checking(new Expectations() {

      {
        oneOf(cacheMock).getLicenseInformation();
        will(returnValue(testVector));

        oneOf(liMock).getFeatureName();
        will(returnValue(license));

        oneOf(liMock).getDeathDay();
        will(returnValue(deathDayValue));
      }
    });
  }

  private Meta_collection_sets createMockedMetaCollectionSet(final String enabledStatus) {
    final Meta_collection_sets mockedMetaCollectionSets = context.mock(Meta_collection_sets.class);
    setUpGetEnabledOnMockedMetaCollectionSets(enabledStatus, mockedMetaCollectionSets);
    return mockedMetaCollectionSets;
  }

  private void setUpGetEnabledOnMockedMetaCollectionSets(final String enabledStatus,
      final Meta_collection_sets mockedMetaCollectionSets) {
    context.checking(new Expectations() {

      {
        one(mockedMetaCollectionSets).getEnabled_flag();
        will(returnValue(enabledStatus));
      }
    });
  }

  private Meta_collection_sets createMockedMetaCollectionSet(final String enabledStatus, final Long collectionSetId) {
    final Meta_collection_sets mockedMetaCollectionSets = context.mock(Meta_collection_sets.class);
    setUpGetEnabledOnMockedMetaCollectionSets(enabledStatus, mockedMetaCollectionSets);
    context.checking(new Expectations() {

      {
        one(mockedMetaCollectionSets).getCollection_set_id();
        will(returnValue(collectionSetId));

      }
    });
    return mockedMetaCollectionSets;
  }

  private void expectGetOnMetaCollectionFactory(final Vector<Meta_collections> metaCollectionsToReturn) {

    context.checking(new Expectations() {

      {
        one(mockedMetaCollectionsFactory).get();
        will(returnValue(metaCollectionsToReturn));

      }
    });

  }

  private void expectGetOnMetaCollectionSetFactory(final Vector<Meta_collection_sets> metaCollectionSetsToReturn) {

    context.checking(new Expectations() {

      {
        one(mockedMetaCollectionSetsFactory).get();
        will(returnValue(metaCollectionSetsToReturn));

      }
    });

  }

  private void expectAddSetToPriorityQueue() {
    context.checking(new Expectations() {

      {
        one(mockedPriorityQueue).addSet(mockedEngineThread);

      }
    });

  }

  class StubbedTransferEngine extends TransferEngine {
    
    private static final long serialVersionUID = 1L;
    private int numberOfCPUCores = 0;
    private int numberOfPhysicalCPUs = 0;
    
    /**
     * @param usePQ
     * @param useDefaultEXSlots
     * @param EXSlots
     * @param log
     * @throws RemoteException
     */
    public StubbedTransferEngine(final boolean usePQ, final boolean useDefaultEXSlots, final int EXSlots,
        final Logger log) throws RemoteException {
      super(usePQ, useDefaultEXSlots, EXSlots, log);
    }

    /**
     * @param mockedLog
     * @throws RemoteException
     */
    public StubbedTransferEngine(final Logger mockedLog) throws RemoteException {
      super(mockedLog);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#createNewEngineThread(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    EngineThread createNewEngineThread(final String collectionSetName, final String collectionName,
        final String ScheduleInfo) throws Exception {
      return mockedEngineThread;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#isInitialized()
     */
    @Override
    public boolean isInitialized() throws RemoteException {
      return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#getPriorityQueue()
     */
    @Override
    PriorityQueue getPriorityQueue() {
      return mockedPriorityQueue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#getExecutionSlotProfileHandler()
     */
    @Override
    ExecutionSlotProfileHandler getExecutionSlotProfileHandler() {
      return mockedExecutionSlotProfileHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#getExecutionSlotProfileHandler()
     */
    @Override
    public int getNumberOfCPU() {
      return 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#getEtlRepRockFactory()
     */
    @Override
    RockFactory getEtlRepRockFactory() {
      return mockedEtlRepRockFactory;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.TransferEngine#getEtlRepRockFactory()
     */
    @Override
    RockFactory getDwhRepRockFactory(String connName) {
      return mockedDwhRepRockFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.distocraft.dc5000.etl.engine.main.TransferEngine#createMetaCollectionSetsFactory(ssc.rockfactory.RockFactory,
     * com.distocraft.dc5000.etl.rock.Meta_collection_sets, java.lang.String)
     */
    @Override
    Meta_collection_setsFactory createMetaCollectionSetsFactory(final RockFactory etlRock,
        final Meta_collection_sets whereMetaCollectionSets, final String sqlQuery) throws SQLException, RockException {
      assertThat(whereMetaCollectionSets.getCollection_set_name(), is(expectedCollectionSetName));
      assertThat(sqlQuery, is(expectedSqlQuery));
      return mockedMetaCollectionSetsFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.distocraft.dc5000.etl.engine.main.TransferEngine#createMetaCollectionFactory(ssc.rockfactory.RockFactory,
     * com.distocraft.dc5000.etl.rock.Meta_collections, java.lang.String)
     */
    @Override
    Meta_collectionsFactory createMetaCollectionFactory(final RockFactory etlRock,
        final Meta_collections whereMetaCollection, final String sqlQuery) throws SQLException, RockException {
      assertThat(whereMetaCollection.getCollection_name(), is(expectedCollectionName));
      assertThat(whereMetaCollection.getCollection_set_id(), is(expectedCollectionSetId));
      assertThat(sqlQuery, is(expectedCollectionQuery));
      return mockedMetaCollectionsFactory;
    }
    
    @Override
    protected LicensingResponse getLicensingResponse(final LicensingCache cache, final String starterLicense) {
      return mockedLicensingResponse;
    }
    
    @Override
    protected void scheduleLicenseCheckTask() {
      // does nothing in test
    }
    
    @Override
    protected int getNumberOfPhysicalCPUs() {
      return numberOfPhysicalCPUs;
    }

    @Override
    protected int getNumberOfCPUCores() {
      return numberOfCPUCores;
    }
    
    public void setNumberOfPhysicalCPUs(int numberOfPhysicalCPUs) {
      this.numberOfPhysicalCPUs = numberOfPhysicalCPUs;
    }

    public void setNumberOfCPUCores(int numberOfCPUCores) {
      this.numberOfCPUCores = numberOfCPUCores;
    }
    
    @Override
    public void slowGracefulShutdown() throws RemoteException {
      // do nothing in test
    }
        
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TransferEngineTest.class);
  }

}
