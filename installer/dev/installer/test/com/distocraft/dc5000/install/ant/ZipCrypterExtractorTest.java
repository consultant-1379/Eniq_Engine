/**
 * 
 */
package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.Object;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;


/**
 * @author ecarbjo
 *
 */
public class ZipCrypterExtractorTest {

	private static ZipCrypterExtractor crypt = null;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		crypt = new ZipCrypterExtractor();
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
		
		/*crypt.setFile("c:/temp/bopacks/");
		crypt.setOutputFile("c:/temp/bopacks_ext/");*/
		String tempDir = System.getProperty("java.io.tmpdir");
		String pathDir = tempDir + "/" + "bopacks";
		String pathDir2 = tempDir + "/" + "bopacks_ext";
		//System.out.println(" pathDir = "+pathDir);
		File f = new File (pathDir);
		File f1 = new File (pathDir2);
		if (!f.exists())
		{
			boolean b = f.mkdir();
			//System.out.println(" Dir done");
		
		}
		if (!f1.exists())
		{
			boolean b1 = f1.mkdir();
			//System.out.println(" Dir done");
		
		}
		
		final Field outputDir = crypt.getClass().getDeclaredField("outputDir");
		outputDir.setAccessible(true);
		final String mockedValue = new String (pathDir);
		File file1 = new File(mockedValue);
		outputDir.set(crypt,file1);
		 
		crypt.setFile(pathDir);
		crypt.setOutputFile(pathDir2);
		
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.distocraft.dc5000.install.ant.ZipCrypterExtractor#execute()}.
	 */
	@Test
	public void testExecute() {
		try {
			crypt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Caught an exception");
		}
	}

}
