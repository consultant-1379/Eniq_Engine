package com.distocraft.dc5000.etl.engine.sql;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

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
 *  <tr>
 * <td>Table name</td>
 * <td>tablename</td>
 * <td>Defines the tablename of the loaded data.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Techpack</td>
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
 * <td>Pattern</td>
 * <td>pattern</td>
 * <td>Defines RegExp mask to filter files from input directory (raw dir).</td>
 * <td>.+\.txt</td>
 * </tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>dateformat</td>
 * <td>Dafines the dateformat (simpleDateFormat) to parse the date (datadate) found at the end of the loadDatafile.</td>
 * <td>yyyy-MM-dd</td>
 * </tr>
 * <tr>
 * <td>Directory</td>
 * <td>dir</td>
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
public class UnPartitionedLoader extends Loader {

  private static final String DEFAULT_DATA_FILE_MASK = ".+\\.txt";


  public UnPartitionedLoader(Meta_versions version, Long collectionSetId, Meta_collections collection,
      Long transferActionId, Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName, SetContext sctx, Logger clog) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions, sctx, clog);

    String where = this.getTrActions().getWhere_clause();

    whereProps = stringToProperty(where);

    if (whereProps == null) {
      whereProps = new Properties();
      whereProps.setProperty("where", where);
    }

    if (!whereProps.contains("checkpoint") || whereProps.getProperty("checkpoint").equalsIgnoreCase("")) {
      whereProps.setProperty("checkpoint", StaticProperties.getProperty("UnPartitionedLoader.checkpoint", "OFF"));
    }

    if (!whereProps.contains("notify_rows") || whereProps.getProperty("notify_rows").equalsIgnoreCase("")) {
      whereProps.setProperty("notify_rows", StaticProperties.getProperty("UnPartitionedLoader.notify_rows", "100000"));
    }

    notifyTypeName = this.tablename;
    
  }

  public void initializeLoggers() {

    final String logname = log.getName() + ".UnPartitionedLoader";

    log = Logger.getLogger(logname);

    final String logname_pfx = logname.substring(logname.indexOf("."));

    fileLog = Logger.getLogger("file." + logname_pfx + ".UnPartitionedLoader");
    sqlLog = Logger.getLogger("sql." + logname_pfx + ".UnPartitionedLoader");
    sqlErrLog = Logger.getLogger("sqlerror." + logname_pfx + ".UnPartitionedLoader");

  }

  /**
   * @see com.distocraft.dc5000.etl.engine.sql.Loader#getTableToFileMap()
   */
  protected Map getTableToFileMap() throws Exception {

    final Map fileMap = new HashMap();

    final String refilter = whereProps.getProperty("pattern", DEFAULT_DATA_FILE_MASK);

    // create a filter that filters out unwanted files
    final FilenameFilter filter = new FilenameFilter() {

      public boolean accept(File dir, String name) {
        return name.matches(refilter);
      }
    };

    // check files that need to be loaded


    String directory = whereProps.getProperty("dir");
    
    if(directory.indexOf("${") >= 0) {
      final int start = directory.indexOf("${");
      final int end = directory.indexOf("}",start);
      
      if(end >= 0) {
        final String variable = directory.substring(start+2,end);
        final String val = System.getProperty(variable);
        final String result = directory.substring(0,start) + val + directory.substring(end+1);
        directory = result;
      }
    }

    final File dir = new File(directory);

    
    if (!dir.exists() || !dir.canRead()) {
      throw new Exception("Cannot read directory \"" + whereProps.getProperty("dir") + "\"");
    }

    final String tableName = whereProps.getProperty("tablename");

    if (tableName == null || tableName.length() <= 0) {
      throw new Exception("Table name must be defined as \"table\"");
    }

    final File[] files = dir.listFiles(filter);

    final List dirFiles = new ArrayList();

    if (files != null) {
      log.info("Found " + files.length + " files.");

      for (int i = 0; i < files.length; i++) {
        dirFiles.add(files[i].toString());
      }
    }

    if (!dirFiles.isEmpty()) {
      fileMap.put(tableName, dirFiles);
    }

    return fileMap;

  }

  protected void fillVelocityContext(final VelocityContext context) {
    final String dateStr = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    context.put("DATE", dateStr);
    context.put("MEASTYPE", whereProps.getProperty("tablename"));
    context.put("CHECKPOINT", whereProps.getProperty("checkpoint"));
    context.put("NOTIFY_ROWS", whereProps.getProperty("notify_rows"));
    context.put("TECHPACK", whereProps.getProperty("techpack"));
    

  }

  protected void updateSessionLog() {
  }

}
