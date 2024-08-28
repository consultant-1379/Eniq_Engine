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

public class Defaulttags implements Cloneable,RockDBObject  {

    private String TAGID;
    private String DATAFORMATID;
    private String DESCRIPTION;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "TAGID"    ,"DATAFORMATID"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Defaulttags original; 

  public Defaulttags(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Defaulttags(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.TAGID = null;
         this.DATAFORMATID = null;
         this.DESCRIPTION = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Defaulttags(RockFactory rockFact   ,String TAGID ,String DATAFORMATID ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.TAGID = TAGID;
            this.DATAFORMATID = DATAFORMATID;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Defaulttags> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Defaulttags o = (Defaulttags) it.next();

              this.TAGID = o.getTagid();
              this.DATAFORMATID = o.getDataformatid();
              this.DESCRIPTION = o.getDescription();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Defaulttags");
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
  public Defaulttags(RockFactory rockFact, Defaulttags whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Defaulttags> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Defaulttags o = (Defaulttags) it.next();
                this.TAGID = o.getTagid();
                this.DATAFORMATID = o.getDataformatid();
                this.DESCRIPTION = o.getDescription();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Defaulttags");
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
    return "Defaulttags";
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
  public int updateDB(boolean useTimestamp, Defaulttags whereObject) throws SQLException, RockException {
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
  public int deleteDB(Defaulttags whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Defaulttags.saveDB(), no primary key defined");
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
    sbuff.append("<Defaulttags ");
        sbuff.append("TAGID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TAGID),12, true)+"\" ");
        sbuff.append("DATAFORMATID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATID),12, true)+"\" ");
        sbuff.append("DESCRIPTION=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DESCRIPTION),12, true)+"\" ");
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
    sbuff.append("<Defaulttags ");
        sbuff.append("TAGID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TAGID),12, true)+"\" ");
        sbuff.append("DATAFORMATID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DATAFORMATID),12, true)+"\" ");
        sbuff.append("DESCRIPTION=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DESCRIPTION),12, true)+"\" ");
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
    sbuff.append("</Defaulttags>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Defaulttags ( ");
	    		sbuff.append("TAGID");
		    		sbuff.append(", DATAFORMATID");
	    		sbuff.append(", DESCRIPTION");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.TAGID,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.DATAFORMATID,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.DESCRIPTION,12)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getTagid() { 
    return this.TAGID;
  }
   public String getDataformatid() { 
    return this.DATAFORMATID;
  }
   public String getDescription() { 
    return this.DESCRIPTION;
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
     if (TAGID == null)
      TAGID = new String ("");
     if (DATAFORMATID == null)
      DATAFORMATID = new String ("");
     if (DESCRIPTION == null)
      DESCRIPTION = new String ("");
   }

   public void setTagid(String TAGID) {
    if (validateData){
      DataValidator.validateData((Object)TAGID,"TAGID",12,128,0);
    }
    modifiedColumns.add("TAGID");
    this.TAGID = TAGID;
  }
   public void setDataformatid(String DATAFORMATID) {
    if (validateData){
      DataValidator.validateData((Object)DATAFORMATID,"DATAFORMATID",12,100,0);
    }
    modifiedColumns.add("DATAFORMATID");
    this.DATAFORMATID = DATAFORMATID;
  }
   public void setDescription(String DESCRIPTION) {
    if (validateData){
      DataValidator.validateData((Object)DESCRIPTION,"DESCRIPTION",12,200,0);
    }
    modifiedColumns.add("DESCRIPTION");
    this.DESCRIPTION = DESCRIPTION;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Defaulttags o) {

         if ((((this.TAGID == null) || (o.TAGID == null)) && (this.TAGID != o.TAGID))
            || (((this.DATAFORMATID == null) || (o.DATAFORMATID == null)) && (this.DATAFORMATID != o.DATAFORMATID))
          ){
    return false;
    } else
         if ((((this.TAGID != null) && (o.TAGID != null)) && (this.TAGID.equals(o.TAGID) == false))
            || (((this.DATAFORMATID != null) && (o.DATAFORMATID != null)) && (this.DATAFORMATID.equals(o.DATAFORMATID) == false))
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

  public boolean equals(Defaulttags o) {

         if ((((this.TAGID == null) || (o.TAGID == null)) && (this.TAGID != o.TAGID))
            || (((this.DATAFORMATID == null) || (o.DATAFORMATID == null)) && (this.DATAFORMATID != o.DATAFORMATID))
            || (((this.DESCRIPTION == null) || (o.DESCRIPTION == null)) && (this.DESCRIPTION != o.DESCRIPTION))
          ){
    return false;
    } else
         if ((((this.TAGID != null) && (o.TAGID != null)) && (this.TAGID.equals(o.TAGID) == false))
            || (((this.DATAFORMATID != null) && (o.DATAFORMATID != null)) && (this.DATAFORMATID.equals(o.DATAFORMATID) == false))
            || (((this.DESCRIPTION != null) && (o.DESCRIPTION != null)) && (this.DESCRIPTION.equals(o.DESCRIPTION) == false))
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
  public static int getTagidColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTagidDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTagidSQLType() {
    
    return 12;   
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
  * return 200
  */
  public static int getDescriptionColumnSize() {
    
     return 200;   
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

  public Defaulttags getOriginal() {
    return original;
  }
   
  public void setOriginal(Defaulttags original) {
    this.original = (Defaulttags) original.clone();
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
