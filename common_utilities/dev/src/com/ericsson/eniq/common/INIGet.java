package com.ericsson.eniq.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This function gets a variable from niq.ini file. Parameters are:<br />
 * <i>file</i> - Path to the niq.ini file.<br />
 * <i>section</i> - Name of the section.<br />
 * <i>parameter</i> - Parameter within that section.<br />
 * <br />
 * The parameter and it's value are set as a ANT project's property with the
 * following style:<br />
 * Property name is <i>section.parameter</i> and value is the value of this
 * section parameter.
 * 
 * @author Berggren
 * 
 */
public class INIGet {

  private String section = new String();

  private String parameter = new String();

  private String parameterValue = new String();

  private String file = new String();

  /**
   * This function will start the execution of this ANT task.
   */
  public void execute(Logger log) {

    String targetFilePath = this.file;
    File targetFile = new File(targetFilePath);

    if (targetFile.isFile() == false || targetFile.canRead() == false) {
      log.severe("Could not read file " + targetFilePath + ". Please check that the file " + targetFilePath
          + " exists and it can be read.");
    }

    try {
      BufferedReader reader = new BufferedReader(new FileReader(targetFile));

      boolean readingCorrectSectionParameters = false;

      String line = new String();

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("[") == true) {
          if (line.startsWith("[" + this.section + "]") == true) {
            readingCorrectSectionParameters = true;
          } else {
            readingCorrectSectionParameters = false;
          }
        }

        if (readingCorrectSectionParameters == true) {
          if (line.startsWith(this.parameter + "=")) {
            // Correct section parameter is found.
            this.parameterValue = line.substring((this.parameter + "=").length(), line.length());
          }
        }
      }
      reader.close();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Reading of file " + targetFilePath + " failed.", e);
    }
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getParameterValue() {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
  }
}
