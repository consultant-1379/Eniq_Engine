/*
 * Created on Mar 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.distocraft.dc5000.etl.engine.system.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.distocraft.dc5000.etl.engine.system.SetContextTriggerAction;
import junit.framework.TestCase;

/**
 * @author savinen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestSetContextTriggerAction extends TestCase {


	public TestSetContextTriggerAction(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void testContains() throws Exception{

		
		HashSet hashSet = new HashSet();
		hashSet.add("c1");
		hashSet.add("c2");
		hashSet.add("c3");
		hashSet.add("c4");
		hashSet.add("c5");
		hashSet.add("c6");
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		assertEquals(false,scta.contains(hashSet,null));
		assertEquals(false,scta.contains(hashSet,""));
		assertEquals(true,scta.contains(hashSet,"c1,c2"));
		assertEquals(false,scta.contains(hashSet,"c1,c12"));		
	}
	
	public void testIsEquals() throws Exception{
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		
		Integer i = new Integer(1);
		assertEquals(true,scta.isEqual(i,"1"));
		assertEquals(false,scta.isEqual(i,"0"));
		assertEquals(false,scta.isEqual(i,""));
		assertEquals(false,scta.isEqual(i,null));
		
		String s = "test";
		assertEquals(true,scta.isEqual(s,"test"));
		assertEquals(false,scta.isEqual(s,"tst"));
		assertEquals(false,scta.isEqual(s,""));
		assertEquals(false,scta.isEqual(s,null));
		
		Boolean bt = new Boolean(true);
		assertEquals(true,scta.isEqual(bt,"true"));
		assertEquals(false,scta.isEqual(bt,"false"));
		assertEquals(false,scta.isEqual(bt,"tre"));
		assertEquals(false,scta.isEqual(bt,""));
		assertEquals(false,scta.isEqual(bt,null));
		
		Boolean bf = new Boolean(false);
		assertEquals(false,scta.isEqual(bf,"true"));
		assertEquals(true,scta.isEqual(bf,"false"));
		assertEquals(false,scta.isEqual(bf,"tre"));
		assertEquals(false,scta.isEqual(bf,""));
		assertEquals(false,scta.isEqual(bf,null));
		
		
	}
	
	public void testIsLess() throws Exception{
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		
		Integer i = new Integer(1);
		assertEquals(false,scta.isLess(i,"1"));
		assertEquals(true,scta.isLess(i,"0"));
		assertEquals(false,scta.isLess(i,"2"));
		assertEquals(false,scta.isLess(i,""));
		assertEquals(false,scta.isLess(i,null));
				
	}

	public void testIsMore() throws Exception{
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		
		Integer i = new Integer(1);
		assertEquals(false,scta.isMore(i,"1"));
		assertEquals(false,scta.isMore(i,"0"));
		assertEquals(true,scta.isMore(i,"2"));
		assertEquals(false,scta.isMore(i,""));
		assertEquals(false,scta.isMore(i,null));
				
	}
	
	
	public void testStrToList(){
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		
		String s1 = "";
		String s2 = "c1";
		String s3 = "c1,c2,c3";
		String s4 = null;
		
		List l1 = new ArrayList();
		List l2 = new ArrayList();
		l2.add("c1");
		List l3 = new ArrayList();
		l3.add("c1");
		l3.add("c2");
		l3.add("c3");
		List l4 = new ArrayList();

		
		assertEquals(l1,scta.strToList(s1));
		assertEquals(l2,scta.strToList(s2));
		assertEquals(l3,scta.strToList(s3));
		assertEquals(l4,scta.strToList(s4));
		
	}
	
	
	private boolean isEqual(List l1, List l2){
		
		
		if (l1 == null && l2 == null) return true;
		
		if (l1 == null || l2 == null) return false;
		
		if (l1.size() != l2.size()) return false;
		
		Iterator iter = l1.iterator();
		while (iter.hasNext()){
			if (!l2.contains(iter.next())) return false;
		}

		return true;
		
	}
	
	public void testSetToList(){
		
		SetContextTriggerAction scta = new SetContextTriggerAction();
		
		Set s1 = new HashSet();
		Set s2 = new HashSet();
		s2.add("c1");
		Set s3 = new HashSet();
		s3.add("c1");
		s3.add("c2");
		s3.add("c3");
		Set s4 = null;
		
		List l1 = new ArrayList();
		List l2 = new ArrayList();
		l2.add("c1");
		List l3 = new ArrayList();
		l3.add("c1");
		l3.add("c2");
		l3.add("c3");
		List l4 = new ArrayList();

		
		assertEquals(true,isEqual(l1,scta.setToList(s1)));
		assertEquals(true,isEqual(l2,scta.setToList(s2)));
		assertEquals(true,isEqual(l3,scta.setToList(s3)));
		assertEquals(true,isEqual(l4,scta.setToList(s4)));
		
	}

	
	public void testTrigger() throws Exception{
		

		List l1 = new ArrayList();
		List l2 = new ArrayList();
		l2.add("c1");
		l2.add("c2");
		l2.add("c3");
		List l3 = null;
		
		
		
		SetContextTriggerActionWrapper scta = new SetContextTriggerActionWrapper();
		
		scta.triggerCounter = 0;
		scta.trigger(l2,"");
		assertEquals(3,scta.triggerCounter);
		
		scta.triggerCounter = 0;
		scta.trigger(l1,"");
		assertEquals(0,scta.triggerCounter);
		
		scta.triggerCounter = 0;
		scta.trigger(l3,"");
		assertEquals(0,scta.triggerCounter);

		
	}
}
