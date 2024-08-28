package com.distocraft.dc5000.etl.engine.system.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.system.SetContextTriggerAction;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;


public class SetContextTriggerActionWrapper extends SetContextTriggerAction {
	
	int triggerCounter = 0;

	public SetContextTriggerActionWrapper(){
		//super();
		triggerCounter=0;
		
	}
	
	public void triggerSchedule(String triggerName) throws Exception{
		triggerCounter++;
	}

	public void trigger(Object str1,String prefix){
		super.trigger( str1, prefix);
	}
	
}

