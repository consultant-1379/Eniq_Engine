/**
 * 
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ecarbjo
 *
 */
public class LicensingSettingsTest {

	private static LicensingSettings settings = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		settings = new LicensingSettings();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
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
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerHostName()}.
	 */
	@Test
	public void testGetServerHostName() {
		final String server = settings.getServerHostName();
		assertTrue(server != null);
		assertTrue(!server.equals(""));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerPort()}.
	 */
	@Test
	public void testGetServerPort() {
		assertTrue(settings.getServerPort() > 0);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getServerRefName()}.
	 */
	@Test
	public void testGetServerRefName() {
		final String ref = settings.getServerRefName();
		assertTrue(ref != null);
		assertTrue(!ref.equals(""));
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getLicensingServers()}.
	 */
	@Test
	public void testGetLicensingServers() {
		final String[] servers = settings.getLicensingServers();
		assertTrue(servers != null);
		assertTrue(servers.length > 0);
		for (int i = 0; i<servers.length; i++) {
			assertTrue(servers[i] != null);
			assertTrue(!servers[i].equals(""));
			assertTrue(servers[i].trim().equals(servers[i]));
		}
	}

	/**
	 * Test method for {@link com.ericsson.eniq.licensing.cache.LicensingSettings#getMappingFile()}.
	 */
/*	@Test
	public void testGetMappingFile() {
		assertTrue(settings.getMappingFile() != null);
		assertTrue(!settings.getMappingFile().equals(""));
	}*/

}
