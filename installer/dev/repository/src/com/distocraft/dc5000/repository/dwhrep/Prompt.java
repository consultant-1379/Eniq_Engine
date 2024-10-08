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

public class Prompt implements Cloneable,RockDBObject  {

    private String VERSIONID;
    private Integer PROMPTIMPLEMENTORID;
    private String PROMPTNAME;
    private Integer ORDERNUMBER;
    private String UNREFRESHABLE;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSIONID"    ,"PROMPTIMPLEMENTORID"    ,"PROMPTNAME"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Prompt original; 

  public Prompt(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Prompt(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.VERSIONID = null;
         this.PROMPTIMPLEMENTORID = null;
         this.PROMPTNAME = null;
         this.ORDERNUMBER = null;
         this.UNREFRESHABLE = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Prompt(RockFactory rockFact   ,String VERSIONID ,Integer PROMPTIMPLEMENTORID ,String PROMPTNAME ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSIONID = VERSIONID;
            this.PROMPTIMPLEMENTORID = PROMPTIMPLEMENTORID;
            this.PROMPTNAME = PROMPTNAME;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Prompt> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Prompt o = (Prompt) it.next();

              this.VERSIONID = o.getVersionid();
              this.PROMPTIMPLEMENTORID = o.getPromptimplementorid();
              this.PROMPTNAME = o.getPromptname();
              this.ORDERNUMBER = o.getOrdernumber();
              this.UNREFRESHABLE = o.getUnrefreshable();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Prompt");
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
  public Prompt(RockFactory rockFact, Prompt whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Prompt> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Prompt o = (Prompt) it.next();
                this.VERSIONID = o.getVersionid();
                this.PROMPTIMPLEMENTORID = o.getPromptimplementorid();
                this.PROMPTNAME = o.getPromptname();
                this.ORDERNUMBER = o.getOrdernumber();
                this.UNREFRESHABLE = o.getUnrefreshable();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Prompt");
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
    return "Prompt";
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
  public int updateDB(boolean useTimestamp, Prompt whereObject) throws SQLException, RockException {
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
  public int deleteDB(Prompt whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Prompt.saveDB(), no primary key defined");
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
    sbuff.append("<Prompt ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("PROMPTIMPLEMENTORID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PROMPTIMPLEMENTORID),4, true)+"\" ");
        sbuff.append("PROMPTNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PROMPTNAME),12, true)+"\" ");
        sbuff.append("ORDERNUMBER=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.ORDERNUMBER),4, true)+"\" ");
        sbuff.append("UNREFRESHABLE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.UNREFRESHABLE),12, true)+"\" ");
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
    sbuff.append("<Prompt ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("PROMPTIMPLEMENTORID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PROMPTIMPLEMENTORID),4, true)+"\" ");
        sbuff.append("PROMPTNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PROMPTNAME),12, true)+"\" ");
        sbuff.append("ORDERNUMBER=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.ORDERNUMBER),4, true)+"\" ");
        sbuff.append("UNREFRESHABLE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.UNREFRESHABLE),12, true)+"\" ");
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
    sbuff.append("</Prompt>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Prompt ( ");
	    		sbuff.append("VERSIONID");
		    		sbuff.append(", PROMPTIMPLEMENTORID");
	    		sbuff.append(", PROMPTNAME");
	    		sbuff.append(", ORDERNUMBER");
	    		sbuff.append(", UNREFRESHABLE");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.VERSIONID,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.PROMPTIMPLEMENTORID,4)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.PROMPTNAME,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.ORDERNUMBER,4)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.UNREFRESHABLE,12)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getVersionid() { 
    return this.VERSIONID;
  }
   public Integer getPromptimplementorid() { 
    return this.PROMPTIMPLEMENTORID;
  }
   public String getPromptname() { 
    return this.PROMPTNAME;
  }
   public Integer getOrdernumber() { 
    return this.ORDERNUMBER;
  }
   public String getUnrefreshable() { 
    return this.UNREFRESHABLE;
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
     if (PROMPTIMPLEMENTORID == null)
      PROMPTIMPLEMENTORID = new Integer (0);
     if (PROMPTNAME == null)
      PROMPTNAME = new String ("");
     if (ORDERNUMBER == null)
      ORDERNUMBER = new Integer (0);
     if (UNREFRESHABLE == null)
      UNREFRESHABLE = new String ("");
   }

   public void setVersionid(String VERSIONID) {
    if (validateData){
      DataValidator.validateData((Object)VERSIONID,"VERSIONID",12,128,0);
    }
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }
   public void setPromptimplementorid(Integer PROMPTIMPLEMENTORID) {
    if (validateData){
      DataValidator.validateData((Object)PROMPTIMPLEMENTORID,"PROMPTIMPLEMENTORID",4,10,0);
    }
    modifiedColumns.add("PROMPTIMPLEMENTORID");
    this.PROMPTIMPLEMENTORID = PROMPTIMPLEMENTORID;
  }
   public void setPromptname(String PROMPTNAME) {
    if (validateData){
      DataValidator.validateData((Object)PROMPTNAME,"PROMPTNAME",12,255,0);
    }
    modifiedColumns.add("PROMPTNAME");
    this.PROMPTNAME = PROMPTNAME;
  }
   public void setOrdernumber(Integer ORDERNUMBER) {
    if (validateData){
      DataValidator.validateData((Object)ORDERNUMBER,"ORDERNUMBER",4,10,0);
    }
    modifiedColumns.add("ORDERNUMBER");
    this.ORDERNUMBER = ORDERNUMBER;
  }
   public void setUnrefreshable(String UNREFRESHABLE) {
    if (validateData){
      DataValidator.validateData((Object)UNREFRESHABLE,"UNREFRESHABLE",12,32,0);
    }
    modifiedColumns.add("UNREFRESHABLE");
    this.UNREFRESHABLE = UNREFRESHABLE;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Prompt o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.PROMPTIMPLEMENTORID == null) || (o.PROMPTIMPLEMENTORID == null)) && (this.PROMPTIMPLEMENTORID != o.PROMPTIMPLEMENTORID))
            || (((this.PROMPTNAME == null) || (o.PROMPTNAME == null)) && (this.PROMPTNAME != o.PROMPTNAME))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.PROMPTIMPLEMENTORID != null) && (o.PROMPTIMPLEMENTORID != null)) && (this.PROMPTIMPLEMENTORID.equals(o.PROMPTIMPLEMENTORID) == false))
            || (((this.PROMPTNAME != null) && (o.PROMPTNAME != null)) && (this.PROMPTNAME.equals(o.PROMPTNAME) == false))
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

  public boolean equals(Prompt o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.PROMPTIMPLEMENTORID == null) || (o.PROMPTIMPLEMENTORID == null)) && (this.PROMPTIMPLEMENTORID != o.PROMPTIMPLEMENTORID))
            || (((this.PROMPTNAME == null) || (o.PROMPTNAME == null)) && (this.PROMPTNAME != o.PROMPTNAME))
            || (((this.ORDERNUMBER == null) || (o.ORDERNUMBER == null)) && (this.ORDERNUMBER != o.ORDERNUMBER))
            || (((this.UNREFRESHABLE == null) || (o.UNREFRESHABLE == null)) && (this.UNREFRESHABLE != o.UNREFRESHABLE))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.PROMPTIMPLEMENTORID != null) && (o.PROMPTIMPLEMENTORID != null)) && (this.PROMPTIMPLEMENTORID.equals(o.PROMPTIMPLEMENTORID) == false))
            || (((this.PROMPTNAME != null) && (o.PROMPTNAME != null)) && (this.PROMPTNAME.equals(o.PROMPTNAME) == false))
            || (((this.ORDERNUMBER != null) && (o.ORDERNUMBER != null)) && (this.ORDERNUMBER.equals(o.ORDERNUMBER) == false))
            || (((this.UNREFRESHABLE != null) && (o.UNREFRESHABLE != null)) && (this.UNREFRESHABLE.equals(o.UNREFRESHABLE) == false))
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
  * return 10
  */
  public static int getPromptimplementoridColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getPromptimplementoridDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 4
  */
  public static int getPromptimplementoridSQLType() {
    
    return 4;   
  }
    
 
  /**
  * get columnSize
  * return 255
  */
  public static int getPromptnameColumnSize() {
    
     return 255;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getPromptnameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getPromptnameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 10
  */
  public static int getOrdernumberColumnSize() {
    
     return 10;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getOrdernumberDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 4
  */
  public static int getOrdernumberSQLType() {
    
    return 4;   
  }
    
 
  /**
  * get columnSize
  * return 32
  */
  public static int getUnrefreshableColumnSize() {
    
     return 32;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getUnrefreshableDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getUnrefreshableSQLType() {
    
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

  public Prompt getOriginal() {
    return original;
  }
   
  public void setOriginal(Prompt original) {
    this.original = (Prompt) original.clone();
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
