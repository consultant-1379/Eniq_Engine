package com.distocraft.dc5000.etl.rock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import ssc.rockfactory.DataValidator;
import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

public class Meta_transfer_actions implements Cloneable,RockDBObject  {

    private String VERSION_NUMBER;
    private Long TRANSFER_ACTION_ID;
    private Long COLLECTION_ID;
    private Long COLLECTION_SET_ID;
    private String ACTION_TYPE;
    private String TRANSFER_ACTION_NAME;
    private Long ORDER_BY_NO;
    private String DESCRIPTION;
    private String ENABLED_FLAG;
    private Long CONNECTION_ID;
    private String WHERE_CLAUSE_02;
    private String WHERE_CLAUSE_03;
    private String ACTION_CONTENTS_03;
    private String ACTION_CONTENTS_02;
    private String ACTION_CONTENTS_01;
    private String WHERE_CLAUSE_01;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSION_NUMBER"    ,"TRANSFER_ACTION_ID"    ,"COLLECTION_ID"    ,"COLLECTION_SET_ID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;

  public Meta_transfer_actions(RockFactory rockFact) {
  	this(rockFact, false);
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Meta_transfer_actions(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.VERSION_NUMBER = null;
         this.TRANSFER_ACTION_ID = null;
         this.COLLECTION_ID = null;
         this.COLLECTION_SET_ID = null;
         this.ACTION_TYPE = null;
         this.TRANSFER_ACTION_NAME = null;
         this.ORDER_BY_NO = null;
         this.DESCRIPTION = null;
         this.ENABLED_FLAG = null;
         this.CONNECTION_ID = null;
         this.WHERE_CLAUSE_02 = null;
         this.WHERE_CLAUSE_03 = null;
         this.ACTION_CONTENTS_03 = null;
         this.ACTION_CONTENTS_02 = null;
         this.ACTION_CONTENTS_01 = null;
         this.WHERE_CLAUSE_01 = null;
    
  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Meta_transfer_actions(RockFactory rockFact   ,String VERSION_NUMBER ,Long TRANSFER_ACTION_ID ,Long COLLECTION_ID ,Long COLLECTION_SET_ID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSION_NUMBER = VERSION_NUMBER;
            this.TRANSFER_ACTION_ID = TRANSFER_ACTION_ID;
            this.COLLECTION_ID = COLLECTION_ID;
            this.COLLECTION_SET_ID = COLLECTION_SET_ID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Meta_transfer_actions o = (Meta_transfer_actions) it.next();

              this.VERSION_NUMBER = o.getVersion_number();
              this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
              this.COLLECTION_ID = o.getCollection_id();
              this.COLLECTION_SET_ID = o.getCollection_set_id();
              this.ACTION_TYPE = o.getAction_type();
              this.TRANSFER_ACTION_NAME = o.getTransfer_action_name();
              this.ORDER_BY_NO = o.getOrder_by_no();
              this.DESCRIPTION = o.getDescription();
              this.ENABLED_FLAG = o.getEnabled_flag();
              this.CONNECTION_ID = o.getConnection_id();
              this.WHERE_CLAUSE_02 = o.getWhere_clause_02();
              this.WHERE_CLAUSE_03 = o.getWhere_clause_03();
              this.ACTION_CONTENTS_03 = o.getAction_contents_03();
              this.ACTION_CONTENTS_02 = o.getAction_contents_02();
              this.ACTION_CONTENTS_01 = o.getAction_contents_01();
              this.WHERE_CLAUSE_01 = o.getWhere_clause_01();
       
        results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transfer_actions");
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
  public Meta_transfer_actions(RockFactory rockFact, Meta_transfer_actions whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Meta_transfer_actions o = (Meta_transfer_actions) it.next();
                this.VERSION_NUMBER = o.getVersion_number();
                this.TRANSFER_ACTION_ID = o.getTransfer_action_id();
                this.COLLECTION_ID = o.getCollection_id();
                this.COLLECTION_SET_ID = o.getCollection_set_id();
                this.ACTION_TYPE = o.getAction_type();
                this.TRANSFER_ACTION_NAME = o.getTransfer_action_name();
                this.ORDER_BY_NO = o.getOrder_by_no();
                this.DESCRIPTION = o.getDescription();
                this.ENABLED_FLAG = o.getEnabled_flag();
                this.CONNECTION_ID = o.getConnection_id();
                this.WHERE_CLAUSE_02 = o.getWhere_clause_02();
                this.WHERE_CLAUSE_03 = o.getWhere_clause_03();
                this.ACTION_CONTENTS_03 = o.getAction_contents_03();
                this.ACTION_CONTENTS_02 = o.getAction_contents_02();
                this.ACTION_CONTENTS_01 = o.getAction_contents_01();
                this.WHERE_CLAUSE_01 = o.getWhere_clause_01();
                results.close();
        this.newItem = false;
      } else {
        results.close();
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Meta_transfer_actions");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  public Set gimmeModifiedColumns(){   
    return modifiedColumns;  
  }

  public void setModifiedColumns(Set<String> modifiedColumns){   
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
    return "Meta_transfer_actions";
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
  public int updateDB(boolean useTimestamp, Meta_transfer_actions whereObject) throws SQLException, RockException {
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
  public int deleteDB(Meta_transfer_actions whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Meta_transfer_actions.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
  }


   public String getVersion_number() { 
    return this.VERSION_NUMBER;
  }
   public Long getTransfer_action_id() { 
    return this.TRANSFER_ACTION_ID;
  }
   public Long getCollection_id() { 
    return this.COLLECTION_ID;
  }
   public Long getCollection_set_id() { 
    return this.COLLECTION_SET_ID;
  }
   public String getAction_type() { 
    return this.ACTION_TYPE;
  }
   public String getTransfer_action_name() { 
    return this.TRANSFER_ACTION_NAME;
  }
   public Long getOrder_by_no() { 
    return this.ORDER_BY_NO;
  }
   public String getDescription() { 
    return this.DESCRIPTION;
  }
  public String getWhere_clause() {
    List<String> whereClauseList = new ArrayList<String>();
    whereClauseList.add((null == this.WHERE_CLAUSE_01) ? "" : this.WHERE_CLAUSE_01);
    whereClauseList.add((null == this.WHERE_CLAUSE_02) ? "" : this.WHERE_CLAUSE_02);
    whereClauseList.add((null == this.WHERE_CLAUSE_03) ? "" : this.WHERE_CLAUSE_03);
    String whereClause = joinStringsIntoOne(whereClauseList);
    return whereClause;
  }

  public String getAction_contents() {
    List<String> actionContentsList = new ArrayList<String>();
    actionContentsList.add((null == this.ACTION_CONTENTS_01) ? "" : this.ACTION_CONTENTS_01);
    actionContentsList.add((null == this.ACTION_CONTENTS_02) ? "" : this.ACTION_CONTENTS_02);
    actionContentsList.add((null == this.ACTION_CONTENTS_03) ? "" : this.ACTION_CONTENTS_03);
    String actionContents = joinStringsIntoOne(actionContentsList);
    return actionContents;
  }

   public String getEnabled_flag() { 
    return this.ENABLED_FLAG;
  }
   public Long getConnection_id() { 
    return this.CONNECTION_ID;
  }
   public String getWhere_clause_02() { 
    return this.WHERE_CLAUSE_02;
  }
   public String getWhere_clause_03() { 
    return this.WHERE_CLAUSE_03;
  }
   public String getAction_contents_03() { 
    return this.ACTION_CONTENTS_03;
  }
   public String getAction_contents_02() { 
    return this.ACTION_CONTENTS_02;
  }
   public String getAction_contents_01() { 
    return this.ACTION_CONTENTS_01;
  }
   public String getWhere_clause_01() { 
    return this.WHERE_CLAUSE_01;
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
     if (TRANSFER_ACTION_ID == null)
      TRANSFER_ACTION_ID = new Long (0);
     if (COLLECTION_ID == null)
      COLLECTION_ID = new Long (0);
     if (COLLECTION_SET_ID == null)
      COLLECTION_SET_ID = new Long (0);
     if (ACTION_TYPE == null)
      ACTION_TYPE = new String ("");
     if (TRANSFER_ACTION_NAME == null)
      TRANSFER_ACTION_NAME = new String ("");
     if (ORDER_BY_NO == null)
      ORDER_BY_NO = new Long (0);
     if (DESCRIPTION == null)
      DESCRIPTION = new String ("");
     if (ENABLED_FLAG == null)
      ENABLED_FLAG = new String ("");
     if (CONNECTION_ID == null)
      CONNECTION_ID = new Long (0);
     if (WHERE_CLAUSE_02 == null)
      WHERE_CLAUSE_02 = new String ("");
     if (WHERE_CLAUSE_03 == null)
      WHERE_CLAUSE_03 = new String ("");
     if (ACTION_CONTENTS_03 == null)
      ACTION_CONTENTS_03 = new String ("");
     if (ACTION_CONTENTS_02 == null)
      ACTION_CONTENTS_02 = new String ("");
     if (ACTION_CONTENTS_01 == null)
      ACTION_CONTENTS_01 = new String ("");
     if (WHERE_CLAUSE_01 == null)
      WHERE_CLAUSE_01 = new String ("");
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
   public void setCollection_id(Long COLLECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_ID,"COLLECTION_ID",2,38,0);
    }
    modifiedColumns.add("COLLECTION_ID");
    this.COLLECTION_ID = COLLECTION_ID;
  }
   public void setCollection_set_id(Long COLLECTION_SET_ID) {
    if (validateData){
      DataValidator.validateData((Object)COLLECTION_SET_ID,"COLLECTION_SET_ID",2,38,0);
    }
    modifiedColumns.add("COLLECTION_SET_ID");
    this.COLLECTION_SET_ID = COLLECTION_SET_ID;
  }
   public void setAction_type(String ACTION_TYPE) {
    if (validateData){
      DataValidator.validateData((Object)ACTION_TYPE,"ACTION_TYPE",12,20,0);
    }
    modifiedColumns.add("ACTION_TYPE");
    this.ACTION_TYPE = ACTION_TYPE;
  }
   public void setTransfer_action_name(String TRANSFER_ACTION_NAME) {
    if (validateData){
      DataValidator.validateData((Object)TRANSFER_ACTION_NAME,"TRANSFER_ACTION_NAME",12,128,0);
    }
    modifiedColumns.add("TRANSFER_ACTION_NAME");
    this.TRANSFER_ACTION_NAME = TRANSFER_ACTION_NAME;
  }
   public void setOrder_by_no(Long ORDER_BY_NO) {
    if (validateData){
      DataValidator.validateData((Object)ORDER_BY_NO,"ORDER_BY_NO",2,38,0);
    }
    modifiedColumns.add("ORDER_BY_NO");
    this.ORDER_BY_NO = ORDER_BY_NO;
  }
   public void setDescription(String DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)DESCRIPTION,"DESCRIPTION",12,32000,0);
    }
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }
  public void setWhere_clause(final String WHERE_CLAUSE) {
    if (stringTooLong(WHERE_CLAUSE.length())) {
      Logger log = Logger.getLogger("etl.engine.RockFactory");
      log.severe("On setting META_TRANSFER_ACTION (TRANSFER_ACTION_ID=" + this.TRANSFER_ACTION_ID + ", TRANSFER_ACTION_NAME="
          + this.TRANSFER_ACTION_NAME + ", COLLECTION_ID=" + this.COLLECTION_ID + ", COLLECTION_SET_ID="
          + this.COLLECTION_SET_ID + ") the WHERE_CLAUSE is longer than maximum length ("
          + getTotalSQLClauseMaxLength() + "). Rest of the clause is not stored.");
    }
    final String[] splittedWhereClause = splitStringIntoPieces(WHERE_CLAUSE, 32000);
    setWhere_clause_01(splittedWhereClause[0]);
    setWhere_clause_02(splittedWhereClause[1]);
    setWhere_clause_03(splittedWhereClause[2]);
  }

  public void setAction_contents(final String ACTION_CONTENTS) {
    if (stringTooLong(ACTION_CONTENTS.length())) {
      Logger log = Logger.getLogger("etl.engine.RockFactory");
      log.severe("On setting META_TRANSFER_ACTION (TRANSFER_ACTION_ID=" + this.TRANSFER_ACTION_ID + ", TRANSFER_ACTION_NAME="
          + this.TRANSFER_ACTION_NAME + ", COLLECTION_ID=" + this.COLLECTION_ID + ", COLLECTION_SET_ID="
          + this.COLLECTION_SET_ID + ") the ACTION_CONTENTS is longer than maximum length ("
          + getTotalSQLClauseMaxLength() + "). Rest of the clause is not stored.");
    }
    final String[] splittedActionContents = splitStringIntoPieces(ACTION_CONTENTS, 32000);
    setAction_contents_01(splittedActionContents[0]);
    setAction_contents_02(splittedActionContents[1]);
    setAction_contents_03(splittedActionContents[2]);
  }
   public void setEnabled_flag(String ENABLED_FLAG) {
    if (validateData){
      DataValidator.validateData((Object)ENABLED_FLAG,"ENABLED_FLAG",12,1,0);
    }
    modifiedColumns.add("ENABLED_FLAG");
    this.ENABLED_FLAG = ENABLED_FLAG;
  }
   public void setConnection_id(Long CONNECTION_ID) {
    if (validateData){
      DataValidator.validateData((Object)CONNECTION_ID,"CONNECTION_ID",2,38,0);
    }
    modifiedColumns.add("CONNECTION_ID");
    this.CONNECTION_ID = CONNECTION_ID;
  }
   public void setWhere_clause_02(String WHERE_CLAUSE_02) {
    if (validateData){
      DataValidator.validateData((Object)WHERE_CLAUSE_02,"WHERE_CLAUSE_02",12,32000,0);
    }
    modifiedColumns.add("WHERE_CLAUSE_02");
    this.WHERE_CLAUSE_02 = WHERE_CLAUSE_02;
  }
   public void setWhere_clause_03(String WHERE_CLAUSE_03) {
    if (validateData){
      DataValidator.validateData((Object)WHERE_CLAUSE_03,"WHERE_CLAUSE_03",12,32000,0);
    }
    modifiedColumns.add("WHERE_CLAUSE_03");
    this.WHERE_CLAUSE_03 = WHERE_CLAUSE_03;
  }
   public void setAction_contents_03(String ACTION_CONTENTS_03) {
    if (validateData){
      DataValidator.validateData((Object)ACTION_CONTENTS_03,"ACTION_CONTENTS_03",12,32000,0);
    }
    modifiedColumns.add("ACTION_CONTENTS_03");
    this.ACTION_CONTENTS_03 = ACTION_CONTENTS_03;
  }
   public void setAction_contents_02(String ACTION_CONTENTS_02) {
    if (validateData){
      DataValidator.validateData((Object)ACTION_CONTENTS_02,"ACTION_CONTENTS_02",12,32000,0);
    }
    modifiedColumns.add("ACTION_CONTENTS_02");
    this.ACTION_CONTENTS_02 = ACTION_CONTENTS_02;
  }
   public void setAction_contents_01(String ACTION_CONTENTS_01) {
    if (validateData){
      DataValidator.validateData((Object)ACTION_CONTENTS_01,"ACTION_CONTENTS_01",12,32000,0);
    }
    modifiedColumns.add("ACTION_CONTENTS_01");
    this.ACTION_CONTENTS_01 = ACTION_CONTENTS_01;
  }
   public void setWhere_clause_01(String WHERE_CLAUSE_01) {
    if (validateData){
      DataValidator.validateData((Object)WHERE_CLAUSE_01,"WHERE_CLAUSE_01",12,32000,0);
    }
    modifiedColumns.add("WHERE_CLAUSE_01");
    this.WHERE_CLAUSE_01 = WHERE_CLAUSE_01;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */

 public boolean equals(Meta_transfer_actions o) {

    if ((((this.VERSION_NUMBER == null) || (o.VERSION_NUMBER == null)) && (this.VERSION_NUMBER != o.VERSION_NUMBER))
        || (((this.TRANSFER_ACTION_ID == null) || (o.TRANSFER_ACTION_ID == null)) && (this.TRANSFER_ACTION_ID != o.TRANSFER_ACTION_ID))
        || (((this.COLLECTION_ID == null) || (o.COLLECTION_ID == null)) && (this.COLLECTION_ID != o.COLLECTION_ID))
        || (((this.COLLECTION_SET_ID == null) || (o.COLLECTION_SET_ID == null)) && (this.COLLECTION_SET_ID != o.COLLECTION_SET_ID))
        || (((this.ACTION_TYPE == null) || (o.ACTION_TYPE == null)) && (this.ACTION_TYPE != o.ACTION_TYPE))
        || (((this.TRANSFER_ACTION_NAME == null) || (o.TRANSFER_ACTION_NAME == null)) && (this.TRANSFER_ACTION_NAME != o.TRANSFER_ACTION_NAME))
        || (((this.ORDER_BY_NO == null) || (o.ORDER_BY_NO == null)) && (this.ORDER_BY_NO != o.ORDER_BY_NO))
        || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
        || (((this.WHERE_CLAUSE_01 == null) || (o.WHERE_CLAUSE_01 == null)) && (this.WHERE_CLAUSE_01 != o.WHERE_CLAUSE_01))
        || (((this.ACTION_CONTENTS_01 == null) || (o.ACTION_CONTENTS_01 == null)) && (this.ACTION_CONTENTS_01 != o.ACTION_CONTENTS_01))
        || (((this.ENABLED_FLAG == null) || (o.ENABLED_FLAG == null)) && (this.ENABLED_FLAG != o.ENABLED_FLAG))
        || (((this.CONNECTION_ID == null) || (o.CONNECTION_ID == null)) && (this.CONNECTION_ID != o.CONNECTION_ID))
        || (((this.WHERE_CLAUSE_02 == null) || (o.WHERE_CLAUSE_02 == null)) && (this.WHERE_CLAUSE_02 != o.WHERE_CLAUSE_02))
        || (((this.WHERE_CLAUSE_03 == null) || (o.WHERE_CLAUSE_03 == null)) && (this.WHERE_CLAUSE_03 != o.WHERE_CLAUSE_03))
        || (((this.ACTION_CONTENTS_02 == null) || (o.ACTION_CONTENTS_02 == null)) && (this.ACTION_CONTENTS_02 != o.ACTION_CONTENTS_02))
        || (((this.ACTION_CONTENTS_03 == null) || (o.ACTION_CONTENTS_03 == null)) && (this.ACTION_CONTENTS_03 != o.ACTION_CONTENTS_03))) {
      return false;
    } else if ((((this.VERSION_NUMBER != null) && (o.VERSION_NUMBER != null)) && (this.VERSION_NUMBER
        .equals(o.VERSION_NUMBER) == false))
        || (((this.TRANSFER_ACTION_ID != null) && (o.TRANSFER_ACTION_ID != null)) && (this.TRANSFER_ACTION_ID
            .equals(o.TRANSFER_ACTION_ID) == false))
        || (((this.COLLECTION_ID != null) && (o.COLLECTION_ID != null)) && (this.COLLECTION_ID.equals(o.COLLECTION_ID) == false))
        || (((this.COLLECTION_SET_ID != null) && (o.COLLECTION_SET_ID != null)) && (this.COLLECTION_SET_ID
            .equals(o.COLLECTION_SET_ID) == false))
        || (((this.ACTION_TYPE != null) && (o.ACTION_TYPE != null)) && (this.ACTION_TYPE.equals(o.ACTION_TYPE) == false))
        || (((this.TRANSFER_ACTION_NAME != null) && (o.TRANSFER_ACTION_NAME != null)) && (this.TRANSFER_ACTION_NAME
            .equals(o.TRANSFER_ACTION_NAME) == false))
        || (((this.ORDER_BY_NO != null) && (o.ORDER_BY_NO != null)) && (this.ORDER_BY_NO.equals(o.ORDER_BY_NO) == false))
        || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
        || (((this.WHERE_CLAUSE_01 != null) && (o.WHERE_CLAUSE_01 != null)) && (this.WHERE_CLAUSE_01
            .equals(o.WHERE_CLAUSE_01) == false))
        || (((this.ACTION_CONTENTS_01 != null) && (o.ACTION_CONTENTS_01 != null)) && (this.ACTION_CONTENTS_01
            .equals(o.ACTION_CONTENTS_01) == false))
        || (((this.ENABLED_FLAG != null) && (o.ENABLED_FLAG != null)) && (this.ENABLED_FLAG.equals(o.ENABLED_FLAG) == false))
        || (((this.CONNECTION_ID != null) && (o.CONNECTION_ID != null)) && (this.CONNECTION_ID.equals(o.CONNECTION_ID) == false))
        || (((this.WHERE_CLAUSE_02 != null) && (o.WHERE_CLAUSE_02 != null)) && (this.WHERE_CLAUSE_02
            .equals(o.WHERE_CLAUSE_02) == false))
        || (((this.WHERE_CLAUSE_03 != null) && (o.WHERE_CLAUSE_03 != null)) && (this.WHERE_CLAUSE_03
            .equals(o.WHERE_CLAUSE_03) == false))
        || (((this.ACTION_CONTENTS_02 != null) && (o.ACTION_CONTENTS_02 != null)) && (this.ACTION_CONTENTS_02
            .equals(o.ACTION_CONTENTS_02) == false))
        || (((this.ACTION_CONTENTS_03 != null) && (o.ACTION_CONTENTS_03 != null)) && (this.ACTION_CONTENTS_03
            .equals(o.ACTION_CONTENTS_03) == false))) {
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
  * return 20
  */
  public static int getAction_typeColumnSize() {
    
     return 20;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAction_typeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAction_typeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getTransfer_action_nameColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTransfer_action_nameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTransfer_action_nameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 38
  */
  public static int getOrder_by_noColumnSize() {
    
     return 38;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getOrder_by_noDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getOrder_by_noSQLType() {
    
    return 2;   
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
  * return 32000
  */
  public static int getWhere_clauseColumnSize() {
    
     return 32000;   
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
  * return 32000
  */
  public static int getAction_contentsColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAction_contentsDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAction_contentsSQLType() {
    
    return 12;   
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
  * return 32000
  */
  public static int getWhere_clause_02ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getWhere_clause_02DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getWhere_clause_02SQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getWhere_clause_03ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getWhere_clause_03DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getWhere_clause_03SQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getAction_contents_03ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAction_contents_03DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAction_contents_03SQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getAction_contents_02ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAction_contents_02DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAction_contents_02SQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getAction_contents_01ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getAction_contents_01DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getAction_contents_01SQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32000
  */
  public static int getWhere_clause_01ColumnSize() {
    
     return 32000;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getWhere_clause_01DecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getWhere_clause_01SQLType() {
    
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


  private String[] splitStringIntoPieces(String string, int howLongPieces) {
    final int NUMBER_OF_CLAUSE_PARTS = 3; 
    String[] splitted = new String[NUMBER_OF_CLAUSE_PARTS];
    for(int i=0; i<splitted.length; i++){
      splitted[i] = "";
    }
    
    if (null != string) {
      int stringLength = string.length();
      int intoHowMany = (int) Math.ceil((double)stringLength/(double)howLongPieces);
      for (int i = 0; i < NUMBER_OF_CLAUSE_PARTS; i++) {
        if(i < intoHowMany){
          int endIndex = i * howLongPieces + howLongPieces;
          endIndex = endIndex > stringLength ? stringLength : endIndex;
          splitted[i] = string.substring(i * howLongPieces, endIndex);
        }
      }
    }
    
    return splitted;
  }

  private String joinStringsIntoOne(List strings) {
    final StringBuffer returnString = new StringBuffer();
    returnString.append("");
    
    if(null != strings){ 
      Iterator it = strings.iterator();
      while (it.hasNext()) {
        Object nextPart = it.next();
        if(null != nextPart){
          returnString.append(nextPart.toString());
        }
      }
    }
    return returnString.toString();
  }

  private boolean stringTooLong(int length) {
    return (getTotalSQLClauseMaxLength() < length);
  }

  private int getTotalSQLClauseMaxLength() {
    return 32000 * 3;
  }

}
