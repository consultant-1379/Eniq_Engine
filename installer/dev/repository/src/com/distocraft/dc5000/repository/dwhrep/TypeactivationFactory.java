
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



public class TypeactivationFactory implements Cloneable {
  private Vector<Typeactivation> vec;

  private RockFactory rockFact;

  private Typeactivation whereObject;

  public TypeactivationFactory(RockFactory rockFact, Typeactivation whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Typeactivation>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Typeactivation> it = rockFact.getData(whereObject, results);
    Typeactivation o = new Typeactivation(rockFact);

    while (it.hasNext()) {
      o = (Typeactivation) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public TypeactivationFactory(RockFactory rockFact, Typeactivation whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Typeactivation>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Typeactivation> it = rockFact.getData(whereObject, results);
    Typeactivation o = new Typeactivation(rockFact, validate);

    while (it.hasNext()) {
      o = (Typeactivation) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public TypeactivationFactory(RockFactory rockFact, Typeactivation whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Typeactivation>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Typeactivation> it = rockFact.getData(whereObject, results);
    Typeactivation o = new Typeactivation(rockFact);
    while (it.hasNext()) {
      o = (Typeactivation) it.next();
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
  public Typeactivation getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Typeactivation) this.vec.elementAt(i);
    }
    return (Typeactivation) null;
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
  public Vector<Typeactivation> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Typeactivation> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Typeactivation o = (Typeactivation) this.vec.elementAt(i);
      Typeactivation otherO = (Typeactivation) otherVector.elementAt(i);
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
