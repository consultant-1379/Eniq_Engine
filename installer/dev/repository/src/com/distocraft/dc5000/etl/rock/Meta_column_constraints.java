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

public class Meta_column_constraints implements Cloneable,RockDBObject  {

    private Long ID;
    private String LOW_VALUE;
    private String HIGH_VALUE;
    private String VERSION_NUMBER;
    private Long CONNECTION_ID;
    private Long TABLE_ID;
    private Long COLUMN_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "ID"    ,"VERSION_NUMBER"    ,"CONNECTION_ID"    ,"TABLE_ID"    ,"COLUMN_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_column_constraints(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_column_constraints(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.ID = null;
         this.LOW_VALUE = null;
         this.HIGH_VALUE = null;
         this.VERSION_NUMBER = null;
         this.CONNECTION_ID = null;
         this.TABLE_ID = null;
         this.COLUMN_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_column_constraints(RockFactory rockFact   ,Long ID ,String VERSION_NUMBER ,Long CONNECTION_ID ,Long TABLE_ID ,Long COLUMN_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.ID = ID;
            this.VERSION_NUMBER = VERSION_NUMBER;
            this.CONNECTION_ID = CONNECTION_ID;
            this.TABLE_ID = TABLE_ID;
            this.COLUMN_ID = COLUMN_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_column_constraints o = (Meta_column_constraints) it.next();

              this.ID = o.getId();
              this.LOW_VALUE = o.getLow_value();
              this.HIGH_VALUE = o.getHigh_value();
              this.VERSION_NUMBER = o.getVersion_number();
              this.CONNECTION_ID = o.getConnection_id();
              this.TABLE_ID = o.getTable_id();
              this.COLUMN_ID = o.getColumn_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_column_constraints");
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
  public Meta_column_constraints(RockFactory rockFact, Meta_column_constraints whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_column_constraints o = (Meta_column_constraints) it.next();
                this.ID = o.getId();
                this.LOW_VALUE = o.getLow_value();
                this.HIGH_VALUE = o.getHigh_value();
                this.VERSION_NUMBER = o.getVersion_number();
                this.CONNECTION_ID = o.getConnection_id();
                this.TABLE_ID = o.getTable_id();
                this.COLUMN_ID = o.getColumn_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_column_constraints");
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
    return "Meta_column_constraints";
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
  public int updateDB(boolean useTimestamp, Meta_column_constraints whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_column_constraints whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_column_constraints.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public Long getId() { 
    return this.ID;
  }
   public String getLow_value() { 
    return this.LOW_VALUE;
  }
   public String getHigh_value() { 
    return this.HIGH_VALUE;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public Long getTable_id() { 
    return this.TABLE_ID;
  }
   public Long getColumn_id() { 
    return this.COLUMN_ID;
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
     if (LOW_VALUE == null)
      LOW_VALUE = new String ("");
     if (HIGH_VALUE == null)
      HIGH_VALUE = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (TABLE_ID == null)
      TABLE_ID = new Long (0);
     if (COLUMN_ID == null)
      COLUMN_ID = new Long (0);
   }

   public void setId(Long ID) {
    if (validateData){
      DataValidator.validateData((Object)ID,"ID",2,38,0);
    }
    modifiedColumns.add("ID");
    this.ID = ID;
  }
   public void setLow_value(String LOW_VALUE) {
    if (validateData){
      DataValidator.validateData((Object)LOW_VALUE,"LOW_VALUE",12,30,0);
    }
    modifiedColumns.add("LOW_VALUE");
    this.LOW_VALUE = LOW_VALUE;
  }
   public void setHigh_value(String HIGH_VALUE) {
    if (validateData){
      DataValidator.validateData((Object)HIGH_VALUE,"HIGH_VALUE",12,30,0);
    }
    modifiedColumns.add("HIGH_VALUE");
    this.HIGH_VALUE = HIGH_VALUE;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
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
   public void setColumn_id(Long COLUMN_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLUMN_ID,"COLUMN_ID",2,38,0);
    }
    modifiedColumns.add("COLUMN_ID");
    this.COLUMN_ID = COLUMN_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_column_constraints o) {

         if ((((this.ID == null) || (o.ID == null)) && (this.ID != o.ID))
            || (((this.LOW_VALUE == null) || (o.LOW_VALUE == null)) && (this.LOW_VALUE != o.LOW_VALUE))
            || (((this.HIGH_VALUE == null) || (o.HIGH_VALUE == null)) && (this.HIGH_VALUE != o.HIGH_VALUE))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
            || (((this.TABLE_ID == null) || (o.TABLE_ID == null)) && (this.TABLE_ID != o.TABLE_ID))
            || (((this.COLUMN_ID == null) || (o.COLUMN_ID == null)) && (this.COLUMN_ID != o.COLUMN_ID))
          ){
    return false;
    } else
         if ((((this.ID != null) && (o.ID != null)) && (this.ID.equals(o.ID) == false))
            || (((this.LOW_VALUE != null) && (o.LOW_VALUE != null)) && (this.LOW_VALUE.equals(o.LOW_VALUE) == false))
            || (((this.HIGH_VALUE != null) && (o.HIGH_VALUE != null)) && (this.HIGH_VALUE.equals(o.HIGH_VALUE) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
            || (((this.TABLE_ID != null) && (o.TABLE_ID != null)) && (this.TABLE_ID.equals(o.TABLE_ID) == false))
            || (((this.COLUMN_ID != null) && (o.COLUMN_ID != null)) && (this.COLUMN_ID.equals(o.COLUMN_ID) == false))
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
  * return 30
  */
  public static int getLow_valueColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getLow_valueDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getLow_valueSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getHigh_valueColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getHigh_valueDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getHigh_valueSQLType() {
    
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
  public static int getColumn_idColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getColumn_idDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getColumn_idSQLType() {
    
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
