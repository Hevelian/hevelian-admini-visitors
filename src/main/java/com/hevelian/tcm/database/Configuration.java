package com.hevelian.tcm.database;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.Context;

public class Configuration {
	
	private Context ctx 						= null;
	private HashMap<String,String> config 		= new HashMap<String,String>();
	private String homePath						= null;

	private final String CONTEXT_NAME			= "hevelian-tcm";
	
	public final static String TAG_DATABASE			= "file_h2_database";
	
	public Configuration() {
		
		try {
		    
			ctx = new InitialContext();
			try {
				homePath = (String) ctx.lookup(CONTEXT_NAME);
			} catch(Exception e) {
				ctx = (Context) ctx.lookup("java:comp/env");
				homePath = (String) ctx.lookup(CONTEXT_NAME);
			}
			
			if(homePath==null) {
				System.out.println("HEVELIAN: ERROR: context 'hevelian-tcm' not found in JNDI.");
				return;
			}
			
			/* own configuration and settings stuff */
			config.put(TAG_DATABASE, homePath);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets a given property to a given value. Allows overrides on defined properties or adding new properties
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value) {
		config.put(name, value);
	}
	
	/**
	 * Returns the value of a given property of the JNDI definitions for ExoniteJ
	 * @param _name
	 * @return
	 */
	public String getProperty(String _name) {
		return (String) config.get(_name);
	}
	
}
