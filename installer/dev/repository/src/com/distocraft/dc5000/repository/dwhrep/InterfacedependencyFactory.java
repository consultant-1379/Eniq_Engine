
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



public class InterfacedependencyFactory implements Cloneable {
  private Vector<Interfacedependency> vec;

  private RockFactory rockFact;

  private Interfacedependency whereObject;

  public InterfacedependencyFactory(RockFactory rockFact, Interfacedependency whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Interfacedependency>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Interfacedependency> it = rockFact.getData(whereObject, results);
    Interfacedependency o = new Interfacedependency(rockFact);

    while (it.hasNext()) {
      o = (Interfacedependency) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public InterfacedependencyFactory(RockFactory rockFact, Interfacedependency whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Interfacedependency>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Interfacedependency> it = rockFact.getData(whereObject, results);
    Interfacedependency o = new Interfacedependency(rockFact, validate);

    while (it.hasNext()) {
      o = (Interfacedependency) it.next();
      o.setModifiedColumns(new HashSet<String>());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public InterfacedependencyFactory(RockFactory rockFact, Interfacedependency whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Interfacedependency>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Interfacedependency> it = rockFact.getData(whereObject, results);
    Interfacedependency o = new Interfacedependency(rockFact);
    while (it.hasNext()) {
      o = (Interfacedependency) it.next();
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
  public Interfacedependency getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Interfacedependency) this.vec.elementAt(i);
    }
    return (Interfacedependency) null;
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
  public Vector<Interfacedependency> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Interfacedependency> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Interfacedependency o = (Interfacedependency) this.vec.elementAt(i);
      Interfacedependency otherO = (Interfacedependency) otherVector.elementAt(i);
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
