package com.distocraft.dc5000.etl.engine.system;


import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author epiituo
 *
 */
public class StatusEventTest {

	private static final Date CURRENT_TIME = new Date(System.currentTimeMillis());
	private static final String MESSAGE = "message"; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructorWithDispatcher() {
		StatusEvent statusEvent = new StatusEvent(this, CURRENT_TIME, MESSAGE);
		String className = this.getClass().getName();
		Assert.assertEquals(statusEvent.getDispatcher(), className);
	}
	
	@Test
	public void testCreatingEventWithStaticMethod() {
		StatusEvent s = StatusEvent.statusEventWithCurrentTime(this, MESSAGE);
		String className = this.getClass().getName();
		Assert.assertEquals(s.getDispatcher(), className);
		Assert.assertEquals(s.getMessage(), MESSAGE);
	}
	
	@Test
	public void testUsingNullsAsConstructorParameters() {
		StatusEvent s = new StatusEvent(null, null, null);
		
		boolean causedException = false;
		try {
			s.toString();
		} catch (Exception e) {
			
		}
		Assert.assertFalse(causedException);
		
		Assert.assertNull(s.getDispatcher());
		Assert.assertNull(s.getTime());
		Assert.assertNull(s.getMessage());
	}

}
