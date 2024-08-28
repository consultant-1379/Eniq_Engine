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

public class Busyhourrankkeys implements Cloneable,RockDBObject  {

    private String VERSIONID;
    private String BHLEVEL;
    private String BHTYPE;
    private String KEYNAME;
    private String KEYVALUE;
    private Long ORDERNBR;
    private String TARGETVERSIONID;
    private String BHOBJECT;
   
  private String timeStampName = "LAST_UPDATED";

  private String[] columnsAndSequences = {  };

  private String[] primaryKeyNames = {    "VERSIONID"    ,"BHLEVEL"    ,"BHTYPE"    ,"KEYNAME"    ,"TARGETVERSIONID"    ,"BHOBJECT"   };

  private RockFactory rockFact;

  private boolean newItem;
  
  private Set<String> modifiedColumns = new HashSet<String>();
  
  private boolean validateData = false;
  
  private Busyhourrankkeys original; 

  public Busyhourrankkeys(RockFactory rockFact) {
  	this(rockFact, false);
  	original = null; 
  }

  /**
   * Constructor to initialize all objects to null
   */
  public Busyhourrankkeys(RockFactory rockFact, boolean validate) {
    this.rockFact = rockFact;
    this.newItem = true;
    this.validateData = validate;
    
         this.VERSIONID = null;
         this.BHLEVEL = null;
         this.BHTYPE = null;
         this.KEYNAME = null;
         this.KEYVALUE = null;
         this.ORDERNBR = null;
         this.TARGETVERSIONID = null;
         this.BHOBJECT = null;
      	original = null; 

  }

  /**
   * Constructor for primary selection from database PRIMARY KEY MUST BE DEFINED
   * 
   * @params primarykeys
   * @exception SQLException
   */
  public Busyhourrankkeys(RockFactory rockFact   ,String VERSIONID ,String BHLEVEL ,String BHTYPE ,String KEYNAME ,String TARGETVERSIONID ,String BHOBJECT ) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;

            this.VERSIONID = VERSIONID;
            this.BHLEVEL = BHLEVEL;
            this.BHTYPE = BHTYPE;
            this.KEYNAME = KEYNAME;
            this.TARGETVERSIONID = TARGETVERSIONID;
            this.BHOBJECT = BHOBJECT;
      
      RockResultSet results = rockFact.setSelectSQL(true, this);
      Iterator<Busyhourrankkeys> it = rockFact.getData(this, results);
      if (it.hasNext()) {
        Busyhourrankkeys o = (Busyhourrankkeys) it.next();

              this.VERSIONID = o.getVersionid();
              this.BHLEVEL = o.getBhlevel();
              this.BHTYPE = o.getBhtype();
              this.KEYNAME = o.getKeyname();
              this.KEYVALUE = o.getKeyvalue();
              this.ORDERNBR = o.getOrdernbr();
              this.TARGETVERSIONID = o.getTargetversionid();
              this.BHOBJECT = o.getBhobject();
       
        results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Busyhourrankkeys");
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
  public Busyhourrankkeys(RockFactory rockFact, Busyhourrankkeys whereObject) throws SQLException,
      RockException {
    try {
      this.rockFact = rockFact;
      RockResultSet results = rockFact.setSelectSQL(false, whereObject);
      Iterator<Busyhourrankkeys> it = rockFact.getData(whereObject, results);
      if (it.hasNext()) {
        Busyhourrankkeys o = (Busyhourrankkeys) it.next();
                this.VERSIONID = o.getVersionid();
                this.BHLEVEL = o.getBhlevel();
                this.BHTYPE = o.getBhtype();
                this.KEYNAME = o.getKeyname();
                this.KEYVALUE = o.getKeyvalue();
                this.ORDERNBR = o.getOrdernbr();
                this.TARGETVERSIONID = o.getTargetversionid();
                this.BHOBJECT = o.getBhobject();
                results.close();
        this.newItem = false;
  	    this.original = this; 
      } else {
        results.close();
  	    this.original = this; 
        throw new SQLException(FactoryRes.CANNOT_GET_TABLE_DATA + "Busyhourrankkeys");
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
    return "Busyhourrankkeys";
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
  public int updateDB(boolean useTimestamp, Busyhourrankkeys whereObject) throws SQLException, RockException {
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
  public int deleteDB(Busyhourrankkeys whereObject) throws SQLException, RockException {
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
        throw new RockException("Cannot use rock.Busyhourrankkeys.saveDB(), no primary key defined");
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
    sbuff.append("<Busyhourrankkeys ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("BHLEVEL=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHLEVEL),12, true)+"\" ");
        sbuff.append("BHTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHTYPE),12, true)+"\" ");
        sbuff.append("KEYNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.KEYNAME),12, true)+"\" ");
        sbuff.append("KEYVALUE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.KEYVALUE),12, true)+"\" ");
        sbuff.append("ORDERNBR=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.ORDERNBR),2, true)+"\" ");
        sbuff.append("TARGETVERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TARGETVERSIONID),12, true)+"\" ");
        sbuff.append("BHOBJECT=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHOBJECT),12, true)+"\" ");
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
    sbuff.append("<Busyhourrankkeys ");
        sbuff.append("VERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.VERSIONID),12, true)+"\" ");
        sbuff.append("BHLEVEL=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHLEVEL),12, true)+"\" ");
        sbuff.append("BHTYPE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHTYPE),12, true)+"\" ");
        sbuff.append("KEYNAME=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.KEYNAME),12, true)+"\" ");
        sbuff.append("KEYVALUE=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.KEYVALUE),12, true)+"\" ");
        sbuff.append("ORDERNBR=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.ORDERNBR),2, true)+"\" ");
        sbuff.append("TARGETVERSIONID=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.TARGETVERSIONID),12, true)+"\" ");
        sbuff.append("BHOBJECT=\""+DataValidator.wrap(DataValidator.escapeXML(""+this.BHOBJECT),12, true)+"\" ");
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
    sbuff.append("</Busyhourrankkeys>\n");
    return sbuff.toString();
  }

  /**
   * Prints the object out as a sql Insert clause
   * 
   * @exception SQLException
   */
   
  public String toSQLInsert(){
       
    StringBuffer sbuff = new StringBuffer();
    sbuff.append("insert into Busyhourrankkeys ( ");
	    		sbuff.append("VERSIONID");
		    		sbuff.append(", BHLEVEL");
	    		sbuff.append(", BHTYPE");
	    		sbuff.append(", KEYNAME");
	    		sbuff.append(", KEYVALUE");
	    		sbuff.append(", ORDERNBR");
	    		sbuff.append(", TARGETVERSIONID");
	    		sbuff.append(", BHOBJECT");
	        sbuff.append(" ) values ( ");
    	        sbuff.append(""+DataValidator.wrap(""+this.VERSIONID,12)+"");
        	        sbuff.append(", "+DataValidator.wrap(""+this.BHLEVEL,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.BHTYPE,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.KEYNAME,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.KEYVALUE,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.ORDERNBR,2)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.TARGETVERSIONID,12)+"");
    	        sbuff.append(", "+DataValidator.wrap(""+this.BHOBJECT,12)+"");
    	    sbuff.append(" );\n");   
    return sbuff.toString();
  }
  

   public String getVersionid() { 
    return this.VERSIONID;
  }
   public String getBhlevel() { 
    return this.BHLEVEL;
  }
   public String getBhtype() { 
    return this.BHTYPE;
  }
   public String getKeyname() { 
    return this.KEYNAME;
  }
   public String getKeyvalue() { 
    return this.KEYVALUE;
  }
   public Long getOrdernbr() { 
    return this.ORDERNBR;
  }
   public String getTargetversionid() { 
    return this.TARGETVERSIONID;
  }
   public String getBhobject() { 
    return this.BHOBJECT;
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
     if (BHTYPE == null)
      BHTYPE = new String ("");
     if (KEYNAME == null)
      KEYNAME = new String ("");
     if (KEYVALUE == null)
      KEYVALUE = new String ("");
     if (ORDERNBR == null)
      ORDERNBR = new Long (0);
     if (TARGETVERSIONID == null)
      TARGETVERSIONID = new String ("");
     if (BHOBJECT == null)
      BHOBJECT = new String ("");
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
   public void setBhtype(String BHTYPE) {
    if (validateData){
      DataValidator.validateData((Object)BHTYPE,"BHTYPE",12,32,0);
    }
    modifiedColumns.add("BHTYPE");
    this.BHTYPE = BHTYPE;
  }
   public void setKeyname(String KEYNAME) {
    if (validateData){
      DataValidator.validateData((Object)KEYNAME,"KEYNAME",12,128,0);
    }
    modifiedColumns.add("KEYNAME");
    this.KEYNAME = KEYNAME;
  }
   public void setKeyvalue(String KEYVALUE) {
    if (validateData){
      DataValidator.validateData((Object)KEYVALUE,"KEYVALUE",12,128,0);
    }
    modifiedColumns.add("KEYVALUE");
    this.KEYVALUE = KEYVALUE;
  }
   public void setOrdernbr(Long ORDERNBR) {
    if (validateData){
      DataValidator.validateData((Object)ORDERNBR,"ORDERNBR",2,9,0);
    }
    modifiedColumns.add("ORDERNBR");
    this.ORDERNBR = ORDERNBR;
  }
   public void setTargetversionid(String TARGETVERSIONID) {
    if (validateData){
      DataValidator.validateData((Object)TARGETVERSIONID,"TARGETVERSIONID",12,128,0);
    }
    modifiedColumns.add("TARGETVERSIONID");
    this.TARGETVERSIONID = TARGETVERSIONID;
  }
   public void setBhobject(String BHOBJECT) {
    if (validateData){
      DataValidator.validateData((Object)BHOBJECT,"BHOBJECT",12,32,0);
    }
    modifiedColumns.add("BHOBJECT");
    this.BHOBJECT = BHOBJECT;
  }
 
  public void setcolumnsAndSequences(String[] newColsAndSeqs) {
    this.columnsAndSequences = newColsAndSeqs;
  }

  /**
   * dbEquals method test wheather the objects primary key values are equal.
   */

  public boolean dbEquals(Busyhourrankkeys o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.BHLEVEL == null) || (o.BHLEVEL == null)) && (this.BHLEVEL != o.BHLEVEL))
            || (((this.BHTYPE == null) || (o.BHTYPE == null)) && (this.BHTYPE != o.BHTYPE))
            || (((this.KEYNAME == null) || (o.KEYNAME == null)) && (this.KEYNAME != o.KEYNAME))
            || (((this.TARGETVERSIONID == null) || (o.TARGETVERSIONID == null)) && (this.TARGETVERSIONID != o.TARGETVERSIONID))
            || (((this.BHOBJECT == null) || (o.BHOBJECT == null)) && (this.BHOBJECT != o.BHOBJECT))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.BHLEVEL != null) && (o.BHLEVEL != null)) && (this.BHLEVEL.equals(o.BHLEVEL) == false))
            || (((this.BHTYPE != null) && (o.BHTYPE != null)) && (this.BHTYPE.equals(o.BHTYPE) == false))
            || (((this.KEYNAME != null) && (o.KEYNAME != null)) && (this.KEYNAME.equals(o.KEYNAME) == false))
            || (((this.TARGETVERSIONID != null) && (o.TARGETVERSIONID != null)) && (this.TARGETVERSIONID.equals(o.TARGETVERSIONID) == false))
            || (((this.BHOBJECT != null) && (o.BHOBJECT != null)) && (this.BHOBJECT.equals(o.BHOBJECT) == false))
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

  public boolean equals(Busyhourrankkeys o) {

         if ((((this.VERSIONID == null) || (o.VERSIONID == null)) && (this.VERSIONID != o.VERSIONID))
            || (((this.BHLEVEL == null) || (o.BHLEVEL == null)) && (this.BHLEVEL != o.BHLEVEL))
            || (((this.BHTYPE == null) || (o.BHTYPE == null)) && (this.BHTYPE != o.BHTYPE))
            || (((this.KEYNAME == null) || (o.KEYNAME == null)) && (this.KEYNAME != o.KEYNAME))
            || (((this.KEYVALUE == null) || (o.KEYVALUE == null)) && (this.KEYVALUE != o.KEYVALUE))
            || (((this.ORDERNBR == null) || (o.ORDERNBR == null)) && (this.ORDERNBR != o.ORDERNBR))
            || (((this.TARGETVERSIONID == null) || (o.TARGETVERSIONID == null)) && (this.TARGETVERSIONID != o.TARGETVERSIONID))
            || (((this.BHOBJECT == null) || (o.BHOBJECT == null)) && (this.BHOBJECT != o.BHOBJECT))
          ){
    return false;
    } else
         if ((((this.VERSIONID != null) && (o.VERSIONID != null)) && (this.VERSIONID.equals(o.VERSIONID) == false))
            || (((this.BHLEVEL != null) && (o.BHLEVEL != null)) && (this.BHLEVEL.equals(o.BHLEVEL) == false))
            || (((this.BHTYPE != null) && (o.BHTYPE != null)) && (this.BHTYPE.equals(o.BHTYPE) == false))
            || (((this.KEYNAME != null) && (o.KEYNAME != null)) && (this.KEYNAME.equals(o.KEYNAME) == false))
            || (((this.KEYVALUE != null) && (o.KEYVALUE != null)) && (this.KEYVALUE.equals(o.KEYVALUE) == false))
            || (((this.ORDERNBR != null) && (o.ORDERNBR != null)) && (this.ORDERNBR.equals(o.ORDERNBR) == false))
            || (((this.TARGETVERSIONID != null) && (o.TARGETVERSIONID != null)) && (this.TARGETVERSIONID.equals(o.TARGETVERSIONID) == false))
            || (((this.BHOBJECT != null) && (o.BHOBJECT != null)) && (this.BHOBJECT.equals(o.BHOBJECT) == false))
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
  * return 32
  */
  public static int getBhtypeColumnSize() {
    
     return 32;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getBhtypeDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getBhtypeSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getKeynameColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getKeynameDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getKeynameSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getKeyvalueColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getKeyvalueDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getKeyvalueSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 9
  */
  public static int getOrdernbrColumnSize() {
    
     return 9;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getOrdernbrDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 2
  */
  public static int getOrdernbrSQLType() {
    
    return 2;   
  }
    
 
  /**
  * get columnSize
  * return 128
  */
  public static int getTargetversionidColumnSize() {
    
     return 128;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getTargetversionidDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getTargetversionidSQLType() {
    
    return 12;   
  }
    
 
  /**
  * get columnSize
  * return 32
  */
  public static int getBhobjectColumnSize() {
    
     return 32;   
  }

 /**
  * get DecimalDigits
  * return 0
  */
  public static int getBhobjectDecimalDigits() {
    
     return 0;   
  }
  
 /**
  * get SQLType
  * return 12
  */
  public static int getBhobjectSQLType() {
    
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

  public Busyhourrankkeys getOriginal() {
    return original;
  }
   
  public void setOriginal(Busyhourrankkeys original) {
    this.original = (Busyhourrankkeys) original.clone();
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
