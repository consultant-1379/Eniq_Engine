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

public class Meta_collections implements Cloneable,RockDBObject  {

    private Long COLLECTION_ID;
    private String COLLECTION_NAME;
    private String COLLECTION;
    private String MAIL_ERROR_ADDR;
    private String MAIL_FAIL_ADDR;
    private String MAIL_BUG_ADDR;
    private Long MAX_ERRORS;
    private Long MAX_FK_ERRORS;
    private Long MAX_COL_LIMIT_ERRORS;
    private String CHECK_FK_ERROR_FLAG;
    private String CHECK_COL_LIMITS_FLAG;
    private Timestamp LAST_TRANSFER_DATE;
    private String VERSION_NUMBER;
    private Long COLLECTION_SET_ID;
    private String USE_BATCH_ID;
    private Long PRIORITY;
    private Long QUEUE_TIME_LIMIT;
    private String ENABLED_FLAG;
    private String SETTYPE;
    private String FOLDABLE_FLAG;
    private String MEASTYPE;
    private String HOLD_FLAG;
    private String SCHEDULING_INFO;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "COLLECTION_ID"    ,"VERSION_NUMBER"    ,"COLLECTION_SET_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_collections(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_collections(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.COLLECTION_ID = null;
         this.COLLECTION_NAME = null;
         this.COLLECTION = null;
         this.MAIL_ERROR_ADDR = null;
         this.MAIL_FAIL_ADDR = null;
         this.MAIL_BUG_ADDR = null;
         this.MAX_ERRORS = null;
         this.MAX_FK_ERRORS = null;
         this.MAX_COL_LIMIT_ERRORS = null;
         this.CHECK_FK_ERROR_FLAG = null;
         this.CHECK_COL_LIMITS_FLAG = null;
         this.LAST_TRANSFER_DATE = null;
         this.VERSION_NUMBER = null;
         this.COLLECTION_SET_ID = null;
         this.USE_BATCH_ID = null;
         this.PRIORITY = null;
         this.QUEUE_TIME_LIMIT = null;
         this.ENABLED_FLAG = null;
         this.SETTYPE = null;
         this.FOLDABLE_FLAG = null;
         this.MEASTYPE = null;
         this.HOLD_FLAG = null;
         this.SCHEDULING_INFO = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_collections(RockFactory rockFact   ,Long COLLECTION_ID ,String VERSION_NUMBER ,Long COLLECTION_SET_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.COLLECTION_ID = COLLECTION_ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
            this.COLLECTION_SET_ID = COLLECTION_SET_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_collections o = (Meta_collections) it.next();

              this.COLLECTION_ID = o.getCollection_id();
              this.COLLECTION_NAME = o.getCollection_name();
              this.COLLECTION = o.getCollection();
              this.MAIL_ERROR_ADDR = o.getMail_error_addr();
              this.MAIL_FAIL_ADDR = o.getMail_fail_addr();
              this.MAIL_BUG_ADDR = o.getMail_bug_addr();
              this.MAX_ERRORS = o.getMax_errors();
              this.MAX_FK_ERRORS = o.getMax_fk_errors();
              this.MAX_COL_LIMIT_ERRORS = o.getMax_col_limit_errors();
              this.CHECK_FK_ERROR_FLAG = o.getCheck_fk_error_flag();
              this.CHECK_COL_LIMITS_FLAG = o.getCheck_col_limits_flag();
              this.LAST_TRANSFER_DATE = o.getLast_transfer_date();
              this.VERSION_NUMBER = o.getVersion_number();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.USE_BATCH_ID = o.getUse_batch_id();
              this.PRIORITY = o.getPriority();
              this.QUEUE_TIME_LIMIT = o.getQueue_time_limit();
              this.ENABLED_FLAG = o.getEnabled_flag();
              this.SETTYPE = o.getSettype();
              this.FOLDABLE_FLAG = o.getFoldable_flag();
              this.MEASTYPE = o.getMeastype();
              this.HOLD_FLAG = o.getHold_flag();
              this.SCHEDULING_INFO = o.getScheduling_info();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_collections");
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
  public Meta_collections(RockFactory rockFact, Meta_collections whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_collections o = (Meta_collections) it.next();
                this.COLLECTION_ID = o.getCollection_id();
                this.COLLECTION_NAME = o.getCollection_name();
                this.COLLECTION = o.getCollection();
                this.MAIL_ERROR_ADDR = o.getMail_error_addr();
                this.MAIL_FAIL_ADDR = o.getMail_fail_addr();
                this.MAIL_BUG_ADDR = o.getMail_bug_addr();
                this.MAX_ERRORS = o.getMax_errors();
                this.MAX_FK_ERRORS = o.getMax_fk_errors();
                this.MAX_COL_LIMIT_ERRORS = o.getMax_col_limit_errors();
                this.CHECK_FK_ERROR_FLAG = o.getCheck_fk_error_flag();
                this.CHECK_COL_LIMITS_FLAG = o.getCheck_col_limits_flag();
                this.LAST_TRANSFER_DATE = o.getLast_transfer_date();
                this.VERSION_NUMBER = o.getVersion_number();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.USE_BATCH_ID = o.getUse_batch_id();
                this.PRIORITY = o.getPriority();
                this.QUEUE_TIME_LIMIT = o.getQueue_time_limit();
                this.ENABLED_FLAG = o.getEnabled_flag();
                this.SETTYPE = o.getSettype();
                this.FOLDABLE_FLAG = o.getFoldable_flag();
                this.MEASTYPE = o.getMeastype();
                this.HOLD_FLAG = o.getHold_flag();
                this.SCHEDULING_INFO = o.getScheduling_info();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_collections");
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
    return "Meta_collections";
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
  public int updateDB(boolean useTimestamp, Meta_collections whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_collections whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_collections.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getCollection_id() { 
    return this.COLLECTION_ID;
  }
   public String getCollection_name() { 
    return this.COLLECTION_NAME;
  }
   public String getCollection() { 
    return this.COLLECTION;
  }
   public String getMail_error_addr() { 
    return this.MAIL_ERROR_ADDR;
  }
   public String getMail_fail_addr() { 
    return this.MAIL_FAIL_ADDR;
  }
   public String getMail_bug_addr() { 
    return this.MAIL_BUG_ADDR;
  }
   public Long getMax_errors() { 
    return this.MAX_ERRORS;
  }
   public Long getMax_fk_errors() { 
    return this.MAX_FK_ERRORS;
  }
   public Long getMax_col_limit_errors() { 
    return this.MAX_COL_LIMIT_ERRORS;
  }
   public String getCheck_fk_error_flag() { 
    return this.CHECK_FK_ERROR_FLAG;
  }
   public String getCheck_col_limits_flag() { 
    return this.CHECK_COL_LIMITS_FLAG;
  }
   public Timestamp getLast_transfer_date() { 
    return this.LAST_TRANSFER_DATE;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getCollection_set_id() { 
    return this.COLLECTION_SET_ID;
  }
   public String getUse_batch_id() { 
    return this.USE_BATCH_ID;
  }
   public Long getPriority() { 
    return this.PRIORITY;
  }
   public Long getQueue_time_limit() { 
    return this.QUEUE_TIME_LIMIT;
  }
   public String getEnabled_flag() { 
    return this.ENABLED_FLAG;
  }
   public String getSettype() { 
    return this.SETTYPE;
  }
   public String getFoldable_flag() { 
    return this.FOLDABLE_FLAG;
  }
   public String getMeastype() { 
    return this.MEASTYPE;
  }
   public String getHold_flag() { 
    return this.HOLD_FLAG;
  }
   public String getScheduling_info() { 
    return this.SCHEDULING_INFO;
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
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (COLLECTION_NAME == null)
      COLLECTION_NAME = new String ("");
     if (COLLECTION == null)
      COLLECTION = new String ("");
     if (MAIL_ERROR_ADDR == null)
      MAIL_ERROR_ADDR = new String ("");
     if (MAIL_FAIL_ADDR == null)
      MAIL_FAIL_ADDR = new String ("");
     if (MAIL_BUG_ADDR == null)
      MAIL_BUG_ADDR = new String ("");
     if (MAX_ERRORS == null)
      MAX_ERRORS = new Long (0);
     if (MAX_FK_ERRORS == null)
      MAX_FK_ERRORS = new Long (0);
     if (MAX_COL_LIMIT_ERRORS == null)
      MAX_COL_LIMIT_ERRORS = new Long (0);
     if (CHECK_FK_ERROR_FLAG == null)
      CHECK_FK_ERROR_FLAG = new String ("");
     if (CHECK_COL_LIMITS_FLAG == null)
      CHECK_COL_LIMITS_FLAG = new String ("");
     if (LAST_TRANSFER_DATE == null)
      LAST_TRANSFER_DATE = new Timestamp (0);
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (USE_BATCH_ID == null)
      USE_BATCH_ID = new String ("");
     if (PRIORITY == null)
      PRIORITY = new Long (0);
     if (QUEUE_TIME_LIMIT == null)
      QUEUE_TIME_LIMIT = new Long (0);
     if (ENABLED_FLAG == null)
      ENABLED_FLAG = new String ("");
     if (SETTYPE == null)
      SETTYPE = new String ("");
     if (FOLDABLE_FLAG == null)
      FOLDABLE_FLAG = new String ("");
     if (MEASTYPE == null)
      MEASTYPE = new String ("");
     if (HOLD_FLAG == null)
      HOLD_FLAG = new String ("");
     if (SCHEDULING_INFO == null)
      SCHEDULING_INFO = new String ("");
   }

   public void setCollection_id(Long COLLECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_ID,"COLLECTION_ID",2,38,0);
    }
    modifiedColumns.add("COLLECTION_ID");
    this.COLLECTION_ID = COLLECTION_ID;
  }
   public void setCollection_name(String COLLECTION_NAME) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_NAME,"COLLECTION_NAME",12,128,0);
    }
    modifiedColumns.add("COLLECTION_NAME");
    this.COLLECTION_NAME = COLLECTION_NAME;
  }
   public void setCollection(String COLLECTION) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION,"COLLECTION",12,200,0);
    }
    modifiedColumns.add("COLLECTION");
    this.COLLECTION = COLLECTION;
  }
   public void setMail_error_addr(String MAIL_ERROR_ADDR) {
    if (validateData){
      DataValidator.validateData((Object)MAIL_ERROR_ADDR,"MAIL_ERROR_ADDR",12,100,0);
    }
    modifiedColumns.add("MAIL_ERROR_ADDR");
    this.MAIL_ERROR_ADDR = MAIL_ERROR_ADDR;
  }
   public void setMail_fail_addr(String MAIL_FAIL_ADDR) {
    if (validateData){
      DataValidator.validateData((Object)MAIL_FAIL_ADDR,"MAIL_FAIL_ADDR",12,100,0);
    }
    modifiedColumns.add("MAIL_FAIL_ADDR");
    this.MAIL_FAIL_ADDR = MAIL_FAIL_ADDR;
  }
   public void setMail_bug_addr(String MAIL_BUG_ADDR) {
    if (validateData){
      DataValidator.validateData((Object)MAIL_BUG_ADDR,"MAIL_BUG_ADDR",12,100,0);
    }
    modifiedColumns.add("MAIL_BUG_ADDR");
    this.MAIL_BUG_ADDR = MAIL_BUG_ADDR;
  }
   public void setMax_errors(Long MAX_ERRORS) {
    if (validateData){
      DataValidator.validateData((Object)MAX_ERRORS,"MAX_ERRORS",2,38,0);
    }
    modifiedColumns.add("MAX_ERRORS");
    this.MAX_ERRORS = MAX_ERRORS;
  }
   public void setMax_fk_errors(Long MAX_FK_ERRORS) {
    if (validateData){
      DataValidator.validateData((Object)MAX_FK_ERRORS,"MAX_FK_ERRORS",2,38,0);
    }
    modifiedColumns.add("MAX_FK_ERRORS");
    this.MAX_FK_ERRORS = MAX_FK_ERRORS;
  }
   public void setMax_col_limit_errors(Long MAX_COL_LIMIT_ERRORS) {
    if (validateData){
      DataValidator.validateData((Object)MAX_COL_LIMIT_ERRORS,"MAX_COL_LIMIT_ERRORS",2,38,0);
    }
    modifiedColumns.add("MAX_COL_LIMIT_ERRORS");
    this.MAX_COL_LIMIT_ERRORS = MAX_COL_LIMIT_ERRORS;
  }
   public void setCheck_fk_error_flag(String CHECK_FK_ERROR_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)CHECK_FK_ERROR_FLAG,"CHECK_FK_ERROR_FLAG",12,1,0);
    }
    modifiedColumns.add("CHECK_FK_ERROR_FLAG");
    this.CHECK_FK_ERROR_FLAG = CHECK_FK_ERROR_FLAG;
  }
   public void setCheck_col_limits_flag(String CHECK_COL_LIMITS_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)CHECK_COL_LIMITS_FLAG,"CHECK_COL_LIMITS_FLAG",12,1,0);
    }
    modifiedColumns.add("CHECK_COL_LIMITS_FLAG");
    this.CHECK_COL_LIMITS_FLAG = CHECK_COL_LIMITS_FLAG;
  }
   public void setLast_transfer_date(Timestamp LAST_TRANSFER_DATE) {
    if (validateData){
      DataValidator.validateData((Object)LAST_TRANSFER_DATE,"LAST_TRANSFER_DATE",93,23,0);
    }
    modifiedColumns.add("LAST_TRANSFER_DATE");
    this.LAST_TRANSFER_DATE = LAST_TRANSFER_DATE;
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
   public void setUse_batch_id(String USE_BATCH_ID) {
    if (validateData){
      DataValidator.validateData((Object)USE_BATCH_ID,"USE_BATCH_ID",12,1,0);
    }
    modifiedColumns.add("USE_BATCH_ID");
    this.USE_BATCH_ID = USE_BATCH_ID;
  }
   public void setPriority(Long PRIORITY) {
    if (validateData){
      DataValidator.validateData((Object)PRIORITY,"PRIORITY",2,3,0);
    }
    modifiedColumns.add("PRIORITY");
    this.PRIORITY = PRIORITY;
  }
   public void setQueue_time_limit(Long QUEUE_TIME_LIMIT) {
    if (validateData){
      DataValidator.validateData((Object)QUEUE_TIME_LIMIT,"QUEUE_TIME_LIMIT",2,38,0);
    }
    modifiedColumns.add("QUEUE_TIME_LIMIT");
    this.QUEUE_TIME_LIMIT = QUEUE_TIME_LIMIT;
  }
   public void setEnabled_flag(String ENABLED_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)ENABLED_FLAG,"ENABLED_FLAG",12,1,0);
    }
    modifiedColumns.add("ENABLED_FLAG");
    this.ENABLED_FLAG = ENABLED_FLAG;
  }
   public void setSettype(String SETTYPE) {
    if (validateData){
      DataValidator.validateData((Object)SETTYPE,"SETTYPE",12,10,0);
    }
    modifiedColumns.add("SETTYPE");
    this.SETTYPE = SETTYPE;
  }
   public void setFoldable_flag(String FOLDABLE_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)FOLDABLE_FLAG,"FOLDABLE_FLAG",12,1,0);
    }
    modifiedColumns.add("FOLDABLE_FLAG");
    this.FOLDABLE_FLAG = FOLDABLE_FLAG;
  }
   public void setMeastype(String MEASTYPE) {
    if (validateData){
      DataValidator.validateData((Object)MEASTYPE,"MEASTYPE",12,30,0);
    }
    modifiedColumns.add("MEASTYPE");
    this.MEASTYPE = MEASTYPE;
  }
   public void setHold_flag(String HOLD_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)HOLD_FLAG,"HOLD_FLAG",12,1,0);
    }
    modifiedColumns.add("HOLD_FLAG");
    this.HOLD_FLAG = HOLD_FLAG;
  }
   public void setScheduling_info(String SCHEDULING_INFO) {
    if (validateData){
      DataValidator.validateData((Object)SCHEDULING_INFO,"SCHEDULING_INFO",12,2000,0);
    }
    modifiedColumns.add("SCHEDULING_INFO");
    this.SCHEDULING_INFO = SCHEDULING_INFO;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_collections o) {

         if ((((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.COLLECTION_NAME == null) || (o.COLLECTION_NAME == null)) && (this.COLLECTION_NAME != o.COLLECTION_NAME))
            || (((this.COLLECTION == null) || (o.COLLECTION == null)) && (this.COLLECTION != o.COLLECTION))
            || (((this.MAIL_ERROR_ADDR == null) || (o.MAIL_ERROR_ADDR == null)) && (this.MAIL_ERROR_ADDR != o.MAIL_ERROR_ADDR))
            || (((this.MAIL_FAIL_ADDR == null) || (o.MAIL_FAIL_ADDR == null)) && (this.MAIL_FAIL_ADDR != o.MAIL_FAIL_ADDR))
            || (((this.MAIL_BUG_ADDR == null) || (o.MAIL_BUG_ADDR == null)) && (this.MAIL_BUG_ADDR != o.MAIL_BUG_ADDR))
            || (((this.MAX_ERRORS == null) || (o.MAX_ERRORS == null)) && (this.MAX_ERRORS != o.MAX_ERRORS))
            || (((this.MAX_FK_ERRORS == null) || (o.MAX_FK_ERRORS == null)) && (this.MAX_FK_ERRORS != o.MAX_FK_ERRORS))
            || (((this.MAX_COL_LIMIT_ERRORS == null) || (o.MAX_COL_LIMIT_ERRORS == null)) && (this.MAX_COL_LIMIT_ERRORS != o.MAX_COL_LIMIT_ERRORS))
            || (((this.CHECK_FK_ERROR_FLAG == null) || (o.CHECK_FK_ERROR_FLAG == null)) && (this.CHECK_FK_ERROR_FLAG != o.CHECK_FK_ERROR_FLAG))
            || (((this.CHECK_COL_LIMITS_FLAG == null) || (o.CHECK_COL_LIMITS_FLAG == null)) && (this.CHECK_COL_LIMITS_FLAG != o.CHECK_COL_LIMITS_FLAG))
            || (((this.LAST_TRANSFER_DATE == null) || (o.LAST_TRANSFER_DATE == null)) && (this.LAST_TRANSFER_DATE != o.LAST_TRANSFER_DATE))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.USE_BATCH_ID == null) || (o.USE_BATCH_ID == null)) && (this.USE_BATCH_ID != o.USE_BATCH_ID))
            || (((this.PRIORITY == null) || (o.PRIORITY == null)) && (this.PRIORITY != o.PRIORITY))
            || (((this.QUEUE_TIME_LIMIT == null) || (o.QUEUE_TIME_LIMIT == null)) && (this.QUEUE_TIME_LIMIT != o.QUEUE_TIME_LIMIT))
            || (((this.ENABLED_FLAG == null) || (o.ENABLED_FLAG == null)) && (this.ENABLED_FLAG != o.ENABLED_FLAG))
            || (((this.SETTYPE == null) || (o.SETTYPE == null)) && (this.SETTYPE != o.SETTYPE))
            || (((this.FOLDABLE_FLAG == null) || (o.FOLDABLE_FLAG == null)) && (this.FOLDABLE_FLAG != o.FOLDABLE_FLAG))
            || (((this.MEASTYPE == null) || (o.MEASTYPE == null)) && (this.MEASTYPE != o.MEASTYPE))
            || (((this.HOLD_FLAG == null) || (o.HOLD_FLAG == null)) && (this.HOLD_FLAG != o.HOLD_FLAG))
            || (((this.SCHEDULING_INFO == null) || (o.SCHEDULING_INFO == null)) && (this.SCHEDULING_INFO != o.SCHEDULING_INFO))
          ){
    return false;
    } else
         if ((((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.COLLECTION_NAME != null) && (o.COLLECTION_NAME != null)) && (this.COLLECTION_NAME.equals(o.COLLECTION_NAME) == false))
            || (((this.COLLECTION != null) && (o.COLLECTION != null)) && (this.COLLECTION.equals(o.COLLECTION) == false))
            || (((this.MAIL_ERROR_ADDR != null) && (o.MAIL_ERROR_ADDR != null)) && (this.MAIL_ERROR_ADDR.equals(o.MAIL_ERROR_ADDR) == false))
            || (((this.MAIL_FAIL_ADDR != null) && (o.MAIL_FAIL_ADDR != null)) && (this.MAIL_FAIL_ADDR.equals(o.MAIL_FAIL_ADDR) == false))
            || (((this.MAIL_BUG_ADDR != null) && (o.MAIL_BUG_ADDR != null)) && (this.MAIL_BUG_ADDR.equals(o.MAIL_BUG_ADDR) == false))
            || (((this.MAX_ERRORS != null) && (o.MAX_ERRORS != null)) && (this.MAX_ERRORS.equals(o.MAX_ERRORS) == false))
            || (((this.MAX_FK_ERRORS != null) && (o.MAX_FK_ERRORS != null)) && (this.MAX_FK_ERRORS.equals(o.MAX_FK_ERRORS) == false))
            || (((this.MAX_COL_LIMIT_ERRORS != null) && (o.MAX_COL_LIMIT_ERRORS != null)) && (this.MAX_COL_LIMIT_ERRORS.equals(o.MAX_COL_LIMIT_ERRORS) == false))
            || (((this.CHECK_FK_ERROR_FLAG != null) && (o.CHECK_FK_ERROR_FLAG != null)) && (this.CHECK_FK_ERROR_FLAG.equals(o.CHECK_FK_ERROR_FLAG) == false))
            || (((this.CHECK_COL_LIMITS_FLAG != null) && (o.CHECK_COL_LIMITS_FLAG != null)) && (this.CHECK_COL_LIMITS_FLAG.equals(o.CHECK_COL_LIMITS_FLAG) == false))
            || (((this.LAST_TRANSFER_DATE != null) && (o.LAST_TRANSFER_DATE != null)) && (this.LAST_TRANSFER_DATE.equals(o.LAST_TRANSFER_DATE) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.USE_BATCH_ID != null) && (o.USE_BATCH_ID != null)) && (this.USE_BATCH_ID.equals(o.USE_BATCH_ID) == false))
            || (((this.PRIORITY != null) && (o.PRIORITY != null)) && (this.PRIORITY.equals(o.PRIORITY) == false))
            || (((this.QUEUE_TIME_LIMIT != null) && (o.QUEUE_TIME_LIMIT != null)) && (this.QUEUE_TIME_LIMIT.equals(o.QUEUE_TIME_LIMIT) == false))
            || (((this.ENABLED_FLAG != null) && (o.ENABLED_FLAG != null)) && (this.ENABLED_FLAG.equals(o.ENABLED_FLAG) == false))
            || (((this.SETTYPE != null) && (o.SETTYPE != null)) && (this.SETTYPE.equals(o.SETTYPE) == false))
            || (((this.FOLDABLE_FLAG != null) && (o.FOLDABLE_FLAG != null)) && (this.FOLDABLE_FLAG.equals(o.FOLDABLE_FLAG) == false))
            || (((this.MEASTYPE != null) && (o.MEASTYPE != null)) && (this.MEASTYPE.equals(o.MEASTYPE) == false))
            || (((this.HOLD_FLAG != null) && (o.HOLD_FLAG != null)) && (this.HOLD_FLAG.equals(o.HOLD_FLAG) == false))
            || (((this.SCHEDULING_INFO != null) && (o.SCHEDULING_INFO != null)) && (this.SCHEDULING_INFO.equals(o.SCHEDULING_INFO) == false))
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
  * return 128
  */
  public static int getCollection_nameColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCollection_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCollection_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getCollectionColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCollectionDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCollectionSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 100
  */
  public static int getMail_error_addrColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMail_error_addrDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMail_error_addrSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 100
  */
  public static int getMail_fail_addrColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMail_fail_addrDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMail_fail_addrSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 100
  */
  public static int getMail_bug_addrColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMail_bug_addrDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMail_bug_addrSQLType() {
    
    return 12;   
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
  * return 38
  */
  public static int getMax_fk_errorsColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMax_fk_errorsDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getMax_fk_errorsSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getMax_col_limit_errorsColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMax_col_limit_errorsDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getMax_col_limit_errorsSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getCheck_fk_error_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCheck_fk_error_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCheck_fk_error_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getCheck_col_limits_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCheck_col_limits_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCheck_col_limits_flagSQLType() {
    
    return 12;   
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
  * return 1
  */
  public static int getUse_batch_idColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getUse_batch_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getUse_batch_idSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 3
  */
  public static int getPriorityColumnSize() {
    
     return 3;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getPriorityDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getPrioritySQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getQueue_time_limitColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getQueue_time_limitDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getQueue_time_limitSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getEnabled_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getEnabled_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getEnabled_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 10
  */
  public static int getSettypeColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getSettypeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getSettypeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getFoldable_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getFoldable_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getFoldable_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getMeastypeColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMeastypeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMeastypeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getHold_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getHold_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getHold_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 2000
  */
  public static int getScheduling_infoColumnSize() {
    
     return 2000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getScheduling_infoDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getScheduling_infoSQLType() {
    
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
