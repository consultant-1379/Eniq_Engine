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

public class Meta_transfer_batches implements Cloneable,RockDBObject  {

    private Long ID;
    private Timestamp START_DATE;
    private Timestamp END_DATE;
    private String FAIL_FLAG;
    private String STATUS;
    private String VERSION_NUMBER;
    private Long COLLECTION_SET_ID;
    private Long COLLECTION_ID;
    private String META_COLLECTION_NAME;
    private String META_COLLECTION_SET_NAME;
    private String SETTYPE;
    private Integer SLOT_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "ID"    ,"VERSION_NUMBER"    ,"META_COLLECTION_NAME"    ,"META_COLLECTION_SET_NAME"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_transfer_batches(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_transfer_batches(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.ID = null;
         this.START_DATE = null;
         this.END_DATE = null;
         this.FAIL_FLAG = null;
         this.STATUS = null;
         this.VERSION_NUMBER = null;
         this.COLLECTION_SET_ID = null;
         this.COLLECTION_ID = null;
         this.META_COLLECTION_NAME = null;
         this.META_COLLECTION_SET_NAME = null;
         this.SETTYPE = null;
         this.SLOT_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_transfer_batches(RockFactory rockFact   ,Long ID ,String VERSION_NUMBER ,String META_COLLECTION_NAME ,String META_COLLECTION_SET_NAME ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.ID = ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
            this.META_COLLECTION_NAME = META_COLLECTION_NAME;
            this.META_COLLECTION_SET_NAME = META_COLLECTION_SET_NAME;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_transfer_batches o = (Meta_transfer_batches) it.next();

              this.ID = o.getId();
              this.START_DATE = o.getStart_date();
              this.END_DATE = o.getEnd_date();
              this.FAIL_FLAG = o.getFail_flag();
              this.STATUS = o.getStatus();
              this.VERSION_NUMBER = o.getVersion_number();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.META_COLLECTION_NAME = o.getMeta_collection_name();
              this.META_COLLECTION_SET_NAME = o.getMeta_collection_set_name();
              this.SETTYPE = o.getSettype();
              this.SLOT_ID = o.getSlot_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transfer_batches");
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
  public Meta_transfer_batches(RockFactory rockFact, Meta_transfer_batches whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_transfer_batches o = (Meta_transfer_batches) it.next();
                this.ID = o.getId();
                this.START_DATE = o.getStart_date();
                this.END_DATE = o.getEnd_date();
                this.FAIL_FLAG = o.getFail_flag();
                this.STATUS = o.getStatus();
                this.VERSION_NUMBER = o.getVersion_number();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.META_COLLECTION_NAME = o.getMeta_collection_name();
                this.META_COLLECTION_SET_NAME = o.getMeta_collection_set_name();
                this.SETTYPE = o.getSettype();
                this.SLOT_ID = o.getSlot_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transfer_batches");
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
    return "Meta_transfer_batches";
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
  public int updateDB(boolean useTimestamp, Meta_transfer_batches whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_transfer_batches whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_transfer_batches.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getId() { 
    return this.ID;
  }
   public Timestamp getStart_date() { 
    return this.START_DATE;
  }
   public Timestamp getEnd_date() { 
    return this.END_DATE;
  }
   public String getFail_flag() { 
    return this.FAIL_FLAG;
  }
   public String getStatus() { 
    return this.STATUS;
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
   public String getMeta_collection_name() { 
    return this.META_COLLECTION_NAME;
  }
   public String getMeta_collection_set_name() { 
    return this.META_COLLECTION_SET_NAME;
  }
   public String getSettype() { 
    return this.SETTYPE;
  }
   public Integer getSlot_id() { 
    return this.SLOT_ID;
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
     if (START_DATE == null)
      START_DATE = new Timestamp (0);
     if (END_DATE == null)
      END_DATE = new Timestamp (0);
     if (FAIL_FLAG == null)
      FAIL_FLAG = new String ("");
     if (STATUS == null)
      STATUS = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (META_COLLECTION_NAME == null)
      META_COLLECTION_NAME = new String ("");
     if (META_COLLECTION_SET_NAME == null)
      META_COLLECTION_SET_NAME = new String ("");
     if (SETTYPE == null)
      SETTYPE = new String ("");
     if (SLOT_ID == null)
      SLOT_ID = new Integer (0);
   }

   public void setId(Long ID) {
    if (validateData){
      DataValidator.validateData((Object)ID,"ID",2,38,0);
    }
    modifiedColumns.add("ID");
    this.ID = ID;
  }
   public void setStart_date(Timestamp START_DATE) {
    if (validateData){
      DataValidator.validateData((Object)START_DATE,"START_DATE",93,23,0);
    }
    modifiedColumns.add("START_DATE");
    this.START_DATE = START_DATE;
  }
   public void setEnd_date(Timestamp END_DATE) {
    if (validateData){
      DataValidator.validateData((Object)END_DATE,"END_DATE",93,23,0);
    }
    modifiedColumns.add("END_DATE");
    this.END_DATE = END_DATE;
  }
   public void setFail_flag(String FAIL_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)FAIL_FLAG,"FAIL_FLAG",12,1,0);
    }
    modifiedColumns.add("FAIL_FLAG");
    this.FAIL_FLAG = FAIL_FLAG;
  }
   public void setStatus(String STATUS) {
    if (validateData){
      DataValidator.validateData((Object)STATUS,"STATUS",12,10,0);
    }
    modifiedColumns.add("STATUS");
    this.STATUS = STATUS;
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
   public void setMeta_collection_name(String META_COLLECTION_NAME) {
    if (validateData){
      DataValidator.validateData((Object)META_COLLECTION_NAME,"META_COLLECTION_NAME",12,12,0);
    }
    modifiedColumns.add("META_COLLECTION_NAME");
    this.META_COLLECTION_NAME = META_COLLECTION_NAME;
  }
   public void setMeta_collection_set_name(String META_COLLECTION_SET_NAME) {
    if (validateData){
      DataValidator.validateData((Object)META_COLLECTION_SET_NAME,"META_COLLECTION_SET_NAME",12,12,0);
    }
    modifiedColumns.add("META_COLLECTION_SET_NAME");
    this.META_COLLECTION_SET_NAME = META_COLLECTION_SET_NAME;
  }
   public void setSettype(String SETTYPE) {
    if (validateData){
      DataValidator.validateData((Object)SETTYPE,"SETTYPE",12,12,0);
    }
    modifiedColumns.add("SETTYPE");
    this.SETTYPE = SETTYPE;
  }
   public void setSlot_id(Integer SLOT_ID) {
    if (validateData){
      DataValidator.validateData((Object)SLOT_ID,"SLOT_ID",4,10,0);
    }
    modifiedColumns.add("SLOT_ID");
    this.SLOT_ID = SLOT_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_transfer_batches o) {

         if ((((this.ID == null) || (o.ID == null)) && (this.ID != o.ID))
            || (((this.START_DATE == null) || (o.START_DATE == null)) && (this.START_DATE != o.START_DATE))
            || (((this.END_DATE == null) || (o.END_DATE == null)) && (this.END_DATE != o.END_DATE))
            || (((this.FAIL_FLAG == null) || (o.FAIL_FLAG == null)) && (this.FAIL_FLAG != o.FAIL_FLAG))
            || (((this.STATUS == null) || (o.STATUS == null)) && (this.STATUS != o.STATUS))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.META_COLLECTION_NAME == null) || (o.META_COLLECTION_NAME == null)) && (this.META_COLLECTION_NAME != o.META_COLLECTION_NAME))
            || (((this.META_COLLECTION_SET_NAME == null) || (o.META_COLLECTION_SET_NAME == null)) && (this.META_COLLECTION_SET_NAME != o.META_COLLECTION_SET_NAME))
            || (((this.SETTYPE == null) || (o.SETTYPE == null)) && (this.SETTYPE != o.SETTYPE))
            || (((this.SLOT_ID == null) || (o.SLOT_ID == null)) && (this.SLOT_ID != o.SLOT_ID))
          ){
    return false;
    } else
         if ((((this.ID != null) && (o.ID != null)) && (this.ID.equals(o.ID) == false))
            || (((this.START_DATE != null) && (o.START_DATE != null)) && (this.START_DATE.equals(o.START_DATE) == false))
            || (((this.END_DATE != null) && (o.END_DATE != null)) && (this.END_DATE.equals(o.END_DATE) == false))
            || (((this.FAIL_FLAG != null) && (o.FAIL_FLAG != null)) && (this.FAIL_FLAG.equals(o.FAIL_FLAG) == false))
            || (((this.STATUS != null) && (o.STATUS != null)) && (this.STATUS.equals(o.STATUS) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.META_COLLECTION_NAME != null) && (o.META_COLLECTION_NAME != null)) && (this.META_COLLECTION_NAME.equals(o.META_COLLECTION_NAME) == false))
            || (((this.META_COLLECTION_SET_NAME != null) && (o.META_COLLECTION_SET_NAME != null)) && (this.META_COLLECTION_SET_NAME.equals(o.META_COLLECTION_SET_NAME) == false))
            || (((this.SETTYPE != null) && (o.SETTYPE != null)) && (this.SETTYPE.equals(o.SETTYPE) == false))
            || (((this.SLOT_ID != null) && (o.SLOT_ID != null)) && (this.SLOT_ID.equals(o.SLOT_ID) == false))
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
  * return 23
  */
  public static int getStart_dateColumnSize() {
    
     return 23;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getStart_dateDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 93
  */
  public static int getStart_dateSQLType() {
    
    return 93;   
  }
    
 
  /**
  * get columnSize
  * return 23
  */
  public static int getEnd_dateColumnSize() {
    
     return 23;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getEnd_dateDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 93
  */
  public static int getEnd_dateSQLType() {
    
    return 93;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getFail_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getFail_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getFail_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 10
  */
  public static int getStatusColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getStatusDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getStatusSQLType() {
    
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
  * return 12
  */
  public static int getMeta_collection_nameColumnSize() {
    
     return 12;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMeta_collection_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMeta_collection_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 12
  */
  public static int getMeta_collection_set_nameColumnSize() {
    
     return 12;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMeta_collection_set_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMeta_collection_set_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 12
  */
  public static int getSettypeColumnSize() {
    
     return 12;   
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
  * return 10
  */
  public static int getSlot_idColumnSize() {
    
     return 10;   
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
  * return 4
  */
  public static int getSlot_idSQLType() {
    
    return 4;   
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
