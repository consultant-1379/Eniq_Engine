package com.distocraft.dc5000.etl.engine.common;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Test;

public class EngineComTest {

  @Test
  public void testSetAndGetSlotId() {
    EngineCom ec = new EngineCom();
    ec.setSlotId(10);
    assertEquals(10, ec.getSlotId());
  }
  
  @Test
  public void testSetAndGetCommand(){
    EngineCom ec = new EngineCom();
    ec.setCommand("command"); 
    assertEquals("command", ec.getCommand());
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(EngineComTest.class);
  }*/
}
