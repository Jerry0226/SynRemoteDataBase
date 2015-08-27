package com.chm.bll;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chm.common.CHMConnection;
import com.chm.common.ConfigManager;

/**
 * 
 * 客户端类，用来接收客户单参数以及处理结果信息</br>
 * 此工具的数据同步机制，不考虑外键关联关系，先后顺序可以通过表字段的输入来控制
 * @author chm
 * 
 */
public class SynDataMain 
{
	
	static SynRemotingStrategy synRemStr = null;
	static File logFile ;
	static File logFileStat ;
	
	/**每批次最大的记录提交阀值*/
	private static final int MAXNUM = 1000;
	
	/**每次从源数据库中取的记录总数*/
	private static final int SELECTMAXNUM = 20000;
	
	/**每次同步数据，单线程可以同步的最大记录数，超过此记录数，将启动新的线程进行同步操作*/
	private static int  SINGLETHREADMAXNUM = 100000;
	
	/**
	 * 当前仅数据源为oracle 可以并发同步数据同时可以同步
	 * ResultSet   columns   =   databaseMetaData.getColumns(null,   schema,table,   null); 
	 * ORA-01000:   超出打开游标的最大数
	 * http://topic.csdn.net/t/20040331/10/2907399.html
	 * 
	 * args[0] 配置文件config.xml的路径
	 * args[1] 需要同步的表名，如果同步此用户下的所有的表记录，输入：ALLTABLES，如果是其他名称则只同步给定的表名，表名之间用逗号分割，如：doc,role
	 * args[2] 启动的同步线程个数，任务分配不精细到记录数来分配任务线程，直接根据表的个数来分配
	 * args[3] 单表单个线程最大的导入记录数，当单表的记录超过此值时，将启动多个线程进行操作，默认为100000
	 * @param args 
	 */
	public static void main(String[] args)
	{
//		args = new String[3];
//		args[0] = "F:/v73webJava" ;
//		args[1] = "ACCNBR_ACM_CYCLE,ACCT_ACM,ACCT_ACM_CYCLE,ACCT_ACM_DAILY,ACM_CYCLE,ACM_CYCLE_BFE,ASYN_CALL" ;
//		args[1] = "ASYN_CALL_BATCH,BAL,BAL_HIS,BAL_SHARE_HIS,BATCH_TIME_HINT,BUNDLE_ACM,BUNDLE_ACM_CYCLE,BUNDLE_ACM_DAILY,CC_ACCT_ACM" ;
//		args[1] = "CC_SUBS_ACM,OCS_SESSION,PCRF_SESSION,SESSION_MSCC_KEY,SQL_TIME_HINT,SUBS_ACM,SUBS_ACM_CYCLE,SUBS_ACM_DAILY,SUBS_CTRL_ACM_CYCLE";
		
//		args[1] = "ACCT_ACM";
//		args[2] = "20" ; 
		
		if(args.length>2)
		{
			try
			{
				Date date = new Date();
				logFile = new File("./"+"logFile"+date.getTime()+".log");
				logFileStat = new File("./"+"logFile"+date.getTime()+"_stat.log");
				Connection con_source = null;
				Set<String> tables = new HashSet<String>();
				
				/**设置配置文件的路径*/
				ConfigManager.setFilePath("", args[0]);
				ConfigManager.getCFGMAP();
				
				con_source = CHMConnection.getConnection("SOURCEDATABASE"); // 连接到数据库
				
				/**如果是所有的表，则同步此用户下所有的表记录*/
				String dataType = (String)ConfigManager.CfgMap.get("SOURCEDATATYPE");
				
				if(dataType.equalsIgnoreCase("MYSQL"))
				{
					synRemStr = new MysqlStrategy();
				}
				else if(dataType.equalsIgnoreCase("TIMESTEN"))
				{
					synRemStr = new TimestenStrategy();
				}
				else //(dataType.equalsIgnoreCase("ORACLE"))
                {
                    synRemStr = new OracleStrategy();
                }
				
				tables = synRemStr.genrTables(args[1], con_source, logFile);
				
				if(args.length == 4) {
				    SINGLETHREADMAXNUM = Integer.valueOf(args[3]);
				}
				
				
				SynDataMain synData = new SynDataMain();
				synData.execSynRemoteData(Integer.valueOf(args[2]),tables);
				
				con_source.close();
				
			}
			  catch (SQLException ex) 
			  {
				  ex.printStackTrace();
				  recordLog("ERROR Operation fail: \n"+ex.getMessage(),logFile, true);
				  
				  System.out.println("SQLException:");
				  while (ex != null) 
				  {
					  System.out.println("Message:" + ex.getMessage());
					  ex = ex.getNextException();
				  }
			  } 
			  catch (Exception e) 
			  {
			      recordLog("ERROR Operation fail: \n"+e.getMessage(),logFile, true);
				  e.printStackTrace();
			  }
		}
		else
		{
			System.out.println("参数的个数必须是两个:");
			System.out.println("args[0] 配置文件config.xml的路径");
			System.out.println("args[1] 需要同步的表名，如果同步此用户下的所有的表记录，输入：ALLTABLES，如果是其他名称则只同步给定的表名，表名之间用逗号分割，如：doc,role");
			System.out.println("args[2] 同步操作启动的线程个数");
			System.out.println("args[3] 单表单个线程最大的导入记录数，当单表的记录超过此值时，将启动多个线程进行操作");
			
			
		}
	}
	
	/***
	 * 执行同步
	 * @param threadCount 线程数
	 * @param tableList table 列表
	 */
	public void execSynRemoteData(int threadCount, Set<String> tableList) {
	    
	    if(tableList.size() <= 0 ) {
	        System.out.println("被同步的表个数为零，直接退出");
	        return;
	    }
	    if ( threadCount <= 0  ) {
	        threadCount = 1;
	    }
	    recordLog("syn data table list: " + tableList ,logFile, true);
	    //不考虑记录数，暂时仅考虑表的个数进行线程的分配
        if (tableList.size() > 0) {
            
            if(threadCount > tableList.size()) {
                for(String tableNametemp: tableList) {
                    String[] tablesTemp = new String[1];
                    tablesTemp[0] = tableNametemp;
                    Thread td = new Thread(new ExecThread(Arrays.asList(tablesTemp)));
                    td.start();
                }
            }
            else {
                int threadTableCount = tableList.size() / threadCount;
                for(int i = 0; i < threadCount; i++) {
                    String[] tablesTemp;
                    if (i < threadCount -1) {
                       tablesTemp = new String[threadTableCount];
                       System.arraycopy(tableList.toArray(), i*threadTableCount, tablesTemp, 0, threadTableCount);
                    }
                    else {
                       tablesTemp = new String[threadTableCount + tableList.size()%threadCount];
                       System.arraycopy(tableList.toArray(), i*threadTableCount, tablesTemp, 0, threadTableCount + tableList.size()%threadCount);
                    }
                    
                    Thread td = new Thread(new ExecThread(Arrays.asList(tablesTemp)));
                    td.start();
                }
            }
            
            
        }
	}
	
	/***
	 * 日志文件处理
	 * @param logFile 日志文件的路径
	 * @param content 待写入的内容
	 * @param isApp 是否是写入文件尾部
	 */
	public synchronized static void recordLog(String content, File logFile, boolean isApp) {
	    synRemStr.writeLog(content, logFile, isApp);
	}
	
	/**
	 * 具体的执行类，用来根据给定的表来具体做同步数据操作，系统可以设定具体有多少线程来进行同步操作
	 * @author chm
	 *
	 */
	class ExecThread implements Runnable {
	    
	    public ExecThread(List<String> tables) {
	        this.tableNameList = tables;
	    }
	    
	    /**此线程需要同步的表名集合*/
	    private List<String> tableNameList = new ArrayList<String>();
	    
        public void run() {
            try
            {
//                System.out.println(Thread.currentThread().getName() + " 同步： " + tableNameList + " 表");
                
                Connection con_source = null;
                Connection con_des = null;
                
                con_source = CHMConnection.getConnection("SOURCEDATABASE"); // 连接到数据库
                
                /***是否删除目标数据库表中已经存在的记录*/
                boolean isDelDestTableRecords = Boolean.valueOf((String)ConfigManager.CfgMap.get("ISDelDestTableRecords"));
                
                System.out.println(tableNameList);
                
                for(String tb : tableNameList)
                {
                  try
                  {
                      
                       long startTime =  System.currentTimeMillis();
                       int sumRow = 0;
                       ResultSet rs = null;
                       con_des = CHMConnection.getConnection("DESDATABASE"); // 连接到数据库,目标数据库
                       con_des.setAutoCommit(false);
                       
                       StringBuffer sql_count_temp = new StringBuffer("select count(*) from " + tb);
                       Statement stmt_count = con_source.createStatement();
                       rs = stmt_count.executeQuery(sql_count_temp.toString());
                       if(rs.next()) {
                           sumRow = rs.getInt(1);
                       }
                       if (sumRow<=0) {
                           recordLog("table: " + tb + " sumCount = 0 don't syn data ",logFile, true);
                           continue;
                       }
                       else {
                           
                           //处理目标表中已经存在的记录
                           if (isDelDestTableRecords) {
                               
                               PreparedStatement pstat_delete = con_des.prepareStatement("delete from "+tb);
                               int deleteCount = pstat_delete.executeUpdate();
                               pstat_delete.close();
                               System.out.println("delete original table: " + tb + " " + deleteCount);
                               recordLog("delete original table: " + tb + " " + deleteCount,logFile, true);
                           }
                           
                           recordLog("table: " + tb + " sumCount： " + sumRow + " , begin syn data...",logFile, true);
                           
                           //获取表的结构
                           StringBuffer sql_temp = new StringBuffer("select * from " + tb + " where 1=2");
                           Statement stmt = con_source.createStatement();
                           ResultSet rsTemp = stmt.executeQuery(sql_temp.toString());
                           ResultSetMetaData rsmd = rsTemp.getMetaData();
                           //因为目标数据被加锁了，所以并发写入性能没有多少优化
                           if (sumRow > SINGLETHREADMAXNUM) {
                               System.out.println("beginTime: " + System.currentTimeMillis());
                               muliThreadSynData(tb, sumRow);
                           }
                           else {
                               synData4SingleThread(tb, rsmd, con_des, con_source, 0, sumRow);
                           }
                           
                       }
                       System.out.println("table: " + tb + " duration： " + ((System.currentTimeMillis() - startTime)/1000.0) + " s");
                       recordLog("table: " + tb + " duration： " + ((System.currentTimeMillis() - startTime)/1000.0) + " s",logFile, true);
                       con_des.commit();
                       con_des.close(); 
                  }
                  catch (Exception e) 
                  {
                      recordLog("ERROR Table "+tb+" Operation fail: "+e.getMessage(),logFile, true);
                      e.printStackTrace();
                  }
                }
               con_source.close();
              } 
              catch (SQLException ex) 
              {
                  ex.printStackTrace();
                  recordLog("ERROR Operation fail: \n"+ex.getMessage(),logFile, true);
                  
              } 
              catch (Exception e) 
              {
                  recordLog("ERROR Operation fail: \n"+e.getMessage(),logFile, true);
                  e.printStackTrace();
                  System.out.println("IOException");
              }
        }
        
        
        
        /***
         * 使用多线程同步执行单表数据同步
         * @param tb
         * @param sumRow
         * @param rsmd
         * @throws Exception
         */
        public void muliThreadSynData(String tb, int sumRow) throws Exception {
            
            int countTemp = sumRow/SINGLETHREADMAXNUM;
            for (int i=0;i<countTemp;i++) {
                SyndataMuliThread smt = new SyndataMuliThread(tb,(SINGLETHREADMAXNUM*i),(SINGLETHREADMAXNUM*(i+1)));
                new Thread(smt).start();
                
            }
            if (sumRow%SINGLETHREADMAXNUM > 0) {
                SyndataMuliThread smt = new SyndataMuliThread(tb,(SINGLETHREADMAXNUM*countTemp),sumRow);
                new Thread(smt).start();
            }
        }
	    
	}
	
	
	class SyndataMuliThread implements Runnable {
	    
	    private String tb;
	    private int beginRow;
	    private int endRow;
	    /**每一个线程开始同步的起始行数*/
	   
	    public SyndataMuliThread(String tb,int beginRow ,int endRow){
	        this.tb = tb;
	        this.beginRow = beginRow;
	        this.endRow = endRow;
	    }

        public void run() {
            Connection con_source = null;
            Connection con_des = null ;
            try {
                con_source = CHMConnection.getConnection("SOURCEDATABASE"); // 连接到数据库
                con_des = CHMConnection.getConnection("DESDATABASE"); // 连接到数据库,目标数据库
                con_des.setAutoCommit(false);
                
                StringBuffer sql_temp = new StringBuffer("select * from " + tb + " where 1=2");
                Statement stmt = con_source.createStatement();
                ResultSet rsTemp = stmt.executeQuery(sql_temp.toString());
                ResultSetMetaData rsmd = rsTemp.getMetaData();
                
                System.out.println(Thread.currentThread().toString() + " beginSynData from " + beginRow + " to " + endRow + " rows");
                recordLog(Thread.currentThread().toString() + " beginSynData from " + beginRow + " to " + endRow + " rows",logFile, true);
                
                synData4SingleThread(tb, rsmd, con_des, con_source, beginRow, endRow);
                
                
                
                if(!con_des.getAutoCommit()) {
                    con_des.commit();
                }
                con_source.close();
                con_des.close();
                
                System.out.println("endTime: " + System.currentTimeMillis());
                
            }
            catch (Exception e) {
                try {
                    con_source.close();
                    con_des.close();
                }
                catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
                recordLog("ERROR Operation fail: \n"+e.getMessage(),logFile, true);
                e.printStackTrace();
                System.out.println("IOException");
            }
            
           
            
        }
	    
	}
	
	/**
     * 左开右闭区间
     * @param tb
     * @param minValue
     * @param maxValue
     * @param con_source
     * @return
     * @throws SQLException
     */
    public ResultSet querySourceData(String tb, int minValue, int maxValue, Connection con_source) throws SQLException {
        StringBuffer sql_data_temp = new StringBuffer();
        
        if(maxValue > 0 &&  maxValue > minValue) {
            sql_data_temp.append("select * from ( \n");
            sql_data_temp.append("select tt.*,rownum as rwn from ( \n");
            sql_data_temp.append("select * from ").append(tb).append(" \n");
            sql_data_temp.append(") tt where rownum <= ").append(maxValue).append(") t where t.rwn > ").append(minValue);
            
            System.out.println(sql_data_temp);
//            recordLog(sql_data_temp.toString(), logFile, true);
        }
        else {
            sql_data_temp.append("select * from " + tb);
        }
        Statement stmt_data = con_source.createStatement();
        ResultSet rs_data = stmt_data.executeQuery(sql_data_temp.toString());
        return rs_data;
        
    }
    
    public void synData(String tb, ResultSetMetaData rsmd, Connection con_des, ResultSet rs, int beginNum) throws SQLException {
        String insertSql_temp = synRemStr.genrInsertSql(rsmd,tb);//根据此数据库的对象得到insert语句
        int len;
        len = rsmd.getColumnCount();
        PreparedStatement pstat_insert = con_des.prepareStatement(insertSql_temp);
        
        
        
        int sum_count = 0;
        int count = 0;
        while(rs.next())
        {
            sum_count++;
            count ++;
            for(int i = 1; i <= len; i++)
            {
                
                //每一行的数据根据类的类型来设置值，最后一次提交
                //在转换过程中，如果数据oracle的数据类型是date 类型，需转换为java.sql.Timestamp 类型，需进行插入，否则会丢失时分秒
                
                if(rsmd.getColumnType(i) == java.sql.Types.DATE) {
                    pstat_insert.setTimestamp(i, rs.getTimestamp(i));
                }
                else {
                    pstat_insert.setObject(i, rs.getObject(rsmd.getColumnName(i)), rsmd.getColumnType(i));
                }
                
                
            }
            pstat_insert.addBatch();
            if(count >= MAXNUM) {
                pstat_insert.executeBatch();
                count = 0;
                System.out.println("INFO  Table "+tb+": "+(beginNum + sum_count)+" rows affected");
                recordLog("INFO  Table "+tb+": "+(beginNum + sum_count)+" rows affected",logFile, true);
            }
            
        }
        pstat_insert.executeBatch();
        System.out.println("INFO  Table "+tb+": "+(beginNum + sum_count)+" rows affected");
        recordLog("INFO  Table "+tb+": "+(beginNum + sum_count)+" rows affected",logFile, true);
        
        //每个表的每个批次提交一次
        if(!con_des.getAutoCommit()) {
            con_des.commit();
        }
        
    }
    
    
    /**
     * 单个线程进行的数据同步
     * @param sumRow 
     * @param tb
     * @param rsmd
     * @param con_des
     * @param isDelDestTableRecords
     * @param con_source
     * @throws SQLException
     */
    public void synData4SingleThread(String tb,ResultSetMetaData rsmd, Connection con_des, 
        Connection con_source, int beginRow, int endRow) throws SQLException {
        int sumRow = endRow - beginRow;
        if(sumRow < SELECTMAXNUM) {
            synData(tb, rsmd, con_des, querySourceData(tb, beginRow, endRow, con_source), beginRow);
        }
        else {
            int countTemp = sumRow/SELECTMAXNUM;
            for (int i=0;i<countTemp;i++) {
                ResultSet rs_data = querySourceData(tb, (SELECTMAXNUM*i) + beginRow,(SELECTMAXNUM*(i+1)) + beginRow, con_source);
                synData(tb, rsmd, con_des, rs_data,SELECTMAXNUM*i + beginRow);
            }
            if (sumRow%SELECTMAXNUM > 0) {
                ResultSet rs_data = querySourceData(tb, (SELECTMAXNUM*countTemp) + beginRow ,sumRow + beginRow, con_source);
                synData(tb, rsmd, con_des, rs_data,SELECTMAXNUM*countTemp + beginRow);
            }
        }
    }
	
}
