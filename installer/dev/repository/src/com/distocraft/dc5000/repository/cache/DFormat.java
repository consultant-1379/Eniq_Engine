package com.distocraft.dc5000.repository.cache;

import java.util.Iterator;
import java.util.List;

public class DFormat {
  
  private String interfaceName;
  private String tagID;
  private String dataFormatID;
  private String folderName;
  private String transformerID;
  
  private List ditems = null;
  
  public DFormat(String ifname, String tid, String dfid, String fname, String trID) {
    interfaceName = ifname;
    tagID = tid;
    dataFormatID = dfid;
    folderName = fname;
    transformerID = trID;
  }

  public String getDataFormatID() {
    return dataFormatID;
  }

  public List getDitems() {
    return ditems;
  }

  public String getFolderName() {
    return folderName;
  }

  public String getTransformerID() {
    return transformerID;
  }

  
  public String getInterfaceName() {
    return interfaceName;
  }

  public String getTagID() {
    return tagID;
  }
  
  public int getDItemCount() {
    return ditems.size();
  }
  
  public Iterator getDItems() {
    return ditems.iterator();
  }
  
  public void setItems(List list) {
    ditems = list;
  }
  
}
