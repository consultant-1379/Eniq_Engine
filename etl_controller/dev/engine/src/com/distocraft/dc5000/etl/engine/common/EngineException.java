package com.distocraft.dc5000.etl.engine.common;


import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;



/**
 *
 * This class constructs different types of EngineExeptions. 
 * EngineExeptionHandler is built to parse the information from
 * the Exceptions to show it user friendly.
 *
 * original author  Pekka Kaarela, modified to Dagger Engine project Jukka J‰‰heimo
 *
 * @author          $Author: raatikainen $
 * @since           JDK1.1
 */
 
public class EngineException extends EngineBaseException {
   
    

    

    
    /**  
    * Constructor with error message, non internationalized information and nested exception
    *
    * @param message error message
    * @param params parameters e.g. filename, path    
    * @param nestedException Nested exception
    * @param TransferActionBase trActionBase
    * @param String methodName
    */
    
    public EngineException (    String message, 
                                String[] params, 
                                Throwable nestedException,
                                TransferActionBase trActionBase,
                                String methodName,
                                String errorType) {
        
        super(message);
        this.trActionBase = trActionBase;
        this.nestedException=nestedException;
        this.errorMessage = message;
        this.params = params;
        this.methodName = methodName;
        this.errorType = errorType;
    }
    /**  
    * Constructor with error message, non internationalized information and nested exception
    *
    * @param message error message
    * @param nestedException Nested exception
    * @param TransferActionBase trActionBase
    * @param String methodName
    */
    
    public EngineException (    String message, 
                                Throwable nestedException,
                                TransferActionBase trActionBase,
                                String methodName,
                                String errorType) {
                                
        
        super(message);
        this.trActionBase = trActionBase;
        this.nestedException=nestedException;
        this.errorMessage = message;
        //this.params = params;
        this.methodName = methodName;
        this.errorType = errorType;
        
    }
    
}