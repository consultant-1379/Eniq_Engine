package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.Project;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

/**
 * 
 * @author ejarsok
 *
 */

public class GetInstalledTechPacksTest {

  private GetInstalledTechPacks gitp = new GetInstalledTechPacks();
  
  private static RockFactory rockFact;
  
  private static Statement stm;

  @BeforeClass
  public static void init() {
    File prop = new File(System.getProperty("user.home"), "ETLCServer.properties");

    try {
      PrintWriter pw = new PrintWriter(new FileWriter(prop));
      pw.write("ENGINE_DB_URL=jdbc:hsqldb:mem:testdb\n");
      pw.write("ENGINE_DB_USERNAME=SA\n");
      pw.write("ENGINE_DB_PASSWORD= \n");
      pw.write("ENGINE_DB_DRIVERNAME=org.hsqldb.jdbcDriver\n");
      pw.close();
    } catch (IOException e1) {
      e1.printStackTrace();
      fail("Failed, can't write in file.");
    }

    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("init() failed, ClassNotFoundException");
    }
    Connection c;
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
    
    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
    } catch (SQLException e) {
      e.printStackTrace();
      fail("init() failed, SQLException");
    } catch (RockException e) {
      e.printStackTrace();
      fail("init() failed, RockException");
    }
  }
  
  @Test
  public void testSetAndGetConfigurationDirectory() {
    gitp.setConfigurationDirectory("CONF_DIR");
    assertEquals("CONF_DIR", gitp.getConfigurationDirectory());
  }

  @Test
  public void testSetAndGetShowNames() {
    gitp.setShowNames("SHOW_NAMES");
    assertEquals("SHOW_NAMES", gitp.getShowNames());
  }

  @Test
  public void testSetAndGetShowProductNumbers() {
    gitp.setShowProductNumbers("SHOW_PNUMB");
    assertEquals("SHOW_PNUMB", gitp.getShowProductNumbers());
  }

  @Test
  public void testSetAndGetShowVersionNumbers() {
    gitp.setShowVersionNumbers("SHOW_VNUMB");
    assertEquals("SHOW_VNUMB", gitp.getShowVersionNumbers());
  }
  
  @Test
  public void testGetDatabaseConnectionDetails() {

    HashMap hm;

    GetInstalledTechPacks instance = new GetInstalledTechPacks();
    Class secretClass = instance.getClass();

    try {
      Field field = secretClass.getDeclaredField("propertiesFilepath");
      Method method = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      field.setAccessible(true);
      method.setAccessible(true);
      field.set(instance, System.getProperty("user.home") + File.separator + "ETLCServer.properties");

      Project proj = new Project();
      instance.setProject(proj);
      hm = (HashMap) method.invoke(instance, null);

      String expected = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + "," + hm.get("etlrepDatabasePassword") + 
                    "," + hm.get("etlrepDatabaseDriver");
      
      assertEquals(expected, actual);

      proj = instance.getProject();
      Hashtable ht = proj.getProperties();
      assertEquals("jdbc:hsqldb:mem:testdb", ht.get("etlrepDatabaseUrl"));
      assertEquals("SA", ht.get("etlrepDatabaseUsername"));
      assertEquals("", ht.get("etlrepDatabasePassword"));
      assertEquals("org.hsqldb.jdbcDriver", ht.get("etlrepDatabaseDriver"));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetDatabaseConnectionDetails() failed, Exception");
    }
  }
  
  @Test
  public void testCreateEtlrepRockFactory() {
    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");

    GetInstalledTechPacks instance = new GetInstalledTechPacks();
    Class secretClass = instance.getClass();

    try {
      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);
      RockFactory rf = (RockFactory) method.invoke(instance, new Object[] { hm });

      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateEtlrepRockFactory() failed, Exception");
    }
  }

  @Test
  public void testCreateDwhrepRockFactory() {
    GetInstalledTechPacks instance = new GetInstalledTechPacks();
    Class secretClass = instance.getClass();

    try {
      Method method = secretClass.getDeclaredMethod("createDwhrepRockFactory", null);
      method.setAccessible(true);

      instance.etlrepRockFactory = rockFact;
      method.invoke(instance, null);

      String actual = "jdbc:hsqldb:mem:testdb,SA,,org.hsqldb.jdbcDriver";
      String expected = instance.dwhrepRockFactory.getDbURL() + "," + instance.dwhrepRockFactory.getUserName() + 
                        "," + instance.dwhrepRockFactory.getPassword() + "," + instance.dwhrepRockFactory.getDriverName();
      
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("CreateDwhrepRockFactory() failed, Exception");
    }
  }
  
  @Ignore
  public void testExecute() {
    // TODO execute only print data
    fail("Not yet implemented");
  }
  
  @AfterClass
  public static void clean() throws Exception {
    // TODO System.gc remove
    System.gc();
    File prop = new File(System.getProperty("user.home"), "ETLCServer.properties");
    prop.delete();
    stm.execute("DROP TABLE Meta_databases");
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(GetInstalledTechPacksTest.class);
  }*/
}
