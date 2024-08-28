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

public class Meta_transformation_tables implements Cloneable,RockDBObject  {

    private Long TRANSF_TABLE_ID;
    private String TRANSF_TABLE_NAME;
    private String DESCRIPTION;
    private String VERSION_NUMBER;
    private String IS_LOOKUP;
    private Long CONNECTION_ID;
    private Long TABLE_ID;
    private Long KEY_COLUMN_ID;
    private Long VALUE_COLUMN_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "TRANSF_TABLE_ID"    ,"VERSION_NUMBER"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_transformation_tables(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_transformation_tables(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.TRANSF_TABLE_ID = null;
         this.TRANSF_TABLE_NAME = null;
         this.DESCRIPTION = null;
         this.VERSION_NUMBER = null;
         this.IS_LOOKUP = null;
         this.CONNECTION_ID = null;
         this.TABLE_ID = null;
         this.KEY_COLUMN_ID = null;
         this.VALUE_COLUMN_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_transformation_tables(RockFactory rockFact   ,Long TRANSF_TABLE_ID ,String VERSION_NUMBER ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.TRANSF_TABLE_ID = TRANSF_TABLE_ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_transformation_tables o = (Meta_transformation_tables) it.next();

              this.TRANSF_TABLE_ID = o.getTransf_table_id();
              this.TRANSF_TABLE_NAME = o.getTransf_table_name();
              this.DESCRIPTION = o.getDescription();
              this.VERSION_NUMBER = o.getVersion_number();
              this.IS_LOOKUP = o.getIs_lookup();
              this.CONNECTION_ID = o.getConnection_id();
              this.TABLE_ID = o.getTable_id();
              this.KEY_COLUMN_ID = o.getKey_column_id();
              this.VALUE_COLUMN_ID = o.getValue_column_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transformation_tables");
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
  public Meta_transformation_tables(RockFactory rockFact, Meta_transformation_tables whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_transformation_tables o = (Meta_transformation_tables) it.next();
                this.TRANSF_TABLE_ID = o.getTransf_table_id();
                this.TRANSF_TABLE_NAME = o.getTransf_table_name();
                this.DESCRIPTION = o.getDescription();
                this.VERSION_NUMBER = o.getVersion_number();
                this.IS_LOOKUP = o.getIs_lookup();
                this.CONNECTION_ID = o.getConnection_id();
                this.TABLE_ID = o.getTable_id();
                this.KEY_COLUMN_ID = o.getKey_column_id();
                this.VALUE_COLUMN_ID = o.getValue_column_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transformation_tables");
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
    return "Meta_transformation_tables";
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
  public int updateDB(boolean useTimestamp, Meta_transformation_tables whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_transformation_tables whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_transformation_tables.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getTransf_table_id() { 
    return this.TRANSF_TABLE_ID;
  }
   public String getTransf_table_name() { 
    return this.TRANSF_TABLE_NAME;
  }
   public String getDescription() { 
    return this.DESCRIPTION;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public String getIs_lookup() { 
    return this.IS_LOOKUP;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public Long getTable_id() { 
    return this.TABLE_ID;
  }
   public Long getKey_column_id() { 
    return this.KEY_COLUMN_ID;
  }
   public Long getValue_column_id() { 
    return this.VALUE_COLUMN_ID;
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
     if (TRANSF_TABLE_ID == null)
      TRANSF_TABLE_ID = new Long (0);
     if (TRANSF_TABLE_NAME == null)
      TRANSF_TABLE_NAME = new String ("");
     if (DESCRIPTION == null)
      DESCRIPTION = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (IS_LOOKUP == null)
      IS_LOOKUP = new String ("");
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (TABLE_ID == null)
      TABLE_ID = new Long (0);
     if (KEY_COLUMN_ID == null)
      KEY_COLUMN_ID = new Long (0);
     if (VALUE_COLUMN_ID == null)
      VALUE_COLUMN_ID = new Long (0);
   }

   public void setTransf_table_id(Long TRANSF_TABLE_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSF_TABLE_ID,"TRANSF_TABLE_ID",2,38,0);
    }
    modifiedColumns.add("TRANSF_TABLE_ID");
    this.TRANSF_TABLE_ID = TRANSF_TABLE_ID;
  }
   public void setTransf_table_name(String TRANSF_TABLE_NAME) {
    if (validateData){
      DataValidator.validateData((Object)TRANSF_TABLE_NAME,"TRANSF_TABLE_NAME",12,30,0);
    }
    modifiedColumns.add("TRANSF_TABLE_NAME");
    this.TRANSF_TABLE_NAME = TRANSF_TABLE_NAME;
  }
   public void setDescription(String DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)DESCRIPTION,"DESCRIPTION",12,32000,0);
    }
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setIs_lookup(String IS_LOOKUP) {
    if (validateData){
      DataValidator.validateData((Object)IS_LOOKUP,"IS_LOOKUP",12,1,0);
    }
    modifiedColumns.add("IS_LOOKUP");
    this.IS_LOOKUP = IS_LOOKUP;
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
   public void setKey_column_id(Long KEY_COLUMN_ID) {
    if (validateData){
      DataValidator.validateData((Object)KEY_COLUMN_ID,"KEY_COLUMN_ID",2,38,0);
    }
    modifiedColumns.add("KEY_COLUMN_ID");
    this.KEY_COLUMN_ID = KEY_COLUMN_ID;
  }
   public void setValue_column_id(Long VALUE_COLUMN_ID) {
    if (validateData){
      DataValidator.validateData((Object)VALUE_COLUMN_ID,"VALUE_COLUMN_ID",2,38,0);
    }
    modifiedColumns.add("VALUE_COLUMN_ID");
    this.VALUE_COLUMN_ID = VALUE_COLUMN_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_transformation_tables o) {

         if ((((this.TRANSF_TABLE_ID == null) || (o.TRANSF_TABLE_ID == null)) && (this.TRANSF_TABLE_ID != o.TRANSF_TABLE_ID))
            || (((this.TRANSF_TABLE_NAME == null) || (o.TRANSF_TABLE_NAME == null)) && (this.TRANSF_TABLE_NAME != o.TRANSF_TABLE_NAME))
            || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.IS_LOOKUP == null) || (o.IS_LOOKUP == null)) && (this.IS_LOOKUP != o.IS_LOOKUP))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.TABLE_ID == null) || (o.TABLE_ID == null)) && (this.TABLE_ID != o.TABLE_ID))
            || (((this.KEY_COLUMN_ID == null) || (o.KEY_COLUMN_ID == null)) && (this.KEY_COLUMN_ID != o.KEY_COLUMN_ID))
            || (((this.VALUE_COLUMN_ID == null) || (o.VALUE_COLUMN_ID == null)) && (this.VALUE_COLUMN_ID != o.VALUE_COLUMN_ID))
          ){
    return false;
    } else
         if ((((this.TRANSF_TABLE_ID != null) && (o.TRANSF_TABLE_ID != null)) && (this.TRANSF_TABLE_ID.equals(o.TRANSF_TABLE_ID) == false))
            || (((this.TRANSF_TABLE_NAME != null) && (o.TRANSF_TABLE_NAME != null)) && (this.TRANSF_TABLE_NAME.equals(o.TRANSF_TABLE_NAME) == false))
            || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.IS_LOOKUP != null) && (o.IS_LOOKUP != null)) && (this.IS_LOOKUP.equals(o.IS_LOOKUP) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.TABLE_ID != null) && (o.TABLE_ID != null)) && (this.TABLE_ID.equals(o.TABLE_ID) == false))
            || (((this.KEY_COLUMN_ID != null) && (o.KEY_COLUMN_ID != null)) && (this.KEY_COLUMN_ID.equals(o.KEY_COLUMN_ID) == false))
            || (((this.VALUE_COLUMN_ID != null) && (o.VALUE_COLUMN_ID != null)) && (this.VALUE_COLUMN_ID.equals(o.VALUE_COLUMN_ID) == false))
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
  public static int getTransf_table_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTransf_table_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getTransf_table_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getTransf_table_nameColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTransf_table_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTransf_table_nameSQLType() {
    
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
  * return 1
  */
  public static int getIs_lookupColumnSize() {
    
     return 1;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getIs_lookupDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getIs_lookupSQLType() {
    
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
  public static int getKey_column_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getKey_column_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getKey_column_idSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getValue_column_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getValue_column_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getValue_column_idSQLType() {
    
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
