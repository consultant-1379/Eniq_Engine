package com.distocraft.dc5000.etl.engine.common;

import ssc.rockfactory.FactoryRes;
import ssc.rockfactory.RockFactory;
/**
 * This class is String resource pool for dagger.engine
 * All constants are here.
 *
 * @author      Jukka Jääheimo
 * @since       JDK1.2
 */
public abstract class DatabaseSpecific
{
    
    
    /**
	 * Returns the table owner name of DAGGER tables. 
	 * 
	 * @param		rockFact The database connection.
	 * @return 
	 */
	public static String getDaggerOwnerName(RockFactory rockFact) {
    
        if (rockFact.getDriverName().indexOf(FactoryRes.SYBASE_DRIVER_NAME) > 0) {
            return EngineConstants.DAGGER_OWNER_NAME_SYBASE;
        }
        else {
            return EngineConstants.DAGGER_OWNER_NAME_ORACLE;
        }
    
    }


}