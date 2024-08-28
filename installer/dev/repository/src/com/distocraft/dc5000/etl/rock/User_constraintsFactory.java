/**
 * ETL Repository access library. <br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved. <br>
 * 
 * @author melantie
 */
package com.distocraft.dc5000.etl.rock;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;



public class User_constraintsFactory implements Cloneable {

  private Vector vec;
  private RockFactory rockFact;

  /**
   * Constructor to initialize all objects to null
   */
  public User_constraintsFactory(RockFactory rockFact, User_constraints whereObject) throws SQLException, RockException {
    vec = new Vector();
    RockResultSet results = rockFact.setSelectSQL(false, whereObject);
    Iterator it = rockFact.getData(whereObject, results);
    User_constraints o = new User_constraints(rockFact);
    while (it.hasNext()) {
      o = (User_constraints) it.next();
      this.vec.addElement(o);
    }
    results.close();
  }

  /**
   * Constructor to initialize all objects to null
   */
  public User_constraintsFactory(RockFactory rockFact, User_constraints whereObject, String orderByClause)
      throws SQLException, RockException {
    vec = new Vector();
    RockResultSet results = rockFact.setSelectSQL(false, whereObject, orderByClause);
    Iterator it = rockFact.getData(whereObject, results);
    User_constraints o = new User_constraints(rockFact);
    while (it.hasNext()) {
      o = (User_constraints) it.next();
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
  public User_constraints getElementAt(int i) {
    if (i < this.vec.size()) {
      return (User_constraints) this.vec.elementAt(i);
    }
    return (User_constraints) null;
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
   * to enable a public clone method inherited from Object class (private
   * method)
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