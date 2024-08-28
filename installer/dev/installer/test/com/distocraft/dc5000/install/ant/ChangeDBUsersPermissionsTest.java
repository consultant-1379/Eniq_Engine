package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;

import org.apache.tools.ant.Project;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.BeforeClass;

import ssc.rockfactory.RockFactory;

import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 *
 */

public class ChangeDBUsersPermissionsTest {

  private static RockFactory rockFact;
  
  private static Method getDatabaseConnectionDetails;
  
  private static Method createDwhRockFactory;
  
  private static Method changeDBPermissions;
  
  private static Field etlrepRockFactory;
  
  private static Field dwhRockFactory;
  
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
      
      stm.executeUpdate("INSERT INTO Meta_databases VALUES('SA', '1', 'DBA', '1', 'dwh', "
          + "'jdbc:hsqldb:mem:testdb', '', 'description', 'org.hsqldb.jdbcDriver', 'dblinkname')");
      
    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }
    
    
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    Class secretClass = cup.getClass();
    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
      
      getDatabaseConnectionDetails = secretClass.getDeclaredMethod("getDatabaseConnectionDetails", null);
      createDwhRockFactory = secretClass.getDeclaredMethod("createDwhRockFactory", null);
      changeDBPermissions = secretClass.getDeclaredMethod("changeDBPermissions", null);
      etlrepRockFactory = secretClass.getDeclaredField("etlrepRockFactory");
      dwhRockFactory = secretClass.getDeclaredField("dwhRockFactory");
      propertiesFilepath = secretClass.getDeclaredField("propertiesFilepath");
      getDatabaseConnectionDetails.setAccessible(true);
      createDwhRockFactory.setAccessible(true);
      changeDBPermissions.setAccessible(true);
      etlrepRockFactory.setAccessible(true);
      dwhRockFactory.setAccessible(true);
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
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    cup.setConfigurationDirectory("confDir");
    assertEquals("confDir", cup.getConfigurationDirectory());
  }

  @Test
  public void testSetAndGetAction() {
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    cup.setAction("action");
    assertEquals("action", cup.getAction());
  }

  @Test
  public void testSetAndGetDbUser() {
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    cup.setDbUser("DBuser");
    assertEquals("DBuser", cup.getDbUser());
  }
  
  @Test
  public void testGetDatabaseConnectionDetails() {
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    Project proj = new Project();
    cup.setProject(proj);
    String propPath = System.getProperty("user.home") + File.separator + "DatabaseConnectionDetails";
    HelpClass hc = new HelpClass();
    File f = hc.createPropertyFile(System.getProperty("user.home"), "DatabaseConnectionDetails",
        "ENGINE_DB_URL=url;ENGINE_DB_USERNAME=uName;ENGINE_DB_PASSWORD=passwd;ENGINE_DB_DRIVERNAME=driver");

    try {
      propertiesFilepath.set(cup, propPath);

      HashMap hm = (HashMap) getDatabaseConnectionDetails.invoke(cup, null);

      String expected = "url,uName,passwd,driver";
      String actual = hm.get("etlrepDatabaseUrl") + "," + hm.get("etlrepDatabaseUsername") + "," + hm.get("etlrepDatabasePassword") + 
                    "," + hm.get("etlrepDatabaseDriver");
      
      assertEquals(expected, actual);
      

      proj = cup.getProject();
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
  
  @Test
  public void testCreateEtlrepRockFactory() {
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    HashMap hm = new HashMap();
    hm.put("etlrepDatabaseUsername", "SA");
    hm.put("etlrepDatabasePassword", "");
    hm.put("etlrepDatabaseUrl", "jdbc:hsqldb:mem:testdb");
    hm.put("etlrepDatabaseDriver", "org.hsqldb.jdbcDriver");

    Class secretClass = cup.getClass();
    try {
      Method method = secretClass.getDeclaredMethod("createEtlrepRockFactory", new Class[] { HashMap.class });
      method.setAccessible(true);

      RockFactory rf = (RockFactory) method.invoke(cup, new Object[] { hm });

      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      
      assertEquals(expected, actual);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateEtlrepRockFactory() failed, Exception");
    }
  }
  
  @Test
  public void testCreateDwhRockFactory() {
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();

    try {
      etlrepRockFactory.set(cup, rockFact);
      assertNull(dwhRockFactory.get(cup));

      createDwhRockFactory.invoke(cup, null);

      assertNotNull(dwhRockFactory.get(cup));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateDwhrepRockFactory()");
    }
  }
  
  @Ignore
  public void testChangeDBPermissions() {
    //  TODO CALL lock_user procedure problem
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    cup.setAction("lock");
    cup.setDbUser("all");
    try {
      dwhRockFactory.set(cup, rockFact);
      Integer i = (Integer) changeDBPermissions.invoke(cup, null);
      assertEquals((Integer) 0, i);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChangeDBPermissions() failed");
    }
  }
  
  /**
   * If Action = null or empty string, method returns integer 1
   *
   */
  
  @Ignore
  public void testChangeDBPermissions2() {
    //  TODO CALL lock_user procedure problem
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    try {
      Integer i = (Integer) changeDBPermissions.invoke(cup, null);
      assertEquals((Integer) 1, i);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChangeDBPermissions() failed");
    }
  }
  
  /**
   * if dbUser = null or empty string, method returns integer 2
   *
   */
  
  @Ignore
  public void testChangeDBPermissions3() {
    //  TODO CALL lock_user procedure problem
    ChangeDBUsersPermissions cup = new ChangeDBUsersPermissions();
    cup.setAction("action");
    try {
      Integer i = (Integer) changeDBPermissions.invoke(cup, null);
      assertEquals((Integer) 2, i);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testChangeDBPermissions() failed");
    }
  }
  
  @Ignore
  public void testExecute() {
    //  TODO CALL lock_user procedure problem
    fail("Not yet implemented");
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ChangeDBUsersPermissionsTest.class);
  }*/
}
