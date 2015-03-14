package com.github.tinyretry.retry.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.commons.model.Observable;
import com.github.commons.model.Observer;
import com.github.tinyretry.timer.domain.McJob;

/**
 * <pre>
 * desc: 
 * created: 2012-8-23 下午02:56:50
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class RetryJob extends McJob implements Observable<ProcessResult> {
	private static final Logger logger = LoggerFactory.getLogger(RetryJob.class);

	private AtomicBoolean changed = new AtomicBoolean(false);

	private Set<Observer<ProcessResult>> observerList = new HashSet<Observer<ProcessResult>>();

	/**
	 * 当前的任务信息
	 */
	private Task task;

	private Processor processor;

	private Serializable data;

	@Override
	public boolean execute() throws Exception {
		ProcessContext context = new ProcessContext();
		context.setData(data);
		context.setTask(task);
		ProcessResult result = null;
		try {
			result = processor.execute(context);
		} catch (Exception e) {
			logger.error("Retry process exception.", e);
			result = new ProcessResult(false);
			result.setData(data);
			result.setFailReason(e.toString());
		}

		notifyObservers(result);

		return result.isSuccess();
	}

	public void afterExec() throws Exception {

	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void addObserver(Observer<ProcessResult> observer) {
		changed.set(observerList.add(observer));
	}

	public int countObservers() {
		return this.observerList.size();
	}

	public void deleteObserver(Observer<ProcessResult> observer) {
		changed.set(this.observerList.remove(observer));
	}

	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public void deleteObservers() {
		this.observerList.clear();
		this.clearChanged();
	}

	public boolean isChanged() {
		return changed.get();
	}

	public void notifyObservers() {
		this.notifyObservers(null);
	}

	public void clearChanged() {
		changed.set(false);
	}

	public void setChanged() {
		changed.set(true);
	}

	public void notifyObservers(ProcessResult arg) {
		if (!isChanged())
			return;
		for (Observer<ProcessResult> observer : this.observerList) {
			observer.update(this, arg);
		}
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public float successRate() {
		if (this.getTask() == null || this.getTask().getCurrentTime() <= 0) {
			return 1f;
		}
		float f = (float) 1.0 / this.getTask().getCurrentTime();
		BigDecimal b = new BigDecimal(f);
		return b.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
	}

}
