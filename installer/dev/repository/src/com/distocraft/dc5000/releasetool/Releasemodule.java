package com.distocraft.dc5000.releasetool;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

public class Releasemodule implements Cloneable,RockDBObject  {

    private String RELEASEMODULE;
    private String RELEASESUBSYSTEM;
    private String VERSIONID;
    private Long BUILDNUMBER;
    private String RSTATE;
    private String DEV_VERSIONID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "RELEASEMODULE"    ,"VERSIONID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Releasemodule(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;
    
         this.RELEASEMODULE = null;
         this.RELEASESUBSYSTEM = null;
         this.VERSIONID = null;
         this.BUILDNUMBER = null;
         this.RSTATE = null;
         this.DEV_VERSIONID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Releasemodule(RockFactory rockFact   ,String RELEASEMODULE ,String VERSIONID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.RELEASEMODULE = RELEASEMODULE;
            this.VERSIONID = VERSIONID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Releasemodule o = (Releasemodule) it.next();

              this.RELEASEMODULE = o.getReleasemodule();
              this.RELEASESUBSYSTEM = o.getReleasesubsystem();
              this.VERSIONID = o.getVersionid();
              this.BUILDNUMBER = o.getBuildnumber();
              this.RSTATE = o.getRstate();
              this.DEV_VERSIONID = o.getDev_versionid();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Releasemodule");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  /**
   * Constructor to select the object according to whereObject from the db NO PRIMARY KEY DEFINED
   * 
   * @param whereObject
   *          the where part is constructed from this object
   * @exception SQLException
   */
  public Releasemodule(RockFactory rockFact, Releasemodule whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Releasemodule o = (Releasemodule) it.next();
                this.RELEASEMODULE = o.getReleasemodule();
                this.RELEASESUBSYSTEM = o.getReleasesubsystem();
                this.VERSIONID = o.getVersionid();
                this.BUILDNUMBER = o.getBuildnumber();
                this.RSTATE = o.getRstate();
                this.DEV_VERSIONID = o.getDev_versionid();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Releasemodule");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  public Set gimmeModifiedColumns(){   
    return modifiedColumns;  
  }

  public void cleanModifiedColumns(){   
    modifiedColumns.clear();  
  }

  /**
   * Method for getting the table name
   * 
   * @return String name of the corresponding table
   */
  public String getTableName() {
    return "Releasemodule";
  }

  /**
   * Update object contents into database PRIMARY KEY MUST BE DEFINED
   * 
   * @exception SQLException
   */
  public int updateDB() throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, true, null);
  }

  /**
   * Update object contents into database PRIMARY KEY MUST BE DEFINED
   * 
   * @param boolean
   *          useTimestamp if false, the timestamp is not used to compare if the data has been
   *          changed during the transaction
   * @exception SQLException
   */
  public int updateDB(boolean useTimestamp) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, true, null, useTimestamp);
  }

  /**
   * Update object contents into database NO PRIMARY KEY DEFINED
   * 
   * @param boolean
   *          useTimestamp if false, the timestamp is not used to compare if the data has been
   *          changed during the transaction
   * @param <this
   *          object type> whereObject the where part is constructed from this object
   * @exception SQLException
   */
  public int updateDB(boolean useTimestamp, Releasemodule whereObject) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.updateData(this, false, whereObject, useTimestamp);
  }

  /**
   * Insert object contents into database
   * 
   * @exception SQLException
   */
  public int insertDB() throws SQLException, RockException {
    this.newItem = false;
    return rockFact.insertData(this);
  }

  /**
   * Insert object contents into database
   * 
   * @param boolean
   *          useTimestamp if false, the timestamp is not used to compare if the data has been
   *          changed during the transaction
   * @param boolean
   *          useSequence if false the sequence columns don't get their values from db sequences
   * @exception SQLException
   */
  public int insertDB(boolean useTimestamp, boolean useSequence) throws SQLException, RockException {
    this.newItem = false;
    return rockFact.insertData(this, useTimestamp, useSequence);
  }

  /**
   * Delete object contents from database PRIMARY KEY MUST BE DEFINED
   * 
   * @exception SQLException
   */
  public int deleteDB() throws SQLException, RockException {
    this.newItem = true;
    return rockFact.deleteData(true, this);
  }

  /**
   * Delete object contents from database NO PRIMARY KEY DEFINED
   * 
   * @param <this
   *          object type> whereObject the where part is constructed from this object
   * @exception SQLException
   */
  public int deleteDB(Releasemodule whereObject) throws SQLException, RockException {
    this.newItem = true;
    return rockFact.deleteData(false, whereObject);
  }

  /**
   * Saves the data into the database
   * 
   * @exception SQLException
   */
  public void saveDB() throws SQLException, RockException {

    if (this.newItem) {
      insertDB();
    } else {
      if (isPrimaryDefined()) {
        rockFact.updateData(this, true, this, true);
      } else {
        throw new RockException("Cannot use rock.Releasemodule.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

   public String getReleasemodule() { 
    return this.RELEASEMODULE;
  }
   public String getReleasesubsystem() { 
    return this.RELEASESUBSYSTEM;
  }
   public String getVersionid() { 
    return this.VERSIONID;
  }
   public Long getBuildnumber() { 
    return this.BUILDNUMBER;
  }
   public String getRstate() { 
    return this.RSTATE;
  }
   public String getDev_versionid() { 
    return this.DEV_VERSIONID;
  }
 
  public String gettimeStampName() {
    return timeStampName;
  }

  public String[] getcolumnsAndSequences() {
    return columnsAndSequences;
  }

  public String[] getprimaryKeyNames() {
    return primaryKeyNames;
  }

  public RockFactory getRockFactory() {
    return this.rockFact;
  }

  public void removeNulls() {
     if (RELEASEMODULE == null)
      RELEASEMODULE = new String ("");
     if (RELEASESUBSYSTEM == null)
      RELEASESUBSYSTEM = new String ("");
     if (VERSIONID == null)
      VERSIONID = new String ("");
     if (BUILDNUMBER == null)
      BUILDNUMBER = new Long (0);
     if (RSTATE == null)
      RSTATE = new String ("");
     if (DEV_VERSIONID == null)
      DEV_VERSIONID = new String ("");
   }

   public void setReleasemodule(String RELEASEMODULE) {
    modifiedColumns.add("RELEASEMODULE");
    this.RELEASEMODULE = RELEASEMODULE;
  }
   public void setReleasesubsystem(String RELEASESUBSYSTEM) {
    modifiedColumns.add("RELEASESUBSYSTEM");
    this.RELEASESUBSYSTEM = RELEASESUBSYSTEM;
  }
   public void setVersionid(String VERSIONID) {
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }
   public void setBuildnumber(Long BUILDNUMBER) {
    modifiedColumns.add("BUILDNUMBER");
    this.BUILDNUMBER = BUILDNUMBER;
  }
   public void setRstate(String RSTATE) {
    modifiedColumns.add("RSTATE");
    this.RSTATE = RSTATE;
  }
   public void setDev_versionid(String DEV_VERSIONID) {
    modifiedColumns.add("DEV_VERSIONID");
    this.DEV_VERSIONID = DEV_VERSIONID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Releasemodule o) {

         if ((((this.RELEASEMODULE == null) || (o.RELEASEMODULE == null)) && (this.RELEASEMODULE != o.RELEASEMODULE))
            || (((this.RELEASESUBSYSTEM == null) || (o.RELEASESUBSYSTEM == null)) && (this.RELEASESUBSYSTEM != o.RELEASESUBSYSTEM))
            || (((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.BUILDNUMBER == null) || (o.BUILDNUMBER == null)) && (this.BUILDNUMBER != o.BUILDNUMBER))
            || (((this.RSTATE == null) || (o.RSTATE == null)) && (this.RSTATE != o.RSTATE))
            || (((this.DEV_VERSIONID == null) || (o.DEV_VERSIONID == null)) && (this.DEV_VERSIONID != o.DEV_VERSIONID))
          ){
    return false;
    } else
         if ((((this.RELEASEMODULE != null) && (o.RELEASEMODULE != null)) && (this.RELEASEMODULE.equals(o.RELEASEMODULE) == false))
            || (((this.RELEASESUBSYSTEM != null) && (o.RELEASESUBSYSTEM != null)) && (this.RELEASESUBSYSTEM.equals(o.RELEASESUBSYSTEM) == false))
            || (((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.BUILDNUMBER != null) && (o.BUILDNUMBER != null)) && (this.BUILDNUMBER.equals(o.BUILDNUMBER) == false))
            || (((this.RSTATE != null) && (o.RSTATE != null)) && (this.RSTATE.equals(o.RSTATE) == false))
            || (((this.DEV_VERSIONID != null) && (o.DEV_VERSIONID != null)) && (this.DEV_VERSIONID.equals(o.DEV_VERSIONID) == false))
          ){
    return false;
    } else {
      return true;
    }
  }
  
  /**
   * to enable a public clone method inherited from Object class (private method)
   */
  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException e) {
    }
    return o;
  }

  /**
   * Is the primakey defined for this table
   */
  public boolean isPrimaryDefined() {
    if (this.primaryKeyNames.length > 0)
      return true;
    else
      return false;
  }

  /**
   * Sets the member variables to Db default values
   */
  public void setDefaults() {

  }

  /**
   * Does the the object exist in the database
   * 
   * @return boolean true if exists else false.
   */
  public boolean existsDB() throws SQLException, RockException {
    RockResultSet results = rockFact.setSelectSQL(false, this);
    Iterator it = rockFact.getData(this, results);
    if (it.hasNext()) {
      results.close();
      return true;
    }
    results.close();
    return false;
  }

}
