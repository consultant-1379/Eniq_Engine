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

public class Meta_databases implements Cloneable,RockDBObject  {

    private String USERNAME;
    private String VERSION_NUMBER;
    private String TYPE_NAME;
    private Long CONNECTION_ID;
    private String CONNECTION_NAME;
    private String CONNECTION_STRING;
    private String PASSWORD;
    private String DESCRIPTION;
    private String DRIVER_NAME;
    private String DB_LINK_NAME;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSION_NUMBER"    ,"CONNECTION_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_databases(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_databases(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.USERNAME = null;
         this.VERSION_NUMBER = null;
         this.TYPE_NAME = null;
         this.CONNECTION_ID = null;
         this.CONNECTION_NAME = null;
         this.CONNECTION_STRING = null;
         this.PASSWORD = null;
         this.DESCRIPTION = null;
         this.DRIVER_NAME = null;
         this.DB_LINK_NAME = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_databases(RockFactory rockFact   ,String VERSION_NUMBER ,Long CONNECTION_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSION_NUMBER = VERSION_NUMBER;
            this.CONNECTION_ID = CONNECTION_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_databases o = (Meta_databases) it.next();

              this.USERNAME = o.getUsername();
              this.VERSION_NUMBER = o.getVersion_number();
              this.TYPE_NAME = o.getType_name();
              this.CONNECTION_ID = o.getConnection_id();
              this.CONNECTION_NAME = o.getConnection_name();
              this.CONNECTION_STRING = o.getConnection_string();
              this.PASSWORD = o.getPassword();
              this.DESCRIPTION = o.getDescription();
              this.DRIVER_NAME = o.getDriver_name();
              this.DB_LINK_NAME = o.getDb_link_name();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_databases");
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
  public Meta_databases(RockFactory rockFact, Meta_databases whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_databases o = (Meta_databases) it.next();
                this.USERNAME = o.getUsername();
                this.VERSION_NUMBER = o.getVersion_number();
                this.TYPE_NAME = o.getType_name();
                this.CONNECTION_ID = o.getConnection_id();
                this.CONNECTION_NAME = o.getConnection_name();
                this.CONNECTION_STRING = o.getConnection_string();
                this.PASSWORD = o.getPassword();
                this.DESCRIPTION = o.getDescription();
                this.DRIVER_NAME = o.getDriver_name();
                this.DB_LINK_NAME = o.getDb_link_name();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_databases");
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
    return "Meta_databases";
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
  public int updateDB(boolean useTimestamp, Meta_databases whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_databases whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_databases.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getUsername() { 
    return this.USERNAME;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public String getType_name() { 
    return this.TYPE_NAME;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public String getConnection_name() { 
    return this.CONNECTION_NAME;
  }
   public String getConnection_string() { 
    return this.CONNECTION_STRING;
  }
   public String getPassword() { 
    return this.PASSWORD;
  }
   public String getDescription() { 
    return this.DESCRIPTION;
  }
   public String getDriver_name() { 
    return this.DRIVER_NAME;
  }
   public String getDb_link_name() { 
    return this.DB_LINK_NAME;
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
     if (USERNAME == null)
      USERNAME = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (TYPE_NAME == null)
      TYPE_NAME = new String ("");
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (CONNECTION_NAME == null)
      CONNECTION_NAME = new String ("");
     if (CONNECTION_STRING == null)
      CONNECTION_STRING = new String ("");
     if (PASSWORD == null)
      PASSWORD = new String ("");
     if (DESCRIPTION == null)
      DESCRIPTION = new String ("");
     if (DRIVER_NAME == null)
      DRIVER_NAME = new String ("");
     if (DB_LINK_NAME == null)
      DB_LINK_NAME = new String ("");
   }

   public void setUsername(String USERNAME) {
    if (validateData){
      DataValidator.validateData((Object)USERNAME,"USERNAME",12,30,0);
    }
    modifiedColumns.add("USERNAME");
    this.USERNAME = USERNAME;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setType_name(String TYPE_NAME) {
    if (validateData){
      DataValidator.validateData((Object)TYPE_NAME,"TYPE_NAME",12,15,0);
    }
    modifiedColumns.add("TYPE_NAME");
    this.TYPE_NAME = TYPE_NAME;
  }
   public void setConnection_id(Long CONNECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_ID,"CONNECTION_ID",2,38,0);
    }
    modifiedColumns.add("CONNECTION_ID");
    this.CONNECTION_ID = CONNECTION_ID;
  }
   public void setConnection_name(String CONNECTION_NAME) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_NAME,"CONNECTION_NAME",12,30,0);
    }
    modifiedColumns.add("CONNECTION_NAME");
    this.CONNECTION_NAME = CONNECTION_NAME;
  }
   public void setConnection_string(String CONNECTION_STRING) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_STRING,"CONNECTION_STRING",12,200,0);
    }
    modifiedColumns.add("CONNECTION_STRING");
    this.CONNECTION_STRING = CONNECTION_STRING;
  }
   public void setPassword(String PASSWORD) {
    if (validateData){
      DataValidator.validateData((Object)PASSWORD,"PASSWORD",12,30,0);
    }
    modifiedColumns.add("PASSWORD");
    this.PASSWORD = PASSWORD;
  }
   public void setDescription(String DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)DESCRIPTION,"DESCRIPTION",12,32000,0);
    }
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }
   public void setDriver_name(String DRIVER_NAME) {
    if (validateData){
      DataValidator.validateData((Object)DRIVER_NAME,"DRIVER_NAME",12,100,0);
    }
    modifiedColumns.add("DRIVER_NAME");
    this.DRIVER_NAME = DRIVER_NAME;
  }
   public void setDb_link_name(String DB_LINK_NAME) {
    if (validateData){
      DataValidator.validateData((Object)DB_LINK_NAME,"DB_LINK_NAME",12,128,0);
    }
    modifiedColumns.add("DB_LINK_NAME");
    this.DB_LINK_NAME = DB_LINK_NAME;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_databases o) {

         if ((((this.USERNAME == null) || (o.USERNAME == null)) && (this.USERNAME != o.USERNAME))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.TYPE_NAME == null) || (o.TYPE_NAME == null)) && (this.TYPE_NAME != o.TYPE_NAME))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.CONNECTION_NAME == null) || (o.CONNECTION_NAME == null)) && (this.CONNECTION_NAME != o.CONNECTION_NAME))
            || (((this.CONNECTION_STRING == null) || (o.CONNECTION_STRING == null)) && (this.CONNECTION_STRING != o.CONNECTION_STRING))
            || (((this.PASSWORD == null) || (o.PASSWORD == null)) && (this.PASSWORD != o.PASSWORD))
            || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
            || (((this.DRIVER_NAME == null) || (o.DRIVER_NAME == null)) && (this.DRIVER_NAME != o.DRIVER_NAME))
            || (((this.DB_LINK_NAME == null) || (o.DB_LINK_NAME == null)) && (this.DB_LINK_NAME != o.DB_LINK_NAME))
          ){
    return false;
    } else
         if ((((this.USERNAME != null) && (o.USERNAME != null)) && (this.USERNAME.equals(o.USERNAME) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.TYPE_NAME != null) && (o.TYPE_NAME != null)) && (this.TYPE_NAME.equals(o.TYPE_NAME) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.CONNECTION_NAME != null) && (o.CONNECTION_NAME != null)) && (this.CONNECTION_NAME.equals(o.CONNECTION_NAME) == false))
            || (((this.CONNECTION_STRING != null) && (o.CONNECTION_STRING != null)) && (this.CONNECTION_STRING.equals(o.CONNECTION_STRING) == false))
            || (((this.PASSWORD != null) && (o.PASSWORD != null)) && (this.PASSWORD.equals(o.PASSWORD) == false))
            || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
            || (((this.DRIVER_NAME != null) && (o.DRIVER_NAME != null)) && (this.DRIVER_NAME.equals(o.DRIVER_NAME) == false))
            || (((this.DB_LINK_NAME != null) && (o.DB_LINK_NAME != null)) && (this.DB_LINK_NAME.equals(o.DB_LINK_NAME) == false))
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
  * return 30
  */
  public static int getUsernameColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getUsernameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getUsernameSQLType() {
    
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
  * return 15
  */
  public static int getType_nameColumnSize() {
    
     return 15;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getType_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getType_nameSQLType() {
    
    return 12;   
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
  * return 30
  */
  public static int getConnection_nameColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getConnection_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getConnection_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 200
  */
  public static int getConnection_stringColumnSize() {
    
     return 200;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getConnection_stringDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getConnection_stringSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getPasswordColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getPasswordDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getPasswordSQLType() {
    
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
  * return 100
  */
  public static int getDriver_nameColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDriver_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDriver_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getDb_link_nameColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDb_link_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDb_link_nameSQLType() {
    
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
