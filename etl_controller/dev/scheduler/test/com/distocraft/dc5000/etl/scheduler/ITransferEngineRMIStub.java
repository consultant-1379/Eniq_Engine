package com.distocraft.dc5000.etl.scheduler;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;

import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.engine.system.SetStatusTO;

/**
 * Stub implementing the Remote Method Invocation class which Registers remote
 * methods to be used in some of the test classes (like SchedulerThread).
 * 
 * @author EJAAVAH
 * 
 */
public class ITransferEngineRMIStub extends UnicastRemoteObject implements ITransferEngineRMI {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   * Wanted remote method names are bound to be used in test classes.
   * 
   * @throws RemoteException
   */
  public ITransferEngineRMIStub() throws RemoteException {
    super();
    try {
      Naming.rebind("//localhost:1200/Engine", this);
      System.out.println("Server registered to already running RMI naming");
    } catch (Throwable e) {

      try {

        LocateRegistry.createRegistry(1200);
        System.out.println("RMI-Registry started on port " + 1200);

        Naming.bind("//localhost:1200/Engine", this);
        System.out.println("Server registered to started RMI naming");

      } catch (Exception exception) {
        System.out.println("Unable to initialize LocateRegistry " + exception);
      }
    }
  }

  public void activateScheduler() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void activateSetInPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void addWorkerToQueue(String name, String type, Object wobj) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void changeAggregationStatus(String status, String aggregation, long datadate) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public boolean changeSetPriorityInPriorityQueue(Long ID, long priority) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  public void execute(RockFactory rockFact, String collectionSetName, String collectionName) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void execute(String collectionSetName, String collectionName, String ScheduleInfo) throws RemoteException {
    // TODO Auto-generated method stub

  }
  
  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName) throws RemoteException {

  }

  /**
   * This method emulates executing certain set. However nothing is actually
   * done, this is implemented just for testing purposes.
   */
  public void execute(String url, String userName, String password, String dbDriverName, String collectionSetName,
      String collectionName, String ScheduleInfo) throws RemoteException {
  }

  public String executeAndWait(String collectionSetName, String collectionName, String ScheduleInfo)
      throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public void fastGracefulShutdown() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void forceShutdown() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public Set getAllActiveSetTypesInExecutionProfiles() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public Set getAllRunningExecutionSlotWorkers() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public List getExecutedSets() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public List getFailedSets() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPluginConstructorParameterInfo(String pluginName) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPluginConstructorParameters(String pluginName) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPluginMethodParameters(String pluginName, String methodName) throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public String[] getPluginMethods(String pluginName, boolean isGetSetMethods, boolean isGetGetMethods)
      throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public String[] getPluginNames() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public List getQueuedSets() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public List getRunningSets() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public void holdExecutionSlot(int ExecutionSlotNumber) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void holdPriorityQueue() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void holdSetInPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub

  }

  /**
   * method which checks if engine is initialized. For testing purposes this
   * method always returns true.
   */
  public boolean isInitialized() throws RemoteException {
    return true;
  }

  public boolean isSetRunning(Long techpackID, Long setID) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  public void lockExecutionprofile() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void reaggregate(String aggregation, long datadate) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void reloadDBLookups(String tableName) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void reloadExecutionProfiles() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void reloadProperties() throws RemoteException {
    // TODO Auto-generated method stub

  }

    public void reloadAggregationCache() throws RemoteException {
    }

    public void reloadTransformations() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public boolean removeSetFromPriorityQueue(Long ID) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  public void restartExecutionSlot(int ExecutionSlotNumber) throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void restartPriorityQueue() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public boolean setActiveExecutionProfile(String profileName) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean setAndWaitActiveExecutionProfile(String profileName) throws RemoteException {
    // TODO Auto-generated method stub
    return false;
  }

  public void slowGracefulShutdown() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public List status() throws RemoteException {
    // TODO Auto-generated method stub
    return null;
  }

  public void unLockExecutionprofile() throws RemoteException {
    // TODO Auto-generated method stub

  }

  public void writeSQLLoadFile(String fileContents, String fileName) throws RemoteException {
    // TODO Auto-generated method stub

  }

public SetStatusTO executeSetViaSetManager(String arg0, String arg1,
		String arg2, Properties arg3) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public String executeWithSetListener(String arg0, String arg1, String arg2)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public SetStatusTO getSetStatusViaSetManager(String arg0, String arg1,
		int arg2, int arg3) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public SetStatusTO getStatusEventsWithId(String arg0, int arg1, int arg2)
		throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public void giveEngineCommand(String arg0) throws RemoteException {
	// TODO Auto-generated method stub
	
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

public List loggingStatus() throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

public void reloadLogging() throws RemoteException {
	// TODO Auto-generated method stub
	
}

public boolean setActiveExecutionProfile(String arg0, String arg1)
		throws RemoteException {
	// TODO Auto-generated method stub
	return false;
}

public void updateTransformation(String arg0) throws RemoteException {
	// TODO Auto-generated method stub
	
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

@Override
public void reloadAlarmConfigCache() throws RemoteException {
  // TODO Auto-generated method stub
  
}

}
