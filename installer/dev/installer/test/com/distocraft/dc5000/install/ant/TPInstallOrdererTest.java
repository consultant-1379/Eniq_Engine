package com.distocraft.dc5000.install.ant;

import static org.junit.Assert.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.apache.tools.ant.BuildException;
import com.distocraft.dc5000.install.ant.TPInstallOrderer.TPEntry;
import com.ericsson.junit.HelpClass;

/**
 * 
 * @author ejarsok
 */
public class TPInstallOrdererTest {

  private static TPInstallOrderer tpio = new TPInstallOrderer();

  private static Method listIndex;

  private static Method isInList;

  private static Method loadTPE;

  private static Field tpDir;

  private static ArrayList al;

  @BeforeClass
  public static void init() {

    HelpClass hc = new HelpClass();

    Class secretClass = tpio.getClass();

    TPInstallOrderer.TPEntry TPE1 = tpio.new TPEntry();
    TPInstallOrderer.TPEntry TPE2 = tpio.new TPEntry();
    TPE1.name = "tpe1";
    TPE2.name = "tpe2";

    al = new ArrayList();
    al.add(TPE1);
    al.add(TPE2);

    try {

      listIndex = secretClass.getDeclaredMethod("listIndex", new Class[] { String.class, List.class });
      isInList = secretClass.getDeclaredMethod("isInList", new Class[] { String.class, List.class });
      loadTPE = secretClass.getDeclaredMethod("loadTPE", new Class[] { String.class });
      tpDir = secretClass.getDeclaredField("tpDir");
      listIndex.setAccessible(true);
      isInList.setAccessible(true);
      loadTPE.setAccessible(true);
      tpDir.setAccessible(true);

    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed, Exception");
    }

    String homeDir = System.getProperty("user.home");

    File f = hc.createPropertyFile(homeDir, "version.properties",
        "tech_pack.name=DC_Z_ALARM;required_tech_packs.REQ_T_P");
    File ilf = hc.createPropertyFile(homeDir, "InstallListFile", "DC_Z_ALARM");
    // ilf.deleteOnExit();
    hc.zipFile(homeDir, "DC_Z_ALARM_R1A_b55.tpi", f);
    f.delete();
    f = hc.createPropertyFile(homeDir, "version.properties", "tech_pack.name=REQ_T_P");
    hc.zipFile(homeDir, "REQ_T_P_R1A_b55.tpi", f);
    f.delete();
  }

  @Test
  public void testSetAndGetTechpackDirectory() {

    tpio.setTechpackDirectory("TechPack_directory");
    assertEquals("TechPack_directory", tpio.getTechpackDirectory());
  }

  @Test
  public void testSetAndGetInstallListFile() {

    tpio.setInstallListFile("install_list_file");
    assertEquals("install_list_file", tpio.getInstallListFile());
  }

  @Test
  public void testSetAndGetDebug() {

    tpio.setDebug("debug");
    assertEquals("debug", tpio.getDebug());
  }

  @Test
  public void testSetAndGetCheckForRequiredTechPacks() {

    tpio.setCheckForRequiredTechPacks("True");
    assertEquals("true", tpio.getCheckForRequiredTechPacks());
  }

  /**
   * Test method check TPInstallOrderer.TPEntry TPE2 list index in ArrayList
   */
  @Test
  public void testListIndex() {

    try {

      assertEquals(1, listIndex.invoke(tpio, new Object[] { "tpe2", al }));

    } catch (Exception e) {
      e.printStackTrace();
      fail("testListIndex() failed, Exception");
    }

  }

  /**
   * Test method checks that TPInstallOrderer.TPEntry TPE1 is in ArrayList and
   * tpe3 is not
   */
  @Test
  public void testIsInList() {

    try {

      assertEquals(true, isInList.invoke(tpio, new Object[] { "tpe1", al }));
      assertEquals(false, isInList.invoke(tpio, new Object[] { "tpe3", al }));
    } catch (Exception e) {
      e.printStackTrace();
      fail("testIsInList() failed, Exception");
    }
  }

  /**
   * Test method load tech pack properties in .tpi file and create TPEntry
   * object which contains name of tech pack and string array which contains
   * required tech pack names
   */
  @Test
  public void testLoadTPE() {

    File homeDir = new File(System.getProperty("user.home"));

    try {

      tpDir.set(tpio, homeDir);
      TPInstallOrderer.TPEntry tpe = (TPEntry) loadTPE.invoke(tpio, new Object[] { "DC_Z_ALARM" });
      assertEquals("DC_Z_ALARM", tpe.name);
      assertEquals("REQ_T_P", tpe.deps[0]);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * execute method reads InstallListFile's content. The content is tech pack
   * names wanted to install. After that method opens all .tpi files and load
   * all properties and check dependencies. Once all the tech pack and their
   * dependencies are read .tpi file names will be written in InstallListFile
   */
  @Test
  public void testExecute() {

    HelpClass hc = new HelpClass();
    tpio.setTechpackDirectory(System.getProperty("user.home"));
    tpio.setInstallListFile(System.getProperty("user.home") + File.separator + "InstallListFile");
    tpio.setCheckForRequiredTechPacks("true");
    tpio.execute();
    File f = new File(System.getProperty("user.home") + File.separator + "InstallListFile");
    ArrayList al = null;

    try {
      al = hc.readFileToArray(f);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testExecute() failed");
    }

    assertEquals(true, al.contains("DC_Z_ALARM_R1A_b55.tpi"));
    assertEquals(true, al.contains("REQ_T_P_R1A_b55.tpi"));
  }


    @Test
  public void testExecuteWithSpacedNames() {
        final String tplist = "InstallListFile";
        final String location = System.getProperty("java.io.tmpdir");
        final HelpClass hc = new HelpClass();
        final File f = hc.createPropertyFile(location, "version.properties", "tech_pack.name=DC_Z_ALARM");
        hc.createPropertyFile(location, tplist, "DC_Z_ALARM "); // note the space at the end, TPInstallOrderer should trim this off
        hc.zipFile(location, "DC_Z_ALARM_R1A_b55.tpi", f);
        f.delete();

        tpio.setTechpackDirectory(location);
        tpio.setInstallListFile(location + File.separator + tplist);
        tpio.setCheckForRequiredTechPacks("true");
        try{
            tpio.execute();
        } catch (BuildException e){
            fail("testExecuteWithSpacedNames failed : " + e.toString());
        }
  }
  @AfterClass
  public static void clean() {

    /* TODO System.gc remove */
    System.gc();
    File tpi = new File(System.getProperty("user.home") + File.separator + "DC_Z_ALARM_R1A_b55.tpi");
    File tpi2 = new File(System.getProperty("user.home") + File.separator + "REQ_T_P_R1A_b55.tpi");
    File ilf = new File(System.getProperty("user.home") + File.separator + "InstallListFile");
    tpi.delete();
    tpi2.delete();
    ilf.delete();
  }
}
