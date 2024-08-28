package com.distocraft.dc5000.repository.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;
import utils.TestUtils;


@RunWith(JMock.class)
public class AggregationStatusCacheTest {
	private Mockery context = new JUnit4Mockery();
	
	private Mockery concreteContext = new JUnit4Mockery() {{
	    setImposteriser(ClassImposteriser.INSTANCE);
	}};	
	
	private RockFactory rock = null;

	private final String drvname = "org.hsqldb.jdbcDriver";
	private final String dburl   = "jdbc:hsqldb:mem:dwhrep";
	private final String dbuser  = "SA";
	private final String passwd  = "";
	private RockFactory jUnitTestDB = null;
	private static boolean databaseSetUp = false;
		
	@BeforeClass
	public static void setUpClass() throws Exception {
	  // 
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
	  databaseSetUp = false;
	}
	
	@Before
	public void setUp() throws Exception {
	  if (!databaseSetUp) {
	    jUnitTestDB = getTestDb();
	    TestUtils.loadSetup(jUnitTestDB, "aggregationStatusCacheTest");
	    databaseSetUp = true;
	  }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoopCount(){
		try {
			AggregationStatusCache.init(dburl, dbuser, passwd, drvname);
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			long longDate = sdf.parse("2010-11-23 00:00:00").getTime();

			AggregationStatus aggSta = AggregationStatusCache.getStatus("DC_E_CPP_VCLTP_DAYBH_VCLTP", longDate);
			assertNotNull(aggSta);
			assertEquals(aggSta.LOOPCOUNT, 1);
			
			assertEquals(aggSta.ROWCOUNT, 10);
		} catch (Exception e) {
			fail("TEST FAILED: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
  @Test
  public void testThreshold() {
    try {
      AggregationStatusCache.init(dburl, dbuser, passwd, drvname);
      
      final PhysicalTableCache ptcMock = concreteContext.mock(PhysicalTableCache.class);     
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");           
      final long longDate = sdf.parse("2010-11-23 00:00:00").getTime();
      
      // Set a mock cache for the physical table cache.
      // Table name will be 'LOG_AggregationStatus':
      PhysicalTableCache.setCache(ptcMock);      
      concreteContext.checking(new Expectations() {{
        oneOf(ptcMock).getTableName(with("LOG_AggregationStatus:PLAIN"), with(longDate));
        will(returnValue("LOG_AggregationStatus"));             
      }}); 
      
      // Test that the threshold value is correct from the cache:  
      AggregationStatus aggSta = AggregationStatusCache.getStatus("DC_E_CPP_VCLTP_DAYBH_VCLTP", longDate);
      assertNotNull(aggSta);
      assertEquals("Threshold time value should be retrieved properly from cache", longDate, aggSta.THRESHOLD);
      
      // Test that the threshold value is set correctly in the cache:
      // Set a new time:           
      long newTime = sdf.parse("2010-11-23 03:00:00").getTime();   
      aggSta.THRESHOLD = newTime;
      AggregationStatusCache.setStatus(aggSta);
      AggregationStatusCache.getStatus("DC_E_CPP_VCLTP_DAYBH_VCLTP", newTime);
      assertEquals("Threshold time value should be updated properly in cache", newTime, aggSta.THRESHOLD);            
    } catch (Exception e) {
      fail("TEST FAILED: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  @Test
  public void testCheckThresholdReset() {    
    boolean result = AggregationStatusCache.checkThresholdReset("QUEUED");
    assertEquals("QUEUED aggregation should not have its threshold value reset", false, result);
    
    result = AggregationStatusCache.checkThresholdReset("LOADED");
    assertEquals("LOADED aggregation should not have its threshold value reset", false, result);
    
    // MANUAL, IGNORED, ERROR, FAILEDDEPENDENCY, LATE DATA or AGGREGATED should have the threshold value reset:   
    result = AggregationStatusCache.checkThresholdReset("MANUAL");
    assertEquals("MANUAL aggregation should have its threshold value reset", true, result);
    
    result = AggregationStatusCache.checkThresholdReset("IGNORED");
    assertEquals("IGNORED aggregation should have its threshold value reset", true, result);
    
    result = AggregationStatusCache.checkThresholdReset("ERROR");
    assertEquals("ERROR aggregation should have its threshold value reset", true, result);
    
    result = AggregationStatusCache.checkThresholdReset("FAILEDDEPENDENCY");
    assertEquals("FAILEDDEPENDENCY aggregation should have its threshold value reset", true, result);
    
    result = AggregationStatusCache.checkThresholdReset("LATE_DATA");
    assertEquals("LATE_DATA aggregation should have its threshold value reset", true, result);
    
    result = AggregationStatusCache.checkThresholdReset("AGGREGATED");
    assertEquals("AGGREGATED aggregation should have its threshold value reset", true, result);
  }
	
	private RockFactory getTestDb() throws Exception {
		if(jUnitTestDB == null || jUnitTestDB.getConnection().isClosed()){
			jUnitTestDB = new RockFactory(dburl, dbuser, passwd, drvname, "con", true, -1);
		}
		return jUnitTestDB;
	}

}
