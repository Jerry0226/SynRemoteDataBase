package com.chm.bll;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.chm.common.CHMConnection;

public class MysqlStrategy extends SynRemotingStrategy
{

	@Override
	public Set<String> genrTables(String tableList,Connection con,File logFile) throws Exception 
	{
	    Set<String> tables = new HashSet<String>();
		/**如果是所有的表，则同步此用户下所有的表记录*/
		if(tableList!=null && tableList.equalsIgnoreCase("ALLTABLES"))
		{
			if(con == null)
			{
				con = CHMConnection.getConnection("SOURCEDATABASE");
			}
			ResultSet rs = null;
			DatabaseMetaData dm = null;
			String []pram = new String[1];
			pram[0] = (String) "table";
    		dm = con.getMetaData();
    		System.out.println("同步用户："+dm.getUserName()+" 下所有的表");
    		writeLog("同步用户："+dm.getUserName()+" 下所有的表",logFile);
		    rs = dm.getTables(null, null, null, pram);//查找所有的表
		    while(rs.next())
		    {
		    	tables.add(rs.getString(3));
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
