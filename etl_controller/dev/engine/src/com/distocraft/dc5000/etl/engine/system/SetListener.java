package com.distocraft.dc5000.etl.engine.system;

import java.util.List;
import java.util.Vector;

public class SetListener {
	
  public static final String NOTFINISHED = "";
  public static final String DROPPED = "dropped";
  public static final String FAILED = "failed";
  public static final String SUCCEEDED = "succeeded";
  public static final String NOSET = "noset";
  
  public static final NullSetListener NULL = NullSetListener.instance();
  
  private String status = NOTFINISHED;
  
  private List<StatusEvent> statusList = new Vector<StatusEvent>(); 
  
  public String listen(){
    try{
    synchronized(this){      
      this.wait();  
    }
    }catch (Exception e){     
    }    
    return status;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void dropped(){
    synchronized(this){
      
      status = DROPPED;
      this.notifyAll();
    }
  }
  
  public void failed(){
    synchronized(this){
      
      status = FAILED;
      this.notifyAll();
    }
  } 
  
  public void succeeded(){
    synchronized(this){
      
      status = SUCCEEDED;
      this.notifyAll();
    }
  }

  public boolean addStatusEvent(StatusEvent se){
    return statusList.add(se);
  }

  public StatusEvent getStatusEvent(int index){
    return statusList.get(index);
  }
  
  /**
   * Gets a sub list with given parameters
   * 
   * @param beginIndex
   * @param count
   * @return - sublist of status list with given parameters.
   * @throws - IllegalArgumentException if beginIndex or count is below zero
   */
  public List<StatusEvent> getStatusEvents(int beginIndex, int count){
	if (beginIndex >= 0 && count >= 0) { 
	  int endIndex = beginIndex + count - 1;
	  List<StatusEvent> result = createSubList(this.statusList, beginIndex, endIndex);
      return result;
    }else {
      throw new IllegalArgumentException("beginIndex and count must be >=0");
    }
  }
  
  /**
   * Gets all 
   * 
   * @return
   */
  public List<StatusEvent> getAllStatusEvents() {
    return statusList;//Return the whole list 
  }
  
  /**
   * Returns the number of status events stored in this set listener.
   * 
   * @return Number of status events stored in this set listener.
   */
  public int getNumberOfStatusEvents() {
	  return statusList.size();
  }
  
  /**
   * Returns the SetListener's contents in a SetStatusTO object. This 
   * parameterless version of the method return all the status events stored in
   * this SetListener.
   *  
   * @return A SetStatusTO object containing the status information stored in 
   * 		 this SetListener.
   */
  public SetStatusTO getStatusAsTO() {
    SetStatusTO result = new SetStatusTO(this.status, this.statusList);
    return result;
  }
  
  /**
   * Returns the SetListener's contents in a SetStatusTO object. The retrieved
   * status events can be selected using the parameters firstStatusEventIndex
   * and numberOfRetrievedEvents.
   * 
   * @param firstStatusEventIndex	  The index of the first status event to be 
   * 								  included to the returned object's list of
   * 								  status events.
   * @param numberOfRetrievedEvents   The number of status events to be included
   * 								  to the returned object's list of status
   * 								  events.
   * @throws IllegalArgumentException if both firstStatusEventIndex and
   *								  numberOfRetrievedEvents are not non-
   *								  negative.
   * @return 						  A SetStatusTO object containing status 
   * 								  information from this SetListener.
   */
  public SetStatusTO getStatusAsTO(int firstStatusEventIndex, int numberOfRetrievedEvents) {
	if (firstStatusEventIndex >= 0 && numberOfRetrievedEvents >= 0) {
	  int fromIndex = firstStatusEventIndex; 
	  int toIndex = firstStatusEventIndex + numberOfRetrievedEvents - 1;
	  List<StatusEvent> statusEvents = createSubList(this.statusList, fromIndex, toIndex);
	  
	  SetStatusTO result = new SetStatusTO(this.status, statusEvents);
	  return result;
	}
	else {
      throw new IllegalArgumentException("Both firstStatusEventIndex and numberOfRetrievedEvents must be >= 0");
	}
  }
  
  protected List<StatusEvent> createSubList(List<StatusEvent> list, int fromIndex, int toIndex) {
	List<StatusEvent> result = new Vector<StatusEvent>();
	int lastIndexInList = list.size() - 1;
	int currentIndex = fromIndex;
	while(currentIndex <= lastIndexInList && currentIndex <= toIndex) {
		StatusEvent currentListItem = list.get(currentIndex);
		result.add(currentListItem);
		++currentIndex;
	}
	return result;
  }
}