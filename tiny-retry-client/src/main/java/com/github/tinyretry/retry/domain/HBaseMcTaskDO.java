package com.github.tinyretry.retry.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <pre>
 * desc: 
 * created: Apr 7, 2013 2:27:26 PM
 * author: xiangfeng
 * todo: 
 * history:
 * </pre>
 */
public class HBaseMcTaskDO extends McTaskDO {
	public final static String QUALIFIER_PRIORITY = "priority";
	public final static String QUALIFIER_GMT_CREATE = "gmt_create";
	public final static String QUALIFIER_GMT_MODIFIED = "gmt_modified";
	public final static String QUALIFIER_FAIL_REASON = "fail_reason";
	public final static String QUALIFIER_RETRY_TIME = "retry_time";
	public final static String QUALIFIER_CONTEXT = "context";
	public final static String QUALIFIER_VERSION = "version";
	public final static String QUALIFIER_APP_CODE = "app_code";
	public final static String QUALIFIER_PROCESSOR = "processor";
	public final static String QUALIFIER_TYPE = "type";
	public final static String QUALIFIER_TIME_OUT = "time_out";
	public final static String QUALIFIER_STATUS = "status";

	public final static byte[] QUALIFIER_PRIORITY_BYTE_ARRAY = QUALIFIER_PRIORITY.getBytes();
	public final static byte[] QUALIFIER_GMT_CREATE_BYTE_ARRAY = QUALIFIER_GMT_CREATE.getBytes();
	public final static byte[] QUALIFIER_GMT_MODIFIED_BYTE_ARRAY = QUALIFIER_GMT_MODIFIED.getBytes();
	public final static byte[] QUALIFIER_FAIL_REASON_BYTE_ARRAY = QUALIFIER_FAIL_REASON.getBytes();
	public final static byte[] QUALIFIER_RETRY_TIME_BYTE_ARRAY = QUALIFIER_RETRY_TIME.getBytes();
	public final static byte[] QUALIFIER_CONTEXT_BYTE_ARRAY = QUALIFIER_CONTEXT.getBytes();
	public final static byte[] QUALIFIER_VERSION_BYTE_ARRAY = QUALIFIER_VERSION.getBytes();
	public final static byte[] QUALIFIER_APP_CODE_BYTE_ARRAY = QUALIFIER_APP_CODE.getBytes();
	public final static byte[] QUALIFIER_PROCESSOR_BYTE_ARRAY = QUALIFIER_PROCESSOR.getBytes();
	public final static byte[] QUALIFIER_TYPE_BYTE_ARRAY = QUALIFIER_TYPE.getBytes();
	public final static byte[] QUALIFIER_TIME_OUT_BYTE_ARRAY = QUALIFIER_TIME_OUT.getBytes();
	public final static byte[] QUALIFIER_STATUS_BYTE_ARRAY = QUALIFIER_STATUS.getBytes();

	private static final long serialVersionUID = 1L;

	private String rowKey;
	private String family;
	private String timeOut;

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
