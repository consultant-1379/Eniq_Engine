package com.distocraft.dc5000.etl.engine.structure;


import com.distocraft.dc5000.etl.engine.common.EngineException;
import com.distocraft.dc5000.etl.engine.common.EngineMetaDataException;

/** 
  *  An interface for TransferActionBase class 
  *  
  *
  *  @author Jukka J‰‰heimo
  *  @since  JDK1.1
  */
interface ITransferAction {
 
    public void execute() throws Exception;
    
}