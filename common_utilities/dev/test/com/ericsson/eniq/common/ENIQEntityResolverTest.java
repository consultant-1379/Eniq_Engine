package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ejarsok
 *
 */

public class ENIQEntityResolverTest {

  private static Map<String, String> env = System.getenv();


  @Test
  public void testResolveEntity() {
    InputSource is = null;
    ENIQEntityResolver er = new ENIQEntityResolver("Log");

    try {
      is = er.resolveEntity("publicId", "systemId");
      assertNotNull(is);
      assertEquals("publicId", is.getPublicId());
      assertEquals("systemId", is.getSystemId());
    } catch (SAXException e) {
      e.printStackTrace();
      fail("SAXException");
    }
  }

  /**
   * InputSource is = er.resolveEntity("publicId/publicIdFile", "systemId");<br />
   * What should publicIdFile contain??
   *
   */

  @Test
  public void testResolveEntity2() {
    String homeDir = env.get("WORKSPACE");
    System.setProperty("CONF_DIR", homeDir);
    File dtdDir = new File(homeDir, "dtd");
    dtdDir.mkdir();

    InputSource is = null;
    ENIQEntityResolver er = new ENIQEntityResolver("Log");

    try {
      is = er.resolveEntity("publicId", "systemId/systemIdFile");
      assertNotNull(is);
      assertEquals("publicId", is.getPublicId());
      assertEquals("systemIdFile", is.getSystemId());
    } catch (SAXException e) {
      e.printStackTrace();
      fail("SAXException");
    }

    dtdDir.delete();
  }

  @Test
  public void testResolveEntity3() {
    InputSource is = null;
    ENIQEntityResolver er = new ENIQEntityResolver("Log");

    try {
      is = er.resolveEntity(null, null);
      assertNotNull(is);
      assertEquals(null, is.getPublicId());
      assertEquals(null, is.getSystemId());
    } catch (SAXException e) {
      e.printStackTrace();
      fail("SAXException");
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ENIQEntityResolverTest.class);
  }
}
