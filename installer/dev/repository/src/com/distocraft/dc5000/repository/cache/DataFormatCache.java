package com.distocraft.dc5000.repository.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;

/**
 * Parser and loader specific cache implementation for accessing DataFormats
 * 
 * @author lemminkainen
 * 
 */
public class DataFormatCache {

  private static Logger log = Logger.getLogger("etlengine.repository.DataFormatCache");

	private static String defaultSchema = "dwhrep.";
	static {
		defaultSchema = System.getProperty(defaultSchema, defaultSchema);
	}

  // HM33299 etogust
  // Take only dataformats from active techpacks in order to guarantee that 
  // dataformats/dataitems can only be used from active TPs
  private static final String GET_INTERFACES = "select di.interfacename, im.tagid, im.dataformatid, df.foldername, im.transformerid"
      + " from datainterface di, interfacemeasurement im, dataformat df"
      + " where di.interfacename = im.interfacename and im.dataformatid = df.dataformatid"
      + " and di.status = 1 and im.status = 1"
      + " and df.versionid in (select versionid from dwhrep.tpactivation where status = 'ACTIVE')"
      + " ORDER BY im.dataformatid";

  private static final String GET_ITEMS = " SELECT di.dataname, di.colnumber, di.dataid, di.process_instruction, di.dataformatid, di.datatype, di.datasize, di.datascale,"
	  									+ " COALESCE("
	  									+ " (SELECT 1 FROM "+defaultSchema+"MeasurementCounter mc WHERE di.dataname = mc.dataname AND df.typeid = mc.typeid),"
	  									+ " (SELECT 1 FROM "+defaultSchema+"ReferenceColumn rc WHERE di.dataname = rc.dataname AND df.typeid = rc.typeid AND uniquekey = 0),"
	  									+ " 0) AS is_counter "  
	  									+ " FROM "+defaultSchema+"dataformat df "
	  									+ " JOIN "+defaultSchema+"dataitem di ON df.dataformatid = di.dataformatid"
	  									+ " WHERE df.versionid in (select versionid from "+defaultSchema+"tpactivation where status = 'ACTIVE')";
  
  
  private String dburl = null;

  private String dbusr = null;

  private String dbpwd = null;

  private Map it_map = null;

  private Map id_map = null;

  private Set if_names = null;

  private HashMap folder_map = null;

  private static DataFormatCache dfc = null;

  private DataFormatCache() {
  }

  public static void initialize(RockFactory rock, String dburl, String dbusr, String dbpwd) {

    dfc = new DataFormatCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwhrep");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      dfc.dburl = dburl;
      dfc.dbusr = dbusr;
      dfc.dbpwd = dbpwd;

      log.config("Repository: " + dfc.dburl);

      dfc.revalidate();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  
  public static void initialize(RockFactory rock) {

    dfc = new DataFormatCache();

    try {

      log.fine("Initializing...");

      Meta_databases selO = new Meta_databases(rock);
      selO.setConnection_name("dwhrep");
      selO.setType_name("USER");

      Meta_databasesFactory mdbf = new Meta_databasesFactory(rock, selO);

      Vector dbs = mdbf.get();

      if (dbs == null || dbs.size() != 1) {
        log.severe("dwhrep database not correctly defined in etlrep.Meta_databases.");
      }

      Meta_databases repdb = (Meta_databases) dbs.get(0);

      Class.forName(repdb.getDriver_name());

      dfc.dburl = repdb.getConnection_string();
      dfc.dbusr = repdb.getUsername();
      dfc.dbpwd = repdb.getPassword();

      log.config("Repository: " + dfc.dburl);

      dfc.revalidate();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Fatal initialization error", e);
    }

  }

  public static DataFormatCache getCache() {
    return dfc;
  }

  public DFormat getFormatWithTagID(String interfaceName, String tagID) {
    return (DFormat) it_map.get(interfaceName + "_" + tagID);
  }

  public List getFormatWithFormatID(String interfaceName, String dataFormatID) {
    return (List) id_map.get(interfaceName + "_" + dataFormatID);
  }

  public Vector getInterfaceNames() {
    Vector v = new Vector();
    v.addAll(if_names);
    Collections.sort(v);

    return v;
  }
  
  /**
   * This method returns the data format object (DFormat) of the measurement type who's load file would be put into directory folderName
   * 
   * @param folderName	String with directory name of a load file (not the path).
   * @return	The DFormat that maps to folderName
   */
  public DFormat getFormatWithFolderName(String folderName){
	  return (DFormat) folder_map.get(folderName);
  }

  public boolean isAnInterface(String ifname) {
    return if_names.contains(ifname);
  }

  public void revalidate() throws Exception {

    log.fine("Revalidating...");

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {

      con = DriverManager.getConnection(dburl, dbusr, dbpwd);

      ps = con.prepareStatement(GET_INTERFACES);

      rs = ps.executeQuery();

      HashSet if_names2 = new HashSet();
      HashMap it_map2 = new HashMap();
      HashMap id_map2 = new HashMap();
      HashMap folder_map2 = new HashMap();

      while (rs.next()) {
        DFormat df = new DFormat(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));

        if_names2.add(df.getInterfaceName());

        it_map2.put(df.getInterfaceName() + "_" + df.getTagID(), df);

        List dfs = (List) id_map2.get(df.getInterfaceName() + "_" + df.getDataFormatID());
        if (dfs == null) {
          dfs = new ArrayList();
          id_map2.put(df.getInterfaceName() + "_" + df.getDataFormatID(), dfs);
        }

        dfs.add(df);
        
        folder_map2.put(df.getFolderName(), df);

      }

      rs.close();
      rs = null;
      ps.close();
      ps = null;

      ps = con.prepareStatement(GET_ITEMS);
      rs = ps.executeQuery();
      
      //TR HL94231 - EEIKBE. 
      //comment out rs.next() as it's reading the first result, 
      //then in while loop, it's discarded. Therefore the first 
      //result from query never gets put into cache.
      //rs.next();
      
      HashMap<String, List<DItem>> map = new HashMap<String, List<DItem>>();
      while (rs.next()) {
        List<DItem> list = map.get(rs.getString(5));
        if (list == null)
          list = new ArrayList<DItem>();

        DItem di = new DItem(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getInt(9));
        list.add(di);
        map.put(rs.getString(5), list);
      }

      Iterator it = it_map2.values().iterator();
      while (it.hasNext()) {

        DFormat df = (DFormat) it.next();

        List list = (List)map.get(df.getDataFormatID());
        if (list != null){
          Collections.sort(list);
        }
        df.setItems(list);
      }

      if_names = if_names2;
      it_map = it_map2;
      id_map = id_map2;
      folder_map = folder_map2;
      
      log.info("Revalidation succesfully performed. " + if_names.size() + " formats found");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

      if (con != null) {
        try {
          con.close();
        } catch (Exception e) {
          log.log(Level.WARNING, "Cleanup error", e);
        }
      }

    }
  }
}
