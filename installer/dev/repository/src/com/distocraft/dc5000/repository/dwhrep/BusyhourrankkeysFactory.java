
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



public class BusyhourrankkeysFactory implements Cloneable {
  private Vector<Busyhourrankkeys> vec;

  private RockFactory rockFact;

  private Busyhourrankkeys whereObject;

  public BusyhourrankkeysFactory(RockFactory rockFact, Busyhourrankkeys whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Busyhourrankkeys>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Busyhourrankkeys> it = rockFact.getData(whereObject, results);
    Busyhourrankkeys o = new Busyhourrankkeys(rockFact);

    while (it.hasNext()) {
      o = (Busyhourrankkeys) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public BusyhourrankkeysFactory(RockFactory rockFact, Busyhourrankkeys whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Busyhourrankkeys>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Busyhourrankkeys> it = rockFact.getData(whereObject, results);
    Busyhourrankkeys o = new Busyhourrankkeys(rockFact, validate);

    while (it.hasNext()) {
      o = (Busyhourrankkeys) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public BusyhourrankkeysFactory(RockFactory rockFact, Busyhourrankkeys whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Busyhourrankkeys>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Busyhourrankkeys> it = rockFact.getData(whereObject, results);
    Busyhourrankkeys o = new Busyhourrankkeys(rockFact);
    while (it.hasNext()) {
      o = (Busyhourrankkeys) it.next();
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
  public Busyhourrankkeys getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Busyhourrankkeys) this.vec.elementAt(i);
    }
    return (Busyhourrankkeys) null;
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
  public Vector<Busyhourrankkeys> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Busyhourrankkeys> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Busyhourrankkeys o = (Busyhourrankkeys) this.vec.elementAt(i);
      Busyhourrankkeys otherO = (Busyhourrankkeys) otherVector.elementAt(i);
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
