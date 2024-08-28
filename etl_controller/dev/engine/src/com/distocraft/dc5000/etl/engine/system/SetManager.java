/**
 * This class handles running the sets with listeners.
 */
package com.distocraft.dc5000.etl.engine.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.distocraft.dc5000.etl.engine.main.EngineThread;
import com.distocraft.dc5000.etl.engine.main.TransferEngine;


/**
 * @author epetrmi
 *
 */
public class SetManager {

  
  private Logger log = Logger.getLogger(SetExecuterRunnable.class);
  private static SetManager instance;
  private static String ID_DELIMETER = "###";
  private Map<String, SetListener> setListeners = new HashMap<String,SetListener>();
  
  /**
   * Returns the instance of SetListenerManager (singleton).
   * @return
   */
  public static SetManager getInstance() {
    if (instance == null) {
      instance = new SetManager();
    }
    return instance;
  }
  
  
  public SetStatusTO executeSet(String colSetName, String setName, Properties props, EngineThread et, TransferEngine transferEngine){
   SetStatusTO ret = null;
    //Check if thread is already running
    String setIdentifier = colSetName+ID_DELIMETER+setName;
    
   //We want to allow only one set execution per time
    //   String setIdentifier = ID_DELIMETER;
    
    //Prevent concurrent running
    SetListener alreadyRunningSetListener = getRunningSetIfExists();

    SetListener existingListener = setListeners.get(setIdentifier);
    final String SET_IS_RUNNING = "";
    if(existingListener==null || 
        SET_IS_RUNNING.equals(existingListener.getStatus()) ||
        alreadyRunningSetListener==null ){
      //Set is not active so we can run a set in a thread
      
      SetListener setListener = new SetListener();
      setListeners.put(setIdentifier, setListener);
      et.setListener = setListener;//##TODO## Consider adding getter/setter in EngThread for this
      
      //We have to run this stuff in a separate thread
      Thread t = null;
      SetExecuterRunnable runnable = new SetExecuterRunnable(props, setListener, et, transferEngine);
      t = new Thread(runnable);
      t.start();
      
      //Get listenermanager SetStatusTO and return it!
      SetStatusTO statusTO = setListeners.get(setIdentifier).getStatusAsTO();
      ret = statusTO;
    }else{
        //Thread is still running
        ret = setListeners.get(setIdentifier).getStatusAsTO();//##TODO## Check that status is running
    }
    return ret;
  }
  
  /**
   * Traverse through the map that holds the setListeners
   *
   * @return - SetT immediately if setListener status="" (NOT_FINISHED). 
   *              Otherwise false.
   */
  private SetListener getRunningSetIfExists() {
    for( String key : this.setListeners.keySet() ) {
      SetListener sl = this.setListeners.get(key);
      if(sl!=null){
        if( SetListener.NOTFINISHED.equals( sl.getStatus() )){
          return sl;
        }
      }
    }
    return null;
  }


  public SetStatusTO getSetStatus(String colSetName, String setName, int beginIndex, int count){
    String setIdentifier = colSetName+ID_DELIMETER+setName;
//    String setIdentifier = ID_DELIMETER;
    SetListener sl = setListeners.get(setIdentifier);
    if(sl!=null){
      return sl.getStatusAsTO();
    }else{
      return null;
    }
  }
}
