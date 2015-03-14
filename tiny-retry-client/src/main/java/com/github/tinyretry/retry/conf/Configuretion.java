package com.github.tinyretry.retry.conf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tinyretry.retry.constants.Constants.DEFAULT_RETRY_CONFIG_FAILE;

/**
 * <pre>
 * desc: 读取配置文件
 * created: 2012-8-23 上午10:00:30
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class Configuretion {
	private static final Logger log = LoggerFactory
			.getLogger(Configuretion.class);

	private List<Object> configuretionMap = new ArrayList<Object>(2);
	private Properties mergedProp = new Properties();

	public Configuretion() {
		super();
	}

	/**
	 * 加载默认的配置文件
	 */
	public void loadDefault() {
		addConf(DEFAULT_RETRY_CONFIG_FAILE);
		loadResouceFromXml();
		configuretionMap.clear();
	}

	/**
	 * 添加Configuretion
	 * 
	 * @param configure
	 */
	public void addConfiguretion(Configuretion configure) {
		if (configure != null && configure.mergedProp.size() > 0) {
			configure.mergeProp(configure.mergedProp, mergedProp);
		}
	}

	/**
	 * 添加配置文件
	 * 
	 * @param classpathUrl
	 */
	public synchronized void addConf(String classpathUrl) {
		if (StringUtils.isNotBlank(classpathUrl)
				&& !configuretionMap.contains(classpathUrl)) {
			configuretionMap.add(classpathUrl);
		}
	}

	/**
	 * 添加配置文件
	 * 
	 * @param inputStream
	 */
	public synchronized void addConf(InputStream inputStream) {
		if (inputStream != null && !configuretionMap.contains(inputStream)) {
			configuretionMap.add(inputStream);
		}
	}

	/**
	 * 添加配置文件
	 * 
	 * @param url
	 */
	public synchronized void addConf(URL url) {
		if (url != null && !configuretionMap.contains(url)) {
			configuretionMap.add(url);
		}
	}

	/**
	 * 重新加载资源文件
	 */
	public void reloadResouceFromXml() {
		mergedProp.clear();
		loadResouceFromXml();
	}

	/**
	 * 加载xml配置文件，xml必须符合 http://java.sun.com/dtd/properties.dtd
	 */
	public void loadResouceFromXml() {
		if (configuretionMap != null) {
			Properties tempProp = null;
			for (Object configure : configuretionMap) {
				tempProp = null;
				if (configure == null) {
					continue;
				}
				InputStream inStream = null;
				try {
					if (configure instanceof String) {
						tempProp = new Properties();
						inStream = Configuretion.class.getResourceAsStream(String
								.valueOf(configure));
					} else if (configure instanceof InputStream) {
						tempProp = new Properties();
						inStream = (InputStream) configure;
					} else if (configure instanceof URL) {
						tempProp = new Properties();
						try {
							inStream = ((URL) configure).openStream();
						} catch (IOException e) {
							log.error("open URL resouce exception", e);
							continue;
						}
					}

					if (loadFromXml(tempProp, inStream)) {
						mergeProp(tempProp, mergedProp);
					}
				} finally {
					if (inStream != null) {
						try {
							inStream.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
	}

	/**
	 * 读取配置文件xml文件，xml必须符合 http://java.sun.com/dtd/properties.dtd
	 * 
	 * @param prop
	 * @param inStream
	 * @return
	 */
	private boolean loadFromXml(Properties prop, InputStream inStream) {
		if (inStream == null) {
			return false;
		}
		try {
			prop.loadFromXML(inStream);
		} catch (Exception e) {
			log.error("Load inputStream resouce from xml exception ", e);
		}
		return true;
	}

	/**
	 * 合并prop属性集合
	 * 
	 * @param source
	 *            源配置信息
	 * @param dest
	 *            合并配置信息的目标地址
	 */
	public void mergeProp(Properties source, Properties dest) {
		for (Object obj : source.keySet()) {
			if (obj != null && obj instanceof String) {
				String key = String.valueOf(obj);
				if (StringUtils.isNotBlank(key)) {
					dest.put(key, source.get(obj));
				}
			}
		}
	}

	/**
	 * 获取int配置值，如果找不到配置，返回默认值为""
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return mergedProp.getProperty(key, StringUtils.EMPTY);
	}

	/**
	 * 设置string配置项
	 * 
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value) {
		mergedProp.setProperty(key, value);
	}

	/**
	 * 获取int配置值，如果找不到配置，返回默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		if (StringUtils.isNotBlank(getString(key))) {
			return Integer.valueOf(getString(key)).intValue();
		}
		return 0;
	}

	/**
	 * 获取short配置值，如果找不到配置，返回默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public short getShort(String key) {
		if (StringUtils.isNotBlank(getString(key))) {
			return Short.valueOf(getString(key)).shortValue();
		}
		return 0;
	}

	/**
	 * 获取int配置值，如果找不到配置，返回默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		if (StringUtils.isNotBlank(getString(key))) {
			return Long.valueOf(getString(key)).longValue();
		}
		return 0l;
	}

	/**
	 * 获取int配置值，如果找不到配置，返回默认值为0.0
	 * 
	 * @param key
	 * @return
	 */
	public float getFloat(String key) {
		if (StringUtils.isNotBlank(getString(key))) {
			return Float.valueOf(getString(key)).floatValue();
		}
		return 0.0f;
	}

	// public static void main(String[] args) {
	// Configuretion conf = new Configuretion();
	// conf.loadDefault();
	// System.out.println(conf.getString("retry.sys.retry.count"));
	// System.out.println(conf.getString("retry.sys.retry.period.millisecond"));
	// System.out.println(conf.getString("retry.sys.retry.period.factor"));
	//
	// // Configuretion conf1 = new Configuretion();
	// // conf1.addConf("/self_retry.xml");
	// // conf1.loadResouceFromXml();
	// //
	// // System.out.println(conf1.getString("test1"));
	// // System.out.println(conf1.getString("test2"));
	// // System.out.println(conf1.getString("test3"));
	// }
}
