/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package com.distocraft.dc5000.etl.rock;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;


import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;
import sun.security.util.Debug;

public class All_tablesFactory implements Cloneable {
  private Vector vec;

  private RockFactory rockFact;

  private All_tables whereObject;

  /**
   * Constructor to initialize all objects to null
   */
  public All_tablesFactory(RockFactory rockFact, All_tables whereObject, String dbLinkName) throws SQLException,
      RockException {
    this.whereObject = whereObject;
    this.whereObject.setDbLinkName(dbLinkName);
    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator it = rockFact.getData(whereObject, results);
    All_tables o = new All_tables(rockFact, dbLinkName);
    o.setDbLinkName(dbLinkName);
    while (it.hasNext()) {
      o = (All_tables) it.next();
      this.vec.addElement(o);
    }
    results.close();
  }

  /**
   * Constructor to initialize all objects to null
   */
  public All_tablesFactory(RockFactory rockFact, All_tables whereObject, String orderByClause, String dbLinkName)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.whereObject.setDbLinkName(dbLinkName);
    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator it = rockFact.getData(whereObject, results);
    All_tables o = new All_tables(rockFact, dbLinkName);
    o.setDbLinkName(dbLinkName);
    while (it.hasNext()) {
      o = (All_tables) it.next();
      this.vec.addElement(o);
    }
    results.close();
  }

  /**
   * Get an element from the vector
   * 
   * @param i
   *          the element indicator
   */
  public All_tables getElementAt(int i) {
    if (i < this.vec.size()) {
      return (All_tables) this.vec.elementAt(i);
    }
    return (All_tables) null;
  }

  /**
   * The size of the RockFactory vector
   */
  public int size() {
    return this.vec.size();
  }

  /**
   * The generated GET METHODS
   */
  public Vector get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      All_tables o = (All_tables) this.vec.elementAt(i);
      All_tables otherO = (All_tables) otherVector.elementAt(i);
      if (o.equals(otherO) == false)
        return false;
    }
    return true;
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
  
}
