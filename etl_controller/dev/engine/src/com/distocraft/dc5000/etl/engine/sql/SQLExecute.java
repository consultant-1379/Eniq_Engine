package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;

public class SQLExecute extends SQLOperation {

	private String batchColumnName;

	private Logger log;

	private SetContext sctx;

	protected SQLExecute() {
	}

	/**
	 * Constructor
	 * 
	 * @param versionNumber
	 *            metadata version
	 * @param collectionSetId
	 *            primary key for collection set
	 * @param collectionId
	 *            primary key for collection
	 * @param transferActionId
	 *            primary key for transfer action
	 * @param transferBatchId
	 *            primary key for transfer batch
	 * @param connectId
	 *            primary key for database connections
	 * @param rockFact
	 *            metadata repository connection object
	 * @param connectionPool
	 *            a pool for database connections in this collection
	 * @param trActions
	 *            object that holds transfer action information (db contents)
	 * 
	 * @author Jukka Jääheimo
	 * @since JDK1.1
	 */
	public SQLExecute(Meta_versions version, Long collectionSetId,
			Meta_collections collection, Long transferActionId,
			Long transferBatchId, Long connectId, RockFactory rockFact,
			ConnectionPool connectionPool, Meta_transfer_actions trActions,
			String batchColumnName, SetContext sctx, Logger clog)
			throws Exception {
		super(version, collectionSetId, collection, transferActionId,
				transferBatchId, connectId, rockFact, connectionPool, trActions);

		this.batchColumnName = batchColumnName;
		this.log = Logger.getLogger(clog.getName() + ".SQLExecute");
		this.sctx = sctx;

	}

	public void execute() throws Exception {
		try {
			Properties properties = new Properties();
			String where = this.getTrActions().getWhere_clause();
			if (where != null && where.length() > 0) {
				try {
					ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
					properties.load(bais);
					bais.close();

				} catch (Exception e) {
					System.out.println("Error loading action contents");
					e.printStackTrace();
				}
			}

			boolean useSystemProperties = "TRUE".equalsIgnoreCase(properties.getProperty("useSystemProperties", "false"));

			String sqlClause = this.getTrActions().getAction_contents();

			if (useSystemProperties) {
				sqlClause = parseSystemProperties(sqlClause);
			}

			log.finer("Trying to execute: " + sqlClause);

			Connection conn = null;
			Statement s = null;
			int newRowsAffected = 0;
			Integer rowsAffected = null;

			try {
				conn = this.getConnection().getConnection();
				s = conn.createStatement();
				
				rowsAffected = (Integer) sctx.get("RowsAffected");
				if (rowsAffected == null) {
					rowsAffected = new Integer(0);
				}

				log.finer("Executing statement");
				
				s.execute(sqlClause);
								
				log.finer("Counting affected rows for each statement");
				int rows = s.getUpdateCount();
				while (s.getMoreResults() || rows != -1){
					if (rows != -1){
						newRowsAffected += rows;					
						log.finer("New rows affected:" + rows);
						log.finer("All together now:" + newRowsAffected);
					}
					
					rows = s.getUpdateCount();
				}
				
				s.close();

			} catch (Exception e) {

				try {
					if (s != null)
						s.close();

				} catch (Exception ee) {

				}

				throw new Exception(e);
			}

			if (newRowsAffected > 0) {
				rowsAffected = new Integer(rowsAffected.intValue() + newRowsAffected);
				log.finer("Executed: \n" + sqlClause + "\nRows Affected "+ newRowsAffected + "\nRows Affected for the set:" + rowsAffected);
				if (rowsAffected != null) {
					sctx.put("RowsAffected", rowsAffected);
				}
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "SQL execution failed to exception", e);
			throw new EngineException(EngineConstants.CANNOT_EXECUTE, new String[] { this.getTrActions().getAction_contents() },e, this, this.getClass().getName(),EngineConstants.ERR_TYPE_EXECUTION);
		}
	}

	/**
	 * Replaces Systemproperties with corresponding values
	 * 
	 * @param sqlClause
	 * @return
	 */
	private String parseSystemProperties(String sqlClause) {
		if (!sqlClause.contains("${"))
			return sqlClause;

		final HashMap<String, String> propMap = new HashMap<String, String>();
		final String[] clauseParts = sqlClause.split("\\$\\{");

		for (String part : clauseParts) {
			if (!part.contains("}")){
				continue;
			}
			String key = part.substring(0, part.indexOf('}'));
			String value = System.getProperty(key, null);
			if (value != null){
				propMap.put(key, value);
			}
		}

		for (String key : propMap.keySet()) {
			String value = propMap.get(key);
			sqlClause = sqlClause.replaceAll("\\$\\{" + key + "\\}", value);
		}

		return sqlClause;
	}	
}
