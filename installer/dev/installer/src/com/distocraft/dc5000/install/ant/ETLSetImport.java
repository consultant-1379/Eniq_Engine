package com.distocraft.dc5000.install.ant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.importexport.ETLCImport;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * This is a custom made ANT task that creates the sets used by the teck pack.
 * The sets to be created are in the unzipped_tp/set directory
 * where all xml files are parsed. In case of tech pack interface sets the directory is 
 * unzipped_tp/TECH_PACK_NAME/interface.
 * @author berggren
 */
public class ETLSetImport extends Task {

  private String setDirectoryPath = new String();

  private String activatedInterface = new String();

  private String importingInterfaces = new String("false");

  /**
   * This is the filter to filter out only the files with xml extension.
   */
  protected FileFilter xmlFileFilter = new FileFilter() {

    public boolean accept(File file) {
      if (file.isFile()) {
        if (file.canRead()) {
          String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
          System.out.println("Found file " + file.getName() + ". Extension is " + fileExtension);
          if (fileExtension.equalsIgnoreCase("xml")) {
            return true;
          } else
            return false;
        } else {
          System.out.println("File " + file.getName() + " was not readable.");
          return false;
        }
      } else {
        return false;
      }
    }
  };

  public class metaCollectionSetEntryDetails {

    String collectionSetId = new String();

    String collectionSetName = new String();

    String description = new String();

    String versionNumber = new String();

    String enabledFlag = new String();

    String type = new String();

  }

  public class MyXmlHandler extends DefaultHandler {

    Vector entryDetailsMap = new Vector();

    String currentTagValue = new String();

    String currentFile = new String();

    String currentRevision = new String();

    public MyXmlHandler(Vector entryDetailsMap) {
      this.entryDetailsMap = entryDetailsMap;
    }

    public Vector getEntryDetailsMap() {
      return this.entryDetailsMap;
    }

    public void startDocument() {
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {
      currentTagValue = new String();
      if (qName.equals("META_COLLECTION_SETS")) {
        metaCollectionSetEntryDetails currentMetaCollectionSetEntry = new metaCollectionSetEntryDetails();
        currentMetaCollectionSetEntry.collectionSetId = atts.getValue("COLLECTION_SET_ID");
        currentMetaCollectionSetEntry.collectionSetName = atts.getValue("COLLECTION_SET_NAME");
        currentMetaCollectionSetEntry.description = atts.getValue("DESCRIPTION");
        currentMetaCollectionSetEntry.versionNumber = atts.getValue("VERSION_NUMBER");
        currentMetaCollectionSetEntry.enabledFlag = atts.getValue("ENABLED_FLAG");
        currentMetaCollectionSetEntry.type = atts.getValue("TYPE");
        this.entryDetailsMap.add(currentMetaCollectionSetEntry);
      }
    }

    public void endElement(String uri, String localName, String qName) {

    }

    /**
     * This function reads the characters between the xml-tags.
     */
    public void characters(char ch[], int start, int length) {
      StringBuffer charBuffer = new StringBuffer(length);
      for (int i = start; i < start + length; i++) {
        //If no control char
        if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
          charBuffer.append(ch[i]);
        }
      }
      currentTagValue += charBuffer;
    }
  }

  RockFactory etlrepRockFactory = null;

  RockFactory dwhrepRockFactory = null;

  /**
   * This function starts the installations of the tech pack sets.
   */
  public void execute() throws BuildException {

    HashMap etlrepDatabaseConnectionDetails = new HashMap();
    etlrepDatabaseConnectionDetails.put("etlrepDatabaseUrl", getProject().getProperty("etlrepDatabaseUrl"));
    etlrepDatabaseConnectionDetails.put("etlrepDatabaseUsername", getProject().getProperty("etlrepDatabaseUsername"));
    etlrepDatabaseConnectionDetails.put("etlrepDatabasePassword", getProject().getProperty("etlrepDatabasePassword"));
    etlrepDatabaseConnectionDetails.put("etlrepDatabaseDriver", getProject().getProperty("etlrepDatabaseDriver"));
    this.etlrepRockFactory = this.createRockFactory(etlrepDatabaseConnectionDetails);

    HashMap dwhrepDatabaseConnectionDetails = new HashMap();
    dwhrepDatabaseConnectionDetails.put("dwhrepDatabaseUrl", getProject().getProperty("dwhrepDatabaseUrl"));
    dwhrepDatabaseConnectionDetails.put("dwhrepDatabaseUsername", getProject().getProperty("dwhrepDatabaseUsername"));
    dwhrepDatabaseConnectionDetails.put("dwhrepDatabasePassword", getProject().getProperty("dwhrepDatabasePassword"));
    dwhrepDatabaseConnectionDetails.put("dwhrepDatabaseDriver", getProject().getProperty("dwhrepDatabaseDriver"));
    this.dwhrepRockFactory = this.createRockFactory(dwhrepDatabaseConnectionDetails);

    this.importSets();
  }

  public String getSetDirectoryPath() {
    return setDirectoryPath;
  }

  public void setSetDirectoryPath(String setDirectoryPath) {
    this.setDirectoryPath = setDirectoryPath;
  }

  /**
   * This function creates the rockfactory object from the database connection details.
   * @param databaseConnectionDetails is a HashMap containing the etlrep connection details.
   * @return Returns the created RockFactory.
   */
  private RockFactory createRockFactory(HashMap databaseConnectionDetails) throws BuildException {
    RockFactory rockFactory = null;
    String databaseUsername = new String();
    String databasePassword = new String();
    String databaseUrl = new String();
    String databaseDriver = new String();

    Set databaseConnectionKeys = databaseConnectionDetails.keySet();
    Iterator databaseConnectionKeysIterator = databaseConnectionKeys.iterator();
    while (databaseConnectionKeysIterator.hasNext()) {
      String databaseConnectionKey = (String) databaseConnectionKeysIterator.next();
      String databaseConnectionValue = (String) databaseConnectionDetails.get(databaseConnectionKey);

      if (databaseConnectionKey.indexOf("DatabaseUsername") != -1) {
        databaseUsername = databaseConnectionValue;
      } else if (databaseConnectionKey.indexOf("DatabasePassword") != -1) {
        databasePassword = databaseConnectionValue;
      } else if (databaseConnectionKey.indexOf("DatabaseUrl") != -1) {
        databaseUrl = databaseConnectionValue;
      } else if (databaseConnectionKey.indexOf("DatabaseDriver") != -1) {
        databaseDriver = databaseConnectionValue;
      }
    }

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, "PreinstallCheck",
          true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Unable to initialize database connection.", e);
    }

    if (rockFactory == null)
      throw new BuildException(
          "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");

    return rockFactory;
  }

  /**
   * This function iterates through all xml files in the directory specified by class variable setDirectoryPath
   * and parses the xml content. In the xml content exists the set's to insert to the database.
   */
  private void importSets() throws BuildException {
    try {
      File setDirectory = new File(this.setDirectoryPath);

      File[] xmlFiles = setDirectory.listFiles(this.xmlFileFilter);

      if (xmlFiles == null || xmlFiles.length == 0) {
        System.out.println("No xml files found in " + this.setDirectoryPath);
      } else {
        System.out.println("Starting to create sets.");
      }

      HashMap newSets = new HashMap();

      if (xmlFiles != null) {

        for (int i = 0; i < xmlFiles.length; i++) {

          File currentXmlFile = xmlFiles[i];

          Vector metaCollectionSetVector = new Vector();

          SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
          MyXmlHandler xmlHandler = new MyXmlHandler(metaCollectionSetVector);
          try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            // Parse the file
            saxParser.parse(currentXmlFile, xmlHandler);
          } catch (SAXException se) {
            se.printStackTrace();
          } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
          } catch (IOException ie) {
            ie.printStackTrace();
          }

          System.out.println("Finished parsing " + currentXmlFile.getAbsoluteFile() + "...");

          // Get the loaded entry details map from the xmlHandler.
          metaCollectionSetVector = xmlHandler.getEntryDetailsMap();

          Iterator metaCollectionSetIterator = metaCollectionSetVector.iterator();
          while (metaCollectionSetIterator.hasNext()) {
            metaCollectionSetEntryDetails currentMetaCollectionSetEntryDetails = (metaCollectionSetEntryDetails) metaCollectionSetIterator
                .next();

            newSets.put(currentMetaCollectionSetEntryDetails.collectionSetName,
                currentMetaCollectionSetEntryDetails.versionNumber);

            // Disable the previous sets of this interface.
            this.disablePreviousMetaCollectionSets(currentMetaCollectionSetEntryDetails.collectionSetName);
            // Disable also the schedulings of this interface.
            this.disablePreviousSchedules(currentMetaCollectionSetEntryDetails.collectionSetName);
            // Disable also the meta collections of this interface.
            this.disablePreviousMetaCollections(currentMetaCollectionSetEntryDetails.collectionSetName);
            // Disable also the meta transfer actions of this interface.
            this.disablePreviousMetaTransferActions(currentMetaCollectionSetEntryDetails.collectionSetName);
          }

          System.out.println("Creating sets from file " + currentXmlFile.getName());

          String propertiesDirectory = new String("");

          if (currentXmlFile.isFile() && currentXmlFile.canRead()) {
            ETLCImport etlcImport = new ETLCImport(propertiesDirectory, this.etlrepRockFactory.getConnection());
            // Create the sets and schedules from the xml file.
            etlcImport.doImport(currentXmlFile.getAbsolutePath(), true, true, false);
            System.out.println("Sets created succesfully from file " + currentXmlFile.getName());
          }

          // Iterate through all new sets and deactivate them.
          Set newSetNames = newSets.keySet();
          Iterator newSetNamesIterator = newSetNames.iterator();
          while (newSetNamesIterator.hasNext()) {
            String currentCollectionSetName = (String) newSetNamesIterator.next();
            String currentCollectionVersionNumber = (String) newSets.get(currentCollectionSetName);
            if (this.importingInterfaces.equalsIgnoreCase("true")
                && this.activatedInterface.equalsIgnoreCase("") == false
                && currentCollectionSetName.equalsIgnoreCase(this.activatedInterface) == false) {
              Meta_collection_sets whereMetaCollectionSets = new Meta_collection_sets(this.etlrepRockFactory);
              whereMetaCollectionSets.setCollection_set_name(currentCollectionSetName);
              whereMetaCollectionSets.setVersion_number(currentCollectionVersionNumber);
              Meta_collection_setsFactory metaCollectionSetsFactory = new Meta_collection_setsFactory(
                  this.etlrepRockFactory, whereMetaCollectionSets);
              Vector metaCollections = metaCollectionSetsFactory.get();
              if (metaCollections.size() == 1) {
                Meta_collection_sets targetMetaCollectionSet = (Meta_collection_sets) metaCollections.get(0);
                targetMetaCollectionSet.setEnabled_flag("N");
                targetMetaCollectionSet.updateDB();

                disablePreviousMetaCollections(targetMetaCollectionSet.getCollection_set_name());
                disablePreviousMetaTransferActions(targetMetaCollectionSet.getCollection_set_name());

                System.out.println("Disabled tech pack set " + targetMetaCollectionSet.getCollection_set_name());
              } else {
                System.out.println("");
              }
            }
          }
        }
        if (xmlFiles.length != 0) {
          System.out.println("All sets created succesfully.");
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Installation of sets failed.", e);
    }

  }

  /**
   * This function sets all the previous versions of this interface's sets to disabled mode.
   * @param metaCollectionSetName Name of the new interface.
   */
  private void disablePreviousMetaCollectionSets(String metaCollectionSetName) throws BuildException {
    try {
      Meta_collection_sets whereMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSet.setCollection_set_name(metaCollectionSetName);
      Meta_collection_setsFactory metaCollectionSetFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereMetaCollectionSet);
      Vector metaCollectionSets = metaCollectionSetFactory.get();
      Iterator metaCollectionSetsIterator = metaCollectionSets.iterator();
      while (metaCollectionSetsIterator.hasNext()) {
        Meta_collection_sets currentMetaCollectionSet = (Meta_collection_sets) metaCollectionSetsIterator.next();
        currentMetaCollectionSet.setEnabled_flag("N");
        currentMetaCollectionSet.updateDB();
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Disabling tech pack's sets failed.", e);
    }

  }

  /**
   * This function disables all scheduling of the previous tech pack.
   * @param metaCollectionSetName is the name of the meta collection set.
   */
  private void disablePreviousSchedules(String metaCollectionSetName) throws BuildException {
    try {
      Meta_collection_sets whereMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSet.setCollection_set_name(metaCollectionSetName);
      Meta_collection_setsFactory metaCollectionSetFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereMetaCollectionSet);
      Vector metaCollectionSets = metaCollectionSetFactory.get();
      Iterator metaCollectionSetsIterator = metaCollectionSets.iterator();
      while (metaCollectionSetsIterator.hasNext()) {
        Meta_collection_sets currentMetaCollectionSet = (Meta_collection_sets) metaCollectionSetsIterator.next();
        Long metaCollectionSetId = currentMetaCollectionSet.getCollection_set_id();
        Meta_schedulings whereMetaSchedulings = new Meta_schedulings(this.etlrepRockFactory);
        whereMetaSchedulings.setCollection_set_id(metaCollectionSetId);
        Meta_schedulingsFactory metaSchedulingsFactory = new Meta_schedulingsFactory(this.etlrepRockFactory,
            whereMetaSchedulings);
        Vector metaSchedulings = metaSchedulingsFactory.get();
        Iterator metaSchedulingsIterator = metaSchedulings.iterator();
        while (metaSchedulingsIterator.hasNext()) {
          Meta_schedulings currentMetaScheduling = (Meta_schedulings) metaSchedulingsIterator.next();
          currentMetaScheduling.setHold_flag("Y");
          currentMetaScheduling.updateDB();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Disabling tech pack's schedulings failed.", e);
    }
  }

  /**
   * This function disables the previous tech pack's meta collections. 
   * @param metaCollectionSetName is the name of the meta collection set.
   */
  private void disablePreviousMetaCollections(String metaCollectionSetName) throws BuildException {
    try {
      Meta_collection_sets whereMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSet.setCollection_set_name(metaCollectionSetName);
      Meta_collection_setsFactory metaCollectionSetFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereMetaCollectionSet);
      Vector metaCollectionSets = metaCollectionSetFactory.get();
      Iterator metaCollectionSetsIterator = metaCollectionSets.iterator();
      while (metaCollectionSetsIterator.hasNext()) {
        Meta_collection_sets currentMetaCollectionSet = (Meta_collection_sets) metaCollectionSetsIterator.next();
        Long metaCollectionSetId = currentMetaCollectionSet.getCollection_set_id();
        Meta_collections whereMetaCollections = new Meta_collections(this.etlrepRockFactory);
        whereMetaCollections.setCollection_set_id(metaCollectionSetId);
        Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
            whereMetaCollections);
        Vector metaCollections = metaCollectionsFactory.get();
        Iterator metaCollectionsIterator = metaCollections.iterator();
        while (metaCollectionsIterator.hasNext()) {
          Meta_collections currentMetaCollection = (Meta_collections) metaCollectionsIterator.next();
          currentMetaCollection.setEnabled_flag("N");
          currentMetaCollection.updateDB();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Disabling tech pack's collection failed.", e);
    }
  }

  /**
   * This function disables the previous tech pack's actions. 
   * @param metaCollectionSetName is the name of the meta collection set.
   */
  private void disablePreviousMetaTransferActions(String metaCollectionSetName) throws BuildException {
    try {
      Meta_collection_sets whereMetaCollectionSet = new Meta_collection_sets(this.etlrepRockFactory);
      whereMetaCollectionSet.setCollection_set_name(metaCollectionSetName);
      Meta_collection_setsFactory metaCollectionSetFactory = new Meta_collection_setsFactory(this.etlrepRockFactory,
          whereMetaCollectionSet);
      Vector metaCollectionSets = metaCollectionSetFactory.get();
      Iterator metaCollectionSetsIterator = metaCollectionSets.iterator();
      while (metaCollectionSetsIterator.hasNext()) {
        Meta_collection_sets currentMetaCollectionSet = (Meta_collection_sets) metaCollectionSetsIterator.next();
        Long metaCollectionSetId = currentMetaCollectionSet.getCollection_set_id();
        Meta_transfer_actions whereMetaTransferActions = new Meta_transfer_actions(this.etlrepRockFactory);
        whereMetaTransferActions.setCollection_set_id(metaCollectionSetId);
        Meta_transfer_actionsFactory metaTransferActionsFactory = new Meta_transfer_actionsFactory(
            this.etlrepRockFactory, whereMetaTransferActions);
        Vector metaTransferActions = metaTransferActionsFactory.get();
        Iterator metaTransferActionsIterator = metaTransferActions.iterator();
        while (metaTransferActionsIterator.hasNext()) {
          Meta_transfer_actions currentMetaTransferAction = (Meta_transfer_actions) metaTransferActionsIterator.next();
          currentMetaTransferAction.setEnabled_flag("N");
          currentMetaTransferAction.updateDB();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Disabling tech pack's transfer action failed.", e);
    }
  }


  public String getActivatedInterface() {
    return activatedInterface;
  }

  public void setActivatedInterface(String activatedInterface) {
    this.activatedInterface = activatedInterface;
  }

  public String getImportingInterfaces() {
    return importingInterfaces;
  }

  public void setImportingInterfaces(String importingInterfaces) {
    this.importingInterfaces = importingInterfaces;
  }

}
