package com.distocraft.dc5000.etl.engine.main;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.engine.system.SetStatusTO;

/**
 * 
 * @author ejarsok
 * 
 */

public class TestITransferEngineRMI extends UnicastRemoteObject implements ITransferEngineRMI {

  private boolean throwsE = false;
  
  private String DBLookup;
  
  private String activeExecutionProfile;
  
  public TestITransferEngineRMI(final boolean ee) throws RemoteException {
    super();
    this.throwsE = ee;
    try {
      Naming.rebind("//localhost:1200/TransferEngine", this);
      // System.out.println("Server registered to already running RMI naming");
    } catch (Throwable e) {

      try {

        LocateRegistry.createRegistry(1200);
        // System.out.println("RMI-Registry started on port " + 1200);

        Naming.bind("//localhost:1200/TransferEngine", this);
        // System.out.println("Server registered to started RMI naming");

      } catch (Exception exception) {
        exception.printStackTrace();
        // System.out.println("Unable to initialize LocateRegistry" +
        // exception);

      }
    }
  }

  public void activateScheduler() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();

  }

  public void activateSetInPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void addWorkerToQueue(String name, String type, Object wobj) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void changeAggregationStatus(String status, String aggregation, long datadate) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public boolean changeSetPriorityInPriorityQueue(Long ID, long priority) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public void execute(RockFactory rockFact, String collectionSetName, String collectionName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, String ScheduleInfo) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void execute(String collectionSetName, String collectionName, String ScheduleInfo) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public String executeAndWait(String collectionSetName, String collectionName, String ScheduleInfo)
      throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public void fastGracefulShutdown() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void forceShutdown() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public Set getAllActiveSetTypesInExecutionProfiles() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    HashSet hs = new HashSet();
    hs.add("type");
    hs.add("type0");
    
    return hs;
  }

  public Set getAllRunningExecutionSlotWorkers() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public List getExecutedSets() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public List getFailedSets() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public String getPluginConstructorParameterInfo(String pluginName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public String getPluginConstructorParameters(String pluginName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public String getPluginMethodParameters(String pluginName, String methodName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public String[] getPluginMethods(String pluginName, boolean isGetSetMethods, boolean isGetGetMethods)
      throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public String[] getPluginNames() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public List getQueuedSets() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public List getRunningSets() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public void giveEngineCommand(String com) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();

  }

    public void disableTechpack(String techpackName) throws RemoteException {
    }

    public void disableSet(String techpackName, String setName) throws RemoteException {
    }

    public void disableAction(String techpackName, String setName, Integer actionOrder) throws RemoteException {
    }

    public void enableTechpack(String techpackName) throws RemoteException {
    }

    public void enableSet(String techpackName, String setName) throws RemoteException {
    }

    public void enableAction(String techpackName, String setName, Integer actionNumber) throws RemoteException {
    }

    public ArrayList<String> showDisabledSets() throws RemoteException {
        return null;
    }

    public void holdExecutionSlot(int ExecutionSlotNumber) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void holdPriorityQueue() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void holdSetInPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public boolean isInitialized() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public boolean isSetRunning(Long techpackID, Long setID) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public void lockExecutionprofile() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public List loggingStatus() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public void reaggregate(String aggregation, long datadate) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void reloadDBLookups(String tableName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    DBLookup = tableName;
  }

  public void reloadExecutionProfiles() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void reloadLogging() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void reloadProperties() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }
  
  public void reloadAggregationCache() throws RemoteException {
	    // TODO Auto-generated method stub
	    if(throwsE)
	      throw new RemoteException();
  }

  @Override
  public void reloadAlarmConfigCache() throws RemoteException {
    if(throwsE)
      throw new RemoteException();
  }
  
  public void reloadTransformations() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public boolean removeSetFromPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public void restartExecutionSlot(int ExecutionSlotNumber) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void restartPriorityQueue() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public boolean setActiveExecutionProfile(String profileName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    if(profileName != null && profileName.length() > 0) {
      activeExecutionProfile = profileName;
      return true;
    }
    else
      return false;
  }

  public boolean setActiveExecutionProfile(String profileName, String messageText) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public boolean setAndWaitActiveExecutionProfile(String profileName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return false;
  }

  public void slowGracefulShutdown() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public List status() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    return null;
  }

  public void unLockExecutionprofile() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void updateTransformation(String tpName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void writeSQLLoadFile(String fileContents, String fileName) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }
  
  public String getDBLookup() {
    return DBLookup;
  }
  
  public String getActiveExecutionProfile() {
    return activeExecutionProfile;
  }

public SetStatusTO executeSetViaSetManager(String collectionSetName,
		String collectionName, String ScheduleInfo, Properties props)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public String executeWithSetListener(String collectionSetName,
		String collectionName, String ScheduleInfo) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public SetStatusTO getSetStatusViaSetManager(String collectionSetName,
		String collectionName, int beginIndex, int count)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public SetStatusTO getStatusEventsWithId(String statusListenerId,
		int beginIndex, int count) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public List<String> slotInfo() throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ArrayList<String> showActiveInterfaces() throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public String currentProfile() throws RemoteException {
  // TODO Auto-generated method stub
  return null;
}

}
