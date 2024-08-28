package com.distocraft.dc5000.etl.engine.sql;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.ericsson.eniq.etl.ebsHandler.action.EBSUpdater;

/**
 * This class encapsulates a series of actions that perform an update on a 
 * techpack's sets.
 * 
 * @author epiituo
 */
public class EBSUpdateAction extends TransferActionBase {
	
	protected Logger log = Logger.getLogger("EBSUpdateAction");
	protected Meta_collections collection;
	
	protected RockFactory etlRepRockFactory;
	protected RockFactory dwhRockFactory;
	protected RockFactory dwhRepRockFactory;
	protected Meta_collection_sets techPack;
	private EBSUpdater ebsUpdater;
	
	/**
	 * Empty protected constructor
	 */
	protected EBSUpdateAction() {}

	/**
	 * 
	 * @param version
	 * @param techPackId
	 * @param collection
	 * @param transferActionId
	 * @param transferBatchId
	 * @param connectId
	 * @param rockFact
	 * @param trActions
	 * @throws Exception
	 */
	public EBSUpdateAction(Meta_versions version, Long techPackId, Meta_collections collection, Long transferActionId,
			Long transferBatchId, Long connectId, RockFactory rockFact, Meta_transfer_actions trActions, Logger clog) 
			throws Exception {

		super(version, techPackId, collection, transferActionId, transferBatchId, connectId, rockFact, trActions);
		
	    this.log = Logger.getLogger(clog.getName() + ".EBSUpdateAction");
		
		this.etlRepRockFactory = rockFact;
		
		this.dwhRockFactory = getDwhRockFactory(this.etlRepRockFactory);
		if (this.dwhRockFactory == null) {
			throw new Exception("Database dwh is not defined in Meta_databases");
		}
		
		this.dwhRepRockFactory = getDwhRockFactory(this.etlRepRockFactory);
		if (this.dwhRockFactory == null) {
			throw new Exception("Database dwhrep is not defined in Meta_databases");
		}
		
		this.techPack = getTechPack(this.etlRepRockFactory, techPackId);
		if (this.dwhRockFactory == null) {
			throw new Exception("Unable to resolve techpack name");
		}
		
	    Properties properties = new Properties();

	    String act_cont = trActions.getWhere_clause();

	    if (act_cont != null && act_cont.length() > 0) {

	      try {
	        ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
	        properties.load(bais);
	        bais.close();
	      } catch (Exception e) {
	    	 
	      }
	    }
		
	    Meta_collection_sets mcs = new Meta_collection_sets(etlRepRockFactory);
	    mcs.setCollection_set_id(techPackId);
	    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(etlRepRockFactory, mcs);
	    	    
	  properties.put("tpName", mcsF.getElementAt(0).getCollection_set_name());
	    
		ebsUpdater = new EBSUpdater(properties, rockFact, log);
				
		
	}

	/** 
	 * Executes an SQL procedure.
	 * @throws Exception
	 * 
	 * (non-Javadoc)
	 * @see com.distocraft.dc5000.etl.engine.structure.TransferActionBase#execute()
	 */
	public void execute() throws Exception {
		
		ebsUpdater.execute(this.setListener);
		
	}
	

	/*
	 * 
	 */
	protected RockFactory getDwhRockFactory(RockFactory etlRepRock) {
		RockFactory result = getRockFactory(etlRepRock, "dwhrep");
		return result;
	}
	
	/*
	 * 
	 */
	protected RockFactory getDwhRepRockFactory(RockFactory etlRepRock) {
		RockFactory result = getRockFactory(etlRepRock, "dwh");
		return result;
	}

	/*
	 * 
	 */
	protected RockFactory getRockFactory(RockFactory etlRepRock, String name) {

		RockFactory result = null;
		
		try {
			Meta_databases md_cond = new Meta_databases(etlRepRock);
			md_cond.setType_name("USER");
			Meta_databasesFactory md_fact = new Meta_databasesFactory(etlRepRock, md_cond);
			
			Vector dbs = md_fact.get();
			
			Iterator it = dbs.iterator();
			while (it.hasNext()) {
				Meta_databases db = (Meta_databases) it.next();
				
				if (db.getConnection_name().equalsIgnoreCase(name)) {
					result = new RockFactory(db.getConnection_string(), 
											 db.getUsername(), 
											 db.getPassword(), 
											 db.getDriver_name(), 
											 "DWHMgr", true);
				}
			}
		}
		catch (Exception e) {
			result = null;
		}
		
		return result;
	}
	
	/*
	 * 
	 */
	protected Meta_collection_sets getTechPack(RockFactory etlRepRock, Long techPackId) {
		Meta_collection_sets result = null;
		
		Meta_collection_sets mcs_cond = new Meta_collection_sets(etlRepRock);
		mcs_cond.setCollection_set_id(techPackId);
		try {
			Meta_collection_setsFactory mcs_fact = new Meta_collection_setsFactory(etlRepRock, mcs_cond);
			Vector tps = mcs_fact.get();
			result = (Meta_collection_sets) tps.get(0);
		}
		catch (Exception e) {
			result = null;
		}

		return result;
	}
	
}