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

public class Meta_source_tables implements Cloneable,RockDBObject  {

    private Timestamp LAST_TRANSFER_DATE;
    private Long TRANSFER_ACTION_ID;
    private Long TABLE_ID;
    private String USE_TR_DATE_IN_WHERE_FLAG;
    private Long COLLECTION_SET_ID;
    private Long COLLECTION_ID;
    private Long CONNECTION_ID;
    private String DISTINCT_FLAG;
    private String AS_SELECT_OPTIONS;
    private String AS_SELECT_TABLESPACE;
    private String VERSION_NUMBER;
    private Long TIMESTAMP_COLUMN_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "TRANSFER_ACTION_ID"    ,"TABLE_ID"    ,"COLLECTION_SET_ID"    ,"COLLECTION_ID"    ,"CONNECTION_ID"    ,"VERSION_NUMBER"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_source_tables(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_source_tables(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.LAST_TRANSFER_DATE = null;
         this.TRANSFER_ACTION_ID = null;
         this.TABLE_ID = null;
         this.USE_TR_DATE_IN_WHERE_FLAG = null;
         this.COLLECTION_SET_ID = null;
         this.COLLECTION_ID = null;
         this.CONNECTION_ID = null;
         this.DISTINCT_FLAG = null;
         this.AS_SELECT_OPTIONS = null;
         this.AS_SELECT_TABLESPACE = null;
         this.VERSION_NUMBER = null;
         this.TIMESTAMP_COLUMN_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_source_tables(RockFactory rockFact   ,Long TRANSFER_ACTION_ID ,Long TABLE_ID ,Long COLLECTION_SET_ID ,Long COLLECTION_ID ,Long CONNECTION_ID ,String VERSION_NUMBER ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
            this.TABLE_ID = TABLE_ID;
            this.COLLECTION_SET_ID = COLLECTION_SET_ID;
            this.COLLECTION_ID = COLLECTION_ID;
            this.CONNECTION_ID = CONNECTION_ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_source_tables o = (Meta_source_tables) it.next();

              this.LAST_TRANSFER_DATE = o.getLast_transfer_date();
              this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
              this.TABLE_ID = o.getTable_id();
              this.USE_TR_DATE_IN_WHERE_FLAG = o.getUse_tr_date_in_where_flag();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.CONNECTION_ID = o.getConnection_id();
              this.DISTINCT_FLAG = o.getDistinct_flag();
              this.AS_SELECT_OPTIONS = o.getAs_select_options();
              this.AS_SELECT_TABLESPACE = o.getAs_select_tablespace();
              this.VERSION_NUMBER = o.getVersion_number();
              this.TIMESTAMP_COLUMN_ID = o.getTimestamp_column_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_source_tables");
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
  public Meta_source_tables(RockFactory rockFact, Meta_source_tables whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_source_tables o = (Meta_source_tables) it.next();
                this.LAST_TRANSFER_DATE = o.getLast_transfer_date();
                this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
                this.TABLE_ID = o.getTable_id();
                this.USE_TR_DATE_IN_WHERE_FLAG = o.getUse_tr_date_in_where_flag();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.CONNECTION_ID = o.getConnection_id();
                this.DISTINCT_FLAG = o.getDistinct_flag();
                this.AS_SELECT_OPTIONS = o.getAs_select_options();
                this.AS_SELECT_TABLESPACE = o.getAs_select_tablespace();
                this.VERSION_NUMBER = o.getVersion_number();
                this.TIMESTAMP_COLUMN_ID = o.getTimestamp_column_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_source_tables");
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
    return "Meta_source_tables";
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
  public int updateDB(boolean useTimestamp, Meta_source_tables whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_source_tables whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_source_tables.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Timestamp getLast_transfer_date() { 
    return this.LAST_TRANSFER_DATE;
  }
   public Long getTransfer_action_id() { 
    return this.TRANSFER_ACTION_ID;
  }
   public Long getTable_id() { 
    return this.TABLE_ID;
  }
   public String getUse_tr_date_in_where_flag() { 
    return this.USE_TR_DATE_IN_WHERE_FLAG;
  }
   public Long getCollection_set_id() { 
    return this.COLLECTION_SET_ID;
  }
   public Long getCollection_id() { 
    return this.COLLECTION_ID;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public String getDistinct_flag() { 
    return this.DISTINCT_FLAG;
  }
   public String getAs_select_options() { 
    return this.AS_SELECT_OPTIONS;
  }
   public String getAs_select_tablespace() { 
    return this.AS_SELECT_TABLESPACE;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getTimestamp_column_id() { 
    return this.TIMESTAMP_COLUMN_ID;
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
     if (LAST_TRANSFER_DATE == null)
      LAST_TRANSFER_DATE = new Timestamp (0);
     if (TRANSFER_ACTION_ID == null)
      TRANSFER_ACTION_ID = new Long (0);
     if (TABLE_ID == null)
      TABLE_ID = new Long (0);
     if (USE_TR_DATE_IN_WHERE_FLAG == null)
      USE_TR_DATE_IN_WHERE_FLAG = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (DISTINCT_FLAG == null)
      DISTINCT_FLAG = new String ("");
     if (AS_SELECT_OPTIONS == null)
      AS_SELECT_OPTIONS = new String ("");
     if (AS_SELECT_TABLESPACE == null)
      AS_SELECT_TABLESPACE = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (TIMESTAMP_COLUMN_ID == null)
      TIMESTAMP_COLUMN_ID = new Long (0);
   }

   public void setLast_transfer_date(Timestamp LAST_TRANSFER_DATE) {
    if (validateData){
      DataValidator.validateData((Object)LAST_TRANSFER_DATE,"LAST_TRANSFER_DATE",93,23,0);
    }
    modifiedColumns.add("LAST_TRANSFER_DATE");
    this.LAST_TRANSFER_DATE = LAST_TRANSFER_DATE;
  }
   public void setTransfer_action_id(Long TRANSFER_ACTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSFER_ACTION_ID,"TRANSFER_ACTION_ID",2,38,0);
    }
    modifiedColumns.add("TRANSFER_ACTION_ID");
    this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
  }
   public void setTable_id(Long TABLE_ID) {
    if (validateData){
      DataValidator.validateData((Object)TABLE_ID,"TABLE_ID",2,38,0);
    }
    modifiedColumns.add("TABLE_ID");
    this.TABLE_ID = TABLE_ID;
  }
   public void setUse_tr_date_in_where_flag(String USE_TR_DATE_IN_WHERE_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)USE_TR_DATE_IN_WHERE_FLAG,"USE_TR_DATE_IN_WHERE_FLAG",12,1,0);
    }
    modifiedColumns.add("USE_TR_DATE_IN_WHERE_FLAG");
    this.USE_TR_DATE_IN_WHERE_FLAG = USE_TR_DATE_IN_WHERE_FLAG;
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
   public void setConnection_id(Long CONNECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_ID,"CONNECTION_ID",2,38,0);
    }
    modifiedColumns.add("CONNECTION_ID");
    this.CONNECTION_ID = CONNECTION_ID;
  }
   public void setDistinct_flag(String DISTINCT_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)DISTINCT_FLAG,"DISTINCT_FLAG",12,1,0);
    }
    modifiedColumns.add("DISTINCT_FLAG");
    this.DISTINCT_FLAG = DISTINCT_FLAG;
  }
   public void setAs_select_options(String AS_SELECT_OPTIONS) {
    if (validateData){
      DataValidator.validateData((Object)AS_SELECT_OPTIONS,"AS_SELECT_OPTIONS",12,200,0);
    }
    modifiedColumns.add("AS_SELECT_OPTIONS");
    this.AS_SELECT_OPTIONS = AS_SELECT_OPTIONS;
  }
   public void setAs_select_tablespace(String AS_SELECT_TABLESPACE) {
    if (validateData){
      DataValidator.validateData((Object)AS_SELECT_TABLESPACE,"AS_SELECT_TABLESPACE",12,30,0);
    }
    modifiedColumns.add("AS_SELECT_TABLESPACE");
    this.AS_SELECT_TABLESPACE = AS_SELECT_TABLESPACE;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setTimestamp_column_id(Long TIMESTAMP_COLUMN_ID) {
    if (validateData){
      DataValidator.validateData((Object)TIMESTAMP_COLUMN_ID,"TIMESTAMP_COLUMN_ID",2,38,0);
    }
    modifiedColumns.add("TIMESTAMP_COLUMN_ID");
    this.TIMESTAMP_COLUMN_ID = TIMESTAMP_COLUMN_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_source_tables o) {

         if ((((this.LAST_TRANSFER_DATE == null) || (o.LAST_TRANSFER_DATE == null)) && (this.LAST_TRANSFER_DATE != o.LAST_TRANSFER_DATE))
            || (((this.TRANSFER_ACTION_ID == null) || (o.TRANSFER_ACTION_ID == null)) && (this.TRANSFER_ACTION_ID != o.TRANSFER_ACTION_ID))
            || (((this.TABLE_ID == null) || (o.TABLE_ID == null)) && (this.TABLE_ID != o.TABLE_ID))
            || (((this.USE_TR_DATE_IN_WHERE_FLAG == null) || (o.USE_TR_DATE_IN_WHERE_FLAG == null)) && (this.USE_TR_DATE_IN_WHERE_FLAG != o.USE_TR_DATE_IN_WHERE_FLAG))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.DISTINCT_FLAG == null) || (o.DISTINCT_FLAG == null)) && (this.DISTINCT_FLAG != o.DISTINCT_FLAG))
            || (((this.AS_SELECT_OPTIONS == null) || (o.AS_SELECT_OPTIONS == null)) && (this.AS_SELECT_OPTIONS != o.AS_SELECT_OPTIONS))
            || (((this.AS_SELECT_TABLESPACE == null) || (o.AS_SELECT_TABLESPACE == null)) && (this.AS_SELECT_TABLESPACE != o.AS_SELECT_TABLESPACE))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.TIMESTAMP_COLUMN_ID == null) || (o.TIMESTAMP_COLUMN_ID == null)) && (this.TIMESTAMP_COLUMN_ID != o.TIMESTAMP_COLUMN_ID))
          ){
    return false;
    } else
         if ((((this.LAST_TRANSFER_DATE != null) && (o.LAST_TRANSFER_DATE != null)) && (this.LAST_TRANSFER_DATE.equals(o.LAST_TRANSFER_DATE) == false))
            || (((this.TRANSFER_ACTION_ID != null) && (o.TRANSFER_ACTION_ID != null)) && (this.TRANSFER_ACTION_ID.equals(o.TRANSFER_ACTION_ID) == false))
            || (((this.TABLE_ID != null) && (o.TABLE_ID != null)) && (this.TABLE_ID.equals(o.TABLE_ID) == false))
            || (((this.USE_TR_DATE_IN_WHERE_FLAG != null) && (o.USE_TR_DATE_IN_WHERE_FLAG != null)) && (this.USE_TR_DATE_IN_WHERE_FLAG.equals(o.USE_TR_DATE_IN_WHERE_FLAG) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.DISTINCT_FLAG != null) && (o.DISTINCT_FLAG != null)) && (this.DISTINCT_FLAG.equals(o.DISTINCT_FLAG) == false))
            || (((this.AS_SELECT_OPTIONS != null) && (o.AS_SELECT_OPTIONS != null)) && (this.AS_SELECT_OPTIONS.equals(o.AS_SELECT_OPTIONS) == false))
            || (((this.AS_SELECT_TABLESPACE != null) && (o.AS_SELECT_TABLESPACE != null)) && (this.AS_SELECT_TABLESPACE.equals(o.AS_SELECT_TABLESPACE) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.TIMESTAMP_COLUMN_ID != null) && (o.TIMESTAMP_COLUMN_ID != null)) && (this.TIMESTAMP_COLUMN_ID.equals(o.TIMESTAMP_COLUMN_ID) == false))
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
  * return 23
  */
  public static int getLast_transfer_dateColumnSize() {
    
     return 23;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getLast_transfer_dateDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 93
  */
  public static int getLast_transfer_dateSQLType() {
    
    return 93;   
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
  * return 1
  */
  public static int getUse_tr_date_in_where_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getUse_tr_date_in_where_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getUse_tr_date_in_where_flagSQLType() {
    
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
  * return 1
  */
  public static int getDistinct_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDistinct_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDistinct_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getAs_select_optionsColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAs_select_optionsDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAs_select_optionsSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getAs_select_tablespaceColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAs_select_tablespaceDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAs_select_tablespaceSQLType() {
    
    return 12;   
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
  public static int getTimestamp_column_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTimestamp_column_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTimestamp_column_idSQLType() {
    
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
