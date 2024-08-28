/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package ssc.rockfactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class RockFactory implements RockFactory_I {

  protected Connection connection;

  protected String dbURL;

  protected String driverName;

  protected String strUserName;

  protected String strPassword;

  protected boolean autoCommit;

  protected String sqlQuote;
  
  protected String dbType;

  /**
   * Create RockFactory with connection name
   */
  public RockFactory(String dbURL, String dbType, String strUserName, String strPassword, String driverName, String conName, boolean autoCommit)
      throws SQLException, RockException {
    this(dbURL, dbType, strUserName, strPassword, driverName, conName, autoCommit,-1);
  }
  
  /**
   * Create RockFactory with connection name
   */
  public RockFactory(String dbURL, String strUserName, String strPassword, String driverName, String conName, boolean autoCommit)
      throws SQLException, RockException {
    this(dbURL, "sybase", strUserName, strPassword, driverName, conName, autoCommit);
  }
  
    /**
   * Create RockFactory with connection name
   */
  public RockFactory(String dbURL, String strUserName, String strPassword, String driverName, String conName, boolean autoCommit, int isolationLevel)
      throws SQLException, RockException {
    this(dbURL, "sybase", strUserName, strPassword, driverName, conName, autoCommit,isolationLevel);
  }
  
  /**
   * Create RockFactory with connection name
   */
  public RockFactory(String dbURL, String dbType, String strUserName, String strPassword, String driverName, String conName, boolean autoCommit, int isolationLevel)
      throws SQLException, RockException {

    this.dbURL = dbURL;
    this.dbType = dbType;
    this.driverName = driverName;
    this.strUserName = strUserName;
    this.strPassword = strPassword;
    this.autoCommit = autoCommit;
    this.connection = initiateConnection(conName, isolationLevel);
  }

private Connection initiateConnection(String conName, int isolationLevel) throws SQLException, RockException {
    Connection conn = null;
    try {
      Driver driver = (Driver) Class.forName(driverName).newInstance();
      Properties p = new Properties();
      p.put("user", strUserName);
      p.put("password", strPassword);
      if (dbType.equalsIgnoreCase( "sybase" ))
      {
        p.put("REMOTEPWD",",,CON="+conName);    
      }
      if (isolationLevel != -1) {
        p.put("SQLINITSTRING", "SET OPTION "+strUserName+".ISOLATION_LEVEL="+isolationLevel);
      }
      conn = driver.connect(dbURL, p);
      conn.setAutoCommit(autoCommit);

      DatabaseMetaData metaData = conn.getMetaData();
      this.sqlQuote = metaData.getIdentifierQuoteString();

    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException("Driver not found", e);
    }
    return conn;
  }

  /**
   * Return the correct is null database function according to the databse type.
   * 
   * @return String
   */
  private String getIsNullFName() {

    if (this.driverName.indexOf(FactoryRes.SYBASE_DRIVER_NAME) > 0) {
      return FactoryRes.SYBASE_IS_NULL_FUNCTION_NAME;
    } else {
      return FactoryRes.ORACLE_IS_NULL_FUNCTION_NAME;
    }

  }

  /**
   * Returns the sql error code when child records exist when trying to delete a row.
   * 
   * @return
   */
  public int getChildRecorsExistCode() {
    if (this.driverName.indexOf(FactoryRes.SYBASE_DRIVER_NAME) > 0) {
      return FactoryRes.SYBASE_CHILD_RECORD_EXIST_CODE;
    } else {
      return FactoryRes.ORACLE_CHILD_RECORD_EXIST_CODE;
    }
  }

  public String getDbURL() {
    return this.dbURL;
  }

  public String getDriverName() {
    return this.driverName;
  }

  public String getUserName() {
    return this.strUserName;
  }

  public String getPassword() {
    return this.strPassword;
  }

  public boolean getAutoCommit() {
    return this.autoCommit;
  }

  /**
   * isColumnName tests if name is a columns name
   * 
   * @return boolean true if a column name, else false
   * @exception
   */
  protected boolean isColumnName(String fieldName, String fieldTypeName) {
    if (fieldTypeName.equals("Vector"))
      return false;
    for (int i = 0; i < FactoryRes.NOT_COLUMN_NAMES.length; i++) {
      if (fieldName.equals(FactoryRes.NOT_COLUMN_NAMES[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * getFieldValueWithQuotes returns a string containing quotes if type is ´String or Timestamp.
   * 
   * @return a SELECT - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String getFieldValueWithQuotes(Object obj, Field field, Method method) throws RockException {
    try {
      String[] argv = null;
      Object tempObj = method.invoke(obj, argv);
      String helpString = "NULL";
      if (tempObj != null) {
        helpString = tempObj.toString();
      }

      Class fieldClass = field.getType();

      String className = fieldClass.toString();
      int pointLoc = className.lastIndexOf(".") + 1;
      String name = className.substring(pointLoc, className.length());

      if (name.equals("Timestamp")) {
        if (helpString.equals("NULL")) {
          return helpString;
        } else {
          pointLoc = helpString.lastIndexOf(".");
          if (this.driverName.indexOf(FactoryRes.ORACLE_DRIVER_NAME) > 0) {
            return "TO_DATE('" + helpString.substring(0, pointLoc) + "','yyyy-mm-dd hh24:mi:ss')";
          } else {
            return "'" + helpString.substring(0, pointLoc) + "'";
          }
        }
      } else {
        if (name.equals("String")) {
          if (helpString.equals("NULL")) {
            return helpString;
          } else {
            String newString = "";
            newString = helpString.replaceAll("'","''");
            if (newString.length() > 0) {
              return "'" + newString + "'";
            } else {
              return "'" + helpString + "'";
            }
          }
        } else {
          return helpString;
        }
      }
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createSelectSQL is general SQL -clause parser for all of the SELECT -clauses.
   * 
   * @return a SELECT - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String createSelectSQL(Object obj, Class<?> c, String whereString) throws RockException {

    String sQLString = "SELECT ";
    Field[] classFields = c.getDeclaredFields();

    for (int i = 0; i < classFields.length; i++) {

      Field field = classFields[i];
      String fieldType = field.getType().getName();
      String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

      if (isColumnName(field.getName(), fieldTypeName)) {

        if (!sQLString.equals("SELECT ")) {
          sQLString = new StringBuffer(sQLString).append(",").toString();
        }
        sQLString = new StringBuffer(sQLString).append(field.getName()).toString();
      }

    }

    sQLString = new StringBuffer(sQLString).append(" FROM ").append(getTableName(obj, c)).toString();
    sQLString = new StringBuffer(sQLString).append(whereString).toString();

    return sQLString;

  }

  /**
   * getFieldGetMethod returns the getmethod for a field
   * 
   * @return method get method the a given field.
   * @exception
   */
  protected Method getFieldMethod(Class<?> objClass, Field field, String setOrGet) throws RockException {
    try {
      String columnName = field.getName();
      String firstCharOfColumn = columnName.substring(0, 1);
      String restOfTheColumn = columnName.substring(1, columnName.length());

      String getMethodName = setOrGet + firstCharOfColumn.toUpperCase() + restOfTheColumn.toLowerCase();
      Class<?>[] parameterTypes = { field.getType() };
      if (setOrGet.equals("get")) {
        parameterTypes = null;
      }
      return objClass.getMethod(getMethodName, parameterTypes);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createUpdateSQL is general SQL -clause parser for all of the UPDATE -clauses.
   * 
   * @return a UPDATE - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String createUpdateSQL(Object obj, Class<?> dataClass, String whereString) throws RockException {
    try {
      String sQLString = "UPDATE ";
      sQLString = new StringBuffer(sQLString).append(getTableName(obj, dataClass)).append(" SET ").toString();
      Field[] classFields = dataClass.getDeclaredFields();
      String setStr = " ";

      Set<String> modCols = null;
      
      if (obj instanceof RockDBObject)
        modCols = ((RockDBObject)obj).gimmeModifiedColumns();
     
      for (int i = 0; i < classFields.length; i++) {

        Field field = classFields[i];
        String fieldType = field.getType().getName();
        String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

        if (isColumnName(field.getName(), fieldTypeName)) {

          if (modCols!=null && !modCols.contains(field.getName()))
            continue;             
          
          if (!setStr.equals(" ")) {
            setStr = new StringBuffer(setStr).append(",").toString();
          }
          setStr = new StringBuffer(setStr).append(field.getName()).append("=").toString();
          Method method = getFieldMethod(dataClass, field, "get");
          setStr = new StringBuffer(setStr).append(getFieldValueWithQuotes(obj, field, method)).toString();
        }
      }
      sQLString = new StringBuffer(sQLString).append(setStr).toString();
      sQLString = new StringBuffer(sQLString).append(whereString).toString();

      return sQLString;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * compares a string and a string vector
   * 
   * @param String
   * @param String[]
   * @return int index of a string in the string vector
   * @exception
   */

  private int getSeqColIndex(String fieldName, String[] colSeqNames) {
    for (int i = 0; i < colSeqNames.length - 1; i++)
      if (fieldName.toLowerCase().equals(colSeqNames[i].toLowerCase()))
        return i;
    return -1;
  }

  /**
   * Check if the parameter column name exists in the class fields
   * 
   * @param dataClass
   *          class where the field should be
   * @param columnName
   *          column name to compare
   * @return boolean true if the column existed else false
   */
  private boolean columnExists(Class<?> dataClass, String columnName) {
    Field[] classFields = dataClass.getDeclaredFields();

    for (int i = 0; i < classFields.length; i++) {
      Field field = classFields[i];

      if (field.getName().toLowerCase().equals(columnName.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * createInsertSQL is general SQL -clause parser for all of the INSERT -clauses.
   * 
   * @param boolean
   *          useTimestamp If true, the current time is automatically updated.
   * @return a INSERT -String SQL -clause
   * @exception SQLException
   */
  protected String createInsertSQL(Object obj, Class<?> dataClass, boolean useTimestamp, boolean useSequence)
      throws SQLException, RockException {
    try {
      String sQLString = "INSERT INTO ";
      sQLString = new StringBuffer(sQLString).append(getTableName(obj, dataClass)).append(" (").toString();
      
      Field[] classFields = dataClass.getDeclaredFields();
      String columnStr = " ";
      for (int i = 0; i < classFields.length; i++) {

        Field field = classFields[i];
        String fieldType = field.getType().getName();
        String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

        if (isColumnName(field.getName(), fieldTypeName)) {

          if (!columnStr.equals(" ")) {
            columnStr = new StringBuffer(columnStr).append(",").toString();
          }
          columnStr = new StringBuffer(columnStr).append(field.getName()).toString();
        }

      }

      sQLString = new StringBuffer(sQLString).append(columnStr).toString();
      sQLString = new StringBuffer(sQLString).append(") VALUES(").toString();
      String valuesStr = " ";
      Method timeGetMethod = dataClass.getDeclaredMethod(FactoryRes.TIMESTAMP_METHOD, null);
      String timeStampColumnName = (String) timeGetMethod.invoke(obj, null);
      Method seqGetMethod = dataClass.getDeclaredMethod(FactoryRes.COLS_SEQS_METHOD, null);
      String[] colSeqNames = (String[]) seqGetMethod.invoke(obj, null);

      for (int i = 0; i < classFields.length; i++) {

        Field field = classFields[i];
        String fieldType = field.getType().getName();
        String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

        if (isColumnName(field.getName(), fieldTypeName)) {

          if (!valuesStr.equals(" ")) {
            valuesStr = new StringBuffer(valuesStr).append(",").toString();
          }
          if (useTimestamp) {
            if (field.getName().toUpperCase().equals(timeStampColumnName.toUpperCase())) {
              Method timeTsSetMethod = getFieldMethod(dataClass, dataClass.getDeclaredField(timeStampColumnName), "set");
              Timestamp currentTime = new Timestamp(System.currentTimeMillis());
              Object[] objVect = { currentTime };
              timeTsSetMethod.invoke(obj, objVect);
            }
          }
          if (useSequence) {
            int seqColIndex = getSeqColIndex(field.getName(), colSeqNames);
            if (seqColIndex >= 0) {
              String columnName = colSeqNames[seqColIndex];
              String sequenceName = colSeqNames[seqColIndex + 1];

              String sql1 = "";

              if (sequenceName.equals("MAXVALUE")) {
                sql1 = "SELECT MAX(" + this.getIsNullFName() + "(" + columnName + ",0))+1 FROM "
                    + getTableName(obj, dataClass) + " " + createWhereSQLfromPrimaries(obj, columnName);
              } else {
                sql1 = "SELECT " + sequenceName + ".NEXTVAL" + " FROM DUAL";
              }
              Statement sqlStatement = connection.createStatement();
              ResultSet getSeqResults = sqlStatement.executeQuery(sql1);

              int i_fromDBSeq = 0;
              long l_fromDBSeq = 0;
              if (getSeqResults.next()) {
                if (fieldTypeName.equals("Long"))
                  l_fromDBSeq = getSeqResults.getLong(1);
                else
                  i_fromDBSeq = getSeqResults.getInt(1);
              }
              Method colSeqSetMethod = getFieldMethod(dataClass, dataClass.getDeclaredField(columnName), "set");
              if (fieldTypeName.equals("Long")) {
                Long seqValue = new Long(l_fromDBSeq);
                Object[] objVect = { seqValue };
                colSeqSetMethod.invoke(obj, objVect);
              } else {
                Integer seqValue = new Integer(i_fromDBSeq);
                Object[] objVect = { seqValue };
                colSeqSetMethod.invoke(obj, objVect);
              }
              sqlStatement.close();
            }
          }

          Method method = getFieldMethod(dataClass, field, "get");
          valuesStr = new StringBuffer(valuesStr).append(getFieldValueWithQuotes(obj, field, method)).toString();
        }

      }
      sQLString = new StringBuffer(sQLString).append(valuesStr).append(" )").toString();
      return sQLString;
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * getTableName gets the table name from a class name.
   * 
   * @param Class
   *          dataclass
   * @return String tablename
   * @exception
   */

  protected String getTableName(Object obj, Class<?> dataClass) throws RockException {

    try {
      Method getTableNameMethod = dataClass.getMethod(FactoryRes.GET_TABLE_NAME_METHOD_NAME, new Class[] {});
      String tableName = (String) getTableNameMethod.invoke(obj, new Object[] {});

      return tableName;

    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }

  }

  /**
   * fieldValueOtherThanInitial tests wheather the fields value is the initial one or not
   * 
   * @param Object
   *          obj
   * @param Class
   *          whereClass
   * @param Field
   *          field
   * @return Boolean false if the field contains the initial value else true
   * @exception
   */
  protected boolean fieldValueOtherThanInitial(Object obj, Class<?> whereClass, Field field) throws RockException {
    try {
      Method method = getFieldMethod(whereClass, field, "get");
      String[] argv = null;
      Object tempObj = method.invoke(obj, argv);

      // Class<?> tempClass = method.getReturnType();

      if (tempObj == null) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createWhereSQL is general SQL -clause parser for all of the WHERE -parts.
   * 
   * @return a WHERE -SQL clause
   * @exception
   */
  protected String createWhereSQL(Object obj, Class<?> whereClass) throws RockException {
    try {
      String whereString = " ";
      Field[] classFields = whereClass.getDeclaredFields();

      for (int i = 0; i < classFields.length; i++) {

        Field field = classFields[i];
        String fieldType = field.getType().getName();
        String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

        if (isColumnName(field.getName(), fieldTypeName)) {

          if (fieldValueOtherThanInitial(obj, whereClass, field)) {

            if (!whereString.equals(" ")) {
              whereString = new StringBuffer(whereString).append(" AND ").toString();
            }

            Method method = getFieldMethod(whereClass, field, "get");
            String fieldValue = getFieldValueWithQuotes(obj, field, method);

//  LIKE caused problems with strings containing "%"'s when data was updated ("ASA Error -606: The pattern is too long")            
//            if (fieldValue.indexOf("%") < 0) {
//              whereString = new StringBuffer(whereString).append(field.getName()).append("=").append(fieldValue)
//                  .toString();
//            } else {
//              whereString = new StringBuffer(whereString).append(field.getName()).append(" LIKE ").append(fieldValue)
//                  .toString();
//            }
            whereString = new StringBuffer(whereString).append(field.getName()).append("=").append(fieldValue)
                  .toString();
          }
        }
      }
      if (!whereString.equals(" ")) {
        whereString = " WHERE " + whereString;
      }

      return whereString;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }

  }

  /**
   * createWhereSQLfromPrimaries Creates a SQL clause from primaryKey columns.
   * 
   * @return a WHERE -SQL clause
   * @exception
   */
  private String createWhereSQLfromPrimaries(Object dataObj, String columnNotIncluded) throws RockException {
    String whereSQL = "";
    try {
      Method pkGetMethod = dataObj.getClass().getDeclaredMethod(FactoryRes.PRIMARYKEY_METHOD, null);
      String[] pkColumnNames = (String[]) pkGetMethod.invoke(dataObj, null);
      if (pkColumnNames.length > 0) {
        whereSQL = " ";
        for (int i = 0; i < pkColumnNames.length; i++) {
          Field field = dataObj.getClass().getDeclaredField(pkColumnNames[i]);
          if ((fieldValueOtherThanInitial(dataObj, dataObj.getClass(), field))
              && ((columnNotIncluded == null) || (columnNotIncluded.equals(field.getName()) == false))) {
            if (!whereSQL.equals(" ")) {
              whereSQL = new StringBuffer(whereSQL).append(" AND ").toString();
            }
            Method method = getFieldMethod(dataObj.getClass(), field, "get");
            String fieldValue = getFieldValueWithQuotes(dataObj, field, method);

            if (fieldValue.indexOf("%") < 0) {
              whereSQL = new StringBuffer(whereSQL).append(pkColumnNames[i]).append("=").append(fieldValue).toString();
            } else {
              whereSQL = new StringBuffer(whereSQL).append(pkColumnNames[i]).append(" LIKE ").append(fieldValue)
                  .toString();
            }
          }

        }
        if (!whereSQL.equals(" "))
          whereSQL = " WHERE " + whereSQL;
      } else {
        throw new RockException(FactoryRes.NO_PRIMARYKEY_INFO);
      }
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
    return whereSQL;
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @param boolean
   *          useTimestamp If true, the current time is automatically updated.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  private int insertDataPriv(Object dataObj, boolean useTimestamp, boolean useSequence) throws SQLException,
      RockException {
    
    // Log the insert operation performed on the database table
    TableModificationLogger.instance().add("INSERT" + " " + dbURL + " " + getTableName(dataObj, dataObj.getClass()));
    
    int records = -1;
    try {
      Statement sqlStatement = connection.createStatement();
      String sql = createInsertSQL(dataObj, dataObj.getClass(), useTimestamp, useSequence);

      records = sqlStatement.executeUpdate(sql);
      sqlStatement.close();
      return records;
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @param boolean
   *          useTimestamp If true, the current time is automatically updated.
   * @param boolean
   *          useTimestamp If true, the number fields associated with sequences get values
   *          automatically.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  public int insertData(Object dataObj, boolean useTimestamp, boolean useSequence) throws SQLException, RockException {
    return insertDataPriv(dataObj, useTimestamp, useSequence);
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  public int insertData(Object dataObj) throws SQLException, RockException {
    return insertDataPriv(dataObj, true, true);
  }

  /**
   * Executes a delete SQL -clause based on given object data and class information.
   * 
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  private int deleteDataPriv(boolean isPrimaryKeyDelete, Object whereObj, String whereString) throws SQLException,
      RockException {
    
    // Log the delete operation performed on the database table
    TableModificationLogger.instance().add("DELETE" + " " + dbURL + " " + getTableName(whereObj, whereObj.getClass()));
          
    int records = -1;
    try {
      String whereSQL = "";
      if (isPrimaryKeyDelete)
        whereSQL = createWhereSQLfromPrimaries(whereObj, null);
      else if (whereObj != null)
        whereSQL = createWhereSQL(whereObj, whereObj.getClass());
      // dead code
      //else if (whereString != null)
      //  if (whereString.equals("") == false)
      //    whereSQL = " WHERE " + whereString;

      Statement sqlStatement = connection.createStatement();
      String sql = "DELETE FROM " + getTableName(whereObj, whereObj.getClass()) + " " + whereSQL;
      
      records = sqlStatement.executeUpdate(sql);
      sqlStatement.close();

      return records;
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * Executes a delete SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          whereObj The data object to delete.
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  public int deleteData(boolean isPrimaryKeyDelete, Object whereObj) throws SQLException, RockException {
    return deleteDataPriv(isPrimaryKeyDelete, whereObj, null);
  }

  /**
   * Executes a delete SQL -clause based on given object data and class information.
   * 
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  public int deleteData(boolean isPrimaryKeyDelete, String whereString) throws SQLException, RockException {
    return deleteDataPriv(isPrimaryKeyDelete, null, whereString);
  }

  /**
   * updateData Executes an update SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean
   *          isPrimaryKeyUpdate If true, where -clause is created from primary key information.
   * @param Object
   *          whereObj The data for the WHERE -clause.
   * @param boolean
   *          useTimestamp If false, it is not checked wheather the data has changed.
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @return int Number of updated rows.
   * @exception SQLException
   */
  private int updateDataPriv(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj, boolean useTimestamp,
      String whereString) throws SQLException, RockException {
    
    // Log the update operation performed on the database table
    TableModificationLogger.instance().add("UPDATE" + " " + dbURL + " " + getTableName(dataObj, dataObj.getClass()));
    
    int records = -1;
    String whereSQL = "";

    if (isPrimaryKeyUpdate)
      whereSQL = createWhereSQLfromPrimaries(dataObj, null);
    else if (whereObj != null)
      whereSQL = createWhereSQL(whereObj, whereObj.getClass());
    else if (whereString != null)
      if (whereString.equals("") == false)
        whereSQL = " WHERE " + whereString;

    Method timeTsSetMethod = null;
    Timestamp timeStampColumnValue = null;

    try {
      if (useTimestamp) {
        Method timeGetMethod = dataObj.getClass().getDeclaredMethod(FactoryRes.TIMESTAMP_METHOD, null);
        String timeStampColumnName = (String) timeGetMethod.invoke(dataObj, null);

        if ((timeStampColumnName != null) && (timeStampColumnName.equals("") == false)
            && (columnExists(dataObj.getClass(), timeStampColumnName))) {

          Method timeTsGetMethod = getFieldMethod(dataObj.getClass(), dataObj.getClass().getDeclaredField(
              timeStampColumnName), "get");

          timeStampColumnValue = (Timestamp) timeTsGetMethod.invoke(dataObj, null);

          Statement sqlStatement = connection.createStatement();
          String sql1 = "SELECT " + timeStampColumnName + " FROM " + getTableName(dataObj, dataObj.getClass())
              + whereSQL + " FOR UPDATE";

          ResultSet forUpdateResults = sqlStatement.executeQuery(sql1);

          Timestamp fromDBTs = new Timestamp(0);
          if (forUpdateResults.next()) {
            fromDBTs = forUpdateResults.getTimestamp(timeStampColumnName);
          } else {
            throw new RockException(FactoryRes.CANNOT_GET_TIMESTAMP);
          }
          if (fromDBTs != null) {
            timeStampColumnValue.setNanos(0);
            if (fromDBTs.equals(timeStampColumnValue) == false)
              throw new RockException(FactoryRes.SOMEONE_HAS_UPDATED + " Database: " + fromDBTs.toString()
                  + " Curr.record: " + timeStampColumnValue.toString());
          }

          timeTsSetMethod = getFieldMethod(dataObj.getClass(),
              dataObj.getClass().getDeclaredField(timeStampColumnName), "set");

          Timestamp currentTime = new Timestamp(System.currentTimeMillis());
          Object[] objVect = { currentTime };
          timeTsSetMethod.invoke(dataObj, objVect);
          sqlStatement.close();

        }
      }
      Statement sqlStatement = connection.createStatement();
      String sql2 = createUpdateSQL(dataObj, dataObj.getClass(), whereSQL);

      records = sqlStatement.executeUpdate(sql2);
      sqlStatement.close();

      return records;
    } catch (SQLException sqlE) {
      // Return the timestamp column value as it were not updated
      if ((timeTsSetMethod != null) && (timeStampColumnValue != null)) {
        try {
          Object[] objVect = { timeStampColumnValue };
          timeTsSetMethod.invoke(dataObj, objVect);
        } catch (Exception e) {
          throw new RockException(e.getMessage(), e);
        }
      }
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * Executes an update SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean
   *          isPrimaryKeyUpdate If true, where -clause is created from primary key information.
   * @param Object
   *          whereObj The data for the WHERE -clause.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj) throws SQLException, RockException {
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, whereObj, true, null);
  }

  /**
   * Executes an update SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean
   *          isPrimaryKeyUpdate If true, where -clause is created from primary key information.
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param boolean
   *          useTimestamp If false, it is not checked wheather the data has changed.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj, boolean useTimestamp)
      throws SQLException, RockException {
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, whereObj, useTimestamp, null);
  }

  /**
   * Executes an update SQL -clause based on given object data and class information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean
   *          isPrimaryKeyUpdate If true, where -clause is created from primary key information.
   * @param String
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @param boolean
   *          useTimestamp If false, it is not checked wheather the data has changed.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, String whereStr, boolean useTimestamp)
      throws SQLException, RockException {
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, null, useTimestamp, whereStr);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else WHERE is constructed
   *          from whereDBParameter
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @return
   * @exception SQLException
   */
  private RockResultSet setSelectSQLPriv(boolean isPrimaryKeySelect, Object whereObj, String whereString,
      String orderByStr) throws SQLException, RockException {
    RockResultSet rockResults = new RockResultSet();
    try {
      String whereSQL = "";

      if (isPrimaryKeySelect)
        whereSQL = createWhereSQLfromPrimaries(whereObj, null);
      else if (whereObj != null)
        whereSQL = createWhereSQL(whereObj, whereObj.getClass());
      else if (whereString != null)
        if (whereString.equals("") == false)
          whereSQL = " WHERE " + whereString;
      if (orderByStr != null) {
        whereSQL = new StringBuffer(whereSQL).append(" ").append(orderByStr).toString();
      }
      Statement sqlSelectStatement = connection.createStatement();
      String sql = createSelectSQL(whereObj, whereObj.getClass(), whereSQL);

      rockResults.setResultSet(sqlSelectStatement.executeQuery(sql), sqlSelectStatement);
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
    return rockResults;
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else WHERE is constructed
   *          from whereDBParameter
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE).
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(String selectString) throws SQLException, RockException {
    RockResultSet rockResults = new RockResultSet();
    try {
      Statement sqlSelectStatement = connection.createStatement();

      rockResults.setResultSet(sqlSelectStatement.executeQuery(selectString), sqlSelectStatement);
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
    return rockResults;
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else WHERE is constructed
   *          from whereDBParameter
   * @param whereDBParameter
   *          all field that are != null are used for the WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, Object whereObj) throws SQLException, RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, whereObj, null, null);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else WHERE is constructed
   *          from whereDBParameter
   * @param whereDBParameter
   *          all field that are != null are used for the WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, Object whereObj, String orderByStr)
      throws SQLException, RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, whereObj, null, orderByStr);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else WHERE is constructed
   *          from whereDBParameter
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the where part, does not
   *          include WHERE). WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, String whereString, String orderByStr)
      throws SQLException, RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, null, whereString, orderByStr);
  }

  /**
   * Gets data from the cursor for a SQL -clause
   * 
   * @param dataObj
   *          holds the data retrieved from the db
   * 
   * @return true while more data is available
   * @exception SQLException
   */
  public Iterator getData(Object dataObj, RockResultSet rockResults) throws SQLException, RockException {
    ResultSet results = rockResults.getResultSet();
    Collection cSql = rockResults.getCollection();
    try {
      while (results.next()) {

        Method cloneMethod = dataObj.getClass().getDeclaredMethod("clone", null);
        Object newObj = cloneMethod.invoke(dataObj, null);

        Class<?> dataClass = newObj.getClass();

        Field[] classFields = dataClass.getDeclaredFields();

        for (int i = 0; i < classFields.length; i++) {

          Field field = classFields[i];

          String fieldType = field.getType().getName();
          String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());
          if (isColumnName(field.getName(), fieldTypeName)) {

            Method setMethod = getFieldMethod(dataClass, field, "set");
            String attributeName = field.getName();

            if (fieldTypeName.equals("Integer")) {
              int resultInt = results.getInt(attributeName);
              Integer tempInteger = null;
              if (results.wasNull() == false) {
                tempInteger = new Integer(resultInt);
              }
              Object[] objList = { tempInteger };
              setMethod.invoke(newObj, objList);
            } else {
              if (fieldTypeName.equals("Float")) {
                float resultFloat = results.getFloat(attributeName);
                Float tempFloat = null;
                if (results.wasNull() == false) {
                  tempFloat = new Float(resultFloat);
                }
                Object[] objList = { tempFloat };
                setMethod.invoke(newObj, objList);
              } else {
                if (fieldTypeName.equals("Double")) {
                  double resultDouble = results.getDouble(attributeName);
                  Double tempDouble = null;
                  if (results.wasNull() == false) {
                    tempDouble = new Double(resultDouble);
                  }
                  Object[] objList = { tempDouble };
                  setMethod.invoke(newObj, objList);
                } else {
                  if (fieldTypeName.equals("Short")) {
                    short resultShort = results.getShort(attributeName);
                    Short tempShort = null;
                    if (results.wasNull() == false) {
                      tempShort = new Short(resultShort);
                    }
                    Object[] objList = { tempShort };
                    setMethod.invoke(newObj, objList);
                  } else {
                    if (fieldTypeName.equals("Long")) {
                      long resultLong = results.getLong(attributeName);
                      Long tempLong = null;
                      if (results.wasNull() == false) {
                        tempLong = new Long(resultLong);
                      }
                      Object[] objList = { tempLong };
                      setMethod.invoke(newObj, objList);
                    } else {
                      Object resultObject = results.getObject(attributeName);
                      Object[] objList = { resultObject };
                      setMethod.invoke(newObj, objList);
                    }
                  }
                }
              }
            }
          }
        }
        cSql.add(newObj);
      }
      return cSql.iterator();
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      
      if (e instanceof InvocationTargetException) {    
        if (((InvocationTargetException)e).getTargetException() instanceof InvalidDataException){
          throw new InvalidDataException(((InvocationTargetException)e).getTargetException().getMessage()); 
        }        
      }    
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * Executes an insert SQL -clause based on String.
   * 
   * @param sqlStr
   *          String to execute
   * @exception SQLException
   */
  public void executeSql(String sqlStr) throws SQLException, RockException {
    try {
      Statement sqlStatement = connection.createStatement();

      sqlStatement.execute(sqlStr);
      sqlStatement.close();
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * Executes a prepared SQL -clause based on String.
   * 
   * @param preparedSqlStr
   *          String to execute
   * @param objVec
   *          vector containig the objects related to the string
   * @exception SQLException
   */
  public void executePreparedSql(String preparedSqlStr, Vector rowVec) throws SQLException, RockException {
    try {
      PreparedStatement sqlStatement = connection.prepareStatement(preparedSqlStr);

      for (int i = 0; i < rowVec.size(); i++) {
        Object[] objs = (Object[]) rowVec.elementAt(i);
        Vector objVec = (Vector) objs[0];
        Vector pkVec = (Vector) objs[1];

        for (int j = 0; j < objVec.size(); j++) {
          Object[] obj = (Object[]) objVec.elementAt(j);
          if (obj[0] != null) {
            sqlStatement.setObject(j + 1, obj[0]);
          } else {
            sqlStatement.setNull(j + 1, ((Integer) obj[1]).intValue());
          }
        }

        for (int j = 0; j < pkVec.size(); j++) {
          Object[] obj = (Object[]) pkVec.elementAt(j);
          if (obj[0] != null) {
            sqlStatement.setObject(objVec.size() + j + 1, obj[0]);
          } else {
            sqlStatement.setNull(objVec.size() + j + 1, ((Integer) obj[1]).intValue());
          }
        }

        sqlStatement.executeUpdate();
      }
      sqlStatement.close();
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }
  
  /**
   * Create prepared statement which can be used in executePreparedSqlQuery 
   * @param preparedSqlStr
   * @return
   * @throws SQLException
   * @throws RockException
   */
  public PreparedStatement createPreparedSqlQuery(final String preparedSqlStr) throws SQLException, RockException {
    return connection.prepareStatement(preparedSqlStr);
  }
  
  /**
   * Close functionality for prepared query 
   * @param preparedSqlStmt
   * @throws SQLException
   * @throws RockException
   */
  public void closePreparedSqlQuery(final PreparedStatement preparedSqlStmt) throws SQLException, RockException {
    preparedSqlStmt.close();
  }
  
  /**
   * Executes a prepared SQL Query-clause based on String.
   * 
   * @param preparedSqlStr
   *          String to execute
   * @param objVec
   *          vector containing the parameters related in the string sql
   * @exception SQLException
   */
  public Vector<Vector<Object>> executePreparedSqlQuery(final PreparedStatement preparedSqlStmt, final Vector<Object> parameters) throws SQLException, RockException {
    final Vector<Vector<Object>> results = new Vector<Vector<Object>>();
    try {
      int ind = 1;
      for (Object parameter : parameters) {
        preparedSqlStmt.setObject(ind, parameter);
        ind++;
      }
      final ResultSet rs = preparedSqlStmt.executeQuery();
      try {
        final ResultSetMetaData rsMetaData = rs.getMetaData();
        final int numberOfColumns = rsMetaData.getColumnCount();          
        while (rs.next()) {
          final Vector<Object> result = new Vector<Object>(numberOfColumns);
          for (int jnd = 1; jnd <= numberOfColumns; jnd++) {
            result.add(rs.getObject(jnd));
          }
          results.add(result);
        }
      } finally {
        rs.close();
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
    return results;
  }

  /**
   * Executes a prepared SQL -clause based on String. If update cannot be done the an insert is
   * exec.
   * 
   * @param preparedSqlStr
   *          String to execute
   * @param objVec
   *          vector containig the objects related to the string
   * @exception SQLException
   */
  public void executePreparedInsAndUpdSql(String preparedUpdStr, Vector rowVec, String preparedInsStr)
      throws SQLException, RockException {
    try {
      PreparedStatement updStatement = connection.prepareStatement(preparedUpdStr);
      PreparedStatement insStatement = connection.prepareStatement(preparedInsStr);

      for (int i = 0; i < rowVec.size(); i++) {
        Object[] objs = (Object[]) rowVec.elementAt(i);
        Vector objVec = (Vector) objs[0];
        Vector pkVec = (Vector) objs[1];

        for (int j = 0; j < objVec.size(); j++) {
          Object[] obj = (Object[]) objVec.elementAt(j);
          if (obj[0] != null) {
            updStatement.setObject(j + 1, obj[0]);
            insStatement.setObject(j + 1, obj[0]);
          } else {
            updStatement.setNull(j + 1, ((Integer) obj[1]).intValue());
            insStatement.setNull(j + 1, ((Integer) obj[1]).intValue());
          }
        }

        for (int j = 0; j < pkVec.size(); j++) {
          Object[] obj = (Object[]) pkVec.elementAt(j);
          if (obj[0] != null) {
            updStatement.setObject(objVec.size() + j + 1, obj[0]);
          } else {
            updStatement.setNull(objVec.size() + j + 1, ((Integer) obj[1]).intValue());
          }
        }

        int rows = updStatement.executeUpdate();

        if (rows <= 0) {

          rows = insStatement.executeUpdate();

        }

      }
      updStatement.close();
      insStatement.close();
    } catch (SQLException sqlE) {
      throw sqlE;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * commit Commits the transaction if autocommit is OFF.
   * 
   * @exception SQLException
   */
  public void commit() throws SQLException {
    if (!autoCommit) {

      connection.commit();
    }
  }

  /**
   * rollback Rollbacks the transaction if autocommit is OFF.
   * 
   * @exception SQLException
   */
  public void rollback() throws SQLException {
    if (!autoCommit) {

      connection.rollback();
    }
  }

  /**
   * begin Begins a transaction (ended by commit/rollback ) DOES NOTHING IN THIS CONTEXT (just to
   * implement the interface needs
   * 
   */
  public void begin() {
  }

  /**
   * Returns the database connect element
   * 
   * 
   * @return The connect element
   */
  public Connection getConnection() {
    return this.connection;
  }

  /**
   * Copies tables child table information
   * 
   * @param tableName
   * @param setValues
   *          key <setMethodName> value {oldValue,newValue}
   */
  public void copySchema(String tableName, Hashtable setValues, String packagePath) throws SQLException, RockException {
    CopySchema copySchema = new CopySchema(this, "", // catalog
        this.strUserName, setValues, packagePath);

    copySchema.copy(tableName);

    copySchema.close();
  }

  /**
   * Copies tables child table information
   * 
   * @param tableName
   * @param setValues
   *          key <setMethodName> value {oldValue,newValue}
   */
  public void copySchemaNonRecursive(String[] tableNames, Hashtable setValues, String packagePath) throws SQLException,
      RockException {

    CopySchema copySchema = new CopySchema(this, "", // catalog
        this.strUserName, setValues, packagePath);

    for (int i = 0; i < tableNames.length; i++) {

      copySchema.copyNonRecursive(tableNames[i]);

    }

    copySchema.close();

  }

}
