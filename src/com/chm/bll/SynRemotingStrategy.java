package com.chm.bll;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Set;

/**
 * 数据库同步策略类，此类提供日志文件的统一操作方法，对于select语句和insert 语句的生成由子类生成</br>
 * 以及
 * @author chm
 *
 */
public abstract class SynRemotingStrategy
{
	
	/**
	 * 根据table的参数得到表列表，如输入的是ALLTABLES，那就需要把所有表都同步,待子类实现
	 * @param tableList  要被同步的表
	 * @param con	数据库连接
	 * @param logFile 日志文件
	 * @return List<tables> 返回表的集合
	 */
	public abstract Set<String>  genrTables(String tableList,Connection con,File logFile)throws Exception;
	
	/**
	 * <b>用来根据ResultSetMetaData对象和表名生成查询语句</b><br>
	 * @param rsmd
	 * @param tableName
	 * @return select 形式的sql语句
	 * @throws SQLException
	 */
	public String genrSelectSql(ResultSetMetaData rsmd,String tableName) throws SQLException
	{
		 int len;
	     len = rsmd.getColumnCount();
	     StringBuffer str_bef = new StringBuffer();
	     str_bef.append("select ");
	     for(int i = 1;i<= len ;i++)
	     {
	    	 str_bef.append(rsmd.getColumnName(i));
	    	 if(i!= len)
	    	 {
	    		 str_bef.append(","); 
	    	 }
	     }
	     str_bef.append(" from "+tableName);
		return str_bef.toString();
	}
	
	/**
	 * <b>用来根据ResultSetMetaData对象和表名生成插入语句</b><br>
	 * @param rsmd
	 * @param tableName
	 * @return insert形式的sql语句
	 * @throws SQLException
	 */
	public String genrInsertSql(ResultSetMetaData rsmd,String tableName) throws SQLException
	{
		int len;
	     len = rsmd.getColumnCount();
	     StringBuffer str_bef = new StringBuffer();
	     StringBuffer str_aft = new StringBuffer();
	     str_bef.append("insert into "+tableName +"(");
	     str_aft.append(" values(");
	     for(int i = 1;i<= len ;i++)
	     {
	    	 str_bef.append(rsmd.getColumnName(i));
	    	 str_aft.append("?");
	    	 if(i!= len)
	    	 {
	    		 str_aft.append(",");
	    		 str_bef.append(","); 
	    	 }
	     }
	     str_aft.append(") ");
	     str_bef.append(") ");
	     str_bef.append(str_aft);
		return str_bef.toString();
	}
	
	
	/**
	 * <b>根据传入的文件对象和文件的消息进行日志操作</b><br>
	 * 
	 * @param message
	 * @param logFile
	 */
	public void writeLog(String message,File logFile)
	{
		try
		{
			OutputStream onput = new FileOutputStream(logFile,true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(onput));
			bw.write(message+"\n");
			bw.flush();
			bw.close();
		}
		catch (Exception e) 
		{
			System.out.println("写入日志失败！");
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	
	/**
     * <b>根据传入的文件对象和文件的消息进行日志操作</b><br>
     * 
     * @param message
     * @param logFile
     * @param isApp 在文件尾写入
     */
    public void writeLog(String message,File logFile, boolean isApp)
    {
        try
        {
            OutputStream onput = new FileOutputStream(logFile,isApp);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(onput));
            bw.write(message+"\n");
            bw.flush();
            bw.close();
        }
        catch (Exception e) 
        {
            System.out.println("写入日志失败！");
            e.printStackTrace();
            // TODO: handle exception
        }
        
    }
}
