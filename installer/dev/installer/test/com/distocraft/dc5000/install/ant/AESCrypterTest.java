/**
 * 
 */
package com.distocraft.dc5000.install.ant;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.Key;
import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author ecarbjo
 *
 */
public class AESCrypterTest {

	static private Key key;
	private String fileName = "Tech_Pack_DC_E_CUDB";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	private File getFile(final String name) throws Exception {
		final URL url = ClassLoader.getSystemResource("sql");
		System.out.println("url= "+url);
		
		if(url == null){
			System.out.println("URL IS NULL");
			throw new FileNotFoundException("sql");
		}
		final File sqlBase = new File(url.toURI());
		System.out.println("sqlBase= "+sqlBase);
		
		final String sqlFile = sqlBase.getAbsolutePath() + "/"+name;
		System.out.println("sqlFile = "+sqlFile);
		
		return new File(sqlFile);
	}
	private File newFile(final String name){
		return new File(System.getProperty("java.io.tmpdir")+"/" + name);
	}

	@Test
	public void testAESEncryption() throws Exception {
		long startTime = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream(getFile(fileName+".sql"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		AESCrypter aes = new AESCrypter();
		key = AESCrypter.getRandomKey();
		aes.encrypt(fis, bos);
		
		fis.close();
		FileOutputStream fos = new FileOutputStream(newFile(fileName+".enc.sql"));
		bos.flush();
		fos.write(bos.toByteArray());
		bos.close();
		fos.close();
		double duration = System.currentTimeMillis() - startTime/1000.0;
		System.out.println("Encryption complete in " + duration + " seconds");
	}

	@Test
	public void testAESDecryption() throws Exception {
		long startTime = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream(getFile(fileName+".enc.sql"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		AESCrypter aes = new AESCrypter();
		aes.decrypt(fis, bos, key);
		System.out.println("Key: " + new BigInteger(key.getEncoded()));
		
		fis.close();
		FileOutputStream fos = new FileOutputStream(newFile(fileName+".dec.sql"));
		bos.flush();
		fos.write(bos.toByteArray());
		bos.close();
		fos.close();		
		double duration = System.currentTimeMillis() - startTime/1000.0;
		System.out.println("Decryption complete in " + duration + " seconds");
	}

}
