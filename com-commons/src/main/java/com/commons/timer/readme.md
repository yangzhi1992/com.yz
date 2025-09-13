1、java 自带定时任务 Timer，TimerTask
    线程模型	    单线程
    异常处理	    未捕获异常会终止整个Timer
    任务调度精度	依赖系统时钟，精度较低
    任务隔离性	差，一个任务异常会影响所有任务
    灵活性	    有限
2、spring 自带定时任务  @EnableAsync（@EnableAsync + @Async），@EnableScheduling（@Scheduled + @EnableScheduling），SpringScheduler 三种使用
    SpringScheduler是单线程调度（@Scheduled + @EnableScheduling），SpringScheduler2是多线程调用（@Scheduled + @EnableScheduling + @EnableAsync + @Async）
    TaskScheduler的使用SchedulerConfig
    @EnableAsync+@Async 注解多线程使用及配合自定义线程池使用
    @Scheduled和TaskScheduler的概述
    特性	            @Scheduled	                                                            TaskScheduler
    定位	            用来定义简单、固定的定时任务	                                                提供基于灵活编程的定时任务调度能力
    使用方式	        声明式，注解驱动方式	                                                    编程式， 需要更灵活的控制
    任务表现形式	    执行特定方法	                                                            手动注册可运行的任务（Runnable 或 Callable 等）
    线程池支持	    与全局任务调度器共用，默认单线程（可通过配置 @EnableAsync 支持多线程调度）	        可以自定义线程池
    适用场景	        简单任务，周期性调度（默认是单线程运行，可能会因任务阻塞影响其他任务）	            灵活复杂任务调度，尤其需要动态创建/取消任务的场景
    任务终止/取消	    不支持，在应用运行期间会一直执行	                                            支持通过 ScheduledFuture 停止任务
3、rocketmq 定时任务
4、docker 定时任务
5、timewheel 时间轮 HashedWheelTimer
    引用jar:
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.97.Final</version>
    </dependency>
6、quartz 定时任务
7、xxl-job 分布式定时任务  
    https://github.com/xuxueli/xxl-job?tab=readme-ov-file
    https://www.xuxueli.com/xxl-job/
    引用jar:
    <!-- http://repo1.maven.org/maven2/com/xuxueli/xxl-job-core/ -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-job-core</artifactId>
        <version>2.4.2</version>
    </dependency>

 


    
    

