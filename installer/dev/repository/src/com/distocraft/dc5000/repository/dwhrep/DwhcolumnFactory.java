
package com.distocraft.dc5000.repository.dwhrep;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashSet;
import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;



public class DwhcolumnFactory implements Cloneable {
  private Vector<Dwhcolumn> vec;

  private RockFactory rockFact;

  private Dwhcolumn whereObject;

  public DwhcolumnFactory(RockFactory rockFact, Dwhcolumn whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Dwhcolumn>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Dwhcolumn> it = rockFact.getData(whereObject, results);
    Dwhcolumn o = new Dwhcolumn(rockFact);

    while (it.hasNext()) {
      o = (Dwhcolumn) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public DwhcolumnFactory(RockFactory rockFact, Dwhcolumn whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Dwhcolumn>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Dwhcolumn> it = rockFact.getData(whereObject, results);
    Dwhcolumn o = new Dwhcolumn(rockFact, validate);

    while (it.hasNext()) {
      o = (Dwhcolumn) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public DwhcolumnFactory(RockFactory rockFact, Dwhcolumn whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Dwhcolumn>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Dwhcolumn> it = rockFact.getData(whereObject, results);
    Dwhcolumn o = new Dwhcolumn(rockFact);
    while (it.hasNext()) {
      o = (Dwhcolumn) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setOriginal(o);
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
  public Dwhcolumn getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Dwhcolumn) this.vec.elementAt(i);
    }
    return (Dwhcolumn) null;
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
  public Vector<Dwhcolumn> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Dwhcolumn> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Dwhcolumn o = (Dwhcolumn) this.vec.elementAt(i);
      Dwhcolumn otherO = (Dwhcolumn) otherVector.elementAt(i);
      if (o.equals(otherO) == false)
        return false;
    }
    return true;
  }

  /**
   * Delete object contents from database
   * 
   * @exception SQLException
   */
  public int deleteDB() throws SQLException, RockException {
    return this.rockFact.deleteData(false, this.whereObject);
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
