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

public class Dataformat implements Cloneable,RockDBObject  {

    private String DATAFORMATID;
    private String TYPEID;
    private String VERSIONID;
    private String OBJECTTYPE;
    private String FOLDERNAME;
    private String DATAFORMATTYPE;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "DATAFORMATID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Dataformat original; 

  public Dataformat(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Dataformat(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.DATAFORMATID = null;
         this.TYPEID = null;
         this.VERSIONID = null;
         this.OBJECTTYPE = null;
         this.FOLDERNAME = null;
         this.DATAFORMATTYPE = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Dataformat(RockFactory rockFact   ,String DATAFORMATID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.DATAFORMATID = DATAFORMATID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Dataformat> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Dataformat o = (Dataformat) it.next();

              this.DATAFORMATID = o.getDataformatid();
              this.TYPEID = o.getTypeid();
              this.VERSIONID = o.getVersionid();
              this.OBJECTTYPE = o.getObjecttype();
              this.FOLDERNAME = o.getFoldername();
              this.DATAFORMATTYPE = o.getDataformattype();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Dataformat");
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
  public Dataformat(RockFactory rockFact, Dataformat whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Dataformat> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Dataformat o = (Dataformat) it.next();
                this.DATAFORMATID = o.getDataformatid();
                this.TYPEID = o.getTypeid();
                this.VERSIONID = o.getVersionid();
                this.OBJECTTYPE = o.getObjecttype();
                this.FOLDERNAME = o.getFoldername();
                this.DATAFORMATTYPE = o.getDataformattype();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Dataformat");
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
    return "Dataformat";
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
  public int updateDB(boolean useTimestamp, Dataformat whereObject) throws SQLException, RockException {
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
  public int deleteDB(Dataformat whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Dataformat.saveDB(), no primary key defined");
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
    sbuff.append("<Dataformat ");
        sbuff.append("DATAFORMATID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATID),12, true)+"\" ");
        sbuff.append("TYPEID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TYPEID),12, true)+"\" ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("OBJECTTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.OBJECTTYPE),12, true)+"\" ");
        sbuff.append("FOLDERNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.FOLDERNAME),12, true)+"\" ");
        sbuff.append("DATAFORMATTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATTYPE),12, true)+"\" ");
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
    sbuff.append("<Dataformat ");
        sbuff.append("DATAFORMATID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATID),12, true)+"\" ");
        sbuff.append("TYPEID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TYPEID),12, true)+"\" ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("OBJECTTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.OBJECTTYPE),12, true)+"\" ");
        sbuff.append("FOLDERNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.FOLDERNAME),12, true)+"\" ");
        sbuff.append("DATAFORMATTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATTYPE),12, true)+"\" ");
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
    sbuff.append("</Dataformat>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Dataformat ( ");
	    		sbuff.append("DATAFORMATID");
		    		sbuff.append(", TYPEID");
	    		sbuff.append(", VERSIONID");
	    		sbuff.append(", OBJECTTYPE");
	    		sbuff.append(", FOLDERNAME");
	    		sbuff.append(", DATAFORMATTYPE");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.DATAFORMATID,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.TYPEID,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.VERSIONID,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.OBJECTTYPE,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.FOLDERNAME,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.DATAFORMATTYPE,12)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getDataformatid() { 
    return this.DATAFORMATID;
  }
   public String getTypeid() { 
    return this.TYPEID;
  }
   public String getVersionid() { 
    return this.VERSIONID;
  }
   public String getObjecttype() { 
    return this.OBJECTTYPE;
  }
   public String getFoldername() { 
    return this.FOLDERNAME;
  }
   public String getDataformattype() { 
    return this.DATAFORMATTYPE;
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
     if (DATAFORMATID == null)
      DATAFORMATID = new String ("");
     if (TYPEID == null)
      TYPEID = new String ("");
     if (VERSIONID == null)
      VERSIONID = new String ("");
     if (OBJECTTYPE == null)
      OBJECTTYPE = new String ("");
     if (FOLDERNAME == null)
      FOLDERNAME = new String ("");
     if (DATAFORMATTYPE == null)
      DATAFORMATTYPE = new String ("");
   }

   public void setDataformatid(String DATAFORMATID) {
    if (validateData){
      DataValidator.validateData((Object)DATAFORMATID,"DATAFORMATID",12,100,0);
    }
    modifiedColumns.add("DATAFORMATID");
    this.DATAFORMATID = DATAFORMATID;
  }
   public void setTypeid(String TYPEID) {
    if (validateData){
      DataValidator.validateData((Object)TYPEID,"TYPEID",12,255,0);
    }
    modifiedColumns.add("TYPEID");
    this.TYPEID = TYPEID;
  }
   public void setVersionid(String VERSIONID) {
    if (validateData){
      DataValidator.validateData((Object)VERSIONID,"VERSIONID",12,128,0);
    }
    modifiedColumns.add("VERSIONID");
    this.VERSIONID = VERSIONID;
  }
   public void setObjecttype(String OBJECTTYPE) {
    if (validateData){
      DataValidator.validateData((Object)OBJECTTYPE,"OBJECTTYPE",12,255,0);
    }
    modifiedColumns.add("OBJECTTYPE");
    this.OBJECTTYPE = OBJECTTYPE;
  }
   public void setFoldername(String FOLDERNAME) {
    if (validateData){
      DataValidator.validateData((Object)FOLDERNAME,"FOLDERNAME",12,128,0);
    }
    modifiedColumns.add("FOLDERNAME");
    this.FOLDERNAME = FOLDERNAME;
  }
   public void setDataformattype(String DATAFORMATTYPE) {
    if (validateData){
      DataValidator.validateData((Object)DATAFORMATTYPE,"DATAFORMATTYPE",12,50,0);
    }
    modifiedColumns.add("DATAFORMATTYPE");
    this.DATAFORMATTYPE = DATAFORMATTYPE;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Dataformat o) {

         if ((((this.DATAFORMATID == null) || (o.DATAFORMATID == null)) && (this.DATAFORMATID != o.DATAFORMATID))
          ){
    return false;
    } else
         if ((((this.DATAFORMATID != null) && (o.DATAFORMATID != null)) && (this.DATAFORMATID.equals(o.DATAFORMATID) == false))
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

  public boolean equals(Dataformat o) {

         if ((((this.DATAFORMATID == null) || (o.DATAFORMATID == null)) && (this.DATAFORMATID != o.DATAFORMATID))
            || (((this.TYPEID == null) || (o.TYPEID == null)) && (this.TYPEID != o.TYPEID))
            || (((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.OBJECTTYPE == null) || (o.OBJECTTYPE == null)) && (this.OBJECTTYPE != o.OBJECTTYPE))
            || (((this.FOLDERNAME == null) || (o.FOLDERNAME == null)) && (this.FOLDERNAME != o.FOLDERNAME))
            || (((this.DATAFORMATTYPE == null) || (o.DATAFORMATTYPE == null)) && (this.DATAFORMATTYPE != o.DATAFORMATTYPE))
          ){
    return false;
    } else
         if ((((this.DATAFORMATID != null) && (o.DATAFORMATID != null)) && (this.DATAFORMATID.equals(o.DATAFORMATID) == false))
            || (((this.TYPEID != null) && (o.TYPEID != null)) && (this.TYPEID.equals(o.TYPEID) == false))
            || (((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.OBJECTTYPE != null) && (o.OBJECTTYPE != null)) && (this.OBJECTTYPE.equals(o.OBJECTTYPE) == false))
            || (((this.FOLDERNAME != null) && (o.FOLDERNAME != null)) && (this.FOLDERNAME.equals(o.FOLDERNAME) == false))
            || (((this.DATAFORMATTYPE != null) && (o.DATAFORMATTYPE != null)) && (this.DATAFORMATTYPE.equals(o.DATAFORMATTYPE) == false))
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
  * return 100
  */
  public static int getDataformatidColumnSize() {
    
     return 100;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDataformatidDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDataformatidSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 255
  */
  public static int getTypeidColumnSize() {
    
     return 255;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTypeidDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTypeidSQLType() {
    
    return 12;   
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
  public static int getObjecttypeColumnSize() {
    
     return 255;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getObjecttypeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getObjecttypeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getFoldernameColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getFoldernameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getFoldernameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 50
  */
  public static int getDataformattypeColumnSize() {
    
     return 50;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDataformattypeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getDataformattypeSQLType() {
    
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

  public Dataformat getOriginal() {
    return original;
  }
   
  public void setOriginal(Dataformat original) {
    this.original = (Dataformat) original.clone();
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
