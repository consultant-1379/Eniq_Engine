/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

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
@Ignore("Tests have been replaced by DefaultLicensingCacheTest.")
public class LicensingCacheTest {

	private static LicensingCache cache = null;

	/**
	 * @throws java.lang.Exception
	 */
//	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TODO: Change if implementation changes.
		// is dummy == false.
		cache = new DefaultLicensingCache(true, "c:/temp/cache.properties");
		System.out.println("Starting tests.");
	}

	/**
	 * @throws java.lang.Exception
	 */
//	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
	 * Test method for
	 * {@link com.ericsson.eniq.licensing.cache.DefaultLicensingCache#checkLicense(com.ericsson.eniq.licensing.cache.LicenseDescriptor)}
	 * .
	 * 
	 * @throws
	 */
	//@Test
	public void testCheckLicense() {
		// we are unit testing in dummy mode. All should be true.
		final LicenseDescriptor invalid = new DefaultLicenseDescriptor(
				"CXCSOMETHING");
		try {
			assertTrue("The license was deemed false", cache.checkLicense(
					invalid).isValid());
		} catch (RemoteException e) {
			fail("Caught a RemoteException");
		}
	}

	//@Test
	public void testStatus() {
		try {
			final LicensingSettings conf = cache.getSettings();
			final LicensingCache stub = (LicensingCache) Naming.lookup("rmi://"
					+ conf.getServerHostName() + ":" + conf.getServerPort()
					+ "/" + conf.getServerRefName());

			final List<String> status = stub.status();
			assertTrue("Status returned nothing.", status != null
					&& status.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught an Exception");
		}
	}
}
