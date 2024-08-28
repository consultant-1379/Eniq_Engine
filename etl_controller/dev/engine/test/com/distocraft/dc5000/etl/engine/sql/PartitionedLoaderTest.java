package com.distocraft.dc5000.etl.engine.sql;

import static org.junit.Assert.*;

//import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Hashtable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
//import ssc.rockfactory.RockResultSet;

//import com.distocraft.dc5000.common.Properties;
//import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.BaseMock;
import com.distocraft.dc5000.etl.engine.common.SetContext;
import com.distocraft.dc5000.etl.engine.connect.ConnectionPool;
//import com.distocraft.dc5000.etl.engine.main.TransferEngine;
//import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_versions;
import com.distocraft.dc5000.etl.engine.sql.Loader;
//import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.eniq.common.VelocityPool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PartitionedLoaderTest extends BaseMock{
	
	//The PartitionedLoader to be tested:
	PartitionedLoader pl;
	
	//All the interface parameters of PartitionedLoader:
	Meta_versions mockedMeta_versions;
	Long collectionSetId;
	Meta_collections mockedMeta_collections;
	Long transferActionId;
	Long transferBatchId;
	Long connectId;
	RockFactory mockedRockFactory;
	ConnectionPool mockedConnectionPool;
	Meta_transfer_actions mockedMeta_transfer_actions;
	String batchColumnName;
	SetContext mockedSetContext;
	Logger mockedLogger;
	
	Meta_collection_sets mockedMeta_col_sets;
	Iterator mockedIterator;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() {
		//Make an instance of a Partitioned Loader
		pl = new PartitionedLoader();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testLoadTable() throws Exception {
		
		//Make necessary private/protected fields and methods of PartitionedLoaded/Loader accessible by was of reflection:
		Field loaderParameters_BINARYField = Loader.class.getDeclaredField("loaderParameters_BINARY");
		Field logField = Loader.class.getDeclaredField("log");
		Field wherePropsField = Loader.class.getDeclaredField("whereProps");
		Field sqlLogField = Loader.class.getDeclaredField("sqlLog");
		Field fileLogField = Loader.class.getDeclaredField("fileLog");
		Method loadTableMethod = Loader.class.getDeclaredMethod("loadTable", String.class, List.class, String.class,
			     VelocityEngine.class, Statement.class, String.class); //This is the method that's going to be tested.
		
		loaderParameters_BINARYField.setAccessible(true);
		logField.setAccessible(true);
		wherePropsField.setAccessible(true);
		sqlLogField.setAccessible(true);
		fileLogField.setAccessible(true);
		loadTableMethod.setAccessible(true);
		
		
		//And now set the values ot the necessary fields of the PartitionedLoader object
		logField.set(pl, Logger.getLogger("global"));
		sqlLogField.set(pl, Logger.getLogger("global"));
		fileLogField.set(pl, Logger.getLogger("global"));
		wherePropsField.set(pl, pl.stringToProperty("#Thu Apr 22 13:20:07 BST 2010"+
		"\ndateformat=yyyy-MM-dd"+
		"\ntaildir=raw"+
		"\ntechpack=DC_E_RAN"+
		"\ntablename=DC_E_RAN_CCDEVICE"+
		"\ncheckpoint=OFF"+
		"\nnotify_rows=100000"+
		"\ntechpack=DC_E_RAN"+
		"\nmeastype=DC_E_RAN_CCDEVICE"
		));
		
		//Setting system properties required by the loadTable method
		System.setProperty("LOG_DIR", "/eniq/log/sw_log");
		System.setProperty("REJECTED_DIR", "/eniq/data/rejected");

		
		
		//Create the interface parameters to be submitted to the interface  
		//of the loadTable method - and mock whatever needs to be mocked.
		String tableName = "DC_E_RAN_CCDEVICE_RAW_99";
		List fileNames = new ArrayList<String>();
		fileNames.add("\\eniq\\data\\etldata\\dc_e_ran_ccdevice\\raw\\DC_E_RAN_CCDEVICE_1112223334445_2010-04-09.binary"); fileNames.add("\\eniq\\data\\etldata\\dc_e_ran_ccdevice\\raw\\DC_E_RAN_CCDEVICE_5556667778889_2010-04-09.binary");
		String binLoadTemplate = "LOAD TABLE $TABLE (COL1 BINARY NULL BYTE, COL2 BINARY NULL BYTE) FROM $FILENAMES $LOADERPARAMETERS";
		VelocityEngine vengine = VelocityPool.reserveEngine();
		final Statement mockedStatement = context.mock(Statement.class);
		
		//Get today's date in the form YYYYMMDD
		final String todaysDate = getDateAsYYYYMMDD();
		//The SQL expected to be generated in the loadTable method:
		final String expectedSQL = "LOAD TABLE DC_E_RAN_CCDEVICE_RAW_99 (COL1 BINARY NULL BYTE, COL2 BINARY NULL BYTE) FROM '/eniq/data/etldata/dc_e_ran_ccdevice/raw/DC_E_RAN_CCDEVICE_1112223334445_2010-04-09.binary'," +
		"'/eniq/data/etldata/dc_e_ran_ccdevice/raw/DC_E_RAN_CCDEVICE_5556667778889_2010-04-09.binary' "
		+ "ESCAPES OFF  QUOTES OFF FORMAT BINARY WITH CHECKPOINT OFF \n"
		+ "NOTIFY 100000 ON FILE ERROR CONTINUE IGNORE CONSTRAINT NULL 0, DATA VALUE 2000000, UNIQUE 2000000 \n"
		+ "MESSAGE LOG '/eniq/log/sw_log/iqloader/DC_E_RAN/DC_E_RAN_CCDEVICE_RAW/DC_E_RAN_CCDEVICE_"+todaysDate+"_msg.log' \n"
        + "ROW LOG '/eniq/data/rejected/DC_E_RAN/DC_E_RAN_CCDEVICE_RAW/DC_E_RAN_CCDEVICE_"+todaysDate+"_row.log' \n"
        + "ONLY LOG UNIQUE, NULL, DATA VALUE LOG DELIMITED BY ';' \n";
		
		
		//This will be the expected behaviour of the mocked object(s).
		context.checking(new Expectations() 
			{{
				allowing(mockedStatement).getConnection();
			
				//This is the important check - if this doesn't occur with this exact input value (SQL) then this junit test fails:
				oneOf(mockedStatement).executeUpdate(with(expectedSQL)); 
				will(returnValue(1));
			
			}}
		);
		
		//Now run the method to be tested
		loadTableMethod.invoke(pl, tableName, fileNames, binLoadTemplate, vengine, mockedStatement, (String)loaderParameters_BINARYField.get(pl));

	}

	/**
	 * Gives current date in the form YYYYMMDD
	 * @param cal
	 * @return
	 */
	private String getDateAsYYYYMMDD() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		final String year = Integer.toString(cal.get(Calendar.YEAR));
		final int monthNum = cal.get(Calendar.MONTH)+1;
		final String month;
		if (monthNum<10){ month = "0"+monthNum;}
		else{month = Integer.toString(monthNum);}
		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		if(day.length()==1) day = "0"+day;
		final String todaysDate = year+month+day;
		return todaysDate;
	}

}

