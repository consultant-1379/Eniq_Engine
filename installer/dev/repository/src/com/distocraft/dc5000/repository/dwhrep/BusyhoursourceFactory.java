
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



public class BusyhoursourceFactory implements Cloneable {
  private Vector<Busyhoursource> vec;

  private RockFactory rockFact;

  private Busyhoursource whereObject;

  public BusyhoursourceFactory(RockFactory rockFact, Busyhoursource whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Busyhoursource>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Busyhoursource> it = rockFact.getData(whereObject, results);
    Busyhoursource o = new Busyhoursource(rockFact);

    while (it.hasNext()) {
      o = (Busyhoursource) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public BusyhoursourceFactory(RockFactory rockFact, Busyhoursource whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Busyhoursource>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Busyhoursource> it = rockFact.getData(whereObject, results);
    Busyhoursource o = new Busyhoursource(rockFact, validate);

    while (it.hasNext()) {
      o = (Busyhoursource) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public BusyhoursourceFactory(RockFactory rockFact, Busyhoursource whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Busyhoursource>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Busyhoursource> it = rockFact.getData(whereObject, results);
    Busyhoursource o = new Busyhoursource(rockFact);
    while (it.hasNext()) {
      o = (Busyhoursource) it.next();
      o.setModifiedColumns(new HashSet<String>());
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
  public Busyhoursource getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Busyhoursource) this.vec.elementAt(i);
    }
    return (Busyhoursource) null;
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
  public Vector<Busyhoursource> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Busyhoursource> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Busyhoursource o = (Busyhoursource) this.vec.elementAt(i);
      Busyhoursource otherO = (Busyhoursource) otherVector.elementAt(i);
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
