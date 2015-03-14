package com.github.tinyretry.timer.event;

import java.util.EventObject;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
 * desc: 
 * created: 2012-5-7 ÏÂÎç02:21:50
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class McJobNotify extends EventObject {
	private static final long serialVersionUID = 1683841631125340981L;

	private String notifyType;
	private Object source;
	private String groupId;
	private String fromJobName;

	public McJobNotify(Object source) {
		super(source);
		this.source = source;
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getFromJobName() {
		return fromJobName;
	}

	public void setFromJobName(String fromJobName) {
		this.fromJobName = fromJobName;
	}

	public boolean validate() {
		if (StringUtils.isBlank(notifyType) || source == null) {
			return false;
		}
		return true;
	}
}
