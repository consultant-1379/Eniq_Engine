package com.distocraft.dc5000.etl.engine.common;


import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;

import java.sql.*;
import java.util.ArrayList;

public class DataMigration {

	/**
	 * @param args
	 */
	private static RockFactory repdb;
	private static RockFactory dwhdb;
	private  ArrayList<Dwhpartition> str = new ArrayList<Dwhpartition>();
	
	
	
	public static void main(String[] args) {
		DataMigration m = new DataMigration();
		int argList=args.length;
		if(argList==0){
			System.out.println("Please provide the Techpack name");
			System.exit(0);
		}
		if(argList!=1){
			System.out.println("Please provide a valid command line parameter");
			System.exit(0);
		}
		
		
		
		
		int roweffected=0;
		int rowupdated=0;
		
		try{
		repdb = new RockFactory("jdbc:sybase:Tds:localhost:2641","Sybase","dwhrep","dwhrep","com.sybase.jdbc3.jdbc.SybDriver","ETLEngine",true,-1);
		dwhdb = new RockFactory("jdbc:sybase:Tds:localhost:2640","Sybase","dc","dc","com.sybase.jdbc3.jdbc.SybDriver","ETLEngine",true,-1);
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Successfully connected to the database");
		
		String query0 = "Select count(*) as ROWCOUNT from DWHTechPacks where TECHPACK_NAME = '"+args[0]+"'";
		String query1 = "select * from DWHPartition where storageid like '"+args[0]+"%COUNT'";
		
		try{
			Connection repcon = repdb.getConnection();
			Connection dwhcon = dwhdb.getConnection();
			Statement stmtrep = repcon.createStatement();
			Statement stmtdwh = dwhcon.createStatement();
			
			ResultSet rs =stmtrep.getResultSet();
			rs = stmtrep.executeQuery(query0);
			rs.next();
			int tpexit = rs.getInt("ROWCOUNT");
			if(tpexit!=0){
				rs=stmtrep.executeQuery(query1);
				m.str = new ArrayList<Dwhpartition>();
				while(rs.next()){
					Dwhpartition data = new Dwhpartition(repdb);
					data.setTablename(rs.getString("TABLENAME"));
					data.setStatus(rs.getString("STATUS"));
					data.setStorageid(rs.getString("STORAGEID"));
					data.setEndtime(rs.getTimestamp("ENDTIME"));
					data.setStarttime(rs.getTimestamp("STARTTIME"));
					m.str.add(data);
				}
			
			
				for(Dwhpartition dwhdata : m.str){
			
				
					String tablename = dwhdata.getTablename();
					Timestamp starttime = dwhdata.getStarttime();
					Timestamp endtime = dwhdata.getEndtime();
					int len = tablename.length();
					len=len-9;
					String subtable = tablename.substring(0, len);
					String rawview = subtable+"_RAW";
					String count_date = subtable+"_COUNT_DISTINCT_DATES";
					ResultSet set = stmtdwh.executeQuery("Select min(DATE_ID)as DATE_ID from "+count_date);
					set.next();
					String minDate = set.getString("DATE_ID");
					if(minDate==null){
						String sqlQuery = "insert into "+ tablename+ " select * from "+ rawview +" where ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MIN_ID !=0 and DATETIME_ID < '"+endtime+"'"+" and DATETIME_ID >= '"+starttime+"'";
						roweffected = stmtdwh.executeUpdate(sqlQuery);
						System.out.println("Statement: "+sqlQuery);
						System.out.println("Executed Successfully: Rows Effected "+roweffected);
					}
				
					else{
						String sqlQuery1 = "insert into "+ tablename+ " select * from "+ rawview +" where ROWSTATUS NOT IN ('DUPLICATE','SUSPECTED') and MIN_ID !=0 and DATE_ID < '"+minDate+"' and DATETIME_ID < '"+endtime+"'"+" and DATETIME_ID >= '"+starttime+"'";
						roweffected = stmtdwh.executeUpdate(sqlQuery1);
						System.out.println("Statement: "+sqlQuery1);
						System.out.println("Executed Successfully: Rows Effected "+roweffected);
					}
				
				
				
				
					rowupdated = stmtdwh.executeUpdate("update "+tablename+" set ROWSTATUS = 'AGGREGATED' where ROWSTATUS = 'LOADED'");
					System.out.println("Rowstatus updated successfully");
					System.out.println("Rows Updated :"+rowupdated);

				
				}
				System.out.println("Data Migrated successfully");
				stmtrep.close();
				stmtdwh.close();
			}else
				System.out.println("Either the techpack is not active or Techpack Name is incorrect");
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
