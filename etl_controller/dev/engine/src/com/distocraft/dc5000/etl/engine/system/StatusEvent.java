/**
 * 
 */
package com.distocraft.dc5000.etl.engine.system;

import java.io.Serializable;
import java.util.Date;

/**
 * @author epetrmi
 *
 */
public class StatusEvent implements Serializable {
  
  /**
   * Generated UID 
   */
  private static final long serialVersionUID = 5462997671404047385L;
  
  private String dispatcher = ""; 
  private Date time = null;
  private String message = "";
  
  public static StatusEvent statusEventWithCurrentTime(String aDispatcherName, String aMessage) {
	  StatusEvent result = new StatusEvent(aDispatcherName, null, aMessage);
	  Date currentTime = new Date(System.currentTimeMillis());
	  result.setTime(currentTime);
	  return result;
  }

  public static StatusEvent statusEventWithCurrentTime(Object aDispatcher, String aMessage) {
	  String dispatcherName = aDispatcher.getClass().getName();
	  return statusEventWithCurrentTime(dispatcherName, aMessage);
  }
  
  public static StatusEvent statusEventWithCurrentTime(String aMessage) {
	  return statusEventWithCurrentTime("", aMessage);
  }
  
  public StatusEvent(String aDispatcherName, Date aTime, String aMessage) {
	this.dispatcher = aDispatcherName;
    this.time = aTime;
    this.message = aMessage;
  }
  
  /**
   * A constructor. If aDispatcher == null, then the dispatcher class name, 
   * stored in the object, is also set to null.
   * 
   * @param aDispatcher
   * @param aTime
   * @param aMessage
   */
  public StatusEvent(Object aDispatcher, Date aTime, String aMessage) {
	if (aDispatcher != null) {
		this.dispatcher = aDispatcher.getClass().getName();
	}
	else {
		this.dispatcher = null;
	}
	
	this.time = aTime;
	this.message = aMessage;
  }
  
  public StatusEvent(String aMessage) {
	this.message = aMessage;
  }
  
  public String getDispatcher() {
    return this.dispatcher;
  }
  
  public Date getTime() {
    return this.time;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setDispatcher(String aDispatcher) {
    this.dispatcher = aDispatcher;
  }
  
  public void setTime(Date aTime) {
    this.time = aTime;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    String className = "StatusEvent";//this.getClass().getSimpleName();
	return className + ": " +
           "dispatcher=" + this.dispatcher + " : " +
           "time=" + this.time + " : " +
	       "message=" + this.message + " : "
//         "xxx="+xxx + " : "
           ;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
	else if (!(obj instanceof StatusEvent)) {
      return false;
    }
    else {
      StatusEvent otherStatusEvent = (StatusEvent)obj;
      boolean result = this.dispatcher.equals(otherStatusEvent.dispatcher) &&
                       this.time.equals(otherStatusEvent.time) &&
                       this.message.equals(otherStatusEvent.message);
      return result;
    }
  }
}
