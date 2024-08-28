package com.distocraft.dc5000.etl.engine.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.Properties;
import com.distocraft.dc5000.common.SessionHandler;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.ExceptionHandler;
import com.distocraft.dc5000.etl.engine.common.Share;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlot;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfile;
import com.distocraft.dc5000.etl.engine.executionslots.ExecutionSlotProfileHandler;
import com.distocraft.dc5000.etl.engine.main.exceptions.InvalidSetParametersException;
import com.distocraft.dc5000.etl.engine.main.exceptions.InvalidSetParametersRemoteException;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.priorityqueue.PriorityQueue;
import com.distocraft.dc5000.etl.engine.priorityqueue.PriorityQueueThread;
import com.distocraft.dc5000.etl.engine.system.AlarmConfigCacheWrapper;
import com.distocraft.dc5000.etl.engine.system.ETLCEventHandler;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.engine.system.SetListenerManager;
import com.distocraft.dc5000.etl.engine.system.SetManager;
import com.distocraft.dc5000.etl.engine.system.SetStatusTO;
import com.distocraft.dc5000.etl.monitoring.ReAggregation;
import com.distocraft.dc5000.etl.parser.TransformerCache;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_errors;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_batches;
import com.distocraft.dc5000.etl.rock.Meta_transfer_batchesFactory;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;
import com.distocraft.dc5000.repository.cache.ActivationCache;
import com.distocraft.dc5000.repository.cache.AggregationRuleCache;
import com.distocraft.dc5000.repository.cache.AggregationStatus;
import com.distocraft.dc5000.repository.cache.AggregationStatusCache;
import com.distocraft.dc5000.repository.cache.DBLookupCache;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.distocraft.dc5000.repository.cache.PhysicalTableCache;
import com.distocraft.dc5000.repository.dwhrep.Configuration;
import com.distocraft.dc5000.repository.dwhrep.ConfigurationFactory;
import com.ericsson.eniq.licensing.cache.DefaultLicenseDescriptor;
import com.ericsson.eniq.licensing.cache.DefaultLicensingCache;
import com.ericsson.eniq.licensing.cache.DefaultLicensingResponse;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingResponse;
import com.ericsson.eniq.licensing.cache.LicenseInformation;

/**
 * The main class of ETLC.engine
 * 
 * @author Jarno Savinen, Tuomas Lemminkainen
 * @since JDK1.1
 */

public class TransferEngine extends UnicastRemoteObject implements ITransferEngineRMI {

    private static final long serialVersionUID = 1055137567126712402L;
    
    /** Starter and capacity license definitions */    
    public final static String ENIQ_12_STARTER_LICENSE = "CXC4011273";
    
    public final static String ENIQ_12_CAPACITY_LICENSE = "CXC4011274";
    
    /** The active starter license */
    private String activeStarterlicense = "";

    private long startedAt = 0L;

    private PluginLoader pluginLoader;

    private String pluginPath = "/work/dagger/plugins";

    private int serverPort;

    private String serverHostName;

    private String serverRefName;

    // SS
    private final String GET_PARAM_INFO = "getConstructorParameterInfo";

    private PriorityQueue priorityQueue;

    private PriorityQueueThread pqt;

    private ExecutionSlotProfileHandler executionSlotHandler;

    private long priorityQueuePollIntervall = 0;

    private int maxPriorityLevel = 0;

    private boolean usePriorityQueue = true;

    private boolean useDefaultExecutionSlots = false;

    private int NumberOfDefaulrExecutionSlots = 1;

    // this is used when startin set directly...
    private static ExecutionSlot staticExSlot = null;

    private String url = "";

    private String userName = "";

    private String password = "";

    private String dbDriverName = "";

    private Logger log;

    private boolean isInitialized = false;

    private EngineCom eCom = null;

    /** The number of CPU cores */
    private int numberOfCPUCores;
  
    /** The number of physical CPUs. */
    private int numberOfPhysicalCPUs;    
    
    /**
     * Constructor for starting the transfer
     * 
     * @param usePQ
     * @param useDefaultEXSlots
     * @param EXSlots
     * @throws RemoteException
     */
    public TransferEngine(final boolean usePQ, final boolean useDefaultEXSlots, final int EXSlots, final Logger log)
            throws RemoteException {
        super();

        this.usePriorityQueue = usePQ;
        this.useDefaultExecutionSlots = useDefaultEXSlots;
        this.NumberOfDefaulrExecutionSlots = EXSlots;

        this.log = log;
    }

    /**
     * Constructor for starting the transfer
     */
    public TransferEngine(final Logger log) throws RemoteException {
        super();

        this.log = log;
    }
        
  /**
   * Checks the starter license.
   * @param cache               The licensing cache.
   * @param starterLicense      The starter license number.
   * @param licenseDescription  String description of the license.
   * @return starterLicenseOk   A boolean value that is true if the starter license is valid.
   */
  private boolean checkStarterLicense(final LicensingCache cache, final String starterLicense, final String licenseDescription) {
    boolean starterLicenseOk = true;
    this.log.log(Level.INFO, "Checking Eniq starter license: " + starterLicense);   
    
    try {
      // create a dummy license descriptor. CXC4010583 = FAJ 121 1137 = Eniq
      // Starter license
      final LicenseDescriptor license = new DefaultLicenseDescriptor(starterLicense);
      
      // get a licensing response for the created descriptors.
      LicensingResponse response = null;
      
      if (ENIQ_12_STARTER_LICENSE.equals(starterLicense)) {
        // Check starter license using number of physical CPUs:
        response = cache.checkCapacityLicense(license, numberOfPhysicalCPUs);                     
      } else {
        this.log.log(Level.WARNING, "Eniq starter license not recognised: " + starterLicense);
        return false;
      }
      
      if (response.isValid()) {
        this.log.log(Level.INFO,
            "The Eniq Starter license is valid: " + response.isValid() + " msg: " + response.getMessage()
                + ". Engine will start normally.");
        final long expiryDate = getExpiryDate(cache, starterLicense);
        /*
         * If the expiry date is in future, then initiate a TimerTask which will
         * check the Starter License at 4 a.m. everyday
         */
        if (expiryDate > 0) {
          this.log
              .log(
                  Level.INFO,
                  "Starter license is not an unlimited expiry license. Starting a timer task to poll every day at 4 am to check if the starter license is valid");
          scheduleLicenseCheckTask();
        } else {
          this.log.log(Level.INFO, "Starter license is a no expiry license.");
        }
      } else {
        this.log.log(Level.SEVERE,
            licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
        log.log(Level.INFO, "Response from licensing module: " + response.getResponseType());
        System.out
            .println(licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
        starterLicenseOk = false;
      }
    } catch (Exception exc) {
      log.log(Level.SEVERE, "Error checking starter license: " + exc.toString());
      starterLicenseOk = false;
    }
    return starterLicenseOk;
  }

  /**
   * Checks a license and returns a LicensingResponse. Calls checkLicense() in licensing module.
   * @param cache
   * @param starterLicense
   * @return
   * @throws RemoteException
   */
  protected LicensingResponse getLicensingResponse(final LicensingCache cache, final String starterLicense)
  throws RemoteException {
    final LicenseDescriptor license = new DefaultLicenseDescriptor(starterLicense);
    
    // get a licensing response for the created descriptors.
    final LicensingResponse response = cache.checkLicense(license);
    return response;
  }
  
  /**
   * Get expiry date for starter license.
   * @param cache             
   * @param starterLicense
   * @return expiryDate       
   * @throws RemoteException
   */
  protected long getExpiryDate(final LicensingCache cache, final String starterLicense) throws RemoteException {
    /*
     * Check for time-limited starter license - CXC4010583.
     */
    this.log.log(Level.INFO,
    "Checking if starter license is time-restricted. Getting license information for all installed licenses");
    final Vector<LicenseInformation> information = cache.getLicenseInformation();
    final Enumeration<LicenseInformation> elements = information.elements();
    long expiryDate = 0;
    while (elements.hasMoreElements()) {
      final LicenseInformation li = elements.nextElement();
      if (li.getFeatureName().equalsIgnoreCase(starterLicense)) {
        expiryDate = li.getDeathDay();
        this.log.log(Level.INFO, "Expiry date in long of starter license:" + expiryDate);
      }
    }
    return expiryDate;
  }
  
  /**
   * Starts a timer task to check once a day at 4 am if the starter license is valid.
   */
  protected void scheduleLicenseCheckTask() {
    final EngineLicenseCheckTask checkLicense = new EngineLicenseCheckTask();
    // perform the task once a day at 4 a.m., starting tomorrow morning
    final Timer timer = new Timer();
    final long oncePerDay = 1000 * 60 * 60 * 24;
    timer.scheduleAtFixedRate(checkLicense, checkLicense.getTomorrowMorning4am(), oncePerDay);
  }

  
  /**
   * Checks the capacity license.
   * @param cache
   * @param capacityLicense
   * @param licenseDescription
   * @return capacityLicenseOk True if the capacity license
   */
  private boolean checkCapacityLicense(final LicensingCache cache, final String capacityLicense, final String licenseDescription) {
    boolean capacityLicenseOk = true;
    this.log.log(Level.INFO, "Checking Eniq Capacity license: " + capacityLicense);  

    try {
      // now check the capacity license.
      LicensingResponse response = null;
      final LicenseDescriptor capacity = new DefaultLicenseDescriptor(capacityLicense);
      
      if (capacityLicense.equals(ENIQ_12_CAPACITY_LICENSE)) {
        // For Eniq 11, check the capacity with number of physical CPUs:
       // setCapacity is called to set the actual capacity of server. 
        // if setCapacity method is not called. By default capacity is -1 and no capacity check will be done in Licensing
      capacity.setCapacity(numberOfPhysicalCPUs);
        response = cache.checkCapacityLicense(capacity, numberOfPhysicalCPUs);  
      } else {
        this.log.log(Level.WARNING, "Eniq capacity license not recognised: " + capacityLicense);
        return false;
      }
            
      if (response.isValid()) {
        this.log
          .log(
              Level.INFO,"The Eniq Capacity license is valid: " + response.isValid() + " msg: " + response.getMessage()
            + ". Engine will start normally.");
      } else {
        log.severe(licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
        log.log(Level.SEVERE, "Licensing response: " + response.getMessage());
        System.out
            .println(licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
        capacityLicenseOk = false;
      }
    } catch (Exception exc) {
      log.log(Level.SEVERE, licenseDescription + " is not valid. Engine will not start. Please check the validity of the " + licenseDescription);
      capacityLicenseOk = false;
    }
    return capacityLicenseOk;
  }

  /**
   * Gets the number of cores.
   * @return numberOfCores
   */
  protected int getNumberOfCPUCores() {
    int numberOfCores = 0;
    numberOfCores = DefaultLicensingCache.getNumberOfCores();
    return numberOfCores;
  }
  
  /**
   * Gets the number of physical CPUs.
   * @return  numberOfPhysicalCPUs
   * @throws RemoteException 
   */
  protected int getNumberOfPhysicalCPUs() {
    int numberOfPhysicalCPUs = 0;
    numberOfPhysicalCPUs = DefaultLicensingCache.getNumberOfPhysicalCPUs();
    return numberOfPhysicalCPUs;
  }

  /**
   * Gets the licensing cache.
   * @return cache  The LicensingCache.
   * @throws NotBoundException
   * @throws MalformedURLException
   * @throws RemoteException
   */
  protected LicensingCache retrieveLicensingCache() throws NotBoundException, MalformedURLException, RemoteException {
    // contact the registry and get the cache instance.
    final LicensingCache cache = (LicensingCache) Naming.lookup("rmi://" + this.serverHostName + ":"
            + this.serverPort + "/LicensingCache");
    return cache;
  }
  
    /**
     * Initializes the server - binds the server object to RMI registry -
     * initializes the omi connection - instantiates the access controller
     * 
     * @param name
     *          String Name of server in registry
     * @return boolean true if initialisation succeeds otherwise false
     * @exception
     */
    public boolean init() {

        log.fine("Initializing TransferEngine...");

        startedAt = System.currentTimeMillis();

        String dwh_url = null;
        String dwh_usr = null;
        String dwh_pwd = null;
        String dwh_drv = null;

        String dwh_dba_url = null;
        String dwh_dba_usr = null;
        String dwh_dba_pwd = null;
        String dwh_dba_drv = null;

        String rep_url = null;
        String rep_usr = null;
        String rep_pwd = null;
        String rep_drv = null;

        // check connection to metadata
        RockFactory etlRock = null;              
        try {

            try {

                getProperties();
                this.log.log(Level.FINE, "Getting license information for Eniq Starter license from host "
                    + this.serverHostName + ".");
                final LicensingCache cache = retrieveLicensingCache();

                if (cache == null) {
                    this.log
                            .log(
                                    Level.SEVERE,
                                    "Could not verify Eniq Starter license from LicenseManager host "
                                            + this.serverHostName
                                            + ". Engine will not start without the Eniq Starter license verification. Please verify that LicenseManager service is running and try again.");
                    return false;
                } else {
                  boolean licenseCheck = false;
                  
                  // Get number of cores and physical CPUs:
                  numberOfCPUCores = getNumberOfCPUCores();
                  numberOfPhysicalCPUs = getNumberOfPhysicalCPUs();
                  if (numberOfCPUCores==-1){
                    throw new Exception("Error getting number of CPU Cores.");
                  }
                  if (numberOfPhysicalCPUs==-1){
                    throw new Exception("Error getting number of Physical CPUs.");
                  }
                  
                  this.log.log(Level.INFO, "Checking Eniq 12 starter and capacity licenses");                  
                  // Check Eniq 11 starter license:                  
                  final boolean eniq12starterLicenseOk = checkStarterLicense(cache, ENIQ_12_STARTER_LICENSE, "Starter license for Eniq 12");
                  if (eniq12starterLicenseOk) {
                    activeStarterlicense = ENIQ_12_STARTER_LICENSE;
                    // Check capacity license also:
                    final boolean eniq12capacityLicenseOk = checkCapacityLicense(cache, ENIQ_12_CAPACITY_LICENSE, "Capacity license for Eniq 12");
                    if (eniq12capacityLicenseOk) {
                      licenseCheck = true;
                      this.log.log(Level.INFO, "Starter license and capacity license for Eniq 12 are ok, licenseCheck = " + licenseCheck);
                    }
                  }                                            
                  
                  if (!licenseCheck) {
                    log.log(Level.SEVERE, "Starter or capacity license check failed");
                    return false;
                  }
                }
            } catch (final ConnectException ce) {
                this.log.log(Level.SEVERE, "Failed to create connection to LicenseManager. Engine will not start.");
                return false;
            } catch (final Exception e) {
                this.log
                        .log(
                                Level.SEVERE, "Unknown error. Engine will not start", e);
                return false;
            }

            etlRock = new RockFactory(url, userName, password, dbDriverName, "ETLEngInit", true);

            final Meta_databases selO = new Meta_databases(etlRock);

            final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlRock, selO);

            final Vector<Meta_databases> dbs = mdbf.get();

            for (int i = 0; i < dbs.size(); i++) {

                final Meta_databases mdb = (Meta_databases) dbs.get(i);

                if (mdb.getConnection_name().equalsIgnoreCase("dwh") && mdb.getType_name().equalsIgnoreCase("USER")) {
                    dwh_url = mdb.getConnection_string();
                    dwh_usr = mdb.getUsername();
                    dwh_pwd = mdb.getPassword();
                    dwh_drv = mdb.getDriver_name();
                } else if (mdb.getConnection_name().equalsIgnoreCase("dwhrep")
                        && mdb.getType_name().equalsIgnoreCase("USER")) {
                    rep_url = mdb.getConnection_string();
                    rep_usr = mdb.getUsername();
                    rep_pwd = mdb.getPassword();
                    rep_drv = mdb.getDriver_name();
                } else if (mdb.getConnection_name().equalsIgnoreCase("dwh")
                        && mdb.getType_name().equalsIgnoreCase("DBA")) {
                    dwh_dba_url = mdb.getConnection_string();
                    dwh_dba_usr = mdb.getUsername();
                    dwh_dba_pwd = mdb.getPassword();
                    dwh_dba_drv = mdb.getDriver_name();
                }

            }

            if (dwh_url == null || rep_url == null)
                throw new Exception("Database dwh must be defined in META_DATABASES"); // NOPMD
            // by
            // ejarsav
            // on
            // 13.4.2007
            // 14:49

        } catch (final Exception e) {
            log.log(Level.SEVERE, "Unable to initialize rockengine", e);
            log.log(Level.INFO, "Exiting..");
            System.exit(1);
        }

        try {
            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }
        } catch (final NumberFormatException exp) {
            return false;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }

        final String rmiRef = "//" + this.serverHostName + ":" + this.serverPort + "/" + this.serverRefName;

        log.config("Engine RMI reference is: \"" + rmiRef + "\"");

        try {
            Naming.rebind(rmiRef, this);
            log.fine("Server registered to already running RMI naming");
        } catch (final Throwable e) {
            try {

                LocateRegistry.createRegistry(this.serverPort);
                log.info("RMI-Registry started on port " + this.serverPort);

                Naming.bind(rmiRef, this);
                log.fine("Server registered to started RMI naming");

            } catch (final Exception exception) {

                log.log(Level.SEVERE, "Unable to initialize LocateRegistry", exception);

                return false;
            }
        }

        // Perform some static object initialization...

        try {

            final Share sh = Share.instance();

            eCom = new EngineCom();

            // Create static properties.
            new StaticProperties().reload();

            // Initialize SessionHandler
            SessionHandler.init();

            // reload properties
            Properties.reload();

            // Read properties
            getProperties();

            // Reload logging configurations
            reloadLogging();

            // Init AggregationStatusCache
            AggregationStatusCache.init(dwh_url, dwh_usr, dwh_pwd, dwh_drv);

            // Init Parser DataFormat Cache
            DataFormatCache.initialize(etlRock);

            // Init Loader PhysicalTable Cache
            PhysicalTableCache.initialize(etlRock);

            // Init Loader AggregationRule Cache
            AggregationRuleCache.initialize(etlRock);

            // Init Activation Cache
            ActivationCache.initialize(etlRock);

            // init DBLookup Cache
            DBLookupCache.initialize(etlRock);

            // create ETLC Event Handler
            final ETLCEventHandler eh = new ETLCEventHandler("LoadedTypes");
            eh.addListener(DBLookupCache.getCache());
            sh.add("LoadedTypes", eh);

            RockFactory rdwh = null;
            RockFactory rdwhdba = null;
            RockFactory rrep = null;
            try {
                rdwh = new RockFactory(dwh_url, dwh_usr, dwh_pwd, dwh_drv, "ETLEngTCInit", true);
                rdwhdba = new RockFactory(dwh_dba_url, dwh_dba_usr, dwh_dba_pwd, dwh_dba_drv, "ETLEngTCInit", true);
                rrep = new RockFactory(rep_url, rep_usr, rep_pwd, rep_drv, "ETLEngTCInit", true);

                // Init Transformer Cache
                final TransformerCache tc = new TransformerCache();
                tc.revalidate(rrep, rdwh);

                try {
                    // Get DWHDB's collation/charset/encoding
                    getDWHDBCharsetEncoding(rdwh);
                } catch (final Exception e) {
                    log.log(Level.WARNING, "Error in reading charset encoding from dwhdb database.", e);
                }
                configureWorkerLimits(rrep);

                try {
                    // Set DML_Options5 to dwhdb
                    runDMLOptions5Setting(rdwhdba);
                } catch (final Exception e) {
                    log.log(Level.WARNING, "Error in setting the DML_Options5 option to dwhdb database.", e);
                }
                
                // Init Alarm Config Cache
                AlarmConfigCacheWrapper.revalidate(rrep);

            } catch (final Exception e) {
                log.log(Level.WARNING, "Error in transformer or alarmcfg cache initialization", e);
            } finally {
                try {
                    rdwh.getConnection().close();
                } catch (final Exception e) {
                }
                try {
                    //rdwhdba.getConnection().close();   This is instead being closed in method runMax_query_parallelismSetting
                } catch (final Exception e) {
                }
                try {
                    rrep.getConnection().close();
                } catch (final Exception e) {
                }
            }

            // Create Execution profile
            if (this.useDefaultExecutionSlots) {
                executionSlotHandler = new ExecutionSlotProfileHandler(this.NumberOfDefaulrExecutionSlots);
            } else {
                executionSlotHandler = new ExecutionSlotProfileHandler(this.url, this.userName, this.password,
                        this.dbDriverName);
                if (executionSlotHandler.getAllExecutionProfiles() == null
                        || executionSlotHandler.getAllExecutionProfiles().isEmpty())
                    executionSlotHandler.createActiveProfile(this.NumberOfDefaulrExecutionSlots);
            }

            if (executionSlotHandler == null) {
                this.log.log(Level.SEVERE, "No executionSlots created. ExecutionSlotProfile is null.");
            } else {
                sh.add("executionSlotProfileObject", executionSlotHandler);
            }
            
            try {
                // Set max_query_parallelism to dwhdb
                runMax_query_parallelismSetting(rdwhdba);
            } catch (final Exception e) {
                log.log(Level.WARNING, "Error in setting the max_query_parallelism option to dwhdb database.", e);
            }

            if (this.usePriorityQueue) {

                // Create Priority queue
                priorityQueue = new PriorityQueue(this.priorityQueuePollIntervall, this.maxPriorityLevel);

                sh.add("priorityQueueObject", priorityQueue);

                // Create priority queue Thread
                pqt = new PriorityQueueThread(priorityQueue, executionSlotHandler);

                // startup schedulings
                addStartupSetsToQueue(etlRock);

                pqt.setName("Priority Queue");
                pqt.start();

            }

        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
        } finally {

            try {

                etlRock.getConnection().close();

            } catch (final Exception ef) {
                ExceptionHandler.handleException(ef);

            }
        }
        log.fine("TransferEngine initialized.");
        this.isInitialized = true;

        return true;

    } // init

    
  private void addStartupSetsToQueue(final RockFactory rock) {

        try {

            /* get list of collection sets */
            final Meta_collection_sets whereCollection_sets = new Meta_collection_sets(rock);
            final Meta_collection_setsFactory activeCollectionSets = new Meta_collection_setsFactory(rock,
                    whereCollection_sets);

            // list IDs of the active collection sets
            final List<Long> activeCollectionIDs = new ArrayList<Long>();
            for (int i = 0; i < activeCollectionSets.size(); i++) {

                final Meta_collection_sets cSet = activeCollectionSets.getElementAt(i);

                // if collection set is active add it to the list
                if (cSet.getEnabled_flag().equalsIgnoreCase("y")) {
                    activeCollectionIDs.add(cSet.getCollection_set_id());
                }
            }

            // retrievs rows from schedule
            final Meta_schedulings whereSchedule = new Meta_schedulings(rock);
            whereSchedule.setExecution_type("onStartup");
            whereSchedule.setHold_flag("N");
            final Meta_schedulingsFactory schedules = new Meta_schedulingsFactory(rock, whereSchedule);

            if (activeCollectionIDs.size() > 0)
                for (int i = 0; i < schedules.size(); i++) {

                    final Meta_schedulings schedule = schedules.getElementAt(i);

                    // Create only scheduling that reference active collection
                    // sets
                    if (activeCollectionIDs.contains(schedule.getCollection_set_id())) {

                        final Long colSetID = schedule.getCollection_set_id();
                        final Long colID = schedule.getCollection_id();

                        /* get list of collection sets */
                        final Meta_collection_sets whereCollection_sets1 = new Meta_collection_sets(rock);
                        whereCollection_sets1.setCollection_set_id(colSetID);
                        final Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(rock,
                                whereCollection_sets1);

                        /* get list of collections */
                        final Meta_collections whereCollections = new Meta_collections(rock);
                        whereCollections.setCollection_set_id(colSetID);
                        whereCollections.setCollection_id(colID);
                        final Meta_collectionsFactory mcf = new Meta_collectionsFactory(rock, whereCollections);

                        try {

                            // create engineThread (set)
                            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, mcsf
                                    .getElementAt(0).getCollection_set_name(),
                                    mcf.getElementAt(0).getCollection_name(), this.pluginLoader, "", log, eCom);

                            et.setName(mcsf.getElementAt(0).getCollection_set_name() + "_"
                                    + mcf.getElementAt(0).getCollection_name());

                            // adn ad iot to the queue.
                            priorityQueue.addSet(et);

                        } catch (final Exception e) {
                            log.log(Level.WARNING, "Could not create engineThread from (techpack/set): "
                                    + mcsf.getElementAt(0).getCollection_set_name() + "/"
                                    + mcf.getElementAt(0).getCollection_name(), e);

                        }

                    }

                }
        } catch (final Exception e) {
            log.log(Level.WARNING, "Error while adding statup set to queue", e);

        }
    }

    private void executeSet(final EngineThread et) throws Exception {

        while (!isInitialized()) {
            this.log.fine("Waiting for the engine to initialize before executing set " + et.getSetName() + ".");
            Thread.sleep(1000);
        }

        // Sanity checking...
        if (this.log == null) {
            this.log = Logger.getLogger("scheduler.thread");
            this.log.severe("TransferEngine.executeSet variable log was null. Created a new logger...");
        }

        if (et == null) {
            this.log.severe("TransferEngine.executeSet variable et was null.");
        }
        if (getPriorityQueue() == null) {
            this.log.severe("TransferEngine.executeSet variable this.priorityQueue was null.");
        }

        if (getExecutionSlotProfileHandler() == null) {
            this.log.severe("TransferEngine.executeSet variable this.executionSlotHandler was null.");
        }

        // put set to queue
        if (this.usePriorityQueue) {
            // put set to queue
            getPriorityQueue().addSet(et);
        } else {

            log.fine("No priority queue in use.. starting directly in execution slot");

            // put set directly to the first free execution slot
            staticExSlot = getExecutionSlotProfileHandler().getActiveExecutionProfile().getFirstFreeExecutionSlots();

            if (staticExSlot != null) {
                // free slot found
                staticExSlot.execute(et);
            } else {
                // NO free slot found
                log.info("No free execution slots left, unable to execute Set (" + staticExSlot.getName() + ")");

            }
        }

    }

    /**
     * @return
     */
    ExecutionSlotProfileHandler getExecutionSlotProfileHandler() {
        return this.executionSlotHandler;
    }

    /**
     * @return
     */
    PriorityQueue getPriorityQueue() {
        return this.priorityQueue;
    }

    /**
     * Executes the initialized collection set / collection
     * 
     * @param rockFact
     *          The database connection
     * @param collectionSetName
     *          the name of the transfer collection set
     * @param collectionName
     *          the name of the transfer collection
     * @exception RemoteException
     */
    public void execute(final RockFactory rockFact, final String collectionSetName, final String collectionName)
            throws RemoteException {

        try {

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            // create set
            final EngineThread et = new EngineThread(rockFact, collectionSetName, collectionName, this.pluginLoader,
                    log, eCom);
            et.setName(collectionSetName + "_" + collectionName);

            this.log.info("Calling executeSet from execute(3 parameters)");

            // execute the set
            executeSet(et);

        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }
    }

    /**
     * Executes the initialized collection set / collection
     * 
     * @param url
     *          Database url
     * @param userName
     *          Database user
     * @param password
     *          Database users password
     * @param dbDriverName
     *          Database driver
     * @param collectionSetName
     *          the name of the transfer collection set
     * @param collectionName
     *          the name of the transfer collection
     * @exception RemoteException
     */
    public void execute(final String url, final String userName, final String password, final String dbDriverName,
            final String collectionSetName, final String collectionName) throws RemoteException {

        try {

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, collectionSetName,
                    collectionName, this.pluginLoader, log, eCom);

            et.setName(collectionSetName + "_" + collectionName);

            this.log.info("Calling executeSet from execute(6 parameters)");

            // execute the set
            executeSet(et);

        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }

    }

    /**
     * Executes the initialized collection set / collection
     * 
     * @param url
     *          Database url
     * @param userName
     *          Database user
     * @param password
     *          Database users password
     * @param dbDriverName
     *          Database driver
     * @param collectionSetName
     *          the name of the transfer collection set
     * @param collectionName
     *          the name of the transfer collection
     * @param ScheduleInfo
     *          Informatin from the Scheduler
     * @exception RemoteException
     */
    public void execute(final String url, final String userName, final String password, final String dbDriverName,
            final String collectionSetName, final String collectionName, final String ScheduleInfo)
            throws RemoteException {
        try {

            // Some sanity checking...

            if (this.log == null) {
                this.log = Logger.getLogger("scheduler.thread");
                this.log
                        .severe("TransferEngine.execute(7 parameters) variable this.log was null. Created a new logger...");
            }

            if (url == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable url was null.");
            }
            if (userName == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable userName was null.");
            }
            if (password == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable password was null.");
            }
            if (dbDriverName == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable dbDriverName was null.");
            }
            if (collectionSetName == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable url collectionSetName null.");
            }
            if (collectionName == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable collectionName was null.");
            }
            if (ScheduleInfo == null) {
                this.log.severe("TransferEngine.execute(7 parameters) variable ScheduleInfo was null.");
            }

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, collectionSetName,
                    collectionName, this.pluginLoader, ScheduleInfo, log, eCom);

            et.setName(collectionSetName + "_" + collectionName);

            // execute the set
            executeSet(et);

        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);

        }

    }

    /**
     * Executes the initialized collection set / collection
     * 
     * @param url
     *          Database url
     * @param userName
     *          Database user
     * @param password
     *          Database users password
     * @param dbDriverName
     *          Database driver
     * @param collectionSetName
     *          the name of the transfer collection set
     * @param collectionName
     *          the name of the transfer collection
     * @param ScheduleInfo
     *          Informatin from the Scheduler
     * @exception RemoteException
     */
    public void execute(final String collectionSetName, final String collectionName, final String ScheduleInfo)
            throws RemoteException {

        try {

            checkSetIsValidAndEnabled(collectionSetName, collectionName);

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            final EngineThread et = createNewEngineThread(collectionSetName, collectionName, ScheduleInfo);

            et.setName(collectionSetName + "_" + collectionName);

            this.log.info("Calling executeSet from execute(3 parameters, all Strings)");

            // execute the set
            executeSet(et);

        } catch (final InvalidSetParametersException cannotStartSetEx) {
            ExceptionHandler.handleException(cannotStartSetEx);
            throw new InvalidSetParametersRemoteException(cannotStartSetEx.getMessage());
        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }
    }

    /**
     * Check that both the collection set (tech pack) and the collection name (set) exist in their respective tables
     * and are enabled
     * 
     * @param collectionSetName
     * @param collectionName
     * @throws RockException 
     * @throws SQLException 
     * @throws Exception
     */
    private void checkSetIsValidAndEnabled(final String collectionSetName, final String collectionName)
            throws InvalidSetParametersException, SQLException, RockException {
        final Long collectionSetID = checkCollectionSetExistsAndIsEnabled(collectionSetName);
        checkCollectionExistsWithCorrectParentSetAndIsEnabled(collectionName, collectionSetID);
    }

    /**
     * Check that the collection name (set) exist in the Meta_collections table and is enabled
     * 
     * @param collectionName
     * @param collectionSetId 
     * @throws InvalidSetParametersRemoteException 
     * @throws RockException 
     * @throws SQLException 
     */
    private void checkCollectionExistsWithCorrectParentSetAndIsEnabled(final String collectionName,
            final Long collectionSetId) throws InvalidSetParametersException, SQLException, RockException {
        final RockFactory etlRock = getEtlRepRockFactory();
        final Meta_collections whereMetaCollection = new Meta_collections(etlRock);
        whereMetaCollection.setCollection_name(collectionName);
        whereMetaCollection.setCollection_set_id(collectionSetId);
        final Meta_collectionsFactory metaCollectionFactory = createMetaCollectionFactory(etlRock, whereMetaCollection,
                " ORDER BY COLLECTION_NAME DESC;");
        final Vector<Meta_collections> collectionsWithThisName = metaCollectionFactory.get();
        if (collectionsWithThisName.isEmpty()) {
            throw new InvalidSetParametersException("Cannot start set, collection " + collectionName
                    + " doesn't exist, or incorrect Tech Pack was supplied");
        }
        final Meta_collections metaCollection = collectionsWithThisName.get(0);

        final String enabledStatus = metaCollection.getEnabled_flag();
        if (enabledStatus.equalsIgnoreCase("n")) {
            throw new InvalidSetParametersException("Cannot start set, collection " + collectionName + " is not enabled");
        }
    }

    /**
     * refactored out in order to get methods under unit test
     *  
     * @param etlRock
     * @param whereMetaCollection
     * @param orderByClause
     * @return
     * @throws SQLException
     * @throws RockException
     */
    Meta_collectionsFactory createMetaCollectionFactory(final RockFactory etlRock,
            final Meta_collections whereMetaCollection, final String orderByClause) throws SQLException, RockException {
        return new Meta_collectionsFactory(etlRock, whereMetaCollection, orderByClause);
    }

    /**
     * Check that the collection set (tech pack) exist in the Meta_Collection_sets table and is enabled
     * 
     * @param collectionSetName
     * @throws InvalidSetParametersRemoteException 
     * @throws RockException 
     * @throws SQLException
     * @return Long             the collection set id of the collection set 
     */
    private Long checkCollectionSetExistsAndIsEnabled(final String collectionSetName) throws InvalidSetParametersException,
            SQLException, RockException {
        Long collectionSetId = null;
        
        final RockFactory etlRock = getEtlRepRockFactory();
        final Meta_collection_sets whereMetaCollectionSets = new Meta_collection_sets(etlRock);
        whereMetaCollectionSets.setCollection_set_name(collectionSetName);
        final Meta_collection_setsFactory metaCollSetsFactory = createMetaCollectionSetsFactory(etlRock,
                whereMetaCollectionSets, " ORDER BY COLLECTION_SET_NAME DESC;");
        final Vector<Meta_collection_sets> setsWithThisName = metaCollSetsFactory.get();
        if (setsWithThisName.isEmpty()) {
            throw new InvalidSetParametersException("Cannot start set, collection set " + collectionSetName
                    + " doesn't exist");
        }
        //Iterate through the list of sets. Find the one that is active (if any) and get its collectionSetId.
        //If no active SET is found, throw exception. NOTE: there is only ever 1 active SET at any time.
        Meta_collection_sets metaCollectionSet = null;
        String isCollectionSetEnabled = null;
        Iterator<Meta_collection_sets> setIterator = setsWithThisName.iterator();
        while(setIterator.hasNext()){
          metaCollectionSet = setIterator.next();
          isCollectionSetEnabled = metaCollectionSet.getEnabled_flag();
          if(isCollectionSetEnabled.equalsIgnoreCase("y")){
            collectionSetId = metaCollectionSet.getCollection_set_id();
          }
        }
        if(collectionSetId == null){
            throw new InvalidSetParametersException("Cannot start set, collection set " + collectionSetName
                    + " is not enabled");
        }
        return collectionSetId;

    }

    /**
     * 
     * refactored out to get methods under unit test
     * 
     * @param etlRock
     * @param whereMetaCollectionSets
     * @param string orderByClause
     * @return
     * @throws SQLException
     * @throws RockException
     */
    Meta_collection_setsFactory createMetaCollectionSetsFactory(final RockFactory etlRock,
            final Meta_collection_sets whereMetaCollectionSets, final String orderByClause) throws SQLException,
            RockException {
        return new Meta_collection_setsFactory(etlRock, whereMetaCollectionSets, orderByClause);
    }

    /**
     * 
     * refactored out to get under unit test
     * 
     * @param collectionSetName
     * @param collectionName
     * @param ScheduleInfo
     * @return
     * @throws Exception
     */
    EngineThread createNewEngineThread(final String collectionSetName, final String collectionName,
            final String ScheduleInfo) throws Exception {
        return new EngineThread(url, userName, password, dbDriverName, collectionSetName, collectionName,
                this.pluginLoader, ScheduleInfo, log, eCom);
    }

    /**
     * Executes the initialized collection set / collection
     * 
     * @param url
     *          Database url
     * @param userName
     *          Database user
     * @param password
     *          Database users password
     * @param dbDriverName
     *          Database driver
     * @param collectionSetName
     *          the name of the transfer collection set
     * @param collectionName
     *          the name of the transfer collection
     * @param ScheduleInfo
     *          Informatin from the Scheduler
     * @exception RemoteException
     */
    public String executeAndWait(final String collectionSetName, final String collectionName, final String ScheduleInfo)
            throws RemoteException {
        try {

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            final SetListener list = new SetListener();

            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, collectionSetName,
                    collectionName, this.pluginLoader, ScheduleInfo, list, log, eCom);

            et.setName(collectionSetName + "_" + collectionName);

            // execute the set
            executeSet(et);

            //
            final String status = list.listen();

            return status;

        } catch (final SQLException se) {

            if (se.getMessage().startsWith(FactoryRes.CANNOT_GET_TABLE_DATA)) {
                log.info("No such set " + collectionSetName + " / " + collectionName);
                return SetListener.NOSET;
            } else {
                ExceptionHandler.handleException(se);
                throw new RemoteException("Could not start a Set", se);
            }
        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }

    }

    /**
     * Executes the initialized collection set / collection, and creates a
     * listener object to observe the execution. The listener id, returned by the
     * method, can be used to examine the execution's status and events with the
     * getStatusEventsWithId method.
     * 
     * @param collectionSetName
     * @param collectionName
     * @param ScheduleInfo
     * @return Set listener id
     * @throws RemoteException
     */
    public String executeWithSetListener(final String collectionSetName, final String collectionName,
            final String ScheduleInfo) throws RemoteException {
        try {

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            final SetListener setListener = new SetListener();

            // # Add listener to SetListenerManager
            final SetListenerManager m = SetListenerManager.instance();
            final long listenerId = m.addListener(setListener);

            log.finest("Creating enginethread");
            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, collectionSetName,
                    collectionName, this.pluginLoader, ScheduleInfo, setListener, log, eCom);

            et.setName(collectionSetName + "_" + collectionName);
            log.finest("executing set");
            // execute the set
            executeSet(et);
            log.finest("returning listenerId=" + String.valueOf(listenerId));
            return String.valueOf(listenerId);

        } catch (final SQLException se) {

            if (se.getMessage().startsWith(FactoryRes.CANNOT_GET_TABLE_DATA)) {
                log.info("No such set " + collectionSetName + " / " + collectionName);
                return SetListener.NOSET;
            } else {
                ExceptionHandler.handleException(se);
                throw new RemoteException("Could not start a Set", se);
            }
        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }

    }

    /**
     * Retrieves the status information and events from a set listener object. The
     * listener is identified by the statusListenerId parameter.
     * 
     * @param statusListenerId
     *          Status listener's id, returned by the executeWithSetListener
     *          method.
     * @param beginIndex
     *          The index of the first retrieved status event
     * @param count
     *          The number of status events to be retrieved
     * @return A set status transfer object, containing the observed state and the
     *         status events. The returned status events are selected by the
     *         parameters beginIndex and count. If the values of both beginIndex
     *         and count are -1, then the all status events are returned.
     * @throws RemoteException
     */
    public SetStatusTO getStatusEventsWithId(final String statusListenerId, final int beginIndex, final int count)
            throws RemoteException {
        SetStatusTO result = null;
        final SetListenerManager m = SetListenerManager.instance();
        try {
            log.finest("Trying to get setListeners with listenerId=" + statusListenerId);
            final SetListener sl = m.get(Long.parseLong(statusListenerId));
            log.finest("SetListener.status.size=" + sl.getAllStatusEvents().size());
            if (beginIndex == -1 && count == -1) {
                result = sl.getStatusAsTO();
            } else {
                result = sl.getStatusAsTO(beginIndex, count);
            }

        } catch (final NumberFormatException e) {
            log.warning("Failed to parse statusListenerId");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Writes the SQL Loader ctl file contents
     * 
     * @param Vector
     *          fileContents The ctl -file description.
     * @param String
     *          fileName The ctl file name.
     * @exception RemoteException
     */
    public void writeSQLLoadFile(final String fileContents, final String fileName) throws RemoteException {

        try {
            final File ctlFile = new File(fileName);
            final File parent = ctlFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            final FileOutputStream fileOutStream = new FileOutputStream(ctlFile);
            final PrintWriter printWriter = new PrintWriter(fileOutStream);

            printWriter.print(fileContents);

            printWriter.close();
            fileOutStream.close();
        } catch (final Exception e) {
            throw new RemoteException("Cannot write file: " + fileName, e);
        }

    }

    /**
     * Returns all available plugin names
     * 
     * @return String[] plugin names
     * @throws RemoteException
     */
    public String[] getPluginNames() throws RemoteException {
        return this.pluginLoader.getPluginNames();
    }

    /**
     * Returns the specified plugins methods
     * 
     * @param String
     *          pluginName the plugin that the methods are fetched from
     * @param boolean isGetSetMethods if true, only set method names are returned
     * @param boolean isGetGetMethods if true, only get method names are returned
     * @return String[] method names
     * @throws RemoteException
     */
    public String[] getPluginMethods(final String pluginName, final boolean isGetSetMethods,
            final boolean isGetGetMethods) throws RemoteException {
        try {
            return this.pluginLoader.getPluginMethodNames(pluginName, isGetGetMethods, isGetSetMethods);
        } catch (final Exception e) {
            throw new RemoteException("", e);
        }
    }

    /**
     * Returns the constructor parameters separated with ,
     * 
     * @param String
     *          pluginName The plugin to load
     * @return String
     */
    public String getPluginConstructorParameters(final String pluginName) throws RemoteException {
        try {
            return this.pluginLoader.getPluginConstructorParameters(pluginName);
        } catch (final Exception e) {
            throw new RemoteException("", e);
        }
    }

    // SS
    /**
     * Returns the constructor parameter info
     * 
     * @param String
     *          pluginName The plugin to load
     * @return String
     */
    public String getPluginConstructorParameterInfo(final String pluginName) throws RemoteException {
        try {
            final Class pluginClass = this.pluginLoader.loadClass(pluginName);
            final Class[] empty = null;
            final String methodName = GET_PARAM_INFO;
            final java.lang.reflect.Method m = pluginClass.getDeclaredMethod(methodName, empty);
            return (String) m.invoke(null, new Object[] {});
        } catch (final Exception e) {
            throw new RemoteException("", e);
        }
    }

    public void activateScheduler() throws RemoteException {

        try {

            final SchedulerAdmin admin = new SchedulerAdmin();
            admin.activate_silent();

        } catch (final Exception e) {
            throw new RemoteException("Could not activate Scheduler.", e);
        }

    }

    /**
     * Returns the method parameters separated with ,
     * 
     * @param String
     *          pluginName The plugin to load
     * @param String
     *          methodName The method that hold the parameters
     * @return String
     */
    public String getPluginMethodParameters(final String pluginName, final String methodName) throws RemoteException {
        try {
            return this.pluginLoader.getPluginMethodParameters(pluginName, methodName);
        } catch (final Exception e) {
            throw new RemoteException("", e);
        }
    }

    protected void getProperties() {
        try {

            // read properties

            String etlcServerPropertiesFile = System.getProperty("CONF_DIR");
            if (etlcServerPropertiesFile == null) {
                log.config("System property CONF_DIR not defined. Using default");
                etlcServerPropertiesFile = "/eniq/sw/conf";
            }
            if (!etlcServerPropertiesFile.endsWith(File.separator)) {
                etlcServerPropertiesFile += File.separator;
            }

            etlcServerPropertiesFile += "ETLCServer.properties";

            log.info("Reading server configuration from \"" + etlcServerPropertiesFile + "\"");

            final FileInputStream streamProperties = new FileInputStream(etlcServerPropertiesFile);
            final java.util.Properties appProps = new java.util.Properties();
            appProps.load(streamProperties);

            this.pluginPath = appProps.getProperty("PLUGIN_PATH");
            if (this.pluginPath == null) {
                log.config("PLUGIN_PATH not defined. Using default.");
                this.pluginPath = "/eniq/sw/etlc/plugins/";
            }

            this.serverHostName = appProps.getProperty("ENGINE_HOSTNAME", null);
            if (this.serverHostName == null) { // trying to determine hostname
                this.serverHostName = "localhost";

                try {
                    this.serverHostName = InetAddress.getLocalHost().getHostName();
                } catch (final java.net.UnknownHostException ex) {
                    log.log(Level.FINE, "getHostName failed", ex);
                }
            }

            this.serverPort = 1200;
            final String sporttmp = appProps.getProperty("ENGINE_PORT", "1200");
            try {
                this.serverPort = Integer.parseInt(sporttmp);
            } catch (final NumberFormatException nfe) {
                log.config("Value of property ENGINE_PORT \"" + sporttmp + "\" is invalid. Using default.");
            }

            this.serverRefName = appProps.getProperty("ENGINE_REFNAME", "TransferEngine");

            this.url = appProps.getProperty("ENGINE_DB_URL");

            log.config("Using repository database from \"" + this.url + "\"");

            this.userName = appProps.getProperty("ENGINE_DB_USERNAME");
            this.password = appProps.getProperty("ENGINE_DB_PASSWORD");
            this.dbDriverName = appProps.getProperty("ENGINE_DB_DRIVERNAME");

            // Priority Queue

            final String priorityQueuePollIntervall = appProps.getProperty("PRIORITY_QUEUE_POLL_INTERVALL");
            final String maxPriorityLevel = appProps.getProperty("MAXIMUM_PRIORITY_LEVEL");

            if (priorityQueuePollIntervall != null)
                this.priorityQueuePollIntervall = Long.parseLong(priorityQueuePollIntervall);

            if (maxPriorityLevel != null)
                this.maxPriorityLevel = Integer.parseInt(maxPriorityLevel);

            streamProperties.close();
        } catch (final Exception e) {
            ExceptionHandler.handleException(e, "Cannot read ETLCServer.properties(" + e.getMessage() + ")");
            System.exit(2);

        }
    }

    /**
     * Method to forcefully shutdown engine, priority queue and execution slots
     */
    public void forceShutdown() throws RemoteException {
        log.info("Shutting down...");
        System.exit(0);
    }

    /**
     * Method to graceful shutdown engine, priority queue and execution slots this
     * lets all the sets in priority queue and all the execution slots finnis
     * their jobs and then exits.
     */
    public void slowGracefulShutdown() throws RemoteException {

        try {
            log.info("Gracefully Shutting down priority queue.,");

            // calculate the number of slots that have sets running..
            final int activeSlots = getExecutionSlotProfileHandler().getActiveExecutionProfile()
                    .getNumberOfExecutionSlots()
                    - getExecutionSlotProfileHandler().getActiveExecutionProfile().getNumberOfFreeExecutionSlots();

            log.info(activeSlots + " slots are still running  \n"
                    + "thease slots will be allowed to finnish before shutdown");

            // put queue on hold so that no new sets are handed to execution
            // slots
            this.priorityQueue.hold();

            // execution profile is locked so nobody can change profile.
            getExecutionSlotProfileHandler().lockProfile();

            log.info("\nclosing execution slots\n");

            // wait until all slots are locked..
            while (!getExecutionSlotProfileHandler().getActiveExecutionProfile().areAllSlotsLockedOrFree()) {
                System.out.print(".");
                Thread.sleep(5000);
            }

            log.info("Shutting down...");
            System.exit(0);

        } catch (final Exception e) {
            throw new RemoteException("Could not start a graceful Shutdown", e);
        }

    }

    /**
     * Method to query server status
     */
    public List<String> status() throws RemoteException, Exception {

        final List<String> al = new ArrayList<String>();

        al.add("--- ETLC Server ---");

        final long now = System.currentTimeMillis();
        long up = now - startedAt;
        final long days = up / (1000 * 60 * 60 * 24);
        up -= (days * (1000 * 60 * 60 * 24));
        final long hours = up / (1000 * 60 * 60);
        al.add("Uptime: " + days + " days " + hours + " hours");

        al.add("");

        if (priorityQueue != null) {
            al.add("Priority Queue");
            String qstat = "  Status: ";
            if (priorityQueue.isQueueClosed())
                qstat += "closed";
            else
                qstat += "open";
            qstat += ",";
            if (priorityQueue.isActive())
                qstat += "active";
            else
                qstat += "on hold";
            al.add(qstat);
            al.add("  Size: " + priorityQueue.getNumberOfSetsInQueue());
            al.add("  Poll Period: " + priorityQueue.getPollIntervall());
            al.add("");
        } else {
            log.warning("Priority Queue disabled");
            al.add("Priority Queue disabled");
        }

        try {
            final ExecutionSlotProfile esp = getExecutionSlotProfileHandler().getActiveExecutionProfile();
            al.add("Execution Profile");
            al.add("  Current Profile: " + esp.name());
            al.add("  Execution Slots: " + esp.getNumberOfFreeExecutionSlots() + "/" + esp.getNumberOfExecutionSlots());
        } catch (final Exception e) {
          log.warning("Execution slots are not created\n" +e);
          al.add("Execution slots are not created");
        }
        al.add("Java VM");
        final Runtime rt = Runtime.getRuntime();
        al.add("  Available processors: " + rt.availableProcessors());
        al.add("  Free Memory: " + rt.freeMemory());
        al.add("  Total Memory: " + rt.totalMemory());
        al.add("  Max Memory: " + rt.maxMemory());
        al.add("");
        
        if(al.contains("Priority Queue disabled") || al.contains("Execution slots are not created")){
          throw new Exception("Engine initialization has not been completed yet");
        }
        return al;
    }
    
    /**
     * Method to query currentProfile
     */
    public String currentProfile() throws RemoteException {
      try {
        final ExecutionSlotProfile esp = getExecutionSlotProfileHandler().getActiveExecutionProfile();
        return esp.name();
      } catch (final Exception e) {
        return e.getMessage();
      }
    }
    

    public void lockExecutionprofile() throws RemoteException {
        getExecutionSlotProfileHandler().lockProfile();
    }

    public void unLockExecutionprofile() throws RemoteException {
        getExecutionSlotProfileHandler().unLockProfile();
    }

    /**
     * Method to graceful shutdown engine, priority queue and execution slots.
     * this lets all the execution slots finnis their jobs and then exits.
     */
    public void fastGracefulShutdown() throws RemoteException {

      log.info("Shutting down priority queue");
      
      if (null == getExecutionSlotProfileHandler()){
        log.info("Shut down of priority queue aborted.");
        throw new RemoteException("Could not start a graceful Shutdown - there is no SlotProfileHandler object available. Engine is possibly not fully started.");
      }
      
        try {

            // calculate the number of slots that have sets running..
            final int activeSlots = getExecutionSlotProfileHandler().getActiveExecutionProfile()
                    .getNumberOfExecutionSlots()
                    - getExecutionSlotProfileHandler().getActiveExecutionProfile().getNumberOfFreeExecutionSlots();

            log.info(activeSlots + " slots are still running  \n" + "thease slots will be stopped");

            // put queue on hold so that no new sets are handed to execution
            // slots
            this.priorityQueue.hold();

            // execution profile is locked so nobody can change profile.
            getExecutionSlotProfileHandler().lockProfile();

            // broadcast a command to all the sets to be ready for a shutdown.
            eCom.setCommand("shutdown");

            log.fine("\nclosing execution slots\n");

            boolean ok = false;
            while (!ok) {

                ok = true;
                final Iterator iter = getExecutionSlotProfileHandler().getActiveExecutionProfile()
                        .getAllRunningExecutionSlotSetTypes().iterator();

                while (iter.hasNext()) {

                    final String setType = (String) iter.next();
                    if (setType.equalsIgnoreCase("adapter")) {
                        ok = false;
                    }
                }

                System.out.print(".");
                Thread.sleep(5000);
            }

            log.info("Shutting down...");
            System.exit(0);

        } catch (final Exception e) {
            throw new RemoteException("Could not start a graceful Shutdown", e);
        }

    }

    /**
     * Set new active Execution profile
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#setActiveExecutionProfile(java.lang.String)
     */
    public boolean setActiveExecutionProfile(final String profileName) throws RemoteException {
        try {

            if (!isInitialized()) {
                log
                        .info("Could not change execution profile, engine is not yet initialized. Waiting engine to initialize itself before changing the profile...");
                while (!isInitialized()) {
                    Thread.sleep(5000);
                }
            }

            getExecutionSlotProfileHandler().resetProfiles();
            final boolean result = getExecutionSlotProfileHandler().setActiveProfile(profileName);
            getExecutionSlotProfileHandler().writeProfile();
            return result;

        } catch (final Exception e) {
            throw new RemoteException("Could not activate Execution Profile", e);
        }
    }

    /**
     * Set new active Execution profile
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#setActiveExecutionProfile(java.lang.String,
     *      java.lang.String)
     */
    public boolean setActiveExecutionProfile(final String profileName, final String messageText) throws RemoteException {
        try {

            if (!isInitialized()) {
                log
                        .info("Could not change execution profile, engine is not yet initialized. Waiting engine to initialize itself before changing the profile...");
                while (!isInitialized()) {
                    Thread.sleep(5000);
                }
            }

            getExecutionSlotProfileHandler().resetProfiles();
            final boolean result = getExecutionSlotProfileHandler().setActiveProfile(profileName, messageText);
            getExecutionSlotProfileHandler().writeProfile();
            return result;

        } catch (final Exception e) {
            throw new RemoteException("Could not activate Execution Profile", e);
        }
    }

    /**
     * Set new active Execution profile and wait untill it has changed complitely.
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#setActiveExecutionProfile(java.lang.String)
     */
    public boolean setAndWaitActiveExecutionProfile(final String profileName) throws RemoteException {
        try {

            getExecutionSlotProfileHandler().resetProfiles();
            final boolean result = getExecutionSlotProfileHandler().setActiveProfile(profileName);
            getExecutionSlotProfileHandler().writeProfile();

            // wait for clean execution slot.
            while (!getExecutionSlotProfileHandler().isProfileClean()) {
                Thread.sleep(500);
            }

            return result;

        } catch (final Exception e) {
            throw new RemoteException("Could not activate Execution Profile", e);
        }
    }

    public Set getAllActiveSetTypesInExecutionProfiles() throws RemoteException {

        try {
            return getExecutionSlotProfileHandler().getActiveExecutionProfile().getAllRunningExecutionSlotSetTypes();
        } catch (final Exception e) {
            throw new RemoteException("Could not retrieve active execution profiles execution slots set types", e);
        }
    }

    public Set getAllRunningExecutionSlotWorkers() throws RemoteException {

        try {
            return getExecutionSlotProfileHandler().getActiveExecutionProfile().getAllRunningExecutionSlotWorkers();
        } catch (final Exception e) {
            throw new RemoteException("Could not retrieve active execution profiles execution slots set types", e);
        }
    }

    /**
     * Set new active Execution profile
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#reloadExecutionProfiles()
     */
    public void reloadExecutionProfiles() throws RemoteException {
        try {
            getExecutionSlotProfileHandler().resetProfiles();
        } catch (final Exception e) {
            throw new RemoteException("Could not reload Execution Profiles", e);
        }

    }

    /**
     * Hold a Execution slot. This slot cant execute sets until it is restarted.
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#holdExecutionSlot(int)
     */
    public void holdExecutionSlot(final int ExecutionSlotNumber) throws RemoteException {
        try {
            getExecutionSlotProfileHandler().getActiveExecutionProfile().getExecutionSlot(ExecutionSlotNumber).hold();
        } catch (final Exception e) {
            throw new RemoteException("Could not hold a exeution slot", e);
        }
    }

    /**
     * Restart a held up Execution slot.
     */
    public void restartExecutionSlot(final int ExecutionSlotNumber) throws RemoteException {
        try {
            getExecutionSlotProfileHandler().getActiveExecutionProfile().getExecutionSlot(ExecutionSlotNumber)
                    .restart();
        } catch (final Exception e) {
            throw new RemoteException("Could not restart a execution slot", e);
        }
    }

    /**
     * sets priority queue in hold. No sets are executed untill restarted.
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#holdPriorityQueue()
     */
    public void holdPriorityQueue() throws RemoteException {
        try {
            priorityQueue.hold();
        } catch (final Exception e) {
            throw new RemoteException("Could not hold the Priority Queue", e);
        }

    }

    /**
     * restarts priority queue.
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#restartPriorityQueue()
     */
    public void restartPriorityQueue() throws RemoteException {
        try {
            priorityQueue.restart();
        } catch (final Exception e) {
            throw new RemoteException("Could not restart the Priority Queue", e);
        }

    }

    public void removeSetFromExecutionSlot(final Long techpackID, final Long setID) throws RemoteException {
        try {
            final Iterator iter = getExecutionSlotProfileHandler().getActiveExecutionProfile().getAllExecutionSlots();
            if (iter != null) {
                while (iter.hasNext()) {
                    final ExecutionSlot slot = (ExecutionSlot) iter.next();
                    final EngineThread set = slot.getRunningSet();
                    if (set != null && set.getSetID() == setID && set.getTechpackID() == techpackID) {

                        slot.stop();

                    }
                }
            }

        } catch (final Exception e) {
            throw new RemoteException("Could not remove set " + techpackID + "/" + setID + " from execution slots", e);
        }

    }

    public void clearExecutionSlot(final String slotName) throws RemoteException {
        try {
            final Iterator iter = getExecutionSlotProfileHandler().getActiveExecutionProfile()
                    .getAllRunningExecutionSlots();
            if (iter != null) {
                while (iter.hasNext()) {
                    final ExecutionSlot slot = (ExecutionSlot) iter.next();
                    if (slot.getName().equalsIgnoreCase("slotName")) {

                        slot.stop();

                    }
                }
            }

        } catch (final Exception e) {
            throw new RemoteException("Could not clear execution slot " + slotName, e);
        }

    }

    /**
     * 
     */
    public void removeSetFromPriorityQueue(final Long techpackID, final Long setID) throws RemoteException {
        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    if (set != null && set.getSetID() == setID && set.getTechpackID() == techpackID) {

                        this.priorityQueue.RemoveSet(set);

                    }

                }
            }
        } catch (final Exception e) {
            throw new RemoteException("Could not remove set " + techpackID + "/" + setID + " from priority queue", e);
        }

    }

    /**
     * 
     */
    public boolean removeSetFromPriorityQueue(final Long ID) throws RemoteException {
        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    if (set != null && set.getQueueID().longValue() == ID.longValue()) {

                        return this.priorityQueue.RemoveSet(set);

                    }

                }
            }
        } catch (final Exception e) {
            throw new RemoteException("Could not remove set " + ID + " from priority queue", e);
        }

        return false;
    }

    /**
     * 
     */
    public boolean changeSetPriorityInPriorityQueue(final Long ID, final long priority) throws RemoteException {
        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    if (set != null && set.getQueueID().longValue() == ID.longValue()) {

                        return this.priorityQueue.ChangeSetsPriority(set, priority);

                    }

                }
            }
        } catch (final Exception e) {
            throw new RemoteException("Could not change sets " + ID + " priority in priority queue", e);
        }

        return false;

    }

    /**
     * 
     */
    public void holdSetInPriorityQueue(final Long ID) throws RemoteException {
        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    if (set != null && set.getQueueID().longValue() == ID.longValue()) {

                        set.hold();
                        break;
                    }
                }
            }
        } catch (final Exception e) {
            throw new RemoteException("Could not hold set " + ID + " in priority queue", e);
        }

    }

    /**
     * 
     */
    public void activateSetInPriorityQueue(final Long ID) throws RemoteException {
        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    if (set != null && set.getQueueID().longValue() == ID.longValue()) {

                        set.restart();
                        break;
                    }

                }
            }
        } catch (final Exception e) {
            throw new RemoteException("Could not activate set " + ID + " in priority queue", e);
        }

    }

    /**
     * return true if given set (in given techpack) is active in exeution slots
     * (running)
     * 
     * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#isSetRunning(java.lang.Long,
     *      java.lang.Long)
     */
    public boolean isSetRunning(final Long techpackID, final Long setID) throws RemoteException {
        try {
            final Iterator iter = getExecutionSlotProfileHandler().getActiveExecutionProfile().getAllExecutionSlots();
            if (iter != null) {
                while (iter.hasNext()) {
                    final ExecutionSlot slot = (ExecutionSlot) iter.next();
                    final EngineThread set = slot.getRunningSet();
                    if (set != null && set.getSetID() == setID && set.getTechpackID() == techpackID) {
                        return true;
                    }
                }
            }

            return false;

        } catch (final Exception e) {
            throw new RemoteException("Error while checking set status", e);
        }

    }

    public void reloadTransformations() throws RemoteException {

        // Init Transformer Cache

        RockFactory rdwh = null;
        RockFactory rrep = null;

        try {

            String dwh_url = null;
            String dwh_usr = null;
            String dwh_pwd = null;
            String dwh_drv = null;

            String rep_url = null;
            String rep_usr = null;
            String rep_pwd = null;
            String rep_drv = null;

            // check connection to metadata
            RockFactory etlRock = null;

            etlRock = new RockFactory(url, userName, password, dbDriverName, "ETLEngInit", true);

            final Meta_databases selO = new Meta_databases(etlRock);
            selO.setType_name("USER");

            final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlRock, selO);

            final Vector dbs = mdbf.get();

            for (int i = 0; i < dbs.size(); i++) {

                final Meta_databases mdb = (Meta_databases) dbs.get(i);

                if (mdb.getConnection_name().equalsIgnoreCase("dwh")) {
                    dwh_url = mdb.getConnection_string();
                    dwh_usr = mdb.getUsername();
                    dwh_pwd = mdb.getPassword();
                    dwh_drv = mdb.getDriver_name();
                } else if (mdb.getConnection_name().equalsIgnoreCase("dwhrep")) {
                    rep_url = mdb.getConnection_string();
                    rep_usr = mdb.getUsername();
                    rep_pwd = mdb.getPassword();
                    rep_drv = mdb.getDriver_name();
                }

            }

            rdwh = new RockFactory(dwh_url, dwh_usr, dwh_pwd, dwh_drv, "ETLEngTCInit", true);
            rrep = new RockFactory(rep_url, rep_usr, rep_pwd, rep_drv, "ETLEngTCInit", true);

            final TransformerCache tc = new TransformerCache();
            tc.revalidate(rrep, rdwh);

        } catch (final Exception e) {
            throw new RemoteException("Error while revalidating transformer cache", e);
        } finally {
            try {
                rdwh.getConnection().close();
            } catch (final Exception e) {
            }
            try {
                rrep.getConnection().close();
            } catch (final Exception e) {
            }
        }

    }

    public void updateTransformation(final String tpName) throws RemoteException {

        // Init Transformer Cache

        RockFactory rdwh = null;
        RockFactory rrep = null;

        try {

            String dwh_url = null;
            String dwh_usr = null;
            String dwh_pwd = null;
            String dwh_drv = null;

            String rep_url = null;
            String rep_usr = null;
            String rep_pwd = null;
            String rep_drv = null;

            // check connection to metadata
            RockFactory etlRock = null;

            etlRock = new RockFactory(url, userName, password, dbDriverName, "ETLEngInit", true);

            final Meta_databases selO = new Meta_databases(etlRock);
            selO.setType_name("USER");

            final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlRock, selO);

            final Vector dbs = mdbf.get();

            for (int i = 0; i < dbs.size(); i++) {

                final Meta_databases mdb = (Meta_databases) dbs.get(i);

                if (mdb.getConnection_name().equalsIgnoreCase("dwh")) {
                    dwh_url = mdb.getConnection_string();
                    dwh_usr = mdb.getUsername();
                    dwh_pwd = mdb.getPassword();
                    dwh_drv = mdb.getDriver_name();
                } else if (mdb.getConnection_name().equalsIgnoreCase("dwhrep")) {
                    rep_url = mdb.getConnection_string();
                    rep_usr = mdb.getUsername();
                    rep_pwd = mdb.getPassword();
                    rep_drv = mdb.getDriver_name();
                }

            }

            rdwh = new RockFactory(dwh_url, dwh_usr, dwh_pwd, dwh_drv, "ETLEngTCInit", true);
            rrep = new RockFactory(rep_url, rep_usr, rep_pwd, rep_drv, "ETLEngTCInit", true);

            final TransformerCache tc = TransformerCache.getCache();
            tc.updateTransformer(tpName, rrep, rdwh);

        } catch (final Exception e) {
            throw new RemoteException("Error while updating transformer cache", e);
        } finally {
            try {
                rdwh.getConnection().close();
            } catch (final Exception e) {
            }
            try {
                rrep.getConnection().close();
            } catch (final Exception e) {
            }
        }

    }

    public void reloadDBLookups() throws RemoteException {

        reloadDBLookups(null);

    }

    public void reloadDBLookups(final String tablename) throws RemoteException {

        try {

            log.info("Refreshing DBLookups");

            DBLookupCache.getCache().refresh(tablename);

        } catch (final Exception e) {
            throw new RemoteException("Refreshing DBLookups failed", e);

        }

    }

    /**
     * Reloads ETLC config and refresh caches
     */
    public void reloadProperties() throws RemoteException {
        try {

            log.info("Reloading configuration");

            Vector loggerOutputlines = new Vector();

            boolean showDetailedLoggingStatus = false;
            try {
                final String logDebugValue = StaticProperties.getProperty("log.debug", "false");
                if (logDebugValue.equalsIgnoreCase("true") == true) {
                    showDetailedLoggingStatus = true;
                }
            } catch (final Exception e) {
                // Don't mind if the value is not found.
            }

            log.info("--- Logging status before reload ---");
            Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();
            while (loggerNames.hasMoreElements()) {
                final String currentLoggerName = (String) loggerNames.nextElement();
                final Logger currentLogger = LogManager.getLogManager().getLogger(currentLoggerName);
                if (currentLogger != null) {
                    final Level loggerLevel = currentLogger.getLevel();
                    if (loggerLevel != null) {
                        final Logger parentLogger = currentLogger.getParent();
                        if (parentLogger != null) {
                            loggerOutputlines.add(currentLogger.getName() + " = " + loggerLevel.getName()
                                    + " || parent = " + parentLogger.getName());
                        } else {
                            loggerOutputlines.add(currentLogger.getName() + " = " + loggerLevel.getName()
                                    + " || parent = null ");

                        }
                    } else {
                        if (showDetailedLoggingStatus == true) {
                            loggerOutputlines.add(currentLogger.getName() + " = null || parent = "
                                    + currentLogger.getParent().getName());

                        }
                    }
                } else {
                    loggerOutputlines.add(currentLoggerName + " was null.");
                }
            }

            // Sort the logger output lines and add them to the status output.
            Collections.sort(loggerOutputlines);
            Iterator loggerOutputlinesIterator = loggerOutputlines.iterator();
            while (loggerOutputlinesIterator.hasNext()) {
                final String currentLoggerOutputline = (String) loggerOutputlinesIterator.next();
                log.info(currentLoggerOutputline);
            }

            // Create static properties.
            new StaticProperties().reload();

            // reload properties
            Properties.reload();

            // Read properties
            getProperties();

            // Proprietary cache refresh
            DataFormatCache.getCache().revalidate();
            PhysicalTableCache.getCache().revalidate();
            ActivationCache.getCache().revalidate();
            AggregationRuleCache.getCache().revalidate();

            // Reload logging configurations
            reloadLogging();

            loggerOutputlines = new Vector();
            loggerNames = LogManager.getLogManager().getLoggerNames();

            showDetailedLoggingStatus = false;
            try {
                final String logDebugValue = StaticProperties.getProperty("log.debug", "false");
                if (logDebugValue.equalsIgnoreCase("true") == true) {
                    showDetailedLoggingStatus = true;
                }
            } catch (final Exception e) {
                // Don't mind if the value is not found.
            }

            log.info("--- Logging status after reload ---");
            while (loggerNames.hasMoreElements()) {
                final String currentLoggerName = (String) loggerNames.nextElement();
                final Logger currentLogger = LogManager.getLogManager().getLogger(currentLoggerName);
                if (currentLogger != null) {
                    final Level loggerLevel = currentLogger.getLevel();
                    if (loggerLevel != null) {
                        final Logger parentLogger = currentLogger.getParent();
                        if (parentLogger != null) {
                            loggerOutputlines.add(currentLogger.getName() + " = " + loggerLevel.getName()
                                    + " || parent = " + parentLogger.getName());
                        } else {
                            loggerOutputlines.add(currentLogger.getName() + " = " + loggerLevel.getName()
                                    + " || parent = null ");
                        }
                    } else {
                        if (showDetailedLoggingStatus == true) {
                            loggerOutputlines.add(currentLogger.getName() + " = null || parent = "
                                    + currentLogger.getParent().getName());
                        }
                    }
                } else {
                    loggerOutputlines.add(currentLoggerName + " was null.");
                }
            }

            // Sort the logger output lines and add them to the status output.
            Collections.sort(loggerOutputlines);
            loggerOutputlinesIterator = loggerOutputlines.iterator();
            while (loggerOutputlinesIterator.hasNext()) {
                final String currentLoggerOutputline = (String) loggerOutputlinesIterator.next();
                log.info(currentLoggerOutputline);
            }

            // reset prioriety queue
            priorityQueue.resetPriorityQueue(this.priorityQueuePollIntervall, this.maxPriorityLevel);

        } catch (final Exception e) {
            throw new RemoteException("Reload config failed", e);
        }

    }

    /**
     * Refreshes the aggregation cache.
     */
    public void reloadAggregationCache() throws RemoteException {
        try {
            log.info("Reloading aggregation cache");
            AggregationRuleCache.getCache().revalidate();
        } catch (final Exception e) {
            throw new RemoteException("Reload aggregation cache failed", e);
        }
    }

  /*
   * (non-Javadoc)
   * @see com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI#reloadAlarmCache()
   */
  @Override
  public void reloadAlarmConfigCache() throws RemoteException {
    log.info("Reloading alarm cache");
    try {
      final RockFactory dwhrepRock = getDwhRepRockFactory("ETLEngACInit");
      try {
        AlarmConfigCacheWrapper.revalidate(dwhrepRock);
      } finally {
        dwhrepRock.getConnection().close();
      }
      log.finest("Alarm cache reloaded");
    } catch (final Exception e) {
      log.severe("Alarm cache reload failed. " + e.getMessage());
      throw new RemoteException("Alarm cache reload failed", e);
    }
  }

    private String getTechpackName(final RockFactory rockFact, final String version, final Long techpackID)
            throws Exception {
        // get techpack name
        final Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
        whereCollSet.setEnabled_flag("Y");
        whereCollSet.setVersion_number(version);
        whereCollSet.setCollection_set_id(techpackID);
        final Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);
        final String techPackName = collSet.getCollection_set_name();

        return techPackName;
    }

    private String getSetName(final RockFactory rockFact, final String version, final Long techpackID, final Long setID)
            throws Exception {
        // get set name
        final Meta_collections whereColl = new Meta_collections(rockFact);
        whereColl.setVersion_number(version);
        whereColl.setCollection_set_id(techpackID);
        whereColl.setCollection_id(setID);
        final Meta_collections coll = new Meta_collections(rockFact, whereColl);
        final String setName = coll.getCollection_name();

        return setName;

    }

    private String getSetType(final RockFactory rockFact, final String version, final Long techpackID, final Long setID)
            throws Exception {
        // get set name
        final Meta_collections whereColl = new Meta_collections(rockFact);
        whereColl.setVersion_number(version);
        whereColl.setCollection_set_id(techpackID);
        whereColl.setCollection_id(setID);
        final Meta_collections coll = new Meta_collections(rockFact, whereColl);
        final String setType = coll.getSettype();

        return setType;

    }

    public void giveEngineCommand(final String command) {

        eCom.setCommand(command);

    }

    private String getFailureReason(final RockFactory rockFact, final Long batchID, final String version,
            final Long techpackID, final Long setID) throws Exception {

        final Meta_errors errColl = new Meta_errors(rockFact);
        errColl.setId(batchID);
        final Meta_errors err = new Meta_errors(rockFact, errColl);

        return err.getText();

    }

    /**
     * Returns the executed sets in the ETL engine.
     * 
     * @return
     */
    public List getExecutedSets() throws java.rmi.RemoteException {

        final List result = new ArrayList();
        RockFactory rockFact = null;
        try {

            rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLEngESets", true, 0);

            final Meta_transfer_batches trBSet = new Meta_transfer_batches(rockFact);
            final Meta_transfer_batchesFactory dbCollections = new Meta_transfer_batchesFactory(rockFact, trBSet);

            final Vector dbVec = dbCollections.get();
            for (int i = 0; i < dbVec.size(); i++) {

                final Meta_transfer_batches batch = (Meta_transfer_batches) dbVec.elementAt(i);
                final Map setMap = new HashMap();

                final String setName = getSetName(rockFact, batch.getVersion_number(), batch.getCollection_set_id(),
                        batch.getCollection_id());
                final String techPackName = getTechpackName(rockFact, batch.getVersion_number(), batch
                        .getCollection_set_id());
                final String setType = getSetType(rockFact, batch.getVersion_number(), batch.getCollection_set_id(),
                        batch.getCollection_id());

                String status;
                if (batch.getFail_flag().equalsIgnoreCase("y")) {
                    status = "ok";
                } else {
                    status = "failed";
                }

                setMap.put("techpackName", techPackName);
                setMap.put("setName", setName);
                setMap.put("setType", setType);
                setMap.put("startTime", batch.getStart_date().toString());
                setMap.put("endTime", batch.getEnd_date().toString());
                setMap.put("status", status);
                setMap.put("failureReason", "");
                setMap.put("priority", "");
                setMap.put("runningSlot", "");
                setMap.put("runningAction", "");
                setMap.put("version", batch.getVersion_number());

                result.add(setMap);

            }

            return result;

        } catch (final Exception e) {
            throw new RemoteException("Error while fetching executed sets from DB", e);

        } finally {

            try {

                if (rockFact != null && rockFact.getConnection() != null)
                    rockFact.getConnection().close();

            } catch (final Exception e) {

                throw new RemoteException("Error while closing DB connection", e);

            }
        }
    }

    /**
     * Returns the failed sets in the ETL engine.
     * 
     * @return
     */
    public List getFailedSets() throws java.rmi.RemoteException {

        final List result = new ArrayList();
        RockFactory rockFact = null;
        try {

            rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLEngFSets", true, 0);

            final Meta_transfer_batches trBSet = new Meta_transfer_batches(rockFact);
            trBSet.setFail_flag("Y");
            final Meta_transfer_batchesFactory dbCollections = new Meta_transfer_batchesFactory(rockFact, trBSet);

            final Vector dbVec = dbCollections.get();
            for (int i = 0; i < dbVec.size(); i++) {

                final Meta_transfer_batches batch = (Meta_transfer_batches) dbVec.elementAt(i);
                final Map setMap = new HashMap();

                final String setName = getSetName(rockFact, batch.getVersion_number(), batch.getCollection_set_id(),
                        batch.getCollection_id());
                final String techPackName = getTechpackName(rockFact, batch.getVersion_number(), batch
                        .getCollection_set_id());
                final String setType = getSetType(rockFact, batch.getVersion_number(), batch.getCollection_set_id(),
                        batch.getCollection_id());
                final String status = "failed";
                final String failureReason = getFailureReason(rockFact, batch.getId(), batch.getVersion_number(), batch
                        .getCollection_set_id(), batch.getCollection_id());

                setMap.put("techpackName", techPackName);
                setMap.put("setName", setName);
                setMap.put("setType", setType);
                setMap.put("startTime", batch.getStart_date().toString());
                setMap.put("endTime", batch.getEnd_date().toString());
                setMap.put("status", status);
                setMap.put("failureReason", failureReason);
                setMap.put("priority", "");
                setMap.put("runningSlot", "");
                setMap.put("runningAction", "");
                setMap.put("version", batch.getVersion_number());

                result.add(setMap);

            }

            return result;

        } catch (final Exception e) {
            throw new RemoteException("Error while fetching executed sets from DB", e);
        } finally {

            try {

                if (rockFact != null && rockFact.getConnection() != null)
                    rockFact.getConnection().close();

            } catch (final Exception e) {

                throw new RemoteException("Error while closing DB connection", e);

            }
        }

    }

    /**
     * Returns the queued sets in the ETL engine.
     * 
     * @return
     */
    public void addWorkerToQueue(final String name, final String type, final Object wobj)
            throws java.rmi.RemoteException {

        try {

            final EngineThread et = new EngineThread(name, type, new Long(100), wobj, log);

            et.setName(name);

            this.priorityQueue.addSet(et);

        } catch (final Exception e) {

            throw new RemoteException("Error while adding worker set to queue", e);

        }

    }

    /**
     * Returns the queued sets in the ETL engine.
     * 
     * @return
     */
    public List<Map<String, String>> getQueuedSets() throws java.rmi.RemoteException {

        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            final Enumeration enumer = this.priorityQueue.getSetsInQueue();

            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    final EngineThread set = (EngineThread) enumer.nextElement();
                    final Map<String, String> setMap = new HashMap<String, String>();

                    setMap.put("techpackName", set.getTechpackName());
                    setMap.put("setName", set.getSetName());
                    setMap.put("setType", set.getSetType());
                    setMap.put("startTime", "");
                    setMap.put("endTime", "");
                    setMap.put("status", "");
                    setMap.put("failureReason", "");
                    setMap.put("priority", set.getSetPriority().toString());
                    setMap.put("runningSlot", "");
                    setMap.put("runningAction", "");
                    setMap.put("version", set.getSetVersion());
                    setMap.put("ID", String.valueOf(set.getQueueID()));
                    setMap.put("creationDate", sdf.format(set.getCreationDate()));
                    setMap.put("active", "false");
                    if (set.isActive())
                        setMap.put("active", "true");

                    result.add(setMap);

                }
            }

            return result;
        } catch (final Exception e) {
            throw new RemoteException("Error while checking sets in Queue", e);
        }

    }

    /**
     * Returns the running sets in the ETL engine.
     * 
     * @return
     */
    public List<Map<String, String>> getRunningSets() throws java.rmi.RemoteException {

        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            final Iterator iter = getExecutionSlotProfileHandler().getActiveExecutionProfile().getAllExecutionSlots();

            if (iter != null) {
                while (iter.hasNext()) {
                    final ExecutionSlot slot = (ExecutionSlot) iter.next();
                    final EngineThread set = slot.getRunningSet();

                    if (set != null) {

                        final Map<String, String> setMap = new HashMap<String, String>();

                        setMap.put("techpackName", set.getTechpackName());
                        setMap.put("setName", set.getSetName());
                        setMap.put("setType", set.getSetType());
                        setMap.put("startTime", sdf.format(slot.getStartTime()));
                        setMap.put("endTime", "");
                        setMap.put("status", "");
                        setMap.put("failureReason", "");
                        setMap.put("priority", set.getSetPriority().toString());
                        setMap.put("runningSlot", slot.getName());
                        setMap.put("runningAction", set.getCurrentAction());
                        setMap.put("version", set.getSetVersion());
                        setMap.put("ID", String.valueOf(set.getQueueID()));
                        setMap.put("creationDate", sdf.format(set.getCreationDate()));

                        result.add(setMap);
                    }
                }
            }

            return result;
        } catch (final Exception e) {
            throw new RemoteException("Error while checking sets in Execution slots", e);
        }

    }

    /**
     * 
     * 
     * 
     */
    public void reaggregate(final String aggregation, final long datadate) throws RemoteException {

        RockFactory rockFact = null;
        try {

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLEngESets", true);

            try {

                // update
                final AggregationStatus aggSta = AggregationStatusCache.getStatus(aggregation, datadate);
                if (aggSta != null) {

                    log.fine("Update aggregation status to MANUAL: " + aggregation);
                    aggSta.STATUS = "MANUAL";
                    // Reset the threshold time value:
                    aggSta.THRESHOLD = 0;
                    aggSta.LOOPCOUNT = 0;
                    AggregationStatusCache.setStatus(aggSta);

                    final ReAggregation reag = new ReAggregation(log, rockFact);
                    reag.reAggregate("MANUAL", aggregation, datadate);

                }

            } catch (final Exception e) {
                throw new RemoteException("Error while setting reAggregation status " + aggregation + " "
                        + sdf.format(new Date(datadate)), e);

            }

        } catch (final Exception e) {
            throw new RemoteException("Error while fetching executed sets from DB", e);
        } finally {

            try {

                if (rockFact != null && rockFact.getConnection() != null)
                    rockFact.getConnection().close();

            } catch (final Exception e) {

                throw new RemoteException("Error while closing DB connection", e);

            }
        }

    }

    /**
     * 
     * Changes status of a aggregation in a specific date.
     * 
     * @param status
     *          new status
     * @param aggregation
     *          aggreagtion
     * @param datadate
     *          date of the aggregation
     * @throws RemoteException
     */
    public void changeAggregationStatus(final String status, final String aggregation, final long datadate)
            throws RemoteException {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        RockFactory rockFact = null;
        try {

            rockFact = new RockFactory(url, userName, password, dbDriverName, "ETLEngESets", true);

            try {

                // update
                final AggregationStatus aggSta = AggregationStatusCache.getStatus(aggregation, datadate);
                if (aggSta != null) {

                    log.fine("Update aggregation monitorings status to " + status + ": " + aggregation);
                    aggSta.STATUS = status;
                    // Reset the threshold time value:
                    aggSta.THRESHOLD = 0;
                    aggSta.LOOPCOUNT = 0;
                    AggregationStatusCache.setStatus(aggSta);

                    if (status.equalsIgnoreCase("MANUAL")) {

                        final ReAggregation reag = new ReAggregation(log, rockFact);
                        reag.reAggregate("MANUAL", aggregation, datadate);

                    }

                }

            } catch (final Exception e) {
                throw new RemoteException("Error while changing aggregation status to " + status + " :" + aggregation
                        + " " + sdf.format(new Date(datadate)), e);

            }
        } catch (final Exception e) {
            throw new RemoteException("Error while fetching executed sets from DB", e);
        } finally {

            try {

                if (rockFact != null && rockFact.getConnection() != null)
                    rockFact.getConnection().close();

            } catch (final Exception e) {

                throw new RemoteException("Error while closing DB connection", e);

            }
        }
    }

    /**
     * main interface for TransferEngine
     */
    public static void main(final String args[]) {

        System.setSecurityManager(new com.distocraft.dc5000.etl.engine.ETLCSecurityManager());
        try {

            final Logger log = Logger.getLogger("etlengine.Engine");

            log.info("---------- ETLC engine is initializing ----------");

            final TransferEngine tE = new TransferEngine(log);
            tE.getProperties();

            if (tE.init() == false) {
                log.severe("Initialisation failed... exiting");
                System.exit(3);
            }

            log.info("ETLC Engine Running...");

            while (true) {
                try {
                    synchronized (args) {
                        args.wait();
                    }
                } catch (final InterruptedException ie) {
                }
            }

        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
        }

    }

    /**
     * This function returns true if this instance of TransferEngine is already
     * initialized.
     * 
     * @return Returns true if initialized, otherwise returns false.
     */
    public boolean isInitialized() throws java.rmi.RemoteException {
        return (this.isInitialized);
    }

    /**
     * This function reloads the logging configuration from
     * CONF_DIR/engineLogging.properties.
     * 
     * @throws java.rmi.RemoteException
     */
    public void reloadLogging() throws java.rmi.RemoteException {

        // Relaod logging configurations
        LogManager.getLogManager().reset();

        try {
            LogManager.getLogManager().readConfiguration();
        } catch (final Exception e) {
            throw new java.rmi.RemoteException(
                    "IOException occurred: Reading or writing engineLogging.properties failed. Error message: "
                            + e.getMessage());
        }

    }

    public List loggingStatus() throws java.rmi.RemoteException {

        final Enumeration loggerNames = LogManager.getLogManager().getLoggerNames();

        final List al = new ArrayList();

        while (loggerNames.hasMoreElements()) {
            final String currentLoggerName = (String) loggerNames.nextElement();
            final Logger currentLogger = LogManager.getLogManager().getLogger(currentLoggerName);
            if (currentLogger != null) {
                final Level loggerLevel = currentLogger.getLevel();
                if (loggerLevel != null) {
                    final Logger parentLogger = currentLogger.getParent();
                    if (parentLogger != null) {
                        al.add(currentLogger.getName() + " = " + loggerLevel.getName() + " || parent = "
                                + parentLogger.getName());
                    } else {
                        al.add(currentLogger.getName() + " = " + loggerLevel.getName() + " || parent = null ");
                    }
                } else {
                    al.add(currentLogger.getName() + " = null || parent = " + currentLogger.getParent().getName());
                }
            } else {
                al.add(currentLoggerName + " was null.");
            }
        }

        Collections.sort(al);

        al.add(0, "");
        al.add(0, "--- Logging status ---");

        al.add("");

        return al;
    }

    private void getDWHDBCharsetEncoding(final RockFactory rdwh) throws Exception {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String charsetEncoding = "";
        final String query = "SELECT SUBSTRING(collation_name, 0, CHARINDEX(',', collation_name)-1) AS collation_name FROM sys.syscollation;";

        try {
            statement = rdwh.getConnection().prepareStatement(query);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                charsetEncoding = resultSet.getString("collation_name");
                final Share share = Share.instance();
                share.add("dwhdb_charset_encoding", charsetEncoding);
            }

        } catch (final Exception e) {
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Just a helper method to run private executeSet
     * 
     * @param et
     */
    public void executeEngineThreadWithListener(final EngineThread et) {
        try {
            this.executeSet(et);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /** Executes set with listener via set manager
     * @return - SetStatusTO data that contains status of executed set
     */
    public SetStatusTO executeSetViaSetManager(final String collectionSetName, final String collectionName,
            final String ScheduleInfo, final java.util.Properties props) throws RemoteException {
        log.finest("executeSetViaSetManager::colSetName=" + collectionSetName + " setName=" + collectionName);
        try {

            if (this.pluginLoader == null) {
                this.pluginLoader = new PluginLoader(this.pluginPath);
            }

            log.finest("Creating enginethread");

            final EngineThread et = new EngineThread(url, userName, password, dbDriverName, collectionSetName,
                    collectionName, this.pluginLoader, ScheduleInfo, SetListener.NULL, log, eCom);
            et.setName(collectionSetName + "_" + collectionName);
            final SetManager setManager = SetManager.getInstance();
            final SetStatusTO status = setManager.executeSet(collectionSetName, collectionName, props, et, this);

            return status;

        } catch (final SQLException se) {

            if (se.getMessage().startsWith(FactoryRes.CANNOT_GET_TABLE_DATA)) {
                log.info("No such set " + collectionSetName + " / " + collectionName);
                //        return SetListener.NOSET;
                return null;
            } else {
                ExceptionHandler.handleException(se);
                throw new RemoteException("Could not start a Set", se);
            }
        } catch (final Exception e) {
            ExceptionHandler.handleException(e);
            throw new RemoteException("Could not start a Set", e);
        }

    }

    /**
     * Returns status info from a set that is executed via setmanager.
     * 
     * @return - status object
     */
    public SetStatusTO getSetStatusViaSetManager(final String collectionSetName, final String collectionName,
            final int beginIndex, final int count) throws RemoteException {
        log.finest("getSetStatusViaSetManager::colSetName=" + collectionSetName + " setName=" + collectionName);
        final SetManager sm = SetManager.getInstance();
        return sm.getSetStatus(collectionSetName, collectionName, beginIndex, count);
    }

    /**
    * This function configures concurrent worker limitations
    */
    private void configureWorkerLimits(final RockFactory rrep) {
        final Share share = Share.instance();

        HashMap memoryUsageFactors = new HashMap();
        HashMap regexpsForWorkerLimitations = new HashMap();

        try {

            memoryUsageFactors = getMemoryUsageFactors(rrep);
            share.add("memory_usage_factors", memoryUsageFactors);

            regexpsForWorkerLimitations = getRegexpsForWorkerLimitations(rrep);
            share.add("regexps_for_worker_limitations", regexpsForWorkerLimitations);

            final int engineMemoryNeed = getEngineMemoryNeedMB(rrep);

            final Integer maxEngineMemory = (int) getHeapSizeInMB(System.getProperty("HEAP_SIZE"));
            share.add("execution_profile_max_memory_usage_mb", maxEngineMemory - engineMemoryNeed);

        } catch (final Exception e) {
            log.log(Level.WARNING, "Error in worker limitation configuration.", e);
            share.add("max_concurrent_workers", null);
            share.add("regexps_for_worker_limitations", null);
            share.add("execution_profile_max_memory_usage_mb", null);
            log.info("The worker limitation configurations are unset.");
        }

    }

    /**
     * This function parses Engine's heap size that is given in Megabytes or Gigabytes.
     * If the no M or G used then the heap size is read from Runtime's maxMemory method. 
     */
    private long getHeapSizeInMB(final String mxHeapSize) {
        long longHeapSize = Long.parseLong(mxHeapSize.substring(0, mxHeapSize.length() - 1));
        final char lastChar = mxHeapSize.charAt(mxHeapSize.length() - 1);

        // if Engine's heap size is given in Gigabytes then calculate it to Megabytes.
        if ('G' == lastChar || 'g' == lastChar) {
            // convert Gigabytes to Megabytes
            longHeapSize = longHeapSize * 1024L;
        } else {
            if ('M' != lastChar && 'm' != lastChar) {
                this.log.warning("Engine heap size is not given in megabytes or in gigabytes: " + mxHeapSize
                        + ". Will be using Runtime's maxMemory method's return value instead.");
                // maxMemory returns memory in bytes so convert it to Megabytes.
                longHeapSize = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
            }
        }

        this.log.info("HeapSizeInMegaBytes: " + longHeapSize);

        return longHeapSize;
    }

    /**
     * This function reads worker limitation regular expressions from Configuration table
     * 
     * @throws Exception
     */
    private HashMap getRegexpsForWorkerLimitations(final RockFactory rrep) throws Exception {
        this.log.info("Getting regexp configurations for worker limitations from Configuration table.");

        final String sqlStr = "SELECT PARAMNAME, PARAMVALUE FROM CONFIGURATION "
                + "WHERE PARAMNAME LIKE 'etlc.workerLimitationRegexp.%'" + " ORDER BY PARAMNAME";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        final List<Configuration> configuration = new ArrayList<Configuration>();
        try {
            statement = rrep.getConnection().prepareStatement(sqlStr);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                final Configuration conf = new Configuration(rrep);
                conf.setParamname(resultSet.getString("PARAMNAME"));
                conf.setParamvalue(resultSet.getString("PARAMVALUE"));
                configuration.add(conf);
            }

        } catch (final Exception e) {
            log.warning("An error occured on getting regexp configurations for worker limitations.");
            throw e;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (final Exception e) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (final Exception e) {
                }
            }
        }

        final HashMap regexps = new HashMap();
        final Iterator iterator = configuration.iterator();
        while (iterator.hasNext()) {
            final Configuration conf = (Configuration) iterator.next();
            final String paramName = conf.getParamname();
            final String shortName = paramName.substring(paramName.lastIndexOf("."));
            final String regexp = conf.getParamvalue();
            regexps.put(shortName, regexp);
            this.log.info("Key: " + shortName + " configured to have regexp: " + regexp + " for worker limitations.");
        }

        return regexps;
    }

    /**
     * This function reads worker limitation regular expressions from Configuration table
     * 
     * @throws Exception
     */
    private HashMap getMemoryUsageFactors(final RockFactory rrep) throws Exception {
        this.log.info("Getting memory usage factors from Configuration table.");

        final String sqlStr = "SELECT PARAMNAME, PARAMVALUE FROM CONFIGURATION "
                + "WHERE PARAMNAME LIKE 'etlc.MemoryUsageFactor.%'" + " ORDER BY PARAMNAME";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        final List<Configuration> configuration = new ArrayList<Configuration>();
        try {
            statement = rrep.getConnection().prepareStatement(sqlStr);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                final Configuration conf = new Configuration(rrep);
                conf.setParamname(resultSet.getString("PARAMNAME"));
                conf.setParamvalue(resultSet.getString("PARAMVALUE"));
                configuration.add(conf);
            }

        } catch (final Exception e) {
            log.warning("An error occured on getting memory usage configurations for worker limitations.");
            throw e;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (final Exception e) {
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (final Exception e) {
                }
            }
        }

        final HashMap memoryUsageFactors = new HashMap();

        final Iterator iterator = configuration.iterator();
        while (iterator.hasNext()) {
            final Configuration conf = (Configuration) iterator.next();
            final String paramName = conf.getParamname();
            final String shortName = paramName.substring(paramName.lastIndexOf("."));
            final String factor = conf.getParamvalue();
            memoryUsageFactors.put(shortName, Integer.valueOf(factor));
            this.log.info("Key: " + shortName + " configured to have memory usage factor: " + factor
                    + " for worker limitations.");
        }

        return memoryUsageFactors;
    }

    /**
     * This function reads Engine's memory need from Configuration table
     * 
     * @throws Exception
     */
    private int getEngineMemoryNeedMB(final RockFactory rrep) throws Exception {
        this.log.info("Getting Engine's memory need from Configuration table.");

        String engMemNeed = "512 + 5%";
        try {
            engMemNeed = StaticProperties.getProperty("EngineMemoryNeedMB", "512 + 5%");
        } catch (final Exception e) {
        }

        final Configuration confCond = new Configuration(rrep);
        confCond.setParamname("etlc.EngineMemoryNeedMB");

        final ConfigurationFactory confFactory = new ConfigurationFactory(rrep, confCond);
        final List configuration = confFactory.get();

        final Iterator iterator = configuration.iterator();
        if (iterator.hasNext()) {
            final Configuration conf = (Configuration) iterator.next();
            engMemNeed = conf.getParamvalue();
            log.info("Found Engine's memory need from Configuration table: " + engMemNeed);
        }

        int engineMemoryNeedMB = 512;
        if (engMemNeed.contains("+")) {
            try {
                final String[] memNeedParts = engMemNeed.split("\\+");
                final int staticPart = Integer.parseInt(memNeedParts[0].trim());
                final int dynamicAddition = Integer.parseInt(memNeedParts[1].substring(0, memNeedParts[1].indexOf('%'))
                        .trim());
                final double multiplier = (double) dynamicAddition / (double) 100;
                final Integer maxEngineMemory = (int) getHeapSizeInMB(System.getProperty("HEAP_SIZE"));
                engineMemoryNeedMB = (int) (staticPart + (multiplier * maxEngineMemory));
                log.info("Calculated memory configuration for engine: " + engineMemoryNeedMB + " from " + engMemNeed);
            } catch (final Exception e) {
                log
                        .warning("Unable to parse the memory configuration for engine. Using default: "
                                + engineMemoryNeedMB);
            }

        } else {
            if (engMemNeed != null)
                engineMemoryNeedMB = Integer.parseInt(engMemNeed);
        }

        log.info("Engine's memory need is set to: " + engineMemoryNeedMB);
        return engineMemoryNeedMB;
    }

    /**
     * This hack function sets database option DML_Options5 to configured value
     * 
     * @throws Exception
     */
    private void runDMLOptions5Setting(final RockFactory rdwhdba) throws Exception {
        PreparedStatement statement = null;

        final String DML_Options5 = StaticProperties.getProperty("sybaseiq.option.public.DML_Options5", null);

        if (null != DML_Options5) {

            log.info("Starting to set public database option DML_Options5 to value: " + DML_Options5);
            final String SQLClause = "SET OPTION PUBLIC.DML_Options5 = '" + DML_Options5 + "';";

            try {

                statement = rdwhdba.getConnection().prepareStatement(SQLClause);
                final int result = statement.executeUpdate();
                log.info("Public database option DML_Options5 is set to value: " + DML_Options5);

            } catch (final Exception e) {
                throw e;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        }
    }
    
    
    /**
     * This  function sets database option max_query_parallelism to configured value. If there is no configured value 
     * available it will calculate value from  formula: number of CPU cores / number of aggregator slots.
     * 
     * @throws Exception
     */
    private void runMax_query_parallelismSetting(RockFactory rdwhdba) throws Exception {
      
      PreparedStatement statement = null;
      String max_query_parallelism = null;
      max_query_parallelism = StaticProperties.getProperty("sybaseiq.option.public.max_query_parallelism", null);
      int numOfAggSlots = this.getExecutionSlotProfileHandler().getNumberOfAggregatorSlots();
      
      if (numOfAggSlots == 0) {
      log.info("num of aggregators slots is zero,max_query_parallelism value is not set");  
      return;
      }
      
      if (null == max_query_parallelism) {

          max_query_parallelism = Double.toString(new Double (new Double(this.getNumberOfCPUCores()) / new Double(numOfAggSlots)) );
          
        }
            
        log.info("Starting to set public database option max_query_parallelism to value: " + max_query_parallelism);
        final String SQLClause = "SET OPTION PUBLIC.max_query_parallelism = '" + max_query_parallelism + "';";

        try {
          Connection conn = rdwhdba.getConnection();
          statement = conn.prepareStatement(SQLClause);
            final int result = statement.executeUpdate();
            log.info("Public database option max_query_parallelism is set to value: \"" + max_query_parallelism + "\"");

        } catch (final Exception e) {
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    
  }

    /**
     * This method will print the execution slot information
     */
    //@Override
    public List<String> slotInfo() throws RemoteException {
        final List<String> info = new ArrayList<String>();
        try {
            getExecutionSlotProfileHandler().getActiveExecutionProfile().getSlotPrintout(info);
        } catch (final Exception e) {
            info.add("Error stopped getting total info, while trying to get slot info");
        }
        return info;
    }

    public void disableTechpack(final String techpackName) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryDisableTechpack = " update meta_schedulings " + " set ms.hold_flag = 'Y' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_schedulings ms"
                + " where mcs.collection_set_name = '" + techpackName + "'" + " and mcs.version_number = '"
                + techpackVersion + "'" + " and mcs.collection_set_id = mc.collection_set_id "
                + " and mc.collection_set_id = ms.collection_set_id " + " and mc.collection_id = ms.collection_id ";

        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtDisableTP;

        try {
            stmtDisableTP = etlConnection.createStatement();
            stmtDisableTP.executeUpdate(qryDisableTechpack);

            stmtDisableTP.close();
            etlConnection.close();

        } catch (final SQLException e) {
            log.warning("Cannot Disable Techpack");
            log.warning(e.getMessage());
        }
    }

    public void disableSet(final String techpackName, final String setName) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryDisableSet = " update meta_schedulings " + " set ms.hold_flag = 'Y' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_schedulings ms "
                + " where mcs.collection_set_name = '" + techpackName + "'" + " and mcs.version_number = '"
                + techpackVersion + "'" + " and mc.Collection_name = '" + setName + "'"
                + " and mcs.collection_set_id = mc.collection_set_id "
                + " and mc.collection_set_id = ms.collection_set_id " + " and mc.collection_id = ms.collection_id ";

        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtDisableSet;
        try {
            stmtDisableSet = etlConnection.createStatement();
            stmtDisableSet.executeUpdate(qryDisableSet);

            stmtDisableSet.close();
            etlConnection.close();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void disableAction(final String techpackName, final String setName, final Integer actionNumber) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryDisableAction = " update meta_transfer_actions " + " set mta.enabled_flag = 'N' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_transfer_actions mta "
                + " where mcs.collection_set_name = '" + techpackName + "' " + " and mcs.version_number = '"
                + techpackVersion + "'" + " and mc.Collection_name = '" + setName + "'"
                + " and mcs.collection_set_id = mc.collection_set_id"
                + " and mc.collection_set_id = mta.collection_set_id" + " and mc.collection_id = mta.collection_id"
                + " and mta.order_by_no = " + actionNumber + " order by mta.order_by_no asc";
        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtDisableAction;

        try {
            stmtDisableAction = etlConnection.createStatement();
            stmtDisableAction.executeUpdate(qryDisableAction);

            stmtDisableAction.close();
            etlConnection.close();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void enableTechpack(final String techpackName) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryEnableTechpack = " update meta_schedulings " + " set ms.hold_flag = 'N' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_schedulings ms "
                + " where mcs.collection_set_name = '" + techpackName + "' " + " and mcs.version_number = '"
                + techpackVersion + "' " + " and mcs.collection_set_id = mc.collection_set_id "
                + " and mc.collection_set_id = ms.collection_set_id " + " and mc.collection_id = ms.collection_id ";

        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtEnableTP;

        try {
            stmtEnableTP = etlConnection.createStatement();
            stmtEnableTP.executeUpdate(qryEnableTechpack);

            stmtEnableTP.close();
            etlConnection.close();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void enableSet(final String techpackName, final String setName) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryEnableSet = " update meta_schedulings " + " set ms.hold_flag = 'N' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_schedulings ms "
                + " where mcs.collection_set_name = '" + techpackName + "' " + " and mcs.version_number = '"
                + techpackVersion + "' " + " and mc.Collection_name = '" + setName + "'"
                + " and mcs.collection_set_id = mc.collection_set_id "
                + " and mc.collection_set_id = ms.collection_set_id " + " and mc.collection_id = ms.collection_id ";

        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtEnableSet;

        try {
            stmtEnableSet = etlConnection.createStatement();
            stmtEnableSet.executeUpdate(qryEnableSet);

            stmtEnableSet.close();
            etlConnection.close();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void enableAction(final String techpackName, final String setName, final Integer actionNumber) {
        final String techpackVersion = getTechpackVersion(techpackName);

        final String qryEnableAction = " update meta_transfer_actions " + " set mta.enabled_flag = 'Y' "
                + " from meta_collections mc, meta_collection_sets mcs, meta_transfer_actions mta "
                + " where mcs.collection_set_name = '" + techpackName + "' " + " and mcs.version_number = '"
                + techpackVersion + "'" + " and mc.Collection_name = '" + setName + "'"
                + " and mcs.collection_set_id = mc.collection_set_id"
                + " and mc.collection_set_id = mta.collection_set_id" + " and mc.collection_id = mta.collection_id"
                + " and mta.order_by_no = " + actionNumber + " order by mta.order_by_no asc";
        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();

        try {
            etlConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Probelem with connection to etl rep database");
            log.fine(e.getMessage());
        }

        Statement stmtEnableAction;

        try {
            stmtEnableAction = etlConnection.createStatement();
            stmtEnableAction.executeUpdate(qryEnableAction);

            stmtEnableAction.close();
            etlConnection.close();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * @author esunbal
     * CLI option engine -e showActiveInterfaces
     */
    public ArrayList showActiveInterfaces() {
        Connection etlConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> interfaceNames = null;
        final String query = "select collection_set_name from etlrep.meta_collection_sets,etlrep.meta_schedulings " +
            "where etlrep.meta_collection_sets.collection_set_id = etlrep.meta_schedulings.collection_set_id " +
            "and etlrep.meta_collection_sets.enabled_flag = 'y' and etlrep.meta_schedulings.hold_flag = 'N' " +
            "and etlrep.meta_schedulings.name like 'TriggerAdapter%' " +
            "and etlrep.meta_collection_sets.collection_set_name like '%-%' " +
            "and etlrep.meta_collection_sets.type = 'interface' group by collection_set_name, type";
        try {

            etlConnection = etlreprock.getConnection();
            if (etlConnection == null) {
                log.info("The etl rep could not be contacted");
            }
            statement = etlConnection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet == null){
              log.info("No active interfaces found.");              
            }
            else{
              interfaceNames = new ArrayList<String>();
                while (resultSet.next()) {
                  interfaceNames.add(resultSet.getString("collection_set_name"));
                }
            }

        }
        catch (final Exception e) {
            log.info("Could not connect to etl rep database");
            log.fine(e.getMessage());
        }
        
        return interfaceNames;        
    }

    public ArrayList showDisabledSets() {

        Connection etlConnection = null;
        //Connection dwhrepConnection = null;
        final RockFactory etlreprock = getEtlRepRockFactory();
        //RockFactory dwhreprock = getDwhRepRockFactory();

        ArrayList<String> alTechpackName = null;
        ArrayList<String> alTechpackVersion = null;
        ArrayList<String> alHeldSets = null;
        ArrayList<String> alDisabledActions = null;
        final ArrayList<String> alSetsActions = new ArrayList<String>();

        Statement stmtGetAllTechpacks = null;
        ResultSet rsGetAllTechpacks = null;

        // used to find all the techpacks and their versions
        final String qryGetAllTechpacks = "select collection_set_name, version_number" + " from meta_collection_sets"
                + " where enabled_flag = 'Y' " + " order by collection_set_name";

        try {
            // getting the techpacks and the versions.

            etlConnection = etlreprock.getConnection();
            if (etlConnection == null) {
                log.info("The etl rep could not be contacted");
            }
            stmtGetAllTechpacks = etlConnection.createStatement();
            rsGetAllTechpacks = stmtGetAllTechpacks.executeQuery(qryGetAllTechpacks);

        }

        catch (final Exception e) {
            log.info("Could not connect to etl rep database");
            log.fine(e.getMessage());
        }

        try {
            etlConnection = etlreprock.getConnection();

        } catch (final Exception e) {
            log.info("Could not connect to etl rep database");
            log.fine(e.getMessage());
        }

        try {
            if (rsGetAllTechpacks == null) {
                log.warning("No techpacks found");
            }

            else {
                Statement stmtGetSets = null;
                ResultSet rsGetSets = null;

                alTechpackName = new ArrayList<String>();
                alTechpackVersion = new ArrayList<String>();
                alHeldSets = new ArrayList<String>();
                alDisabledActions = new ArrayList<String>();

                try {

                    // finding the techpack names and versions
                    while (rsGetAllTechpacks.next()) {
                        alTechpackName.add(rsGetAllTechpacks.getString("collection_set_name"));
                        alTechpackVersion.add(rsGetAllTechpacks.getString("version_number"));
                    }
                } catch (final Exception e) {
                    log.info("Problem with retrieving information from the versioning table");
                    log.fine(e.getMessage());
                }

                // getting the list of techpack's sets on hold
                for (int i = 0; i < alTechpackName.size(); i++) {
                    final String qryGetSchedulesOnHold = " select mc.collection_name, ms.hold_flag"
                            + " from meta_collections mc, meta_collection_sets mcs, meta_schedulings ms"
                            + " where mcs.collection_set_name = '" + alTechpackName.get(i) + "'"
                            + " and mcs.version_number = '" + alTechpackVersion.get(i) + "'"
                            + " and mcs.collection_set_id = mc.collection_set_id "
                            + " and mc.collection_set_id = ms.collection_set_id "
                            + " and mc.collection_id = ms.collection_id " + " and ms.hold_flag = 'Y'";

                    // used to find the disabled actions for the techpacks
                    final String qryGetDisabledActions = " select mc.collection_name, mta.transfer_action_name, mta.order_by_no "
                            + " from meta_collections mc, meta_collection_sets mcs, meta_transfer_actions mta "
                            + " where mcs.collection_set_name = '"
                            + alTechpackName.get(i)
                            + "' "
                            + " and mcs.version_number = '"
                            + alTechpackVersion.get(i)
                            + "' "
                            + " and mcs.collection_set_id = mc.collection_set_id "
                            + " and mc.collection_set_id = mta.collection_set_id "
                            + " and mc.collection_id = mta.collection_id "
                            + " and mta.enabled_flag = 'N' "
                            + " order by mta.collection_id, mta.order_by_no asc ";

                    stmtGetSets = etlConnection.createStatement();
                    rsGetSets = stmtGetSets.executeQuery(qryGetSchedulesOnHold);

                    // if there are sets then add them to the array list 
                    try {
                        while (rsGetSets.next()) {
                            alHeldSets.add("\t" + alTechpackName.get(i) + " - "
                                    + rsGetSets.getString("collection_name"));
                            log.fine("Disable Set: " + alTechpackName.get(i) + " - "
                                    + rsGetSets.getString("collection_name"));
                        }

                    } catch (final Exception e) {
                        log.info("Could not retieve information about the disabled sets");
                        log.fine(e.getMessage());
                    }

                    rsGetSets.close();
                    stmtGetSets.close();

                    final Statement stmtGetActions = etlConnection.createStatement();
                    final ResultSet rsGetActions = stmtGetActions.executeQuery(qryGetDisabledActions);

                    try {
                        while (rsGetActions.next()) {
                            alDisabledActions.add("\t" + alTechpackName.get(i) + " - "
                                    + rsGetActions.getString("collection_name") + " - "
                                    + rsGetActions.getString("transfer_action_name") + " - "
                                    + rsGetActions.getString("order_by_no"));
                            log.info("Disabled Action: " + alTechpackName.get(i) + " - "
                                    + rsGetActions.getString("collection_name") + " - "
                                    + rsGetActions.getString("transfer_action_name") + " - "
                                    + rsGetActions.getString("order_by_no"));
                        }

                    } catch (final Exception e) {
                        log.info("Could not retrieve infomation about the disabled actions");
                        log.fine(e.getMessage());
                    }

                    rsGetActions.close();
                    stmtGetActions.close();
                }
            }

            etlConnection.close();

            // extracts all the sets on hold from the array list
            if (alHeldSets != null) {
                if (alHeldSets.size() == 0) {
                    alSetsActions.add("\n\nNo Sets Disabled");
                } else {
                    // adding output for the sets on hold to the array list
                    alSetsActions.add("\n\nDisabled Sets");

                    for (int i = 0; i < alHeldSets.size(); i++) {
                        alSetsActions.add(alHeldSets.get(i));
                    }
                }

            }

            if (alDisabledActions != null) {
                if (alDisabledActions.size() == 0) {
                    alSetsActions.add("\n\nNo Actions Disabled");
                    alSetsActions.add("\n\n");
                } else {
                    alSetsActions.add("\n\nDisabledActions ");
                    // extracts all the disabled actions in the array list
                    for (int i = 0; i < alDisabledActions.size(); i++) {
                        alSetsActions.add(alDisabledActions.get(i));
                    }
                }

            }

            return alSetsActions;

        }

        catch (final Exception e) {
            log.info(e.getMessage());
        }

        return null;
    }

    private String getTechpackVersion(final String techpackName) {
        Connection etlrepConnection = null;
        String techpackVersion = null;
        try {
            final RockFactory etlreprock = getEtlRepRockFactory();
            etlrepConnection = etlreprock.getConnection();
        } catch (final Exception e) {
            log.info("Cannot connect to ETL Rep database");
            log.info(e.getMessage());
        }

        Statement stmtGetTechpackVersion = null;
        ResultSet rsGetTechpackVersion = null;

        final String qryGetTechpackVersion = "select version_number" + " from meta_collection_sets"
                + " where enabled_flag = 'Y' " + " and collection_set_name = '" + techpackName + "'";

        try {
            stmtGetTechpackVersion = etlrepConnection.createStatement();
            rsGetTechpackVersion = stmtGetTechpackVersion.executeQuery(qryGetTechpackVersion);

            // 
            if (rsGetTechpackVersion != null) {
                try {
                    while (rsGetTechpackVersion.next()) {
                        techpackVersion = rsGetTechpackVersion.getString("version_number");
                        //techpackVersion = rsGetTechpackVersion.getString("techpack_version");
                    }
                } catch (final Exception e) {
                    log.info("Could not retieve information about the techpack version");
                    log.info(e.getMessage());
                }

            }
            rsGetTechpackVersion.close();
            stmtGetTechpackVersion.close();
            etlrepConnection.close();

            return techpackVersion;
        } catch (final Exception e) {
            log.info("Could not run query to retrieve techpack version");
            log.info(e.getMessage());

        }
        return techpackVersion;
    }

    RockFactory getDwhRepRockFactory(String connName) throws SQLException {
        RockFactory etlRock = getEtlRepRockFactory();

        try {

            final Meta_databases selO = new Meta_databases(etlRock);
            final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlRock, selO);
            final Vector dbs = mdbf.get();
            final String username = "USER";
            String rep_url = null;
            String rep_usr = null;
            String rep_pwd = null;
            String rep_drv = null;

            for (int i = 0; i < dbs.size(); i++) {
                final Meta_databases mdb = (Meta_databases) dbs.get(i);
                if (mdb.getConnection_name().equalsIgnoreCase("dwhrep")
                        && mdb.getType_name().equalsIgnoreCase(username)) {
                    rep_url = mdb.getConnection_string();
                    rep_usr = mdb.getUsername();
                    rep_pwd = mdb.getPassword();
                    rep_drv = mdb.getDriver_name();
                }
            }

            return new RockFactory(rep_url, rep_usr, rep_pwd, rep_drv, connName, true);

        } catch (final SQLException e) {
            e.printStackTrace();
        } catch (final RockException e) {
            e.printStackTrace();
        } finally {
          etlRock.getConnection().close();
        }
        return null;
    }

    RockFactory getEtlRepRockFactory() {
        RockFactory etlRock;
        getProperties();

        try {
            etlRock = new RockFactory(url, userName, password, dbDriverName, "ETLEngInit", true);
            return etlRock;
        } catch (final SQLException e) {

            log.info("ETL Repository could not be contacted to retrieve database information");
            log.fine(e.getMessage());
        } catch (final RockException e) {

            e.printStackTrace();
        }

        return null;
    }
           
  public String getActiveStarterlicense() {
    return activeStarterlicense;
  }

  public void setActiveStarterlicense(String activeStarterlicense) {
    this.activeStarterlicense = activeStarterlicense;
  }
    
  /**
   * @author esunbal
   * TimerTask implementation to check the ENIQ starter license everyday at 4 a.m. and if the license expired initiate
   * a graceful shutdown of the engine.
   */
  class EngineLicenseCheckTask extends TimerTask {

    // Time expressed in milliseconds
    private final static long oncePerDay = 1000*60*60*24;

    private final static int oneDay = 1;
    private final static int fourAM = 4;
    private final static int zeroMinutes = 0;

    private Date getTomorrowMorning4am(){
      Calendar tomorrow = new GregorianCalendar();
      tomorrow.add(Calendar.DATE, oneDay);
      Calendar result = new GregorianCalendar(
          tomorrow.get(Calendar.YEAR),
          tomorrow.get(Calendar.MONTH),
          tomorrow.get(Calendar.DATE),
          fourAM,
          zeroMinutes
      );
      return result.getTime();
    }


    @Override
    /*
     * Main logic for the timer task.
     */
    public void run() {
      try {
        LicensingCache cache = retrieveLicensingCache();

        if (cache == null) {
          log.log(Level.SEVERE, "Could not verify Eniq Starter license from LicenseManager host " + serverHostName);
        } else {
          final boolean licenseCheck = doLicenseCheck(cache);
          log.log(Level.INFO, "License check passed: " + licenseCheck);
        }
      } catch (Exception e) {
        log.log(Level.SEVERE, "Error in the run method of the EngineLicenseCheckTask:" + e.getMessage());
        e.printStackTrace();
      }
    }

    /**
     * Does a licensing check every morning at 4 o'clock.
     * @param cache       The licensing cache.
     * @return licenseOk  True if the license check passes.
     * @throws RemoteException
     */
    protected boolean doLicenseCheck(LicensingCache cache) throws RemoteException {
      boolean licenseOk = false;

      // Check Eniq starter license:
      LicenseDescriptor license = new DefaultLicenseDescriptor(activeStarterlicense);

      LicensingResponse response = null;
      if (ENIQ_12_STARTER_LICENSE.equals(activeStarterlicense)) {
        // get a licensing response for the created descriptors.
        response = cache.checkCapacityLicense(license, numberOfPhysicalCPUs);

        if (response.isValid() == true) {
          log.log(Level.INFO,
              "The Eniq Starter license is valid: " + response.isValid() + " msg: " + response.getMessage());
          licenseOk = true;
        } else {
          licenseOk = false;
        }
      } else {
        log.log(Level.SEVERE, "Eniq starter license not recognised: " + activeStarterlicense);
        licenseOk = false;
      }

      if (!licenseOk) {
        log.log(Level.INFO,
            "The Eniq Starter license is not valid:  Shutting down the engine gracefully."); 
        if (response != null) {
          log.log(Level.INFO,
          "License response: " + response.isValid()+ " msg: " + response.getMessage());    
        }                
        slowGracefulShutdown();
      }
      return licenseOk;
    }
  }

  /**
   * Gets the number of physical CPUs. 
   * @return numberOfPhysicalCPUs
   */
  public int getNumberOfCPU() {
    return this.numberOfPhysicalCPUs;     
  }

}
