package com.github.tinyretry.timer.ext.repository;


/**
 * <pre>
 * desc: spring 容器的job仓库
 * 
 * 该容器以来与spring管理任务和任务分组，所以无法（无需）删除任务和分组，对应的方法是无效的。
 * 
 * created: 2012-9-10 下午03:02:10
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
//public class SpringMcJobRepository implements McJobRepository, ApplicationContextAware {
//	private static SpringMcJobRepository respository = null;
//	private ApplicationContext applicationContext;
//	private volatile static Object lock = new Object();
//
//	private SpringMcJobRepository() {
//		super();
//	}
//
//	public static SpringMcJobRepository getInstance() {
//		if (respository == null) {
//			synchronized (lock) {
//				if (respository == null) {
//					respository = new SpringMcJobRepository();
//				}
//			}
//		}
//		return respository;
//	}
//
//	/**
//	 * 不允许添加job
//	 */
//	public boolean addJob(String groupId, McJob job) {
//		return false;
//	}
//
//	/**
//	 * 不允许删
//	 */
//	public boolean removeJob(String groupId, McJob job) {
//		return false;
//	}
//
//	/**
//	 * 不允许删
//	 */
//	public boolean removeJob(String groupId, String jobName) {
//		return false;
//	}
//
//	public McJobGroup getGroups(String groupId) {
//		Object bean = applicationContext.getBean(groupId, McJobGroup.class);
//		if (bean != null) {
//			return (McJobGroup) bean;
//		}
//		return null;
//	}
//
//	public Map<String, McJobGroup> getGroups() {
//		return applicationContext.getBeansOfType(McJobGroup.class);
//	}
//
//	/**
//	 * 不允许添加job
//	 */
//	public void addGroups(McJobGroup groups) {
//	}
//
//	/**
//	 * 不允许删
//	 */
//	public void removeGroups(String groupId) {
//	}
//
//	public McJob getJob(String name) {
//		Object bean = applicationContext.getBean(name, McJob.class);
//		if (bean != null) {
//			return (McJob) bean;
//		}
//		return null;
//	}
//
//	public boolean jobExists(String name) {
//		return applicationContext.containsBean(name);
//	}
//
//	public boolean groupExists(String groupId) {
//		return applicationContext.containsBean(groupId);
//	}
//
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
//	}
//
//	public Map<String, McJob> getJobs() {
//		return applicationContext.getBeansOfType(McJob.class);
//	}
// }
