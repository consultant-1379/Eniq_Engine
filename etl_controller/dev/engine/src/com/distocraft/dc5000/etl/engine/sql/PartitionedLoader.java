package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * TODO intro <br>
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
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
 * <td>Defines the base tablename of the loaded data. Actual tables (or partitons) are retrieved just before actual load.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>techpack</td>
 * <td>Defines the teckpack of the loaded data.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>checkpoint</td>
 * <td>Parameter in loadTable command. This parameter is read first from static properties (PartitionedLoader.checkpoint) then from normal action parameters and if still no value default is used.</td>
 * <td>OFF</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>notify_rows</td>
 * <td>Parameter in loadTable command. This parameter is read first from static properties (PartitionedLoader.notify_rows) then from normal action parameters and if still no value default is used.</td>
 * <td>100000</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>filemask</td>
 * <td>Defines RegExp mask to filter files from input directory (raw dir).</td>
 * <td>.+</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>dateformat</td>
 * <td>Dafines the dateformat (simpleDateFormat) to parse the date (datadate) found at the end of the loadDatafile.</td>
 * <td>yyyy-MM-dd</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>taildir</td>
 * <td>Defines the input directory in etldata/'meastype'/.</td>
 * <td>raw</td>
 * </tr>
 * <td>Load template</td>
 * <td>Action_contents column</td>
 * <td>Defines the velocity template for the actual sql clause.</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>

 * @author lemminkainen
 */
public class PartitionedLoader extends Loader {

  private static PhysicalTableCache ptc = null;
  private Pattern patt = null; 
  
  public PartitionedLoader(Meta_versions version, Long collectionSetId, final Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName, SetContext sctx, Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions, sctx, clog);

    Map sessionLogEntry = new HashMap();

    long loadersetID = -1;
    try {
      loadersetID = SessionHandler.getSessionID("loader");
    } catch (Exception e) {
      throw new EngineMetaDataException("Error getting loaderSetID", e, "init");
    }

    sessionLogEntry.put("LOADERSET_ID", String.valueOf(loadersetID));
    sessionLogEntry.put("SESSION_ID", "");
    sessionLogEntry.put("BATCH_ID", "");
    sessionLogEntry.put("TIMELEVEL", "");
    sessionLogEntry.put("DATATIME", "");
    sessionLogEntry.put("DATADATE", "");
    sessionLogEntry.put("ROWCOUNT", "");
    sessionLogEntry.put("SESSIONSTARTTIME", String.valueOf(System.currentTimeMillis()));
    sessionLogEntry.put("SESSIONENDTIME", "");
    sessionLogEntry.put("STATUS", "");
    sessionLogEntry.put("TYPENAME", "");

    sctx.put("sessionLogEntry", sessionLogEntry);

    if (ptc == null) {
      ptc = PhysicalTableCache.getCache();
    }
    
    String where = this.getTrActions().getWhere_clause();

    whereProps = stringToProperty(where);

    if (whereProps == null) {
      whereProps = new Properties();
      whereProps.setProperty("where", where);
    }

    if (!whereProps.contains("checkpoint") || whereProps.getProperty("checkpoint").equalsIgnoreCase("")) {
      whereProps.setProperty("checkpoint", StaticProperties.getProperty("PartitionedLoader.checkpoint", "OFF"));
    }

    if (!whereProps.contains("notify_rows") || whereProps.getProperty("notify_rows").equalsIgnoreCase("")) {
      whereProps.setProperty("notify_rows", StaticProperties.getProperty("PartitionedLoader.notify_rows", "100000"));
    }
    
    notifyTypeName = this.tablename + "_" + this.tablelevel;

  }
  
  /**
   * This constructor is only intended for test purposes. 
   */
  public PartitionedLoader(){
	  super();
  }

  protected void initializeLoggers() {

    final String logname = log.getName() + ".PartitionedLoader";

    log = Logger.getLogger(logname);

    final String logname_pfx = logname.substring(logname.indexOf("."));

    fileLog = Logger.getLogger("file." + logname_pfx + ".PartitionedLoader");
    sqlLog = Logger.getLogger("sql." + logname_pfx + ".PartitionedLoader");
    sqlErrLog = Logger.getLogger("sqlerror." + logname_pfx + ".PartitionedLoader");
    
  }

  /**
   * @see com.distocraft.dc5000.etl.engine.sql.Loader#getTableToFileMap()
   */
  protected Map getTableToFileMap() throws Exception {

    final Date currDate = new Date();
    final String measType = whereProps.getProperty("tablename");
    patt = Pattern.compile(whereProps.getProperty("filemask",".+"));
    
    sctx.put("MeasType", measType);

    final String dateFormatString = whereProps.getProperty("dateformat", "yyyy-MM-dd");
    final String tailDir = whereProps.getProperty("taildir", "raw");

    final String storageID = measType.trim() + ":RAW";
    
    //Getting the MeasuementType from the table name
    final String tableNameIs = measType.trim();
    
    log.finest("The MeasurementType is :"+ measType.trim());

    final String inDir = System.getProperty("ETLDATA_DIR") +File.separator + measType.toLowerCase().trim() + File.separator + tailDir;

    log.finest("storageID: " + storageID);
    log.finest("inDir: " + inDir);

    final FilenameFilter filter = new FilenameFilter() {

      public boolean accept(File dir, String name) {
        Matcher m = patt.matcher(name);
        return m.matches();
      }
    };
    
    final File DinDir = new File(inDir);
    
    if (!DinDir.isDirectory() || !DinDir.canRead()) {
      log.warning("In directory " + DinDir + " not found or cannot be read");
    }

    File[] files = DinDir.listFiles(filter);

    if (files == null) {
      files = new File[0];
    }
    
    if (files.length <= 0) {
      log.info("No files found on input directory " + DinDir);
    }

    final HashMap fileMap = new HashMap();

    for (int i = 0; i < files.length; i++) {
      final String dateString = parseDate(files[i].toString());

      log.finest("filename: " + files[i].toString());
      log.finest("dateString: " + dateString);

      List filenames = (List) fileMap.get(dateString);
      if (filenames == null) {
        filenames = new ArrayList();
        fileMap.put(dateString, filenames);
      }

      filenames.add(files[i].toString());
    }

    log.fine("Input files for " + fileMap.size() + " different dates");
    
    // Create database tables map mapping tables to files
    final Map tableMap = new HashMap();

    final Iterator fileMapIterator = fileMap.entrySet().iterator();

    log.fine("DateFormat of file is " + dateFormatString);

    final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);

    log.info("Reading PhysicalTable data with StorageID " + storageID);

    while (fileMapIterator.hasNext()) {

      final Map.Entry entry = (Map.Entry) fileMapIterator.next();

      final String dateString = (String) entry.getKey();
      ArrayList filenames = (ArrayList) entry.getValue();

      try {

        log.finer("Physical table for day " + dateString + " " + filenames.size() + " files to load.");

        final java.util.Date date = dateFormat.parse(dateString); // -> parseException

        String tableName = ptc.getTableName(storageID, date.getTime());
        
        if (tableName == null) {
        
          // if no partitions hit this time, lets take all the partitions and get the endtime of the last one.
          final List tableList = ptc.getTableName(storageID,0,currDate.getTime());
          
          if(!tableList.isEmpty())
          {
          final long lastPartitionEndTime = ptc.getEndTime((String)tableList.get(0));
         
         // if data is between currendate and last partitions endtime it is put on wait status.
         // wating status is needed preserve (not to move to failed) files from indir as invalid (not having partitions) files later. 
          if (date.getTime() <= currDate.getTime() && date.getTime() >= lastPartitionEndTime ){
            
            log.info("Cant find table for " + storageID + " @ " + dateString+", but dateString is between current date ("+dateFormat.format(currDate)+") and last partiton endtime ("+dateFormat.format(new Date(lastPartitionEndTime))+") -> waiting for new partition to appear.");
            tableName = "waiting";
            
          } else {

            log.info("Cant find table for " + storageID + " @ " + dateString);
            //continue;
            
          }
          
        }
          
          else {
        	  tableName="waiting";
          }}
        
        ArrayList tablefilenames = (ArrayList) tableMap.get(tableName);
        if (tablefilenames == null) {
          tablefilenames = new ArrayList();
        }

        tablefilenames.addAll(filenames);

        if (!tablefilenames.isEmpty()) {
          tableMap.put(tableName, tablefilenames);
        }

        log.finer("Using table " + tableName + " for files" + formatFileNames_log(filenames, ""));

      } catch (ParseException pe) {
        log.log(Level.WARNING, "Illegal timestamp format: " + dateString, pe);
        log.finer("Failed files: " + formatFileNames_log(filenames, " moved to failed"));
        // move to failed
        moveFilesTo(filenames,this.failedDir);
      }

    }
    
    if (tableMap.size() <= 0) {
      log.info("No physical tables found for files");
    }

    sctx.put("tableMap", tableMap);

    return tableMap;

  }

  /**
   * Add needed values to velocityContext before evaluating
   */
  protected void fillVelocityContext(final VelocityContext context) {
    final String dateStr = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    context.put("DATE", dateStr);
    context.put("MEASTYPE", whereProps.getProperty("tablename"));
    context.put("TECHPACK", whereProps.getProperty("techpack"));
    context.put("CHECKPOINT", whereProps.getProperty("checkpoint"));
    context.put("NOTIFY_ROWS", whereProps.getProperty("notify_rows"));
   
  }

  public void updateSessionLog() {

  }

  /**
   * Date is found at the end of the filename between last '_' and last '.'
   * 
   * @param fileName
   * @return
   */
  private String parseDate(final String fileName) {
    final int start = fileName.lastIndexOf('_') + 1;
    final int end = fileName.indexOf(".", start);

    try {
     
      final String dateString = fileName.substring(start, end);
      log.finest("parsed Date \"" + fileName + "\" -> \"" + dateString + "\"");
      return dateString;
    
    } catch (Exception e){
      log.warning("Error while trying to parse date from " + fileName);
    }
    
    return "";
    
  }

  /**
   * Formats a filename list to suitable format for logs
   * 
   * @param fileNames
   * @param status
   * @return
   */
  private String formatFileNames_log(final List fileNames, final String status) {
    final Iterator it = fileNames.iterator();

    final StringBuffer fileNamesStr = new StringBuffer("\n");

    while (it.hasNext()) {
      final String fileName = (String) it.next();
      fileNamesStr.append(fileName + status + "\n");
    }
    return fileNamesStr.toString();
  }

  /**
   * Moves all files in list to dir
   * 
   * @param fileNames
   * @param dir
   */
  private void moveFilesTo(final List fileNames, String dir){
	  	  
	    if (!dir.endsWith(File.separator)) {
	    	dir += File.separator;
      }

	      if (!new File(dir).exists()) {
	        log.log(Level.INFO, "Creating directory " + dir);
	        new File(dir).mkdirs();
	      }
	  
        final Iterator it = fileNames.iterator();
	    while (it.hasNext()) {
        final String fileName = (String) it.next();
        final File file = new File(fileName);
	      try {
          final File newFile = new File(dir+file.getName());
	    	  if (!file.renameTo(newFile)){

    	          log.finer("renameTo failed. Moving with memory copy");

	    		  try {

                  final InputStream in = new FileInputStream(file);
                  final OutputStream out = new FileOutputStream(newFile);

                  final byte[] buf = new byte[1024];
	    	          int len;
	    	          while ((len = in.read(buf)) > 0) {
	    	            out.write(buf, 0, len);
	    	          }
	    	          in.close();
	    	          out.close();

	    	          file.delete();


	    	        } catch (Exception e) {
	    	          log.log(Level.WARNING, "Move with memory copy failed", e);
	    	        }
	    	  }
	    	  
	      } catch (Exception e){
	    	  log.warning("Could not move file "+fileName+" to "+dir); 
	      }
	    }
	 
  }
  
}
