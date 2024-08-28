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

public class Build implements Cloneable,RockDBObject  {

    private Long BUILDNUMBER;
    private String MODULE;
    private String AUTHOR;
    private Timestamp BUILDDATE;
    private String BUILDTAG;
    private String MODULETESTER;
    private Timestamp MODULETESTDATE;
    private String TESTRESULT;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "BUILDNUMBER"    ,"MODULE"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();

  /**
   * Constructor to initialize all objects to null
   */
  public Build(RockFactory rockFact) {
    this.rockFact = rockFact;
    this.newItem = true;
    
         this.BUILDNUMBER = null;
         this.MODULE = null;
         this.AUTHOR = null;
         this.BUILDDATE = null;
         this.BUILDTAG = null;
         this.MODULETESTER = null;
         this.MODULETESTDATE = null;
         this.TESTRESULT = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Build(RockFactory rockFact   ,Long BUILDNUMBER ,String MODULE ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.BUILDNUMBER = BUILDNUMBER;
            this.MODULE = MODULE;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Build o = (Build) it.next();

              this.BUILDNUMBER = o.getBuildnumber();
              this.MODULE = o.getModule();
              this.AUTHOR = o.getAuthor();
              this.BUILDDATE = o.getBuilddate();
              this.BUILDTAG = o.getBuildtag();
              this.MODULETESTER = o.getModuletester();
              this.MODULETESTDATE = o.getModuletestdate();
              this.TESTRESULT = o.getTestresult();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Build");
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
  public Build(RockFactory rockFact, Build whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Build o = (Build) it.next();
                this.BUILDNUMBER = o.getBuildnumber();
                this.MODULE = o.getModule();
                this.AUTHOR = o.getAuthor();
                this.BUILDDATE = o.getBuilddate();
                this.BUILDTAG = o.getBuildtag();
                this.MODULETESTER = o.getModuletester();
                this.MODULETESTDATE = o.getModuletestdate();
                this.TESTRESULT = o.getTestresult();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Build");
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
    return "Build";
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
  public int updateDB(boolean useTimestamp, Build whereObject) throws SQLException, RockException {
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
  public int deleteDB(Build whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Build.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }

   public Long getBuildnumber() { 
    return this.BUILDNUMBER;
  }
   public String getModule() { 
    return this.MODULE;
  }
   public String getAuthor() { 
    return this.AUTHOR;
  }
   public Timestamp getBuilddate() { 
    return this.BUILDDATE;
  }
   public String getBuildtag() { 
    return this.BUILDTAG;
  }
   public String getModuletester() { 
    return this.MODULETESTER;
  }
   public Timestamp getModuletestdate() { 
    return this.MODULETESTDATE;
  }
   public String getTestresult() { 
    return this.TESTRESULT;
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
     if (BUILDNUMBER == null)
      BUILDNUMBER = new Long (0);
     if (MODULE == null)
      MODULE = new String ("");
     if (AUTHOR == null)
      AUTHOR = new String ("");
     if (BUILDDATE == null)
      BUILDDATE = new Timestamp (0);
     if (BUILDTAG == null)
      BUILDTAG = new String ("");
     if (MODULETESTER == null)
      MODULETESTER = new String ("");
     if (MODULETESTDATE == null)
      MODULETESTDATE = new Timestamp (0);
     if (TESTRESULT == null)
      TESTRESULT = new String ("");
   }

   public void setBuildnumber(Long BUILDNUMBER) {
    modifiedColumns.add("BUILDNUMBER");
    this.BUILDNUMBER = BUILDNUMBER;
  }
   public void setModule(String MODULE) {
    modifiedColumns.add("MODULE");
    this.MODULE = MODULE;
  }
   public void setAuthor(String AUTHOR) {
    modifiedColumns.add("AUTHOR");
    this.AUTHOR = AUTHOR;
  }
   public void setBuilddate(Timestamp BUILDDATE) {
    modifiedColumns.add("BUILDDATE");
    this.BUILDDATE = BUILDDATE;
  }
   public void setBuildtag(String BUILDTAG) {
    modifiedColumns.add("BUILDTAG");
    this.BUILDTAG = BUILDTAG;
  }
   public void setModuletester(String MODULETESTER) {
    modifiedColumns.add("MODULETESTER");
    this.MODULETESTER = MODULETESTER;
  }
   public void setModuletestdate(Timestamp MODULETESTDATE) {
    modifiedColumns.add("MODULETESTDATE");
    this.MODULETESTDATE = MODULETESTDATE;
  }
   public void setTestresult(String TESTRESULT) {
    modifiedColumns.add("TESTRESULT");
    this.TESTRESULT = TESTRESULT;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Build o) {

         if ((((this.BUILDNUMBER == null) || (o.BUILDNUMBER == null)) && (this.BUILDNUMBER != o.BUILDNUMBER))
            || (((this.MODULE == null) || (o.MODULE == null)) && (this.MODULE != o.MODULE))
            || (((this.AUTHOR == null) || (o.AUTHOR == null)) && (this.AUTHOR != o.AUTHOR))
            || (((this.BUILDDATE == null) || (o.BUILDDATE == null)) && (this.BUILDDATE != o.BUILDDATE))
            || (((this.BUILDTAG == null) || (o.BUILDTAG == null)) && (this.BUILDTAG != o.BUILDTAG))
            || (((this.MODULETESTER == null) || (o.MODULETESTER == null)) && (this.MODULETESTER != o.MODULETESTER))
            || (((this.MODULETESTDATE == null) || (o.MODULETESTDATE == null)) && (this.MODULETESTDATE != o.MODULETESTDATE))
            || (((this.TESTRESULT == null) || (o.TESTRESULT == null)) && (this.TESTRESULT != o.TESTRESULT))
          ){
    return false;
    } else
         if ((((this.BUILDNUMBER != null) && (o.BUILDNUMBER != null)) && (this.BUILDNUMBER.equals(o.BUILDNUMBER) == false))
            || (((this.MODULE != null) && (o.MODULE != null)) && (this.MODULE.equals(o.MODULE) == false))
            || (((this.AUTHOR != null) && (o.AUTHOR != null)) && (this.AUTHOR.equals(o.AUTHOR) == false))
            || (((this.BUILDDATE != null) && (o.BUILDDATE != null)) && (this.BUILDDATE.equals(o.BUILDDATE) == false))
            || (((this.BUILDTAG != null) && (o.BUILDTAG != null)) && (this.BUILDTAG.equals(o.BUILDTAG) == false))
            || (((this.MODULETESTER != null) && (o.MODULETESTER != null)) && (this.MODULETESTER.equals(o.MODULETESTER) == false))
            || (((this.MODULETESTDATE != null) && (o.MODULETESTDATE != null)) && (this.MODULETESTDATE.equals(o.MODULETESTDATE) == false))
            || (((this.TESTRESULT != null) && (o.TESTRESULT != null)) && (this.TESTRESULT.equals(o.TESTRESULT) == false))
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
