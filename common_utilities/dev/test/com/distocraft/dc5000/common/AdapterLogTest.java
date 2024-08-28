package com.distocraft.dc5000.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 * 
 * @author ejarsok
 * 
 */

public class AdapterLogTest {

  private static File adapter;

  static Map<String, String> env = System.getenv();

  private static String homeDir = env.get("WORKSPACE");

  private static String dateID = "dateID_value";
  
  private static AdapterLog aL;
  
  private static HashMap map;
  
  private static File file;
  
  private static Long time;

  @BeforeClass
  public static void init() {
    Properties prop = new Properties();
    prop.setProperty("SessionHandling.log.ADAPTER.inputTableDir", homeDir);
    time = System.currentTimeMillis();

    file = new File(homeDir, "adapterLogFile");
    file.deleteOnExit();
 
    map = new HashMap();

    map.put("dateID", dateID);
    map.put("sessionStartTime", "100");
    map.put("sessionEndTime", "200");
    map.put("srcLastModified", String.valueOf(time));

    try {
      StaticProperties.giveProperties(prop);
      aL = new AdapterLog();
      
    } catch (Exception e1) {
      e1.printStackTrace();
      fail("testAdapterLog failed");
    }
  }
  
  @Test
  public void testAdapterLog() {
    aL.log(map);
    adapter = new File(homeDir, "ADAPTER." + dateID + ".unfinished");
    adapter.deleteOnExit();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    try {
      //String expected = "sessionID\tbatchID\t" + dateID + "fileName\t" + sdf.format(new Date(100L)) + "\t" + sdf.format(new Date(200L)) + "source\tstatus\t" + sdf.format(new Date(time)) + "\t0\t\n";
      String expected = "null\tnull\t" + dateID + "\tLOADED\tnull\t" + sdf.format(new Date(100L)) + "\t" + sdf.format(new Date(200L)) + "\tnull\tnull\t" + sdf.format(new Date(time)) + "\t0\t\t\t\t\t";
      String actual = new HelpClass().readFileToString(adapter);
      
      assertEquals(expected, actual);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAdapterLog() failed");
    }
  }
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(AdapterLogTest.class);
  }
}
