package com.distocraft.dc5000.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HelpClass {

  public String readFileToString(File f) throws Exception {

    BufferedReader reader = null;
    String result = null;

    try {
      reader = new BufferedReader(new FileReader(f));
      String input;
      while ((input = reader.readLine()) != null) {
        result = input;
      }
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Exception e) {
          System.out.println("Error occured during closing the file");
          e.printStackTrace();
        }
      }
    }
    return result;
  }
}
