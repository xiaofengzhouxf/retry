package com.github.tinyretry.timer;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobGroup;
import com.github.tinyretry.timer.domain.McJobRepository;

/**
 * <pre>
 * desc: 默认任务资源库
 * created: 2012-5-7 下午02:10:58
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class DefaultMcJobRepository implements McJobRepository {
	private static DefaultMcJobRepository respository = null;
	private static Map<String, McJobGroup> groupMap = new WeakHashMap<String, McJobGroup>();
	private static Map<String, McJob> mapForJobName = new WeakHashMap<String, McJob>();
	private volatile static Object lock = new Object();
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private long maxJobCount = 100000;

	private DefaultMcJobRepository() {
		super();
	}

	public static DefaultMcJobRepository getInstance() {
		if (respository == null) {
			synchronized (lock) {
				if (respository == null) {
					respository = new DefaultMcJobRepository();
				}
			}
		}
		return respository;
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

	// =================
	public long getMaxJobCount() {
		return maxJobCount;
	}

	public void setMaxJobCount(long maxJobCount) {
		this.maxJobCount = maxJobCount;
	}

}
