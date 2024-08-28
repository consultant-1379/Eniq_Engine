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

public class Releasesubsystem implements Cloneable,RockDBObject  {

    private String RELEASESUBSYSTEM;
    private String VERSIONID;
    private String RELEASETYPE;
    private Timestamp RELEASEDATE;
    private String VERSION;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "RELEASESUBSYSTEM"    ,"VERSIONID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Releasesubsystem(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;
    
         this.RELEASESUBSYSTEM = null;
         this.VERSIONID = null;
         this.RELEASETYPE = null;
         this.RELEASEDATE = null;
         this.VERSION = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Releasesubsystem(RockFactory rockFact   ,String RELEASESUBSYSTEM ,String VERSIONID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.RELEASESUBSYSTEM = RELEASESUBSYSTEM;
            this.VERSIONID = VERSIONID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Releasesubsystem o = (Releasesubsystem) it.next();

              this.RELEASESUBSYSTEM = o.getReleasesubsystem();
              this.VERSIONID = o.getVersionid();
              this.RELEASETYPE = o.getReleasetype();
              this.RELEASEDATE = o.getReleasedate();
              this.VERSION = o.getVersion();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Releasesubsystem");
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
  public Releasesubsystem(RockFactory rockFact, Releasesubsystem whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Releasesubsystem o = (Releasesubsystem) it.next();
                this.RELEASESUBSYSTEM = o.getReleasesubsystem();
                this.VERSIONID = o.getVersionid();
                this.RELEASETYPE = o.getReleasetype();
                this.RELEASEDATE = o.getReleasedate();
                this.VERSION = o.getVersion();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Releasesubsystem");
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
    return "Releasesubsystem";
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
  public int updateDB(boolean useTimestamp, Releasesubsystem whereObject) throws SQLException, RockException {
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
  public int deleteDB(Releasesubsystem whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Releasesubsystem.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

   public String getReleasesubsystem() { 
    return this.RELEASESUBSYSTEM;
  }
   public String getVersionid() { 
    return this.VERSIONID;
  }
   public String getReleasetype() { 
    return this.RELEASETYPE;
  }
   public Timestamp getReleasedate() { 
    return this.RELEASEDATE;
  }
   public String getVersion() { 
    return this.VERSION;
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
     if (RELEASESUBSYSTEM == null)
      RELEASESUBSYSTEM = new String ("");
     if (VERSIONID == null)
      VERSIONID = new String ("");
     if (RELEASETYPE == null)
      RELEASETYPE = new String ("");
     if (RELEASEDATE == null)
      RELEASEDATE = new Timestamp (0);
     if (VERSION == null)
      VERSION = new String ("");
   }

   public void setReleasesubsystem(String RELEASESUBSYSTEM) {
    modifiedColumns.add("RELEASESUBSYSTEM");
    this.RELEASESUBSYSTEM = RELEASESUBSYSTEM;
  }
   public void setVersionid(String VERSIONID) {
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }
   public void setReleasetype(String RELEASETYPE) {
    modifiedColumns.add("RELEASETYPE");
    this.RELEASETYPE = RELEASETYPE;
  }
   public void setReleasedate(Timestamp RELEASEDATE) {
    modifiedColumns.add("RELEASEDATE");
    this.RELEASEDATE = RELEASEDATE;
  }
   public void setVersion(String VERSION) {
    modifiedColumns.add("VERSION");
    this.VERSION = VERSION;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Releasesubsystem o) {

         if ((((this.RELEASESUBSYSTEM == null) || (o.RELEASESUBSYSTEM == null)) && (this.RELEASESUBSYSTEM != o.RELEASESUBSYSTEM))
            || (((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.RELEASETYPE == null) || (o.RELEASETYPE == null)) && (this.RELEASETYPE != o.RELEASETYPE))
            || (((this.RELEASEDATE == null) || (o.RELEASEDATE == null)) && (this.RELEASEDATE != o.RELEASEDATE))
            || (((this.VERSION == null) || (o.VERSION == null)) && (this.VERSION != o.VERSION))
          ){
    return false;
    } else
         if ((((this.RELEASESUBSYSTEM != null) && (o.RELEASESUBSYSTEM != null)) && (this.RELEASESUBSYSTEM.equals(o.RELEASESUBSYSTEM) == false))
            || (((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.RELEASETYPE != null) && (o.RELEASETYPE != null)) && (this.RELEASETYPE.equals(o.RELEASETYPE) == false))
            || (((this.RELEASEDATE != null) && (o.RELEASEDATE != null)) && (this.RELEASEDATE.equals(o.RELEASEDATE) == false))
            || (((this.VERSION != null) && (o.VERSION != null)) && (this.VERSION.equals(o.VERSION) == false))
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
