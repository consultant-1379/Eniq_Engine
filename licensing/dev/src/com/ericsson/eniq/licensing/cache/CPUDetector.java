package com.ericsson.eniq.licensing.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Detects the number of CPUs (physical CPUs or cores) present in a system.
 * @author eciacah
 */
public class CPUDetector {

  /** Command used to get number of cores on system */
  public static final String CORE_CPUS_COMMAND = "/usr/sbin/psrinfo";

  /** Command used to get number of cores on system */
  public static final String PHYSICAL_CPUS_COMMAND = "/usr/sbin/psrinfo -p";

  /** Logger */
  private static final Logger log = Logger.getLogger("licensing.cache.CPUDetector");

  /**
   * Gets the number of physical CPUs installed in a system.
   * 
   * @return
   */
  public int getNumberOfPhysicalCPUs() {
    return this.getNumberOfCPUs(true);
  }

  /**
   * Gets the number of cores installed in a system.
   * 
   * @return
   */
  public int getNumberOfCores() {
    return this.getNumberOfCPUs(false);
  }

  /**
   * Executes a command in the operating system.
   * 
   * @param command
   *          The text of the command to execute.
   * @return ps The process for the command.
   * @throws IOException
   */
  protected Process executeCommand(final String command) throws IOException {
    Process ps = Runtime.getRuntime().exec(command);
    return ps;
  }

  /**
   * Creates a BufferedReader from the input stream of a process.
   * 
   * @param process
   *          A process.
   * @return in The new BufferedReader.
   */
  protected BufferedReader createBufferedReaderForProcess(final Process process) {
    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return in;
  }

  /**
   * Gets the number of CPUs for a machine.
   * 
   * @param getPhysicalCPUs
   *          True if we need the number of physical CPUs, else returns the
   *          number of cores
   * @return numberOfProcessors The number of CPUs. Will return -1 if there is
   *         an error.
   */
  private int getNumberOfCPUs(final boolean getPhysicalCPUs) {
    Process cpuinfo = null;
    int numberOfProcessors = 0;
    BufferedReader in = null;

    log.info("Getting number of CPUs");
    
    String command = "";
    if (getPhysicalCPUs) {
      command = PHYSICAL_CPUS_COMMAND;
    } else {
      command = CORE_CPUS_COMMAND;
    }

    try {
      // execute the sunos command psrinfo -p to get information about the
      // physical CPUs:
      cpuinfo = executeCommand(command);
      in = createBufferedReaderForProcess(cpuinfo);
      cpuinfo.waitFor();

      if (getPhysicalCPUs) {
        numberOfProcessors = calculatePhysicalCPUs(in);
      } else {
        numberOfProcessors = calculateNoCores(in);
      }

      log.info(command + " reported " + numberOfProcessors + " processors.");
    } catch (Exception e) {
      log.warning("Error getting number of processors: " + e.toString());
      numberOfProcessors = -1;
      e.printStackTrace();
    } finally {
      // close the stream
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          log.warning("IOException when closing the psrinfo stream reader.");
        }
      }
    }
    log.info("Detected " + numberOfProcessors + " processors");
    return numberOfProcessors;
  }
  
  /**
   * Calculates the number of physical CPUS, given output from psrinfo -p command.
   * @param   in            The BufferedReader holding the output from psrinfo.
   * @return  numberOfCpus  Number of cpus.           
   * @throws IOException
   */
  private int calculatePhysicalCPUs(final BufferedReader in) throws IOException {
    int numberOfCpus = 0;

      // psrinfo -p outputs a single line with the number of processors:
      final String processorsString = in.readLine();
      if (processorsString != null && processorsString.length() > 0) {
        numberOfCpus = Integer.parseInt(processorsString);
        log.info("psrinfo -p reported " + numberOfCpus + " processors.");
      } else {
        log.warning("Read in a blank line from psrinfo command.");
      }
    return numberOfCpus;
  }
  
  /**
   * Calculates the number of cores, given output from psrinfo command.
   * @param   in            The BufferedReader holding the output from psrinfo.
   * @return  numberOfCpus  Number of cores.  
   * @throws IOException
   */
  private int calculateNoCores(final BufferedReader in) throws IOException {
    int numberOfCpus = 0;
      // psrinfo outputs one line for each core. Count the lines to get the
      // number of cores.
      String newLine = in.readLine();
      while (newLine != null) {
        try {
          if (newLine.length() > 0) {
            // Check that the first character is a number:
            char firstChar = newLine.charAt(0);
            final String charString = Character.toString(firstChar);
            // Check if we can parse the string:
            Integer.parseInt(charString);
            numberOfCpus++;
          } else {
            log.warning("Read in a blank line from psrinfo command.");
          }
        } catch (NumberFormatException numberFormatExc) {
          log.severe("Error reading output from psrinfo command. Could not parse the number of the core: "
              + numberFormatExc.toString());
          log.severe("Line = " + newLine);
        } catch (Exception exc) {
          log.severe("Error reading line from psrinfo command. Line did not start with the number of the core. Line = " + newLine);
        }
        // Read in the next line:
        newLine = in.readLine();
    }
    return numberOfCpus;
  }

}
