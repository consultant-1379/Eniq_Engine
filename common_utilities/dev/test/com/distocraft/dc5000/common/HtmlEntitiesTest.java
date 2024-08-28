package com.distocraft.dc5000.common;

import static org.junit.Assert.*;
import java.util.logging.Logger;
import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the HtmlEntities class in com.distrocraft.dc5000.common. <br>
 * <br>
 * Testing if different HTML tags are converted into characters and the other
 * way around.
 * 
 * @author EJAAVAH
 * 
 */
public class HtmlEntitiesTest {

  private static HtmlEntities objUnderTest;

  private static Logger log;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    log = Logger.getLogger("testLogger");

    try {
      objUnderTest = new HtmlEntities();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    objUnderTest = null;
  }

  /**
   * Testing if different html tags are converted to symbolic characaters.
   */
  @Test
  public void testConvertHtmlEntities() throws Exception {
    String TestString = "&Testing html converter; euro - &euro;, Auml - &Auml;, frac12 - &frac12; &#x48; &#48;";
    assertEquals("&Testing html converter; euro - €, Auml - Ä, frac12 - ½ H 0", objUnderTest.convertHtmlEntities(
        TestString, log));
  }

  /**
   * Testing if different symbolic characters are converted into HTML tags.
   */
  @Test
  public void testCreateHtmlEntities() throws Exception {
    // Test for the html creator (symbolic chars to html tags)
    String TestString = "€-euro,Ä-Auml";
    assertEquals("&euro;-euro,&Auml;-Auml", objUnderTest.createHtmlEntities(TestString));
  }

  // Making the test work with ant 1.6.5 and JUnit 4.x
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(HtmlEntitiesTest.class);
  }*/  //Commented out because CI makes it redundant.
}
