package com.distocraft.dc5000.etl.engine.plugin;

import com.distocraft.dc5000.etl.engine.common.EngineBaseException;

/** PluginException an exception class for the plugins to use
*
*/

public class PluginException extends EngineBaseException {
    
    private String message = "";
    
    public PluginException(Throwable nestedException) {
        super();
        this.message = nestedException.getMessage();
        this.nestedException = nestedException;
    }
    public PluginException(String message) {
        super(message);
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
}