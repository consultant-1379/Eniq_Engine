package ssc.rockfactory;

import java.sql.SQLException;
import java.util.Set;


public interface RockDBObject {

  public Set<String> gimmeModifiedColumns();
  public void cleanModifiedColumns();
  public int insertDB() throws SQLException, RockException;
  public int deleteDB() throws SQLException, RockException;
  public int updateDB() throws SQLException, RockException;
  public Object clone ();
}
