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

public class Partitionplan implements Cloneable,RockDBObject  {

    private String PARTITIONPLAN;
    private Long DEFAULTSTORAGETIME;
    private Long DEFAULTPARTITIONSIZE;
    private Long MAXSTORAGETIME;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "PARTITIONPLAN"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Partitionplan original; 

  public Partitionplan(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Partitionplan(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.PARTITIONPLAN = null;
         this.DEFAULTSTORAGETIME = null;
         this.DEFAULTPARTITIONSIZE = null;
         this.MAXSTORAGETIME = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Partitionplan(RockFactory rockFact   ,String PARTITIONPLAN ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.PARTITIONPLAN = PARTITIONPLAN;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Partitionplan> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Partitionplan o = (Partitionplan) it.next();

              this.PARTITIONPLAN = o.getPartitionplan();
              this.DEFAULTSTORAGETIME = o.getDefaultstoragetime();
              this.DEFAULTPARTITIONSIZE = o.getDefaultpartitionsize();
              this.MAXSTORAGETIME = o.getMaxstoragetime();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Partitionplan");
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
  public Partitionplan(RockFactory rockFact, Partitionplan whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Partitionplan> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Partitionplan o = (Partitionplan) it.next();
                this.PARTITIONPLAN = o.getPartitionplan();
                this.DEFAULTSTORAGETIME = o.getDefaultstoragetime();
                this.DEFAULTPARTITIONSIZE = o.getDefaultpartitionsize();
                this.MAXSTORAGETIME = o.getMaxstoragetime();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Partitionplan");
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
    return "Partitionplan";
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
  public int updateDB(boolean useTimestamp, Partitionplan whereObject) throws SQLException, RockException {
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
  public int deleteDB(Partitionplan whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Partitionplan.saveDB(), no primary key defined");
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
    sbuff.append("<Partitionplan ");
        sbuff.append("PARTITIONPLAN=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PARTITIONPLAN),12, true)+"\" ");
        sbuff.append("DEFAULTSTORAGETIME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DEFAULTSTORAGETIME),2, true)+"\" ");
        sbuff.append("DEFAULTPARTITIONSIZE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DEFAULTPARTITIONSIZE),2, true)+"\" ");
        sbuff.append("MAXSTORAGETIME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.MAXSTORAGETIME),2, true)+"\" ");
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
    sbuff.append("<Partitionplan ");
        sbuff.append("PARTITIONPLAN=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.PARTITIONPLAN),12, true)+"\" ");
        sbuff.append("DEFAULTSTORAGETIME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DEFAULTSTORAGETIME),2, true)+"\" ");
        sbuff.append("DEFAULTPARTITIONSIZE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.DEFAULTPARTITIONSIZE),2, true)+"\" ");
        sbuff.append("MAXSTORAGETIME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.MAXSTORAGETIME),2, true)+"\" ");
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
    sbuff.append("</Partitionplan>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Partitionplan ( ");
	    		sbuff.append("PARTITIONPLAN");
		    		sbuff.append(", DEFAULTSTORAGETIME");
	    		sbuff.append(", DEFAULTPARTITIONSIZE");
	    		sbuff.append(", MAXSTORAGETIME");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.PARTITIONPLAN,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.DEFAULTSTORAGETIME,2)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.DEFAULTPARTITIONSIZE,2)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.MAXSTORAGETIME,2)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getPartitionplan() { 
    return this.PARTITIONPLAN;
  }
   public Long getDefaultstoragetime() { 
    return this.DEFAULTSTORAGETIME;
  }
   public Long getDefaultpartitionsize() { 
    return this.DEFAULTPARTITIONSIZE;
  }
   public Long getMaxstoragetime() { 
    return this.MAXSTORAGETIME;
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
     if (PARTITIONPLAN == null)
      PARTITIONPLAN = new String ("");
     if (DEFAULTSTORAGETIME == null)
      DEFAULTSTORAGETIME = new Long (0);
     if (DEFAULTPARTITIONSIZE == null)
      DEFAULTPARTITIONSIZE = new Long (0);
     if (MAXSTORAGETIME == null)
      MAXSTORAGETIME = new Long (0);
   }

   public void setPartitionplan(String PARTITIONPLAN) {
    if (validateData){
      DataValidator.validateData((Object)PARTITIONPLAN,"PARTITIONPLAN",12,128,0);
    }
    modifiedColumns.add("PARTITIONPLAN");
    this.PARTITIONPLAN = PARTITIONPLAN;
  }
   public void setDefaultstoragetime(Long DEFAULTSTORAGETIME) {
    if (validateData){
      DataValidator.validateData((Object)DEFAULTSTORAGETIME,"DEFAULTSTORAGETIME",2,9,0);
    }
    modifiedColumns.add("DEFAULTSTORAGETIME");
    this.DEFAULTSTORAGETIME = DEFAULTSTORAGETIME;
  }
   public void setDefaultpartitionsize(Long DEFAULTPARTITIONSIZE) {
    if (validateData){
      DataValidator.validateData((Object)DEFAULTPARTITIONSIZE,"DEFAULTPARTITIONSIZE",2,9,0);
    }
    modifiedColumns.add("DEFAULTPARTITIONSIZE");
    this.DEFAULTPARTITIONSIZE = DEFAULTPARTITIONSIZE;
  }
   public void setMaxstoragetime(Long MAXSTORAGETIME) {
    if (validateData){
      DataValidator.validateData((Object)MAXSTORAGETIME,"MAXSTORAGETIME",2,9,0);
    }
    modifiedColumns.add("MAXSTORAGETIME");
    this.MAXSTORAGETIME = MAXSTORAGETIME;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Partitionplan o) {

         if ((((this.PARTITIONPLAN == null) || (o.PARTITIONPLAN == null)) && (this.PARTITIONPLAN != o.PARTITIONPLAN))
          ){
    return false;
    } else
         if ((((this.PARTITIONPLAN != null) && (o.PARTITIONPLAN != null)) && (this.PARTITIONPLAN.equals(o.PARTITIONPLAN) == false))
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

  public boolean equals(Partitionplan o) {

         if ((((this.PARTITIONPLAN == null) || (o.PARTITIONPLAN == null)) && (this.PARTITIONPLAN != o.PARTITIONPLAN))
            || (((this.DEFAULTSTORAGETIME == null) || (o.DEFAULTSTORAGETIME == null)) && (this.DEFAULTSTORAGETIME != o.DEFAULTSTORAGETIME))
            || (((this.DEFAULTPARTITIONSIZE == null) || (o.DEFAULTPARTITIONSIZE == null)) && (this.DEFAULTPARTITIONSIZE != o.DEFAULTPARTITIONSIZE))
            || (((this.MAXSTORAGETIME == null) || (o.MAXSTORAGETIME == null)) && (this.MAXSTORAGETIME != o.MAXSTORAGETIME))
          ){
    return false;
    } else
         if ((((this.PARTITIONPLAN != null) && (o.PARTITIONPLAN != null)) && (this.PARTITIONPLAN.equals(o.PARTITIONPLAN) == false))
            || (((this.DEFAULTSTORAGETIME != null) && (o.DEFAULTSTORAGETIME != null)) && (this.DEFAULTSTORAGETIME.equals(o.DEFAULTSTORAGETIME) == false))
            || (((this.DEFAULTPARTITIONSIZE != null) && (o.DEFAULTPARTITIONSIZE != null)) && (this.DEFAULTPARTITIONSIZE.equals(o.DEFAULTPARTITIONSIZE) == false))
            || (((this.MAXSTORAGETIME != null) && (o.MAXSTORAGETIME != null)) && (this.MAXSTORAGETIME.equals(o.MAXSTORAGETIME) == false))
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
  public static int getPartitionplanColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getPartitionplanDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getPartitionplanSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 9
  */
  public static int getDefaultstoragetimeColumnSize() {
    
     return 9;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDefaultstoragetimeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getDefaultstoragetimeSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 9
  */
  public static int getDefaultpartitionsizeColumnSize() {
    
     return 9;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getDefaultpartitionsizeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getDefaultpartitionsizeSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 9
  */
  public static int getMaxstoragetimeColumnSize() {
    
     return 9;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getMaxstoragetimeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getMaxstoragetimeSQLType() {
    
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

  public Partitionplan getOriginal() {
    return original;
  }
   
  public void setOriginal(Partitionplan original) {
    this.original = (Partitionplan) original.clone();
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
