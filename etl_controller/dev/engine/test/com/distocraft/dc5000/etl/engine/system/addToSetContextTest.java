package com.distocraft.dc5000.etl.engine.system;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * 
 * @author ejarsok
 * 
 */

public class addToSetContextTest {

  private static addToSetContext atsc = null;
  
  private static HashSet hashSet;
  
  private static Statement stm;
  
  @BeforeClass
  public static void init() {
    Long collectionSetId = 1L;
    Long transferActionId = 1L;
    Long transferBatchId = 1L;
    Long connectId = 1L;
    RockFactory rockFact = null;
    SetContext sctx = new SetContext();
    Connection c;

    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
      fail("init() failed, ClassNotFoundException");
    }

    try {
      c = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
      stm = c.createStatement();

      stm.execute("CREATE TABLE Meta_collection_sets (COLLECTION_SET_ID VARCHAR(20), COLLECTION_SET_NAME VARCHAR(20),"
          + "DESCRIPTION VARCHAR(20),VERSION_NUMBER VARCHAR(20),ENABLED_FLAG VARCHAR(20),TYPE VARCHAR(20))");

      stm.executeUpdate("INSERT INTO Meta_collection_sets VALUES('1', 'set_name', 'description', '1', 'Y', 'type')");

    } catch (SQLException e1) {
      e1.printStackTrace();
      fail("init() failed, SQLException e1");
    }
    try {
      rockFact = new RockFactory("jdbc:hsqldb:mem:.", "SA", "", "org.hsqldb.jdbcDriver", "conName", true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Fail RockFactory Exception");
    }
    
    Meta_versions version = new Meta_versions(rockFact);
    Meta_collections collection = new Meta_collections(rockFact);
    Meta_transfer_actions trActions = new Meta_transfer_actions(rockFact);

    atsc = new addToSetContext(version, collectionSetId, collection, transferActionId, transferBatchId, connectId,
        rockFact, trActions, sctx);
    
    hashSet = new HashSet();
    hashSet.add("c1");
    hashSet.add("c2");
    hashSet.add("c3");
    hashSet.add("c4");
    hashSet.add("c5");
    hashSet.add("c6");
  }

  @Test
  public void testExecute() {
    // TODO assertion fix
    Class secretClass = atsc.getClass();
    
    try {
      Field field = secretClass.getDeclaredField("sctx");
      field.setAccessible(true);
      
      atsc.execute();
      SetContext sc = (SetContext) field.get(atsc);

      assertEquals("", sc.get("anyObjectInSetContext"));
      assertEquals(true, sc.containsValue(hashSet));
      assertEquals(10, sc.get("s2"));
      
    } catch (Exception e1) {
      e1.printStackTrace();
      fail("testExecute() failed, Exception");
    }
  }
  
  @AfterClass
  public static void clean() {
    try {
      stm.execute("DROP TABLE Meta_collection_sets");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(addToSetContextTest.class);
  }*/
}
