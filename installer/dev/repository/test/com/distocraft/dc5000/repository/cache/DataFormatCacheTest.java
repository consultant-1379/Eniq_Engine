package com.distocraft.dc5000.repository.cache;

import static org.junit.Assert.*;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;
import utils.TestUtils;

@RunWith(JMock.class)
public class DataFormatCacheTest {

	private Mockery context = new JUnit4Mockery();
	
	private Mockery concreteContext = new JUnit4Mockery() {{
	    setImposteriser(ClassImposteriser.INSTANCE);
	}};	
	
	private RockFactory rock = null;
	
	
	@Before
	public void setUp() throws Exception {
	      final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	      final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";  
	        rock = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
	        TestUtils.loadSetup(rock, "repositoryTest");
	        System.setProperty("dwhrep.", "");
	}

	@After
	public void tearDown() throws Exception {
		
	}

	/*
	 * This test is designed to test that the DataFormatCache 
	 * doesn't exclude the first item from the database.
	 * This test was motivated by TR: HL94231
	 */
	@Test
	public void testRevalidate() {
		String interfaceName = "INTF_DIM_E_GRAN_SCGR";
		String tagID = "SCGR";
		int numberOfExpectedItems = 10;
		
		DataFormatCache.initialize(rock) ;
		DataFormatCache dataFormatCache = DataFormatCache.getCache();
		DFormat dFormat = dataFormatCache.getFormatWithTagID(interfaceName, tagID);
		int numberOfActualItems = dFormat.getDItemCount();
		assertEquals(numberOfExpectedItems, numberOfActualItems);
	}

}
