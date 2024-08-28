package com.distocraft.dc5000.etl.engine.system;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;

/**
 * 
 * Method triggers sets according rules and data retrieved from set context.
 * <br>
 * Usually parsedMeastypes (HashSet) <br>
 * <br>
 * There can be more than one rule sets in actions. <br>
 * Rules set number zero is not added to the rule name (rule.key). <br>
 * Rule sets numbers from 1 are shown in rule name (ex. rule.1.key) <br>
 * <br>
 * ex1: <br>
 * <br>
 * rule.key = parsedMeastypes <br>
 * rule.contain = s1 <br>
 * rule.triggers = t1 <br>
 * <br>
 * rule.1.contain = s2 <br>
 * rule.1.triggers = t2 <br>
 *  - triggers t1 if parsedMeasTypes contain s1 <br>- triggers t2 if
 * parsedMeasTypes contain s2 <br>
 * <br>
 * ex2. <br>
 * <br>
 * rule.key = parsedMeastypes <br>
 * rule.triggers = ALL <br>
 * rule.prefix = Loader_ <br>
 * <br>- triggers all measurements listed in parsedMeastypes. Adds String
 * 'Loader_' <br>
 * to the names of measurementtypes <br>
 * <br>
 * <br>
 * ex3. <br>
 * <br>
 * rule.key = anyObjectInSetContext <br>
 * rule.exists = true <br>
 * rule.triggers = t1 <br>
 * <br>- triggers t1 if anyObjectInSetContext object is in set context <br>
 * <br>
 * <br>
 * <br>
 * -------------------------------------------------------------------- <br>
 * <br>
 * rule[.num].key = objectName: <br>
 * <br>- is the mapKey that retrieves the required object from set context
 * (parsebleFiles,parsedMeasTypes) <br>- parsebleFiles (Integer) contain number
 * of parseable file in INdirectory. <br>- parsedMeasTypes (HashSet) contains
 * list of measurements that adapter has handled <br>
 * rule[.num].exists (Boolean) <br>
 * <br>- object retrieved from content is treated as object <br>- checks if
 * key defined in rule[.num].key exists (true) or does not exists (false) in set
 * context <br>
 * rule[.num].contain = str1,str2,str3 (HashSet) <br>
 * <br>- object retrieved from content is treated as HashSet <br>- list (1 or
 * more) comma delimited strings that are searched from HashTable. <br>
 * if all strings are found from HashTable, scheduled sets (listed in property
 * rule.triggers) are triggered from scheduler <br>
 * rule[.num].more (Integer) <br>
 * <br>- object retrieved from content is treated as Integer <br>- checks if
 * Integer from setContext is larger than given value <br>
 * rule[.num].equal (Integer) <br>
 * <br>- object retrieved from content is treated as Integer <br>- checks if
 * Integer from setContext is equal than given value <br>
 * rule[.num].less (Integer) <br>
 * <br>- object retrieved from content is treated as Integer <br>- checks if
 * Integer from setContext is smaller than given value <br>
 * rule[.num].triggers <br>
 * <br>- List of set names that are triggered if one of the conditions
 * (contains,more,equal,less) is true <br>- if triggers value is 'ALL' all of
 * the elements from HashSet defined in key are listed in triggered list (and
 * triggered if condition matches) <br>
 * rule[.num].prefix <br>
 * <br>- prefix that is added to the trigered sets name <br>
 * <br>
 * <br>
 */
public class SetContextTriggerAction extends TransferActionBase {

	private Meta_collections collection;

	private RockFactory rockFact;

	private String logString;

	private String shareKey;

	private SetContext sctx;

	private String where;

	private String action;

	/**
	 * Empty public constructor
	 *  
	 */
	public SetContextTriggerAction() {
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
	 */
	public SetContextTriggerAction(Meta_versions version, Long collectionSetId,
			Meta_collections collection, Long transferActionId,
			Long transferBatchId, Long connectId, RockFactory rockFact,
			Meta_transfer_actions trActions, SetContext sctx)
			throws EngineMetaDataException {

		super(version, collectionSetId, collection, transferActionId,
				transferBatchId, connectId, rockFact, trActions);

		this.collection = collection;
		this.rockFact = rockFact;
		this.sctx = sctx;
		this.where = trActions.getWhere_clause();
		this.action = trActions.getAction_contents();
		try {
			Meta_collection_sets whereCollSet = new Meta_collection_sets(
					rockFact);
			whereCollSet.setEnabled_flag("Y");
			whereCollSet.setCollection_set_id(collectionSetId);
			Meta_collection_sets collSet = new Meta_collection_sets(rockFact,
					whereCollSet);

			String tech_pack = collSet.getCollection_set_name();
			String set_type = collection.getSettype();
			String set_name = collection.getCollection_name();

			this.logString = "etl." + tech_pack + "." + set_type + "."
					+ set_name + ".action.SetContextTriggerAction";

		} catch (Exception e) {
			throw new EngineMetaDataException(
					"ExecuteSetAction unable to initialize loggers", e, "init");
		}

	}

	
	public boolean isEqual(Object str1,String str2) throws Exception{
		
		if (str1 != null){
					
			try{
							
				// is it Boolean
				
				boolean tmp = ((Boolean)str1).booleanValue();
				
				if (str2.equalsIgnoreCase("true")){
					
					return  tmp == "true".equalsIgnoreCase(str2);
				} else
				if (str2.equalsIgnoreCase("false")){
						
					return  tmp == "true".equalsIgnoreCase(str2);
				} else {
					
					throw new Exception("not boolean");	
				}
				
				
			} catch (Exception e1){
				// no Boolean
				try{
					
					Integer i = (Integer)str1;
					
					// is it Integer
					return i.intValue() == Integer.parseInt(str2);
					
				} catch (Exception e2){
					
					// no Integer
					try{
						
						// is it string
						return ((String)str1).equalsIgnoreCase(str2);

						
					} catch (Exception e3){
						
						return false;						
					} 					
				} 				
			} 
						
		} else {
			return false;
		}
		
	}
	
	public boolean isMore(Integer i,String str2) throws Exception{
		

		if (i != null && str2 !=null && !str2.equalsIgnoreCase("")) {
			return (i.intValue()) < Integer.parseInt(str2);
		} else {
			return false;
		}
		
	}

	public boolean isLess(Integer i,String str2) throws Exception{
		
		if (i != null && str2 !=null && !str2.equalsIgnoreCase("")) {
			return (i.intValue()) > Integer.parseInt(str2);
		} else {
			return false;
		}
		
	}

	public void trigger(Object str1,String prefix){
								
	
		if (str1 != null){
			
			String triggerName = null;	
			List list = (List)str1;
			
			try {
					
			
				if (!list.isEmpty()) {
	
					Iterator iter = list.iterator();
					while (iter.hasNext()) {
						triggerName = (String) iter.next();
						if (prefix != null)
							triggerName = prefix + triggerName;
	
						triggerSchedule(triggerName);
					}
				}
	
			} catch (Exception e) {
	
				// could not start trigger in scheduler
				Logger.getLogger(this.logString + ".execute").log(
						Level.WARNING,
						"Could not start trigger " + triggerName
								+ " in scheduler");
			}		
		}
			
	}
	
	
	public void triggerSchedule(String triggerName) throws Exception{
		SchedulerAdmin admin = new SchedulerAdmin();
		Logger.getLogger(
				this.logString + ".execute").log(
				Level.FINE,
				"Triggering set " + triggerName);
		admin.trigger(triggerName);
	}
	

	public boolean contains(Set set,String content) throws Exception{
	
		
		if (set != null){
			
			List list = null;

			if (content != null) {

				// list content
				list = new ArrayList();
				StringTokenizer contentTokens = new StringTokenizer(
						content, ",");
				while (contentTokens.hasMoreTokens()) {
					list.add(contentTokens.nextToken());
				}
			}
					
			 if (list!=null && !list.isEmpty()) {
				return set.containsAll(list);
			} else
				return false;
					 
		} else 
			return false;		
	}
	
	
	public List strToList(String str){
		
		ArrayList list =  new ArrayList();

		if (str!=null){
						
			// list all triggers
			StringTokenizer triggerTokens = new StringTokenizer(
					((String)str), ",");
			while (triggerTokens.hasMoreTokens()) {
				list.add(triggerTokens.nextToken());
			}				
		}
				
		return list;
	}
	
	
	
	public List setToList(Set set){
	
		ArrayList list =  new ArrayList();

		if (set!=null){
			Iterator iter = set.iterator();
			
			while (iter.hasNext()) {
				list.add(iter.next());
			}						
		}
		
		return list;
	}
	
	/**
	 *  
	 */
	public void execute() throws EngineException {

		Logger.getLogger(this.logString + ".execute").log(Level.FINEST,
				"Starting SetContextTriggerAction");

		Properties properties = new Properties();
		String dateString = "";

		try {

			ByteArrayInputStream bais = new ByteArrayInputStream(where
					.getBytes());
			properties.load(bais);
			bais.close();

			int ruleNum = 0;
			String key = null;

			do {

				String oldKey = key;
				key = null;
				String content = null;
				String triggers = null;
				List triggerList = new ArrayList();
				String less = null;
				String more = null;
				String equals = null;
				String prefix = "";
				String exists = null;

				try {
					String cmd = "";
					if (ruleNum == 0)
						cmd = "rule";
					else
						cmd = "rule." + ruleNum;

					key = (String) properties.get(cmd + ".key");
					content = (String) properties.get(cmd + ".contain");
					more = (String) properties.get(cmd + ".more");
					equals = (String) properties.get(cmd + ".equal");
					less = (String) properties.get(cmd + ".less");
					triggers = (String) properties.get(cmd + ".triggers");
					prefix = (String) properties.get(cmd + ".prefix");
					exists = (String) properties.get(cmd + ".exists");

					ruleNum++;

					if (triggers == null)
						break;

					if (key == null) {
						if (oldKey != null) {
							key = oldKey;
						} else
							break;
					}
				
					boolean trigger = true;
					if (triggers.equalsIgnoreCase("ALL")) {
						
						triggerList = setToList((Set)sctx.get(key));
																
					} else {
						
						triggerList = strToList(triggers);
						
					}
								
					
					if ("true".equalsIgnoreCase(exists)) {
						if (sctx.containsKey(key))
							trigger = true;
						
					} else if ("false".equalsIgnoreCase(exists)) {
						if (!sctx.containsKey(key)) 
							trigger = true;	
						
					} else if (content != null) {

						trigger = contains((Set)sctx.get(key),content);
					
					} else

					if (equals != null) {

						trigger = isEqual(sctx.get(key),equals);						
					} else

					if (less != null) {

						trigger = isLess((Integer)sctx.get(key),less);
					} else

					if (more != null) {

						trigger = isMore((Integer)sctx.get(key),more);
					}				
					
					if (trigger) {

						trigger(triggerList,prefix);
					}

				} catch (Exception e) {

					Logger.getLogger(this.logString + ".execute").log(
							Level.WARNING,
							" Error while reading properties " + where);

				}

			} while (key != null);

		} catch (Exception e) {
			Logger.getLogger(this.logString + ".execute").log(Level.WARNING,
					" Error In SetContextTriggerAction ", e);
			throw new EngineException("Exception in SetContextTriggerAction",
					new String[] { "" }, e, this, this.getClass().getName(),
					EngineConstants.ERR_TYPE_SYSTEM);

		}

	}
}

