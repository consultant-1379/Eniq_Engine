package com.distocraft.dc5000.etl.engine.plugin;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

/** Class PluginLoader
*   Reads & loads java classes from plugin directory
*   It constructed once from TransferEngine at the start of the dagger TransferEngine.
*
*/
public class PluginLoader {
    //the directory of available plugin classes
    private String pluginPath;
    // Loaded classes & jars
    private URLClassLoader urlCl;

    /** Constructor
    *
    *   @param String pluginPath the directory of available plugin classes
    */
    public PluginLoader(String pluginPath) throws IOException{
        this.pluginPath = pluginPath;
        // Save class loader so that we can restore later
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

        // Create class loader using given URL
        // Use prevCl as parent to maintain current visibility
        this.urlCl = 
               URLClassLoader.newInstance(this.getUrls(), prevCl);

        Thread.currentThread().setContextClassLoader(urlCl);            
        
        
    }
    
    /**
      * Loads a class
      *
      * @param name of the class
      * @return the class
      * @exception could not find given class
      */
     public Class loadClass(String className) throws ClassNotFoundException
     {
            Class c = this.urlCl.loadClass(className);
            return c;

     }



     
     
    /** Returns the class names in the plugin directory
    *
    *
    *
    */
    public String[] getPluginNames() {
        File classFile = new File(this.pluginPath);
        
        String[] fileList = classFile.list();
        int classes = 0;
        for (int i=0; i<fileList.length;i++) {
            if (fileList[i].toUpperCase().indexOf("PLUG.CLASS")>0) {
                classes++;
            }
        }
        
        String[] classFileList = new String[classes];
        
        int counter = 0;
        for (int i=0; i<fileList.length;i++) {
            if (fileList[i].toUpperCase().indexOf("PLUG.CLASS")>0) {
                String plugName = fileList[i];
                classFileList[counter] = plugName.substring(0,plugName.lastIndexOf("."));
                counter++;
            }
        }
        return classFileList;
    }
    
    /** Returns the class names in the plugin directory
    *
    *
    *
    */
    public String[] getPluginMethodNames(String pluginName, boolean isGetGetMethod, boolean isGetSetMethod ) 
                    throws ClassNotFoundException{
    
        Class pluginClass = this.loadClass(pluginName);
        
        Method[] pluginMethods = pluginClass.getMethods();
        
        Vector selectedMethodNames = new Vector();
        
        for (int i=0; i<pluginMethods.length; i++) {
            
            String methodName = pluginMethods[i].getName();
           
            if ((isGetGetMethod && methodName.indexOf("get")>=0) ||
                (isGetSetMethod && methodName.indexOf("set")>=0)) {
                    
                    selectedMethodNames.addElement(methodName);
            }
        }
        
        String[] methodNames = new String[selectedMethodNames.size()];
        
        for (int i=0; i<selectedMethodNames.size();i++) {
            methodNames[i] = (String)selectedMethodNames.elementAt(i);
        }
        return methodNames;
            
        
    }
    
    /** Returns the method parameters separated with ,
    *
    *   @param  String pluginName   The plugin to load
    *   @param  String methodName   The method that hold the parameters
    *   @return String
    */
    public String getPluginMethodParameters(String pluginName, String methodName ) 
                    throws ClassNotFoundException{
    
        Class pluginClass = this.loadClass(pluginName);
        
        Method[] pluginMethods = pluginClass.getMethods();
        
       
        for (int i=0; i<pluginMethods.length; i++) {
            
            if (methodName.equals(pluginMethods[i].getName())) {
                
                Class[] classes = pluginMethods[i].getParameterTypes();
                
                if (classes.length>0) {
                    return classesToString(classes);
                }
            }
           
        }
        
        return "";
        
    }
    
    
    /** Returns the constructor parameters separated with ,
    *
    *   @param  String pluginName   The plugin to load
    *   @return String
    */
    public String getPluginConstructorParameters(String pluginName ) 
                    throws ClassNotFoundException{
    
        Class pluginClass = this.loadClass(pluginName);
        
        Constructor[] pluginConstructors = pluginClass.getConstructors();
        
        for (int i=0; i<pluginConstructors.length; i++) {
            
            Class[] classes = pluginConstructors[i].getParameterTypes();
                
            if (classes.length>0) {
                return classesToString(classes);
            }
           
        }
        
        return "";
        
    }
    
    /** Return a string containing the class names separated with a ,
    *
    *
    *   @return String
    */
    protected  String   classesToString(Class[] classes){
    
        String retString = "";
        
        for (int i=0; i<classes.length; i++) {
            if (retString.length()>0) {
                retString += ",";
            }
            String tempStr = classes[i].toString();
            retString += tempStr.substring(tempStr.lastIndexOf(".")+1,tempStr.length());
        }
        if (retString.lastIndexOf(";")>=0) {
            retString = retString.substring(0,retString.lastIndexOf(";"));
        }
        return retString;
    }
    
    protected URL[] getUrls() throws MalformedURLException{
    
    
            Vector temp = new Vector();
            File tempDir = new File(this.pluginPath);
            if (tempDir.exists()){
                String[] filesInDir = tempDir.list();
                
                for (int i = 0; i < filesInDir.length ;i++){
                        
                    if (filesInDir[i].toUpperCase().indexOf(".JAR")>=0) {
                        
                        // Save class loader so that we can restore later
                        temp.addElement(new URL("file:"+this.pluginPath+"/"+filesInDir[i]));
                            
                    }
                        
                }
                temp.addElement(new URL("file:"+this.pluginPath+"/"));

                
            }
        
        URL[] urls = new URL[temp.size()];
        for (int i=0;i<temp.size();i++) {
           urls[i] = (URL)temp.elementAt(i);
        }
        return urls;
            
    }
   
}