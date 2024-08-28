/**
 * Collection of common functions in Tech Pack IDE
 */
package com.ericsson.eniq.common;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

/**
 * @author eheijun
 * 
 */
public class Utils {

  private Utils() {
  }

  public static Integer replaceNull(final Integer value) {
    if (value == null) {
      return Integer.valueOf(0);
    }
    return value;
  }

  public static String replaceNull(final String value) {
    if (value == null) {
      return "";
    }
    return value;
  }

  public static Double replaceNull(final Double value) {
    if (value == null) {
      return new Double(0);
    }
    return value;
  }

  public static Long replaceNull(final Long value) {
    if (value == null) {
      return Long.valueOf(0);
    }
    return value;
  }

  /**
   * Convince method to check if a String is empty, because checking if a string
   * is null or empty is a common occurrence. A String is considered empty if it
   * is null or "" or just whitespace, e.g. "  ".
   * 
   * @author eeoidiv 20090707
   * @param str
   * @return
   */
  public static boolean isEmpty(String str) {
    if ((str == null) || (str.trim().length() == 0)) {
      return true;
    }
    return false;
  }

  public static Integer stringToInteger(final String value) {
    if ((value != null) && (!value.trim().equals(""))) {
      return new Integer(value);
    }
    return Integer.valueOf(0);
  }

  public static Long stringToLong(final String value) {
    if ((value != null) && (!value.trim().equals(""))) {
      return new Long(value);
    }
    return Long.valueOf(0);
  }

  public static Integer booleanToInteger(final Boolean value) {
    if ((value != null) && (value.booleanValue())) {
      return Integer.valueOf(1);
    }
    return Integer.valueOf(0);
  }

  public static Boolean integerToBoolean(final Integer value) {
    if ((value != null) && (value.intValue() == 1)) {
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(false);
  }

  public static int getSmaller(final int v1, final int v2) {
    if (v1 > v2) {
      return v2;
    }
    return v1;
  }

  public static int getLarger(final int v1, final int v2) {
    if (v1 < v2) {
      return v2;
    }
    return v1;
  }

  public static String removeFromStart(String text, final String removeText) {
    if (text.startsWith(removeText)) {
      text = text.substring(removeText.length());
    }
    return text;
  }

  public static String decodeTypeclassid(String value, final String versionid, final String techpackname) {
    value = Utils.removeFromStart(value, versionid);
    value = Utils.removeFromStart(value, Constants.TYPESEPARATOR);
    value = Utils.removeFromStart(value, techpackname);
    value = Utils.removeFromStart(value, Constants.TYPENAMESEPARATOR);
    return value;
  }

  public static String encodeTypeclassid(final String value, final String versionid, final String techpackname) {
    final String typeclassid = versionid + Constants.TYPESEPARATOR + techpackname + Constants.TYPENAMESEPARATOR + value;
    return typeclassid;
  }

  public static String encodeTypeid(final String value, final String versionid) {
    final String typeid = versionid + Constants.TYPESEPARATOR + value;
    return typeid;
  }

  public static Properties createProperty(final String str) throws Exception {

    final Properties prop = new Properties();

    final StringTokenizer st = new StringTokenizer(str, "\n");
    while (st.hasMoreTokens()) {
      final String tmp = st.nextToken();
      final String[] split = tmp.split("=");
      if (split[0] != null) {
        final String key = split[0];
        String value = "";
        if (split.length > 1) {
          value = split[1];
        }
        prop.setProperty(key.trim(), value.trim());
      }

    }

    return prop;

  }

  public static Properties stringToProperty(final String str) throws Exception {

    final Properties prop = new Properties();

    if (str != null && str.length() > 0) {
      final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
      prop.load(bais);
      bais.close();
    }

    return prop;

  }

  public static String propertyToString(final Properties prop) throws Exception {

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    prop.store(baos, "");

    return baos.toString();
  }

  public static long getScheduleMaxID(final RockFactory rockFact) throws Exception {

    Long m = 0L;
    final Statement s = rockFact.getConnection().createStatement();
    try {
      final ResultSet r = s.executeQuery("select max(id) maxval from META_SCHEDULINGS");
      try {
        r.next();
        m = r.getLong("maxval");
      } finally {
        r.close();
      }
    } finally {
      s.close();
    }
    return m;

  }

  public static long getActionMaxID(final RockFactory rockFact) throws Exception {

    Long m = 0L;
    final Statement s = rockFact.getConnection().createStatement();
    try {
      final ResultSet r = s.executeQuery("select max(TRANSFER_ACTION_ID) maxval from META_TRANSFER_ACTIONS");
      try {
        r.next();
        m = r.getLong("maxval");
      } finally {
        r.close();
      }
    } finally {
      s.close();
    }
    return m;

  }

  public static long getSetMaxID(final RockFactory rockFact) throws Exception {

    Long m = 0L;
    final Statement s = rockFact.getConnection().createStatement();
    try {
      final ResultSet r = s.executeQuery("select max(COLLECTION_ID) maxval from META_COLLECTIONS");
      try {
        r.next();
        m = r.getLong("maxval");
      } finally {
        r.close();
      }
    } finally {
      s.close();
    }
    return m;

  }

  public static long getTPMaxID(final RockFactory rockFact) throws Exception {

    Long m = 0L;
    final Statement s = rockFact.getConnection().createStatement();
    try {
      final ResultSet r = s.executeQuery("select max(COLLECTION_SET_ID) maxval from META_COLLECTION_SETS");
      try {
        r.next();
        m = r.getLong("maxval");
      } finally {
        r.close();
      }
    } finally {
      s.close();
    }
    return m;

  }

  /*
   * public static long getActionMaxID(RockFactory rockFact) throws Exception {
   * 
   * Meta_transfer_actions act = new Meta_transfer_actions(rockFact);
   * 
   * Meta_transfer_actionsFactory dbCollections = new
   * Meta_transfer_actionsFactory(rockFact, act);
   * 
   * long largest = 0; Vector dbVec = dbCollections.get(); for (int i = 0; i <
   * dbVec.size(); i++) { Meta_transfer_actions action = (Meta_transfer_actions)
   * dbVec.elementAt(i); if (largest <
   * action.getTransfer_action_id().longValue()) largest =
   * action.getTransfer_action_id().longValue(); }
   * 
   * return largest; }
   * 
   * public static long getScheduleMaxID(RockFactory rockFact) throws Exception
   * {
   * 
   * Meta_schedulings sche = new Meta_schedulings(rockFact);
   * 
   * Meta_schedulingsFactory dbScheduling = new
   * Meta_schedulingsFactory(rockFact, sche);
   * 
   * long largest = 0; Vector dbVec = dbScheduling.get(); for (int i = 0; i <
   * dbVec.size(); i++) { Meta_schedulings scheduling = (Meta_schedulings)
   * dbVec.elementAt(i); if (largest < scheduling.getId().longValue()) largest =
   * scheduling.getId().longValue(); }
   * 
   * return largest; }
   * 
   * 
   * public static long getSetMaxID(RockFactory rockFact) throws Exception {
   * 
   * Meta_collections coll = new Meta_collections(rockFact);
   * 
   * Meta_collectionsFactory dbCollections = new
   * Meta_collectionsFactory(rockFact, coll);
   * 
   * long largest = 0; Vector dbVec = dbCollections.get(); for (int i = 0; i <
   * dbVec.size(); i++) { Meta_collections collection = (Meta_collections)
   * dbVec.elementAt(i); if (largest <
   * collection.getCollection_id().longValue()) largest =
   * collection.getCollection_id().longValue(); }
   * 
   * return largest; }
   */

  public static String getSChedulingDescription(final Meta_schedulings sch) {
    // Description
    final String typ = sch.getExecution_type();
    if (typ != null) {
      if (typ.equals("interval") || typ.equals("intervall")) {
        return "Occurs once every " + sch.getInterval_hour() + " hours " + sch.getInterval_min() + " minutes";
      } else if (typ.equals("wait")) {
        return "Waiting trigger";
      } else if (typ.equals("fileExists")) {
        return "Waiting file " + sch.getTrigger_command();
      } else if (typ.equals("weekly")) {
        final StringBuffer sb = new StringBuffer("");
        if ("Y".equals(sch.getMon_flag())) {
          sb.append("Mon");
        }
        if ("Y".equals(sch.getTue_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Tue");
        }
        if ("Y".equals(sch.getWed_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Wed");
        }
        if ("Y".equals(sch.getThu_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Thu");
        }
        if ("Y".equals(sch.getFri_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Fri");
        }
        if ("Y".equals(sch.getSat_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Sat");
        }
        if ("Y".equals(sch.getSun_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Sun");
        }
        sb.insert(0, "Every ");
        sb.append(" at ");
        if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_hour()).append(":");
        if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_min());

        return sb.toString();
      } else if (typ.equals("monthly")) {
        final int day = sch.getScheduling_day().intValue();
        if (day <= 0) {
          final StringBuffer sb = new StringBuffer("Occures last day of month at ");
          if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_hour()).append(":");
          if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_min());
          return sb.toString();
        } else {
          final StringBuffer sb = new StringBuffer("Occures ");
          sb.append(sch.getScheduling_day()).append(". day of month at ");
          if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_hour()).append(":");
          if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
            sb.append("0");
          }
          sb.append(sch.getScheduling_min());

          return sb.toString();
        }

      } else if (typ.equals("once")) {
        final StringBuffer sb = new StringBuffer("Occures ");
        sb.append(sch.getScheduling_day()).append(".").append(sch.getScheduling_month().intValue() + 1);
        sb.append(".").append(sch.getScheduling_year()).append(" at ");
        if (String.valueOf(sch.getScheduling_hour()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_hour()).append(":");
        if (String.valueOf(sch.getScheduling_min()).length() <= 1) {
          sb.append("0");
        }
        sb.append(sch.getScheduling_min());

        return sb.toString();
      } else if (typ.equals("weeklyinterval")) {
        StringBuffer sb = new StringBuffer("Occures every ");

        if ("Y".equals(sch.getMon_flag())) {
          sb.append("Mon");
        }
        if ("Y".equals(sch.getTue_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Tue");
        }
        if ("Y".equals(sch.getWed_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Wed");
        }
        if ("Y".equals(sch.getThu_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Thu");
        }
        if ("Y".equals(sch.getFri_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Fri");
        }
        if ("Y".equals(sch.getSat_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Sat");
        }
        if ("Y".equals(sch.getSun_flag())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append("Sun");
        }

        // Drop the comma, if it is the first character before the weekdays in
        // info string.
        if (sb.charAt(14) == ',') {
          sb.deleteCharAt(14);
        }

        // Drop the comma, if it is the last character in info string.
        if (sb.charAt(sb.length() - 1) == ',') {
          sb = new StringBuffer(sb.substring(0, sb.length() - 1));
        }

        // Get/parse the interval values from database to these variables from
        // OSCommand.
        final String serializedIntervalString = sch.getOs_command();

        final Properties intervalProps = new Properties();

        if (serializedIntervalString != null && serializedIntervalString.length() > 0) {

          try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(serializedIntervalString.getBytes());
            intervalProps.load(bais);
            bais.close();
            Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.ScheduleTableModel").log(Level.FINEST,
                "Interval Properties-object read in view");
          } catch (Exception e) {
            Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.ScheduleTableModel").log(Level.FINEST,
                "Interval Properties-object error in view");
          }
        }

        final Integer intervalStartHour = new Integer(intervalProps.getProperty("intervalStartHour"));
        final Integer intervalStartMinute = new Integer(intervalProps.getProperty("intervalStartMinute"));
        final Integer intervalEndHour = new Integer(intervalProps.getProperty("intervalEndHour"));
        final Integer intervalEndMinute = new Integer(intervalProps.getProperty("intervalEndMinute"));

        String intervalStartHourString = new String(intervalStartHour.toString());
        String intervalStartMinuteString = new String(intervalStartMinute.toString());
        String intervalEndHourString = new String(intervalEndHour.toString());
        String intervalEndMinuteString = new String(intervalEndMinute.toString());

        if (intervalStartHour.intValue() < 10 && intervalStartHour.intValue() >= 0) {
          intervalStartHourString = new String("0" + intervalStartHour.toString());
        }
        if (intervalStartMinute.intValue() < 10 && intervalStartMinute.intValue() >= 0) {
          intervalStartMinuteString = new String("0" + intervalStartMinute.toString());
        }
        if (intervalEndHour.intValue() < 10 && intervalEndHour.intValue() >= 0) {
          intervalEndHourString = new String("0" + intervalEndHour.toString());
        }
        if (intervalEndMinute.intValue() < 10 && intervalEndMinute.intValue() >= 0) {
          intervalEndMinuteString = new String("0" + intervalEndMinute.toString());
        }

        sb.append(" from ");
        sb.append(intervalStartHourString + ":" + intervalStartMinuteString);

        sb.append(" to ");
        sb.append(intervalEndHourString + ":" + intervalEndMinuteString);

        sb.append(" every " + sch.getInterval_hour() + " hours and " + sch.getInterval_min() + " minutes");
        return sb.toString();

      } else if (typ.equals("onStartup")) {
        return "Executed on ETLC startup";
      } else {
        return "unknown schedule type";
      }
    }
    return "No Scheduling Type Defined";
  }

  public static Vector<String> checkDatatype(final String datatype, final Integer datasize, final Integer datascale) {
    final Vector<String> result = new Vector<String>();
    if (replaceNull(datatype).equalsIgnoreCase("numeric") || replaceNull(datatype).equalsIgnoreCase("char")
        || replaceNull(datatype).equalsIgnoreCase("varchar") || replaceNull(datatype).equalsIgnoreCase("varchar2")) {
      if (replaceNull(datasize) == 0) {
        result.add("Datasize can not be 0.");
      }
      if (replaceNull(datatype).equalsIgnoreCase("char") || replaceNull(datatype).equalsIgnoreCase("varchar")
          || replaceNull(datatype).equalsIgnoreCase("varchar2")) {
        if (replaceNull(datasize) > 32767) {
          result.add("Datasize can not be greater than 32767.");
        }
      } else {

      }
    } else {
      if (replaceNull(datasize) != 0) {
        result.add("Datasize must be 0.");
      }
    }
    if (!replaceNull(datatype).equalsIgnoreCase("numeric")) {
      if (replaceNull(datascale) != 0) {
        result.add("Datascale must be 0.");
      }
    } else {
      if (replaceNull(datasize) < 0) {
        result.add("Datasize can not be smaller than 0.");
      } else if (replaceNull(datasize) > 126) {
        result.add("Datasize must not be greater than 126.");
      }
      if (replaceNull(datascale) < 0) {
        result.add("Datascale must be 0 or greater.");
      }
      if (replaceNull(datascale) > replaceNull(datasize)) {
        result.add("Datascale can not be greater than datasize.");
      }
    }
    return result;
  }

  /**
   * Moves jframe to center of the screen
   * 
   * @param messageDialog
   */
  public static void center(final JFrame frame) {
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final Point center = ge.getCenterPoint();
    final int w = frame.getWidth();
    final int h = frame.getHeight();
    final int x = center.x - w / 2, y = center.y - h / 2;
    frame.setBounds(x, y, w, h);
    frame.validate();
  }

  /**
   * Moves dialog to center of the screen
   * 
   * @param messageDialog
   */
  public static void center(final JDialog messageDialog) {
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final Point center = ge.getCenterPoint();
    final int w = messageDialog.getWidth();
    final int h = messageDialog.getHeight();
    final int x = center.x - w / 2, y = center.y - h / 2;
    messageDialog.setBounds(x, y, w, h);
    messageDialog.validate();
  }

  /**
   * Copies file from jar file into file system
   * 
   * @param cl
   *          ClassLoader of the jar
   * @param inputFileName
   *          file to copy
   * @param outputFileName
   *          result file
   * @throws IOException
   * @throws Exception
   */
  public static void extractFileFromJar(final ClassLoader cl, final String inputFileName, final String outputFileName)
      throws IOException, Exception {
    // copy file from current jar into file system
    final URL inputFileURL = cl.getResource(inputFileName);
    if (inputFileURL == null) {
      throw new Exception("File '" + inputFileName + "' not found in jar.");
    }
    final URLConnection conn = inputFileURL.openConnection();
    final InputStream is = conn.getInputStream();
    try {
      final OutputStream os = new FileOutputStream(outputFileName);
      try {
        final byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
          os.write(buf, 0, len);
        }
      } finally {
        os.close();
      }
    } finally {
      is.close();
    }
  }

  public static boolean isSet(String name, String version, long tpid, final RockFactory rock) throws Exception {
    Meta_collections mc = new Meta_collections(rock);
    mc.setCollection_name(name);
    mc.setVersion_number(version);
    mc.setCollection_set_id(tpid);
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);
    return ((Meta_collections) mcF.getElementAt(0)) != null;
  }

  public static boolean isAction(String name, String version, long tpid, long setid, final RockFactory rock)
      throws Exception {
    Meta_transfer_actions mta = new Meta_transfer_actions(rock);
    mta.setTransfer_action_name(name);
    mta.setVersion_number(version);
    mta.setCollection_set_id(tpid);
    mta.setCollection_id(setid);
    Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
    return ((Meta_transfer_actions) mtaF.getElementAt(0)) != null;
  }

  public static boolean isScheduling(String name, String version, long tpid, long setid, final RockFactory rock)
      throws Exception {
    Meta_schedulings ms = new Meta_schedulings(rock);
    ms.setName(name);
    ms.setVersion_number(version);
    ms.setCollection_set_id(tpid);
    ms.setCollection_id(setid);
    Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock, ms);
    return ((Meta_schedulings) msF.getElementAt(0)) != null;
  }

  public static void removeSet(String name, String version, long tpid, final RockFactory rock) throws Exception {
    Meta_collections mc = new Meta_collections(rock);
    mc.setCollection_name(name);
    mc.setVersion_number(version);
    mc.setCollection_set_id(tpid);
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);

    if (mcF.size() > 0) {

      Meta_collections mcs = ((Meta_collections) mcF.getElementAt(0));
      Meta_transfer_actions mta = new Meta_transfer_actions(rock);

      mta.setVersion_number(version);
      mta.setCollection_set_id(tpid);
      mta.setCollection_id(mcs.getCollection_id());
      Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);

      Iterator<?> mtaFI = mtaF.get().iterator();
      while (mtaFI.hasNext()) {
        Meta_transfer_actions mtas = (Meta_transfer_actions) mtaFI.next();
        mtas.deleteDB();
      }

      Meta_schedulings ms = new Meta_schedulings(rock);

      ms.setVersion_number(version);
      ms.setCollection_set_id(tpid);
      ms.setCollection_id(mcs.getCollection_id());
      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock, ms);

      Iterator<?> msFI = msF.get().iterator();
      while (msFI.hasNext()) {
        Meta_schedulings mss = (Meta_schedulings) msFI.next();
        mss.deleteDB();
      }

      mcs.deleteDB();
    }
  }

  public static void removeAction(String name, String version, long tpid, long setid, final RockFactory rock)
      throws Exception {

    Meta_transfer_actions mta = new Meta_transfer_actions(rock);
    mta.setTransfer_action_name(name);
    mta.setVersion_number(version);
    mta.setCollection_set_id(tpid);
    mta.setCollection_id(setid);
    Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
    if (mtaF.size() > 0) {
      ((Meta_transfer_actions) mtaF.getElementAt(0)).deleteDB();
    }

  }

  public static void removeScheduling(String name, String version, long tpid, long setid, final RockFactory rock)
      throws Exception {

    Meta_schedulings ms = new Meta_schedulings(rock);
    ms.setName(name);
    ms.setVersion_number(version);
    ms.setCollection_set_id(tpid);
    ms.setCollection_id(setid);
    Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock, ms);
    if (msF.size() > 0) {
      ((Meta_schedulings) msF.getElementAt(0)).deleteDB();
    }

  }

  public static long getSetId(String name, String version, long tpid, final RockFactory rock) throws Exception {
    Meta_collections mc = new Meta_collections(rock);
    mc.setCollection_name(name);
    mc.setVersion_number(version);
    mc.setCollection_set_id(tpid);
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);
    if (mcF.size() == 0) {
      return -1;
    }
    return ((Meta_collections) mcF.getElementAt(0)).getCollection_id();
  }

  public static long getNextTransformationOrderNro(long first, final RockFactory rock) throws Exception {

    Vector<Long> vdata = new Vector<Long>();
    final Statement s = rock.getConnection().createStatement();
    try {
      final ResultSet r = s.executeQuery("select orderno from TRANSFORMATION");
      try {
        while (r.next()) {
          vdata.add(r.getLong("orderno"));
        }

      } finally {
        r.close();
      }
    } finally {
      s.close();
    }

    Collections.sort(vdata);
    Iterator<Long> i = vdata.iterator();

    long last = -1;
    while (i.hasNext()) {

      long l = i.next();

      if (l - 1 == last && l > first) {

        return l - 1l;
      }

    }
    return 0l;
  }

  /**
   * Gets properties file by name
   * 
   * @return Properties
   * @throws Exception
   */
  public static Properties getProperties(String fileName) throws Exception {
    File f = new File(fileName);
    FileInputStream fis = null;
    Properties conProps = new Properties();
    try {
      conProps = new Properties();
      if (f.exists() && f.isFile() && f.canRead()) {
        fis = new FileInputStream(f);
        conProps.load(fis);
      } else {
        throw new FileNotFoundException(fileName + " does not exists");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
      }
    }
    return conProps;
  }

  public static String decodeMeasurementDeltaCalcSupport(Vector<Object> deltecalcsupport) {
    String countaggregation = "";
    String hasVrSupport = "";
    String notVrSupport = "";
    for (Iterator<Object> iter = deltecalcsupport.iterator(); iter.hasNext();) {
      Object tmp = iter.next();
      if (tmp instanceof Measurementdeltacalcsupport) {
        Measurementdeltacalcsupport measurementdeltacalcsupport = (Measurementdeltacalcsupport) tmp;
        if (measurementdeltacalcsupport.getDeltacalcsupport().intValue() == 1) {
          if (hasVrSupport.length() != 0) {
            hasVrSupport += ",";
          }
          hasVrSupport += measurementdeltacalcsupport.getVendorrelease();
        } else {
          if (notVrSupport.length() != 0) {
            notVrSupport += ",";
          }
          notVrSupport += measurementdeltacalcsupport.getVendorrelease();
        }
      }
    }
    if (hasVrSupport.length() != 0) {
      if (notVrSupport.length() != 0) {
        countaggregation = hasVrSupport + ";PEG" + "/" + notVrSupport + ";GAUGE";
      } else {
        countaggregation = "PEG";
      }
    } else {
      if (notVrSupport.length() != 0) {
        countaggregation = "GAUGE";
      } else {
        countaggregation = "PEG";
      }
    }

    return countaggregation;

  }

  public static boolean patternMatch(String str, String pattern) {
    final Pattern p = Pattern.compile(pattern);
    final Matcher m = p.matcher(str);

    if (m.matches()) {
      return true;
    }

    return false;
  }

  public static void deleteDir(File removeDir) {
    if (removeDir.isDirectory()) {
      String[] filesAndDirs = removeDir.list();
      for (int i = 0; i < filesAndDirs.length; i++) {
        deleteDir(new File(removeDir, filesAndDirs[i]));
      }
    }
    removeDir.delete();
  }

  public static boolean isCustomTP(String TPType) {
    for (int ind = 0; ind < Constants.CUSTOMTECHPACKTYPES.length; ind++) {
      if (TPType.equals(Constants.CUSTOMTECHPACKTYPES[ind])) {
        return true;
      }
    }
    return false;
  }

  public static String showAggtypeGUI(String str) {

    for (int i = 0; i < Constants.BH_AGGREGATION_TYPPES.length; i++) {
      if (Constants.BH_AGGREGATION_TYPPES[i].equals(str)) {
        return Constants.BH_AGGREGATION_TYPPES_SHOW[i];
      }
    }
    return "";
  }

  public static String showAggtypeDB(String str) {

    for (int i = 0; i < Constants.BH_AGGREGATION_TYPPES_SHOW.length; i++) {
      if (Constants.BH_AGGREGATION_TYPPES_SHOW[i].equals(str)) {
        return Constants.BH_AGGREGATION_TYPPES[i];
      }
    }
    return "";
  }

  /**
   * Gets the ENIQ_LEVEL for a techpack from Versioning table based on the given
   * versionId.
   * 
   * @param rockFactory
   * @param versionId
   * @return eniq_level, or null in case no such techpack exists.
   */
  public static String getTechpackEniqLevel(RockFactory rockFactory, String versionId) {

    try {
      Versioning v = new Versioning(rockFactory, true);
      v.setVersionid(versionId);
      VersioningFactory vF = new VersioningFactory(rockFactory, v, true);
      Iterator<Versioning> dataI = vF.get().iterator();
      if (dataI.hasNext()) {
        return dataI.next().getEniq_level();
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

}
