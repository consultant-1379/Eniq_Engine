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

public class Meta_sql_loads implements Cloneable,RockDBObject  {

    private String INPUT_FILE;
    private String CTL_FILE;
    private Long COLLECTION_SET_ID;
    private Long COLLECTION_ID;
    private Long CONNECTION_ID;
    private String DIS_FILE;
    private String BAD_FILE;
    private String LOAD_TYPE;
    private String TEXT;
    private String DELIM;
    private String SQLLDR_CMD;
    private String LOAD_OPTION;
    private String VERSION_NUMBER;
    private Long TRANSFER_ACTION_ID;
    private Long TABLE_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "COLLECTION_SET_ID"    ,"COLLECTION_ID"    ,"CONNECTION_ID"    ,"VERSION_NUMBER"    ,"TRANSFER_ACTION_ID"    ,"TABLE_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_sql_loads(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_sql_loads(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.INPUT_FILE = null;
         this.CTL_FILE = null;
         this.COLLECTION_SET_ID = null;
         this.COLLECTION_ID = null;
         this.CONNECTION_ID = null;
         this.DIS_FILE = null;
         this.BAD_FILE = null;
         this.LOAD_TYPE = null;
         this.TEXT = null;
         this.DELIM = null;
         this.SQLLDR_CMD = null;
         this.LOAD_OPTION = null;
         this.VERSION_NUMBER = null;
         this.TRANSFER_ACTION_ID = null;
         this.TABLE_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_sql_loads(RockFactory rockFact   ,Long COLLECTION_SET_ID ,Long COLLECTION_ID ,Long CONNECTION_ID ,String VERSION_NUMBER ,Long TRANSFER_ACTION_ID ,Long TABLE_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.COLLECTION_SET_ID = COLLECTION_SET_ID;
            this.COLLECTION_ID = COLLECTION_ID;
            this.CONNECTION_ID = CONNECTION_ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
            this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
            this.TABLE_ID = TABLE_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_sql_loads o = (Meta_sql_loads) it.next();

              this.INPUT_FILE = o.getInput_file();
              this.CTL_FILE = o.getCtl_file();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.CONNECTION_ID = o.getConnection_id();
              this.DIS_FILE = o.getDis_file();
              this.BAD_FILE = o.getBad_file();
              this.LOAD_TYPE = o.getLoad_type();
              this.TEXT = o.getText();
              this.DELIM = o.getDelim();
              this.SQLLDR_CMD = o.getSqlldr_cmd();
              this.LOAD_OPTION = o.getLoad_option();
              this.VERSION_NUMBER = o.getVersion_number();
              this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
              this.TABLE_ID = o.getTable_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_sql_loads");
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
  public Meta_sql_loads(RockFactory rockFact, Meta_sql_loads whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_sql_loads o = (Meta_sql_loads) it.next();
                this.INPUT_FILE = o.getInput_file();
                this.CTL_FILE = o.getCtl_file();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.CONNECTION_ID = o.getConnection_id();
                this.DIS_FILE = o.getDis_file();
                this.BAD_FILE = o.getBad_file();
                this.LOAD_TYPE = o.getLoad_type();
                this.TEXT = o.getText();
                this.DELIM = o.getDelim();
                this.SQLLDR_CMD = o.getSqlldr_cmd();
                this.LOAD_OPTION = o.getLoad_option();
                this.VERSION_NUMBER = o.getVersion_number();
                this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
                this.TABLE_ID = o.getTable_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_sql_loads");
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
    return "Meta_sql_loads";
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
  public int updateDB(boolean useTimestamp, Meta_sql_loads whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_sql_loads whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_sql_loads.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getInput_file() { 
    return this.INPUT_FILE;
  }
   public String getCtl_file() { 
    return this.CTL_FILE;
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
   public String getDis_file() { 
    return this.DIS_FILE;
  }
   public String getBad_file() { 
    return this.BAD_FILE;
  }
   public String getLoad_type() { 
    return this.LOAD_TYPE;
  }
   public String getText() { 
    return this.TEXT;
  }
   public String getDelim() { 
    return this.DELIM;
  }
   public String getSqlldr_cmd() { 
    return this.SQLLDR_CMD;
  }
   public String getLoad_option() { 
    return this.LOAD_OPTION;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getTransfer_action_id() { 
    return this.TRANSFER_ACTION_ID;
  }
   public Long getTable_id() { 
    return this.TABLE_ID;
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
     if (INPUT_FILE == null)
      INPUT_FILE = new String ("");
     if (CTL_FILE == null)
      CTL_FILE = new String ("");
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (DIS_FILE == null)
      DIS_FILE = new String ("");
     if (BAD_FILE == null)
      BAD_FILE = new String ("");
     if (LOAD_TYPE == null)
      LOAD_TYPE = new String ("");
     if (TEXT == null)
      TEXT = new String ("");
     if (DELIM == null)
      DELIM = new String ("");
     if (SQLLDR_CMD == null)
      SQLLDR_CMD = new String ("");
     if (LOAD_OPTION == null)
      LOAD_OPTION = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (TRANSFER_ACTION_ID == null)
      TRANSFER_ACTION_ID = new Long (0);
     if (TABLE_ID == null)
      TABLE_ID = new Long (0);
   }

   public void setInput_file(String INPUT_FILE) {
    if (validateData){
      DataValidator.validateData((Object)INPUT_FILE,"INPUT_FILE",12,200,0);
    }
    modifiedColumns.add("INPUT_FILE");
    this.INPUT_FILE = INPUT_FILE;
  }
   public void setCtl_file(String CTL_FILE) {
    if (validateData){
      DataValidator.validateData((Object)CTL_FILE,"CTL_FILE",12,200,0);
    }
    modifiedColumns.add("CTL_FILE");
    this.CTL_FILE = CTL_FILE;
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
   public void setDis_file(String DIS_FILE) {
    if (validateData){
      DataValidator.validateData((Object)DIS_FILE,"DIS_FILE",12,200,0);
    }
    modifiedColumns.add("DIS_FILE");
    this.DIS_FILE = DIS_FILE;
  }
   public void setBad_file(String BAD_FILE) {
    if (validateData){
      DataValidator.validateData((Object)BAD_FILE,"BAD_FILE",12,200,0);
    }
    modifiedColumns.add("BAD_FILE");
    this.BAD_FILE = BAD_FILE;
  }
   public void setLoad_type(String LOAD_TYPE) {
    if (validateData){
      DataValidator.validateData((Object)LOAD_TYPE,"LOAD_TYPE",12,30,0);
    }
    modifiedColumns.add("LOAD_TYPE");
    this.LOAD_TYPE = LOAD_TYPE;
  }
   public void setText(String TEXT) {
    if (validateData){
      DataValidator.validateData((Object)TEXT,"TEXT",12,2000,0);
    }
    modifiedColumns.add("TEXT");
    this.TEXT = TEXT;
  }
   public void setDelim(String DELIM) {
    if (validateData){
      DataValidator.validateData((Object)DELIM,"DELIM",12,1,0);
    }
    modifiedColumns.add("DELIM");
    this.DELIM = DELIM;
  }
   public void setSqlldr_cmd(String SQLLDR_CMD) {
    if (validateData){
      DataValidator.validateData((Object)SQLLDR_CMD,"SQLLDR_CMD",12,200,0);
    }
    modifiedColumns.add("SQLLDR_CMD");
    this.SQLLDR_CMD = SQLLDR_CMD;
  }
   public void setLoad_option(String LOAD_OPTION) {
    if (validateData){
      DataValidator.validateData((Object)LOAD_OPTION,"LOAD_OPTION",12,30,0);
    }
    modifiedColumns.add("LOAD_OPTION");
    this.LOAD_OPTION = LOAD_OPTION;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
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
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_sql_loads o) {

         if ((((this.INPUT_FILE == null) || (o.INPUT_FILE == null)) && (this.INPUT_FILE != o.INPUT_FILE))
            || (((this.CTL_FILE == null) || (o.CTL_FILE == null)) && (this.CTL_FILE != o.CTL_FILE))
            || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
            || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.DIS_FILE == null) || (o.DIS_FILE == null)) && (this.DIS_FILE != o.DIS_FILE))
            || (((this.BAD_FILE == null) || (o.BAD_FILE == null)) && (this.BAD_FILE != o.BAD_FILE))
            || (((this.LOAD_TYPE == null) || (o.LOAD_TYPE == null)) && (this.LOAD_TYPE != o.LOAD_TYPE))
            || (((this.TEXT == null) || (o.TEXT == null)) && (this.TEXT != o.TEXT))
            || (((this.DELIM == null) || (o.DELIM == null)) && (this.DELIM != o.DELIM))
            || (((this.SQLLDR_CMD == null) || (o.SQLLDR_CMD == null)) && (this.SQLLDR_CMD != o.SQLLDR_CMD))
            || (((this.LOAD_OPTION == null) || (o.LOAD_OPTION == null)) && (this.LOAD_OPTION != o.LOAD_OPTION))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.TRANSFER_ACTION_ID == null) || (o.TRANSFER_ACTION_ID == null)) && (this.TRANSFER_ACTION_ID != o.TRANSFER_ACTION_ID))
            || (((this.TABLE_ID == null) || (o.TABLE_ID == null)) && (this.TABLE_ID != o.TABLE_ID))
          ){
    return false;
    } else
         if ((((this.INPUT_FILE != null) && (o.INPUT_FILE != null)) && (this.INPUT_FILE.equals(o.INPUT_FILE) == false))
            || (((this.CTL_FILE != null) && (o.CTL_FILE != null)) && (this.CTL_FILE.equals(o.CTL_FILE) == false))
            || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID.equals(o.COLLECTION_SET_ID) == false))
            || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.DIS_FILE != null) && (o.DIS_FILE != null)) && (this.DIS_FILE.equals(o.DIS_FILE) == false))
            || (((this.BAD_FILE != null) && (o.BAD_FILE != null)) && (this.BAD_FILE.equals(o.BAD_FILE) == false))
            || (((this.LOAD_TYPE != null) && (o.LOAD_TYPE != null)) && (this.LOAD_TYPE.equals(o.LOAD_TYPE) == false))
            || (((this.TEXT != null) && (o.TEXT != null)) && (this.TEXT.equals(o.TEXT) == false))
            || (((this.DELIM != null) && (o.DELIM != null)) && (this.DELIM.equals(o.DELIM) == false))
            || (((this.SQLLDR_CMD != null) && (o.SQLLDR_CMD != null)) && (this.SQLLDR_CMD.equals(o.SQLLDR_CMD) == false))
            || (((this.LOAD_OPTION != null) && (o.LOAD_OPTION != null)) && (this.LOAD_OPTION.equals(o.LOAD_OPTION) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.TRANSFER_ACTION_ID != null) && (o.TRANSFER_ACTION_ID != null)) && (this.TRANSFER_ACTION_ID.equals(o.TRANSFER_ACTION_ID) == false))
            || (((this.TABLE_ID != null) && (o.TABLE_ID != null)) && (this.TABLE_ID.equals(o.TABLE_ID) == false))
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
  * return 200
  */
  public static int getInput_fileColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getInput_fileDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getInput_fileSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getCtl_fileColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCtl_fileDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCtl_fileSQLType() {
    
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
  * return 200
  */
  public static int getDis_fileColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDis_fileDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDis_fileSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getBad_fileColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getBad_fileDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getBad_fileSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getLoad_typeColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getLoad_typeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getLoad_typeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 2000
  */
  public static int getTextColumnSize() {
    
     return 2000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTextDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTextSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getDelimColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDelimDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDelimSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getSqlldr_cmdColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getSqlldr_cmdDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getSqlldr_cmdSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getLoad_optionColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getLoad_optionDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getLoad_optionSQLType() {
    
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
