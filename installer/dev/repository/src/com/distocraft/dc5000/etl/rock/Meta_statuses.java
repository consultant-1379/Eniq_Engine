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

public class Meta_statuses implements Cloneable,RockDBObject  {

    private Long ID;
    private String STATUS_DESCRIPTION;
    private Timestamp LAST_UPDATED;
    private String VERSION_NUMBER;
    private Long COLLECTION_SET_ID;
    private Long COLLECTION_ID;
    private Long TRANSFER_BATCH_ID;
    private Long TRANSFER_ACTION_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_statuses(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_statuses(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.ID = null;
         this.STATUS_DESCRIPTION = null;
         this.LAST_UPDATED = null;
         this.VERSION_NUMBER = null;
         this.COLLECTION_SET_ID = null;
         this.COLLECTION_ID = null;
         this.TRANSFER_BATCH_ID = null;
         this.TRANSFER_ACTION_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_statuses(RockFactory rockFact   ,Long ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.ID = ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_statuses o = (Meta_statuses) it.next();

              this.ID = o.getId();
              this.STATUS_DESCRIPTION = o.getStatus_description();
              this.LAST_UPDATED = o.getLast_updated();
              this.VERSION_NUMBER = o.getVersion_number();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.TRANSFER_BATCH_ID = o.getTransfer_batch_id();
              this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_statuses");
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
  public Meta_statuses(RockFactory rockFact, Meta_statuses whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_statuses o = (Meta_statuses) it.next();
                this.ID = o.getId();
                this.STATUS_DESCRIPTION = o.getStatus_description();
                this.LAST_UPDATED = o.getLast_updated();
                this.VERSION_NUMBER = o.getVersion_number();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.TRANSFER_BATCH_ID = o.getTransfer_batch_id();
                this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_statuses");
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
    return "Meta_statuses";
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
  public int updateDB(boolean useTimestamp, Meta_statuses whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_statuses whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_statuses.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getId() { 
    return this.ID;
  }
   public String getStatus_description() { 
    return this.STATUS_DESCRIPTION;
  }
   public Timestamp getLast_updated() { 
    return this.LAST_UPDATED;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getCollection_set_id() { 
    return this.COLLECTION_SET_ID;
  }
   public Long getCollection_id() { 
    return this.COLLECTION_ID;
  }
   public Long getTransfer_batch_id() { 
    return this.TRANSFER_BATCH_ID;
  }
   public Long getTransfer_action_id() { 
    return this.TRANSFER_ACTION_ID;
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
     if (ID == null)
      ID = new Long (0);
     if (STATUS_DESCRIPTION == null)
      STATUS_DESCRIPTION = new String ("");
     if (LAST_UPDATED == null)
      LAST_UPDATED = new Timestamp (0);
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (TRANSFER_BATCH_ID == null)
      TRANSFER_BATCH_ID = new Long (0);
     if (TRANSFER_ACTION_ID == null)
      TRANSFER_ACTION_ID = new Long (0);
   }

   public void setId(Long ID) {
    if (validateData){
      DataValidator.validateData((Object)ID,"ID",2,38,0);
    }
    modifiedColumns.add("ID");
    this.ID = ID;
  }
   public void setStatus_description(String STATUS_DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)STATUS_DESCRIPTION,"STATUS_DESCRIPTION",12,32000,0);
    }
    modifiedColumns.add("STATUS_DESCRIPTION");
    this.STATUS_DESCRIPTION = STATUS_DESCRIPTION;
  }
   public void setLast_updated(Timestamp LAST_UPDATED) {
    if (validateData){
      DataValidator.validateData((Object)LAST_UPDATED,"LAST_UPDATED",93,23,0);
    }
    modifiedColumns.add("LAST_UPDATED");
    this.LAST_UPDATED = LAST_UPDATED;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setCollection_set_id(Long COLLECTION_SET_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_SET_ID,"COLLECTION_SET_ID",2,38,0);
    }
    modifiedColumns.add("COLLECTION_SET_ID");
    this.COLLECTION_SET_ID = COLLECTION_SET_ID;
  }
   public void setCollection_id(Long COLLECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_ID,"COLLECTION_ID",2,38,0);
    }
    modifiedColumns.add("COLLECTION_ID");
    this.COLLECTION_ID = COLLECTION_ID;
  }
   public void setTransfer_batch_id(Long TRANSFER_BATCH_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSFER_BATCH_ID,"TRANSFER_BATCH_ID",2,38,0);
    }
    modifiedColumns.add("TRANSFER_BATCH_ID");
    this.TRANSFER_BATCH_ID = TRANSFER_BATCH_ID;
  }
   public void setTransfer_action_id(Long TRANSFER_ACTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSFER_ACTION_ID,"TRANSFER_ACTION_ID",2,38,0);
    }
    modifiedColumns.add("TRANSFER_ACTION_ID");
    this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_statuses o) {

         if ((((this.ID == null) || (o.ID == null)) && (this.ID != o.ID))
            || (((this.STATUS_DESCRIPTION == null) || (o.STATUS_DESCRIPTION == null)) && (this.STATUS_DESCRIPTION != o.STATUS_DESCRIPTION))
            || (((this.LAST_UPDATED == null) || (o.LAST_UPDATED == null)) && (this.LAST_UPDATED != o.LAST_UPDATED))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.TRANSFER_BATCH_ID == null) || (o.TRANSFER_BATCH_ID == null)) && (this.TRANSFER_BATCH_ID != o.TRANSFER_BATCH_ID))
            || (((this.TRANSFER_ACTION_ID == null) || (o.TRANSFER_ACTION_ID == null)) && (this.TRANSFER_ACTION_ID != o.TRANSFER_ACTION_ID))
          ){
    return false;
    } else
         if ((((this.ID != null) && (o.ID != null)) && (this.ID.equals(o.ID) == false))
            || (((this.STATUS_DESCRIPTION != null) && (o.STATUS_DESCRIPTION != null)) && (this.STATUS_DESCRIPTION.equals(o.STATUS_DESCRIPTION) == false))
            || (((this.LAST_UPDATED != null) && (o.LAST_UPDATED != null)) && (this.LAST_UPDATED.equals(o.LAST_UPDATED) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.TRANSFER_BATCH_ID != null) && (o.TRANSFER_BATCH_ID != null)) && (this.TRANSFER_BATCH_ID.equals(o.TRANSFER_BATCH_ID) == false))
            || (((this.TRANSFER_ACTION_ID != null) && (o.TRANSFER_ACTION_ID != null)) && (this.TRANSFER_ACTION_ID.equals(o.TRANSFER_ACTION_ID) == false))
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
  public static int getIdColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getIdDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getIdSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getStatus_descriptionColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getStatus_descriptionDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getStatus_descriptionSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 23
  */
  public static int getLast_updatedColumnSize() {
    
     return 23;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getLast_updatedDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 93
  */
  public static int getLast_updatedSQLType() {
    
    return 93;   
  }
    
 
  /**
  * get columnSize
  * return 32
  */
  public static int getVersion_numberColumnSize() {
    
     return 32;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getVersion_numberDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getVersion_numberSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getCollection_set_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCollection_set_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getCollection_set_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getCollection_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCollection_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getCollection_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getTransfer_batch_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTransfer_batch_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTransfer_batch_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getTransfer_action_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTransfer_action_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTransfer_action_idSQLType() {
    
    return 2;   
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
