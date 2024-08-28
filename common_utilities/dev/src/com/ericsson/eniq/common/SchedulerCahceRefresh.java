package com.ericsson.eniq.common;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;

public class SchedulerCahceRefresh {
	
	private static Logger log = Logger.getLogger(SchedulerCahceRefresh.class.getName());
	
	  public static void refereshSchedulerCache() 
		throws Exception{
	  
	    log.finer("Refresing Scheduler Cache...");
	    
	    String serverHostName;

	    int serverPort;

	    String serverRefName;
  
        String etlcServerPropertiesFile = System.getProperty("CONF_DIR");
        if (etlcServerPropertiesFile == null) {
            log.fine("System property CONF_DIR not defined. Using default");
            etlcServerPropertiesFile = "/eniq/sw/conf";
        }
        if (!etlcServerPropertiesFile.endsWith(File.separator)) {
            etlcServerPropertiesFile += File.separator;
        }

        etlcServerPropertiesFile += "ETLCServer.properties";

        log.info("Reading server configuration from \"" + etlcServerPropertiesFile + "\"");

	    FileInputStream streamProperties = null;
	    Properties appProps = new Properties();

	    try {

	      streamProperties = new FileInputStream(etlcServerPropertiesFile);
	      appProps.load(streamProperties);

	    } finally {
	      if (streamProperties != null)
	        streamProperties.close();
	    }
	    serverHostName = appProps.getProperty("SCHEDULER_HOSTNAME", null);
	    if (serverHostName == null) { // trying to determine hostname
	      serverHostName = "localhost";

	      try {
	        serverHostName = InetAddress.getLocalHost().getHostName();
	      } catch (java.net.UnknownHostException ex) {
	        log.log(Level.FINE, "getHostName failed", ex);
	      }
	    }
	    
	    serverPort = 1200;
	    String sporttmp = appProps.getProperty("SCHEDULER_PORT", "1200");
	    try {
	      serverPort = Integer.parseInt(sporttmp);
	    } catch (NumberFormatException nfe) {
	      log.info("Using default SCHEDULER_PORT 1200.");
	    }

	    serverRefName = appProps.getProperty("SCHEDULER_REFNAME", null);
	    if (serverRefName == null) {
	      log.info("Using default SCHEDULER_REFNAME \"Scheduler\"");
	      serverRefName = "Scheduler";
	    }

	    String rmiRef = "//" + serverHostName + ":" + serverPort + "/" + serverRefName;

	    log.config("RMI Reference is: " + rmiRef);
    	ISchedulerRMI scheduler = null;
	    try{
	    	scheduler = (ISchedulerRMI) Naming.lookup(rmiRef);
	    }
	    catch(Exception e){
	    	log.severe("Scheduler object has not been returned");
	    	throw new Exception("Scheduler object has not been returned",e);
	    }
	    
	    try{
	     scheduler.reload();
	    }
	    catch(Exception e){
	    	log.severe("Problem while reloading Scheduler");
	    	throw new Exception("Problem while reloading Scheduler", e);
	    	
	    }
	    
	    
		
}

}
