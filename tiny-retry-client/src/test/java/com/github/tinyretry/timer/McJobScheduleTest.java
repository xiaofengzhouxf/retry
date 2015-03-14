package com.github.tinyretry.timer;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.quartz.spi.TriggerFiredBundle;

import com.github.tinyretry.timer.McJobSchedule;
import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.McJobScheduleFactory;
import com.github.tinyretry.timer.domain.McJob;
import com.github.tinyretry.timer.domain.McJobGroup;
import com.github.tinyretry.timer.event.McJobNotify;
import com.github.tinyretry.timer.event.McJobNotifyListener;
import com.github.tinyretry.timer.ext.McJobDefinition;
import com.github.tinyretry.timer.ext.McJobListener;
import com.github.tinyretry.timer.ext.jobfactory.McJobFactory;

/**
 * <pre>
 * desc: 
 * created: 2012-7-12 下午02:57:32
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class McJobScheduleTest {

    private McJobGroup          towPerSecond = null;
    private static final String groupId      = "testGroup";
    private static int          beforeCount1 = 0;
    private static int          afterCount1  = 0;
    private static int          beforeCount2 = 0;
    private static int          afterCount2  = 0;
    private static int          notifyCount1 = 0;
    private static int          notifyCount2 = 0;

    @Before
    public void init() {
        // ==========> 第一步 定义GROUP ================
        towPerSecond = new McJobGroup(); // 定义一个定时组
        towPerSecond.setGroupId(groupId); // 设置组ID
        // towPerSecond.setCornExpression("0 0 */1 * * ?"); //定义定时的表达是
        towPerSecond.setRepeatInterval(3000); // 或者周期执行时间
        towPerSecond.setRepeatCount(0); // 重试次数，0为无限次
    }

    @Test
    public void testStart() throws InterruptedException, McJobScheduleException {
        // ==========> 第二步 定义JOB ================
        TestJob1 job1 = new TestJob1(); // 定义一个JOB
        McJobDefinition definition = new McJobDefinition(); // 定义这个JOB的信息
        definition.setGroupId(groupId); // 这个JOB 归属的组，按组的定时时间执行
        definition.setName("job1"); // JOB 名词
        definition.setDescription("test1111111111"); // 描述

        // ==========> 第三步 定义监听器 这步是可选的 ================
        List<McJobListener> jobListeners = new ArrayList<McJobListener>(1); // 监听器
        int afterCount = 0;
        McJobListener listener1 = new McJobListener() {

            @Override
            public void before(McJobDefinition definitionInner) {
                System.out.println("job1 before........" + definitionInner.getName());
                beforeCount1++;
            }

            @Override
            public void after(McJobDefinition definitionInner) {
                System.out.println("job1 after........" + definitionInner.getName());
                afterCount1++;
            }
        };
        listener1.setName("JobListener1");
        jobListeners.add(listener1);
        definition.setJobListeners(jobListeners); // /设置好这个JOB的监听器
        job1.setDefinition(definition); // 设置好JOB定义

        TestJob2 job2 = new TestJob2();
        McJobDefinition definition2 = new McJobDefinition();
        definition2.setGroupId(groupId);
        definition2.setName("job2");
        definition2.setDescription("test22222222222");

        List<McJobListener> jobListeners2 = new ArrayList<McJobListener>(1);
        McJobListener listener2 = new McJobListener() {

            @Override
            public void before(McJobDefinition definitionInner) {
                System.out.println("job2 before........" + definitionInner.getName());
                beforeCount2++;
            }

            @Override
            public void after(McJobDefinition definitionInner) {
                System.out.println("job2 after........" + definitionInner.getName());
                afterCount2++;
            }
        };
        listener2.setName("JobListener2");
        jobListeners2.add(listener2);
        definition2.setJobListeners(jobListeners2);
        job2.setDefinition(definition2);

        // ==========> 第四步 定义调度器 ================

        // 这里为了演示，默认是只需要传一个线程数就行
        McJobSchedule js = McJobScheduleFactory.createMcJobSchedule(2, new McJobFactory() {

            @Override
            public McJob createJob(TriggerFiredBundle bundle) {
                // job名称
                String name = bundle.getJobDetail().getName();
                if (StringUtils.isNotBlank(name)) {
                    // 从资源库中找job
                    McJob job = repository.getJob(name);

                    return job;
                }
                return null;
            }
        });

        // ==========> 第五步 把GROUP 关联到调度器 ================
        List<McJobGroup> groups = new ArrayList<McJobGroup>(1);
        groups.add(towPerSecond);
        js.setGroups(groups);

        // ==========> 第六步 初始化调度器 ================
        js.init();

        // ==========> 第七步 注册JOB到调度器 ================
        js.registerJob(job1);
        js.registerJob(job2);

        // ==========> 第八步 启动调度器 ================
        js.start();

        // =========一下是增强功能===================
        // 从当前调度器里面取的存储了，但是还没又被执行的JOB
        List<McJobDefinition> currentStoreJob = js.getCurrentStoreJob();
        System.out.println("=========currentStore1===========");
        if (currentStoreJob != null) for (McJobDefinition df : currentStoreJob) {
            System.out.println(df.getGroupId() + "===========" + df.getName());
        }

        Thread.sleep(1000);
        // 从当前调度器里面取的运行中的JOB
        List<McJobDefinition> currentRuningJob = js.getCurrentRunningJob();
        System.out.println("=========currentRuning1===========");
        if (currentRuningJob != null) {
            for (McJobDefinition df : currentRuningJob) {
                System.out.println(df.getGroupId() + "===========" + df.getName());
            }
        }

        Thread.sleep(3000l);

        // js.stop();
        System.out.println("stop.....");
        Thread.sleep(10000l);

        js.start();

        System.out.println("unRegisterJob.....");
        js.unRegisterJob(job1);
        js.unRegisterJob(job2);
        Thread.sleep(10000l);

        // js.init();
        // js.registerJob(job1);
        // js.registerJob(job2);

        js.start();

        currentRuningJob = js.getCurrentRunningJob();
        System.out.println("=========currentRuning2===========");
        if (currentRuningJob != null) {
            for (McJobDefinition df : currentRuningJob) {
                System.out.println(df.getGroupId() + "===========" + df.getName());
            }
        }

        currentStoreJob = js.getCurrentStoreJob();
        System.out.println("=========currentStore2===========");
        if (currentStoreJob != null) for (McJobDefinition df : currentStoreJob) {
            System.out.println(df.getGroupId() + "===========" + df.getName());
        }

        Thread.sleep(10000l);
        Assert.assertEquals(1, job1.getJobCount());
        Assert.assertEquals(1, job2.getJobCount());

        Assert.assertEquals(1, beforeCount1);
        Assert.assertEquals(1, afterCount1);
        Assert.assertEquals(1, beforeCount2);
        Assert.assertEquals(1, afterCount2);
        Assert.assertEquals(1, notifyCount1);
        Assert.assertEquals(1, notifyCount2);

        Thread.sleep(300000l);
    }

    // =============================JOB定义==============
    // 继承McJob ，可以实现监听接口McJobNotifyListener
    class TestJob2 extends McJob implements McJobNotifyListener {

        private int jobCount = 0;

        // 接收GROUP内，JOB间的内部消息
        public boolean onFile(McJobNotify notify) {
            System.out.println("notify2: " + notify.getGroupId() + " ::" + notify.getSource());
            notifyCount1++;
            return false;
        }

        public boolean execute() throws Exception {
            System.out.println("TestJob2.........");
            jobCount++;
            McJobNotify notify = new McJobNotify(TestJob2.class.getName());
            notify.setFromJobName(this.getDefinition().getName());
            notify.setGroupId(this.getDefinition().getGroupId());

            // 发送GROUP 内 JOB间的内部消息
            this.sendNotify(notify);
            Thread.sleep(5000);
            return true;
        }

        public int getJobCount() {
            return jobCount;
        }

    }

    class TestJob1 extends McJob implements McJobNotifyListener {

        private int jobCount = 0;

        public boolean onFile(McJobNotify notify) {
            System.out.println("notify1: " + notify.getGroupId() + " ::" + notify.getSource());
            notifyCount2++;
            return false;
        }

        public boolean execute() throws Exception {
            System.out.println("TestJob1...........");
            jobCount++;
            McJobNotify notify = new McJobNotify(TestJob2.class.getName());
            notify.setFromJobName(this.getDefinition().getName());
            notify.setGroupId(this.getDefinition().getGroupId());
            this.sendNotify(notify);
            Thread.sleep(5000);
            return true;
        }

        public int getJobCount() {
            return jobCount;
        }
    }
}
