/*
 * Created on 24.8.2004
 *
 */
package com.distocraft.dc5000.etl.engine.common;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author savinen
 *
 */
public class Tags 
{

    /**
     * 
     * tagpPairDelimiter delimited tag pairs from string.
     * Each tag contains name and value devided by '=' (name=value).
     * Identifier is added to each pairs name.
     * 
     * returns map containing tall found tag pairs.
     * 
     * @param identifier
     * @param dataString
     * @return
     * @throws Exception
     */
    public static HashMap GetTagPairs(String identifier,String tagpPairDelimiter, String dataString) throws Exception
    {
		/* Read ACTION_CONTENT in to a vector */		
    	HashMap tagMap = new HashMap();
		StringTokenizer tokens = new StringTokenizer(dataString,tagpPairDelimiter);
		while (tokens.hasMoreElements())
		{
	
			/* get the tags name and value */
			String token = tokens.nextToken();
			
			/* Name is the first part of the string, before '=' */
			String tagName = token.substring(0,token.indexOf("="));
			
			/* Value is the second part of the sttring, after the '=' */
			String tagValue = token.substring(token.indexOf("=")+1);
			
			tagMap.put(identifier+tagName,tagValue);

		}
 	
		return tagMap;
		
    }
    

	
}
