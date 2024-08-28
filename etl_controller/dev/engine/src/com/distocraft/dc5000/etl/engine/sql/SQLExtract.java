package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.ericsson.eniq.common.VelocityPool;


/**
 * 
 * SQLExtract executes a sql clause in DB and uses a velocity template to format the result rows in an output file.<br>
 * 
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
 * <tr>
 * <td>SQL Clause</td>
 * <td>clause</td>
 * <td>Defines the SQL clause to be executed in DB.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Template Header</td>
 * <td>templateHeader</td>
 * <td>Defines the header row of the outputfile. This is writen once in an outputfile.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Template Body</td>
 * <td>template</td>
 * <td>Defines the body the outputfile. Body is an velocity template that resivies the result of the SQL Clause in context named $result. $result contains number of maps containing the result columns. There is one map to one result row.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Template Footer</td>
 * <td>templateFooter</td>
 * <td>Defines the footer row of the outputfile. This is writen once in an outputfile.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Row Buffer Size</td>
 * <td>rowBufferSize</td>
 * <td>Defines the number of rows writen in the outputfile in one go. If total number of the rows exceeds the Row Buffer Size output file is written in two or more iterations. Header and footer is still writen only once in the outputfile.</td>
 * <td>Max Integer</td>
 * </tr>
 * <tr>
 * <td>Output Dir</td>
 * <td>outputDir</td>
 * <td>Defines the output directory.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Filename template</td>
 * <td>filenameTemplate</td>
 * <td>Name of the outputfile. Possible '$'- sign is replaced with the datetime string defined in Timestamp format.</td>
 * <td>&nbsp;</td>
 * </tr>
 * <tr>
 * <td>Timestamp format</td>
 * <td>timestampFormat</td>
 * <td>Defines the format (SimpleDateFormat) of the datetime string added to the output filename.</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table> <br>
 * <br>
 * <br>
 * Ex.<br>
 * <br>
 * Reads AGGREGATION and RULEID columns from LOG_AggregationRules table and writes it to the outputfile.<br>
 * <br>
 * <b>SQL Clause:</b><br>
 * <br>
 * select * from LOG_AggregationRules<br>
 * <br>
 * <b>Template Header:</b><br>
 * <br>
 * HEADER<br>
 * <br>
 * <b>Template Body:</b><br> 
 * <br>
 * #foreach ($i in $result)<br>
 * $i.get("AGGREGATION") $i.get("RULEID")<br>
 * #end<br>
 * <br>
 * <b>Template Footer:</b><br>
 * <br>
 * FOOTER<br>
 * <br>
 * <br>
 * @author savinen
 *
 */
public class SQLExtract extends SQLOperation {

  protected Logger log = Logger.getLogger("etlengine.SQLExtract");

  private Meta_transfer_actions actions;

  private static Vector engines = new Vector(); // VEngine instance cache

  private int rowBufferSize = Integer.MAX_VALUE;

  /**
   * Empty protected constructor
   * 
   */
  protected SQLExtract() {
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
  public SQLExtract(Meta_versions version, Long collectionSetId, Meta_collections collection, Long transferActionId,
      Long transferBatchId, Long connectId, RockFactory rockFact, ConnectionPool connectionPool,
      Meta_transfer_actions trActions, String batchColumnName) throws Exception {

    super(version, collectionSetId, collection, transferActionId, transferBatchId, connectId, rockFact, connectionPool,
        trActions);

    this.actions = trActions;

  }

  
  private PrintWriter createPrintWriter(VelocityEngine vengine, String filename, String header) throws Exception {

    FileOutputStream fileOutStream = new FileOutputStream(filename);
    PrintWriter fileWriter = new PrintWriter(fileOutStream);

    StringWriter writer = new StringWriter();
    VelocityContext headerContext = new VelocityContext();
    vengine.evaluate(headerContext, writer, "", header);
    if (writer != null)
      fileWriter.print(writer);

    return fileWriter;
  }

  
  /**
   * Executes a SQL procedure
   * 
   */
  public void execute() throws EngineException {

    Properties properties = new Properties();

    String act_cont = this.actions.getAction_contents();
    if (act_cont != null && act_cont.length() > 0) {

      try {

        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
        properties.load(bais);
        bais.close();

      } catch (Exception e) {
        System.out.println("Error loading action contents");
        e.printStackTrace();
      }
    }

    StringWriter writer = null;
    FileOutputStream fileOutStream = null;
    HashMap dataRowMap = new HashMap();
    HashMap fileWriterMap = new HashMap();
    Connection connection = null;
    Statement stmt = null;

    VelocityEngine vengine = null;
    
    try {
      
      vengine = VelocityPool.reserveEngine();
      String sqlClause = properties.getProperty("clause", "");
      String body = properties.getProperty("template", "");
      String header = properties.getProperty("templateHeader", "");
      String footer = properties.getProperty("templateFooter", "");
      String outputDir = properties.getProperty("outputDir", "");
      String filename = properties.getProperty("filenameTemplate", "");
      String dateColumnName = properties.getProperty("dateColName", "");
      String dateFormatString = properties.getProperty("timestampFormat", "");
      String rowBufferSizeString = properties.getProperty("rowBufferSize", Integer.MAX_VALUE - 1 + "");
      if (rowBufferSizeString.length() == 0)
        rowBufferSizeString = Integer.MAX_VALUE - 1 + "";
      rowBufferSize = Integer.parseInt(rowBufferSizeString);

      SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);

      String dateString = dateFormat.format(new Date());


      RockFactory r = this.getConnection();
      connection = r.getConnection();
      stmt = connection.createStatement();

      stmt = this.getConnection().getConnection().createStatement();
      ResultSet rest = stmt.executeQuery(sqlClause);

      int count = rowBufferSize + 1;
      int dateColumnIndex = -1;
      
      if (rest != null) {

        // loop as long as count is larger than the bufer size -> still more to write.
        while (count > rowBufferSize) {

          count = 0;

          // adds data rows to a map (key is date of the data) untill threshold is reached.. 
          while (rest.next()) {

            if (!dateColumnName.equals("") && dateColumnIndex == -1){
              for (int i = 1 ; i <= rest.getMetaData().getColumnCount() ; i++){
                if (rest.getMetaData().getColumnName(i).equals(dateColumnName)){
                  dateColumnIndex = i;
                  break;
                }
              }
              if (dateColumnIndex == -1)
                throw new Exception("Date column: "+dateColumnName+" not found.");
            }
            
            if (dateColumnIndex != -1)
              dateString = dateFormat.format(rest.getDate(dateColumnIndex));

            if (!dataRowMap.containsKey(dateString))
              dataRowMap.put(dateString, new ArrayList());

            ArrayList result = (ArrayList) dataRowMap.get(dateString);

            HashMap map = new HashMap();

            // write one result row.
            for (int i = 1; i <= rest.getMetaData().getColumnCount(); i++) {
              map.put(rest.getMetaData().getColumnName(i), rest.getObject(i));
            }

            result.add(map);
            count++;

            // if count exceeds buffer size -> jump out.
            if (count > rowBufferSize)
              break;

          }

          // loop all different dates from the map and create new writers and write rowdata to them.
          Iterator iter = dataRowMap.keySet().iterator();
          while (iter.hasNext()) {

            String key = (String) iter.next();

            // create new pritWriter with filename modified ($ replaced ) with the datestring (key).
            if (!fileWriterMap.containsKey(key))
              fileWriterMap.put(key, createPrintWriter(vengine, outputDir + filename.replaceAll("\\$", key), header));

            PrintWriter fileWriter = (PrintWriter) fileWriterMap.get(key);

            ArrayList result = (ArrayList) dataRowMap.get(key);

            writer = new StringWriter();

            VelocityContext bodyContext = new VelocityContext();
            bodyContext.put("result", result);
            vengine.evaluate(bodyContext, writer, "", body);
            result.clear();
            if (writer != null){
              fileWriter.print(writer);
              writer.close();
            }
                        
          }
          
        }

      }

      Iterator iter = fileWriterMap.keySet().iterator();

      while (iter.hasNext()) {
        String key = (String) iter.next();
        PrintWriter fileWriter = (PrintWriter) fileWriterMap.get(key);

        writer = new StringWriter();

        VelocityContext footerContext = new VelocityContext();
        vengine.evaluate(footerContext, writer, "", footer);

        if (writer != null){
          fileWriter.print(writer);
          writer.close();
        }
      }

    } catch (Exception e) {
      throw new EngineException(EngineConstants.CANNOT_EXECUTE,
          new String[] { this.getTrActions().getAction_contents() }, e, this, this.getClass().getName(),
          EngineConstants.ERR_TYPE_EXECUTION);
    } finally {

    	VelocityPool.releaseEngine(vengine);
    	
      try {

        Iterator iter = fileWriterMap.keySet().iterator();

        while (iter.hasNext()) {
          String key = (String) iter.next();
          PrintWriter fileWriter = (PrintWriter) fileWriterMap.get(key);

          if (fileWriter != null)
            fileWriter.close();

        }
       
         if (fileOutStream != null)
          fileOutStream.close();
        if (writer != null)
          writer.close();
        if (stmt != null)
          stmt.close();

      } catch (Exception e) {
        log.log(Level.FINEST, "error closing statement", e);
      }

    }
  }

}




