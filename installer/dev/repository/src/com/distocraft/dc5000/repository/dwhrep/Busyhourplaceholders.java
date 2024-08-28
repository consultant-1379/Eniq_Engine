package com.distocraft.dc5000.repository.dwhrep;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;
import ssc.rockfactory.DataValidator;

public class Busyhourplaceholders implements Cloneable,RockDBObject  {

    private String VERSIONID;
    private String BHLEVEL;
    private Integer PRODUCTPLACEHOLDERS;
    private Integer CUSTOMPLACEHOLDERS;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSIONID"    ,"BHLEVEL"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Busyhourplaceholders original; 

  public Busyhourplaceholders(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Busyhourplaceholders(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.VERSIONID = null;
         this.BHLEVEL = null;
         this.PRODUCTPLACEHOLDERS = null;
         this.CUSTOMPLACEHOLDERS = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Busyhourplaceholders(RockFactory rockFact   ,String VERSIONID ,String BHLEVEL ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSIONID = VERSIONID;
            this.BHLEVEL = BHLEVEL;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Busyhourplaceholders> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Busyhourplaceholders o = (Busyhourplaceholders) it.next();

              this.VERSIONID = o.getVersionid();
              this.BHLEVEL = o.getBhlevel();
              this.PRODUCTPLACEHOLDERS = o.getProductplaceholders();
              this.CUSTOMPLACEHOLDERS = o.getCustomplaceholders();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Busyhourplaceholders");
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
  public Busyhourplaceholders(RockFactory rockFact, Busyhourplaceholders whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Busyhourplaceholders> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Busyhourplaceholders o = (Busyhourplaceholders) it.next();
                this.VERSIONID = o.getVersionid();
                this.BHLEVEL = o.getBhlevel();
                this.PRODUCTPLACEHOLDERS = o.getProductplaceholders();
                this.CUSTOMPLACEHOLDERS = o.getCustomplaceholders();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Busyhourplaceholders");
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  public Set<String> gimmeModifiedColumns(){   
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
    return "Busyhourplaceholders";
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
  public int updateDB(boolean useTimestamp, Busyhourplaceholders whereObject) throws SQLException, RockException {
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
  public int deleteDB(Busyhourplaceholders whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Busyhourplaceholders.saveDB(), no primary key defined");
      }
    }
    this.newItem = false;
    this.setOriginal(this);
  }

  /**
   * Saves the data into the database (without primary key update)
   * 
   * @exception SQLException
   */
  public void saveToDB() throws SQLException, RockException {

    if (this.newItem) {
      insertDB();
    } else if (this.gimmeModifiedColumns().size() > 0) {
      rockFact.updateData(this, false, this.getOriginal(), true);
    }
    this.newItem = false;
    this.setOriginal(this);
  }


  /**
   * Prints the object out as XML
   * 
   * @exception SQLException
   */
    public String toXML_tag() throws SQLException, RockException {
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("<Busyhourplaceholders ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("BHLEVEL=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHLEVEL),12, true)+"\" ");
        sbuff.append("PRODUCTPLACEHOLDERS=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PRODUCTPLACEHOLDERS),4, true)+"\" ");
        sbuff.append("CUSTOMPLACEHOLDERS=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.CUSTOMPLACEHOLDERS),4, true)+"\" ");
        sbuff.append("DiffStatus=\"\"");
    sbuff.append(" />\n");  
    return sbuff.toString();
  }

  /**
   * Prints the object out as XML
   * 
   * @exception SQLException
   */
   
    public String toXML_startTag() throws SQLException, RockException {
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("<Busyhourplaceholders ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("BHLEVEL=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHLEVEL),12, true)+"\" ");
        sbuff.append("PRODUCTPLACEHOLDERS=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PRODUCTPLACEHOLDERS),4, true)+"\" ");
        sbuff.append("CUSTOMPLACEHOLDERS=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.CUSTOMPLACEHOLDERS),4, true)+"\" ");
        sbuff.append("DiffStatus=\"\"");
    sbuff.append(" >\n"); 
    return sbuff.toString();
  }

  /**
   * Prints the object out as XML
   * 
   * @exception SQLException
   */
   
    public String toXML_endTag() throws SQLException, RockException {
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("</Busyhourplaceholders>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Busyhourplaceholders ( ");
	    		sbuff.append("VERSIONID");
		    		sbuff.append(", BHLEVEL");
	    		sbuff.append(", PRODUCTPLACEHOLDERS");
	    		sbuff.append(", CUSTOMPLACEHOLDERS");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.VERSIONID,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.BHLEVEL,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.PRODUCTPLACEHOLDERS,4)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.CUSTOMPLACEHOLDERS,4)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getVersionid() { 
    return this.VERSIONID;
  }
   public String getBhlevel() { 
    return this.BHLEVEL;
  }
   public Integer getProductplaceholders() { 
    return this.PRODUCTPLACEHOLDERS;
  }
   public Integer getCustomplaceholders() { 
    return this.CUSTOMPLACEHOLDERS;
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
     if (VERSIONID == null)
      VERSIONID = new String ("");
     if (BHLEVEL == null)
      BHLEVEL = new String ("");
     if (PRODUCTPLACEHOLDERS == null)
      PRODUCTPLACEHOLDERS = new Integer (0);
     if (CUSTOMPLACEHOLDERS == null)
      CUSTOMPLACEHOLDERS = new Integer (0);
   }

   public void setVersionid(String VERSIONID) {
    if (validateData){
      DataValidator.validateData((Object)VERSIONID,"VERSIONID",12,128,0);
    }
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }
   public void setBhlevel(String BHLEVEL) {
    if (validateData){
      DataValidator.validateData((Object)BHLEVEL,"BHLEVEL",12,255,0);
    }
    modifiedColumns.add("BHLEVEL");
    this.BHLEVEL = BHLEVEL;
  }
   public void setProductplaceholders(Integer PRODUCTPLACEHOLDERS) {
    if (validateData){
      DataValidator.validateData((Object)PRODUCTPLACEHOLDERS,"PRODUCTPLACEHOLDERS",4,10,0);
    }
    modifiedColumns.add("PRODUCTPLACEHOLDERS");
    this.PRODUCTPLACEHOLDERS = PRODUCTPLACEHOLDERS;
  }
   public void setCustomplaceholders(Integer CUSTOMPLACEHOLDERS) {
    if (validateData){
      DataValidator.validateData((Object)CUSTOMPLACEHOLDERS,"CUSTOMPLACEHOLDERS",4,10,0);
    }
    modifiedColumns.add("CUSTOMPLACEHOLDERS");
    this.CUSTOMPLACEHOLDERS = CUSTOMPLACEHOLDERS;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Busyhourplaceholders o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.BHLEVEL == null) || (o.BHLEVEL == null)) && (this.BHLEVEL != o.BHLEVEL))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.BHLEVEL != null) && (o.BHLEVEL != null)) && (this.BHLEVEL.equals(o.BHLEVEL) == false))
          ){
    return false;
    } else {
      return true;
    }
  }

  /**
   * equals method test wheather the objects field values and the parametrs objects field values
   * are equal.
   */

  public boolean equals(Busyhourplaceholders o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.BHLEVEL == null) || (o.BHLEVEL == null)) && (this.BHLEVEL != o.BHLEVEL))
            || (((this.PRODUCTPLACEHOLDERS == null) || (o.PRODUCTPLACEHOLDERS == null)) && (this.PRODUCTPLACEHOLDERS != o.PRODUCTPLACEHOLDERS))
            || (((this.CUSTOMPLACEHOLDERS == null) || (o.CUSTOMPLACEHOLDERS == null)) && (this.CUSTOMPLACEHOLDERS != o.CUSTOMPLACEHOLDERS))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.BHLEVEL != null) && (o.BHLEVEL != null)) && (this.BHLEVEL.equals(o.BHLEVEL) == false))
            || (((this.PRODUCTPLACEHOLDERS != null) && (o.PRODUCTPLACEHOLDERS != null)) && (this.PRODUCTPLACEHOLDERS.equals(o.PRODUCTPLACEHOLDERS) == false))
            || (((this.CUSTOMPLACEHOLDERS != null) && (o.CUSTOMPLACEHOLDERS != null)) && (this.CUSTOMPLACEHOLDERS.equals(o.CUSTOMPLACEHOLDERS) == false))
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
  * return 128
  */
  public static int getVersionidColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getVersionidDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getVersionidSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 255
  */
  public static int getBhlevelColumnSize() {
    
     return 255;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getBhlevelDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getBhlevelSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 10
  */
  public static int getProductplaceholdersColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getProductplaceholdersDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 4
  */
  public static int getProductplaceholdersSQLType() {
    
    return 4;   
  }
    
 
  /**
  * get columnSize
  * return 10
  */
  public static int getCustomplaceholdersColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getCustomplaceholdersDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 4
  */
  public static int getCustomplaceholdersSQLType() {
    
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

  public Busyhourplaceholders getOriginal() {
    return original;
  }
   
  public void setOriginal(Busyhourplaceholders original) {
    this.original = (Busyhourplaceholders) original.clone();
  }
   
  /**
   * Return true if rock object is new, modified or changed
   *
   */ 
  public boolean isUpdated() {
    if (isNew()) {
      return true;
    } else if (isModified()) {
      return true;
    } else if (isChanged()) {   
      return true;
    } else {
      return false;
    }
  }
   
  /**
   * Return true if rock object is new 
   *
   */ 
  public boolean isNew() {
    return (original == null);
  }
   
  /**
   * Return true if rock object is modified (values can be same)
   *
   */ 
  public boolean isModified() {
    return (gimmeModifiedColumns().size() > 0);
  }
   
  /**
   * Return true if rock object is changed 
   *
   */ 
  public boolean isChanged() {
    if (original != null) {
    	return (!this.equals(original));
    }
    return false;
  }
   
}
