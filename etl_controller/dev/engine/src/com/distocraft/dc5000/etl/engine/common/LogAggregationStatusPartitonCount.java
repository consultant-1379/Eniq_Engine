package com.distocraft.dc5000.etl.engine.common;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

/**
 * 
 * @author eninkar
 *
 * Used to get the Partition size of Log_aggregationStatus table from DWHType 
 */
public class LogAggregationStatusPartitonCount {
	
	  private String DbUrl = null;
	  private String DBUserName = null;
	  private String DBPassword = null;
	  private String DBDriverName = null;
	  private RockFactory etlRockFactory = null;
	  
	 /**
	  * Setting Database connection related setting 
	  */
	 private void getServerProperties(){

		    String etlcServerPropertiesFile;

		    try{

		      etlcServerPropertiesFile= System.getProperty("CONF_DIR");

		      if (etlcServerPropertiesFile == null) {
		        //System.out.println("System property CONF_DIR not defined. Using default");
		        etlcServerPropertiesFile = "/eniq/sw/conf";
		      }
		      if (!etlcServerPropertiesFile.endsWith(File.separator)) {
		        etlcServerPropertiesFile += File.separator;
		      }

		      etlcServerPropertiesFile += "ETLCServer.properties";

		      final FileInputStream streamProperties = new FileInputStream(etlcServerPropertiesFile);
		      final java.util.Properties appProps = new java.util.Properties();
		      appProps.load(streamProperties);

		      this.DbUrl = appProps.getProperty("ENGINE_DB_URL");
		      this.DBUserName = appProps.getProperty("ENGINE_DB_USERNAME");
		      this.DBPassword = appProps.getProperty("ENGINE_DB_PASSWORD");
		      this.DBDriverName = appProps.getProperty("ENGINE_DB_DRIVERNAME");

		    }
		    catch(Exception e){

		    }
		  }
	 /**
	  * To print the partition Size of Log_AggregationStatus type
	  */
	 private void getCount(){
		 
		 int count = 0; 
		 
		 getServerProperties();
		 
		 RockFactory dwhreprock = null;
		 
		   try{

			      this.etlRockFactory = new RockFactory(
			          DbUrl,
			          DBUserName,
			          DBPassword,
			          DBDriverName,
			          "Test", false);

			      Meta_databases md_cond = new Meta_databases(this.etlRockFactory);
			      Meta_databasesFactory md_fact = new Meta_databasesFactory(this.etlRockFactory, md_cond);

			      Vector dbs = md_fact.get();

			      Iterator it = dbs.iterator();
			      while (it.hasNext()) {
			        Meta_databases db = (Meta_databases) it.next();

			        if (db.getConnection_name().equalsIgnoreCase("dwhrep") && db.getType_name().equals("USER")) {
			          dwhreprock = new RockFactory(db.getConnection_string(), db.getUsername(), db.getPassword(), db
			              .getDriver_name(), "LogAggr", true);

			        }	

			      } // for each Meta_databases

			      if (dwhreprock == null)
			        throw new Exception("Database dwhrep is not defined in Meta_databases?!");
			    Connection con = dwhreprock.getConnection();
			    Statement stmt = con.createStatement();
			    
			    String sql = "select partitioncount from dwhrep.DWHType where typename='Log_AggregationStatus'";
			    ResultSet rs = stmt.executeQuery(sql);
			    while(rs.next()){
			    	count = rs.getInt(1);
			    }
			    
			    if(count == 0){
			    	
			    	throw new Exception("No Partition Defined");
			    }
			    
			    System.out.println(count);
			    
		    }

		    catch(Exception e){
		    	
		    	e.printStackTrace();

		    }
	 }
	 
	 public static void main(String[] arg){
		 
		 LogAggregationStatusPartitonCount dwhPartitionCountTest = new LogAggregationStatusPartitonCount();
		 
		 dwhPartitionCountTest.getCount();
	 }

}
