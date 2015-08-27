package com.chm.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class CHMConnection
{
	public final static String DRIVERCLASS = "DriverClass";
	public final static String DBURL = "databaseURL";
	public final static String DBPASSWORD = "password";
	public final static String DBUSERNAME = "userName";
	public static Connection getConnection(String databasetype) throws Exception
	{
		Map<String, String> list = new HashMap<String, String>();
		
		list = (Map<String, String>) ConfigManager.CfgMap.get(databasetype);
		Connection conn = null;
		Class.forName(list.get(DRIVERCLASS));
		Properties conProps = new Properties();
        conProps.put("user", list.get(DBUSERNAME));
        conProps.put("password", list.get(DBPASSWORD));
        conProps.put("includeSynonyms","true");
		
        conn = DriverManager.getConnection(list.get(DBURL),  conProps);
		
		return conn;
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			CHMConnection.getConnection("");
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
}
