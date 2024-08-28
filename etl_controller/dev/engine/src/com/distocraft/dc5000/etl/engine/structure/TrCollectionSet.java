package com.distocraft.dc5000.etl.engine.structure;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineCom;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.common.RemoveDataException;
import com.distocraft.dc5000.etl.engine.plugin.PluginLoader;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.etl.rock.Meta_versionsFactory;

/**
 * A class for transfer collection set . A starting point for a transfer.
 * 
 * @author Jukka J‰‰heimo
 * @since JDK1.1
 */
public class TrCollectionSet {

  // version number
  private Meta_versions version;

  private String versionNumber;

  // collection set id
  private Long collectionSetId;

  private String collectionSetName;

  // collection id
  private Long collectionId;

  // db connection object
  private RockFactory rockFact;

  // Vector of transfer collections
  private Vector vecTrCollections;

  // Plugin loader
  private PluginLoader pLoader;

  // is this techpack enabled
  private String isEnabled;

  private EngineCom eCom = null;
  
  private Logger log = Logger.getLogger("etlengine.engine.TrCollectionSet");

  private TrCollection currentCollection = null;
  
  /**
   * Empty protected constructor
   */
  protected TrCollectionSet() {
  }

  /**
   * Constructor for starting the transfer
   * 
   * @param rockFact
   *          the database connection for the metadata
   * @param collectionSetName
   *          the name of the transfer collection set
   */
  public TrCollectionSet(RockFactory rockFact, String collectionSetName, PluginLoader pLoader, EngineCom eCom) throws Exception {
    init(rockFact, collectionSetName, null, pLoader, eCom);

  }

  /**
   * Constructor for starting the transfer
   * 
   * @param rockFact
   *          the database connection for the metadata
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   */
  public TrCollectionSet(RockFactory rockFact, String collectionSetName, String collectionName, PluginLoader pLoader, EngineCom eCom)
      throws Exception {
    init(rockFact, collectionSetName, collectionName, pLoader, eCom);

  }

  /**
   * Init method for constructors
   * 
   * @param rockFact
   *          the database connection for the metadata
   * @param collectionSetName
   *          the name of the transfer collection set
   * @param collectionName
   *          the name of the transfer collection
   */
  public void init(RockFactory rockFact, String collectionSetName, String collectionName, PluginLoader pLoader, EngineCom eCom)
      throws Exception {

    this.eCom = eCom;
    this.collectionSetName = collectionSetName;

    String tableName = "META_VERSIONS";

    /* Get the version info */
    this.rockFact = rockFact;
    Meta_versions whereVersion = new Meta_versions(rockFact);
    whereVersion.setCurrent_flag("Y");
    Meta_versionsFactory mF = new Meta_versionsFactory(rockFact, whereVersion);
    this.version = mF.getElementAt(0);

    this.pLoader = pLoader;

    tableName = "META_COLLECTION_SETS";
    Meta_collection_sets whereCollSet = new Meta_collection_sets(rockFact);
    whereCollSet.setEnabled_flag("Y");
    whereCollSet.setCollection_set_name(collectionSetName);
    Meta_collection_sets collSet = new Meta_collection_sets(rockFact, whereCollSet);
    this.collectionSetId = collSet.getCollection_set_id();

    this.versionNumber = collSet.getVersion_number();
    this.isEnabled = collSet.getEnabled_flag();

    if (collectionName != null) {
      tableName = "META_COLLECTIONS";
      
      Meta_collections whereColl = new Meta_collections(rockFact);
      // whereColl.setVersion_number(this.version.getVersion_number());
      whereColl.setVersion_number(versionNumber);
      whereColl.setCollection_set_id(this.collectionSetId);
      whereColl.setCollection_name(collectionName);
      Meta_collections coll = new Meta_collections(rockFact, whereColl);
      this.collectionId = coll.getCollection_id();
    }

    this.vecTrCollections = getTrCollections();

  }

  /**
   * Creates executed transfer collection objects
   */
  private Vector getTrCollections() throws Exception {
    Vector vec = new Vector();

    Meta_collections whereColl = new Meta_collections(this.rockFact);
    whereColl.setVersion_number(this.versionNumber);
    whereColl.setCollection_set_id(this.collectionSetId);
    whereColl.setCollection_id(this.collectionId);
    Meta_collectionsFactory dbCollections = new Meta_collectionsFactory(this.rockFact, whereColl);
    
    Vector dbVec = dbCollections.get();

    for (int i = 0; i < dbVec.size(); i++) {
      Meta_collections dbTrCollection = (Meta_collections) dbVec.elementAt(i);
      TrCollection trCollection = new TrCollection(this.rockFact, this.version, this.collectionSetId, dbTrCollection,
          this.pLoader, eCom);
      vec.addElement(trCollection);
    }

    return vec;
  }

  /**
   * Returns the collection of a given name
   * 
   * @param name
   * @return
   */
  public TrCollection getCollection(String name) {
    for (int i = 0; i < this.vecTrCollections.size(); i++) {
      TrCollection trCollection = (TrCollection) this.vecTrCollections.elementAt(i);
      if (trCollection.getName().endsWith(name)) {
        return trCollection;
      }
    }

    return null;
  }

  /**
   * Executes all transfer collections of this collection set.
   * 
   * @throws Exception
   */
  public void executeSet() throws Exception {
	  executeSet(SetListener.NULL);
  }
  
  /**
   * Executes all transfer collections of this collection set
   * 
   * @param SetListener
   */
  public void executeSet(SetListener setListener) throws Exception {

    for (int i = 0; i < this.vecTrCollections.size(); i++) {

      // if set is not on hold and is not disabled
      TrCollection trCollection = (TrCollection) this.vecTrCollections.elementAt(i);

      if (trCollection.getEnabledFlag().equalsIgnoreCase("y") && trCollection.getHoldFlag().equalsIgnoreCase("n")) {

        currentCollection = trCollection;
        
        trCollection.execute(setListener); // EXECUTE SET
        
        currentCollection = null;

      } else {

        Logger slog = Logger.getLogger("etl." + collectionSetName + "." + trCollection.getSettype() + "."
            + trCollection.getName());

        if (!trCollection.getEnabledFlag().equalsIgnoreCase("y"))
          slog.info("Execution cancelled: Set " + trCollection.getName() + " is disabled");

        if (!trCollection.getHoldFlag().equalsIgnoreCase("n"))
          slog.info("Execution cancelled: Set " + trCollection.getName() + " is on hold");

      }

    } // for each set

  }

  public int cleanSet() throws EngineMetaDataException, RemoveDataException {

    int count = 0;

    for (int i = 0; i < this.vecTrCollections.size(); i++) {

      // if set is not on hold and is not disabled
      TrCollection trCollection = (TrCollection) this.vecTrCollections.elementAt(i);
      if (trCollection.getEnabledFlag().equalsIgnoreCase("y") && trCollection.getHoldFlag().equalsIgnoreCase("n")) {
        count += trCollection.cleanCollection();
      }

    }

    return count;
  }

  /**
   * return true is collection set is enabled.
   * 
   * @return
   */
  public boolean isEnabled() {
    return this.isEnabled.equalsIgnoreCase("y");
  }

  public TrCollection getCurrentCollection() {
    return currentCollection;
  }
  
}
