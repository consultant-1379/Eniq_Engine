package com.distocraft.dc5000.install.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Because ANT is somehow unable to destroy links here is a dirty task to perform such action
 * 
 * TODO intro <br> 
 * TODO usage <br>
 * TODO used databases/tables <br>
 * TODO used properties <br>
 * <br>
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class ForceDeleteFile extends Task {

  private String file = null;
  
  public void setFile(String file) {
    this.file = file;
  }
  
  public void execute() throws BuildException {
    
    System.out.println("Deleting file: "+file);
    
    if(file == null || file.length() <= 0)
      return;
    
    try {
      
      File f = new File(file);
      f.delete();
      
    } catch(Exception e) {
      System.out.println(e.getMessage());
      
      System.out.println("Java delete file failed. Executing native rm.");
      
      try {
        Runtime rt = Runtime.getRuntime();
        String[] ar = { "rm -fr "+file };
        Process p = rt.exec(ar);
        p.waitFor();
        
      } catch(Exception e2) {
        throw new BuildException("Unable to remove file "+file+": "+e2.getMessage());
      }
    }
    
  }
  
}
