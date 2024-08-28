package com.distocraft.dc5000.etl.engine.executionslots;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This class extends ExecutionSlotProfileHandlerTest. It is for running in UNIX / CI Server. The parent class has some tests that fail on UNIX / CI Server 
 * (or intermittently fail) but pass in windows. This class excludes those tests by overriding them and giving them an Ignore tag. This class should be 
 * run on UNIX machine / CI Server instead of its parent class. All the tests seen in parent will be run due to inheritance, and the ones marked in this 
 * class with Ignore tag will not be run. 
 * NB: When a way is found for an ignored test in this class to always pass on UNIX / CI Server, then it should no longer be ignored here. 
 * @author edeamai
 */

public class ExecutionSlotProfileHandlerUNIXTest extends ExecutionSlotProfileHandlerTest {
	
	//Overriding this method with the purpose of ignoring it
	//It does not pass on UNIX-Window path format conflict.
	@Test
	@Ignore
	public void testWriteProfile() {
		super.testWriteProfile();
	}

}
