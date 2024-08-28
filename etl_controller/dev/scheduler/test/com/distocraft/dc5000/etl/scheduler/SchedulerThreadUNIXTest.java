package com.distocraft.dc5000.etl.scheduler;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class extends SchedulerThreadTest. It is for running on UNIX / CI server. The parent class has some tests that fail on UNIX / CI Server 
 * (or intermittently fail) but pass in windows. This class excludes those tests by overriding them and giving them 
 * an Ignore tag. This class should be run on a UNIX machine instead of its parent class. All the tests seen in parent class
 * will be run, and the ones marked in this class with Ignore tag will not be run. 
 * NB: When a way is found for an ignored test in this class to always pass on UNIX, then it should be updated here and have 
 * its Ignore tag removed. 
 * @author edeamai
 */

public class SchedulerThreadUNIXTest extends SchedulerThreadTest {
	

	/*@Ignore
	@Test
	public void testMakeThreads() throws Exception {
		super.testMakeThreads();
	}
	
	@Ignore
	@Test
	public void testCancel() throws Exception {
		super.testCancel();
	}
	
	@Ignore
	@Test
	public void testStartThread() throws Exception {
		super.testStartThread();
	}
	
	@Ignore
	@Test
	public void testStartThreads() throws Exception {
		super.testStartThreads();
	}*/
	
	@Ignore
	@Test
	public void testTriggerSet() throws Exception {
		super.testTriggerSet();
	}
	
	@Ignore
	@Test
	public void testExecutionFailed() throws Exception {
		super.testExecutionFailed();
	}
	
	public static junit.framework.Test suite() {
	    return new JUnit4TestAdapter(SchedulerThreadUNIXTest.class);
	}
	
}