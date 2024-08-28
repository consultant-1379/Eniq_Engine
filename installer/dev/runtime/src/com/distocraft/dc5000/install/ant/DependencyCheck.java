package com.distocraft.dc5000.install.ant;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Created on Mar 2, 2005
 * 
 * @author lemminkainen
 */
public class DependencyCheck extends Task {

  public class VersionInfo {
    public int bld = -1;
  };

  public void execute() throws BuildException {
    System.out.println("Checking dependencies...");
    
    Project proj = getProject();
    Hashtable props = proj.getProperties();

    String inst_type = (String)props.get("dc.installation.type");
    
    String this_name = (String)props.get("module.name");    
    String this_in_inst = (String) props.get("module." + this_name);
    
    System.out.println("installation type "+inst_type+" name "+this_name);
    
    if(!inst_type.equalsIgnoreCase("update") && this_in_inst != null) {
      throw new BuildException("A previous version of this package is already installed. Use \"update\" instead of \"install\".");
    }
    
    if(this_in_inst != null) {
      String this_version = (String)props.get("module.version");
      String this_build = (String)props.get("module.build");
      
      if(this_in_inst.trim().equals((this_version+"b"+this_build).trim()))
        throw new BuildException("This package is already installed");
    }
    
    boolean passed = true;

    Iterator i = props.keySet().iterator();
    
    while (i.hasNext()) {
      String key = (String) i.next();

      if (!key.startsWith("dcinstall.require."))
        continue;
      
      if (!validateRequirement(key, props)) {
        
        String module = key.substring(key.lastIndexOf(".") + 1, key.length());

        String rq = ((String) props.get(key)).trim();
        
        System.out.println(module+" version "+rq+" required");
        
        if (passed)
          passed = false;

      }
    }

    if (!passed) {
      throw new BuildException("Failed dependencies");
    }

  }

  private boolean validateRequirement(String key, Hashtable props) {

    String module = key.substring(key.lastIndexOf(".") + 1, key.length());

    String rq = ((String) props.get(key)).trim();

    int ix = rq.indexOf(" ");

    String operator = rq.substring(0, ix).trim();

    rq = rq.substring(ix).trim();

    String installed_module = (String) props.get("module." + module);
    
    if(installed_module == null)
      return false;
    
    VersionInfo rq_version = parseVersionInfo(rq);
    VersionInfo inst_version = parseVersionInfo(installed_module);

    if (operator.equals(">")) {

      if (rq_version.bld >= 0 && inst_version.bld < rq_version.bld)
        return false;

    } else if (operator.equals("=")) {

      if (rq_version.bld >= 0 && inst_version.bld != rq_version.bld)
        return false;
    }

    return true;
  }

  private VersionInfo parseVersionInfo(String src) {

    VersionInfo vi = new VersionInfo();

    try {

      if (src.indexOf("b") >= 0) {

        int b_ix = src.lastIndexOf("b");

        String bno = src.substring(b_ix + 1, src.length());

        src = src.substring(0, b_ix).trim();

        vi.bld = Integer.parseInt(bno);

      } else {
        
        System.out.println("Error parsing version info " + src);

      }

    } catch (Exception vne) {
      System.out.println("Error parsing version info " + src);
    }

    return vi;
  }

}
