
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



public class PartitionplanFactory implements Cloneable {
  private Vector<Partitionplan> vec;

  private RockFactory rockFact;

  private Partitionplan whereObject;

  public PartitionplanFactory(RockFactory rockFact, Partitionplan whereObject) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Partitionplan>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Partitionplan> it = rockFact.getData(whereObject, results);
    Partitionplan o = new Partitionplan(rockFact);

    while (it.hasNext()) {
      o = (Partitionplan) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setOriginal(o);
      this.vec.addElement(o);
    }
    results.close();
  }

  public PartitionplanFactory(RockFactory rockFact, Partitionplan whereObject, boolean validate) throws SQLException,
      RockException {
    this.whereObject = whereObject;

    this.vec = new Vector<Partitionplan>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator<Partitionplan> it = rockFact.getData(whereObject, results);
    Partitionplan o = new Partitionplan(rockFact, validate);

    while (it.hasNext()) {
      o = (Partitionplan) it.next();
      o.setModifiedColumns(new HashSet());
      o.setNewItem(false);
      o.setValidateData(validate);
      o.setOriginal(o);  
      this.vec.addElement(o);
    }
    results.close();
  }

  public PartitionplanFactory(RockFactory rockFact, Partitionplan whereObject, String orderByClause)
      throws SQLException, RockException {
    this.whereObject = whereObject;
    this.vec = new Vector<Partitionplan>();
    this.rockFact = rockFact;
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator<Partitionplan> it = rockFact.getData(whereObject, results);
    Partitionplan o = new Partitionplan(rockFact);
    while (it.hasNext()) {
      o = (Partitionplan) it.next();
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
  public Partitionplan getElementAt(int i) {
    if (i < this.vec.size()) {
      return (Partitionplan) this.vec.elementAt(i);
    }
    return (Partitionplan) null;
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
  public Vector<Partitionplan> get() {
    return vec;
  }

  /**
   * equals method test wheather the objects field values and and the parametrs objects field values
   * are equal.
   */
  public boolean equals(Vector<Partitionplan> otherVector) {
    if (this.vec == otherVector)
      return true;
    if ((this.vec == null) || (otherVector == null))
      return false;
    if (this.vec.size() != otherVector.size())
      return false;
    for (int i = 0; i < this.vec.size(); i++) {
      Partitionplan o = (Partitionplan) this.vec.elementAt(i);
      Partitionplan otherO = (Partitionplan) otherVector.elementAt(i);
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
