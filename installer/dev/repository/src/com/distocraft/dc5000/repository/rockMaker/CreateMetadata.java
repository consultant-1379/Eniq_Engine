package com.distocraft.dc5000.repository.rockMaker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
public class CreateMetadata {
	
			
	private static Vector engines = new Vector(); // VEngine instance cache
	
	private static String propertyFILE = "/conf/CreateMetadata.properties";
	protected Properties property;
	protected String propertyKey = "";
	
	public Properties readpropertyFile(String propertyFilename) throws Exception {
		
	  System.out.println("working dir = " +System.getProperty("user.dir"));
		Properties prop = new Properties();		
        BufferedReader br = new BufferedReader(new FileReader(propertyFilename));      
        String line = "";
        while ((line = br.readLine()) != null){
        	
        	if (!line.equalsIgnoreCase("")){

        		String[] splitted = line.split("=");
        		prop.setProperty((String)splitted[0].trim(),(String)splitted[1].trim());  
        		
        	}
         }
        		
		br.close();	
		return prop;
		
	}
	

	private Collection stringToCollection(String str,String delim){
		
		Collection result = new ArrayList();
		StringTokenizer token = new StringTokenizer(str,delim);
		while (token.hasMoreElements()){
			
			result.add(token.nextElement());
			
		}
		
		return result;
		
	}
	
	
	public void init(String key,String propertyFilename) throws Exception {
		
		if (!key.endsWith(".")) 
			key+=".";
		this.propertyKey = key;
		
		if (propertyFilename == null || propertyFilename.equalsIgnoreCase("")){

			property = readpropertyFile(System.getProperty("user.dir")+propertyFILE);

		} else {
			
			property = readpropertyFile(propertyFilename);
	
		}
				
	}
	
	
	/**
	 * Static accessor for VelocityEngine instance pooling. After acquired
	 * engine is no longer used it shall be returned into pool by adding it into
	 * vector engines.
	 * 
	 * @return a VelocityEngine
	 * @throws Exception
	 *             if engine initialization fails
	 */
	private static VelocityEngine getVelocityEngine() throws Exception {

		synchronized (engines) {
			if (engines.size() > 0) {
				VelocityEngine ve = (VelocityEngine) engines.remove(0);
				return ve;
			}
		}

		VelocityEngine ve = new VelocityEngine();
		ve.init();
		return ve;

	}
	
	public void execute() throws Exception{
				
			// Construct meta-connection and metadata-provider
	        MetaConnection metaConnect =
	            new MetaConnection(property.getProperty(propertyKey+"url"), property.getProperty(propertyKey+"driver"), property.getProperty(propertyKey+"username"), property.getProperty(propertyKey+"password"));
	        
	        MetaDataProvider mdp =
	            metaConnect.getMetaDataProvider(
	            	property.getProperty(propertyKey+"catalog"),
	            	property.getProperty(propertyKey+"schema"),
	            	property.getProperty(propertyKey+"tablePattern"),
	            	stringToCollection(property.getProperty(propertyKey+"sequenceColumns"),","),
	                false);
	
   			VelocityEngine ve = getVelocityEngine(); 
			VelocityContext context = new VelocityContext();
   	        	        
	        MetaTable[] table = mdp.getTables();	        
	        System.out.println("Number of tables fetched: "+table.length);
	        
	        for (int i = 0; i< table.length; i++){
	        	System.out.println(((MetaTable)table[i]).getCapitalizedName());
	        	
	        	String name = ((MetaTable)table[i]).getCapitalizedName();

	        	MetaColumn[] col = ((MetaTable)table[i]).getColumns();
	        	MetaColumn[] pkcol = ((MetaTable)table[i]).getPrimaryKeyColumns();
	        	MetaSequence[] seqcol = ((MetaTable)table[i]).getSequences();
	        	MetaTable[] impTable = ((MetaTable)table[i]).getImportingTables();
	        		        	
		        BufferedWriter bwBasic = new BufferedWriter(new FileWriter(property.getProperty(propertyKey+"outDir")+name+".java"));
		        BufferedWriter bwFactory = new BufferedWriter(new FileWriter(property.getProperty(propertyKey+"outDir")+name+"Factory"+".java"));
		        BufferedReader brBasic = new BufferedReader(new FileReader(property.getProperty(propertyKey+"basicTemplateIn")));
		        BufferedReader brFactory = new BufferedReader(new FileReader(property.getProperty(propertyKey+"factoryTemplateIn")));

				context.put("packageName", (String)property.get(propertyKey+"packageName"));
				context.put("className", name);
				context.put("columns", col);
				context.put("seqColumns", seqcol);
				context.put("impTables", impTable);
				context.put("pkColumns", pkcol);
						        			
				ve.evaluate(context, bwBasic, "", brBasic);
				ve.evaluate(context, bwFactory, "", brFactory);
				
				bwBasic.close();
				bwFactory.close();
				brBasic.close();
				brFactory.close();
	        		        	
	        }
				        	        
	}
	
	
	 public static void main(String[] args) {
		
		 try {
			 
			 	if (args.length >=1 ){
			 		

			 		String key = args[0];
			 		String file = "";
			 		if (args.length>1) file = args[1];
			 		
					CreateMetadata c = new CreateMetadata();
					c.init(key,file);
					c.execute();
			 		
			 	} else {
			 		System.out.println("Usage:\n propertyKey [propertyfile]");
			 	}
			 			 				 		 
		 } catch (Exception e) {
			 System.out.println("ERROR: "+e);
		 }

	}
		
}
