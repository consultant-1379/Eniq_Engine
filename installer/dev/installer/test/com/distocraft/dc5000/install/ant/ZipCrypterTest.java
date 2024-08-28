/**
 * 
 */
package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ecarbjo
 *
 */
public class ZipCrypterTest {

	private static ZipCrypter crypt;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		crypt = new ZipCrypter();
		crypt.setFile("c:/temp/DC_E_IMS_IPW_R2B_b6.tpi");
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
	 * Test method for {@link com.distocraft.dc5000.install.ant.RSAZipCrypter#execute()}.
	 */
	@Test
	public void testExecuteEncrypt() {
		crypt.setCryptType("encrypt");
		crypt.setIsPublicKey("false");
		crypt.setKeyModulate("91904075215482429200974130997378134318659730089278694701294663814671976905189836397175101804787466211425807685632407184853265021082292037775446539705083915756665031257078346103497763827097305749433890688361251048827747830535575010868393704647286975226826020988701838915072852700756010735727623592451574735047");
		crypt.setKeyExponent("15494272350822670556198226549735737157435820342042514880977230274323064418960930502653275246671295820224357982796778415024241653080523135410240151627347482620253514319811320458512506170342554844837745624349328607163139404640677933771711452598878317984827861831877002530137005411348042548281392213546793225993");

		try {
			crypt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Did not complete");
		}
	}

	@Test
	public void testExecuteDecrypt() {
		crypt.setCryptType("decrypt");
		crypt.setIsPublicKey("true");
		crypt.setKeyModulate("91904075215482429200974130997378134318659730089278694701294663814671976905189836397175101804787466211425807685632407184853265021082292037775446539705083915756665031257078346103497763827097305749433890688361251048827747830535575010868393704647286975226826020988701838915072852700756010735727623592451574735047");
		crypt.setKeyExponent("65537");

		try {
			crypt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Did not complete");
		}
	}
	
	@Test
	public void testInvalidZipFile() throws FileNotFoundException {
		String fileName = "c:/temp/test.txt";
		String sampleStr = "This is just a small sample text that I have written";
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
		pw.print(sampleStr);
		pw.close();
		
		crypt.setFile(fileName);
		crypt.setCryptType("encrypt");
		crypt.setIsPublicKey("false");
		crypt.setKeyModulate("91904075215482429200974130997378134318659730089278694701294663814671976905189836397175101804787466211425807685632407184853265021082292037775446539705083915756665031257078346103497763827097305749433890688361251048827747830535575010868393704647286975226826020988701838915072852700756010735727623592451574735047");
		crypt.setKeyExponent("15494272350822670556198226549735737157435820342042514880977230274323064418960930502653275246671295820224357982796778415024241653080523135410240151627347482620253514319811320458512506170342554844837745624349328607163139404640677933771711452598878317984827861831877002530137005411348042548281392213546793225993");
		
		System.out.println("Begin ZipCrypter.execute()");
		boolean exceptionRaised = false;
		try {
			crypt.execute();
		} catch (Exception e) {
			exceptionRaised = true;
		}
		System.out.println("Ended ZipCrypter.execute()");		
		assertTrue("No exception was thrown although a non-valid .zip was tried.", exceptionRaised);
	}
}
