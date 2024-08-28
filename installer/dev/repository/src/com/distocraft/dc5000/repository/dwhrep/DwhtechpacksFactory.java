
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



public class DwhtechpacksFactory implements Cloneable {
  private Vector<Dwhtechpacks> vec;

  private RockFactory rockFact;

  private Dwhtechpacks whereObject;

  public DwhtechpacksFactory(RockFactory rockFact, Dwhtechpacks whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Dwhtechpacks>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Dwhtechpacks> it = rockFact.getData(whereObject, results);
    Dwhtechpacks o = new Dwhtechpacks(rockFact);

    while (it.hasNext()) {
      o = (Dwhtechpacks) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public DwhtechpacksFactory(RockFactory rockFact, Dwhtechpacks whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Dwhtechpacks>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Dwhtechpacks> it = rockFact.getData(whereObject, results);
    Dwhtechpacks o = new Dwhtechpacks(rockFact, validate);

    while (it.hasNext()) {
      o = (Dwhtechpacks) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public DwhtechpacksFactory(RockFactory rockFact, Dwhtechpacks whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Dwhtechpacks>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Dwhtechpacks> it = rockFact.getData(whereObject, results);
    Dwhtechpacks o = new Dwhtechpacks(rockFact);
    while (it.hasNext()) {
      o = (Dwhtechpacks) it.next();
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
  public Dwhtechpacks getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Dwhtechpacks) this.vec.elementAt(i);
    }
    return (Dwhtechpacks) null;
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
  public Vector<Dwhtechpacks> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Dwhtechpacks> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Dwhtechpacks o = (Dwhtechpacks) this.vec.elementAt(i);
      Dwhtechpacks otherO = (Dwhtechpacks) otherVector.elementAt(i);
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
