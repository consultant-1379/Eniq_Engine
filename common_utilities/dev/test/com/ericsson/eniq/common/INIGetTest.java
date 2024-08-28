package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 *
 * @author ejarsok
 *
 */

public class INIGetTest {

  private static File x;

  private static String fileName;

  private final INIGet ig = new INIGet();

  private static Map<String, String> env = System.getenv();
  /**
   * Test set and get methods
   *
   */

  @Test
  public void testSetAndGetSetFile() {
    ig.setFile("File");
    assertEquals("File", ig.getFile());
  }

  @Test
  public void testSetAndGetSetParameter() {
    ig.setParameter("Parameter");
    assertEquals("Parameter", ig.getParameter());
  }

  @Test
  public void testSetAndGetSetParameterValue() {
    ig.setParameterValue("ParameterValue");
    assertEquals("ParameterValue", ig.getParameterValue());
  }

  @Test
  public void testSetAndGetSetSection() {
    ig.setSection("Section");
    assertEquals("Section", ig.getSection());
  }


  /**
   * check that correct parameter value is loaded from INIGetFile File
   *
   */

  @Test
  public void testExecute() {
    fileName = env.get("WORKSPACE")+ File.separator + "INIGetFile";
    x = new File(fileName);
    x.deleteOnExit();

    try {
      PrintWriter pw = new PrintWriter(new FileWriter(x));
      pw.print("[foobar]\n");
      pw.print("Parameter=PAR\n");
      pw.print("[fobr]\n");
      pw.print("Parameter=PAR2\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Can´t write in file!");
    }

    INIGet ig = new INIGet();
    ig.setFile(fileName);
    ig.setSection("foobar");
    ig.setParameter("Parameter");

    ig.execute(Logger.getLogger("Log"));
    assertEquals("PAR", ig.getParameterValue());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(INIGetTest.class);
  }
}
