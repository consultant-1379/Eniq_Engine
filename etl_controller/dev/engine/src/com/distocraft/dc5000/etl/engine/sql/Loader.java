package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.engine.system.ETLCEventHandler;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.ActivationCache;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.ericsson.eniq.common.VelocityPool;

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
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>tablename</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>taildir</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>failedDir</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * 
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen, savinen, melantie, melkko
 */
public abstract class Loader extends SQLOperation {

  protected Logger log = null;

  protected Logger fileLog = null;

  protected Logger sqlLog = null;

  protected Logger sqlErrLog = null;

//These are the load parameters for ASCII loading.
  private static final String loaderParameters = "BLOCK  SIZE 50000 \n" + "NOTIFY $NOTIFY_ROWS \n" + "ESCAPES OFF \n"
      + "QUOTES OFF   \n" + "DELIMITED BY '\\x09' \n" + "ROW DELIMITED BY '\\x0a' \n"
      + "IGNORE CONSTRAINT UNIQUE 2000000 \n" + "IGNORE CONSTRAINT NULL 2000000 \n"
      + "IGNORE CONSTRAINT DATA VALUE 2000000 \n" + "WITH CHECKPOINT $CHECKPOINT \n"
      + "MESSAGE LOG '${LOG_DIR}/iqloader/${TECHPACK}/${MEASTYPE}_RAW/${MEASTYPE}_${DATE}_msg.log' \n"
      + "ROW LOG '${REJECTED_DIR}/${TECHPACK}/${MEASTYPE}_RAW/${MEASTYPE}_${DATE}_row.log' \n"
      + "ONLY LOG UNIQUE, NULL, DATA VALUE\n" + "LOG DELIMITED BY ';' \n";
  
  //These are the load parameters for BINARY loading. NB: "IGNORE CONSTRAINT NULL" set to 0 (zero) means the limit to the number of nulls allowed is infinite.
  private static final String loaderParameters_BINARY = "ESCAPES OFF  QUOTES OFF FORMAT BINARY WITH CHECKPOINT $CHECKPOINT \n"
  		+ "NOTIFY $NOTIFY_ROWS ON FILE ERROR CONTINUE IGNORE CONSTRAINT NULL 0, DATA VALUE 2000000, UNIQUE 2000000 \n" 
  		+ "MESSAGE LOG '${LOG_DIR}/iqloader/${TECHPACK}/${MEASTYPE}_RAW/${MEASTYPE}_${DATE}_msg.log' \n"
        + "ROW LOG '${REJECTED_DIR}/${TECHPACK}/${MEASTYPE}_RAW/${MEASTYPE}_${DATE}_row.log' \n"
        + "ONLY LOG UNIQUE, NULL, DATA VALUE LOG DELIMITED BY ';' \n";

  // protected static final Logger statlog =
  // Logger.getLogger("etlengine.Loader");
  // protected String techPack = null;
  protected Properties whereProps;

  // protected Map sessionLogEntry = new HashMap();
  protected Long techPackId;

  protected Meta_versions version;

  protected Meta_collections set;

  protected String tablename;

  protected String tablelevel;

  protected String tpName;

  protected SetContext sctx;

  protected RockFactory rock;

  protected String failedDir;

  protected int maxLoadClauseLength = Integer.MAX_VALUE;

  protected String notifyTypeName = null;

  protected int totalRowCount = 0;

  private boolean useROWSTATUS = false;
  
  //This DataFormatCache is introduced for to get column names and order in cases where  
  //construction load SQL template is needed.
  private static DataFormatCache dfCache = null; 
  private DFormat dataformat = null;

  // private static Vector engines = new Vector(); // VEngine instance cache
  public Loader(Meta_versions version, Long techPackId, Meta_collections set, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, SetContext sctx, Logger clog) throws Exception {
    super(version, techPackId, set, transferActionId, transferBatchId, connectId, rockFact, connectionPool, trActions);
    
    /*/log.finest("Loader object being generated with: version="+version+" techPackId="+techPackId+" set="+set+" transferActionId="+transferActionId+" connectId="+connectId+" " +
    "rockFact="+rockFact+" connectionPool="+connectionPool+" trActions="+trActions+" sctx="+sctx+" clog="+clog);*/
    
    this.version = version;
    this.set = set;
    this.techPackId = techPackId;
    this.sctx = sctx;
    this.rock = rockFact;
    this.log = clog;
    Meta_collection_sets mcs_cond = new Meta_collection_sets(rockFact);
    mcs_cond.setCollection_set_id(techPackId);
    Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(rockFact, mcs_cond);
    Vector tps = mcs_fact.get();
    Meta_collection_sets tp = (Meta_collection_sets) tps.get(0);
    tpName = tp.getCollection_set_name();
    if (tpName == null) {
      throw new Exception("Unable to resolve TP name");
    }
    try {
      String where = this.getTrActions().getWhere_clause();
      whereProps = stringToProperty(where);
      tablename = whereProps.getProperty("tablename", "");
      tablelevel = whereProps.getProperty("taildir", "plain").toUpperCase();
      failedDir = System.getProperty("ETLDATA_DIR") + File.separator + tablename.toLowerCase().trim() + File.separator
          + whereProps.getProperty("failedDir", "failed") + File.separator;
    } catch (Exception e) {
      failedDir = System.getProperty("ETLDATA_DIR") + "/failed/";
      log
          .log(java.util.logging.Level.WARNING, "Error while getting failed dir, default dir " + failedDir + " used:",
              e);
    }
    try {
      Meta_transfer_actions whereDim = new Meta_transfer_actions(rockFact);
      whereDim.setCollection_id(set.getCollection_id());
      whereDim.setCollection_set_id(set.getCollection_set_id());
      whereDim.setVersion_number(set.getVersion_number());
      whereDim.setAction_type("UpdateDimSession");
      try {
        Meta_transfer_actions updateDIMSession = new Meta_transfer_actions(rockFact, whereDim);
        String DIMSessionWhere = updateDIMSession.getWhere_clause();
        Properties DIMSessionProperties = stringToProperty(DIMSessionWhere);
        this.useROWSTATUS = "true".equalsIgnoreCase(DIMSessionProperties.getProperty("useRAWSTATUS", "false"));
      } catch (Exception e) {
        log.finer("No UpdateDIMsession action was found from this set.");
      }
    } catch (Exception e) {
      log.log(java.util.logging.Level.WARNING, "Error while trying to fetch UpdateDimSessions ROWSTATUS property\n", e);
    }
    try {
      String ss = StaticProperties.getProperty("maxLoadClauseLength", null);
      if (ss != null) {
        maxLoadClauseLength = Integer.parseInt(ss);
      }
    } catch (NumberFormatException nfe) {
      log.config("maxLoadClauseLength was invalid. Ignored.");
    }
    
    if (dfCache == null) {
    	log.finest("Getting Data Format cache");
        this.dfCache = DataFormatCache.getCache();
    }
    this.dataformat = dfCache.getFormatWithFolderName(tablename);
    
    initializeLoggers();
  }
  
  /**
   * This constructor is only intended for test purposes. 
   */
  public Loader(){
	  
  }

  public void execute() throws Exception {
    log.fine("Executing...");
    final ActivationCache ac = ActivationCache.getCache();
    Connection connection = null;
    Statement statement = null;
    final List noTableList = new ArrayList();
    final List tableList = new ArrayList();
    Map tableToFile = null;
    VelocityEngine vengine = null;
    try {
      
      final RockFactory r = this.getConnection();
      connection = r.getConnection();
      statement = connection.createStatement();
      
      String loadTemplate = this.getTrActions().getAction_contents(); //get the load template.
      log.info("Load template from actions:loadTemplate="+loadTemplate);
      if(loadTemplate != null){
      log.info("Size of loadTemplate="+loadTemplate.length());
      }
      
      String asciiLoadTemplate = loadTemplate;
      String binLoadTemplate = null;
      
      log.finest("Ascii Template Initially:"+asciiLoadTemplate);
      if(asciiLoadTemplate != null){
    	  log.finest("Size of Ascii Template Initially:"+asciiLoadTemplate.length());  
      }
      log.finest("Binary Template Initially:"+binLoadTemplate);
      
      /*
       * Check if the wran parsed file name ends with .txt. If so then we have parsed text files which are backlog for loading.
       * During an upgrade, the newer tp will not have the template embedded in tp even though the ascii parsed file from older version would have
       * existed. So for this scenario, create the ascii loader sql which will be used to load the .txt backlog after an upgrade.
       */
      
      String fileName = null;
      List asciiFileList = new ArrayList<String>();	  
      List binFileList =  new ArrayList<String>();
      String tempFileName = null;
      int i = 0;      
    
      boolean tryToLoad = false; // did we try to load any files
      log.finest("Statement created: " + loadTemplate);
      tableToFile = getTableToFileMap();

      if (ac.isActive(tpName, tablename, tablelevel)) {
        log.finer("Found " + tableToFile.size() + " mappings.");
        vengine = VelocityPool.reserveEngine();
        // Create load table commands
        final Iterator tableToFileIterator = tableToFile.entrySet().iterator();
        while (tableToFileIterator.hasNext()) { // load files on each table
          final Map.Entry entry = (Map.Entry) tableToFileIterator.next();
          final String tableName = (String) entry.getKey();
          log.finest("table = " + tableName);
          if (tableName == null) {
            noTableList.addAll((List) entry.getValue());
          } else {
            if (tableName.equalsIgnoreCase("waiting")) {
              // waiting, do not remove or try to load.
              log.info("Files waiting a new partition ");
            } else {
              final List files = (List) entry.getValue();
              log.finest("files size = " + files.size());
              // remove files to be loaded from tableToFile-list
              tableToFileIterator.remove();
              /*
               * Check if the file is .txt
               * Iterate the file list and group all the .txt ones together and the remaining together.
               * Call the loadTable once for .txt files and once for others.
               */
              asciiFileList.clear();
              binFileList.clear();
              for(i = 0; i < files.size(); i++){            	 
            	  tempFileName = (String)files.get(i);
            	  if(tempFileName.endsWith(".binary")){
            		  // Implies a binary fileName.
            		  binFileList.add(tempFileName);
            	  }
            	  else
            	  {
            		  // The file is a txt file. Add the fileName into asciiFileList
            		  asciiFileList.add(tempFileName);            		  
            	  }            	   
              }
              
              log.finest("Size of AsciiList="+asciiFileList.size());
              log.finest("Size of BinList="+binFileList.size());
              
              if(asciiFileList.size() > 0){
            	  /*
            	   * We got a txt file with wran. We need to generate the loader sql
            	   */
            	  
            	  if (asciiLoadTemplate == null || asciiLoadTemplate.length() == 0){
            		  log.finest("Generating AsciiLoadTemplate Dynamically");	  
            		  asciiLoadTemplate = generateLoadTemplate(connection, false);
            	  }   
            	  log.finest("Ascii Load Template:"+asciiLoadTemplate);
            	  loadTable(tableName, asciiFileList, asciiLoadTemplate, vengine, statement, this.loaderParameters);
              }
              
              if(binFileList.size() > 0){
            	  log.finest("We have binary files for loading. Generating the binary template.");
            	  binLoadTemplate = generateLoadTemplate(connection, true);
            	  log.finest("Binary SQL Load Template: "+binLoadTemplate);
            	  loadTable(tableName, binFileList, binLoadTemplate, vengine, statement, this.loaderParameters_BINARY);
              }
              
              log.finest("load table done");
              tryToLoad = true;
              log.finest("tryToLoad is now true");
              tableList.add(tableName);
              log.finest(tableName + " was added to tableList");
            }
          }
        }
        log.finest("useROWSTATUS = " + useROWSTATUS);
        if (useROWSTATUS) {
          log.finest("next createTableList()");
          List tableListCreated = createTableList();
          log.finest("createTableList() done, has items " + tableListCreated.size());
          if (tableListCreated.isEmpty()) {
            log.fine("Created table list is empty. Using the loaded table list.");
            tableListCreated = tableList;
          }
          sctx.put("tableList", tableListCreated);
          log.finest("tableListCreated added to context ");
        } else {
          sctx.put("tableList", tableList);
          log.finest("tableList added to context ");
        }
        if (noTableList != null) {
          log.finest("noTableList != null");
          if (noTableList.isEmpty()) {
            log.finest("noTableList.isEmpty()");
            if (tryToLoad) {
              log.fine("Succesfully loaded.");
            } else {
              log.fine("All loader files are waiting for new partition.");
            }
          } else {
            log.warning("Found " + noTableList.size() + " files without tables. Moving to failed.");
            if (!tryToLoad) {
              throw new Exception("All loader files are without table.");
            }
          }
        }
      } else {
        log.log(Level.WARNING, "Measurement type " + tablename + ":" + tablelevel
            + " is DISABLED, loader not executed, files will be deleted.");
        //Adding a DUMMY "_RAW_01" to the table name
        tablename = tablename + "_RAW_01";
        tableList.add(tablename);
        log.finest(tablename + " is added to tableList.");
        sctx.put("tableList",tableList);
        /*Deleting the file as the Measurement Type is Inactive*/
        log.finest("Deleting the file as the MeasurementType is Inactive");
        final Iterator tableToFileIterator = tableToFile.entrySet().iterator();
        while (tableToFileIterator.hasNext()) { // load files on each table
          final Map.Entry entry = (Map.Entry) tableToFileIterator.next();
              final List files = (List) entry.getValue();
              asciiFileList.clear();
              binFileList.clear();
              for(i = 0; i < files.size(); i++){            	 
            	  tempFileName = (String)files.get(i);
            	  if(tempFileName.endsWith(".binary")){
            		  // Implies a binary fileName.
            		  log.finest("The binary file to be deleted is :" + tempFileName);
            		  binFileList.add(tempFileName);
            		  deleteFiles(binFileList);
            	  }
            	  else
            	  {
            		  log.finest("The ascii file to be deleted is :" + tempFileName);
            		  asciiFileList.add(tempFileName);
            		  deleteFiles(asciiFileList);
            	  }            	   
              }}
        /*......................................................*/
        
      }
    } catch (EngineException ee) {
      throw ee;
    } catch (Throwable e) {
      log.log(java.util.logging.Level.WARNING, "General loader failure:", e);
      log.log(java.util.logging.Level.INFO, "Moving all files to failed:");
      throw new Exception("Error while loading files.", e);
    } finally {
      VelocityPool.releaseEngine(vengine);
      // moves to failed if files where not loaded (removed) from
      //tableToFile-list
      moveFilesToFailed(tableToFile);
      try {
        statement.close();
      } catch (Exception e) {
        log.log(Level.FINEST, "error closing statement", e);
      }
      try {
        connection.commit();
      } catch (Exception e) {
        log.log(Level.FINEST, "error committing", e);
      }
    }
    try {
      if (totalRowCount > 0) {
        final Share share = Share.instance();
        final ETLCEventHandler etlcEventHandler = (ETLCEventHandler) share.get("LoadedTypes");
        etlcEventHandler.triggerListeners(this.notifyTypeName);
        log.finer("Event sent to ETLCEventHandler");
      }
    } catch (Exception e) {
      log.warning("Error occured in triggering listeners.");
    }
    log.info("Set loaded in total " + totalRowCount + " rows.");
  }

/**
 * Method to contruct a SQL load template - either of ASCII or BINARY loading.
 * @param connection
 * @param boolean indicating if binary (true) or ascii (false) load template is to be generated.
 * @return
 * @throws SQLException
 */
public String generateLoadTemplate(Connection connection, Boolean binary)
		throws SQLException {
	String loadTemplate;
	String BWNB = new String("");
	
	if (binary){
		log.fine("Going to generate binary load template.");
		BWNB = ("BINARY WITH NULL BYTE");
	}else{
		log.fine("Going to generate ascii load template.");
	}
	
	final Iterator<DItem> dataItems = dataformat.getDItems();
	
	//Get the name for the first column.
	DItem dataItem = dataItems.next();
	String columnNames = dataItem.getDataName() + " " + BWNB;
	
	//And now the rest of them - putting them into a string that is suitable for a SQL load statement
	while (dataItems.hasNext()){ 
		dataItem = dataItems.next();
		columnNames += ", " + dataItem.getDataName() + " " + BWNB;  
	}
	log.finest("Column names formatted in preparation for SQL load table statement: " + columnNames);
	//Make load table statement.
	loadTemplate = "LOAD TABLE $TABLE  (" + columnNames + ") FROM $FILENAMES $LOADERPARAMETERS;" ; 
	return loadTemplate;
}


  protected void deleteFiles(final Map tableToFile) throws Exception {
    try {
      final Iterator i = tableToFile.values().iterator();
      while (i.hasNext()) {
        final List files = (List) i.next();
        final Iterator fi = files.iterator();
        while (fi.hasNext()) {
          final String s = (String) fi.next();
          final File f = new File(s);
          fileLog.finest(f.toString() + " deleted");
          f.delete();
        }
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to delete files", e);
      throw e;
    }
  }

  protected void deleteFiles(final List fileList) throws Exception {
    try {
      final Iterator fi = fileList.iterator();
      while (fi.hasNext()) {
        final String s = (String) fi.next();
        final File f = new File(s);
        fileLog.finest(f.toString() + " deleted");
        f.delete();
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to delete files", e);
      throw e;
    }
  }

  protected void moveFilesToFailed(final Map filesMap) throws Exception {
    try {
      final Iterator i = filesMap.keySet().iterator();
      while (i.hasNext()) {
        final String key = (String) i.next();
        if (key == null || !key.equalsIgnoreCase("waiting")) {
          final List files = (List) filesMap.get(key);
          final Iterator fi = files.iterator();
          while (fi.hasNext()) {
            final String s = (String) fi.next();
            final File f = new File(s);
            fileLog.finest(f.toString() + " moved to failed");
            moveToDirectory(f, failedDir);
          }
        }
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to move files to failed (" + failedDir + ") ", e);
      throw e;
    }
  }

  protected void moveFilesToFailed(final List fileList) throws Exception {
    try {
      final Iterator fi = fileList.iterator();
      while (fi.hasNext()) {
        final String s = (String) fi.next();
        final File f = new File(s);
        fileLog.finest(f.toString() + " moved to failed");
        moveToDirectory(f, failedDir);
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to move files to failed (" + failedDir + ") ", e);
      throw e;
    }
  }

  /**
   * 
   * 
   * 
   * @param tableName
   * @param files
   * @param loadTemplate
   * @param vengine
   * @param statement
   * @return
   * @throws Exception
   */
  private void loadTable(final String tableName, final List files, final String loadTemplate,
      final VelocityEngine vengine, final Statement statement, final String loadParameters) throws Exception {
	  log.finest("Inside the loadTable method.");
    if ("TABLE_NOT_FOUND".equals(tableName)) {
      fileLog.info(formatFileNamesForLog(files, " not loaded, physical table not found."));
      log.warning("Physical table not found for files" + formatFileNamesForLog(files, ""));
      moveFilesToFailed(files);
    } else {
      final StringWriter pwriter = new StringWriter();
      final StringWriter writer = new StringWriter();
      final VelocityContext loaderParameterContext = new VelocityContext();
      //final String loaderParameterString = StaticProperties.getProperty("Loader.loaderparameters", loaderParameters);
      final String loaderParameterString = loadParameters;
      fillVelocityContext(loaderParameterContext);
      loaderParameterContext.put("LOG_DIR", System.getProperty("LOG_DIR"));
      loaderParameterContext.put("REJECTED_DIR", System.getProperty("REJECTED_DIR"));
      vengine.evaluate(loaderParameterContext, pwriter, "", loaderParameterString);
      final VelocityContext context = new VelocityContext();
      context.put("TABLE", tableName);
      context.put("FILENAMES", formatFileNamesForSQL(files, loadTemplate.length()));
      context.put("LOADERPARAMETERS", pwriter.toString());
      context.put("LOG_DIR", System.getProperty("LOG_DIR"));
      context.put("REJECTED_DIR", System.getProperty("REJECTED_DIR"));
      this.log.finest("SQL velocitytemplate context values: TABLE = " + context.get("TABLE") + ", FILENAMES = "
          + context.get("FILENAMES") + ", LOADERPARAMETERS = " + context.get("LOADERPARAMETERS"));
      fillVelocityContext(context);
      vengine.evaluate(context, writer, "", loadTemplate);
      log.info("Executing load table command for table " + tableName);
      this.log.finest("loadSQL = " + writer.toString());
      sqlLog.finer(writer.toString());
      try {
        final String loadSQL = writer.toString();
        if (loadSQL.length() >= (maxLoadClauseLength)) {
          log.severe("Load clause was too long (" + loadSQL.length() + "). Something will go wrong!");
        }
        final int rowcount = statement.executeUpdate(loadSQL);
        
        totalRowCount += rowcount;
        log.info("Load table returned. " + rowcount + " rows loaded.");
        if (rowcount == 0){
        	log.warning("ZERO ROWS LOADED TO TABLE " + tablename + " from file(s) " + context.get("FILENAMES") + "\nusing SQL:\n" + loadSQL);
        }
      } catch (Exception e) {
        sqlErrLog.log(Level.WARNING, "Load table failed exceptionally, files moved to failed ", e);
        sqlErrLog.info(writer.toString());
        moveFilesToFailed(files);
        throw e;
      }
      log.finer("Load table command executed");
      statement.getConnection().commit();
      log.finer("Explicit commit performed");
      fileLog.info(formatFileNamesForLog(files, " loaded to table " + tableName + "."));
      deleteFiles(files);
    }
  }

  /**
   * Formats a filename list to suitable format for SQL-clause
   * 
   * @param fileNames
   * @return
   */
  protected String formatFileNamesForSQL(final List fileNames, final int loadTemplateLength) {
    final StringBuffer fileNamesStr = new StringBuffer();
    int fName_ix = 0;
    for (; fileNamesStr.length() < (maxLoadClauseLength - loadTemplateLength - 1000) && fName_ix < fileNames.size(); fName_ix++) {
      final String fileName = (String) fileNames.get(fName_ix);
      if (fName_ix > 0) {
        fileNamesStr.append(",");
      }
      fileNamesStr.append("'").append(fileName).append("'");
    }
    if (fName_ix < fileNames.size()) { // No space for all files in SQL
      log.fine(fName_ix + " files out of " + fileNames.size() + " inserted into SQL");
      for (; fName_ix < fileNames.size();) {
        fileNames.remove(fName_ix);
      }
    }
    return fileNamesStr.toString().replaceAll("\\\\", "/"); // convert \ -> /
  }

  /**
   * Formats a filename list to suitable format for logs
   * 
   * @param fileNames
   * @param status
   * @return
   */
  protected String formatFileNamesForLog(final List fileNames, final String status) {
    final Iterator it = fileNames.iterator();
    final StringBuffer fileNamesStr = new StringBuffer("\n");
    while (it.hasNext()) {
      final String fileName = (String) it.next();
      fileNamesStr.append(fileName).append(status).append("\n");
    }
    return fileNamesStr.toString();
  }

  /**
   * Tries to create Properties object from a String.
   */
  protected Properties stringToProperty(final String str) throws Exception {
    Properties prop = null;
    if (str != null && str.length() > 0) {
      prop = new Properties();
      final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }
    return prop;
  }

  /**
   * Moves a file to a directory. files is renamed, if that does not work
   * memorycopy is used. If output directory does not exsist, it will be
   * created.
   * 
   * @return
   * @throws Exception
   */
  private boolean moveToDirectory(final File outputFile, String destDir) throws Exception {
    if (!destDir.endsWith(File.separator)) {
      destDir += File.separator;
    }
    if (!new File(destDir).exists()) {
      log.log(Level.INFO, "Creating directory " + destDir);
      new File(destDir).mkdirs();
    }
    final File targetFile = new File(destDir + outputFile.getName());
    log.finer("Moving file " + outputFile.getName() + " to " + destDir);
    boolean moveSuccess = outputFile.renameTo(targetFile);
    if (!moveSuccess) {
      log.finer("renameTo failed. Moving with memory copy");
      try {
        final InputStream in = new FileInputStream(outputFile);
        final OutputStream out = new FileOutputStream(targetFile);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        in.close();
        out.close();
        outputFile.delete();
        moveSuccess = true;
      } catch (Exception e) {
        log.log(Level.WARNING, "Move with memory copy failed", e);
      }
    }
    return moveSuccess;
  }

  /**
   * Creating a list of tables which include null values in their rowstatus
   * column.
   * 
   * @return List of tables to be loaded.
   * @throws Exception
   */
  private List createTableList() throws Exception {
    final List tableList = new ArrayList();
    log.finest("tableList initialized");
    PhysicalTableCache ptc = PhysicalTableCache.getCache();
    log.finest("ptc found");
    final String storageID = tablename.trim() + ":RAW";
    log.finest("storageID " + storageID);
    List activeTables = new ArrayList();
    log.finest("activeTables initialized");
    activeTables = ptc.getActiveTables(storageID);
    log.finest("activeTables initialized");
    String sqlClause = "";
    final String selectPart = "\n SELECT DISTINCT date_id ";
    final String fromPart = "\n FROM ";
    final String wherePartForNulls = "\n WHERE rowstatus IS NULL AND date_id IS NOT NULL ";
    final String wherePartForEmpties = "\n WHERE rowstatus = '' AND date_id IS NOT NULL ";
    final String unionPart = "\n UNION ";
    log.finest("sql ready");
    final int activeTablesSize = activeTables.size();
    log.finest("activeTablesSize = " + activeTablesSize);
    if (activeTablesSize > 0) {
      for (int i = 0; i < activeTablesSize; i++) {
        final String partitionTable = (String) activeTables.get(i);
        log.finest("partitionTable = " + partitionTable);
        // Example:
        // SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus IS
        // NULL
        // UNION
        // SELECT DISTINCT date_id FROM DC_E_RAN_UCELL_RAW_01 WHERE rowstatus =
        // ''
        // ...
        sqlClause += selectPart + fromPart + partitionTable + wherePartForNulls;
        sqlClause += unionPart;
        sqlClause += selectPart + fromPart + partitionTable + wherePartForEmpties;
        if (i < activeTablesSize - 1) {
          sqlClause += unionPart;
        }
      }
      sqlClause += ";";
      log.finest("sqlClause " + sqlClause);
    } else {
      log.fine("No active tables found for storageID: " + storageID);
    }
    final RockFactory r = this.getConnection();
    log.finest("RockFactory r ok");
    final Connection c = r.getConnection();
    log.finest("RockFactory connection ok");
    final Statement s = c.createStatement();
    log.finest("RockFactory statement ok");
    ResultSet resultSet = null;
    if (sqlClause.length() > 0) {
      resultSet = s.executeQuery(sqlClause);
      log.finest("resultSet ok");
    }
    if (activeTablesSize > 0 && null != resultSet) {
      while (resultSet.next()) {
        for (int i = 0; i < activeTablesSize; i++) {
          log.finest("activeTable #" + i);
          Long startTime = ptc.getStartTime((String) activeTables.get(i));
          log.finest("activeTable startTime " + startTime);
          Long endTime = ptc.getEndTime((String) activeTables.get(i));
          log.finest("activeTable endTime " + endTime);
          Date tableDate = resultSet.getDate(1);
          log.finest("resultSet.getDate(1) " + tableDate);
          if (tableDate.getTime() >= startTime && tableDate.getTime() < endTime) {
            log.finest("activeTables.get(i) " + activeTables.get(i));
            if (!tableList.contains(activeTables.get(i)))
              tableList.add((String) activeTables.get(i));
          }
        }

      }
    }
    try {
      if (null != resultSet) {
        resultSet.close();
      }
    } catch (Exception e) {
      log.finest("resultset close failed " + e.getMessage());
    }
    try {
      if (null != s) {
        s.close();
      }
    } catch (Exception e) {
      log.finest("statement close failed " + e.getMessage());
    }
    return tableList;
  }


  /**
   * Determine prober loggernames for log, sqlLog and fileLog
   */
  protected abstract void initializeLoggers();

  /**
   * Should create Map tableName -> List of File-objects
   * 
   * @return
   * @throws Exception
   */
  protected abstract Map getTableToFileMap() throws Exception;

  /**
   * Add needed values to velocityContext before evaluating
   */
  protected abstract void fillVelocityContext(VelocityContext context);

  /**
   * Makes implementations specific modifications to sessionLogEntry.
   */
  protected abstract void updateSessionLog();
}
