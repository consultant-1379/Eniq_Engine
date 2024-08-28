package com.distocraft.dc5000.etl.rock;

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
import ssc.rockfactory.DataValidator;

public class Meta_execution_slot implements Cloneable,RockDBObject  {

    private String PROFILE_ID;
    private String SLOT_NAME;
    private String SLOT_ID;
    private String ACCEPTED_SET_TYPES;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "SLOT_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_execution_slot(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_execution_slot(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.PROFILE_ID = null;
         this.SLOT_NAME = null;
         this.SLOT_ID = null;
         this.ACCEPTED_SET_TYPES = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_execution_slot(RockFactory rockFact   ,String SLOT_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.SLOT_ID = SLOT_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_execution_slot o = (Meta_execution_slot) it.next();

              this.PROFILE_ID = o.getProfile_id();
              this.SLOT_NAME = o.getSlot_name();
              this.SLOT_ID = o.getSlot_id();
              this.ACCEPTED_SET_TYPES = o.getAccepted_set_types();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_execution_slot");
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
  public Meta_execution_slot(RockFactory rockFact, Meta_execution_slot whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_execution_slot o = (Meta_execution_slot) it.next();
                this.PROFILE_ID = o.getProfile_id();
                this.SLOT_NAME = o.getSlot_name();
                this.SLOT_ID = o.getSlot_id();
                this.ACCEPTED_SET_TYPES = o.getAccepted_set_types();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_execution_slot");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  public Set gimmeModifiedColumns(){   
    return modifiedColumns;  
  }

  public void setModifiedColumns(Set modifiedColumns){   
    this.modifiedColumns = modifiedColumns;  
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
    return "Meta_execution_slot";
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
  public int updateDB(boolean useTimestamp, Meta_execution_slot whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_execution_slot whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_execution_slot.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getProfile_id() { 
    return this.PROFILE_ID;
  }
   public String getSlot_name() { 
    return this.SLOT_NAME;
  }
   public String getSlot_id() { 
    return this.SLOT_ID;
  }
   public String getAccepted_set_types() { 
    return this.ACCEPTED_SET_TYPES;
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
     if (PROFILE_ID == null)
      PROFILE_ID = new String ("");
     if (SLOT_NAME == null)
      SLOT_NAME = new String ("");
     if (SLOT_ID == null)
      SLOT_ID = new String ("");
     if (ACCEPTED_SET_TYPES == null)
      ACCEPTED_SET_TYPES = new String ("");
   }

   public void setProfile_id(String PROFILE_ID) {
    if (validateData){
      DataValidator.validateData((Object)PROFILE_ID,"PROFILE_ID",12,38,0);
    }
    modifiedColumns.add("PROFILE_ID");
    this.PROFILE_ID = PROFILE_ID;
  }
   public void setSlot_name(String SLOT_NAME) {
    if (validateData){
      DataValidator.validateData((Object)SLOT_NAME,"SLOT_NAME",12,15,0);
    }
    modifiedColumns.add("SLOT_NAME");
    this.SLOT_NAME = SLOT_NAME;
  }
   public void setSlot_id(String SLOT_ID) {
    if (validateData){
      DataValidator.validateData((Object)SLOT_ID,"SLOT_ID",12,38,0);
    }
    modifiedColumns.add("SLOT_ID");
    this.SLOT_ID = SLOT_ID;
  }
   public void setAccepted_set_types(String ACCEPTED_SET_TYPES) {
    if (validateData){
      DataValidator.validateData((Object)ACCEPTED_SET_TYPES,"ACCEPTED_SET_TYPES",12,2000,0);
    }
    modifiedColumns.add("ACCEPTED_SET_TYPES");
    this.ACCEPTED_SET_TYPES = ACCEPTED_SET_TYPES;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_execution_slot o) {

         if ((((this.PROFILE_ID == null) || (o.PROFILE_ID == null)) && (this.PROFILE_ID != o.PROFILE_ID))
            || (((this.SLOT_NAME == null) || (o.SLOT_NAME == null)) && (this.SLOT_NAME != o.SLOT_NAME))
            || (((this.SLOT_ID == null) || (o.SLOT_ID == null)) && (this.SLOT_ID != o.SLOT_ID))
            || (((this.ACCEPTED_SET_TYPES == null) || (o.ACCEPTED_SET_TYPES == null)) && (this.ACCEPTED_SET_TYPES != o.ACCEPTED_SET_TYPES))
          ){
    return false;
    } else
         if ((((this.PROFILE_ID != null) && (o.PROFILE_ID != null)) && (this.PROFILE_ID.equals(o.PROFILE_ID) == false))
            || (((this.SLOT_NAME != null) && (o.SLOT_NAME != null)) && (this.SLOT_NAME.equals(o.SLOT_NAME) == false))
            || (((this.SLOT_ID != null) && (o.SLOT_ID != null)) && (this.SLOT_ID.equals(o.SLOT_ID) == false))
            || (((this.ACCEPTED_SET_TYPES != null) && (o.ACCEPTED_SET_TYPES != null)) && (this.ACCEPTED_SET_TYPES.equals(o.ACCEPTED_SET_TYPES) == false))
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

  
  /**
  * get columnSize
  * return 38
  */
  public static int getProfile_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getProfile_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getProfile_idSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 15
  */
  public static int getSlot_nameColumnSize() {
    
     return 15;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getSlot_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getSlot_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getSlot_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getSlot_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getSlot_idSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 2000
  */
  public static int getAccepted_set_typesColumnSize() {
    
     return 2000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAccepted_set_typesDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAccepted_set_typesSQLType() {
    
    return 12;   
  }
    
   public boolean isNewItem() {
    return newItem;
  }

  public void setNewItem(boolean newItem) {
    this.newItem = newItem;
  }

  public boolean isValidateData() {
    return validateData;
  }

  public void setValidateData(boolean validateData) {
    this.validateData = validateData;
  }

}
