package com.distocraft.dc5000.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on Jan 21, 2005
 * 
 * @author lemminkainen
 */
public class ProcessedFiles {

  private static Logger log = Logger.getLogger("etlengine.common.ProcessedFiles");

  private String processedDir;

  private String fileNameFormat;

  //private HashMap processedFilesMap = null;
  private Map processedFilesMap = null;

  private static HashMap cache = new HashMap();

  /**
   * Initializes a ProcessedFiles object.
   * add
   * @throws Exception
   *           on intialization failure
   */
  public ProcessedFiles(java.util.Properties conf) throws Exception {

    log.finest("initializing...");

    processedDir = conf.getProperty("ProcessedFiles.processedDir");
    fileNameFormat = conf.getProperty("ProcessedFiles.fileNameFormat");

    log.finest("  processedDir: " + this.processedDir);
    log.finest("  fileNameFormat: " + this.fileNameFormat);

    /*
    if (processedDir.endsWith(File.separator))
      processedDir = processedDir.substring(0, processedDir.length() - 1);

    if (processedDir.indexOf("${") >= 0) {
      int sti = processedDir.indexOf("${");
      int eni = processedDir.indexOf("}", sti);

      if (eni >= 0) {
        String variable = processedDir.substring(sti + 2, eni);
        String val = System.getProperty(variable);
        String result = processedDir.substring(0, sti) + val + processedDir.substring(eni + 1);
        processedDir = result;
      }
    }
*/
    
    
    processedDir = getProcessedDir(processedDir);
    
    //processedFilesMap = new HashMap();
    processedFilesMap= Collections.synchronizedMap(new HashMap());

    File procDir = new File(this.processedDir);

    if (!procDir.exists()) {

      log.finer("Trying to create processedDir " + processedDir);

      procDir.mkdirs();

    }

  }

  public String getProcessedDir(String processedDir){
    
    if (processedDir.endsWith(File.separator))
      processedDir = processedDir.substring(0, processedDir.length() - 1);

    if (processedDir.indexOf("${") >= 0) {
      int sti = processedDir.indexOf("${");
      int eni = processedDir.indexOf("}", sti);

      if (eni >= 0) {
        String variable = processedDir.substring(sti + 2, eni);
        String val = System.getProperty(variable);
        String result = processedDir.substring(0, sti) + val + processedDir.substring(eni + 1);
        processedDir = result;
      }
    }
    
    return processedDir;
    
  }
  
  /**
   * Method receives a file name and looks it from a processedList.
   * 
   * @param fileDesc
   *          Name of the file which is looked for from a text file
   */
  public boolean isProcessed(String fileDesc, String source) throws Exception {

    log.finest("isProcessed " + fileDesc);

    String fileListName = "";

    try {

      fileListName = parseFileName(fileDesc, source);

    } catch (Exception e) {
      log.warning("Could not parse identifier from filename: " + fileDesc + ", no doublicate check is done! ");
      throw (e);
    }

    log.finest("check from processed map.");
    Vector vec = (Vector) processedFilesMap.get(fileListName);
    if (vec != null && vec.contains(fileDesc)) {
      log.finer("file is processed");
      return true;
    }

    Set fileList = null;

    synchronized (cache) {

      SoftReference sr = (SoftReference) cache.get(fileListName);

      // no cache entry or soft reference broken
      if (sr == null || (fileList = (Set) sr.get()) == null) {
        fileList = readFileList(fileListName);
        sr = new SoftReference(fileList);
        cache.put(fileListName, sr);
      }

    }

    log.finest("check from cache");
    if (fileList.contains(fileDesc)) {
      log.finer("file is processed");
      return true;
    }

    return false;
  }

  /**
   * Method receives a name of file and adds this name to fileList and text file
   * The name of the text file is same than a date string which is parsed from
   * the name of the received file.
   * 
   * @param fileDesc
   *          The name of the file which is added to correct text file
   */
  public void addToProcessed(String fileDesc, String source) throws Exception {

    log.finest("addToProcessed " + fileDesc);
    String fileListName = "";

    try {

      fileListName = parseFileName(fileDesc, source);

      synchronized (cache) {

        // update cache

        SoftReference sr = (SoftReference) cache.get(fileListName);

        if (sr != null) { // we have cached entry

          Set fileList = (Set) sr.get();

          if (fileList != null) { // found from cache
            fileList.add(fileDesc);
          } else { // soft reference is already gone
            cache.remove(fileListName);
          }
        }

      }

      log.finer("Adding " + fileDesc + " to processed");
      
      synchronized(processedFilesMap) {

      if (processedFilesMap.containsKey(fileListName)) {

        ((Vector) processedFilesMap.get(fileListName)).add(fileDesc);

      } else {

        Vector vec = new Vector();
        vec.add(fileDesc);
        processedFilesMap.put(fileListName, vec);
      }
      
      } 

    } catch (Exception e) {
      log.warning("Could not parse identifier from filename: " + fileDesc + ", file is NOT added to processed! ");
      throw (e);
    }
  }

  /**
   * 
   * Writes the processed files to a textfile.
   * 
   * 
   */
  public void writeProcessedToFile() {

    Iterator keys = processedFilesMap.keySet().iterator();

    log.log(Level.INFO, "Writing filenames to "+ processedFilesMap.size() +" files");
    
    while (keys.hasNext()) {

      String fileListName = (String) keys.next();
      Vector vec = (Vector) processedFilesMap.get(fileListName);
      FileOutputStream output = null;

      try {

        //output = new FileOutputStream(processedDir + "/" + fileListName, true);
        output = new FileOutputStream(fileListName, true);
        log.finer("Opening file: " + fileListName);
        PrintWriter writer = new PrintWriter(output);

        Iterator values = vec.iterator();
        while (values.hasNext()) {
          String fileDesc = "";
          try {
            fileDesc = (String) values.next();
            log.finer("Appending: " + fileDesc);
            writer.println(fileDesc);
          } catch (Exception e) {
            log.log(Level.WARNING, "Error while appending " + fileDesc + " to file: " + fileListName, e);
          }
        }

        writer.close();
      } catch (Exception e) {

        log.log(Level.WARNING, "Error while opening/writing to/closing file: " + fileListName, e);

      } finally {

        if (output != null) {
          try {
            output.close();
          } catch (Exception e) {
            log.log(Level.WARNING, "Error closing file", e);
          }
        }
      }
    }

    processedFilesMap.clear();
    
    log.log(Level.INFO, "Done writing processed files");
    
  }

  /**
   * Method returns all file names from a single text file. The name of the text
   * file which content is required is parsered from the received dateTime
   * string.
   * 
   * @deprecated
   * @param fileListName
   *          Filename of fileList.
   * @return Set of file names included in which are processed and added to
   *         correct text file
   */
  public Set getLoadedFiles(String fileListName) {

    log.finest("getLoadedFiles");

    try {
      return readFileList(fileListName);
    } catch (Exception e) {
      return new HashSet();
    }

  }

  /**
   * Extracts the fileList filename from specified filename based on filename
   * pattern
   */
  private String parseFileName(String fileName, String source) throws Exception {

    try {

      Pattern pattern = Pattern.compile(fileNameFormat);
      Matcher matcher = pattern.matcher(fileName);

      if (matcher.matches()) {
        String listFileName = source + "_" + matcher.group(1) + ".txt";
        log.finest("parsedFileName: \"" + listFileName + "\"");

        return listFileName;
      } else {
        throw new Exception("FileName " + fileName + " doesn't match defined pattern " + fileNameFormat);
      }

    } catch (IndexOutOfBoundsException iob) {
      throw new Exception("Filename parsing failed. No caption group defined in fileNameFormat.", iob);
    }

  }

  /**
   * Reads processed fileList to Set.
   * 
   * @throws Exception
   *           in case of failure
   * @return loaded Map
   */
  private Set readFileList(String listFileName) throws Exception {

    log.finest("Trying to read from " + listFileName);

    Set set = new HashSet();

    if (listFileName == null || listFileName.length() <= 0) {
      log.info("Tried to readProcessed from empty file.");
      return set;
    }

    File checkedDir = new File(this.processedDir);

    if (checkedDir.isDirectory() && checkedDir.canRead()) {

      //File checkedFile = new File(this.processedDir + File.separator + listFileName);
      File checkedFile = new File(listFileName);

      if (checkedFile.exists() && checkedFile.canRead()) {

        log.finest("Load information file exists: " + checkedFile);

        BufferedReader reader = null;

        try {

          reader = new BufferedReader(new FileReader(checkedFile));

          String input;

          int lines = 0;
          while ((input = reader.readLine()) != null) {
            set.add(input);
            lines++;
          }

          log.fine("Read " + lines + " processed files from list");

        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (Exception e) {
              log.log(Level.WARNING, "Error closing file", e);
            }
          }
        }

      } else {
        log.finest("Load information file not created yet");
      }

    }

    return set;

  }

}
