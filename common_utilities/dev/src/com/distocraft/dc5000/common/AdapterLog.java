package com.distocraft.dc5000.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class AdapterLog extends SessionLogger {

  private static final String colDelimiter = "\t";
  private static final String rowDelimiter = "\n";

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

  public AdapterLog() throws Exception {
    super("ADAPTER");
  }

  private void format(final Map<String/*dataid*/, List<String>/*log-entry*/> lines,
                      final Map<String, Object> data) throws Exception {
    // get the base session information
    final String dateID = (String) data.get("dateID");
    final String sessionID = (String) data.get("sessionID");
    final String batchID = (String) data.get("batchID");
    final String rowStatus = "LOADED";
    final String fileName = (String) data.get("fileName");
    final long sstamp = Long.parseLong((String) data.get("sessionStartTime"));
    final String startStamp = sdf.format(new Date(sstamp));
    final long estamp = Long.parseLong((String) data.get("sessionEndTime"));
    final String endStamp = sdf.format(new Date(estamp));
    final String source = (String) data.get("source");
    final String status = (String) data.get("status");
    final long mstamp = Long.parseLong((String) data.get("srcLastModified"));
    final String modifiedStamp = sdf.format(new Date(mstamp));
    final String flag = "0";

    // dateID needed for log-session-adapter is partitioned by dateID
    if (dateID == null || dateID.trim().equalsIgnoreCase("")) {
      throw new Exception("Date id not defined");
    } else {

      // NON-ROP here means dimension, unpartitioned, topology tables or "measurement types"
      final String datetimeForNoneROP = dateID + "_00:00:00";

      final StringBuilder baseSessionInfo = new StringBuilder();
      final StringBuilder sessionInfoRow = new StringBuilder();

      // build up part of the row with base session information
      baseSessionInfo.append(sessionID);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(batchID);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(dateID);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(rowStatus);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(fileName);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(startStamp);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(endStamp);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(source);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(status);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(modifiedStamp);
      baseSessionInfo.append(colDelimiter);

      baseSessionInfo.append(flag);
      baseSessionInfo.append(colDelimiter);

      // add base session information into row
      sessionInfoRow.append(baseSessionInfo);

      final Map<String, Map<String, String>> counterVolumes =
        (Map<String, Map<String, String>>) data.get("counterVolumes");

      if (null == counterVolumes || 0 == counterVolumes.size()) {
        // counter volume information columns as empty since no info found
        sessionInfoRow.append(colDelimiter);
        sessionInfoRow.append(colDelimiter);
        sessionInfoRow.append(colDelimiter);
        sessionInfoRow.append(colDelimiter);

        // end of row
        sessionInfoRow.append(rowDelimiter);

        final List<String> dlines;
        if (lines.containsKey(dateID)) {
          dlines = lines.get(dateID);
        } else {
          dlines = new ArrayList<String>();
          lines.put(dateID, dlines);
        }
        dlines.add(sessionInfoRow.toString());


      } else {
        // get all counter volume information and log them with base session information
        for (String key : counterVolumes.keySet()) {
          final StringBuilder finalRow = new StringBuilder();
          final StringBuilder counterVolumeCols = new StringBuilder();

          final Map<String, String> counterVolumeInfo = counterVolumes.get(key);

          final String typeName = counterVolumeInfo.get("typeName");
          counterVolumeCols.append(typeName);
          counterVolumeCols.append(colDelimiter);

          String ropStarttime = counterVolumeInfo.get("ropStarttime");
          if (null == ropStarttime || "".equals(ropStarttime)) {
            ropStarttime = datetimeForNoneROP;
          }
          counterVolumeCols.append(ropStarttime);
          counterVolumeCols.append(colDelimiter);

          final String rowCount = counterVolumeInfo.get("rowCount");
          counterVolumeCols.append(rowCount);
          counterVolumeCols.append(colDelimiter);

          final String counterVolume = counterVolumeInfo.get("counterVolume");
          counterVolumeCols.append(counterVolume);
          counterVolumeCols.append(colDelimiter);

          // end of row
          counterVolumeCols.append(rowDelimiter);

          // build up the final row
          finalRow.append(sessionInfoRow);
          finalRow.append(counterVolumeCols);

          final List<String> dlines;
          if (lines.containsKey(dateID)) {
            dlines = lines.get(dateID);
          } else {
            dlines = new ArrayList<String>();
            lines.put(dateID, dlines);
          }
          dlines.add(finalRow.toString());
        }
      }
    }
  }

  /**
   * Logs one entry.
   */
  public void log(final Map<String, Object> data) {
    try {
      final Map<String/*dataid*/, List<String>/*log-entry*/> lines = new HashMap<String, List<String>>();
      format(lines, data);
      if (!lines.isEmpty()) {
        for (String date : lines.keySet()) {
          final StringBuilder buffer = new StringBuilder();
          for (String line : lines.get(date)) {
            buffer.append(line);
          }
          writeLog(buffer, date);
        }
      }

    } catch (Exception e) {
      log.warning("FAILED record: \n+" + "sessionID=\"" + data.get("sessionID") + "\"\n" + "batchID=\""
        + data.get("batchID") + "\"\n" + "dateID=\"" + data.get("dateID") + "\"\n" + "fileName=\""
        + data.get("fileName") + "\"\n" + "sessionStartTime=\"" + data.get("sessionStartTime") + "\"\n"
        + "sessionEndTime=\"" + data.get("sessionEndTime") + "\"\n" + "source=\"" + data.get("source") + "\"\n"
        + "status=\"" + data.get("status") + "\"\n" + "srcLasModified=\"" + data.get("srcLastModified") + "\"\n"
        + "counterVolumes=" + data.get("counterVolumes"));

      log.log(Level.WARNING, "Logging error", e);
    }

    if (log.isLoggable(Level.FINEST)) {
      log.finest("LogEntry [" + name + "]:");

      for (String key : data.keySet()) {
        log.finest("  " + key + " = " + data.get(key));
      }
    }

  }

  @Override
  public void bulkLog(final Collection<Map<String, Object>> bulkData) {
    final Iterator<Map<String, Object>> iterator = bulkData.iterator();
    final Map<String/*dataid*/, List<String>/*log-entry*/> lines = new HashMap<String, List<String>>();
    try {
      while (iterator.hasNext()) {
        final Map<String, Object> data = iterator.next();
        format(lines, data);
      }
      for (String date : lines.keySet()) {
        final StringBuilder buffer = new StringBuilder();
        for (String line : lines.get(date)) {
          buffer.append(line);
        }
        writeLog(buffer, date);
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Logging error", e);
    }
  }

}
