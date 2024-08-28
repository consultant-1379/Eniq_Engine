package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.BeforeClass;
import ssc.rockfactory.RockFactory;

/**
 * @author ejarsok
 */
public class DirectoryCheckerAndDWHMInstallTest {

  private DirectoryCheckerAndDWHMInstall dcdi = new DirectoryCheckerAndDWHMInstall();

  private static RockFactory rockFact;

  private static Statement stm;

  @BeforeClass
  public static void init() {

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
      stm.execute("CREATE TABLE Meta_collections (COLLECTION_ID BIGINT, "
          + "COLLECTION_NAME VARCHAR(20), COLLECTION VARCHAR(20), MAIL_ERROR_ADDR VARCHAR(20), "
          + "MAIL_FAIL_ADDR VARCHAR(20), MAIL_BUG_ADDR VARCHAR(20), MAX_ERRORS BIGINT, "
          + "MAX_FK_ERRORS BIGINT, MAX_COL_LIMIT_ERRORS BIGINT, CHECK_FK_ERROR_FLAG VARCHAR(20), "
          + "CHECK_COL_LIMITS_FLAG VARCHAR(20), LAST_TRANSFER_DATE TIMESTAMP,"
          + "VERSION_NUMBER VARCHAR(20), COLLECTION_SET_ID BIGINT, USE_BATCH_ID VARCHAR(20), PRIORITY BIGINT,"
          + "QUEUE_TIME_LIMIT BIGINT, ENABLED_FLAG VARCHAR(20), SETTYPE VARCHAR(20), FOLDABLE_FLAG VARCHAR(20),"
          + "MEASTYPE VARCHAR(20), HOLD_FLAG VARCHAR(20), SCHEDULING_INFO VARCHAR(20))");
      stm.executeUpdate("INSERT INTO Meta_collections VALUES(1, 'dirname', 'collection', 'me', 'mf', 'mb' ,"
          + "5, 5, 5, 'y', 'y', 2006-10-10, '1.0', 1, '1', 1, 100, 'Y', 'type', 'n', 'mtype', 'y', 'info')");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException");
    }

    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:testdb", "SA", "", "org.hsqldb.jdbcDriver", "con", true, -1);
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed, xception");
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {

    stm.execute("DROP TABLE Meta_collections");
  }

  @Ignore
  public void testExecute() {

    /* TODO runCommand method problem!! */
  }

  @Test
  public void testSetAndGetTechPackName() {

    dcdi.setTechPackName("TECH_PACK");
    assertEquals("TECH_PACK", dcdi.getTechPackName());
  }

  @Test
  public void testSetAndGetBinDirectory() {

    dcdi.setBinDirectory("BIN_DIR");
    assertEquals("BIN_DIR", dcdi.getBinDirectory());
  }

  @Test
  public void testSetAndGetTechPackVersion() {

    dcdi.setTechPackVersion("v 1.0");
    assertEquals("v 1.0", dcdi.getTechPackVersion());
  }

  @Test
  public void testSetAndGetInstallingInterface() {

    dcdi.setInstallingInterface("INS_INTER");
    assertEquals("INS_INTER", dcdi.getInstallingInterface());
  }

  @Test
  public void testSetAndGetExitValue() {

    dcdi.setExitValue(10);
    assertEquals((Integer) 10, dcdi.getExitValue());
  }

  @Test
  public void testDirectoryCheckerSetExists() {

    Class secretClass = dcdi.getClass();
    try {

      Field field = secretClass.getDeclaredField("etlrepRockFactory");
      Field field2 = secretClass.getDeclaredField("directoryCheckerSetName");
      field.setAccessible(true);
      field2.setAccessible(true);
      field.set(dcdi, rockFact);
      field2.set(dcdi, "dirname");
      dcdi.setTechPackVersion("1.0");
      assertEquals(true, dcdi.directoryCheckerSetExists());
      stm.executeUpdate("UPDATE Meta_collections SET COLLECTION_NAME = 'dwhm'" + "WHERE COLLECTION_ID = 1");
      assertEquals(false, dcdi.directoryCheckerSetExists());

    } catch (Exception e) {
      e.printStackTrace();
      fail("testDirectoryCheckerSetExists() failed, Exception");
    }
  }

  @Test
  public void testDwhmInstallSetExists() {

    Class secretClass = dcdi.getClass();
    try {

      Field field = secretClass.getDeclaredField("etlrepRockFactory");
      Field field2 = secretClass.getDeclaredField("dwhmInstallSetName");
      field.setAccessible(true);
      field2.setAccessible(true);
      field.set(dcdi, rockFact);
      field2.set(dcdi, "dwhm");
      dcdi.setTechPackVersion("1.0");
      assertEquals(true, dcdi.dwhmInstallSetExists());
      stm.executeUpdate("UPDATE Meta_collections SET COLLECTION_NAME = 'foobar'" + "WHERE COLLECTION_ID = 1");
      assertEquals(false, dcdi.dwhmInstallSetExists());

    } catch (Exception e) {
      e.printStackTrace();
      fail("testDirectoryCheckerSetExists() failed, Exception");
    }
  }

  @Test
  public void testCreateRockFactory() {

    Class secretClass = dcdi.getClass();
    try {

      Method method = secretClass.getDeclaredMethod("createRockFactory", new Class[] { String.class, String.class,
          String.class, String.class });
      method.setAccessible(true);
      RockFactory rf = (RockFactory) method.invoke(dcdi, new Object[] { "jdbc:hsqldb:mem:testdb", "SA", "",
          "org.hsqldb.jdbcDriver" });
      String expected = "SA,,jdbc:hsqldb:mem:testdb,org.hsqldb.jdbcDriver";
      String actual = rf.getUserName() + "," + rf.getPassword() + "," + rf.getDbURL() + "," + rf.getDriverName();
      assertEquals(expected, actual);

    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreateRockFactory() failed, Exception");
    }
  }
}
