package com.ericsson.eniq.common.setWizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.ericsson.eniq.common.Utils;

/**
 * 
 * This class is used for creating the directory checker sets for a techpack.
 * 
 * @author ejarsav
 * @authore eheitur
 */
public class CreateTPDirCheckerSet {

  private Logger log = Logger.getLogger(CreateTPDirCheckerSet.class.getName());

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected String name;

  protected String version;

  protected String versionid;

  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected long techPackID;

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  private String etldataDir = "${ETLDATA_DIR}";

  protected String topologyBaseDir = "${REFERENCE_DIR}";

  private String loaderlogDir = "${LOG_DIR}/iqloader";

  private String rejectedDir = "${REJECTED_DIR}";

  private String baseLoaderlogDir = "${LOG_DIR}/iqloader";

  private String baseRejectedDir = "${REJECTED_DIR}";

  private String pathSeparator = "/";

  private String topologyName = "";

  /**
   * 
   * Constructor.
   * 
   * @param name
   * @param version
   * @param versionid
   * @param dwhrepRock
   * @param rock
   * @param techPackID
   * @param topologyName
   * @throws Exception
   */
  public CreateTPDirCheckerSet(String name, String version, String versionid, RockFactory dwhrepRock, RockFactory rock,
      long techPackID, String topologyName) throws Exception {

    this.name = name;
    this.version = version;
    this.versionid = versionid;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = Utils.getSetMaxID(rock) + 1;
    this.maxActionID = Utils.getActionMaxID(rock) + 1;

    this.topologyName = topologyName;

    loaderlogDir += pathSeparator + topologyName;
    rejectedDir += pathSeparator + topologyName;

  }

  /**
   * Create the directory checker set and the actions for it.
   * 
   * @param topology
   *          If true, then topology sets are created.
   * @param skip
   *          If true, the existing sets are skipped, instead of overwritten.
   * @throws Exception
   */
  public void create(boolean topology, boolean skip) throws Exception {

    if (skip && Utils.isSet("Directory_Checker_" + name, version, techPackID, rock)) {
      log.log(Level.INFO, "Skipped directory checker Set: " + name);
      return;
    }

    long iSet = 0;
    long iAction = 0;

    Properties prop = new Properties();
    prop.put("permission", "750");

    Properties loaderBaseProp = new Properties();
    loaderBaseProp.put("permission", "770");
    loaderBaseProp.put("owner", "dcuser");

    Properties rejectedBaseProp = new Properties();
    rejectedBaseProp.put("permission", "770");
    rejectedBaseProp.put("owner", "dcuser");

    Properties loaderProp = new Properties();
    loaderProp.put("permission", "750");
    loaderProp.put("owner", "dcuser");

    Properties rejectedProp = new Properties();
    rejectedProp.put("permission", "750");
    rejectedProp.put("owner", "dcuser");

    log.log(Level.INFO, "Creating directory checker Set: " + name);
    createSet("Install", new Long(this.maxSetID + iSet).intValue(), name).insertToDB(rock);

    // basedir
    log.log(Level.INFO, "  Creating directory checker Action: base_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + etldataDir, etldataDir + pathSeparator,
        propertyToString(prop)).insertToDB(rock);
    iAction++;

    // iqloaderBase
    log.log(Level.INFO, "  Creating directory checker Action: CreateDir_iqloaderBase_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_iqloaderBase_" + baseLoaderlogDir,
        baseLoaderlogDir + pathSeparator, propertyToString(loaderBaseProp)).insertToDB(rock);
    iAction++;

    // rejectedBAse
    log.log(Level.INFO, "  Creating directory checker Action: CreateDir_rejectedBase_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_rejectedBase_" + baseRejectedDir,
        baseRejectedDir + pathSeparator, propertyToString(rejectedBaseProp)).insertToDB(rock);
    iAction++;

    // iqloader+techpack
    log.log(Level.INFO, "  Creating directory checker Action: CreateDir_iqloaderBase_Techpack_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_iqloaderBase_Techpack_" + loaderlogDir,
        loaderlogDir + pathSeparator, propertyToString(loaderBaseProp)).insertToDB(rock);
    iAction++;

    // rejected+techpack
    log.log(Level.INFO, "  Creating directory checker Action: CreateDir_rejectedBase_Techpack_" + etldataDir);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_rejectedBase_Techpack_" + rejectedDir,
        rejectedDir + pathSeparator, propertyToString(rejectedBaseProp)).insertToDB(rock);
    iAction++;

    if (topology) {

      // basedir
      log.log(Level.INFO, "  Creating directory checker Action: base_" + topologyBaseDir);
      createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + topologyBaseDir,
          topologyBaseDir + pathSeparator, propertyToString(prop)).insertToDB(rock);
      iAction++;

      // meas
      log.log(Level.INFO, "  Creating directory checker Action: base_" + topologyBaseDir + pathSeparator + topologyName
          + pathSeparator);
      createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
          new Long(this.maxActionID + iAction).intValue(), "CreateDir_base_" + topologyName,
          topologyBaseDir + pathSeparator + topologyName + pathSeparator, propertyToString(prop)).insertToDB(rock);
      iAction++;

      // Query q = session.createQuery("from ReferenceTable where VERSIONID=?");
      // q.setString(0,version.getVersionid());

      Referencetable rt = new Referencetable(dwhrepRock);
      rt.setVersionid(versionid);
      ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock, rt);

      Iterator iTable = rtf.get().iterator();

      while (iTable.hasNext()) {

        int uPolicy = 0;
        Referencetable rtable = (Referencetable) iTable.next();

        if (rtable.getUpdate_policy() != null) {
          uPolicy = rtable.getUpdate_policy().intValue();
        }

        if (uPolicy == 1) {

          iAction = createLogCheckers(iSet, iAction, rtable.getTypename(), rejectedProp, loaderProp);

        } else if ((uPolicy == 2 || uPolicy == 3)) {

          iAction = createLogCheckers(iSet, iAction, rtable.getTypename(), rejectedProp, loaderProp);

          // The update policy is dynamic(2) or timed dynamic(3). Create the directory checker
          // actions also for the "CURRENT_DC".
          iAction = createETLCheckers(iSet, iAction, rtable.getTypename().toLowerCase() + "_current_dc", prop);
          iAction = createLogCheckers(iSet, iAction, rtable.getTypename() + "_CURRENT_DC_RAW", rejectedProp, loaderProp);

        } else {

        }
      }
    }

    // Query qm = session.createQuery("from MeasurementType where VERSIONID=?");
    // qm.setString(0,version.getVersionid());

    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(versionid);
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock, mt);

    Iterator iVer = mtf.get().iterator();

    // measurementtypes
    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();

      // Iterator iTable = types.getMeasurementTables().iterator();

      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(types.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock, mta);
      Iterator iTable = mtaf.get().iterator();

      // measurementtypes
      while (iTable.hasNext()) {

        Measurementtable table = (Measurementtable) iTable.next();

        if (table.getTablelevel().equalsIgnoreCase("raw")) {
          iAction = createETLCheckers(iSet, iAction, types.getFoldername().toLowerCase(), prop);
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_RAW", rejectedProp, loaderProp);
        }

        if (table.getTablelevel().equalsIgnoreCase("day"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_DAY", rejectedProp, loaderProp);
        if (table.getTablelevel().equalsIgnoreCase("daybh"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_DAYBH", rejectedProp, loaderProp);
        if (table.getTablelevel().equalsIgnoreCase("rankbh"))
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_RANKBH", rejectedProp, loaderProp);

        if (types.getJoinable() != null && types.getJoinable().length() > 0) {
          iAction = createLogCheckers(iSet, iAction, types.getFoldername() + "_PREV_RAW", rejectedProp, loaderProp);
        }

      }
    }

    // qm = session.createQuery("from ReferenceTable where VERSIONID=?");
    // qm.setString(0,version.getVersionid());

    Referencetable rt = new Referencetable(dwhrepRock);
    rt.setVersionid(versionid);
    ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock, rt);

    iVer = rtf.get().iterator();

    // ReferenceTables
    while (iVer.hasNext()) {
      Referencetable rTable = (Referencetable) iVer.next();

      // Ignore the SELECT_XXX_AGGLEVEL reference type from the base techpack.
      if (rTable.getObjectname().contains("SELECT_") && rTable.getObjectname().contains("_AGGLEVEL")) {
        log.log(Level.INFO, "  Ignoring directory checker actions for reference type: " + rTable.getObjectname());
        continue;
      }

      // Create the actions.
      iAction = createETLCheckers(iSet, iAction, rTable.getTypename().toLowerCase(), prop);
      iAction = createLogCheckers(iSet, iAction, rTable.getTypename() + "_RAW", rejectedProp, loaderProp);

    }
  }

  /**
   * Removes the directory check set and its actions.
   * 
   * @param topology
   * @throws Exception
   */
  public void removeSets(boolean topology) throws Exception {

    log.log(Level.INFO, "Removing directory checker Set: " + name);
    long setid = Utils.getSetId("Directory_Checker_" + name, version, techPackID, rock);
    if (setid == -1) {
      log.log(Level.INFO, "No sets found, no need to remove");
      return;
    }
    // basedir
    log.log(Level.INFO, "  Removing directory checker Action: base_" + etldataDir);
    Utils.removeAction("CreateDir_base_" + etldataDir, version, techPackID, setid, rock);

    // iqloaderBase
    log.log(Level.INFO, "  Removing directory checker Action: CreateDir_iqloaderBase_" + etldataDir);
    Utils.removeAction("CreateDir_iqloaderBase_" + baseLoaderlogDir, version, techPackID, setid, rock);

    // rejectedBAse
    log.log(Level.INFO, "  Removing directory checker Action: CreateDir_rejectedBase_" + etldataDir);
    Utils.removeAction("CreateDir_rejectedBase_" + baseRejectedDir, version, techPackID, setid, rock);

    // iqloader+techpack
    log.log(Level.INFO, "  Removing directory checker Action: CreateDir_iqloaderBase_Techpack_" + etldataDir);
    Utils.removeAction("CreateDir_iqloaderBase_Techpack_" + loaderlogDir, version, techPackID, setid, rock);

    // rejected+techpack
    log.log(Level.INFO, "  Removing directory checker Action: CreateDir_rejectedBase_Techpack_" + etldataDir);
    Utils.removeAction("CreateDir_rejectedBase_Techpack_" + rejectedDir, version, techPackID, setid, rock);

    if (topology) {

      // basedir
      log.log(Level.INFO, "  Removing directory checker Action: base_" + topologyBaseDir);
      Utils.removeAction("CreateDir_base_" + topologyBaseDir, version, techPackID, setid, rock);

      // meas
      log.log(Level.INFO, "  Removing directory checker Action: base_" + topologyBaseDir + pathSeparator + topologyName
          + pathSeparator);
      Utils.removeAction("CreateDir_base_" + topologyName, version, techPackID, setid, rock);

      Referencetable rt = new Referencetable(dwhrepRock);
      rt.setVersionid(versionid);
      ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock, rt);

      Iterator iTable = rtf.get().iterator();

      while (iTable.hasNext()) {

        int uPolicy = 0;
        Referencetable rtable = (Referencetable) iTable.next();

        if (rtable.getUpdate_policy() != null) {
          uPolicy = rtable.getUpdate_policy().intValue();
        }

        if (uPolicy == 1) {

          removeLogCheckers(rtable.getTypename(), setid);

        } else if ((uPolicy == 2 || uPolicy == 3)) {

          removeLogCheckers(rtable.getTypename(), setid);

          removeETLCheckers(rtable.getTypename().toLowerCase() + "_current_dc", setid);
          removeLogCheckers(rtable.getTypename() + "_CURRENT_DC_RAW", setid);

        } else {

        }
      }
    }

    // Query qm = session.createQuery("from MeasurementType where VERSIONID=?");
    // qm.setString(0,version.getVersionid());

    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(versionid);
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock, mt);

    Iterator iVer = mtf.get().iterator();

    // measurementtypes
    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();

      // Iterator iTable = types.getMeasurementTables().iterator();

      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(types.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock, mta);
      Iterator iTable = mtaf.get().iterator();

      // measurementtypes
      while (iTable.hasNext()) {

        Measurementtable table = (Measurementtable) iTable.next();

        if (table.getTablelevel().equalsIgnoreCase("raw")) {
          removeETLCheckers(types.getFoldername().toLowerCase(), setid);
          removeLogCheckers(types.getFoldername() + "_RAW", setid);
        }

        if (table.getTablelevel().equalsIgnoreCase("day"))
          removeLogCheckers(types.getFoldername() + "_DAY", setid);
        if (table.getTablelevel().equalsIgnoreCase("daybh"))
          removeLogCheckers(types.getFoldername() + "_DAYBH", setid);
        if (table.getTablelevel().equalsIgnoreCase("rankbh"))
          removeLogCheckers(types.getFoldername() + "_RANKBH", setid);

        if (types.getJoinable() != null && types.getJoinable().length() > 0) {
          removeLogCheckers(types.getFoldername() + "_PREV_RAW", setid);
        }

      }
    }

    // qm = session.createQuery("from ReferenceTable where VERSIONID=?");
    // qm.setString(0,version.getVersionid());

    Referencetable rt = new Referencetable(dwhrepRock);
    rt.setVersionid(versionid);
    ReferencetableFactory rtf = new ReferencetableFactory(dwhrepRock, rt);

    iVer = rtf.get().iterator();

    // ReferenceTables
    while (iVer.hasNext()) {

      Referencetable rTable = (Referencetable) iVer.next();
      removeETLCheckers(rTable.getTypename().toLowerCase(), setid);
      removeLogCheckers(rTable.getTypename() + "_RAW", setid);
    }

    Utils.removeSet("Directory_Checker_" + name, version, techPackID, rock);

  }

  private long createETLCheckers(long iSet, long iAction, String foldername, Properties prop) throws Exception {

    // meas
    log.log(Level.INFO, "  Creating directory checker Action: " + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername,
        etldataDir + pathSeparator + foldername + pathSeparator, propertyToString(prop)).insertToDB(rock);
    iAction++;

    // raw
    log.log(Level.INFO, "  Creating directory checker Action: raw_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_raw",
        etldataDir + pathSeparator + foldername + pathSeparator + "raw" + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    // joined
    log.log(Level.INFO, "  Creating directory checker Action: joined_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_joined",
        etldataDir + pathSeparator + foldername + pathSeparator + "joined" + pathSeparator, propertyToString(prop))
        .insertToDB(rock);
    iAction++;

    return iAction;

  }

  private void removeETLCheckers(String foldername, long setid) throws Exception {

    // meas
    log.log(Level.INFO, "  Removing directory checker Action: " + foldername);
    Utils.removeAction("CreateDir_" + foldername, version, techPackID, setid, rock);

    // raw
    log.log(Level.INFO, "  Removing directory checker Action: raw_" + foldername);
    Utils.removeAction("CreateDir_" + foldername + "_raw", version, techPackID, setid, rock);

    // joined
    log.log(Level.INFO, "  Removing directory checker Action: joined_" + foldername);
    Utils.removeAction("CreateDir_" + foldername + "_joined", version, techPackID, setid, rock);

  }

  private long createLogCheckers(long iSet, long iAction, String foldername, Properties rejProp, Properties loadProp)
      throws Exception {

    // loaderLog
    log.log(Level.INFO, "  Creating directory checker Action: loaderLog_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_loader",
        loaderlogDir + pathSeparator + foldername + pathSeparator, propertyToString(rejProp)).insertToDB(rock);
    iAction++;

    // rejected
    log.log(Level.INFO, "  Creating directory checker Action: rejected_" + foldername);
    createAction(iAction, "CreateDir", new Long(this.maxSetID + iSet).intValue(),
        new Long(this.maxActionID + iAction).intValue(), "CreateDir_" + foldername + "_rejected",
        rejectedDir + pathSeparator + foldername + pathSeparator, propertyToString(loadProp)).insertToDB(rock);
    iAction++;

    return iAction;

  }

  private void removeLogCheckers(String foldername, long setid) throws Exception {

    // loaderLog
    log.log(Level.INFO, "  Removing directory checker Action: loaderLog_" + foldername);
    Utils.removeAction("CreateDir_" + foldername + "_loader", version, techPackID, setid, rock);

    // rejected
    log.log(Level.INFO, "  Removing directory checker Action: rejected_" + foldername);
    Utils.removeAction("CreateDir_" + foldername + "_rejected", version, techPackID, setid, rock);
  }

  private SetGenerator createSet(String type, long iSet, String techPackName) throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME("Directory_Checker_" + techPackName);
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version);
    set.setCOLLECTION_SET_ID(Long.toString(techPackID));
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  private ActionGenerator createAction(long order, String type, long iSet, int iAct, String foldername, String where,
      String actionContents) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version);
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Long.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(foldername);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(where);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID("2");

    return loadAction;

  }

  /*
   * private long getSetMaxID(RockFactory rockFact) throws Exception {
   * 
   * Meta_collections coll = new Meta_collections(rockFact);
   * 
   * Meta_collectionsFactory dbCollections = new
   * Meta_collectionsFactory(rockFact, coll);
   * 
   * long largest = 0; Vector dbVec = dbCollections.get(); for (int i = 0; i <
   * dbVec.size(); i++) { Meta_collections collection = (Meta_collections)
   * dbVec.elementAt(i); if (largest <
   * collection.getCollection_id().longValue()) largest =
   * collection.getCollection_id().longValue(); }
   * 
   * return largest;
   * 
   * }
   * 
   * private long getActionMaxID(RockFactory rockFact) throws Exception {
   * 
   * Meta_transfer_actions act = new Meta_transfer_actions(rockFact);
   * 
   * Meta_transfer_actionsFactory dbCollections = new
   * Meta_transfer_actionsFactory(rockFact, act);
   * 
   * long largest = 0; Vector dbVec = dbCollections.get(); for (int i = 0; i <
   * dbVec.size(); i++) { Meta_transfer_actions action = (Meta_transfer_actions)
   * dbVec.elementAt(i); if (largest <
   * action.getTransfer_action_id().longValue()) largest =
   * action.getTransfer_action_id().longValue(); }
   * 
   * return largest;
   * 
   * }
   */
  protected Properties stringToProperty(String str) throws Exception {

    Properties prop = null;

    if (str != null && str.length() > 0) {
      ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }

    return prop;

  }

  protected String propertyToString(Properties prop) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    prop.store(baos, "");

    return baos.toString();
  }

  protected Properties createProperty(String str) throws Exception {

    Properties prop = new Properties();
    StringTokenizer st = new StringTokenizer(str, "=");
    String key = st.nextToken();
    String value = st.nextToken();
    prop.setProperty(key.trim(), value.trim());

    return prop;

  }

}
