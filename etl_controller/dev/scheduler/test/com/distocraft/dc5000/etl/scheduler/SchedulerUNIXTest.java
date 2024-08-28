package com.distocraft.dc5000.etl.scheduler;

import junit.framework.JUnit4TestAdapter;


/* This is a version of SchedulerTest for running on UNIX / CI Server. It extends the SchedulerTest class and 
 * therefore inherits its test methods.
 */

public class SchedulerUNIXTest extends SchedulerTest {

	static{
		//A different port number is needed for running on CI server
		portNum = 1200;
	}
	
	public static junit.framework.Test suite() {
	    return new JUnit4TestAdapter(SchedulerUNIXTest.class);
	}
	
}
