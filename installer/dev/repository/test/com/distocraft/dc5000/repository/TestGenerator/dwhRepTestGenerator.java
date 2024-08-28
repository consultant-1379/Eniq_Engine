package com.distocraft.dc5000.repository.TestGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import com.distocraft.dc5000.repository.rockMaker.CreateMetadata;
import com.distocraft.dc5000.repository.rockMaker.MetaColumn;
import com.distocraft.dc5000.repository.rockMaker.MetaConnection;
import com.distocraft.dc5000.repository.rockMaker.MetaDataProvider;
import com.distocraft.dc5000.repository.rockMaker.MetaSequence;
import com.distocraft.dc5000.repository.rockMaker.MetaTable;

/**
 * Class which auto generates test classes for dwhrep classes.<br>
 * <br>
 * Define database connection details from where the meta data is fetched in
 * "createTestData.properties" file located in test directory. Template used in
 * creating test files is also located in the same directory. Tests are created
 * to com.distocraft.dc5000.repository.dwhrep in test directory.
 * 
 * @author EJAAVAH
 * 
 */
public class dwhRepTestGenerator extends CreateMetadata {

//  private Properties property;

//  private String propertyKey = "";

  private void init(String key) {

    try {
      if (!key.endsWith("."))
        key += ".";
      this.propertyKey = key;
      property = readpropertyFile(System.getProperty("user.dir") + "/test/createJUnitTestData.properties");
    } catch (Exception e) {
      System.out.println("Exception caught in initialization! \n");
      e.printStackTrace();
    }
  }

  private Collection stringToCollection(String str, String delim) {

    Collection result = new ArrayList();
    StringTokenizer token = new StringTokenizer(str, delim);
    while (token.hasMoreElements()) {
      result.add(token.nextElement());
    }

    return result;
  }
	@Override
  public void execute() {

    try {
      
      /* Connection to database using connection details defined in properties */
      MetaConnection metaConnect = new MetaConnection(property.getProperty(propertyKey + "url"), property
          .getProperty(propertyKey + "driver"), property.getProperty(propertyKey + "username"), property
          .getProperty(propertyKey + "password"));

      /* Initializing object which gives us meta data from the database */
      MetaDataProvider mdp = metaConnect.getMetaDataProvider(property.getProperty(propertyKey + "catalog"), property
          .getProperty(propertyKey + "schema"), property.getProperty(propertyKey + "tablePattern"), stringToCollection(
          property.getProperty(propertyKey + "sequenceColumns"), ","), false);

      /* Creating and initializing velocity engine & context */
      VelocityContext context = new VelocityContext();
      VelocityEngine ve = new VelocityEngine();
      ve.init();

      /* Table object including meta data of all the tables */
      MetaTable[] table = mdp.getTables();

      /* Counter for created files */
      int createdFileCounter = 0;

      for (int i = 0; i < table.length; i++) {

        /* name of the table */
        String name = ((MetaTable) table[i]).getCapitalizedName();

        /* Metadata from columns */
        MetaColumn[] col = ((MetaTable) table[i]).getColumns();
        MetaColumn[] pkcol = ((MetaTable) table[i]).getPrimaryKeyColumns();
        MetaSequence[] seqcol = ((MetaTable) table[i]).getSequences();
        MetaTable[] impTable = ((MetaTable) table[i]).getImportingTables();

        /* Opening writer to the created test file */
        BufferedWriter bwDwhRepTest = new BufferedWriter(new FileWriter(property.getProperty(propertyKey + "outDir")
            + name + "Test.java"));
        BufferedWriter bwDwhRepTestFactory = new BufferedWriter(new FileWriter(property.getProperty(propertyKey
            + "outDir")
            + name + "FactoryTest" + ".java"));
        BufferedReader brDwhRepTestTemplate = new BufferedReader(new FileReader(property.getProperty(propertyKey
            + "JUnitTestTemplate")));
        BufferedReader brDwhRepTestFactoryTemplate = new BufferedReader(new FileReader(property.getProperty(propertyKey
            + "JUnitTestFactoryTemplate")));

        /* Adding values to context object */
        context.put("packageName", (String) property.get(propertyKey + "packageName"));
        context.put("testClassName", name + "Test");
        context.put("testedClassName", name);
        context.put("testFactoryClassName", name + "FactoryTest");
        context.put("testedFactoryClassName", name + "Factory");
        context.put("columns", col);
        context.put("seqColumns", seqcol);
        context.put("impTables", impTable);
        context.put("pkColumns", pkcol);

        /* Create the files */
        ve.evaluate(context, bwDwhRepTest, "", brDwhRepTestTemplate);
        ve.evaluate(context, bwDwhRepTestFactory, "", brDwhRepTestFactoryTemplate);

        /* Closing streams */
        bwDwhRepTest.close();
        brDwhRepTestTemplate.close();
        bwDwhRepTestFactory.close();
        brDwhRepTestFactoryTemplate.close();

        createdFileCounter = createdFileCounter + 2;
      }

      System.out.println("Generate succeeded!");
      System.out.println(createdFileCounter + " file(s) were created.");

    } catch (Exception e) {
      System.out.println("Exception caught in generate! \n");
      e.printStackTrace();
    }
  }

  /**
   * Main method for generating the test classes
   * 
   * @param args
   */
  public static void main(String[] args) {

    try {

      /* Default value for property key is dwhrep */
      String key = "dwhrep.";

      if (args.length == 1 && (args[0].startsWith("etlrep") || args[0].startsWith("releasetool"))) {
        key = args[0];
      }
			String file = "";
			if (args.length>1) {
				file = args[1];
			}

      dwhRepTestGenerator drtg = new dwhRepTestGenerator();
      //drtg.init(key, file);
      drtg.init(key);
      drtg.execute();

    } catch (Exception e) {
      System.out.println("Exception caught in main! \n");
      e.printStackTrace();
    }
  }
}
