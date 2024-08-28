package com.distocraft.dc5000.common;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class AggregatorLog extends SessionLogger {

  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

  public AggregatorLog() throws Exception {
    super("AGGREGATOR");
  }

  public void log(final Map<String, Object> data) {

    try {

      final String dateID = (String) data.get("DATE_ID");

      if (dateID == null || dateID.trim().equalsIgnoreCase("")) {
        throw new Exception("Date id not defined");
      } else {

        final PrintWriter pw = getWriter(dateID);

        pw.print((String) data.get("AGGREGATORSET_ID"));
        pw.print("\t");
        pw.print((String) data.get("SESSION_ID"));
        pw.print("\t");
        pw.print((String) data.get("BATCH_ID"));
        pw.print("\t");
        pw.print(dateID);
        pw.print("\t");
        pw.print((String) data.get("TIMELEVEL"));
        pw.print("\t");
        final long ddstamp = Long.parseLong((String) data.get("DATADATE"));
        pw.print(sdf.format(new Date(ddstamp)));
        pw.print("\t");
        final long dtstamp = Long.parseLong((String) data.get("DATATIME"));
        pw.print(sdf.format(new Date(dtstamp)));
        pw.print("\t");
        pw.print((String) data.get("ROWCOUNT"));
        pw.print("\t");
        final long estamp = Long.parseLong((String) data.get("SESSIONSTARTTIME"));
        pw.print(sdf.format(new Date(estamp)));
        pw.print("\t");
        final long sstamp = Long.parseLong((String) data.get("SESSIONENDTIME"));
        pw.print(sdf.format(new Date(sstamp)));
        pw.print("\t");
        pw.print((String) data.get("SOURCE"));
        pw.print("\t");
        pw.print((String) data.get("STATUS"));
        pw.print("\t");
        pw.print((String) data.get("TYPENAME"));
        pw.print("\t0\t\n");
        pw.flush();
        pw.close();
      }

    } catch (Exception e) {
      log.warning("FAILED record: \n+" + "AGGREGATORSET_ID=\"" + data.get("AGGREGATORSET_ID") + "\"\n"
          + "SESSION_ID=\"" + data.get("SESSION_ID") + "\"\n" + "BATCH_ID=\"" + data.get("BATCH_ID") + "\"\n"
          + "DATE_ID=\"" + data.get("DATE_ID") + "\"\n" + "TIMELEVEL=\"" + data.get("TIMELEVEL") + "\"\n"
          + "DATADATE=\"" + data.get("DATADATE") + "\"\n" + "DATATIME=\"" + data.get("DATATIME") + "\"\n"
          + "ROWCOUNT=\"" + data.get("ROWCOUNT") + "\"\n" + "SESSIONSTARTTIME=\"" + data.get("SESSIONSTARTTIME")
          + "\"\n" + "SESSIONENDTIME=\"" + data.get("SESSIONENDTIME") + "\"\n" + "SOURCE=\"" + data.get("SOURCE")
          + "\"\n" + "STATUS=\"" + data.get("STATUS") + "\"\n" + "TYPENAME=\"" + data.get("TYPENAME") + "\"");

      log.log(Level.WARNING, "Logging error", e);
    }

    if (log.isLoggable(Level.FINEST)) {
      log.finest("LogEntry [" + name + "]:");

      final Iterator i = data.keySet().iterator();
      while (i.hasNext()) {
        final String key = (String) i.next();

        log.finest("  " + key + " = " + data.get(key));
      }
    }

  }

  @Override
  public void bulkLog(final Collection<Map<String, Object>> data) {
	// TODO Auto-generated method stub
	//Not used yet...
  }

  }
