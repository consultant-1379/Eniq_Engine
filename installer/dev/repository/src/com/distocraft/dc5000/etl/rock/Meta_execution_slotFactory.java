
package com.distocraft.dc5000.etl.rock;

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



public class Meta_execution_slotFactory implements Cloneable {
  private Vector vec;

  private RockFactory rockFact;

  private Meta_execution_slot whereObject;

  public Meta_execution_slotFactory(RockFactory rockFact, Meta_execution_slot whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator it = rockFact.getData(whereObject, results);
    Meta_execution_slot o = new Meta_execution_slot(rockFact);

    while (it.hasNext()) {
      o = (Meta_execution_slot) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      this.vec.addElement(o);
    }
    results.close();
  }

  public Meta_execution_slotFactory(RockFactory rockFact, Meta_execution_slot whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator it = rockFact.getData(whereObject, results);
    Meta_execution_slot o = new Meta_execution_slot(rockFact, validate);

    while (it.hasNext()) {
      o = (Meta_execution_slot) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setValidateData(validate);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public Meta_execution_slotFactory(RockFactory rockFact, Meta_execution_slot whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator it = rockFact.getData(whereObject, results);
    Meta_execution_slot o = new Meta_execution_slot(rockFact);
    while (it.hasNext()) {
      o = (Meta_execution_slot) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
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
  public Meta_execution_slot getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Meta_execution_slot) this.vec.elementAt(i);
    }
    return (Meta_execution_slot) null;
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
      Meta_execution_slotFactory o = (Meta_execution_slotFactory) this.vec.elementAt(i);
      Meta_execution_slotFactory otherO = (Meta_execution_slotFactory) otherVector.elementAt(i);
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
