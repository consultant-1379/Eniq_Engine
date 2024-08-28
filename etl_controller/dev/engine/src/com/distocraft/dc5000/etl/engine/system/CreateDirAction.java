package com.distocraft.dc5000.etl.engine.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

/**
 * 
 * creates directory (or all directories leading to it) . Path is read from
 * Action_contents.
 * 
 * /1/2/3/4
 * 
 * if directories 1,2 and exists only 4 is created. if only 1 is allready
 * created then 2,3 and 4 are created.
 * 
 * 
 * 
 * TODO usage TODO used databases/tables TODO used properties
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 * $id$
 */
public class CreateDirAction extends TransferActionBase {

  private static Logger log = Logger.getLogger("etlengine.CreateDirAction");

  /**
   * Empty protected constructor
   * 
   */
  protected CreateDirAction() {
  }

  /**
   * Constructor
   * 
   * @param versionNumber
   *          metadata version
   * @param collectionSetId
   *          primary key for collection set
   * @param collectionId
   *          primary key for collection
   * @param transferActionId
   *          primary key for transfer action
   * @param transferBatchId
   *          primary key for transfer batch
   * @param connectId
   *          primary key for database connections
   * @param rockFact
   *          metadata repository connection object
   * @param connectionPool
   *          a pool for database connections in this collection
   * @param trActions
   *          object that holds transfer action information (db contents)
   * 
   */
  public CreateDirAction(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, Meta_transfer_actions trActions) {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);

  }

  /**
   * 
   */
  public void execute() throws EngineException {
    String directory = this.getTrActions().getWhere_clause();

    if(directory.indexOf("${") >= 0) {
      int start = directory.indexOf("${");
      int end = directory.indexOf("}",start);
      
      if(end >= 0) {
        String variable = directory.substring(start+2,end);
        String val = System.getProperty(variable);
        String result = directory.substring(0,start) + val + directory.substring(end+1);
        directory = result;
      }
    }
    
    try {

      Properties prop = stringToProperty(this.getTrActions().getAction_contents());

      File dir = new File(directory);

      if (!dir.isDirectory()) {

        if (!dir.mkdir()) {

          throw new Exception("Error while executing mkdir " + directory);

        }

      } else {

        log.info("Directory: " + directory + " allready exists ");
      }

      chmod(prop.getProperty("permission", "750"), dir);

      if (!prop.getProperty("group", "").equalsIgnoreCase(""))
        chgrp(prop.getProperty("group", ""), dir);

      if (!prop.getProperty("owner", "").equalsIgnoreCase(""))
        chown(prop.getProperty("owner", ""), dir);

    } catch (Exception e) {
      throw new EngineException("Error handling directory " + directory, new String[] { directory }, e, this, this
          .getClass().getName(), EngineConstants.ERR_TYPE_SYSTEM);
    }

  }

  private void chmod(String cmd, File file) throws Exception {

    log.info("changing " + file.getAbsolutePath() + " permissions to " + cmd);
    try {
      Process p = Runtime.getRuntime().exec("chmod " + cmd + " " + file.getAbsolutePath());
      if (p.waitFor() > 0) {
        throw new Exception("Error while executing chmod " + cmd + " " + file.getAbsolutePath());
      }

    } catch (Exception e) {
      log.warning("could not change " + file.getAbsolutePath() + " permissions to (" + cmd + ")");
      // throw new Exception(e);
    }

  }

  private void chown(String cmd, File file) throws Exception {
    log.info("changing " + file.getAbsolutePath() + " owner to " + cmd);

    try {
      Process p = Runtime.getRuntime().exec("chown " + cmd + " " + file.getAbsolutePath());
      if (p.waitFor() > 0) {
        throw new Exception("Error while executing chown " + cmd + " " + file.getAbsolutePath());
      }
    } catch (Exception e) {
      log.warning("could not change " + file.getAbsolutePath() + " owner to (" + cmd + ")");
      // throw new Exception(e);
    }

  }

  private void chgrp(String cmd, File file) throws Exception {
    log.info("changing " + file.getAbsolutePath() + " group to " + cmd);

    try {
      Process p = Runtime.getRuntime().exec("chgrp " + cmd + " " + file.getAbsolutePath());
      if (p.waitFor() > 0) {
        throw new Exception("Error while executing chgrp  " + cmd + " " + file.getAbsolutePath());
      }
    } catch (Exception e) {
      log.warning("could not change " + file.getAbsolutePath() + " group to (" + cmd + ")");
      // throw new Exception(e);
    }

  }

  protected Properties stringToProperty(String str) throws Exception {

    Properties prop = new Properties();

    if (str != null && str.length() > 0) {
      ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }

    return prop;

  }

  protected String propertyToString(Properties prop) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    prop.store(baos, "");

    return baos.toString();
  }

}
