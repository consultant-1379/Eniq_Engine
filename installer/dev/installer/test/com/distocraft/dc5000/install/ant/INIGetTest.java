package com.distocraft.dc5000.install.ant;

import org.apache.tools.ant.Project;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.AfterClass;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * 
 * @author ejarsok
 *
 */

public class INIGetTest {

  private static File x;

  private static String fileName;

  /**
   * Test set and get methods
   *
   */
  
  @Test
  public void testSetAndGetFile() {
    INIGet ig = new INIGet();
    ig.setFile("File");
    assertEquals("File", ig.getFile());
  }
  
  @Test
  public void testSetAndGetParameter() {
    INIGet ig = new INIGet();
    ig.setParameter("Parameter");
    assertEquals("Parameter", ig.getParameter());
  }
  
  @Test
  public void testSetAndGetSection() {
    INIGet ig = new INIGet();
    ig.setSection("Section");
    assertEquals("Section", ig.getSection());
  }

  /**
   * check that correct parameter value is loaded from INIGetFile file
   * 
   */

  @Test
  public void testExecute() {
    fileName = System.getProperty("user.home") + File.separator + "INIGetFile";
    x = new File(fileName);

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
    Project proj = new Project();
    ig.setProject(proj);
    ig.setFile(fileName);
    ig.setSection("foobar");
    ig.setParameter("Parameter");

    ig.execute();
    proj = ig.getProject();
    assertEquals("PAR", proj.getProperty("foobar.Parameter"));
  }

  @AfterClass
  public static void clean() {
    x = new File(fileName);
    x.delete();
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(INIGetTest.class);
  }
}
