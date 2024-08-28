package com.distocraft.dc5000.install.ant;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.releasetool.Build;
import com.distocraft.dc5000.releasetool.BuildFactory;
import com.distocraft.dc5000.releasetool.Moduledefect;
import com.distocraft.dc5000.releasetool.Moduleinfo;

/**
 * This class parses through the ant-generated changelog.xml file and inserts the changed
 * files and comments as ModuleInfo entries to database.
 * 
 * @author berggren
 */
public class ParseChangelog extends Task {

  /**
   * This class is a comparator, which does the comparing of comment lines parsed from changlog.
   * Comment lines start with the revision number so the ordering is done by those numbers.
   * The result will be something like this:
   * 1.1: Comment 1
   * 1.2: Comment 3
   * 1.11: Comment 2
   * 1.99: Comment 4
   * 1.101: Comment 5
   * @author berggren
   *
   */
  public class CommentTextComparator implements Comparator {

    public int compare(Object comment1, Object comment2) {
      try {
        String comment1String = (String) comment1;
        String comment2String = (String) comment2;

        if (comment1String.indexOf(":") == -1 && comment2String.indexOf(":") == -1) {
          return 0;
        } else if (comment1String.indexOf(":") == -1) {
          return -1;
        } else if (comment2String.indexOf(":") == -1) {
          return 1;
        }

        // In the case of revision 1.2.3.4
        // Take only account the first numbers after the dot.
        // For example: 1.2 in the example case.
        String comment1RevisionString = comment1String.substring(2, comment1String.indexOf(":"));
        String parsedComment1RevisionString = comment1RevisionString.substring(0,
            comment1RevisionString.indexOf(".") + 1);
        // Replace all dots beyond the first dot to create a valid float.
        parsedComment1RevisionString = parsedComment1RevisionString
            + comment1RevisionString
                .substring(comment1RevisionString.indexOf(".") + 1, comment1RevisionString.length()).replaceAll("\\.",
                    "");

        String comment2RevisionString = comment2String.substring(2, comment2String.indexOf(":"));
        String parsedComment2RevisionString = comment2RevisionString.substring(0,
            comment2RevisionString.indexOf(".") + 1);
        // Replace all dots beyond the first dot to create a valid float.
        parsedComment2RevisionString = parsedComment2RevisionString
            + comment2RevisionString
                .substring(comment2RevisionString.indexOf(".") + 1, comment2RevisionString.length()).replaceAll("\\.",
                    "");

        Float comment1Revision = new Float(parsedComment1RevisionString);
        Float comment2Revision = new Float(parsedComment2RevisionString);

        if (comment1Revision.floatValue() > comment2Revision.floatValue()) {
          return -1;
        } else if (comment1Revision.floatValue() < comment2Revision.floatValue()) {
          return 1;
        } else {
          return 0;
        }

      } catch (Exception e) {
        System.out.println("Exception in CommentTextComparator.");
        e.printStackTrace();
        return 0;
      }
    }
  }

  public class EntryDetails {

    String date = new String();

    String time = new String();

    String author = new String();

    Map fileChanges = new HashMap();

    String message = new String();
  }

  public class MyXmlHandler extends DefaultHandler {

    Vector entryDetailsMap = new Vector();

    EntryDetails currentEntry = new EntryDetails();

    String currentTagValue = new String();

    String currentFile = new String();

    String currentRevision = new String();

    public MyXmlHandler(Vector entryDetailsMap) {
      this.entryDetailsMap = entryDetailsMap;
    }

    public Vector getEntryDetailsMap() {
      return this.entryDetailsMap;
    }

    public void startDocument() {
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {
      currentTagValue = new String();
      if (qName.equals("name")) {
        this.currentFile = new String();
      } else if (qName.equals("revision")) {
        this.currentRevision = new String();
      }
    }

    public void endElement(String uri, String localName, String qName) {

      // End of the whole xml file
      if (qName.equals("changelog")) {

      } else if (qName.equals("entry")) {
        // Add the loaded entry to entryDetailsMap for later saving to database.
        this.entryDetailsMap.add(this.currentEntry);
        this.currentEntry = new EntryDetails();
      } else if (qName.equals("date")) {
        this.currentEntry.date = currentTagValue;
      } else if (qName.equals("time")) {
        this.currentEntry.time = currentTagValue;
      } else if (qName.equals("author")) {
        this.currentEntry.author = currentTagValue;
      } else if (qName.equals("file")) {
        // Append the key (filename) value (revision) pair to entryDetailsMap fileChanges.
        this.currentEntry.fileChanges.put(this.currentFile, this.currentRevision);
        //System.out.println("Added entry " + this.currentFile + " || " + currentRevision);
      } else if (qName.equals("name")) {
        this.currentFile = currentTagValue;
      } else if (qName.equals("revision")) {
        this.currentRevision = currentTagValue;
      } else if (qName.equals("msg")) {
        this.currentEntry.message = currentTagValue;
      }

    }

    /**
     * This function reads the characters between the xml-tags.
     */
    public void characters(char ch[], int start, int length) {
      StringBuffer charBuffer = new StringBuffer(length);
      for (int i = start; i < start + length; i++) {
        //If no control char
        if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
          charBuffer.append(ch[i]);
        }
      }
      currentTagValue += charBuffer;
    }
  }

  // These four parameters are received from the call of the ant script.
  String tag = new String("");

  String changelogPath = new String("");

  String module = new String("");

  String buildNumber = new String();

  Vector entryDetailsMap = new Vector();

  RockFactory rockFactory = null;

  public static final String URL_PREFIX = "jdbc:sybase:Tds:";

  public static final String DB_DRIVER = "com.sybase.jdbc3.jdbc.SybDriver";

  /**
   * This function starts the parsing of the changelog.
   */
  public void execute() throws BuildException {
    System.out.println("Started parsing changelog " + changelogPath + ".");

    try {
      this.rockFactory = initializeRock();

    } catch (Exception e) {
      throw new BuildException("Failed to initialize RockFactory.", e);
    }

    // First some sanity checking.
    if (this.tag == null || this.tag.equalsIgnoreCase("") == true) {
      throw new BuildException("Invalid parameter \"tag\" was given to ParseChangelog ant task.");
    }
    if (this.changelogPath == null || this.changelogPath.equals("")) {
      throw new BuildException("Invalid parameter \"changelogPath\" was given to ParseChangelog ant task.");
    }

    File changelogFile = new File(changelogPath);

    if (changelogFile.isFile() == false && changelogFile.canRead() == false) {
      throw new BuildException("Parameter \"changelogPath\" (" + changelogPath + ") is not a file or cannot be read.");
    }
    if (this.module == null || this.module.equalsIgnoreCase("") == true) {
      throw new BuildException("Invalid parameter \"module\" was given to ParseChangelog ant task.");
    }
    if (this.buildNumber == null || this.buildNumber.equalsIgnoreCase("") == true) {
      throw new BuildException("Invalid parameter \"buildNumber\" was given to ParseChangelog ant task.");
    }

    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    MyXmlHandler xmlHandler = new MyXmlHandler(this.entryDetailsMap);
    try {
      SAXParser saxParser = saxParserFactory.newSAXParser();
      // Parse the file and also register this class for call backs
      saxParser.parse(changelogFile, xmlHandler);
    } catch (SAXException se) {
      se.printStackTrace();
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (IOException ie) {
      ie.printStackTrace();
    }

    System.out.println("Finished parsing changelog.xml...");
    // Get the loaded entry details map from the xmlHandler.
    this.entryDetailsMap = xmlHandler.getEntryDetailsMap();
    // Save the collected entries to database.
    this.insertEntryDetailsToDatabase();
  }

  /**
   * This function inserts the entry details of changelog to database.
   * Changed files are ModuleInfo entries summary and description is the message of the commit.
   * @return Returns true if the insert was succesful. Otherwise returns false.
   */
  private boolean insertEntryDetailsToDatabase() {
    boolean successfulInsert = true;
    Long previousTagCreation = getPreviousTagBuildDate();
    HashMap changedFilesMap = new HashMap();

    Iterator entryDetailsIterator = this.entryDetailsMap.iterator();
    while (entryDetailsIterator.hasNext()) {
      EntryDetails currentEntryDetails = (EntryDetails) entryDetailsIterator.next();
      SimpleDateFormat usedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      try {
        Long currentBuildDateLong = new Long(usedDateFormat.parse(
            currentEntryDetails.date + " " + currentEntryDetails.time).getTime());

        // Add 3 hours to the entry date because somehow the entrydetails lag 3 hours behind server time.
        currentBuildDateLong = new Long(currentBuildDateLong.longValue() + 10800000); // 60 sec * 60 min * 3 hours * 1000 milliseconds = 10800000 milliseconds

        if (currentBuildDateLong.longValue() > previousTagCreation.longValue()) {
          if (checkForDefectEntry(currentEntryDetails) == true) {
            // Remove the string [Defect#123@Platform_rel5] from the message.
            currentEntryDetails.message = currentEntryDetails.message.substring(currentEntryDetails.message
                .indexOf("]") + 1, currentEntryDetails.message.length());
          }

          Iterator changedFilesKeySetIterator = currentEntryDetails.fileChanges.keySet().iterator();
          while (changedFilesKeySetIterator.hasNext()) {
            String filename = (String) changedFilesKeySetIterator.next();
            String revision = (String) currentEntryDetails.fileChanges.get(filename);
            if (changedFilesMap.containsKey(filename)) {
              // Append the comments to the previous comments.
              String previousComments = (String) changedFilesMap.get(filename);
              String newComments = previousComments + "\n" + revision + ": " + currentEntryDetails.message;
              String[] splittedComments = newComments.split("\n");

              if (splittedComments.length > 0) {
                // Sort the comments to after the addition of the new comment.
                // The sorting is directed by the revision at the start of the comment line.
                CommentTextComparator commentTextComparator = new CommentTextComparator();
                Arrays.sort(splittedComments, commentTextComparator);
                newComments = "";
                for (int i = 0; i < splittedComments.length; i++) {
                  if (splittedComments[i].length() > 0) {
                    newComments += splittedComments[i] + "\n";
                  }
                }
              }

              changedFilesMap.remove(filename);
              changedFilesMap.put(filename, newComments);
            } else {
              // Create new comments for this file.
              changedFilesMap.put(filename, revision + ": " + currentEntryDetails.message);
            }
          }
        }
      } catch (ParseException e) {
        System.out.println("Failed parsing " + currentEntryDetails.date + " " + currentEntryDetails.time
            + " in insertEntryDetailsToDatabase.");
        e.printStackTrace();
        throw new BuildException("Parse exception in insertEntryDetailsToDatabase.", e);
      }
    }

    // Save all the changed files and their cumulated comments to database table ModuleInfo.
    Iterator changedFilesMapIterator = changedFilesMap.keySet().iterator();
    System.out.println("Saving ModuleInfo entries for the changed files...");
    while (changedFilesMapIterator.hasNext()) {
      String changedFilename = (String) changedFilesMapIterator.next();
      Moduleinfo moduleInfo = new Moduleinfo(this.rockFactory);
      moduleInfo.setBuildnumber(new Long(this.buildNumber));
      moduleInfo.setModule(this.module);
      moduleInfo.setType("CVS_COMMIT");
      moduleInfo.setSummary(changedFilename);
      moduleInfo.setDescription( "Build " + this.buildNumber + ":\n" + (String) changedFilesMap.get(changedFilename));
      System.out.println("Saving ModuleInfo for file " + moduleInfo.getSummary());

      try {
        moduleInfo.saveDB();
      } catch (Exception e) {
        throw new BuildException("Saving ModuleInfo to database failed.", e);
      }
    }
    System.out.println("ModuleInfo entries saved succesfully.");

    return successfulInsert;
  }

  /**
   * This function returns the creation date of the previous tag.
   * @return Long Returns the milliseconds for the Date object to create the previous build datetime.
   */
  private Long getPreviousTagBuildDate() {

    try {
      Build whereBuild = new Build(this.rockFactory);
      whereBuild.setModule(this.module);
      BuildFactory buildFactory = new BuildFactory(this.rockFactory, whereBuild, " ORDER BY BUILDDATE DESC;");
      Vector allBuilds = buildFactory.get();
      if (allBuilds.size() <= 1) {
        // No previous builds exist for this module. Return a timestamp from the 1970's.
        return new Long(0); // 0 = in the year 1970 something...
      } else {
        Iterator allBuildsIterator = allBuilds.iterator();
        allBuildsIterator.next(); // <-- Get rid of the newest build.
        // Get the second newest builddate because it's always the previous builddate.
        Build previousBuild = (Build) allBuildsIterator.next();
        Timestamp previousBuildTimestamp = previousBuild.getBuilddate();
        return new Long(previousBuildTimestamp.getTime());
      }

    } catch (Exception e) {
      throw new BuildException("Exception in getPreviousTagBuildDate.", e);
    }

  }

  public String getBuildNumber() {
    return buildNumber;
  }

  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }

  public String getChangelogPath() {
    return changelogPath;
  }

  public void setChangelogPath(String changelogPath) {
    this.changelogPath = changelogPath;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public RockFactory initializeRock() throws Exception {

    String host = "cvs";
    int port = 5000;
    String username = "product";
    String password = "takingYOUforward";
    /*
    String host = "sol7";
    int port = 2641;
    String username = "dwhrep";
    String password = "dwhrep";
    */

    String url = URL_PREFIX + host + ":" + port;

    RockFactory rockFactory = new RockFactory(url, username, password, DB_DRIVER, "CTool", true);

    if (rockFactory == null)
      throw new Exception("Unable to initialize DB connection");

    return rockFactory;

  }

  /**
   * This function checks for the defect entry in the message of this commit.
   * If a defect entry is found, it is saved to the database in the ModuleDefect-table.
   * @param entryDetails EntryDetails-object containing details of the entry.
   * @throws BuildException Throws Ant BuildException.
   * @return Returns true if a defect entry was found, otherwise returns false.
   */
  private boolean checkForDefectEntry(EntryDetails entryDetails) throws BuildException {
    String message = entryDetails.message;

    if (message.indexOf("[Defect#") >= 0 || message.indexOf("[defect#") >= 0) {

      if (message.indexOf("#") == -1 || message.indexOf("@") == -1 || message.indexOf("]") == -1) {
        throw new BuildException("Defect entry has no valid characters in it.");
      }

      String trackerId = message.substring(message.indexOf("#") + 1, message.indexOf("@"));
      String trackerProject = message.substring(message.indexOf("@") + 1, message.indexOf("]"));

      try {
        // Save the defect to database.
        Moduledefect moduleDefect = new Moduledefect(this.rockFactory);
        moduleDefect.setBuildnumber(new Long(this.buildNumber));
        moduleDefect.setModule(this.module);
        moduleDefect.setTrackerid(trackerId);
        moduleDefect.setTrackerproject(trackerProject);
        System.out.println("Saving ModuleDefect of tracker id " + moduleDefect.getTrackerid() + " for project "
            + moduleDefect.getTrackerproject() +". Buildnumber is " + moduleDefect.getBuildnumber().toString());
        
        moduleDefect.saveDB();
        return true;

      } catch (Exception e) {
        System.out.println("ModuleDefect saving failed.");
        System.out.println(e.getMessage());
        e.printStackTrace();
        throw new BuildException("ModuleDefect saving failed.", e);
      }

    }
    return false;

  }

}
