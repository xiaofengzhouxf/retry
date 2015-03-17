# retry
一个简单的重试服务


---------------------
```
    <!--1. 定义一个重试的任务远程服务 -->
	<bean id="retryTaskService" class="com.github.tinyretry.retry.MockRetryTaskService" />
	
    <!--2. 定义任务和处理器，这边写在一起了 -->
	<bean id="testTaskProcessor" class="com.github.tinyretry.retry.TestProcessor" >
        <!-- 任务名称，可以到管理页面查询 -->
        <property name="taskName" value="testTask" />
        <!-- 任务描述信息 -->
        <property name="description" value="testTask" />
        <!-- 任务失败后的重试周期 -->
        <property name="retryPeriod" value="3000" />
        <!-- 任务执行的超时时间，超时后任务会被竞争后重新创建 -->
        <property name="executeTimeOut" value="6000" />
        <!-- 任务失败后的重试周期因子 -->
        <property name="retryFactor" value="1.2" />
        <!-- 重试次数 -->
        <property name="retryTime" value="2" />
        <!-- 优先级，越大越优先 -->
        <property name="priority" value="1" />
        <!-- 任务关联的应用名，可以在管理页面查询 -->
        <property name="appCode" value="mediacodec" />
            </bean>
```

```
public class TestProcessor extends Task implements Processor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TestProcessor(){
        this.setProcessor(this);
    }

    @Override
    public ProcessResult execute(ProcessContext context) {
        // 通过context获取data. data就是retryPorcessClient.async()方法的第二个参数
        Serializable data = context.getData();

        System.out.println("receive data: ---> " + data);

        return ProcessResult.SUCCESS;
    }

}


-------


public class MockRetryTaskService implements RetryTaskService {

    ......

}



```



```
<!-- 启动的代码  -->
public class RetryServerTest {

    private ClassPathXmlApplicationContext appContext = null;
    private RetryCli                       retryCli   = null;

    @Before
    public void init() {

        //default-retry-spring.xml 默认配置在tiny-retry-client 包里面,也可以自定义
        appContext = new ClassPathXmlApplicationContext(new String[] {"classpath:default-retry-spring.xml",
                "classpath:test-tasks.xml" });

        retryCli = appContext.getBean("retryServer", RetryCli.class);

        Map<String, RetryTaskService> retryTaskServices = appContext.getBeansOfType(RetryTaskService.class);

        if (retryTaskServices == null || retryTaskServices.isEmpty()) {
            throw new NullPointerException("retryTaskServices can't be null!");
        }
        
        //服务启动
        retryCli.init();
        retryCli.start();
    }

    @Test
    public void test() throws BeansException, McJobScheduleException, RetryRemoteException, InterruptedException {

        //提交任务
        retryCli.async(appContext.getBean("testTaskProcessor", Task.class), "this is a test");

        TimeUnit.SECONDS.sleep(1);
    }

    @After
    public void after() {
        //销毁异步容器
        retryCli.destroy();
    }

}

```