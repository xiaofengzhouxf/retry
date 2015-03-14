package com.github.tinyretry.timer.ext.repository;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobGroup;
import com.github.tinyretry.timer.domain.McJobRepository;
import com.github.tinyretry.timer.ext.jobfactory.McJobFactory;

/**
 * <pre>
 * desc: 简单的job仓库，用于存放分组和任务
 * 注意： 添加的任务和分组不会自行删除，需要注意内存溢出问题
 * 
 * 任务删除依赖于{@link McJobFactory}
 * 
 * created: 2012-9-10 下午03:02:10
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 * 
 * @see McJobFactory
 * @see McJobSchedule
 */
public class SimpleMcJobRepository implements McJobRepository {
	private Map<String, McJobGroup> groupMap = new WeakHashMap<String, McJobGroup>();
	private Map<String, McJob> mapForJobName = new WeakHashMap<String, McJob>();
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private long maxJobCount = 200000;

	public SimpleMcJobRepository() {
		super();
	}

	public boolean addJob(String groupId, McJob job) {
		if (StringUtils.isBlank(groupId) || job == null || !job.validate()) {
			return false;
		}
		rwLock.writeLock().lock();
		try {
			McJobGroup csJobGroup = groupMap.get(groupId);

			if (csJobGroup == null) {
				return false;
			}

			if (StringUtils.isNotBlank(job.getDefinition().getName())) {
				if (mapForJobName.size() < maxJobCount) {
					mapForJobName.put(job.getDefinition().getName(), job);
					return true;
				}
			}
		} finally {
			rwLock.writeLock().unlock();
		}
		return false;
	}

	public boolean removeJob(String groupId, McJob job) {
		if (StringUtils.isBlank(groupId) || job == null || !job.validate()) {
			return false;
		}
		rwLock.writeLock().lock();
		try {
			McJobGroup csJobGroup = groupMap.get(groupId);
			if (csJobGroup == null) {
				return false;
			}
			if (mapForJobName.remove(job.getDefinition().getName()) != null) {
				return true;
			}
		} finally {
			rwLock.writeLock().unlock();
		}

		return false;
	}
	
	public boolean removeJob(String groupId, String jobName) {
		if (StringUtils.isBlank(groupId) || StringUtils.isBlank(jobName)) {
			return false;
		}
		rwLock.writeLock().lock();
		try {
			McJobGroup csJobGroup = groupMap.get(groupId);
			if (csJobGroup == null) {
				return false;
			}
			if (mapForJobName.remove(jobName) != null) {
				return true;
			}
		} finally {
			rwLock.writeLock().unlock();
		}

		return false;
	}

	public McJobGroup getGroups(String groupId) {
		if (StringUtils.isBlank(groupId)) {
			return null;
		}
		rwLock.readLock().lock();
		try {
			return groupMap.get(groupId);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public Map<String, McJobGroup> getGroups() {
		rwLock.readLock().lock();
		try {
			return Collections.unmodifiableMap(groupMap);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public void addGroups(McJobGroup groups) {
		if (groups == null) {
			return;
		}
		rwLock.writeLock().lock();
		try {
			groupMap.put(groups.getGroupId(), groups);
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public void removeGroups(String groupId) {
		if (StringUtils.isBlank(groupId)) {
			return;
		}
		rwLock.writeLock().lock();
		try {
			groupMap.remove(groupId);
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	public McJob getJob(String name) {
		rwLock.readLock().lock();
		try {
			return mapForJobName.get(name);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public boolean jobExists(String name) {
		rwLock.readLock().lock();
		try {
			return mapForJobName.containsKey(name);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public boolean groupExists(String groupId) {
		rwLock.readLock().lock();
		try {
			return groupMap.containsKey(groupId);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public Map<String, McJob> getJobs() {
		return Collections.unmodifiableMap(mapForJobName);
	}

	// ====================================
	public long getMaxJobCount() {
		return maxJobCount;
	}

	public void setMaxJobCount(long maxJobCount) {
		this.maxJobCount = maxJobCount;
	}



}
