package com.chm.common;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: pridigy</p>
 *
 * <p>Description: 定义一些常量</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: single</p>
 *
 * @author cheng.haiming
 * @version 1.0
 */
public final class Constants {

    private Constants() {
    }

    public static int getTIME() {
		return TIME;
	}

    
	/** 配置文件节点标签名*/
    public static final String CONFIGITEM = "ConfigItem";
    
    /** 配置文件属性名称标签名*/
    public static final String PARAMNAME = "ParamName";
    
    /** 配置文件属性值标签名*/
    public static final String PARAMVALUE = "ParamValue";
    
    
    /** 配置文件单个属性定义的标签名*/
    public static final String CONFIGPARAM = "ConfigParam";
    
    
    /** 配置文件单个属性名称的标签名*/
    public static final String CONFIGNAME = "ConfigName";
    
    
    /** 配置文件名称*/
    public static final String CONFIG_FILENAME = "config.xml";
    
    /** 前台被修改配置文件存放的路径*/
    
    public static final String WEBCONFIGPATH = "WebModifyConfigPath";
    
    /** AUTO系统的配置web修改项配置说明*/
    
    public static final String WEBCONFIGNAME = "WebConfigName";
    
    /**oracle数据库类型*/
    public static final String ORACLETYPE = "oracleDB";
    
    /**mysql数据库类型*/
    public static final String MYSQLTYPE = "mysqlDB";
    
    /**sqlserver数据库类型*/
    public static final String SQLSERVERTYPE = "sqlserverDB";
    
    /**sqlLogFilePath数据库类型*/
    public static final String sqlLogFilePath = "sqlLogFilePath";
    
    /**sqllog 日志的编码方式*/
    public static final String LOG_ENCODE_FROM = "LOG_ENCODE_FROM";
    
    /**sqllog 日志的编码方式*/
    public static final String LOG_ENCODE_TO = "LOG_ENCODE_TO";
    
    
    /**sqllog 是否需要转码*/
    public static final String LOG_ENCODE_FLAG = "LOG_ENCODE_FLAG";
    
    
    
    /** 自动编译前台编译曲线的分类*/
    public static final String DAILY_BUILD_VERSION ="DAILY_BUILD_VERSION";
    
    /**是否需要记录sql日志*/
    public static final Boolean logFlag = true;
    
    /** 控制sql日志打印的sql语句的时间 */
	private static final int TIME = 0; // 默认为0，表示所有的sql都打印
    
	/**公共域*/
	public static final String domain = "chm.test.dal."; 
	
	/**公告区*/
	public static final String NOTICE = "Notice";
	
	/**任务结果信息*/
	public static final String TASKRESULT = "TaskResult";
	
	/**任务状态*/
	public static final String TASKSTATE = "TaskState";
	
	/**任务详细信息*/
	public static final String TASKDETAIL = "TaskDetail";
	
	/**任务模板*/
	public static final String TASKFLOWTEMPLATE = "Task_flow_template";
	
	/**任务节点*/
	public static final String TASKNODETEMPLATE = "Task_node_template";
	
	
	/**菜单*/
	public static final String MENU = "Menu";
	
	/**任务消息*/
	public static final String TASKNOTICE = "TaskNotice";
	
	/**员工消息任务关联表*/
	public static final String STAFFTASK = "StaffTask";
	
	
	
	
	
	/**查询自动编译通过率时，统计的是前多少天的通过率*/
	public static final int DAILYBUILDBEFOREDAYS = 10;
	
	/**数据库数据源编码*/
	public static final String SOURCEENCODE = "utf-8";
	
	/**数据库数据目标编码*/
	public static final String DESENCODE = "utf-8";
	
	/**informix数据库类型*/
	public static final String INFORMIX_INT = "int";
	
	public static final String INFORMIX_DECIMAL = "decimal";
	
	public static final String INFORMIX_CHAR = "char";
	
	public static final String INFORMIX_DATETIMEYEAR = "datetime year to second";
	
	public static final String INFORMIX_NVARCHAR = "nvarchar";
	
	public static final String INFORMIX_VARCHAR = "varchar";
	
	public static final String INFORMIX_NCHAR = "nchar";
	
	public static final String INFORMIX_DATE = "date";
	
	public static final String V71 = "V71";
	public static final String V73 = "V73";
	
	public static final Map<String, Integer> INF_INT = new HashMap<String, Integer>();
	
	static
	{
		INF_INT.put(INFORMIX_INT, 0);
		INF_INT.put(INFORMIX_CHAR,1);
		INF_INT.put(INFORMIX_NCHAR,2);
		INF_INT.put(INFORMIX_VARCHAR,3);
		INF_INT.put(INFORMIX_NVARCHAR,4);
		INF_INT.put(INFORMIX_DECIMAL,5);
		INF_INT.put(INFORMIX_DATE,6);
		INF_INT.put(INFORMIX_DATETIMEYEAR,7);
	}
	
	/**informix数据库操作中，拼接字符串时，表名和后面字符串之间特殊字符标识*/
	public static final String INFORMIX_TABLINK = "&&";
	
	/**informix数据库操作中，拼接字符串时，字段和字段类型之间的特殊间隔标识*/
	public static final String INFORMIX_COLTYPELINK = "@";
	
	/**informix数据库操作中，拼接字符串时，字段和字段之间的特殊间隔标识*/
	public static final String INFORMIX_COLLINK = "@@";
	
	/**informix数据库操作中，拼接字符串时，字段和实际数据之间的特殊间隔标识*/
	public static final String INFORMIX_DATALINK = "---";
	
	
	
	
	
}
