package com.distocraft.dc5000.etl.engine.connect;

import java.rmi.RemoteException;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;
import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.etl.rock.Meta_databases;

public class DbConnection {
 
    private String      versionNumber;
    private Long        connectId;
    // Metadata connection
    private RockFactory rockFact;
    // table connection
    private RockFactory tableRockFact;
    // The transfer action object
    private TransferActionBase trActionBase;
    
    public DbConnection(TransferActionBase trActionBase,RockFactory rockFact, String versionNumber, Long connectId) throws EngineMetaDataException{
        this.versionNumber = versionNumber;
        this.connectId = connectId;
        this.rockFact = rockFact;
        this.trActionBase = trActionBase;
        this.tableRockFact = createConnection();
    }
    
    protected RockFactory createConnection() throws EngineMetaDataException{
  
        String url = "";
        String userName = "";
        String passWord = "";
        String dbDriverName = "";
        
        RockFactory rockFactTmp = null;
		
            try{
                Meta_databases db = new Meta_databases(this.rockFact,this.versionNumber,this.connectId);
                url = db.getConnection_string();
                userName = db.getUsername();
                passWord = db.getPassword();
                dbDriverName = db.getDriver_name();
            }
            catch (Exception e) {
                throw new EngineMetaDataException(EngineConstants.CANNOT_READ_METADATA,new String[]{"META_DATABASES"},e,this.trActionBase,this.getClass().getName());
            }
            try {
                 rockFactTmp = new RockFactory(url,userName,passWord,dbDriverName,"ETLDBCon",true);
                 return rockFactTmp;
            }
            catch (Exception e) {
            	
            	try {
                	
            		if (rockFactTmp!= null && rockFactTmp.getConnection() != null)
            			rockFactTmp.getConnection().close();    		
            		
            	} catch (Exception ee){
            		
                    throw new EngineMetaDataException(  EngineConstants.CANNOT_CREATE_DBCONNECTION +" 2",
                            new String[]{url,userName},
                            ee,
                            this.trActionBase,
                            this.getClass().getName());
        	
            	}   	

            	
                throw new EngineMetaDataException(  EngineConstants.CANNOT_CREATE_DBCONNECTION,
                                                    new String[]{url,userName},
                                                    e,
                                                    this.trActionBase,
                                                    this.getClass().getName());
            }
        }

    
    
    public RockFactory getRockFactory() {
        return this.tableRockFact; 
    }
    
    
    public Long getConnectId() {
        return this.connectId;
    }
    
}