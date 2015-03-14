package com.github.tinyretry.timer.event;

/**
 * <pre>
 * desc: 
 * created: Jul 24, 2013 3:42:18 PM
 * author: xiangfeng
 * todo: 
 * history:
 * </pre>
 */
public interface ReportAble {

	/**
	 * 获取job的耗时情况，从job注册到执行成功或者失败的时间
	 * 
	 * @return 耗时，单位毫秒
	 */
	public long getElapsedTime();

	/**
	 * 真正执行的时间
	 * 
	 * @return
	 */
	public long getActualRunTime();

	/**
	 * 返回成功率,小于等于1的小数，保留小数点后面4位
	 * 
	 * @return
	 */
	public float successRate();
}
