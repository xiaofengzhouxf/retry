package com.github.tinyretry.retry.domain;

import java.io.Serializable;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 обнГ01:37:56
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public interface Processor extends Serializable {

	ProcessResult execute(ProcessContext context);

}
