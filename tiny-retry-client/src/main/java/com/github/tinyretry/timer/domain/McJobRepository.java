package com.github.tinyretry.timer.domain;

import java.util.Map;

import com.github.tinyretry.timer.ext.repository.SimpleMcJobRepository;

/**
 * <pre>
 * desc: 任务资源库，用来存储定时任务列表及其对应的分组列表
 * 
 * 数据结构如下：
 * 
 * McJobRepository{
 *    [groupId:McJobGroup{ 
 *       [jobName:McJob,....]
 *     },...
 *    ]
 * }  
 * 
 * created: 2012-5-7 下午02:10:58
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 * 
 * @see SpringMcJobRepository
 * @see SimpleMcJobRepository
 */
public interface McJobRepository {

	/**
	 * 添加定时任务到任务仓库
	 * 
	 * @param groupId
	 *            任务所在的组ID
	 * @param job
	 *            任务
	 * @return 添加成功返回true,否则返回false
	 */
	boolean addJob(String groupId, McJob job);

	/**
	 * 从任务仓库删除定时任务
	 * 
	 * @param groupId
	 *            任务所在的组ID
	 * @param job
	 *            任务
	 * @return 删除成功返回true,否则返回false
	 */
	boolean removeJob(String groupId, McJob job);

	/**
	 * boolean removeJob(String groupId, McJob job);
	 * 
	 * @param groupId
	 *            任务所在的组ID
	 * @param jobName
	 *            job的名词
	 * @return
	 */
	boolean removeJob(String groupId, String jobName);

	/**
	 * 根据组ID获取任务组
	 * 
	 * @param groupId
	 * @return 返回分组
	 */
	McJobGroup getGroups(String groupId);

	/**
	 * 获取所有的任务组
	 * 
	 * @return Map,key为分组ID，value为分组对象
	 */
	Map<String, McJobGroup> getGroups();

	/**
	 * 添加分组到资源库中，
	 * 
	 * @param groups
	 */
	void addGroups(McJobGroup groups);

	/**
	 * 根据分组ID删除任务组
	 * 
	 * @param groupId
	 */
	void removeGroups(String groupId);

	/**
	 * 根据job名称获取任务
	 * 
	 * @param name
	 * @return
	 */
	McJob getJob(String name);

	/**
	 * 根据job名称判断任务是否存在
	 * 
	 * @param name
	 *            job名称
	 * @return job存在返回true,否则false
	 */
	boolean jobExists(String name);

	/**
	 * 根据分组ID判断分组是否存在
	 * 
	 * @param groupId
	 *            分组ID
	 * @return group存在返回true,否则false
	 */
	boolean groupExists(String groupId);

	/**
	 * 获取当前所有job列表
	 * 
	 * @return
	 */
	Map<String, McJob> getJobs();
}
