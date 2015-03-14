package com.github.tinyretry.timer.builder;

import java.util.HashMap;
import java.util.Map;

import com.github.tinyretry.timer.McJobScheduleException;
import com.github.tinyretry.timer.McJobScheduleFactory;
import com.github.tinyretry.timer.domain.McJobGroup;

/**
 * <pre>
 * desc: 基于内存job仓库的调度服务，job被注册后会被存入内存仓库，以后执行都会从仓库中取，不重复创建对象
 * 使用方法：
 * 
 * 1. 创建job
 * 
 * 创建McJobGroup,定义cornExpression或者repeatInterval，定义groupId
 * 
 *  McJobGroup perTowSec = new McJobGroup();
 * 	towPerSecond.setGroupId("testGroup");
 *  //注意，这里必须是0
 * 	towPerSecond.setRepeatInterval(0);
 * 
 * 
 * 
 * 继承McJob创建job，实现execute()方法
 * 
 * class TestJob extends McJob implements McJobNotifyListener {
 * 		public boolean onFile(McJobNotify notify) {
 * 			.....
 * 		}
 * 		public void execute() throws Exception {
 * 			.....
 * 		}
 * 	}
 * 
 * 
 *  McJobDefinition definition = new McJobDefinition();
 *  //这里注意别有重复的名称同时注册，因为容器中以名称来定位
 *  definition.setName("TestJob");
 *  definition.setGroupId("testGroup");
 *  TestJob testJob= new TestJob()
 * 
 * 2. 创建调度服务，启动并注册job
 *  
 * McJobScheduleBuilder mjsb = new DisposableMcJobScheduleBuilder();
 * mjsb.createMcJobSchedule("TestSchedule",10).addGroups(perTowSec).executeJob(testJob);
 * 
 * 
 * 
 * created: 2012-9-20 下午01:21:56
 * author: xiaofeng.zhouxf
 * todo: 
 * history:
 * </pre>
 */
public class SimpleMcJobScheduleBuilder extends McJobScheduleBuilder {

    private static final Map<String, McJobGroup> groupCache = new HashMap<String, McJobGroup>();

    /**
     * 创建调度容器
     * 
     * @throws McJobScheduleException
     */
    public McJobScheduleBuilder createMcJobSchedule(String scheduleName, int maxThreads) throws McJobScheduleException {
        this.schedule = McJobScheduleFactory.createMcJobSchedule(maxThreads, scheduleName, scheduleName
                                                                                           + INSTANT_SUFFIX);
        return this;
    }

    /**
     * 添加分组
     * 
     * @param repeatInterval
     * @param repeatCount
     * @return
     */
    public McJobScheduleBuilder addGroup(int repeatInterval, int repeatCount) {
        String groupId = repeatInterval + "-" + repeatCount;

        McJobGroup tmpGroup = groupCache.get(groupId);
        if (tmpGroup == null) {
            tmpGroup = new McJobGroup(groupId, repeatInterval, repeatCount);
        }

        groupCache.put(groupId, tmpGroup);

        this.addGroups(tmpGroup);

        return this;
    }

}
