package com.distocraft.dc5000.etl.scheduler;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Stub implementing the Remote Method Invocation class which Registers remote
 * methods to be used in some of the test classes (like SchedulerAdmin).
 * 
 * @author EJAAVAH
 * 
 */
public class ISchedulerRMIStub extends UnicastRemoteObject implements ISchedulerRMI {

  /**
   * SchedulerAdminTest object for flagging purposes.
   */
  private SchedulerAdminTest schedulerAdminTest;

  /**
   * Wanted remote method names are bound to be used in test classes.
   * 
   * @param test =
   *          SchedulerAdminTest object for flagging.
   * 
   * @throws RemoteException
   */
  public ISchedulerRMIStub(SchedulerAdminTest test) throws RemoteException {
    super();

    schedulerAdminTest = test;

    try {
      Naming.rebind("//localhost:1100/TestScheduler", this);
      System.out.println("Server registered to already running RMI naming");
    } catch (Throwable e) {

      try {

        LocateRegistry.createRegistry(1100);
        System.out.println("RMI-Registry started on port " + 1100);

        Naming.bind("//localhost:1100/TestScheduler", this);
        System.out.println("Server registered to started RMI naming");

      } catch (Exception exception) {
        System.out.println("Unable to initialize LocateRegistry " + exception);
      }
    }
  }

  /**
   * Default serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class.
   */
  public void hold() throws RemoteException {
    schedulerAdminTest.holdTestFlag = true;
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class.
   */
  public void reload() throws RemoteException {
    schedulerAdminTest.reloadTestFlag = true;
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class.
   */
  public void reloadLoggingProperties() throws RemoteException {
    schedulerAdminTest.reloadLoggingPropertiesTestFlag = true;
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class.
   */
  public void shutdown() throws RemoteException {
    schedulerAdminTest.shutdownTestFlag = true;
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class.
   */
  public List status() throws RemoteException {
    schedulerAdminTest.statusTestFlag = true;
    List statuslist = new Vector();
    return statuslist;
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class if
   * parameter variable is not null.
   */
  public void trigger(String name) throws RemoteException {
    if(name != null) {
    schedulerAdminTest.triggerByNameTestFlag = true;
    }
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class if
   * parameter variable is not null.
   */
  @Override
  public void trigger(List<String> list) throws RemoteException {
    if(list != null) {
    schedulerAdminTest.triggerByListTestFlag = true;
    }
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class if
   * parameter variable is not null.
   */
  @Override
  public void trigger(List<String> list, Map<String, String> map) throws RemoteException {
    if(list!=null && map != null) {
    schedulerAdminTest.triggerByMapTestFlag = true;
    }
  }

  /**
   * This remote method is for testing purposes and does not actually do
   * anything apart from changing the test flag in SchedulerAdminTest class if
   * parameter variable is not null.
   */
  public void trigger(String name, String command) throws RemoteException {
    if(name != null && command != null) {
    schedulerAdminTest.triggerByNameAndContextTestFlag = true;
    }
  }

}
