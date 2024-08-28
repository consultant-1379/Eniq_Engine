/*
 * Created on 6th of May 2009
 *
 */
package com.distocraft.dc5000.etl.engine.executionslots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;

/**
 * @author unknown
 * 
 */
public class ExecutionMemoryConsumption {

	private List list = null;
	private Logger log;

	/**/
	private static ExecutionMemoryConsumption singletonExecutionMemoryConsumption = null;

	/**
	 * In the first call of this method new share is created and returned. After
	 * the first call the same share is returned.
	 * 
	 * @return share
	 */
	public synchronized static ExecutionMemoryConsumption instance() {

		if (singletonExecutionMemoryConsumption == null) {
			singletonExecutionMemoryConsumption = new ExecutionMemoryConsumption();
		}

		return singletonExecutionMemoryConsumption;

	}

	/**
	 * constructor
	 * 
	 */
	private ExecutionMemoryConsumption() {
		log = Logger.getLogger("etlengine.ExecutionMemoryConsumption");
		list = new ArrayList();
		log.info("Singleton ExecutionMemoryConsumption constructed");
	}

	/**
	 * 
	 * Add a object to the share.
	 * 
	 * @param obj
	 */
	public synchronized void add(Object obj) {
		if (list != null && !list.contains(obj)) {
			list.add(obj);
			log.info("New object added to ExecutionMemoryConsumption");
			int size = calculate();
			int listSize = size();
			log.info("ExecutionMemoryConsumption is now " + size + " MB");
			log.info("ExecutionMemoryConsumption list size is now " + listSize);
		}
	}

	/**
	 * Remove object from the share.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized Object remove(Object obj) {

		if (list != null) {
			Object o = list.remove(obj);
			if (o != null) {
				log.info("Object removed from ExecutionMemoryConsumption");
				int size = calculate();
				int listSize = size();
				log.info("ExecutionMemoryConsumption is now " + size + " MB");
				log.info("ExecutionMemoryConsumption list size is now "
						+ listSize);
			}
			return o;
		}

		return null;
	}

	/**
	 * 
	 * Returns the size of the map.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized int size() {

		if (list != null) {

			return (int) list.size();

		}

		return -1;

	}

	public synchronized int calculate() {
		int returnValue = 0;

		if (list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof MemoryRestrictedParser) {
					returnValue += ((MemoryRestrictedParser) obj).memoryConsumptionMB();
				}
			}
		}

		return returnValue;
	}

}
