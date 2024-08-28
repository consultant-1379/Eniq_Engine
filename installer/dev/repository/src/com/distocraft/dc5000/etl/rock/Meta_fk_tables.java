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

public class Meta_fk_tables implements Cloneable,RockDBObject  {

    private Long MAX_ERRORS;
    private String VERSION_NUMBER;
    private String WHERE_CLAUSE;
    private String FILTER_ERRORS_FLAG;
    private String REPLACE_ERRORS_FLAG;
    private String REPLACE_ERRORS_WITH;
    private Long COLLECTION_SET_ID;
    private Long COLLECTION_ID;
    private Long TRANSFER_ACTION_ID;
    private Long CONNECTION_ID;
    private Long TABLE_ID;
    private Long TARGET_TABLE_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSION_NUMBER"    ,"COLLECTION_SET_ID"    ,"COLLECTION_ID"    ,"TRANSFER_ACTION_ID"    ,"CONNECTION_ID"    ,"TABLE_ID"    ,"TARGET_TABLE_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_fk_tables(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_fk_tables(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.MAX_ERRORS = null;
         this.VERSION_NUMBER = null;
         this.WHERE_CLAUSE = null;
         this.FILTER_ERRORS_FLAG = null;
         this.REPLACE_ERRORS_FLAG = null;
         this.REPLACE_ERRORS_WITH = null;
         this.COLLECTION_SET_ID = null;
         this.COLLECTION_ID = null;
         this.TRANSFER_ACTION_ID = null;
         this.CONNECTION_ID = null;
         this.TABLE_ID = null;
         this.TARGET_TABLE_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_fk_tables(RockFactory rockFact   ,String VERSION_NUMBER ,Long COLLECTION_SET_ID ,Long COLLECTION_ID ,Long TRANSFER_ACTION_ID ,Long CONNECTION_ID ,Long TABLE_ID ,Long TARGET_TABLE_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSION_NUMBER = VERSION_NUMBER;
            this.COLLECTION_SET_ID = COLLECTION_SET_ID;
            this.COLLECTION_ID = COLLECTION_ID;
            this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
            this.CONNECTION_ID = CONNECTION_ID;
            this.TABLE_ID = TABLE_ID;
            this.TARGET_TABLE_ID = TARGET_TABLE_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_fk_tables o = (Meta_fk_tables) it.next();

              this.MAX_ERRORS = o.getMax_errors();
              this.VERSION_NUMBER = o.getVersion_number();
              this.WHERE_CLAUSE = o.getWhere_clause();
              this.FILTER_ERRORS_FLAG = o.getFilter_errors_flag();
              this.REPLACE_ERRORS_FLAG = o.getReplace_errors_flag();
              this.REPLACE_ERRORS_WITH = o.getReplace_errors_with();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
              this.CONNECTION_ID = o.getConnection_id();
              this.TABLE_ID = o.getTable_id();
              this.TARGET_TABLE_ID = o.getTarget_table_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_fk_tables");
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
  public Meta_fk_tables(RockFactory rockFact, Meta_fk_tables whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_fk_tables o = (Meta_fk_tables) it.next();
                this.MAX_ERRORS = o.getMax_errors();
                this.VERSION_NUMBER = o.getVersion_number();
                this.WHERE_CLAUSE = o.getWhere_clause();
                this.FILTER_ERRORS_FLAG = o.getFilter_errors_flag();
                this.REPLACE_ERRORS_FLAG = o.getReplace_errors_flag();
                this.REPLACE_ERRORS_WITH = o.getReplace_errors_with();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
                this.CONNECTION_ID = o.getConnection_id();
                this.TABLE_ID = o.getTable_id();
                this.TARGET_TABLE_ID = o.getTarget_table_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_fk_tables");
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
    return "Meta_fk_tables";
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
  public int updateDB(boolean useTimestamp, Meta_fk_tables whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_fk_tables whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_fk_tables.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getMax_errors() { 
    return this.MAX_ERRORS;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public String getWhere_clause() { 
    return this.WHERE_CLAUSE;
  }
   public String getFilter_errors_flag() { 
    return this.FILTER_ERRORS_FLAG;
  }
   public String getReplace_errors_flag() { 
    return this.REPLACE_ERRORS_FLAG;
  }
   public String getReplace_errors_with() { 
    return this.REPLACE_ERRORS_WITH;
  }
   public Long getCollection_set_id() { 
    return this.COLLECTION_SET_ID;
  }
   public Long getCollection_id() { 
    return this.COLLECTION_ID;
  }
   public Long getTransfer_action_id() { 
    return this.TRANSFER_ACTION_ID;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public Long getTable_id() { 
    return this.TABLE_ID;
  }
   public Long getTarget_table_id() { 
    return this.TARGET_TABLE_ID;
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
     if (MAX_ERRORS == null)
      MAX_ERRORS = new Long (0);
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (WHERE_CLAUSE == null)
      WHERE_CLAUSE = new String ("");
     if (FILTER_ERRORS_FLAG == null)
      FILTER_ERRORS_FLAG = new String ("");
     if (REPLACE_ERRORS_FLAG == null)
      REPLACE_ERRORS_FLAG = new String ("");
     if (REPLACE_ERRORS_WITH == null)
      REPLACE_ERRORS_WITH = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (TRANSFER_ACTION_ID == null)
      TRANSFER_ACTION_ID = new Long (0);
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (TABLE_ID == null)
      TABLE_ID = new Long (0);
     if (TARGET_TABLE_ID == null)
      TARGET_TABLE_ID = new Long (0);
   }

   public void setMax_errors(Long MAX_ERRORS) {
    if (validateData){
      DataValidator.validateData((Object)MAX_ERRORS,"MAX_ERRORS",2,38,0);
    }
    modifiedColumns.add("MAX_ERRORS");
    this.MAX_ERRORS = MAX_ERRORS;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setWhere_clause(String WHERE_CLAUSE) {
    if (validateData){
      DataValidator.validateData((Object)WHERE_CLAUSE,"WHERE_CLAUSE",12,2000,0);
    }
    modifiedColumns.add("WHERE_CLAUSE");
    this.WHERE_CLAUSE = WHERE_CLAUSE;
  }
   public void setFilter_errors_flag(String FILTER_ERRORS_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)FILTER_ERRORS_FLAG,"FILTER_ERRORS_FLAG",12,1,0);
    }
    modifiedColumns.add("FILTER_ERRORS_FLAG");
    this.FILTER_ERRORS_FLAG = FILTER_ERRORS_FLAG;
  }
   public void setReplace_errors_flag(String REPLACE_ERRORS_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)REPLACE_ERRORS_FLAG,"REPLACE_ERRORS_FLAG",12,1,0);
    }
    modifiedColumns.add("REPLACE_ERRORS_FLAG");
    this.REPLACE_ERRORS_FLAG = REPLACE_ERRORS_FLAG;
  }
   public void setReplace_errors_with(String REPLACE_ERRORS_WITH) {
    if (validateData){
      DataValidator.validateData((Object)REPLACE_ERRORS_WITH,"REPLACE_ERRORS_WITH",12,30,0);
    }
    modifiedColumns.add("REPLACE_ERRORS_WITH");
    this.REPLACE_ERRORS_WITH = REPLACE_ERRORS_WITH;
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
   public void setTransfer_action_id(Long TRANSFER_ACTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSFER_ACTION_ID,"TRANSFER_ACTION_ID",2,38,0);
    }
    modifiedColumns.add("TRANSFER_ACTION_ID");
    this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
  }
   public void setConnection_id(Long CONNECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_ID,"CONNECTION_ID",2,38,0);
    }
    modifiedColumns.add("CONNECTION_ID");
    this.CONNECTION_ID = CONNECTION_ID;
  }
   public void setTable_id(Long TABLE_ID) {
    if (validateData){
      DataValidator.validateData((Object)TABLE_ID,"TABLE_ID",2,38,0);
    }
    modifiedColumns.add("TABLE_ID");
    this.TABLE_ID = TABLE_ID;
  }
   public void setTarget_table_id(Long TARGET_TABLE_ID) {
    if (validateData){
      DataValidator.validateData((Object)TARGET_TABLE_ID,"TARGET_TABLE_ID",2,38,0);
    }
    modifiedColumns.add("TARGET_TABLE_ID");
    this.TARGET_TABLE_ID = TARGET_TABLE_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_fk_tables o) {

         if ((((this.MAX_ERRORS == null) || (o.MAX_ERRORS == null)) && (this.MAX_ERRORS != o.MAX_ERRORS))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.WHERE_CLAUSE == null) || (o.WHERE_CLAUSE == null)) && (this.WHERE_CLAUSE != o.WHERE_CLAUSE))
            || (((this.FILTER_ERRORS_FLAG == null) || (o.FILTER_ERRORS_FLAG == null)) && (this.FILTER_ERRORS_FLAG != o.FILTER_ERRORS_FLAG))
            || (((this.REPLACE_ERRORS_FLAG == null) || (o.REPLACE_ERRORS_FLAG == null)) && (this.REPLACE_ERRORS_FLAG != o.REPLACE_ERRORS_FLAG))
            || (((this.REPLACE_ERRORS_WITH == null) || (o.REPLACE_ERRORS_WITH == null)) && (this.REPLACE_ERRORS_WITH != o.REPLACE_ERRORS_WITH))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.TRANSFER_ACTION_ID == null) || (o.TRANSFER_ACTION_ID == null)) && (this.TRANSFER_ACTION_ID != o.TRANSFER_ACTION_ID))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.TABLE_ID == null) || (o.TABLE_ID == null)) && (this.TABLE_ID != o.TABLE_ID))
            || (((this.TARGET_TABLE_ID == null) || (o.TARGET_TABLE_ID == null)) && (this.TARGET_TABLE_ID != o.TARGET_TABLE_ID))
          ){
    return false;
    } else
         if ((((this.MAX_ERRORS != null) && (o.MAX_ERRORS != null)) && (this.MAX_ERRORS.equals(o.MAX_ERRORS) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.WHERE_CLAUSE != null) && (o.WHERE_CLAUSE != null)) && (this.WHERE_CLAUSE.equals(o.WHERE_CLAUSE) == false))
            || (((this.FILTER_ERRORS_FLAG != null) && (o.FILTER_ERRORS_FLAG != null)) && (this.FILTER_ERRORS_FLAG.equals(o.FILTER_ERRORS_FLAG) == false))
            || (((this.REPLACE_ERRORS_FLAG != null) && (o.REPLACE_ERRORS_FLAG != null)) && (this.REPLACE_ERRORS_FLAG.equals(o.REPLACE_ERRORS_FLAG) == false))
            || (((this.REPLACE_ERRORS_WITH != null) && (o.REPLACE_ERRORS_WITH != null)) && (this.REPLACE_ERRORS_WITH.equals(o.REPLACE_ERRORS_WITH) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.TRANSFER_ACTION_ID != null) && (o.TRANSFER_ACTION_ID != null)) && (this.TRANSFER_ACTION_ID.equals(o.TRANSFER_ACTION_ID) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.TABLE_ID != null) && (o.TABLE_ID != null)) && (this.TABLE_ID.equals(o.TABLE_ID) == false))
            || (((this.TARGET_TABLE_ID != null) && (o.TARGET_TABLE_ID != null)) && (this.TARGET_TABLE_ID.equals(o.TARGET_TABLE_ID) == false))
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
  public static int getMax_errorsColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMax_errorsDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getMax_errorsSQLType() {
    
    return 2;   
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
  * return 2000
  */
  public static int getWhere_clauseColumnSize() {
    
     return 2000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getWhere_clauseDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getWhere_clauseSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getFilter_errors_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getFilter_errors_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getFilter_errors_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getReplace_errors_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getReplace_errors_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getReplace_errors_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getReplace_errors_withColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getReplace_errors_withDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getReplace_errors_withSQLType() {
    
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
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getConnection_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getConnection_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getConnection_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getTable_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTable_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTable_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getTarget_table_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTarget_table_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTarget_table_idSQLType() {
    
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
