package com.chm.common;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

//import org.apache.log4j.Logger;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

/**
 *
 * <p>Title: Prodigy 客户资料服务</p>
 *
 * <p>Description: 系统属性帮助类,用于获得系统环境变变量的属性值.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 */
public final class SysProperty {

    /** WINDOW操作系统. */
    public static final int WINDOW_OS = 1;

    /** UNIX操作系统. */
    public static final int UNIX_OS = 2;

    /** 操作系统类型. */
    private static int OS_TYPE;

    private static String[] winCmd = new String[] { "CMD", "/C", "SET" };

    private static String[] unixCmd = new String[] { "env" };

    /** 日志记录对象. */
//    private static Logger logger = Logger.getLogger(SysProperty.class);

    static {
        String osName = System.getProperty("os.name").toUpperCase();
        if (osName.indexOf("WIN") > -1) {
            OS_TYPE = WINDOW_OS;
        }
        else {
            OS_TYPE = UNIX_OS;
        }
    }

    /**
     * 默认构造器.
     */
    public SysProperty() {
    }

    /**
     * 得到属性表的属性值.
     * @param prop 属性表.
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return int 属性值
     */
    public static int getPropertyAsInt(Properties prop, final String key, final int def) {
        int retVal = def;
        String value = getPropertyAsString(prop, key, new Integer(def).toString());

        try {
            retVal = Integer.parseInt(value);
        } catch (java.lang.NumberFormatException e) {
            numberFormartExceptionMsg(e);
        }

        return retVal;
    }

    /**
     * 从系统环境变量中得到int型的属性值.
     * @param key String 主键
     * @param def int 默认值
     * @return int
     */
    public static int getPropertyAsInt(final String key, final int def) {
        return getPropertyAsInt(getPropertys(), key, def);
    }


    /**
     * 得到属性表里的属性值.
     * @param prop Properties 属性表
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return float 属性值
     */
    public static float getPropertyAsFloat(Properties prop, final String key, final float def) {
        float retVal = def;
        String value = getPropertyAsString(key, new Float(def).toString());

        try {
            retVal = Float.parseFloat(value);
        } catch (java.lang.NumberFormatException e) {
            numberFormartExceptionMsg(e);
        }

        return retVal;
    }

    /**
     * 从系统环境变量里得到float型的属性值
     * @param key String 主键
     * @param def float 默认值.
     * @return float
     */
    public static float getPropertyAsFloat(final String key, final float def) {
        return getPropertyAsFloat(getPropertys(), key, def);
    }

    /**
     * 得到属性表里的属性值.
     * @param prop Properties 属性表
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return double 属性值
     */
    public static double getPropertyAsDouble(Properties prop, final String key,
                                      final double def) {
        double retVal = def;
        String value = getPropertyAsString(key, new Double(def).toString());

        try {
            retVal = Double.parseDouble(value);
        } catch (java.lang.NumberFormatException e) {
            numberFormartExceptionMsg(e);
        }

        return retVal;
    }

    /**
     * 重系统环境变量中得到double型的属性值.
     * @param key String 属性键
     * @param def double 默认值
     * @return double
     */
    public static double getPropertyAsDouble(final String key, final double def) {
        return getPropertyAsDouble(getPropertys(), key, def);
    }

    /**
     * 得到属性表里的属性值.
     * @param prop Properties 属性表
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return long 属性值
     */
    public static long getPropertyAsLong(Properties prop, final String key, final long def) {
        long retVal = def;
        String value = getPropertyAsString(key, new Long(def).toString());

        try {
            retVal = Long.parseLong(value);
        } catch (java.lang.NumberFormatException e) {
            numberFormartExceptionMsg(e);
        }

        return retVal;
    }

    /**
     * 从系统环境变量里得到long型的属性值.
     * @param key String 属性键
     * @param def long 默认值
     * @return long
     */
    public static long getPropertyAsLong(final String key, final long def) {
        return getPropertyAsLong(getPropertys(), key, def);
    }

    /**
     * 得到系统环境变量属性值.
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return boolean 属性值
     */
    public static boolean getPropertyAsBool(final String key,
                                     final boolean def) {
        return getPropertyAsBool(getPropertys(), key, def);
    }


    /**
     * 得到boolean型的属性值
     * @param prop Properties 属性表
     * @param key String 属性主键
     * @param def boolean 默认值
     * @return boolean
     */
    public static boolean getPropertyAsBool(Properties prop, final String key,
                                     final boolean def) {
        String value = getPropertyAsString(prop, key, new Boolean(def).toString());
        return new Boolean(value).booleanValue();
    }


    /**
     * 得到系统环境变量属性值.
     * @param key String 属性键
     * @param def int 默认属性值(当找不到系统环境变量时使用)
     * @return String 属性值
     */
    public static String getPropertyAsString(final String key,
                                      final String def) {
        return getPropertyAsString(getPropertys(), key, def);
    }

    /**
     * 得到属性值
     * @param prop Properties 属性表
     * @param key String 属性主键
     * @param def String 默认值
     * @return String
     */
    public static String getPropertyAsString(Properties prop, final String key,
                                      final String def) {
        String retVal = null;
        if (null == def) {
            retVal = "";
        } else {
            retVal = def;
        }

        if (null == prop) {
            return retVal;
        }

        String value = prop.getProperty(key);

        if (null != value) {
            retVal = value;
        } else {
            notExistsPropertyMsg(key, retVal);
        }

        return retVal;
    }

    /**
     * 得到属性文件的属性.
     * @param propertyFile String 属性文件名(在CLASSPATH中),文件格式 key=value, 文件列表中的文件必须是完全路径名
     * @return Properties 属性
     */
    public static Properties getProperties(final String propertyFile) {
//        if (logger.isInfoEnabled()) {
////            logger.info("Property file: " + propertyFile);
//        }

        Properties p = new Properties();

        try {
            File file = new File(propertyFile);
            if (file.exists()) {
                p.load(new FileInputStream(propertyFile));
            } else {
                p.load(SysProperty.class.getResourceAsStream(propertyFile));
            }
        } catch (java.io.IOException ioe) {
//            logger.warn(ioe.getMessage());
//            logger.warn("Get load list failed, property file ["
//                        + propertyFile + "].");
        }

        return p;
    }

    /**
     * 得到属性文件的属性.
     * @param propertyFiles String[] 属性文件名(在CLASSPATH中)列表, 文件格式 key=value, 文件列表中的文件必须是完全路径名
     * @return Properties 属性
     */
    public static Properties getProperties(final String[] propertyFiles) {
        if (propertyFiles == null) return null;

        Properties props = new Properties();
        for (int i = 0; i < propertyFiles.length; i++) {
            props.putAll(getProperties(propertyFiles[i]));
        }

        return props;
    }

    /**
     * 得到属性文件的属性.
     * @param dir File
     * @param propertyFiles String[] 属性文件名(在CLASSPATH中)列表, 文件格式 key=value
     * @return Properties 属性
     */
    public static Properties getProperties(File dir, final String[] propertyFiles) {
        if (propertyFiles == null) return null;

        Properties props = new Properties();
        java.util.Arrays.sort(propertyFiles);
        for (int i = 0; i < propertyFiles.length; i++) {
            props.putAll(getProperties(dir.toString() + "/" +  propertyFiles[i]));
        }

        return props;
    }



    /**
     * 输出数字格式转换失败时的异常.
     * @param e NumberFormatException 格式转换异常
     */
    private static void numberFormartExceptionMsg(
            final NumberFormatException e) {
//        logger.warn("Formart error.", e);
    }

    /**
     * 当系统环境变量找不到时候打印消息.
     * @param key String 系统环境变量名
     * @param defVal String 找不到系统环境变量时的默认值
     */
    private static void notExistsPropertyMsg(final String key,
            final String defVal) {
//        logger.warn("Property not exists, key [" + key + "], default value [" + defVal + "].");
    }

    /**
     * 得到系统环境变量设置值.
     * @return String 系统环境变量值
     */
    private static Properties getPropertys() {
        if (OS_TYPE == WINDOW_OS) {
            return getPropertys(winCmd);
        }
        else {
            return getPropertys(unixCmd);
        }
    }

    /**
     * 得到系统环境变量设置值.
     * @param cmd String[] 取环境变量的命令
     * @return String 系统环境变量值
     */
    private static Properties getPropertys(String[] cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
            StringBuffer buff = new StringBuffer();

            int i = 0;
            while ((i = bis.read()) > 0) {
                buff.append((char) i);
            }

            //替换掉\,否则逢\\u将出错
            String tmp = buff.toString().replace('\\', '/');
            ByteArrayInputStream bas = new ByteArrayInputStream(tmp.getBytes());

            Properties prop = new Properties();
            prop.load(bas);

            bis.close();
            bas.close();

            return prop;

        } catch (IOException e) {
//            logger.error("get property error.", e);
            return null;
        }
    }
    
    
    
    public static void main(String[] args)
    {
    	new SysProperty();
		System.out.println(SysProperty.getPropertyAsString("AUTOBUILD_HOME", ""));
    }

}
