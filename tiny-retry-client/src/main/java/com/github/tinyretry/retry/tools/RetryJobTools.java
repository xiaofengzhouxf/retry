package com.github.tinyretry.retry.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tinyretry.retry.constants.Constants;
import com.github.tinyretry.retry.domain.RetryJob;
import com.github.tinyretry.retry.domain.Task;
import com.github.tinyretry.timer.domain.McJobGroup;
import com.github.tinyretry.timer.ext.McJobDefinition;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 下午04:47:13
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class RetryJobTools {
	private static final Logger logger = LoggerFactory.getLogger(RetryJobTools.class);

	private static final String COLON = ":";
	private static final String SEPARATIVE = "_";

	public static String generateJobName(Task task) {
		return new StringBuilder(StringUtils.isNotBlank(task.getTaskName()) ? task.getTaskName() : StringUtils.EMPTY)
				.append(SEPARATIVE).append(task.getTaskId()).toString();
	}

	public static String generateJobNameByStrId(Task task) {
		return new StringBuilder(StringUtils.isNotBlank(task.getTaskName()) ? task.getTaskName() : StringUtils.EMPTY)
				.append(SEPARATIVE).append(task.getTaskStrId()).toString();
	}

	public static long calculateNextTime(long period, float wight, int currentTime) {
		long nextTime = System.currentTimeMillis();
		nextTime = nextTime + (int) (period
				* Math.pow(
				wight, currentTime
				));

		nextTime = nextTime > 0 ? nextTime : 200;

		return nextTime;
	}

	public static long calculatePreTime(long currentNextTime, long period, float wight, int currentTime) {

		long perTime = currentNextTime - (int) (period
				* Math.pow(
				wight, currentTime
				));

		perTime = perTime > 0 ? perTime : currentNextTime;

		return perTime;
	}

	public static String generateTaskZKNodeName(Long id, String appCode) {
		return MessageFormat.format(Constants.RETRY_ZK_NODE_NAME_TASK, appCode, (id != null ? id.toString() : "999"));
	}

	public static long getAfterNextTime(long addtion) {
		return System.currentTimeMillis() + (addtion != 0 ? addtion : Constants.NEXT_TIME_ADDTINO);
	}

	public static long getBeforeNextTime(long timeOut) {
		return System.currentTimeMillis() - (timeOut != 0 ? timeOut : Constants.NEXT_TIME_TIMEOUT);
	}

	public static RetryJob generateMcJob(McJobDefinition definition) {
		RetryJob job = new RetryJob();
		job.setDefinition(definition);
		return job;
	}

	public static McJobGroup generateMcJobGroup(String groupId, long repeatInterval) {

		McJobGroup jobGroup = new McJobGroup();
		jobGroup.setGroupId(groupId);
		jobGroup.setRepeatInterval(repeatInterval);
		jobGroup.setRepeatCount(0);
		return jobGroup;
	}

	public static McJobDefinition generateMcJobDefinition(String jobName, String groupId, String description) {
		McJobDefinition definition = new McJobDefinition();
		definition.setGroupId(groupId);
		definition.setName(jobName);
		definition.setDescription(description);
		return definition;
	}

	/**
	 * @return 本机IP
	 * @throws java.net.SocketException
	 */
	public static String getIPAddress() {
		String localip = StringUtils.EMPTY;// 本地IP，如果没有配置外网IP则返回它
		String netip = StringUtils.EMPTY;// 外网IP

		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			logger.error("Get Ip exception.", e);
			return StringUtils.EMPTY;
		}
		if (netInterfaces != null) {
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(COLON) == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(COLON) == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
		}
		if (StringUtils.isNotBlank(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	/**
	 * 构建rowkey
	 * 
	 * @param appcode
	 * @param type
	 * @param status
	 * @param nextTimestamp
	 * @return
	 */
	public static String generateHbaseRowkey(String appcode, String type, String status, String nextTimestamp) {
		return new StringBuilder(appcode).append(SEPARATIVE).append(type).append(SEPARATIVE).append(status)
				.append(SEPARATIVE).append(nextTimestamp).toString();
	}
}
