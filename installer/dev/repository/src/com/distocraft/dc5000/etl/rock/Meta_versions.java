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

public class Meta_versions implements Cloneable,RockDBObject  {

    private String VERSION_NUMBER;
    private String DESCRIPTION;
    private String CURRENT_FLAG;
    private String IS_PREDEFINED;
    private String ENGINE_SERVER;
    private String MAIL_SERVER;
    private String SCHEDULER_SERVER;
    private Long MAIL_SERVER_PORT;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSION_NUMBER"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_versions(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_versions(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.VERSION_NUMBER = null;
         this.DESCRIPTION = null;
         this.CURRENT_FLAG = null;
         this.IS_PREDEFINED = null;
         this.ENGINE_SERVER = null;
         this.MAIL_SERVER = null;
         this.SCHEDULER_SERVER = null;
         this.MAIL_SERVER_PORT = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_versions(RockFactory rockFact   ,String VERSION_NUMBER ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSION_NUMBER = VERSION_NUMBER;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_versions o = (Meta_versions) it.next();

              this.VERSION_NUMBER = o.getVersion_number();
              this.DESCRIPTION = o.getDescription();
              this.CURRENT_FLAG = o.getCurrent_flag();
              this.IS_PREDEFINED = o.getIs_predefined();
              this.ENGINE_SERVER = o.getEngine_server();
              this.MAIL_SERVER = o.getMail_server();
              this.SCHEDULER_SERVER = o.getScheduler_server();
              this.MAIL_SERVER_PORT = o.getMail_server_port();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_versions");
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
  public Meta_versions(RockFactory rockFact, Meta_versions whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_versions o = (Meta_versions) it.next();
                this.VERSION_NUMBER = o.getVersion_number();
                this.DESCRIPTION = o.getDescription();
                this.CURRENT_FLAG = o.getCurrent_flag();
                this.IS_PREDEFINED = o.getIs_predefined();
                this.ENGINE_SERVER = o.getEngine_server();
                this.MAIL_SERVER = o.getMail_server();
                this.SCHEDULER_SERVER = o.getScheduler_server();
                this.MAIL_SERVER_PORT = o.getMail_server_port();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_versions");
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
    return "Meta_versions";
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
  public int updateDB(boolean useTimestamp, Meta_versions whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_versions whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_versions.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public String getDescription() { 
    return this.DESCRIPTION;
  }
   public String getCurrent_flag() { 
    return this.CURRENT_FLAG;
  }
   public String getIs_predefined() { 
    return this.IS_PREDEFINED;
  }
   public String getEngine_server() { 
    return this.ENGINE_SERVER;
  }
   public String getMail_server() { 
    return this.MAIL_SERVER;
  }
   public String getScheduler_server() { 
    return this.SCHEDULER_SERVER;
  }
   public Long getMail_server_port() { 
    return this.MAIL_SERVER_PORT;
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
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (DESCRIPTION == null)
      DESCRIPTION = new String ("");
     if (CURRENT_FLAG == null)
      CURRENT_FLAG = new String ("");
     if (IS_PREDEFINED == null)
      IS_PREDEFINED = new String ("");
     if (ENGINE_SERVER == null)
      ENGINE_SERVER = new String ("");
     if (MAIL_SERVER == null)
      MAIL_SERVER = new String ("");
     if (SCHEDULER_SERVER == null)
      SCHEDULER_SERVER = new String ("");
     if (MAIL_SERVER_PORT == null)
      MAIL_SERVER_PORT = new Long (0);
   }

   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setDescription(String DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)DESCRIPTION,"DESCRIPTION",12,32000,0);
    }
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }
   public void setCurrent_flag(String CURRENT_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)CURRENT_FLAG,"CURRENT_FLAG",12,1,0);
    }
    modifiedColumns.add("CURRENT_FLAG");
    this.CURRENT_FLAG = CURRENT_FLAG;
  }
   public void setIs_predefined(String IS_PREDEFINED) {
    if (validateData){
      DataValidator.validateData((Object)IS_PREDEFINED,"IS_PREDEFINED",12,1,0);
    }
    modifiedColumns.add("IS_PREDEFINED");
    this.IS_PREDEFINED = IS_PREDEFINED;
  }
   public void setEngine_server(String ENGINE_SERVER) {
    if (validateData){
      DataValidator.validateData((Object)ENGINE_SERVER,"ENGINE_SERVER",12,50,0);
    }
    modifiedColumns.add("ENGINE_SERVER");
    this.ENGINE_SERVER = ENGINE_SERVER;
  }
   public void setMail_server(String MAIL_SERVER) {
    if (validateData){
      DataValidator.validateData((Object)MAIL_SERVER,"MAIL_SERVER",12,100,0);
    }
    modifiedColumns.add("MAIL_SERVER");
    this.MAIL_SERVER = MAIL_SERVER;
  }
   public void setScheduler_server(String SCHEDULER_SERVER) {
    if (validateData){
      DataValidator.validateData((Object)SCHEDULER_SERVER,"SCHEDULER_SERVER",12,50,0);
    }
    modifiedColumns.add("SCHEDULER_SERVER");
    this.SCHEDULER_SERVER = SCHEDULER_SERVER;
  }
   public void setMail_server_port(Long MAIL_SERVER_PORT) {
    if (validateData){
      DataValidator.validateData((Object)MAIL_SERVER_PORT,"MAIL_SERVER_PORT",2,5,0);
    }
    modifiedColumns.add("MAIL_SERVER_PORT");
    this.MAIL_SERVER_PORT = MAIL_SERVER_PORT;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_versions o) {

         if ((((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
            || (((this.CURRENT_FLAG == null) || (o.CURRENT_FLAG == null)) && (this.CURRENT_FLAG != o.CURRENT_FLAG))
            || (((this.IS_PREDEFINED == null) || (o.IS_PREDEFINED == null)) && (this.IS_PREDEFINED != o.IS_PREDEFINED))
            || (((this.ENGINE_SERVER == null) || (o.ENGINE_SERVER == null)) && (this.ENGINE_SERVER != o.ENGINE_SERVER))
            || (((this.MAIL_SERVER == null) || (o.MAIL_SERVER == null)) && (this.MAIL_SERVER != o.MAIL_SERVER))
            || (((this.SCHEDULER_SERVER == null) || (o.SCHEDULER_SERVER == null)) && (this.SCHEDULER_SERVER != o.SCHEDULER_SERVER))
            || (((this.MAIL_SERVER_PORT == null) || (o.MAIL_SERVER_PORT == null)) && (this.MAIL_SERVER_PORT != o.MAIL_SERVER_PORT))
          ){
    return false;
    } else
         if ((((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
            || (((this.CURRENT_FLAG != null) && (o.CURRENT_FLAG != null)) && (this.CURRENT_FLAG.equals(o.CURRENT_FLAG) == false))
            || (((this.IS_PREDEFINED != null) && (o.IS_PREDEFINED != null)) && (this.IS_PREDEFINED.equals(o.IS_PREDEFINED) == false))
            || (((this.ENGINE_SERVER != null) && (o.ENGINE_SERVER != null)) && (this.ENGINE_SERVER.equals(o.ENGINE_SERVER) == false))
            || (((this.MAIL_SERVER != null) && (o.MAIL_SERVER != null)) && (this.MAIL_SERVER.equals(o.MAIL_SERVER) == false))
            || (((this.SCHEDULER_SERVER != null) && (o.SCHEDULER_SERVER != null)) && (this.SCHEDULER_SERVER.equals(o.SCHEDULER_SERVER) == false))
            || (((this.MAIL_SERVER_PORT != null) && (o.MAIL_SERVER_PORT != null)) && (this.MAIL_SERVER_PORT.equals(o.MAIL_SERVER_PORT) == false))
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
  * return 32000
  */
  public static int getDescriptionColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDescriptionDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDescriptionSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getCurrent_flagColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCurrent_flagDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getCurrent_flagSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 1
  */
  public static int getIs_predefinedColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getIs_predefinedDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getIs_predefinedSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 50
  */
  public static int getEngine_serverColumnSize() {
    
     return 50;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getEngine_serverDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getEngine_serverSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 100
  */
  public static int getMail_serverColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMail_serverDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getMail_serverSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 50
  */
  public static int getScheduler_serverColumnSize() {
    
     return 50;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getScheduler_serverDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getScheduler_serverSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 5
  */
  public static int getMail_server_portColumnSize() {
    
     return 5;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMail_server_portDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getMail_server_portSQLType() {
    
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
