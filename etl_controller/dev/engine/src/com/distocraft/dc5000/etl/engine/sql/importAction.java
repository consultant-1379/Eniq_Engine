package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.importexport.ETLCExport;
import com.distocraft.dc5000.etl.importexport.ETLCImport;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * Common parent for all Loader classes <br>
 * <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * <br>
 * Where-column of this action needs to a serialized properties-object which is
 * stored in class variable whereProps. ActionContents-column shall contain
 * velocity template evaluated to get load clause <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen, savinen, melantie, melkko
 */
public class importAction extends SQLOperation {

  protected Logger log = Logger.getLogger("etlengine.CopyAggregationRules");
  protected Logger fileLog = Logger.getLogger("etlengine.CopyAggregationRules");
  protected Logger sqlLog = Logger.getLogger("etlengine.CopyAggregationRules"); 
  protected static final Logger statlog = Logger.getLogger("etlengine.CopyAggregationRules");

  protected Properties whereProps;


  protected Long techPackId;
  protected Meta_versions version;
  protected Meta_collections set;

  protected SetContext sctx;
  protected RockFactory rock;
  
  protected String action = "";
  protected String where = "";
  protected ConnectionPool connectionPool = null;
  

  private static Vector engines = new Vector(); // VEngine instance cache

  public importAction(Meta_versions      version,
        Long        collectionSetId,
        Meta_collections        collection,
        Long        transferActionId,
        Long        transferBatchId,
        Long        connectId,
        RockFactory rockFact,
        ConnectionPool connectionPool,
        Meta_transfer_actions trActions,
        String batchColumnName, SetContext sctx) throws EngineMetaDataException{

    super(version, collectionSetId, collection, transferActionId,transferBatchId, connectId, rockFact, connectionPool, trActions);

    this.version = version;
    this.rock = rockFact;
    this.action = trActions.getAction_contents();
    this.where = trActions.getWhere_clause();
    this.connectionPool = connectionPool;
    this.sctx = sctx;
    
  }

  public void execute() throws EngineException {


    log.fine("Executing...");
    Connection connection = null;
    Properties  prop = new Properties();
    String tmp="";
    
    Map tableToFile = null;

    try {

    	prop = stringToProperty(where);
    	String oldName = prop.getProperty("replace.tablename.old");
    	String newName = prop.getProperty("replace.tablename.new");
        RockFactory r = this.getConnection();      
        connection = r.getConnection();
  	
        
        
        
        StringReader sr = new StringReader(((String)sctx.get("exportData")).replaceAll(oldName,newName));
    	ETLCImport importAgg = new ETLCImport(prop,connection);
    	importAgg.doImport(sr);
    	
    } catch (Exception e) {
      log.log(java.util.logging.Level.WARNING, "Could Not Import Table(s)", e);
    } finally {

      try {
        connection.commit();
      } catch (Exception e) {
        log.log(Level.FINEST, "error committing", e);
      }

    }

  }
  
  
  /**
   * Tries to create Properties object from a String.
   */
  protected Properties stringToProperty(String str) throws Exception {

    Properties prop = null;

    if (str != null && str.length() > 0) {
      prop = new Properties();
      ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }

    return prop;

  }
  
}
