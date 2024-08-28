/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author ecarbjo
 *
 */
@Ignore("Ignoring class - new tests need to be done.")
public class LicensingCacheAdminTest {
	
	private static LicensingCacheAdmin admin = null;
	private static Process proc = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setSecurityManager(new LicensingCacheSecurityManager());
		//is dummy == false.
		final String execCmd = "\"" + System.getProperty("java.home") + "/bin/java\" -classpath \"" + System.getProperty("java.class.path") + "\" com.ericsson.eniq.licensing.cache.DefaultLicensingCache dummy";

		proc = Runtime.getRuntime().exec(execCmd);
		
		admin = new LicensingCacheAdmin();
		
		Thread.sleep(2000);
		Thread.yield();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			proc.destroy();
		} catch (Exception e) {
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingCacheAdmin#status(java.lang.String[])}.
	 */
	@Test
	public void testStatus() {
		try {
			admin.status();
		} catch(Exception e) {
			e.printStackTrace();
			fail("Caught an exception");
		}
	}
}
