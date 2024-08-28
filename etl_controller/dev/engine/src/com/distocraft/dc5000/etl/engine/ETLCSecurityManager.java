package com.distocraft.dc5000.etl.engine;


/**
 * A simple security manager which just gives all permissions.
 */
public class ETLCSecurityManager
    extends java.rmi.RMISecurityManager
{
    /**
     * Check the permission.
     * @param perm the permission
     */
    public void checkPermission(java.security.Permission perm)
    {
    }
    
    
    
}
