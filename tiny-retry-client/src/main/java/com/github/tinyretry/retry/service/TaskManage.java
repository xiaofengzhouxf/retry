package com.github.tinyretry.retry.service;

import java.util.List;

import com.github.tinyretry.retry.domain.McTaskDO;
import com.github.tinyretry.retry.domain.McTaskHistoryDO;
import com.github.tinyretry.retry.domain.TaskHistoryQuery;
import com.github.tinyretry.retry.domain.TaskQuery;
import com.github.tinyretry.retry.domain.TaskUpdate;
import com.github.tinyretry.retry.exception.RetryRemoteException;

/**
 * <pre>
 * desc: 对任务进行管理
 * created: 2012-8-23 下午03:20:17
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class TaskManage {
	// private static final Logger logger = LoggerFactory.getLogger(TaskManage.class);

	private RetryTaskService retryTaskService;

	public List<McTaskDO> queryMcTaskList(TaskQuery taskQuery) throws RetryRemoteException {
		return retryTaskService.queryMcTaskList(taskQuery);
	}

	public int countMcTaskList(TaskQuery taskQuery) throws RetryRemoteException {
		return retryTaskService.countMcTaskList(taskQuery);
	}

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public McTaskDO insertMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
		return retryTaskService.insertMcTask(mcTaskDO);
	}

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer updateMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
		return retryTaskService.updateMcTask(mcTaskDO);
	}

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer updateMcTaskStatus(TaskUpdate taskUpdate) throws RetryRemoteException {
		return retryTaskService.updateMcTaskStatus(taskUpdate);
	}

	/**
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer deleteMcTask(McTaskDO mcTaskDO) throws RetryRemoteException {
		return retryTaskService.deleteMcTask(mcTaskDO);
	}

	/**
	 * select mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public List<McTaskHistoryDO> queryMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException {
		return retryTaskService.queryMcTaskHistoryList(taskHistoryQuery);
	}

	public int countMcTaskHistoryList(TaskHistoryQuery taskHistoryQuery) throws RetryRemoteException {
		return retryTaskService.countMcTaskHistoryList(taskHistoryQuery);
	}

	/**
	 * insert into mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Long insertMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
		return retryTaskService.insertMcTaskHistory(mcTaskHistoryDO);
	}

	/**
	 * update mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer updateMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
		return retryTaskService.updateMcTaskHistory(mcTaskHistoryDO);
	}

	/**
	 * delete from mc_task_history
	 * 
	 * @author xiaofeng
     * Date 2012-09-05
	 */
	public Integer deleteMcTaskHistory(McTaskHistoryDO mcTaskHistoryDO) throws RetryRemoteException {
		return retryTaskService.deleteMcTaskHistory(mcTaskHistoryDO);
	}

	public void setRetryTaskService(RetryTaskService retryTaskService) {
		this.retryTaskService = retryTaskService;
	}
}
