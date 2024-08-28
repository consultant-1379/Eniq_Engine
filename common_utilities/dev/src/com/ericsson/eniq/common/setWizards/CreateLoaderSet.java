package com.ericsson.eniq.common.setWizards;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.ericsson.eniq.common.Utils;

/**
 * 
 * @author savinen Copyright Distocraft 2005
 * 
 *         $id$
 */
public class CreateLoaderSet implements SetCreator {

  private Logger log = Logger.getLogger(CreateLoaderSet.class.getName());

  protected String loaderTemplateName;

  protected String setTemplateName;

  protected String name;

  protected String version;

  protected RockFactory dwhrepRock;

  protected RockFactory rock;

  protected int techPackID;

  protected String templateName = "LoaderSQLXML.vm";

  protected String postLoadSQLTemplate = "postLoadSQL.vm";

  protected String updateDimSessionTemplate = "updateDimSession.vm";

  protected String temporaryOptionTemplate = "temporaryOption.vm";

  private String pathSeparator = "/";

  private String ignoredKeys = "";

  protected long maxSetID = 0;

  protected long maxActionID = 0;

  protected long maxSchedulingID = 0;

  protected boolean doSchedulings = false;

  protected String techPackName = "";

  protected String templateDir = "";

  private String versionid;

  /**
   * constructor
   * 
   * 
   */
  public CreateLoaderSet(String templateDir, String name, String version, String versionid, RockFactory dwhrepRock,
      RockFactory rock, int techPackID, String techPackName, boolean schedulings) throws Exception {

    this.templateDir = templateDir;
    this.name = name;
    this.version = version;
    this.versionid = versionid;
    this.dwhrepRock = dwhrepRock;
    this.rock = rock;
    this.techPackID = techPackID;

    this.maxSetID = Utils.getSetMaxID(rock) + 1;
    this.maxActionID = Utils.getActionMaxID(rock) + 1;
    this.maxSchedulingID = Utils.getScheduleMaxID(rock) + 1;
    this.doSchedulings = schedulings;
    this.techPackName = techPackName;
  }

  /**
   * 
   * Merges template and context
   * 
   * @param templateName
   * @param context
   * @return string contains the output of the merge
   * @throws Exception
   */
  public String merge(String templateName, VelocityContext context) throws Exception {

    StringWriter strWriter = new StringWriter();
    boolean isMergeOk = Velocity.mergeTemplate(templateDir + "/" + templateName, Velocity.ENCODING_DEFAULT, context,
        strWriter);
    log.finest("   Velocity Merge OK: " + isMergeOk);

    return strWriter.toString();

  }

  private Vector meastypes;

  private Map meascolumns = new HashMap();

  private Map meastables = new HashMap();

  protected String CreateSql(String mTableID) throws Exception {
    List measurementColumnList = new ArrayList();

    if (meastypes == null || meastypes.isEmpty()) {
      Measurementtype mt = new Measurementtype(dwhrepRock);
      mt.setVersionid(versionid);
      MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock, mt);
      meastypes = mtf.get();
    }

    Iterator iVer = meastypes.iterator();

    while (iVer.hasNext()) {

      Measurementtype types = (Measurementtype) iVer.next();

      if (meastables == null || !meastables.containsKey(types.getTypeid())) {
        Measurementtable mta = new Measurementtable(dwhrepRock);
        mta.setTypeid(types.getTypeid());
        MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock, mta);
        meastables.put(types.getTypeid(), mtaf.get());
      }

      Iterator iType = ((Vector) meastables.get(types.getTypeid())).iterator();

      while (iType.hasNext()) {

        Measurementtable tables = (Measurementtable) iType.next();

        if (meascolumns == null || !meascolumns.containsKey(tables.getMtableid())) {
          Measurementcolumn mc = new Measurementcolumn(dwhrepRock);
          mc.setMtableid(tables.getMtableid());
          MeasurementcolumnFactory mcf = new MeasurementcolumnFactory(dwhrepRock, mc);
          meascolumns.put(tables.getMtableid(), mcf.get());
        }

        Iterator iTable = ((Vector) meascolumns.get(tables.getMtableid())).iterator();

        while (iTable.hasNext()) {

          Measurementcolumn columns = (Measurementcolumn) iTable.next();
          if (tables.getMtableid().equals(mTableID)) {

            List sortList = new ArrayList();
            sortList.add(0, columns.getDataname());
            sortList.add(1, columns.getColnumber());
            measurementColumnList.add(sortList);

          }
        }
      }
    }

    class comp implements Comparator {

      public comp() {
      }

      public int compare(Object d1, Object d2) {

        List l1 = (List) d1;
        List l2 = (List) d2;

        Long i1 = (Long) l1.get(1);
        Long i2 = (Long) l2.get(1);
        return i1.compareTo(i2);
      }
    }

    Collections.sort(measurementColumnList, new comp());

    Vector vec = new Vector();
    Iterator iColl = measurementColumnList.iterator();
    while (iColl.hasNext()) {
      vec.add(((List) iColl.next()).get(0));
    }

    VelocityContext context = new VelocityContext();
    context.put("measurementColumn", vec);

    return merge(this.templateName, context);
  }

  /**
   * WRAN if CPP/RAN(=RNC)/RBS
   * @return
   */
  private boolean isWranTechPack() {
	boolean result = false;
	if( (this.techPackName != null) && (this.techPackName.endsWith("_E_CPP") || this.techPackName.endsWith("_E_RAN") || this.techPackName.endsWith("_E_RBS")) ) {
		result = true;
	}
	return result;
  }

private String getSQLJoinQuery(String typename, String mTableID) throws Exception {

    VelocityContext context = new VelocityContext();
    String result = merge("JOIN.vm", context);
    log.finer("   SQL: " + result);

    return result;

  }

  public void create(boolean skip) throws Exception {
    create(skip, null);
  }

  /**
   * Create the sets.
   * 
   * @param skip
   *          already existing sets will be skipped if true.
   * @param skiplist
   *          list of sets
   * @throws Exception
   */
  public void create(boolean skip, Vector<String> skiplist) throws Exception {

    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(versionid);
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock, mt);

    Iterator iType = mtf.get().iterator();

    long iSet = 0;
    long iAction = 0;
    long iScheduling = 0;
    String element = "";

    while (iType.hasNext()) {

      boolean join = false;

      Measurementtype measType = (Measurementtype) iType.next();

      if (measType.getJoinable() != null && measType.getJoinable().length() > 0) {
        join = true;
        ignoredKeys = measType.getJoinable();
      }

      Measurementkey mk = new Measurementkey(dwhrepRock);
      mk.setTypeid(measType.getTypeid());
      MeasurementkeyFactory mkf = new MeasurementkeyFactory(dwhrepRock, mk);
      Iterator keys = mkf.get().iterator();

      while (keys.hasNext()) {
        Measurementkey key = (Measurementkey) keys.next();

        if (key.getIselement() != null && key.getIselement().intValue() == 1)
          element = key.getDataname();
      }

      // Iterator iTable = types.getMeasurementTables().iterator();
      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(measType.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock, mta);
      Iterator measTableIter = mtaf.get().iterator();

      while (measTableIter.hasNext()) {

        Measurementtable measTable = (Measurementtable) measTableIter.next();

        String dir = "${ETLDATA_DIR}/" + measType.getTypename().toLowerCase() + this.pathSeparator + "raw"
            + this.pathSeparator;

        if (measTable.getTablelevel().equalsIgnoreCase("raw") || measTable.getTablelevel().equalsIgnoreCase("plain")) {

          int order = 0;

          if (skiplist != null
              && skiplist.contains(measTable.getBasetablename().substring(0,
                  measTable.getBasetablename().lastIndexOf("_")).toUpperCase())) {
            log.info("Skipped " + measType.getTypename());
            continue;
          }

          if (!(skip && Utils.isSet("Loader_" + measType.getTypename(), version, techPackID, rock))) {

            // create set
            log.info("Creating set Loader_" + measType.getTypename());
            createSet("Loader", new Long(this.maxSetID + iSet).intValue(), measType, measTable).insertToDB(rock);

            // temporary option
            log.info("  Creating action SQL_Execute_" + measType.getTypename());
            createAction("SQL_Execute_" + measType.getTypename(), order, "SQL Execute",
                new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                measTable, "", getTemporaryOptions()).insertToDB(rock);
            iAction++;
            order++;

            if (join) {

              // create unpartitioned loader
              log.info("  Creating action UnPartitioned_Loader_" + measType.getTypename());
              createAction("UnPartitioned_Loader_" + measType.getTypename(), order, "UnPartitioned Loader",
                  new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                  measTable, createPropertyStringUnpartitioned(measType, dir), CreateSql(measTable.getMtableid()))
                  .insertToDB(rock);
              iAction++;
              order++;

            } else {
              // create loader
              log.info("  Creating action Loader_" + measType.getTypename());
              // 20100114, eeoidiv, WRAN Counter Capicity IP: 263/159 41-FCP 103 8147
          	  // For WRAN techpacks (CPP,RNC,RBS), do not use a template.
              String createSql = null;
              if(this.isWranTechPack()) {
            	  	createSql = "";
            	  	log.finest("  For action Loader_" + measType.getTypename() + ", no SQL template as techpack is WRAN: " + this.techPackName);
              } else {
              		createSql = CreateSql(measTable.getMtableid());
              }
              createAction("Loader_" + measType.getTypename(), order, "Loader",
                  new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                  measTable, createPropertyString(measType, "raw"), createSql)
                  .insertToDB(rock);
              iAction++;
              order++;
            }

            // create joiner action
            if (join) {
              log.info("  Creating action SQLJoiner_" + measType.getTypename());
              String sqlJoinQuery = getSQLJoinQuery(measType.getTypename(), measTable.getMtableid());
              createAction("SQLJoiner_" + measType.getTypename(), order, "SQLJoiner",
                  new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                  measTable, createPropertyStringJoiner(measType), sqlJoinQuery).insertToDB(rock);
              iAction++;
              order++;
            }

            // Create duplicate check action. Only for version 5.2 and newer!
            if (templateDir.equalsIgnoreCase("5.0") || templateDir.equalsIgnoreCase("5.1")) {
              log.info("Action DuplicateCheck not created because of selected 5.0 or 5.1 versions.");
            } else {
              log.info("  Creating action DuplicateCheck_" + measType.getTypename());
              String duplicateCheckActionContext = getDuplicateCheckQuery();
              createAction("DuplicateCheck_" + measType.getTypename(), order, "DuplicateCheck",
                  new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                  measTable, "", duplicateCheckActionContext).insertToDB(rock);
              iAction++;
              order++;
            }

            // UpdateDimSession
            Properties uds = getUpdateDimSession(element);
            log.info("  Creating action UpdateDimSession_" + measType.getTypename());
            createAction("UpdateDimSession_" + measType.getTypename(), order, "UpdateDimSession",
                new Long(this.maxSetID + iSet).intValue(), new Long(this.maxActionID + iAction).intValue(), measType,
                measTable, Utils.propertyToString(uds), "").insertToDB(rock);
            iAction++;

            if (doSchedulings) {

              // create scheduling
              String holdFlag = "N";
              String name = "Loader_" + measType.getTypename();

              log.info("  Creating Scheduling " + name);

              createWaitSchedule(new Long(this.maxSchedulingID + iScheduling).intValue(), this.techPackID,
                  new Long(this.maxSetID + iSet).intValue(), name, holdFlag).insertToDB(this.rock);
              iScheduling++;
            }

            iSet++;

          } else {
            log.info("Skipped existing set Loader_" + measType.getTypename());
          }

        }
      }
    }
  }

  /**
   * Removes the created sets.
   * 
   * @throws Exception
   */
  public void removeSets() throws Exception {
    removeSets(null);
  }

  /**
   * Removes the created sets.
   * 
   * @param skiplist
   *          The list of measurement and reference types to be skipped, i.e.
   *          not removed.
   * @throws Exception
   */
  public void removeSets(Vector<String> skiplist) throws Exception {

    Measurementtype mt = new Measurementtype(dwhrepRock);
    mt.setVersionid(versionid);
    MeasurementtypeFactory mtf = new MeasurementtypeFactory(dwhrepRock, mt);

    Iterator iType = mtf.get().iterator();

    while (iType.hasNext()) {

      boolean join = false;

      Measurementtype measType = (Measurementtype) iType.next();

      if (measType.getJoinable() != null && measType.getJoinable().length() > 0) {
        join = true;
        ignoredKeys = measType.getJoinable();
      }

      Measurementtable mta = new Measurementtable(dwhrepRock);
      mta.setTypeid(measType.getTypeid());
      MeasurementtableFactory mtaf = new MeasurementtableFactory(dwhrepRock, mta);
      Iterator measTableIter = mtaf.get().iterator();

      while (measTableIter.hasNext()) {

        Measurementtable measTable = (Measurementtable) measTableIter.next();

        // Skip the remove in case there is a match in the skip list.
        if (skiplist != null
            && skiplist.contains(measTable.getBasetablename().substring(0,
                measTable.getBasetablename().lastIndexOf("_")).toUpperCase())) {
          log.info("Skipped set " + measType.getTypename());
          continue;

        }
        if (measTable.getTablelevel().equalsIgnoreCase("raw") || measTable.getTablelevel().equalsIgnoreCase("plain")) {

          // Remove set
          log.info("Removing set Loader_" + measType.getTypename());
          long setid = Utils.getSetId("Loader" + "_" + measType.getTypename(), version, techPackID, rock);
          if (setid == -1) {
            log.info("No sets found, no need to remove");
            continue;
          }

          // Temporary option
          log.info("  Removing action SQL_Execute_" + measType.getTypename());
          Utils.removeAction("SQL_Execute_" + measType.getTypename(), version, techPackID, setid, rock);

          // Remove loader, based on joinable value
          if (join) {

            // Remove unpartitioned loader
            log.info("  Removing action UnPartitioned_Loader_" + measType.getTypename());
            Utils.removeAction("UnPartitioned_Loader_" + measType.getTypename(), version, techPackID, setid, rock);

          } else {

            // Remove loader
            log.info("  Removing action Loader_" + measType.getTypename());
            Utils.removeAction("Loader_" + measType.getTypename(), version, techPackID, setid, rock);
          }

          // Remove joiner action
          if (join) {
            log.info("  Removing action SQLJoiner_" + measType.getTypename());
            Utils.removeAction("SQLJoiner_" + measType.getTypename(), version, techPackID, setid, rock);

          }

          // Remove duplicate check action. Only for version 5.2 and newer!
          if (templateDir.equalsIgnoreCase("5.0") || templateDir.equalsIgnoreCase("5.1")) {
            log.info("Action DuplicateCheck not Removed because of selected 5.0 or 5.1 versions.");
          } else {
            log.info("  Removing action DuplicateCheck_" + measType.getTypename());
            Utils.removeAction("DuplicateCheck_" + measType.getTypename(), version, techPackID, setid, rock);
          }

          // Remove UpdateDimSession
          log.info("  Removing action UpdateDimSession_" + measType.getTypename());
          Utils.removeAction("UpdateDimSession_" + measType.getTypename(), version, techPackID, setid, rock);

          // Remove scheduling
          String name = "Loader_" + measType.getTypename();
          log.info("  Removing Scheduling " + name);
          Utils.removeScheduling(name, version, techPackID, setid, rock);

          // Remove set
          Utils.removeSet("Loader" + "_" + measType.getTypename(), version, techPackID, rock);
        }
      }
    }
  }

  protected String getPostLoadSQL(String baseTableName) {

    try {

      VelocityContext context = new VelocityContext();
      context.put("basetablename", baseTableName);

      context.toString();

      return merge(postLoadSQLTemplate, context);

    } catch (Exception e) {
      log.log(Level.WARNING, "Template " + postLoadSQLTemplate + " not found.", e);
    }

    return "";
  }

  protected String getTemporaryOptions() {

    try {

      VelocityContext context = new VelocityContext();
      return merge(temporaryOptionTemplate, context);

    } catch (Exception e) {
      log.log(Level.WARNING, "Template " + temporaryOptionTemplate + " not found.", e);
    }

    return "";
  }

  protected Properties getUpdateDimSession(String element) {

    try {

      VelocityContext context = new VelocityContext();
      context.put("element", element);
      return Utils.createProperty(merge(updateDimSessionTemplate, context));

    } catch (Exception e) {
      log.log(Level.WARNING, "Template " + updateDimSessionTemplate + " not found.", e);
    }

    return null;
  }

  protected SetGenerator createSet(String type, long iSet, Measurementtype types, Measurementtable mTable)
      throws Exception {

    SetGenerator set = new SetGenerator();
    set.setCOLLECTION_NAME(type + "_" + types.getTypename());
    set.setCOLLECTION_ID(Long.toString(iSet));
    set.setMAX_ERRORS("0");
    set.setMAX_FK_ERRORS("0");
    set.setMAX_COL_LIMIT_ERRORS("0");
    set.setCHECK_FK_ERROR_FLAG("N");
    set.setCHECK_COL_LIMITS_FLAG("N");
    set.setVERSION_NUMBER(version);
    set.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    set.setPRIORITY("0");
    set.setQUEUE_TIME_LIMIT("30");
    set.setENABLED_FLAG("Y");
    set.setSETTYPE(type);
    set.setFOLDABLE_FLAG("Y");
    set.setHOLD_FLAG("N");

    return set;

  }

  protected ActionGenerator createAction(String name, long order, String type, long iSet, long iAct,
      Measurementtype types, Measurementtable mTable, String whereClause, String actionContents) throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version);
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(whereClause);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID("2");

    return loadAction;

  }

  protected ActionGenerator createAction(String name, long order, String type, long iSet, long iAct,
      Measurementtype types, Measurementtable mTable, String whereClause, String actionContents, String conID)
      throws Exception {

    ActionGenerator loadAction = new ActionGenerator();
    loadAction.setVERSION_NUMBER(version);
    loadAction.setTRANSFER_ACTION_ID(Long.toString(iAct));
    loadAction.setCOLLECTION_ID(Long.toString(iSet));
    loadAction.setCOLLECTION_SET_ID(Integer.toString(techPackID));
    loadAction.setACTION_TYPE(type);
    loadAction.setTRANSFER_ACTION_NAME(name);
    loadAction.setORDER_BY_NO(Long.toString(order));
    loadAction.setWHERE_CLAUSE(whereClause);
    loadAction.setACTION_CONTENTS(actionContents);
    loadAction.setENABLED_FLAG("Y");
    loadAction.setCONNECTION_ID(conID);

    return loadAction;

  }

  protected ScheduleGenerator createWaitSchedule(long scheduleID, long techpackID, long setID, String name,
      String holdFlag) throws Exception {

    ScheduleGenerator schedule = new ScheduleGenerator();

    schedule.setVERSION_NUMBER(this.version);
    schedule.setID(new Long(scheduleID));
    schedule.setEXECUTION_TYPE("wait");
    schedule.setCOLLECTION_SET_ID(new Long(techpackID));
    schedule.setCOLLECTION_ID(new Long(setID));
    schedule.setNAME(name);
    schedule.setHOLD_FLAG(holdFlag);

    return schedule;
  }

  protected String createPropertyStringJoiner(Measurementtype types) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("objName", types.getTypename());
    prop.setProperty("typeName", types.getTypename() + "_RAW");
    prop.setProperty("versionid", versionid);
    prop.setProperty("ignoredKeys", ignoredKeys);
    prop.setProperty("prevTableName", types.getTypename() + "_PREV");

    return Utils.propertyToString(prop);
  }

  protected String createPropertyStringUnpartitioned(Measurementtype types, String dir) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("tablename", types.getTypename() + "_PREV");
    prop.setProperty("versionid", version);
    prop.setProperty("dir", dir);
    prop.setProperty("techpack", techPackName);
    prop.setProperty("pattern", ".+");

    return Utils.propertyToString(prop);
  }

  protected String createPropertyString(Measurementtype types, String tailDir) throws Exception {
    Properties prop = new Properties();

    prop.setProperty("tablename", types.getTypename());
    prop.setProperty("techpack", this.techPackName);
    prop.setProperty("taildir", tailDir);
    prop.setProperty("dateformat", "yyyy-MM-dd");

    return Utils.propertyToString(prop);
  }

  /**
   * This function returns the SQL query template (in string format) used in
   * duplicate checking.
   * 
   * @return String containing velocity context template.
   */
  protected String getDuplicateCheckQuery() {
    return "";
  }

}
