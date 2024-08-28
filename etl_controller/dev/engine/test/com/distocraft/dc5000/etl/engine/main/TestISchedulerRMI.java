package com.distocraft.dc5000.etl.engine.main;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;

/**
 * 
 * @author ejarsok
 * 
 */

public class TestISchedulerRMI extends UnicastRemoteObject implements ISchedulerRMI {

  private boolean throwsE = false;
  
  private ArrayList al = new ArrayList();
  
  private String content = "";
  
  public TestISchedulerRMI(final boolean ee) throws RemoteException {
    super();
    this.throwsE = ee;
    try {
      Naming.rebind("//localhost:1200/Scheduler", this);
      //System.out.println("Server registered to already running RMI naming");
    } catch (Throwable e) {

      try {

        LocateRegistry.createRegistry(1200);
        //System.out.println("RMI-Registry started on port " + 1200);

        Naming.bind("//localhost:1200/Scheduler", this);
        //System.out.println("Server registered to started RMI naming");

      } catch (Exception exception) {
        exception.printStackTrace();
        //System.out.println("Unable to initialize LocateRegistry" + exception);
      }
    }

  }

  public void hold() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void reload() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void reloadLoggingProperties() throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public void shutdown() throws RemoteException {
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

  public void trigger(String name) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
    
    al.add(name);
  }

  @Override
  public void trigger(List<String> list) throws RemoteException {
    if(throwsE)
      throw new RemoteException();
    
    String cont = "";
    Iterator it = list.iterator();
    while(it.hasNext()) {
      cont = (String) it.next();
      if(it.hasNext())
        content += cont + ",";
      else
        content += cont;
    }
  }

  @Override
  public void trigger(List<String> list, Map<String, String> arg0) throws RemoteException {
    if(throwsE)
      throw new RemoteException();
    
    String cont = "";
    for (String item : list) {
      cont += item + "=";
      for (Entry<String, String> entry : arg0.entrySet())  {
        if (!content.isEmpty()) {
          content += ",";
        }
        content += entry.getKey() + "=" + entry.getValue();  
      }
      cont += "\n";
    }
  }
  
  public void trigger(String name, String command) throws RemoteException {
    // TODO Auto-generated method stub
    if(throwsE)
      throw new RemoteException();
  }

  public ArrayList getTriggerArrayList() {
    return al;
  }
  
  public String getContent() {
    return content;
  }

}
