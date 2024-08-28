package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.Project;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.BeforeClass;

import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class GetDBPropertiesTest {

  private static Method setDBProperties;

  private static Method createETLRepConnection;

  private static Method getEtlrepDatabaseConnectionDetails;

  private static Field propertiesFilepath;

  private static Connection c;
  
  private static Statement stm;

  @BeforeClass
  public static void init() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("init() failed, ClassNotFoundException");
    }

    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_databases (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
          + "TYPE_NAME VARCHAR(31), CONNECTION_ID VARCHAR(31), CONNECTION_NAME VARCHAR(31), "
          + "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), DRIVER_NAME VARCHAR(31), "
          + "DB_LINK_NAME VARCHAR(31))");

      stm.executeUpdate("INSERT INTO Meta_databases VALUES('SA', '1', 'USER', '1', 'dwhrep', "
          + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }

    GetDBProperties gdbp = new GetDBProperties();
    Class secretClass = gdbp.getClass();

    try {
      setDBProperties = secretClass.getDeclaredMethod("setDBProperties", Connection.class);
      createETLRepConnection = secretClass.getDeclaredMethod("createETLRepConnection", HashMap.class);
      getEtlrepDatabaseConnectionDetails = secretClass.getDeclaredMethod("getEtlrepDatabaseConnectionDetails", null);
      propertiesFilepath = secretClass.getDeclaredField("propertiesFilepath");
      setDBProperties.setAccessible(true);
      createETLRepConnection.setAccessible(true);
      getEtlrepDatabaseConnectionDetails.setAccessible(true);
      propertiesFilepath.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    stm.execute("DROP TABLE Meta_databases");
  }

  @Test
  public void testSetAndGetConfigurationDirectory() {
    GetDBProperties gdbp = new GetDBProperties();
    gdbp.setConfigurationDirectory("DIR");
    assertEquals("DIR", gdbp.getConfigurationDirectory());
  }

  @Test
  public void testSetAndGetName() {
    GetDBProperties gdbp = new GetDBProperties();
    gdbp.setName("NAME");
    assertEquals("NAME", gdbp.getName());
  }

  @Test
  public void testSetAndGetType() {
    GetDBProperties gdbp = new GetDBProperties();
    gdbp.setType("TYPE");
    assertEquals("TYPE", gdbp.getType());
  }

  @Test
  public void testSetDBProperties() {
    GetDBProperties gdbp = new GetDBProperties();
    gdbp.setName("dwhrep");
    gdbp.setType("USER");
    Project proj = new Project();
    gdbp.setProject(proj);

    try {
      setDBProperties.invoke(gdbp, new Object[] { c });
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetDBProperties() failed");
    }

    proj = gdbp.getProject();
    
    String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
    String actual = proj.getProperty("dwhrepDatabaseUrl") + "," + proj.getProperty("dwhrepDatabaseUsername") + 
                  "," + proj.getProperty("dwhrepDatabasePassword") + "," + proj.getProperty("dwhrepDatabaseDriver");
    
    assertEquals(expected, actual);
    
  }

  @Test
  public void testCreateETLRepConnection() {
    GetDBProperties gdbp = new GetDBProperties();

    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    try {
      Connection c = null;
      c = (Connection) createETLRepConnection.invoke(gdbp, new Object[] { hm });
      assertNotNull(c);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateETLRepConnection() failed");
    }
  }

  @Test
  public void testGetEtlrepDatabaseConnectionDetails() {
    // TODO assertion fix
    GetDBProperties gdbp = new GetDBProperties();
    Project proj = new Project();
    gdbp.setProject(proj);
    String propPath = System.getProperty("user.home") + File.separator + "DatabaseConnectionDetails";
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "DatabaseConnectionDetails",
        "ENGINE_DB_URL=url;ENGINE_DB_USERNAME=uName;ENGINE_DB_PASSWORD=passwd;ENGINE_DB_DRIVERNAME=driver");

    try {
      propertiesFilepath.set(gdbp, propPath);

      HashMap hm = (HashMap) getEtlrepDatabaseConnectionDetails.invoke(gdbp, null);
      
      String expected = "url,uName,passwd,driver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + "," + hm.get("etlrepDatabasePassword") + 
                    "," + hm.get("etlrepDatabaseDriver");
      
      assertEquals(expected, actual);
    
      proj = gdbp.getProject();
      assertEquals("url", proj.getProperty("etlrepDatabaseUrl"));
      assertEquals("uName", proj.getProperty("etlrepDatabaseUsername"));
      assertEquals("passwd", proj.getProperty("etlrepDatabasePassword"));
      assertEquals("driver", proj.getProperty("etlrepDatabaseDriver"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetEtlrepDatabaseConnectionDetails() failed");
    }

    f.delete();
  }

  //This method put properties from file to Project object
  @Test
  public void testExecute() {
    GetDBProperties gdbp = new GetDBProperties();
    Project proj = new Project();
    gdbp.setProject(proj);
    gdbp.setConfigurationDirectory(System.getProperty("user.home"));
    gdbp.setName("etlrep");
    gdbp.setType("user");
    
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "ETLCServer.properties",
        "ENGINE_DB_URL=jdbc:hsqldb:mem:testdb;ENGINE_DB_USERNAME=SA;ENGINE_DB_PASSWORD= ;ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver");
    
    gdbp.execute();
    
    proj = gdbp.getProject(); // Values from file
    
    String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
    String actual = proj.getProperty("etlrepDatabaseUrl") + "," + proj.getProperty("etlrepDatabaseUsername") + 
                  "," + proj.getProperty("etlrepDatabasePassword") + "," + proj.getProperty("etlrepDatabaseDriver");
    
    assertEquals(expected, actual);

    f.delete();
  }
  
  //This method put properties from database to Project object
  @Test
  public void testExecute2() {
    GetDBProperties gdbp = new GetDBProperties();
    Project proj = new Project();
    gdbp.setProject(proj);
    gdbp.setConfigurationDirectory(System.getProperty("user.home"));
    gdbp.setName("dwhrep");
    gdbp.setType("USER");
    
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "ETLCServer.properties",
        "ENGINE_DB_URL=jdbc:hsqldb:mem:testdb;ENGINE_DB_USERNAME=SA;ENGINE_DB_PASSWORD= ;ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver");
    
    gdbp.execute();
    
    proj = gdbp.getProject(); // Values from database
    
    String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
    String actual = proj.getProperty("etlrepDatabaseUrl") + "," + proj.getProperty("etlrepDatabaseUsername") + 
                  "," + proj.getProperty("etlrepDatabasePassword") + "," + proj.getProperty("etlrepDatabaseDriver");
    
    assertEquals(expected, actual);
    
    f.delete();
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(GetDBPropertiesTest.class);
  }*/
}
