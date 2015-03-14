package com.github.tinyretry.retry.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 ÏÂÎç03:03:40
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class ProcessResult implements Serializable {

    private static final long         serialVersionUID = -8036229605987434473L;

    public static final ProcessResult SUCCESS          = new ProcessResult(true);

    public static final ProcessResult FAIL             = new ProcessResult(false);

    private boolean                   isSuccess;

    private Serializable              data;

    private String                    failReason;

    private boolean                   updateData;

    public ProcessResult(boolean b){
        isSuccess = b;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = StringUtils.substring(failReason, 0, 1000);
    }

    public boolean isUpdateData() {
        return updateData;
    }

    public void setUpdateData(boolean updateData) {
        this.updateData = updateData;
    }

}
