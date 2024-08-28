package com.ericsson.eniq.common;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

public class DatabaseConnectionsTest {

	private static DatabaseConnections objUnderTest;
	private static Statement stmt;
	private static Connection con = null;
	private static Map<String, String> env = System.getenv();
	private static String homeDir = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		/* Creating connection to hsql database and create tables in there */
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			con = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa",
					"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
				+ "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
				+ "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
				+ "DB_LINK_NAME VARCHAR(31))");
		stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'USER', 1, 'dwh', "
				+ "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");
		stmt.executeUpdate("INSERT INTO Meta_databases VALUES('sa', '1', 'USER', 1, 'dwhrep', "
				+ "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

		/* Creating ETLC property file */
                homeDir = env.get("WORKSPACE");
		File ETLCConfFile = new File(homeDir + File.separator + "ETLCServer.properties");
		ETLCConfFile.deleteOnExit();
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(ETLCConfFile));
			pw.print("ENGINE_DB_URL=jdbc:hsqldb:mem:testdb\n");
			pw.print("ENGINE_DB_USERNAME=sa\n");
			pw.print("ENGINE_DB_PASSWORD=\n");
			pw.print("ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		/* Cleaning up after every test */
		//con = null;
		//objUnderTest = null;
		stmt.execute("DROP TABLE Meta_databases");
	}

	@Test
	public void getETLRepConnectionTest() {
		RockFactory etlRep = null;
		objUnderTest.conf_dir = homeDir;
		String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		String actual = "";
		String resultSeperator = ", ";
		try {		
			etlRep = DatabaseConnections.getETLRepConnection();
			actual = etlRep.getDbURL() + resultSeperator
					+ etlRep.getUserName() + resultSeperator
					+ etlRep.getPassword() + resultSeperator
					+ etlRep.getDriverName();

		} catch (SecurityException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void getDwhDBConnection() {
		RockFactory dwhDB = null;
		objUnderTest.conf_dir = homeDir;
		String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		String actual = "";
		String resultSeperator = ", ";
		try {		
			dwhDB = DatabaseConnections.getETLRepConnection();
			actual = dwhDB.getDbURL() + resultSeperator
					+ dwhDB.getUserName() + resultSeperator
					+ dwhDB.getPassword() + resultSeperator
					+ dwhDB.getDriverName();

		} catch (SecurityException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		assertEquals(expected, actual);
	}
		
	@Test
	public void getDwhRepConnection() {
		RockFactory dwhRep = null;
		objUnderTest.conf_dir = homeDir;
		String expected = "jdbc:hsqldb:mem:testdb, sa, , org.hsqldb.jdbcDriver";
		
		String actual = "";
		String resultSeperator = ", ";
		try {		
			dwhRep = DatabaseConnections.getETLRepConnection();
			actual = dwhRep.getDbURL() + resultSeperator
					+ dwhRep.getUserName() + resultSeperator
					+ dwhRep.getPassword() + resultSeperator
					+ dwhRep.getDriverName();

		} catch (SecurityException e) {
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		assertEquals(expected, actual);
	}

}
