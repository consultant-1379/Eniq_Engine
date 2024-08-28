package com.ericsson.eniq.repository.migrate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import utils.TestUtils;

/**
 * @author eeikbe
 *
 */

@RunWith (JMock.class)
public class MigrateTest {
	private Mockery context = new JUnit4Mockery();
	
	private String testDbURL      = "jdbc:hsqldb:mem:dwhrep";
	private String testDriverName = "org.hsqldb.jdbcDriver";
    private String testUserName   = "SA";
    private String testPassword   = "";
    
    private Connection connectionMock      = null;
    private Statement  statementMock       = null;
    private Statement  statementBatchMock  = null;
    private ResultSet  resultSetMock       = null;
    
    private static Field connectionField;
    private static Method generateLoadTableStatementsMethod;

	@Before
    public void setUp() throws Exception {
		System.setProperty("LOG_DIR", System.getProperty("user.home") + System.getProperty("file.separator") + "migrateTempTestDir");
		System.setProperty("dbUrl", testDbURL);
	    System.setProperty("dbType", "test"); 
	    System.setProperty("driverName", testDriverName);
	    System.setProperty("strUserName", testUserName); 
	    System.setProperty("strPassword", testPassword);

	    TestUtils.loadSetup(Migrate.initConnection(), "migrationTest");
		
		connectionMock      = context.mock(Connection.class, "connectionMock");
		statementMock       = context.mock(Statement.class,  "statementMock");
		statementBatchMock  = context.mock(Statement.class,  "statementBatchMock");
		resultSetMock       = context.mock(ResultSet.class,  "resultSetMock");
		
		generateLoadTableStatementsMethod = Migrate.class.getDeclaredMethod("generateLoadTableStatements", String.class, String.class);
		generateLoadTableStatementsMethod.setAccessible(true);
	}

	@After
	public void tearDown() throws Exception{
		TestUtils.shutdown(Migrate.initConnection());
	}
	
	@Test
	public void testOpenConnectionToDatabase(){
		Connection c;
		try {
			c = Migrate.initConnection();
			assertNotNull(c);
		} catch (MigrationException e) {
			fail("the test failed: "+e.getMessage());
		}		
	}

	@Test
	public void testCloseConnection() throws Exception{
		Migrate.initConnection();
		Migrate.closeConnection();
	}
	
	@Test
	public void testCreateSQLUnloadTableString(){
		String tableOwner = "dwhrep";
		String tableName  = "AggregationRule";
		String targetDirectoryPath  = "C:\\tmp"; 
		boolean unload = true;
		String expected ="UNLOAD TABLE dwhrep.AggregationRule TO 'C:\\tmp\\dwhrep_AggregationRule.txt';";
		String actualResult   = Migrate.createUnloadLoadTableSQL(tableOwner, tableName, targetDirectoryPath, unload);
		
		assertEquals(expected, actualResult);
	}

	@Test
	public void testCreateSQLLoadTableString(){
		String tableOwner = "dwhrep";
		String tableName  = "AggregationRule";
		String targetDirectoryPath  = "C:\\tmp"; 
		boolean unload = false;
		String expected ="LOAD TABLE dwhrep.AggregationRule FROM 'C:\\tmp\\dwhrep_AggregationRule.txt';";
		String actualResult   = Migrate.createUnloadLoadTableSQL(tableOwner, tableName, targetDirectoryPath, unload);
		
		assertEquals(expected, actualResult);
	}
	
	@Test
	public void testUnloadTables(){
		//setup the mocks...
		final Sequence resultSetSequence  = context.sequence("resultSetSequence");
		final Sequence connectionSequence = context.sequence("connectionSequence");
		final Sequence closingSequence    = context.sequence("closingSequence");
		final int[] result = new int[1];
		
		try {
			context.checking(new Expectations(){{
				//Connection Sequence...
				one(connectionMock).createStatement(); inSequence(connectionSequence); 
					will(returnValue(statementMock)); //return the first statement...
				allowing(statementMock).executeQuery(with(any(String.class))); 
					will(returnValue(resultSetMock));
				one(connectionMock).createStatement(); 
					inSequence(connectionSequence); 
					will(returnValue(statementBatchMock)); //return the second statement...
			    one(statementBatchMock).executeBatch(); 
			    	inSequence(connectionSequence); 
			    	will(returnValue(result));
			    
			    //Closing Sequence...
			    one(resultSetMock).close(); inSequence(closingSequence);
			    one(statementMock).close(); inSequence(closingSequence);
			    one(statementBatchMock).close(); inSequence(closingSequence);
			    
				
			    //ResultSet Sequence...
				one(resultSetMock).next(); 
					inSequence(resultSetSequence); 
					will(returnValue(true));
				one(resultSetMock).getString("tname");
					inSequence(resultSetSequence); 
					will(returnValue("Aggregator"));
				one(resultSetMock).getString("creator");
					inSequence(resultSetSequence); 
					will(returnValue("dwhrep"));
				one(resultSetMock).getString("tname");
					inSequence(resultSetSequence); 
					will(returnValue("Aggregator"));
				one(statementBatchMock).addBatch("UNLOAD TABLE dwhrep.Aggregator TO 'C:\\tmp"+System.getProperty("file.separator")+"dwhrep_Aggregator.txt';"); 
					inSequence(resultSetSequence);
				one(resultSetMock).next(); 
					inSequence(resultSetSequence); 
					will(returnValue(false));
			}});
		} catch (SQLException e1) {
			fail("Failed to created expectations for MOCK.");
		}
		Migrate migrate = new Migrate();
		try {
			connectionField = Migrate.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			connectionField.set(migrate, connectionMock);
		} catch (Exception e) {
			fail("Failed: testUnloadloadTables: "+e.getMessage());
		} 
		try {
			System.setProperty("sqlQuery","select * from REPOSITORYTABLES");
			Migrate.unloadTables();
		} catch (MigrationException e) {
			fail("Failed: testUnloadloadTables: "+e.getMessage());
		}
	}
	
	@Ignore
	public void testLoad_META_DATABASES_Table(){
		//setup the mocks...
		final Sequence resultSetSequence  = context.sequence("resultSetSequence");
		final Sequence connectionSequence = context.sequence("connectionSequence");
		final Sequence closingSequence    = context.sequence("closingSequence");
		
		final String testFileName = "testLoadTableStatements.sql";
		final int[] result = new int[1];
		
		try {
			context.checking(new Expectations(){{
				//Connection Sequence...
				one(connectionMock).createStatement(); inSequence(connectionSequence); 
					will(returnValue(statementMock)); //return the first statement...
				allowing(statementMock).executeQuery(with(any(String.class))); 
					will(returnValue(resultSetMock));
				one(connectionMock).createStatement(); 
					inSequence(connectionSequence); 
					will(returnValue(statementBatchMock)); //return the second statement...
			    one(statementBatchMock).executeBatch(); 
			    	inSequence(connectionSequence); 
			    	will(returnValue(result));
			    
			    //Closing Sequence...
			    one(resultSetMock).close(); inSequence(closingSequence);
			    one(statementMock).close(); inSequence(closingSequence);
			    one(statementBatchMock).close(); inSequence(closingSequence);
			    
			    //ResultSet Sequence...
				one(resultSetMock).next(); 
					inSequence(resultSetSequence); 
					will(returnValue(true));
				one(resultSetMock).getString("creator"); 
					inSequence(resultSetSequence); 
					will(returnValue("etlrep"));
				one(resultSetMock).getString("tname"); 
					inSequence(resultSetSequence); 
					will(returnValue("META_DATABASES"));
				one(resultSetMock).getString("tname"); 
					inSequence(resultSetSequence); 
					will(returnValue("META_DATABASES"));
				one(resultSetMock).getString("tname"); 
					inSequence(resultSetSequence); 
					will(returnValue("META_DATABASES"));
				one(resultSetMock).next(); 
					inSequence(resultSetSequence); 
					will(returnValue(false));
			}});
		} catch (SQLException e1) {
			fail("Failed Setup of testLoadTablesFailToCreateStatement: Failed to created expectations for MOCK.");
		}
		Migrate migrate = new Migrate();
		try {
			connectionField = Migrate.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			connectionField.set(migrate, connectionMock);
		} catch (Exception e) {
			fail("Failed: testLoadTables: "+e.getMessage());
		} 
		try {
			Migrate.loadTables("select * from REPOSITORYTABLES", testFileName);
		} catch (MigrationException e) {
			fail("Failed Setup of testLoadTables: "+e.getMessage());
		}
	}
	
	@Test
	public void testLoadTables(){
		//setup the mocks...
		final Sequence resultSetSequence  = context.sequence("resultSetSequence");
		final Sequence connectionSequence = context.sequence("connectionSequence");
		final Sequence closingSequence    = context.sequence("closingSequence");
		final int[] result = new int[1];
		System.setProperty("migrationPath", "C:\\migrationTestArea");
		final String testFileName = "testLoadTableStatements.sql";
		
		List<String> tableLoadStatements = new ArrayList<String>();
		String value1 = "load table dwhrep.AlarmReportParameter(NAME) from 'C:\\tmp\\dwhrep_AlarmReportParameter.txt';";
		String value2 = "load table etlrep.META_COLLECTION_SETS(COLLECTION_SET_ID,COLLECTION_SET_NAME) from 'C:\\tmp\\etlrep_META_COLLECTION_SETS.txt';";
		tableLoadStatements.add(value1);
		tableLoadStatements.add(value2);
		try {
			Migrate.generateLoadTableStatementFile(tableLoadStatements, testFileName);
		} catch (MigrationException e) {
			fail(e.getMessage());
		}

		
		try {
			context.checking(new Expectations(){{
				//Connection Sequence...
				one(connectionMock).createStatement(); 
					inSequence(connectionSequence); 
					will(returnValue(statementBatchMock)); //return the second statement...
			    one(statementBatchMock).executeBatch(); 
			    	inSequence(connectionSequence); 
			    	will(returnValue(result));
			    
			    //Closing Sequence...
			    one(statementBatchMock).close(); inSequence(closingSequence);
			    
				one(statementBatchMock).addBatch("TRUNCATE TABLE etlrep.META_CONNECTION_TYPES;"); 
					inSequence(resultSetSequence);
				one(statementBatchMock).addBatch("TRUNCATE TABLE etlrep.META_VERSIONS;"); 
					inSequence(resultSetSequence);
				one(statementBatchMock).addBatch("load table dwhrep.AlarmReportParameter(NAME) from 'C:\\tmp\\dwhrep_AlarmReportParameter.txt';"); 
					inSequence(resultSetSequence);
				one(statementBatchMock).addBatch("load table etlrep.META_COLLECTION_SETS(COLLECTION_SET_ID,COLLECTION_SET_NAME) from 'C:\\tmp\\etlrep_META_COLLECTION_SETS.txt';"); 
					inSequence(resultSetSequence);
			}});
		} catch (SQLException e1) {
			fail("Failed Setup of testLoadTablesFailToCreateStatement: Failed to created expectations for MOCK.");
		}
		Migrate migrate = new Migrate();
		try {
			connectionField = Migrate.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			connectionField.set(migrate, connectionMock);
		} catch (Exception e) {
			fail("Failed: testLoadTables: "+e.getMessage());
		} 
		try {
			Migrate.loadTables("select * from REPOSITORYTABLES", testFileName);
		} catch (MigrationException e) {
			fail("Failed Setup of testLoadTables: "+e.getMessage());
		}
	}
	
	@Test (expected = MigrationException.class)
	public void testLoadTablesFailToCreateStatement() throws Exception{

		final String testFileName = "testLoadTableStatements.sql";

		//setup the mocks...
		try {
			context.checking(new Expectations(){{
				//Connection Sequence...
				one(connectionMock).createStatement();
					will(throwException(new SQLException("Cannot create statement")));
			}});
		} catch (SQLException e1) {
			fail("Failed Setup of testLoadTablesFailToCreateStatement: Failed to created expectations for MOCK.");
		}
		Migrate migrate = new Migrate();
		try {
			connectionField = Migrate.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			connectionField.set(migrate, connectionMock);
		} catch (Exception e) {
			fail("Failed Setup of testLoadTablesFailToCreateStatement:"+e.getMessage());
		} 
		Migrate.loadTables("select * from REPOSITORYTABLES", testFileName);
	}
		
	@Test
	public void testRemoveFilesAfterLoadingIsCompleted(){

		//create a test directory on users home area...
		String testArea = System.getProperty("user.home") + System.getProperty("file.separator") + "migrateTempTestDir1";
		File testAreaFile = new File(testArea);
		testAreaFile.mkdir();
		testAreaFile.deleteOnExit();
		
		//Create the temporary files...
		try {
			File.createTempFile("dwhrep_test1", ".txt", testAreaFile); //will be deleted by tested method action.
			File.createTempFile("dwhrep_test2", ".txt", testAreaFile); //will be deleted by tested method action.
			File.createTempFile("dwhrep_test3", ".txt", testAreaFile); //will be deleted by tested method action.
			File.createTempFile("etlrep_test1", ".txt", testAreaFile); //will be deleted by tested method action.
			File.createTempFile("etlrep_test2", ".txt", testAreaFile); //will be deleted by tested method action.
			File.createTempFile("test1", ".txt", testAreaFile); //will not be deleted by tested method action.
			File.createTempFile("test2", ".txt", testAreaFile); //will not be deleted by tested method action.
			
		} catch (IOException e) {
			fail("FAIL Setup: testRemoveFilesAfterLoadingIsCompleted: "+e.getMessage());
		}
		try{
			Migrate.removeFiles(testArea);
			File[] remainingFiles = testAreaFile.listFiles();
			assertEquals(2, remainingFiles.length);
			for(File f: remainingFiles){
				assertTrue(f.getName().startsWith("test") && f.getName().endsWith(".txt"));
				f.delete(); //delete the remaining files in the test dir.
			}
		}catch(MigrationException e){
			fail("Cannot delete the files...");
		}finally{
			testAreaFile.delete(); //remove the test dir.
		}
	}
	
	@Test
	public void testGetTableColumnNames(){
		String targetDirectoryPath  = "C:\\tmp";
		String sysTableName = "SYSCOLUMNS";
		List<String> expected = new ArrayList<String>();
		String value1 = "load table dwhrep.AlarmReportParameter(NAME) from 'C:\\tmp\\dwhrep_AlarmReportParameter.txt';";
		String value2 = "load table dwhrep.Busyhour(VERSIONID,BHLEVEL,BHTYPE,BHCRITERIA,WHERECLAUSE,DESCRIPTION,TARGETVERSIONID," +
				"BHOBJECT,BHELEMENT,ENABLE,AGGREGATIONTYPE,OFFSET,WINDOWSIZE,LOOKBACK,P_THRESHOLD," +
				"N_THRESHOLD,CLAUSE,PLACEHOLDERTYPE,GROUPING,REACTIVATEVIEWS) from 'C:\\tmp\\dwhrep_Busyhour.txt';";
		String value3 = "load table dwhrep.BusyhourMapping(ENABLE) from 'C:\\tmp\\dwhrep_BusyhourMapping.txt';";
		String value4 = "load table etlrep.META_COLLECTION_SETS(COLLECTION_SET_ID,COLLECTION_SET_NAME) from 'C:\\tmp\\etlrep_META_COLLECTION_SETS.txt';";
		expected.add(value1);
		expected.add(value2);
		expected.add(value3);
		expected.add(value4);
		Migrate m = new Migrate();
		try {
			m.initConnection();
			List<String> actual = (List<String>)this.generateLoadTableStatementsMethod.invoke(m, sysTableName, targetDirectoryPath);
			assertNotNull(actual);
			assertTrue("The number of columns returned should be greater than zero.", actual.size() > 0);
			assertEquals(expected, actual);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		} catch (MigrationException e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGenerateLoadTableStatementFile(){
		
		//Test setup...
		//create a test directory on users home area...
		String testArea = System.getProperty("user.home") + System.getProperty("file.separator") + "migrateTempTestDir1";
		File testAreaFile = new File(testArea);
		testAreaFile.mkdir();
		testAreaFile.deleteOnExit();
		
		System.setProperty("migrationPath", testArea);
		List<String> tableLoadStatements = new ArrayList<String>();
		
		String value1 = "load table dwhrep.AlarmReportParameter(NAME) from 'C:\\tmp\\dwhrep_AlarmReportParameter.txt';";
		String value2 = "load table dwhrep.Busyhour(VERSIONID,BHLEVEL,BHTYPE,BHCRITERIA,WHERECLAUSE,DESCRIPTION,TARGETVERSIONID," +
				"BHOBJECT,BHELEMENT,ENABLE,AGGREGATIONTYPE,OFFSET,WINDOWSIZE,LOOKBACK,P_THRESHOLD," +
				"N_THRESHOLD,CLAUSE,PLACEHOLDERTYPE,GROUPING,REACTIVATEVIEWS) from 'C:\\tmp\\dwhrep_Busyhour.txt';";
		String value3 = "load table dwhrep.BusyhourMapping(ENABLE) from 'C:\\tmp\\dwhrep_BusyhourMapping.txt';";
		String value4 = "load table etlrep.META_COLLECTION_SETS(COLLECTION_SET_ID,COLLECTION_SET_NAME) from 'C:\\tmp\\etlrep_META_COLLECTION_SETS.txt';";
		tableLoadStatements.add(value1);
		tableLoadStatements.add(value2);
		tableLoadStatements.add(value3);
		tableLoadStatements.add(value4);
		
		List<String> actualTableLoadStatements = new ArrayList<String>();
		
		String fileName = "loadTableStatementFile.sql";
		
		//Test Execution...
		try {
			Migrate.generateLoadTableStatementFile(tableLoadStatements, fileName);
		} catch (MigrationException e) {
			fail(e.getMessage());
		}
		
		//check that the file was written to the correct path...
		//check that the file contained the correct contents
		StringBuffer filePath = new StringBuffer();
		filePath.append(System.getProperty("migrationPath", testArea));
		filePath.append(System.getProperty("file.separator"));
		filePath.append(fileName);
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filePath.toString()));
			 String line = null; //not declared within while loop
	
		        while (( line = in.readLine()) != null){
		        	actualTableLoadStatements.add(line);
		        }
			in.close();
		} catch (FileNotFoundException e) {
			fail("The file was not found at the following location: "+filePath.toString());
		} catch (IOException e) {
			fail("Could not close the following file: "+filePath.toString());
		}
		
		//Test verification...
		assertNotNull("Expected to find the load table statements file in the following directory: "+filePath.toString(),in);
		assertEquals("The Load statements read from the file don't match the load statements put into the file.", tableLoadStatements, actualTableLoadStatements);
		
		//Clean up the directories and files using during the test phase.
		boolean success = (new File(testArea + System.getProperty("file.separator") + fileName)).delete();
		assertTrue("The test Directory wasn't deleted on exit of the method.", success);
	}
	
	
	//The the loadStatements are null, so an Exception should be thrown.
	@Test (expected = MigrationException.class)
	public void testgenerateLoadTableStatementsNoLoadStatementsProvided() throws Exception{
		Migrate.generateLoadTableStatementFile(null, "someFile");
	}

	//The the target file name is null, so an Exception should be thrown.
	@Test (expected = MigrationException.class)
	public void testgenerateLoadTableStatementsNoFileProvided() throws Exception{
		List<String> list = new ArrayList<String>();
		Migrate.generateLoadTableStatementFile(list, null);
	}

	@Test (expected = MigrationException.class)
	public void testExtractGeneratedLoadTableStatementsFileNotFound() throws Exception{
		String loadTableStatementsFileName = "";
		Migrate.extractGeneratedLoadTableStatements(loadTableStatementsFileName);
	}

	@Test
	public void testExtractGeneratedLoadTableStatements(){
		//create a test directory on users home area...
		String testArea = System.getProperty("user.home") + System.getProperty("file.separator") + "migrateTempTestDir1";
		File testAreaFile = new File(testArea);
		testAreaFile.mkdir();
		testAreaFile.deleteOnExit();

		List<String> actualLoadTableStatements = null;
		System.setProperty("migrationPath", testArea);
		List<String> tableLoadStatements = new ArrayList<String>();
		
		String value1 = "load table dwhrep.AlarmReportParameter(NAME) from 'C:\\tmp\\dwhrep_AlarmReportParameter.txt';";
		String value2 = "load table dwhrep.BusyhourMapping(ENABLE) from 'C:\\tmp\\dwhrep_BusyhourMapping.txt';";
		String value3 = "load table etlrep.META_COLLECTION_SETS(COLLECTION_SET_ID,COLLECTION_SET_NAME) from 'C:\\tmp\\etlrep_META_COLLECTION_SETS.txt';";
		tableLoadStatements.add(value1);
		tableLoadStatements.add(value2);
		tableLoadStatements.add(value3);
				
		String fileName = "loadTableStatementFile.sql";
		
		//Test Execution...
		try {
			Migrate.generateLoadTableStatementFile(tableLoadStatements, fileName);
		} catch (MigrationException e) {
			fail(e.getMessage());
		}
		try {
			actualLoadTableStatements = Migrate.extractGeneratedLoadTableStatements(fileName);
		} catch (MigrationException e) {
			fail(e.getMessage());
		}
		assertNotNull("The method failed to read data from the load table statements file.", actualLoadTableStatements);
		assertEquals(tableLoadStatements, actualLoadTableStatements);

		//Clean up the directories and files using during the test phase.
		boolean success = (new File(testArea + System.getProperty("file.separator") + fileName)).delete();
		assertTrue("The test Directory wasn't deleted on exit of the method.", success);
	}

	
	@Test
	public void testInitLogger(){	
		String testArea = System.getProperty("user.home") + System.getProperty("file.separator") + "migrateTempTestDir";
		File testAreaFile = new File(testArea);
		testAreaFile.mkdir();
		testAreaFile.deleteOnExit();
		
		Migrate.initLogger();
		File migrationLog = new File(testArea+System.getProperty("file.separator")+"migration"+System.getProperty("file.separator")+"migration.log");
		assertTrue(migrationLog.exists());
		migrationLog.delete();
		File migrationDirectory = new File(testArea+System.getProperty("file.separator")+"migration");
		migrationDirectory.delete();
		testAreaFile.delete(); //remove the test dir.
	}
}
