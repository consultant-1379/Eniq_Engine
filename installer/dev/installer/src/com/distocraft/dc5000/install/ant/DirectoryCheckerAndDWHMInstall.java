package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;

/**
 * This is custom made ANT task that calls directory checker and DHWM install
 * sets of the techpack installed by tp_installer script.
 * 
 * @author Berggren
 * 
 */
public class DirectoryCheckerAndDWHMInstall extends Task {

  private String techPackName = new String();

  private int techPackMetadataVersion = 1;

  private String techPackVersion = new String();

  private String buildNumber = new String();

  private String binDirectory = new String();

  private String directoryCheckerSetName = new String();

  private String dwhmInstallSetName = new String();

  private RockFactory etlrepRockFactory = null;

  private String installingInterface = new String();

  private Integer exitValue;

  /**
   * This function starts the calls to directory checker and DWHM install sets.
   */
  public void execute() throws BuildException {
    try {

      this.etlrepRockFactory = this.createRockFactory(getProject().getProperty("etlrepDatabaseUrl"), getProject()
          .getProperty("etlrepDatabaseUsername"), getProject().getProperty("etlrepDatabasePassword"), getProject()
          .getProperty("etlrepDatabaseDriver"));

      if (!this.binDirectory.endsWith(File.separator)) {
        // Add the missing separator char "/" from the end of the directory
        // string.
        this.binDirectory = this.binDirectory + File.separator;
      }

      this.directoryCheckerSetName = new String("Directory_Checker_" + this.techPackName);
      this.dwhmInstallSetName = new String("DWHM_Install_" + this.techPackName);

      // Run the reloadConfig before executing DWHM_Install set of the techpack.

      final String reloadConfigCommand = new String(this.binDirectory + "engine -e reloadConfig");
      System.out.println("Running reloadConfig for the engine");

      final String reloadConfigOut = this.runCommand(reloadConfigCommand);
      System.out.println(reloadConfigOut);

      if (this.getExitValue().intValue() != 0) {
        throw new BuildException(
            "Cannot run reloadConfig for the engine. Engine is not started. Aborting tech pack installation.");
      }

      if (reloadConfigOut.indexOf("Connection to engine refused") >= 0) {

        throw new BuildException("Engine is not started. Aborting tech pack installation.");

      } else {
        if (this.installingInterface.equalsIgnoreCase("true")) {

          System.out.println("Directory checker is not started during installation of an interface.");

        } else {
          // Start the directory checker for the tech pack.

          System.out.println("Searching sets. Metadata version is " + techPackMetadataVersion);

          if (directoryCheckerSetExists()) {
            final String directoryCheckerCommand = new String(this.binDirectory + "engine -e startAndWaitSet "
                + this.techPackName + " " + this.directoryCheckerSetName);
            System.out.println("Running " + this.directoryCheckerSetName);
            System.out.println(this.runCommand(directoryCheckerCommand));

            if (this.getExitValue().intValue() == 1) {
              System.out.println("Directory checker " + this.directoryCheckerSetName + " not found. Cannot run "
                  + this.techPackName + " " + this.directoryCheckerSetName + ".");
            } else if (this.getExitValue().intValue() == 2) {
              throw new BuildException(this.directoryCheckerSetName
                  + " has been dropped from priorityqueue. Aborting tech pack installation.");
            } else if (this.getExitValue().intValue() > 2) {
              throw new BuildException(this.directoryCheckerSetName + " has failed. Aborting tech pack installation.");
            }
          } else {
            System.out
                .println("Directory checker set not found " + this.directoryCheckerSetName + ". Set not started.");
          }
        }

        if (dwhmInstallSetExists()) {
          final String dwhmInstallCommand = new String(this.binDirectory + "engine -e startAndWaitSet "
              + this.techPackName + " " + this.dwhmInstallSetName);

          System.out.println("Running " + this.dwhmInstallSetName);
          System.out.println(this.runCommand(dwhmInstallCommand));

          if (this.getExitValue().intValue() == 1) {
            System.out.println("DWHM_Install set not found. Cannot run " + this.techPackName + " "
                + this.dwhmInstallSetName + ".");
          } else if (this.getExitValue().intValue() == 2) {
            throw new BuildException(this.dwhmInstallSetName
                + " has been dropped from priorityqueue. Aborting tech pack installation.");
          } else if (this.getExitValue().intValue() > 2) {
            throw new BuildException(this.dwhmInstallSetName + " has failed. Aborting tech pack installation.");
          }
        } else {
          System.out.println("DWHM Install set not found " + this.dwhmInstallSetName + ". Set not started.");
        }

        // Run the updateTransformation command for the engine.
        final String updateTransformationCommand = new String(this.binDirectory + "engine -e updateTransformation "
            + this.techPackName);
        System.out.println("Updating TransformerCache for tech pack " + this.techPackName);

        final String outputString = this.runCommand(updateTransformationCommand);
        System.out.println(outputString);

        if (this.getExitValue().intValue() != 0) {
          throw new BuildException("Cannot update TransformerCache for tech pack " + this.techPackName
              + ". Aborting tech pack installation.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Executing tech pack install sets failed.", e);
    }
  }

  /**
   * This command is support for executing any system commands from GUI. Use
   * getExitValue() to get the exitValue of the system command.
   * 
   * @param command
   *          the command that is needed to run
   * @return returns the output of the completed command
   * @throws IOException
   */
  public final String runCommand(String command) throws IOException, BuildException {
    final StringBuffer result = new StringBuffer();

    final Runtime runtime = Runtime.getRuntime();
    final Process process = runtime.exec(command);

    // read what process wrote to the STDIN (immediate)
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      result.append(line).append("\n");
    }

    // wait for process to end
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      try {
        process.waitFor();
      } catch (InterruptedException e2) {
        // do we have a problem here?
      }
    }

    // and read whatever was left to STDIN
    while ((line = bufferedReader.readLine()) != null) {
      result.append(line).append("\n");
    }

    // close streams
    bufferedReader.close();
    process.getErrorStream().close();
    process.getOutputStream().close();

    // save exit information of the process and return with output string
    exitValue = new Integer(process.exitValue());
    result.append("Command executed with exitvalue " + exitValue.toString());

    return result.toString();
  }

  public String getTechPackName() {
    return techPackName;
  }

  public void setTechPackName(final String techPackName) {
    this.techPackName = techPackName;
  }

  public String getTechPackMetadataVersion() {
    return Integer.toString(techPackMetadataVersion);
  }

  public void setTechPackMetadataVersion(final String techPackMetadataVersion) {
    if (techPackMetadataVersion != null && techPackMetadataVersion.length() > 0) {
      try {
        this.techPackMetadataVersion = Integer.parseInt(techPackMetadataVersion);
      } catch (NumberFormatException nfe) {
      }
    }
  }

  public String getTechPackVersion() {
    return techPackVersion;
  }

  public void setTechPackVersion(final String techPackVersion) {
    this.techPackVersion = techPackVersion;
  }

  public void setBuildNumber(final String buildNumber) {
    this.buildNumber = buildNumber;
  }

  public String getBuildNumber() {
    return this.buildNumber;
  }

  public String getBinDirectory() {
    return binDirectory;
  }

  public void setBinDirectory(final String binDirectory) {
    this.binDirectory = binDirectory;
  }

  /**
   * This function returns true if the directory checker set exists for the tech
   * pack to be installed/updated.
   * 
   * @return Returns true if the directory checker exists, otherwise returns
   *         false.
   */
  public boolean directoryCheckerSetExists() throws BuildException {
    try {
      final Meta_collections targetMetaCollection = new Meta_collections(this.etlrepRockFactory);
      targetMetaCollection.setCollection_name(this.directoryCheckerSetName);
      targetMetaCollection.setEnabled_flag("Y");
      final Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
          targetMetaCollection);

      final Vector<Meta_collections> targetMetaCollectionsVector = metaCollectionsFactory.get();
      final Iterator<Meta_collections> targetMetaCollectionsIterator = targetMetaCollectionsVector.iterator();

      System.out.println(targetMetaCollectionsVector.size() + " directory checker sets found with "
          + this.directoryCheckerSetName);

      // Check if the DirectoryChecker set versionnumber starts with tech pack's
      // version.
      while (targetMetaCollectionsIterator.hasNext()) {
        final Meta_collections currentMetaCollection = (Meta_collections) targetMetaCollectionsIterator.next();

        if (techPackMetadataVersion >= 3) {

          if (currentMetaCollection.getVersion_number().equals("((" + buildNumber + "))")) {
            return true;
          }

        } else {

          if (currentMetaCollection.getVersion_number().startsWith(this.techPackVersion)) {
            // Directory checker found.
            return true;
          }

        }

      }

      // No directory checker set found.
      System.out.println("No directory checker set found for " + this.directoryCheckerSetName + " where version "
          + this.techPackVersion);
      return false;

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Checking of directory checker set failed.", e);
    }
  }

  /**
   * This function returns true if the DWHM Install set exists for the tech pack
   * to be installed/updated.
   * 
   * @return Returns true if the directory checker exists, otherwise returns
   *         false.
   */
  public boolean dwhmInstallSetExists() throws BuildException {
    try {
      final Meta_collections targetMetaCollection = new Meta_collections(this.etlrepRockFactory);
      targetMetaCollection.setCollection_name(this.dwhmInstallSetName);
      final Meta_collectionsFactory metaCollectionsFactory = new Meta_collectionsFactory(this.etlrepRockFactory,
          targetMetaCollection);

      final Vector<Meta_collections> targetMetaCollectionsVector = metaCollectionsFactory.get();
      final Iterator<Meta_collections> targetMetaCollectionsIterator = targetMetaCollectionsVector.iterator();

      System.out
          .println(targetMetaCollectionsVector.size() + " dwhminstall sets found with " + this.dwhmInstallSetName);

      // Check if the DWHM_Instal set versionnumber starts with tech pack's
      // version.
      while (targetMetaCollectionsIterator.hasNext()) {
        final Meta_collections currentMetaCollection = targetMetaCollectionsIterator.next();

        if (techPackMetadataVersion >= 3) {

          if (currentMetaCollection.getVersion_number().equals("((" + buildNumber + "))")) {
            // Directory checker found.
            return true;
          }

        } else {

          if (currentMetaCollection.getVersion_number().startsWith(this.techPackVersion)) {
            // Directory checker found.
            return true;
          }

        }

      }

      // No DWHM Install set found.
      System.out.println("No DWHM_Install set found for " + this.dwhmInstallSetName + " where version "
          + this.techPackVersion);
      return false;

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Checking of DWHM Install set failed.", e);
    }

  }

  /**
   * This function creates the rockfactory object from the database connection
   * details.
   * 
   * @return Returns the created RockFactory.
   */
  private RockFactory createRockFactory(final String databaseUrl, final String databaseUsername,
      final String databasePassword, final String databaseDriver) throws BuildException {

    RockFactory rockFactory = null;

    try {
      rockFactory = new RockFactory(databaseUrl, databaseUsername, databasePassword, databaseDriver, "PreinstallCheck",
          true);

    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException("Unable to initialize database connection.", e);
    }

    if (rockFactory == null)
      throw new BuildException(
          "Unable to initialize database connection. Please check the settings in the ETLCServer.properties file.");

    return rockFactory;
  }

  public String getInstallingInterface() {
    return installingInterface;
  }

  public void setInstallingInterface(String installingInterface) {
    this.installingInterface = installingInterface;
  }

  public Integer getExitValue() {
    return exitValue;
  }

  public void setExitValue(Integer exitValue) {
    this.exitValue = exitValue;
  }
}
