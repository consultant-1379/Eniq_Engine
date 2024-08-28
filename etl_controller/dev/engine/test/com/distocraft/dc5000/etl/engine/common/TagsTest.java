package com.distocraft.dc5000.etl.engine.common;

import static org.junit.Assert.*;

import java.util.HashMap;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 * 
 */

public class TagsTest {

  @Test
  public void testGetTagPairs() {

    try {
      Tags t = new Tags();
      HashMap hm = t.GetTagPairs("id", ",", "name1=value1,name2=value2");
      assertEquals("value1", hm.get("idname1"));
      assertEquals("value2", hm.get("idname2"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetTagPairs() failed, Exception");
    }
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TagsTest.class);
  }*/
}
