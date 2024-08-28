package com.distocraft.dc5000.etl.scheduler.trigger;

import java.io.File;

import com.distocraft.dc5000.etl.scheduler.EventTrigger;

/**
 * @author savinen
 */
public class FileExists extends EventTrigger {

  public FileExists() {
    super();
  }

  public boolean execute() throws Exception {

    update();

    File file = new File(this.trigger_command);

    if (file.exists()) {

      if (file.canWrite()) {

        if (!file.delete()) {
          throw new Exception("File exists, but could not be writted/removed.");
        }

        return true;

      } else {
        throw new Exception("File exists, but can not be writted/removed.");
      }

    }

    return false;
  }

}
