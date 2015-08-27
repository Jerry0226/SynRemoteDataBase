package com.chm.bll;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chm.common.CHMConnection;
import com.chm.common.ConfigManager;

public class TimestenStrategy extends SynRemotingStrategy
{

	@Override
	public Set<String>  genrTables(String tableList,Connection con,File logFile) throws Exception 
	{
	    Set<String> tables = new HashSet<String>();
		/**如果是所有的表，则同步此用户下所有的表记录*/
		if(tableList!=null && tableList.equalsIgnoreCase("ALLTABLES"))
		{
			if(con == null)
			{
				con = CHMConnection.getConnection("SOURCEDATABASE");
			}
			Map<String, String> list = new HashMap<String, String>();
			list = (Map<String, String>) ConfigManager.CfgMap.get("SOURCEDATABASE");
			ResultSet rs = null;
			String userName = list.get("userName").toLowerCase();
			String sql = new String("select tblname from tables where lower(tbLowner) = '"+userName+"'");
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
		    while(rs.next())
		    {
		    	tables.add(rs.getString("tblname"));
		    }
		    rs.close();
		}
		else
		{
			/**如果不是所有的表，那么把需要同步的表放入到容器中*/
			if(tableList!=null)
			{
				for(String str:tableList.split(","))
				{
					tables.add(str);
				}
			}
		}
		return tables;
	}

}
