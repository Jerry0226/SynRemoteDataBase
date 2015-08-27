package com.chm.common;



import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * @author cheng.haiming
 * 初始化类，初始化配置文件，把配置文件的信息写入到Map中
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class CfgInit {

	public static void initCfg(String filePath)
	{
		try
		{
			File inputXml = new File(filePath);
			SAXReader saxReader = new SAXReader();
			
			Document document = saxReader.read(inputXml);
			//得到所有的ConfigItem节点
			List<Element> list =  document.selectNodes("//"+Constants.CONFIGITEM);
			 
//			/*
			Iterator<Element> iter=list.iterator();
			while(iter.hasNext())
			{
				Element element_temp=(Element)iter.next();
				
				//得到此element_temp节点下的所有的ConfigParam节点
				List<Element> list_temp = element_temp.elements(Constants.CONFIGPARAM);
				
				//判断一个节点下面有几个ConfigParam，当有一个时插入Map的值是String
				//当如果是多个时，插入Map的值是一个集合
				if(list_temp.size()>1)
				{
					/**如果在一个ConfigItem下面有多个ConfigParam，那么Map的键就是ConfigName,下面是得到ConfigName
					 * 如果要得到某个元素的父节点可以直接使用getParent即可*/
					
					String key_temp = "";
					Element element_list = (Element) list_temp.get(0).getParent().elementIterator(Constants.CONFIGNAME).next();
					key_temp = element_list.getText();
					
					
					Iterator<Element> iter_temp=list_temp.iterator();
					
					Properties props = new Properties();
					
					while(iter_temp.hasNext())
					{
						Element element_if=(Element)iter_temp.next();
						Element temp_name=(Element) element_if.elementIterator(Constants.PARAMNAME).next();
						Element temp_value=(Element) element_if.elementIterator(Constants.PARAMVALUE).next();
						props.setProperty(temp_name.getText(), temp_value.getText());
					}
					ConfigManager.CfgMap.put(key_temp, props);
				}
				/**此处处理只有一层configParam结构的xml文件，此处的Map中的值为单一数据类型，不是集合类型*/
				else
				{
					Iterator<Element> iteratorsum = list_temp.iterator();
				    if(iteratorsum.hasNext())
				    {
				    	Element temp_else = (Element)iteratorsum.next();
				    	Element temp_name=(Element) temp_else.elementIterator(Constants.PARAMNAME).next();
				    	Element temp_value = (Element)temp_else.elementIterator(Constants.PARAMVALUE).next();
				    	ConfigManager.CfgMap.put(temp_name.getText(), temp_value.getText());
				    	
				    }
				}
				
			}
//			*/
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
