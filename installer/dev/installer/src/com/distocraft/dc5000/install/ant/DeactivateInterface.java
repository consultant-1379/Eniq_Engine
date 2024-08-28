package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;

/**
 * This is custom made ANT task that deactivates an interface and copies
 * interface's set to the deactivated interface.
 * 
 * @author Berggren
 * 
 */
public class DeactivateInterface extends Task {

  private String deactivatedInterfaceName = "";

  private String deactivatedInterfaceVersion = "";

  private String ossName = "";

  private RockFactory dwhrepRockFactory = null;

  private RockFactory etlrepRockFactory = null;

  private String configurationDirectory = "";

  protected String propertiesFilepath = "";

  private String binDirectory = "";

  private String onlyDeactivateInterface = "";

  private Integer exitValue;

  /**
   * This function starts the interface deactivation.
   */
  public void execute() throws BuildException {
    try {

      if (!this.configurationDirectory.endsWith(File.separator)) {
        // Add the missing separator char "/" from the end of the directory
        // string.
        this.configurationDirectory = this.configurationDirectory + File.separator;
      }

      this.propertiesFilepath = this.configurationDirectory + "ETLCServer.properties";
      final HashMap databaseConnectionDetails = getDatabaseConnectionDetails();

      // Run the reloadConfig before executing DWHM_Install set of the tech
      // pack.
      final String reloadConfigCommand = new String(this.binDirectory + "/" + "engine -e reloadConfig");
      final String reloadSchedulerCommand = new String(this.binDirectory + "/" + "scheduler activate");

      // Create the connection to the etlrep.
      this.etlrepRockFactory = createEtlrepRockFactory(databaseConnectionDetails);
      // Create also the connection to dwhrep.
      this.createDwhrepRockFactory();

      // Check if the interface is already deactivated.
      if (this.interfaceAlreadyDeactivated()) {
    	  
    	  System.out.println("Interface " + this.deactivatedInterfaceName + " with OSS " + this.ossName
    	            + " is already deactivated. ");
      }else {
        
        // Remove the existing sets
    	  //System.out.println("Removing Interface Sets...");
        removeIntfSets();

        // Activate the scheduler again so that the removed sche.....
        System.out.println("Running scheduler activation");
        System.out.println(this.runCommand(reloadSchedulerCommand));

        if (this.getExitValue().intValue() != 0) {
          throw new BuildException("Cannot run activate for the scheduler. Aborting interface deactivation.");
        }

      }

      //System.out.println("Starting deactivation of interface " + this.deactivatedInterfaceName);
      // Deactivate the interface.
      //this.deactivateInterface();

      //System.out.println("Interface " + this.deactivatedInterfaceName + " deactivated");
/*
      if (this.onlyDeactivateInterface.equalsIgnoreCase("true")) {
        System.out.println("Only deactivation of interface selected. No sets will be copied.");
      } else {
        // Copy the tech pack set.
        System.out.println("Starting copying of interface set " + this.deactivatedInterfaceName);
        //---> final boolean copyIntfSetResult = this.copyInterfaceSet();
        //
        //if (copyIntfSetResult) {
        //  System.out.println("Interface set " + this.deactivatedInterfaceName + " copied");
        //} else {
        //System.out.println("Interface set " + this.deactivatedInterfaceName + " was not copied");
        //}
        //<----

        try {
          // Use the custom ANT task for copying the logging level if not
          // explicitly set already.
          final UpdateProperties updProp = new UpdateProperties();
          System.out.println("Updating logging properties file " + configurationDirectory + "engineLogging.properties");
          updProp.setPropertiesFile(configurationDirectory + "engineLogging.properties");
          updProp.setAction("copy");
          updProp.setKey(".level");
          updProp.setTargetKey("etl." + this.deactivatedInterfaceName + "-" + this.ossName + ".level");
          updProp.execute();

        } catch (Exception e) {
          System.out.println("Updating engineLogging.properties failed with error message:");
          System.out.println(e.getMessage());
          throw new BuildException(e);
        }

        System.out.println("Running reloadConfig for the engine");
        System.out.println(this.runCommand(reloadConfigCommand));

        if (this.getExitValue().intValue() != 0) {
          throw new BuildException("Cannot run reloadConfig for the engine. Aborting interface deactivation.");
        }

        System.out.println("Running scheduler activation");
        System.out.println(this.runCommand(reloadSchedulerCommand));

        if (this.getExitValue().intValue() != 0) {
          throw new BuildException("Cannot run activate for the scheduler. Aborting interface deactivation.");
        }

        final String directoryCheckerSetName = "Directory_Checker_" + this.deactivatedInterfaceName;
        // Start Directory_Checker action if the interface exists.
        if (directoryCheckerSetExists()) {

          final String directoryCheckerCommand = new String(this.binDirectory + "/" + "engine -e startAndWaitSet "
              + this.deactivatedInterfaceName + "-" + this.ossName + " " + directoryCheckerSetName);
          System.out.println("Running " + directoryCheckerSetName);
          System.out.println(this.runCommand(directoryCheckerCommand));

          if (this.getExitValue().intValue() == 1) {
            System.out.println("Directory checker set " + directoryCheckerSetName + " not found. Cannot run "
                + this.deactivatedInterfaceName + "-" + this.ossName + "/" + directoryCheckerSetName + ".");
          } else if (this.getExitValue().intValue() == 2) {
            throw new BuildException(directoryCheckerSetName
                + " has been dropped from priorityqueue. Aborting interface deactivation.");
          } else if (this.getExitValue().intValue() > 2) {
            throw new BuildException("Running directory checker " + directoryCheckerCommand + " failed.");
          }

        } else {
          System.out.println("Directory checker set not found " + directoryCheckerSetName + ". Set not started.");
        }
      }
*/
    } catch (Exception e) {
      throw new BuildException("InterfaceDeactivation failed.", e);
    }

  }

  public String getDeactivatedInterfaceName() {
    return deactivatedInterfaceName;
  }

  public void setDeactivatedInterfaceName(final String deactivatedInterfaceName) {
    this.deactivatedInterfaceName = deactivatedInterfaceName;
  }

  public String getOssName() {
    return ossName;
  }

  public void setOssName(final String ossName) {
    this.ossName = ossName;
  }

  /**
   * This function reads the database connection details from the file
   * ${configurationDirectory}/ETLCServer.properties
   * 
   * @return Returns a HashMap with the database connection details.
   * @throws BuildException
   */
  private HashMap getDatabaseConnectionDetails() throws BuildException {
    final HashMap databaseConnectionDetails = new HashMap();

    try {
      final File targetFile = new File(propertiesFilepath);
      if (!targetFile.isFile() || !targetFile.canRead()) {
        throw new BuildException("Could not read database properties. Please check that the file " + propertiesFilepath
            + " exists and it can be read.");
      }

      final BufferedReader reader = new BufferedReader(new FileReader(targetFile));

      String line = "";
      while ((line = reader.readLine()) != null) {
        if (line.indexOf("ENGINE_DB_URL") != -1) {
          String databaseUrl = line.substring(line.indexOf("=") + 1, line.length());
          if (databaseUrl.charAt(0) == ' ') {
            // Drop the empty string after the equals (=) character.
            databaseUrl = databaseUrl.substring(1, databaseUrl.length());
          }
          databaseConnectionDetails.put("etlrepDatabaseUrl", databaseUrl);
        }
        if (line.indexOf("ENGINE_DB_USERNAME") != -1) {
          String databaseUsername = line.substring(line.indexOf("=") + 1, line.length());
          if (databaseUsername.charAt(0) == ' ') {
            // Drop the empty string after the equals (=) character.
            databaseUsername = databaseUsername.substring(1, databaseUsername.length());
          }
          databaseConnectionDetails.put("etlrepDatabaseUsername", databaseUsername);

        }
        if (line.indexOf("ENGINE_DB_PASSWORD") != -1) {
          String databasePassword = line.substring(line.indexOf("=") + 1, line.length());
          if (databasePassword.charAt(0) == ' ') {
            // Drop the empty string after the equals (=) character.
            databasePassword = databasePassword.substring(1, databasePassword.length());
          }
          databaseConnectionDetails.put("etlrepDatabasePassword", databasePassword);
        }
        if (line.indexOf("ENGINE_DB_DRIVERNAME") != -1) {
          String databaseDriver = line.substring(line.indexOf("=") + 1, line.length());
          if (databaseDriver.charAt(0) == ' ') {
            // Drop the empty string after the equals (=) character.
            databaseDriver = databaseDriver.substring(1, databaseDriver.length());
          }
          databaseConnectionDetails.put("etlrepDatabaseDriver", databaseDriver);
        }
      }
      reader.close();
      // Set the database connection properties as ANT properties.
      final Iterator databaseConnectionDetailsIterator = databaseConnectionDetails.keySet().iterator();
      while (databaseConnectionDetailsIterator.hasNext()) {
        final String property = (String) databaseConnectionDetailsIterator.next();
        final String value = (String) databaseConnectionDetails.get(property);
        // System.out.println(property + " = " + value);
        getProject().setNewProperty(property, value);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Connection to database failed.", e);
    }

    return databaseConnectionDetails;
  }

  /**
   * This function creates the rockfactory object to etlrep from the database
   * connection details read from ETLCServer.properties file.
   * 
   * @param databaseConnectionDetails
   * @return Returns the created RockFactory.
   */
  private RockFactory createEtlrepRockFactory(final HashMap databaseConnectionDetails) throws BuildException {
    RockFactory rockFactory = null;
    final String databaseUsername = databaseConnectionDetails.get("etlrepDatabaseUsername").toString();
    final String databasePassword = databaseConnectionDetails.get("etlrepDatabasePassword").toString();
    final String databaseUrl = databaseConnectionDetails.get("etlrepDatabaseUrl").toString();
    final String databaseDriver = databaseConnectionDetails.get("etlrepDatabaseDriver").toString();

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver,
          "DeactivateInterface", true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Unable to initialize database connection.", e);
    }

    if (rockFactory == null) {
      throw new BuildException(
          "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");
    }

    return rockFactory;
  }

  /**
   * This function creates the RockFactory to dwhrep. The created RockFactory is
   * inserted in class variable dwhrepRockFactory.
   * 
   * @throws BuildException
   */
  private void createDwhrepRockFactory() throws BuildException {
    try {
      final Meta_databases whereMetaDatabases = new Meta_databases(this.etlrepRockFactory);
      whereMetaDatabases.setConnection_name("dwhrep");
      whereMetaDatabases.setType_name("USER");
      final Meta_databasesFactory metaDatabasesFactory = new Meta_databasesFactory(this.etlrepRockFactory,
          whereMetaDatabases);
      final Vector metaDatabases = metaDatabasesFactory.get();

      if (metaDatabases != null || metaDatabases.size() == 1) {
        final Meta_databases targetMetaDatabase = (Meta_databases) metaDatabases.get(0);

        this.dwhrepRockFactory = new RockFactory(targetMetaDatabase.getConnection_string(), targetMetaDatabase
            .getUsername(), targetMetaDatabase.getPassword(), etlrepRockFactory.getDriverName(), "DeactivateInterface",
            true);

      } else {
        throw new BuildException("Unable to connect metadata (No dwhrep or multiple dwhreps defined in Meta_databases)");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Creating database connection to dwhrep failed.", e);
    }
  }

  /**
   * This function deactivates interface.
   * 
   * @throws BuildException
   */
  private boolean deactivateInterface() throws BuildException {

    try {

      // HashMap containing dataformat entries.
      // Key: uniqueId contains of tagid from the DefaultTags table + "#" +
      // dataformatid from DataFormat table.
      // Value: RockObject of DataFormat entry.
      final HashMap dataFormats = new HashMap();

      // Get the dataformattype of this interface.
      final Datainterface whereDataInterface = new Datainterface(this.dwhrepRockFactory);
      whereDataInterface.setInterfacename(this.deactivatedInterfaceName);
      final DatainterfaceFactory dataInterfaceFactory = new DatainterfaceFactory(this.dwhrepRockFactory,
          whereDataInterface);
      final Vector dataInterfaceVector = dataInterfaceFactory.get();

      String dataFormatType = "";

      if (dataInterfaceVector != null && dataInterfaceVector.size() > 0) {
        final Datainterface targetDataInterface = (Datainterface) dataInterfaceVector.get(0);
        dataFormatType = targetDataInterface.getDataformattype();
        deactivatedInterfaceVersion = targetDataInterface.getInterfaceversion();
        if (dataFormatType == null) {
          throw new BuildException("DataFormatType was null. Interface deactivation cannot continue.");
        }

      } else {
        throw new BuildException("Dataformat type not found for interface " + this.deactivatedInterfaceName
            + ". Interface deactivation cannot continue.");
      }

      // Get tech packs related to this interface.
      final Interfacetechpacks whereInterfaceTechPacks = new Interfacetechpacks(this.dwhrepRockFactory);
      whereInterfaceTechPacks.setInterfacename(this.deactivatedInterfaceName);
      final InterfacetechpacksFactory interfaceTechPacksFactory = new InterfacetechpacksFactory(this.dwhrepRockFactory,
          whereInterfaceTechPacks);
      final Vector interfaceTechPacks = interfaceTechPacksFactory.get();
      if (interfaceTechPacks == null) {
        throw new BuildException("Tech packs related to this interface " + this.deactivatedInterfaceName
            + " were not found. Interface deactivation can not continue.");
      }
      final Iterator interfaceTechPacksIterator = interfaceTechPacks.iterator();
      while (interfaceTechPacksIterator.hasNext()) {
        final Interfacetechpacks currentTechPack = (Interfacetechpacks) interfaceTechPacksIterator.next();
        final String techPackName = currentTechPack.getTechpackname();
        // Get the deactivated tech pack activation.
        final Tpactivation whereTPActivation = new Tpactivation(this.dwhrepRockFactory);
        whereTPActivation.setTechpack_name(techPackName);
        whereTPActivation.setStatus("INACTIVE");
        final TpactivationFactory tpActivationFactory = new TpactivationFactory(this.dwhrepRockFactory,
            whereTPActivation);
        final Vector tpActivations = tpActivationFactory.get();
        if (tpActivations == null || tpActivations.size() == 0) {
          System.out.println("Tech pack " + techPackName + " deactivation for interface " + this.deactivatedInterfaceName
              + " was not found. " + techPackName
              + " specific measurements can be added after TP is installed with reactivation of this interface.");
          continue;
        }
        final Iterator tpActivationsIterator = tpActivations.iterator();

        while (tpActivationsIterator.hasNext()) {
          final Tpactivation currentTpActivation = (Tpactivation) tpActivationsIterator.next();
          // VersionId is used to map TypeActivation entries to table
          // DataFormat.
          final String techPackVersionId = currentTpActivation.getVersionid();

          // Get the TypeActivations of this TPActivation.
          final Typeactivation whereTypeActivation = new Typeactivation(this.dwhrepRockFactory);
          whereTypeActivation.setTechpack_name(techPackName);
          final TypeactivationFactory typeActivationFactory = new TypeactivationFactory(this.dwhrepRockFactory,
              whereTypeActivation);
          final Vector typeActivations = typeActivationFactory.get();
          if (typeActivations == null) {
            System.out.println("Type activations for tech pack " + techPackName + " for interface "
                + this.deactivatedInterfaceName + " were not found.");
            continue;
          }
          final Iterator typeActivationsIterator = typeActivations.iterator();

          while (typeActivationsIterator.hasNext()) {
            final Typeactivation currentTypeActivation = (Typeactivation) typeActivationsIterator.next();
            final String typeName = currentTypeActivation.getTypename();
            // TypeId in table DataFormat is in format
            // VERSIONID:TYPENAME:DATAFORMATTYPE.
            final String DataFormatTypeId = techPackVersionId + ":" + typeName + ":" + dataFormatType;

            System.out.println("Looking for entries with dataformatid: " + DataFormatTypeId);

            final Dataformat whereDataFormat = new Dataformat(this.dwhrepRockFactory);
            whereDataFormat.setDataformatid(DataFormatTypeId);
            final DataformatFactory dataFormatFactory = new DataformatFactory(this.dwhrepRockFactory, whereDataFormat);
            final Vector dataFormatVector = dataFormatFactory.get();
            if (dataFormatVector == null) {
              System.out.println("Dataformat type " + typeName + " of " + techPackName + " was not found.");
              continue;
            }
            final Iterator dataFormatIterator = dataFormatVector.iterator();

            while (dataFormatIterator.hasNext()) {
              final Dataformat currentDataFormat = (Dataformat) dataFormatIterator.next();
              // Get the tagid's used by this dataformat.
              final Defaulttags whereDefaultTags = new Defaulttags(this.dwhrepRockFactory);
              whereDefaultTags.setDataformatid(currentDataFormat.getDataformatid());
              final DefaulttagsFactory defaultTagsFactory = new DefaulttagsFactory(this.dwhrepRockFactory,
                  whereDefaultTags);
              final Vector defaultTagsVector = defaultTagsFactory.get();
              if (defaultTagsVector == null) {
                System.out.println("Default tags ids of " + currentDataFormat.getDataformatid() + " of " + techPackName
                    + " was not found.");
                continue;
              }
              final Iterator defaultTagsIterator = defaultTagsVector.iterator();

              while (defaultTagsIterator.hasNext()) {
                final Defaulttags currentDefaultTag = (Defaulttags) defaultTagsIterator.next();
                // Found related dataformat with unique tagid. Add it to
                // comparable dataformat entries.

                System.out.println("Adding dataFormat: " + currentDefaultTag.getTagid() + "#"
                    + currentDataFormat.getDataformatid());

                dataFormats.put(currentDefaultTag.getTagid() + "#" + currentDataFormat.getDataformatid(),
                    currentDataFormat);
              }
            }
          }
        }
      }
      if (dataFormats.size() == 0) {
        throw new BuildException("No Tech packs were deactivated for interface " + this.deactivatedInterfaceName
            + ". Interface deactivation can not proceed.");
      }

      // At this point dataformat entries are collected to dataFormats hashmap.
      // Remove the old entries from InterfaceMeasurement if they exist.
      final Interfacemeasurement whereInterfaceMeasurement = new Interfacemeasurement(this.dwhrepRockFactory);
      whereInterfaceMeasurement.setInterfacename(this.deactivatedInterfaceName);
      final InterfacemeasurementFactory interfaceMeasurementFactory = new InterfacemeasurementFactory(
          this.dwhrepRockFactory, whereInterfaceMeasurement);
      final Vector interfaceMeasurementsVector = interfaceMeasurementFactory.get();
      if (interfaceMeasurementsVector == null) {
        System.out.println("Measurements for this interface " + this.deactivatedInterfaceName + " were not found.");
      } else {
        final Iterator interfaceMeasurementsIterator = interfaceMeasurementsVector.iterator();

        while (interfaceMeasurementsIterator.hasNext()) {
          final Interfacemeasurement currentInterfaceMeasurement = (Interfacemeasurement) interfaceMeasurementsIterator
              .next();

          currentInterfaceMeasurement.deleteDB();

          System.out.println("Removed old InterfaceMeasurement " + currentInterfaceMeasurement.getDataformatid());
        }
      }

      final Date currentTime = new Date();
      final Timestamp currentTimeTimestamp = new Timestamp(currentTime.getTime());

      // Start inserting the values collected from DataFormat table.
      final Set dataFormatsSet = dataFormats.keySet();
      final Iterator dataFormatsIterator = dataFormatsSet.iterator();

      while (dataFormatsIterator.hasNext()) {
        final String uniqueId = (String) dataFormatsIterator.next();
        final Dataformat currentDataFormat = (Dataformat) dataFormats.get(uniqueId);
        final String currentTagId = uniqueId.substring(0, uniqueId.indexOf("#"));

        // Get the defaultTag for this dataformat from DefaultTags table.
        // Long tagId = new Long(0);
        String description = "";
        final Defaulttags whereDefaultTag = new Defaulttags(this.dwhrepRockFactory);
        whereDefaultTag.setDataformatid(currentDataFormat.getDataformatid());
        final DefaulttagsFactory defaultTagsFactory = new DefaulttagsFactory(this.dwhrepRockFactory, whereDefaultTag);
        final Vector defaultTagsVector = defaultTagsFactory.get();
        if (defaultTagsVector == null) {
          System.out.println("Getting default tags for current dataformat " + currentDataFormat + " this interface "
              + this.deactivatedInterfaceName + " did not success. Interface deactivation cannot continue.");
          continue;
        }
        if (defaultTagsVector.size() == 0) {
          System.out.println("No tagid found for dataformat " + currentDataFormat.getDataformatid());
          return false;
        } else {
          final Defaulttags targetDefaultTag = (Defaulttags) defaultTagsVector.get(0);
          // tagId = targetDefaultTag.getTagid();
          description = targetDefaultTag.getDescription();
        }

        // Create a new row to table InterfaceMeasurement.
        final Interfacemeasurement newInterfaceMeasurement = new Interfacemeasurement(this.dwhrepRockFactory);
        // String currentTagId = uniqueId.substring(0, uniqueId.indexOf("#"));

        newInterfaceMeasurement.setTagid(currentTagId);
        newInterfaceMeasurement.setDescription(description);

        newInterfaceMeasurement.setDataformatid(currentDataFormat.getDataformatid());
        newInterfaceMeasurement.setInterfacename(this.deactivatedInterfaceName);

        // R6 change
        if (this.deactivatedInterfaceVersion == null || this.deactivatedInterfaceVersion.equals(""))
          newInterfaceMeasurement.setInterfaceversion("N/A");
        else
          newInterfaceMeasurement.setInterfaceversion(this.deactivatedInterfaceVersion);

        newInterfaceMeasurement.setTechpackversion("N/A");

        // Check if the TransformerId exists in table Transformer.
        // If it doesn't exist in the Transformer, insert null to the column
        // TRANFORMERID in the InterfaceMeasurement.
        final Transformer whereTransformer = new Transformer(this.dwhrepRockFactory);
        whereTransformer.setTransformerid(currentDataFormat.getDataformatid());
        final TransformerFactory transformerFactory = new TransformerFactory(this.dwhrepRockFactory, whereTransformer);
        final Vector targetTransformerVector = transformerFactory.get();

        if (targetTransformerVector != null && targetTransformerVector.size() > 0) {
          // TransformerId exists in table Transformer.
          newInterfaceMeasurement.setTransformerid(currentDataFormat.getDataformatid());
        } else {
          // No transformerId found. Set null to the InterfaceMeasurement's
          // TransformerId.
          newInterfaceMeasurement.setTransformerid(null);
        }

        newInterfaceMeasurement.setStatus(new Long(1));
        newInterfaceMeasurement.setModiftime(currentTimeTimestamp);

        // Save the new InterfaceMeasurement to database table
        newInterfaceMeasurement.insertDB();

      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Function deactivateInterface failed.", e);
    }

  }

  public String getConfigurationDirectory() {
    return configurationDirectory;
  }

  public void setConfigurationDirectory(final String configurationDirectory) {
    this.configurationDirectory = configurationDirectory;
  }

  /**
   * This function copies the existing interface's set, adds the OSS Name to it
   * and updates interface set's parser action's indir.
   */
  private boolean copyInterfaceSet() throws BuildException {
    try {

      // HashMap containing metaCollections of this interface set.
      // Key: collectionId of meta_collections table (primary key).
      // Value: RockObject of Meta_collections entry.
      final HashMap metaCollections = new HashMap();

      // HashMap containing metaTransferActions of this interface's
      // metaCollections.
      // Key: transferActionId of meta_transfer_actions table.
      // Value: RockObject of Meta_transfer_actions entry.
      final HashMap metaTransferActions = new HashMap();

      // Variable containing
      Meta_collection_sets targetMetaCollectionSet = null;

      // First get the tech pack (or in this case interface) entry.
      final Meta_collection_sets whereMetaCollectionSets = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSets.setCollection_set_name(this.deactivatedInterfaceName);
      final Meta_collection_setsFactory metaCollectionSetsFactory = new Meta_collection_setsFactory(
          this.etlrepRockFactory, whereMetaCollectionSets);
      Vector metaCollectionsVector = metaCollectionSetsFactory.get();

      if (metaCollectionsVector.size() > 0) {
        targetMetaCollectionSet = (Meta_collection_sets) metaCollectionsVector.get(0);
      } else {
        System.out.println("No interface set found for " + this.deactivatedInterfaceName);
        return false;
      }

      // Get all the sets of this interface.
      final Meta_collections whereMetaCollections = new Meta_collections(this.etlrepRockFactory);
      whereMetaCollections.setCollection_set_id(targetMetaCollectionSet.getCollection_set_id());
      final Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
          whereMetaCollections);
      metaCollectionsVector = metaCollectionsFactory.get();
      final Iterator metaCollectionsIterator = metaCollectionsVector.iterator();

      while (metaCollectionsIterator.hasNext()) {
        final Meta_collections currentMetaCollection = (Meta_collections) metaCollectionsIterator.next();

        // Insert the metaCollection to a hashmap for later usage.
        metaCollections.put(currentMetaCollection.getCollection_id(), currentMetaCollection);
      }

      // Iterate the sets and get their actions.
      Set metaCollectionsKeySet = metaCollections.keySet();
      Iterator metaCollectionIdsIterator = metaCollectionsKeySet.iterator();

      while (metaCollectionIdsIterator.hasNext()) {
        final Long metaCollectionId = (Long) metaCollectionIdsIterator.next();

        final Meta_transfer_actions whereMetaTransferActions = new Meta_transfer_actions(this.etlrepRockFactory);
        whereMetaTransferActions.setCollection_id(metaCollectionId);
        whereMetaTransferActions.setCollection_set_id(targetMetaCollectionSet.getCollection_set_id());
        final Meta_transfer_actionsFactory metaTransferActionsFactory = new Meta_transfer_actionsFactory(
            this.etlrepRockFactory, whereMetaTransferActions);
        final Vector metaTransferActionsVector = metaTransferActionsFactory.get();
        final Iterator metaTransferActionsIterator = metaTransferActionsVector.iterator();

        while (metaTransferActionsIterator.hasNext()) {
          final Meta_transfer_actions currentMetaTransferAction = (Meta_transfer_actions) metaTransferActionsIterator
              .next();

          // Insert the metaTransferAction to a hashmap for later usage.
          metaTransferActions.put(currentMetaTransferAction.getTransfer_action_id(), currentMetaTransferAction);
        }
      }

      // At this point interface, sets and actions are gathered to hashmaps.
      // Start copying it's values as new entries.
      // Save the new interface first.
      final Long newCollectionSetId = getNewCollectionSetId();

      final Meta_collection_sets newMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
      newMetaCollectionSet.setCollection_set_id(newCollectionSetId);
      // Use the character - to separate interface name from the OSS name.
      // Create the new interface set with extension "-OSSNAME".
      newMetaCollectionSet
          .setCollection_set_name(targetMetaCollectionSet.getCollection_set_name() + "-" + this.ossName);
      newMetaCollectionSet.setVersion_number(targetMetaCollectionSet.getVersion_number());
      newMetaCollectionSet.setDescription(targetMetaCollectionSet.getDescription());
      newMetaCollectionSet.setEnabled_flag("N");
      newMetaCollectionSet.setType(targetMetaCollectionSet.getType());
      newMetaCollectionSet.insertDB(false, false);
      // System.out.println("CollectionSet " +
      // newMetaCollectionSet.getCollection_set_name() + " inserted
      // succesfully.");

      // Start saving the sets.
      metaCollectionsKeySet = metaCollections.keySet();
      metaCollectionIdsIterator = metaCollectionsKeySet.iterator();

      while (metaCollectionIdsIterator.hasNext()) {
        final Long currentCollectionId = (Long) metaCollectionIdsIterator.next();
        final Meta_collections targetMetaCollection = (Meta_collections) metaCollections.get(currentCollectionId);
        final Long newCollectionId = getNewCollectionId();

        final Meta_collections newMetaCollection = new Meta_collections(this.etlrepRockFactory);
        newMetaCollection.setCollection_id(newCollectionId);
        newMetaCollection.setCollection_name(targetMetaCollection.getCollection_name().replaceAll("\\$\\{OSS\\}",
            this.ossName));
        newMetaCollection.setCollection(targetMetaCollection.getCollection());
        newMetaCollection.setMail_error_addr(targetMetaCollection.getMail_error_addr());
        newMetaCollection.setMail_fail_addr(targetMetaCollection.getMail_fail_addr());
        newMetaCollection.setMail_bug_addr(targetMetaCollection.getMail_bug_addr());
        newMetaCollection.setMax_errors(targetMetaCollection.getMax_errors());
        newMetaCollection.setMax_fk_errors(targetMetaCollection.getMax_fk_errors());
        newMetaCollection.setMax_col_limit_errors(targetMetaCollection.getMax_col_limit_errors());
        newMetaCollection.setCheck_fk_error_flag(targetMetaCollection.getCheck_fk_error_flag());
        newMetaCollection.setCheck_col_limits_flag(targetMetaCollection.getCheck_col_limits_flag());
        newMetaCollection.setLast_transfer_date(targetMetaCollection.getLast_transfer_date());
        newMetaCollection.setVersion_number(targetMetaCollection.getVersion_number());
        newMetaCollection.setCollection_set_id(newMetaCollectionSet.getCollection_set_id());
        newMetaCollection.setUse_batch_id(targetMetaCollection.getUse_batch_id());
        newMetaCollection.setPriority(targetMetaCollection.getPriority());
        newMetaCollection.setQueue_time_limit(targetMetaCollection.getQueue_time_limit());
        newMetaCollection.setEnabled_flag("N");
        newMetaCollection.setSettype(targetMetaCollection.getSettype());
        newMetaCollection.setFoldable_flag(targetMetaCollection.getFoldable_flag());
        newMetaCollection.setMeastype(targetMetaCollection.getMeastype());
        newMetaCollection.setHold_flag(targetMetaCollection.getHold_flag());
        newMetaCollection.setScheduling_info(targetMetaCollection.getScheduling_info());

        // Insert the copied set to database.
        newMetaCollection.insertDB(false, false);

        // System.out.println("Collection " +
        // newMetaCollection.getCollection_name() + " inserted succesfully.");

        // Next insert the actions of the new set to database.
        final Set metaTransferActionsKeySet = metaTransferActions.keySet();
        final Iterator metaTransferActionsIterator = metaTransferActionsKeySet.iterator();

        while (metaTransferActionsIterator.hasNext()) {
          final Long metaTransferActionId = (Long) metaTransferActionsIterator.next();
          final Meta_transfer_actions targetMetaTransferAction = (Meta_transfer_actions) metaTransferActions
              .get(metaTransferActionId);

          // Check if this action is related to set we are copying.
          if (targetMetaTransferAction.getCollection_id().longValue() == targetMetaCollection.getCollection_id()
              .longValue()) {
            final Long newTransferActionId = getNewTransferActionId();

            // Create a copy of this action for the new set.
            final Meta_transfer_actions newMetaTransferAction = new Meta_transfer_actions(this.etlrepRockFactory);
            newMetaTransferAction.setVersion_number(targetMetaTransferAction.getVersion_number());
            newMetaTransferAction.setTransfer_action_id(newTransferActionId);
            newMetaTransferAction.setCollection_id(newMetaCollection.getCollection_id());
            newMetaTransferAction.setCollection_set_id(newMetaCollectionSet.getCollection_set_id());
            newMetaTransferAction.setAction_type(targetMetaTransferAction.getAction_type());
            newMetaTransferAction.setTransfer_action_name(targetMetaTransferAction.getTransfer_action_name()
                .replaceAll("\\$\\{OSS\\}", this.ossName));
            newMetaTransferAction.setOrder_by_no(targetMetaTransferAction.getOrder_by_no());
            newMetaTransferAction.setDescription(targetMetaTransferAction.getDescription());

            if (targetMetaTransferAction.getWhere_clause() != null) {
              String newWhereClause = new String(targetMetaTransferAction.getWhere_clause());
              newWhereClause = newWhereClause.replaceAll("\\$\\{OSS\\}", this.ossName);
              newMetaTransferAction.setWhere_clause(newWhereClause);
            } else {
              newMetaTransferAction.setWhere_clause(null);
            }

            if (targetMetaTransferAction.getAction_contents() != null) {
              String newActionContents = new String(targetMetaTransferAction.getAction_contents());
              newActionContents = newActionContents.replaceAll("\\$\\{OSS\\}", this.ossName);
              newMetaTransferAction.setAction_contents(newActionContents);
            } else {
              newMetaTransferAction.setAction_contents(null);
            }

            newMetaTransferAction.setEnabled_flag("N");
            newMetaTransferAction.setConnection_id(targetMetaTransferAction.getConnection_id());

            // Save the new action to database.
            newMetaTransferAction.insertDB(false, false);

          }
        }

        // Copy also the schedulings from the interface.
        final Meta_schedulings whereMetaSchedulings = new Meta_schedulings(this.etlrepRockFactory);
        whereMetaSchedulings.setCollection_id(currentCollectionId);
        whereMetaSchedulings.setCollection_set_id(targetMetaCollectionSet.getCollection_set_id());
        final Meta_schedulingsFactory metaSchedulingsFactory = new Meta_schedulingsFactory(this.etlrepRockFactory,
            whereMetaSchedulings);
        final Vector metaSchedulings = metaSchedulingsFactory.get();
        final Iterator metaSchedulingsIterator = metaSchedulings.iterator();

        while (metaSchedulingsIterator.hasNext()) {
          final Meta_schedulings currentMetaScheduling = (Meta_schedulings) metaSchedulingsIterator.next();

          final Meta_schedulings newMetaScheduling = new Meta_schedulings(this.etlrepRockFactory);
          newMetaScheduling.setCollection_id(newCollectionId);
          newMetaScheduling.setCollection_set_id(newCollectionSetId);
          newMetaScheduling.setExecution_type(currentMetaScheduling.getExecution_type());
          newMetaScheduling.setFri_flag(currentMetaScheduling.getFri_flag());
          // Don't just copy hold flag. Instead set the new scheduling as
          // active.
          newMetaScheduling.setHold_flag("Y");
          newMetaScheduling.setInterval_hour(currentMetaScheduling.getInterval_hour());
          newMetaScheduling.setInterval_min(currentMetaScheduling.getInterval_min());
          // Set the last execution time way back to the year 1970 so that the
          // scheduling is executed as soon as possible.
          newMetaScheduling.setLast_execution_time(new Timestamp(0));
          newMetaScheduling.setMon_flag(currentMetaScheduling.getMon_flag());
          newMetaScheduling.setName(currentMetaScheduling.getName());
          newMetaScheduling.setOs_command(currentMetaScheduling.getOs_command());
          newMetaScheduling.setPriority(currentMetaScheduling.getPriority());
          newMetaScheduling.setSat_flag(currentMetaScheduling.getSat_flag());
          newMetaScheduling.setScheduling_day(currentMetaScheduling.getScheduling_day());
          newMetaScheduling.setScheduling_hour(currentMetaScheduling.getScheduling_hour());
          newMetaScheduling.setScheduling_min(currentMetaScheduling.getScheduling_min());
          newMetaScheduling.setScheduling_month(currentMetaScheduling.getScheduling_month());
          newMetaScheduling.setScheduling_year(currentMetaScheduling.getScheduling_year());
          newMetaScheduling.setSun_flag(currentMetaScheduling.getSun_flag());
          newMetaScheduling.setThu_flag(currentMetaScheduling.getThu_flag());
          newMetaScheduling.setTrigger_command(currentMetaScheduling.getTrigger_command());
          newMetaScheduling.setTue_flag(currentMetaScheduling.getTue_flag());
          newMetaScheduling.setVersion_number(currentMetaScheduling.getVersion_number());
          newMetaScheduling.setWed_flag(currentMetaScheduling.getWed_flag());

          final Long newMetaSchedulingId = getNewMetaSchedulingsId();

          newMetaScheduling.setId(newMetaSchedulingId);

          // Save the new scheduling to database
          newMetaScheduling.insertDB(false, false);
        }
      }

      return true;

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Function copyInterfaceSet failed.", e);
    }

  }

  /**
   * This function gets the CollectionSetId for the new MetaCollectionSet entry.
   * 
   * @return Returns the new CollectionSetId.
   * @throws BuildException
   */
  private Long getNewCollectionSetId() throws BuildException {
    ResultSet resultSet = null;
    Statement statement = null;
    Connection connection = null;
    try {
      Long newCollectionSetId = new Long(0);
      connection = this.etlrepRockFactory.getConnection();
      statement = connection.createStatement();
      final String sqlQuery = "SELECT collection_set_id FROM meta_collection_sets ORDER BY collection_set_id DESC;";

      resultSet = statement.executeQuery(sqlQuery);

      if (resultSet.next()) {
        newCollectionSetId = new Long(resultSet.getLong("collection_set_id") + 1);
      }

      return newCollectionSetId;
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Failed to generate new collection set id.", e);
    } finally {

      try {
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
      }

      try {
        if (statement != null) {
          statement.close();
        }
      } catch (Exception e) {
      }

    }
  }

  /**
   * This function gets the CollectionId for the new MetaCollections entry.
   * 
   * @return Returns the new CollectionId.
   * @throws BuildException
   */
  private Long getNewCollectionId() throws BuildException {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    try {
      Long newCollectionId = new Long(0);
      connection = this.etlrepRockFactory.getConnection();
      statement = connection.createStatement();
      final String sqlQuery = "SELECT collection_id FROM meta_collections ORDER BY collection_id DESC;";

      resultSet = statement.executeQuery(sqlQuery);

      if (resultSet.next()) {
        newCollectionId = new Long(resultSet.getLong("collection_id") + 1);
      }

      return newCollectionId;
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Failed to generate new collection id.", e);
    } finally {

      try {
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
      }

      try {
        if (statement != null) {
          statement.close();
        }
      } catch (Exception e) {
      }

    }
  }

  /**
   * This function gets the TransferActionId for the new MetaTransferActions
   * entry.
   * 
   * @return Returns the new TransferActionId.
   * @throws BuildException
   */
  private Long getNewTransferActionId() throws BuildException {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    try {
      Long newTransferActionId = new Long(0);
      connection = this.etlrepRockFactory.getConnection();
      statement = connection.createStatement();
      final String sqlQuery = "SELECT transfer_action_id FROM meta_transfer_actions ORDER BY transfer_action_id DESC;";

      resultSet = statement.executeQuery(sqlQuery);

      if (resultSet.next()) {
        newTransferActionId = new Long(resultSet.getLong("transfer_action_id") + 1);
      }

      return newTransferActionId;
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Failed to generate new transfer action id.", e);

    } finally {

      try {
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
      }

      try {
        if (statement != null) {
          statement.close();
        }
      } catch (Exception e) {
      }

    }
  }

  /**
   * This command is support for executing any system commands from GUI. Use
   * getExitValue() to get the exitValue of the system command.
   * 
   * @param command
   *          the command that is needed to run
   * @return returns the output of the completed command
   */
  public final String runCommand(final String command) throws BuildException {
    final StringBuffer result = new StringBuffer();

    try {
      final Runtime runtime = Runtime.getRuntime();
      final Process process = runtime.exec(command);

      // read what process wrote to the STDIN (immediate)
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        result.append(line).append("\n");
      }

      // wait for process to end
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        try {
          process.waitFor();
        } catch (InterruptedException e2) {
          // do we have a problem here?
          throw new BuildException("DeactivateInterface runCommand interrupted.", e2);
        }
      }

      // and read whatever was left to STDIN
      while ((line = bufferedReader.readLine()) != null) {
        result.append(line).append("\n");
      }

      // close streams
      bufferedReader.close();
      process.getErrorStream().close();
      process.getOutputStream().close();

      // save exit information of the process and return with output string
      exitValue = new Integer(process.exitValue());
      result.append("Command executed with exitvalue " + exitValue.toString());

    } catch (Exception e) {
      throw new BuildException("DeactivateInterface runCommand failed.", e);
    }

    return result.toString();

  }

  /**
   * This function returns true if the directory checker set exists for the
   * interface to be deactivated.
   * 
   * @return Returns true if the directory checker exists, otherwise returns
   *         false.
   * @throws BuildException
   */
  public boolean directoryCheckerSetExists() throws BuildException {
    try {

      // Get the interface's metaCollectionSetId.
      final Meta_collection_sets whereMetaCollectionSets = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSets.setCollection_set_name(this.deactivatedInterfaceName + "-" + this.ossName);
      final Meta_collection_setsFactory metaCollectionSetsFactory = new Meta_collection_setsFactory(
          this.etlrepRockFactory, whereMetaCollectionSets);
      final Vector metaCollectionSetsVector = metaCollectionSetsFactory.get();
      Long metaCollectionSetId = new Long(0);

      if (metaCollectionSetsVector.size() > 0) {
        final Meta_collection_sets targetMetaCollectionSet = (Meta_collection_sets) metaCollectionSetsVector.get(0);
        metaCollectionSetId = targetMetaCollectionSet.getCollection_set_id();
      } else {
        System.out.println("No set found for " + this.deactivatedInterfaceName + ". Cannot start Directory_Checker set.");
        return false;
      }

      final Meta_collections targetMetaCollection = new Meta_collections(this.etlrepRockFactory);
      targetMetaCollection.setCollection_name("Directory_Checker_" + this.deactivatedInterfaceName);
      targetMetaCollection.setCollection_set_id(metaCollectionSetId);
      final Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
          targetMetaCollection);
      final Vector targetMetaCollectionsVector = metaCollectionsFactory.get();

      if (targetMetaCollectionsVector.size() > 0) {
        // Directory checker set exists.
        System.out.println("Directory checker set found for " + this.deactivatedInterfaceName + "-" + this.ossName);
        return true;
      } else {
        // Directory checker not found.
        System.out.println("Directory checker set not found for " + this.deactivatedInterfaceName + "-" + this.ossName);
        return false;
      }

    } catch (Exception e) {
      throw new BuildException("Checking of directory checker set failed.", e);
    }
  }

  public String getBinDirectory() {
    return binDirectory;
  }

  public void setBinDirectory(final String binDirectory) {
    this.binDirectory = binDirectory;
  }

  public String getOnlyDeactivateInterface() {
    return onlyDeactivateInterface;
  }

  public void setOnlyDeactivateInterface(final String onlyDeactivateInterface) {
    this.onlyDeactivateInterface = onlyDeactivateInterface;
  }

  /**
   * This function gets the Id for the new MetaSchedulings entry.
   * 
   * @return Returns the new CollectionId.
   * @throws BuildException
   */
  private Long getNewMetaSchedulingsId() throws BuildException {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try {
      Long newMetaSchedulingsId = new Long(0);
      connection = this.etlrepRockFactory.getConnection();
      statement = connection.createStatement();
      final String sqlQuery = "SELECT id FROM meta_schedulings ORDER BY id DESC;";

      resultSet = statement.executeQuery(sqlQuery);

      if (resultSet.next()) {
        newMetaSchedulingsId = new Long(resultSet.getLong("id") + 1);
      }

      return newMetaSchedulingsId;
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Failed to generate new MetaScheduling id.", e);
    } finally {
      try {

        try {
          if (resultSet != null) {
            resultSet.close();
          }
        } catch (Exception e) {
        }

        if (statement != null) {
          statement.close();
        }
      } catch (Exception e) {
      }

    }
  }

  public Integer getExitValue() {
    return exitValue;
  }

  public void setExitValue(final Integer exitValue) {
    this.exitValue = exitValue;
  }

  /**
   * This function checks out if the interface is already deactivated. The
   * checking is done from deactivated interface META_COLLECTION_SET_NAME's.
   * 
   * @return Returns true if the interface is already deactivated. Otherwise
   *         returns false.
   */
  private boolean interfaceAlreadyDeactivated() throws BuildException {

    try {
      final Meta_collection_sets whereMetaCollSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollSet.setCollection_set_name(this.deactivatedInterfaceName + "-" + this.ossName);

      final Meta_collection_setsFactory metaCollSetsFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereMetaCollSet);

      final Vector metaCollSets = metaCollSetsFactory.get();

      if (metaCollSets.size() > 0) {
        return false;
      }

      return true;

    } catch (Exception e) {
      throw new BuildException("Failed to check if interface is already deactivated.", e);
    }

  }

  /**
   * This function removes the sets of this interface and all deactivated OSS's
   * interface sets.
   */
  private void removeIntfSets() {

    try {
      final Meta_collection_sets whereCollSets = new Meta_collection_sets(this.etlrepRockFactory);
      final Meta_collection_setsFactory collSetsFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereCollSets);
      final Vector collSets = collSetsFactory.get();
      final Iterator collSetsIterator = collSets.iterator();

      while (collSetsIterator.hasNext()) {
        final Meta_collection_sets currentCollSet = (Meta_collection_sets) collSetsIterator.next();

        // Remove the previously deactivated interface's sets.
        // Deactivated interface's sets are in format INTF_DC_E_XYZ-OSS_NAME.
        if (currentCollSet.getCollection_set_name().equalsIgnoreCase(this.deactivatedInterfaceName + "-" + this.ossName)) {
          System.out.println("Deleting interface set " + currentCollSet.getCollection_set_name()
              + " and it's contents.");

          // This set is the currently installed interface's or some deactivated
          // OSS's sets.
          // Delete the whole set including everything related to it.
          final Meta_collections whereColls = new Meta_collections(this.etlrepRockFactory);
          whereColls.setCollection_set_id(currentCollSet.getCollection_set_id());
//System.out.println(" collection_set_id "+currentCollSet.getCollection_set_id());
          final Meta_collectionsFactory collFactory = new Meta_collectionsFactory(this.etlrepRockFactory, whereColls);
          final Vector collections = collFactory.get();
          final Iterator collectionsIter = collections.iterator();

          while (collectionsIter.hasNext()) {
            final Meta_collections currentCollection = (Meta_collections) collectionsIter.next();

            final Meta_transfer_actions whereTrActions = new Meta_transfer_actions(this.etlrepRockFactory);
            whereTrActions.setCollection_id(currentCollection.getCollection_id());
            //System.out.println(" collection_id "+currentCollection.getCollection_id());
            whereTrActions.setCollection_set_id(currentCollSet.getCollection_set_id());
            //System.out.println(" collection_set_id "+currentCollSet.getCollection_set_id());
            final Meta_transfer_actionsFactory trActionsFactory = new Meta_transfer_actionsFactory(
                this.etlrepRockFactory, whereTrActions);
            final Vector trActions = trActionsFactory.get();
            final Iterator trActionsIter = trActions.iterator();

            while (trActionsIter.hasNext()) {
              final Meta_transfer_actions currTrAction = (Meta_transfer_actions) trActionsIter.next();
              //System.out.println(" deleting data from META_TRANSFER_ACTION");
              currTrAction.deleteDB();
            }

            // Remove also the schedulings from the interface.
            final Meta_schedulings whereMetaSchedulings = new Meta_schedulings(this.etlrepRockFactory);
            whereMetaSchedulings.setCollection_id(currentCollection.getCollection_id());
            whereMetaSchedulings.setCollection_set_id(currentCollSet.getCollection_set_id());
            final Meta_schedulingsFactory metaSchedulingsFactory = new Meta_schedulingsFactory(this.etlrepRockFactory,
                whereMetaSchedulings);
            final Vector metaSchedulings = metaSchedulingsFactory.get();
            final Iterator metaSchedulingsIterator = metaSchedulings.iterator();

            while (metaSchedulingsIterator.hasNext()) {
              final Meta_schedulings currentMetaScheduling = (Meta_schedulings) metaSchedulingsIterator.next();
              //System.out.println(" deleting data from META_TRANSFER_ACTION");
              currentMetaScheduling.deleteDB();
            }

            // Do not delete the meta_transfer_batches. Old
            // meta_transfer_batches are cleaned up by housekeeping set.
            currentCollection.deleteDB();
          }

          currentCollSet.deleteDB();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Failed removing previous installations interface sets.", e);
    }

  }

}
