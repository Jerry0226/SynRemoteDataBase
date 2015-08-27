package com.chm.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//import org.apache.log4j.Logger;

public class ConfigManager 
{ 
	public static Map<String, Object> CfgMap = new HashMap<String, Object>();
	public static Properties ModifyConfigFile = new Properties();
	private static String filePath;
	
	
	public static void setFilePath(String auto_home,String defaultPath)
	{
		filePath = SysProperty.getPropertyAsString(auto_home, defaultPath);
		System.out.println(filePath);
	}
	public static Map<String, Object> getCFGMAP()
	{
		CfgInit.initCfg(filePath);
		return CfgMap;
	}
	
	public static Properties getModifyConfigFile()
	{
		
		try 
		{
				File file = new File(filePath + "/"+CfgMap.get(Constants.WEBCONFIGNAME));
				FileInputStream fins = new FileInputStream(file);
				ModifyConfigFile.load(fins);
				return ModifyConfigFile;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
}
