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

public class Meta_transf_table_values implements Cloneable,RockDBObject  {

    private String OLD_VALUE;
    private String NEW_VALUE;
    private String VERSION_NUMBER;
    private Long TRANSF_TABLE_ID;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "OLD_VALUE"    ,"VERSION_NUMBER"    ,"TRANSF_TABLE_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set modifiedColumns = new HashSet();
  
  private boolean validateData = false;

  public Meta_transf_table_values(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_transf_table_values(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.OLD_VALUE = null;
         this.NEW_VALUE = null;
         this.VERSION_NUMBER = null;
         this.TRANSF_TABLE_ID = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_transf_table_values(RockFactory rockFact   ,String OLD_VALUE ,String VERSION_NUMBER ,Long TRANSF_TABLE_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.OLD_VALUE = OLD_VALUE;
            this.VERSION_NUMBER = VERSION_NUMBER;
            this.TRANSF_TABLE_ID = TRANSF_TABLE_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_transf_table_values o = (Meta_transf_table_values) it.next();

              this.OLD_VALUE = o.getOld_value();
              this.NEW_VALUE = o.getNew_value();
              this.VERSION_NUMBER = o.getVersion_number();
              this.TRANSF_TABLE_ID = o.getTransf_table_id();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transf_table_values");
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
  public Meta_transf_table_values(RockFactory rockFact, Meta_transf_table_values whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_transf_table_values o = (Meta_transf_table_values) it.next();
                this.OLD_VALUE = o.getOld_value();
                this.NEW_VALUE = o.getNew_value();
                this.VERSION_NUMBER = o.getVersion_number();
                this.TRANSF_TABLE_ID = o.getTransf_table_id();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transf_table_values");
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
    return "Meta_transf_table_values";
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
  public int updateDB(boolean useTimestamp, Meta_transf_table_values whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_transf_table_values whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_transf_table_values.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getOld_value() { 
    return this.OLD_VALUE;
  }
   public String getNew_value() { 
    return this.NEW_VALUE;
  }
   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getTransf_table_id() { 
    return this.TRANSF_TABLE_ID;
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
     if (OLD_VALUE == null)
      OLD_VALUE = new String ("");
     if (NEW_VALUE == null)
      NEW_VALUE = new String ("");
     if (VERSION_NUMBER == null)
      VERSION_NUMBER = new String ("");
     if (TRANSF_TABLE_ID == null)
      TRANSF_TABLE_ID = new Long (0);
   }

   public void setOld_value(String OLD_VALUE) {
    if (validateData){
      DataValidator.validateData((Object)OLD_VALUE,"OLD_VALUE",12,30,0);
    }
    modifiedColumns.add("OLD_VALUE");
    this.OLD_VALUE = OLD_VALUE;
  }
   public void setNew_value(String NEW_VALUE) {
    if (validateData){
      DataValidator.validateData((Object)NEW_VALUE,"NEW_VALUE",12,30,0);
    }
    modifiedColumns.add("NEW_VALUE");
    this.NEW_VALUE = NEW_VALUE;
  }
   public void setVersion_number(String VERSION_NUMBER) {
    if (validateData){
      DataValidator.validateData((Object)VERSION_NUMBER,"VERSION_NUMBER",12,32,0);
    }
    modifiedColumns.add("VERSION_NUMBER");
    this.VERSION_NUMBER = VERSION_NUMBER;
  }
   public void setTransf_table_id(Long TRANSF_TABLE_ID) {
    if (validateData){
      DataValidator.validateData((Object)TRANSF_TABLE_ID,"TRANSF_TABLE_ID",2,38,0);
    }
    modifiedColumns.add("TRANSF_TABLE_ID");
    this.TRANSF_TABLE_ID = TRANSF_TABLE_ID;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Meta_transf_table_values o) {

         if ((((this.OLD_VALUE == null) || (o.OLD_VALUE == null)) && (this.OLD_VALUE != o.OLD_VALUE))
            || (((this.NEW_VALUE == null) || (o.NEW_VALUE == null)) && (this.NEW_VALUE != o.NEW_VALUE))
            || (((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
            || (((this.TRANSF_TABLE_ID == null) || (o.TRANSF_TABLE_ID == null)) && (this.TRANSF_TABLE_ID != o.TRANSF_TABLE_ID))
          ){
    return false;
    } else
         if ((((this.OLD_VALUE != null) && (o.OLD_VALUE != null)) && (this.OLD_VALUE.equals(o.OLD_VALUE) == false))
            || (((this.NEW_VALUE != null) && (o.NEW_VALUE != null)) && (this.NEW_VALUE.equals(o.NEW_VALUE) == false))
            || (((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER.equals(o.VERSION_NUMBER) == false))
            || (((this.TRANSF_TABLE_ID != null) && (o.TRANSF_TABLE_ID != null)) && (this.TRANSF_TABLE_ID.equals(o.TRANSF_TABLE_ID) == false))
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
  public static int getOld_valueColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getOld_valueDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getOld_valueSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 30
  */
  public static int getNew_valueColumnSize() {
    
     return 30;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getNew_valueDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getNew_valueSQLType() {
    
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
