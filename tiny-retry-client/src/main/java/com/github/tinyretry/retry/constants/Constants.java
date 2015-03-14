package com.github.tinyretry.retry.constants;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 下午01:27:47
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public interface Constants {
	// 默认的配置文件
	String DEFAULT_RETRY_CONFIG_FAILE = "/default_retry_config.xml";
	// zk节点名称
	String RETRY_ZK_NODE_NAME_TASK = "retry_worker_{0}_task_{1}";

	// 查询下一次运行时间的增量，就是在当前时间的基础上机上这个值来查询
	long NEXT_TIME_ADDTINO = 1500l;

	// 一分钟查询一次
	long NEXT_TIME_TIMEOUT = 60000l;

	String EMPTY = "empty";

	// ==========configuretion===============
	// 重试次数配置项
	String RETRY_SYS_COUNT = "retry.sys.retry.count";
	// 重试周期配置项
	String RETRY_SYS_PERIOD_MILLISECOND = "retry.sys.retry.period.millisecond";
	// 重试周期因子配置项
	String RETRY_SYS_PERIOD_FACTOR = "retry.sys.retry.period.factor";
	// 应用码
	String RETRY_SYS_APP_CODE = "retry.app.code";
	// 任务默认的执行超时时间
	String RETRY_SYS_EXECUTE_TIMEOUT = "retry.sys.retry.execute.timeout";
	// ZK链接地址
	String ZOOKEEPER_CONNECT_STR = "retry.zookeeper.connect.str";
	// zk超时时间
	String ZOOKEEPER_CONNECT_TIME_OUT = "retry.zookeeper.connect.time.out";
	// zk链接重试次数
	String ZOOKEEPER_CONNECT_RETRY_TIMES = "retry.zookeeper.connect.retry.times";
	// zk 重试间隔
	String ZOOKEEPER_CONNECT_RETRY_INTERVAL_TIME = "retry.zookeeper.connect.retry.interval.time";
	// zk 执行超时时间
	String ZOOKEEPER_EXECUTE_CONNECT_TIMEOUT = "retry.zookeeper.execute.connect.timeout";
	// zk 跟节点名称
	String ZOOKEEPER_DEFAULT_PARENT = "retry.zookeeper.default.parent";

}
