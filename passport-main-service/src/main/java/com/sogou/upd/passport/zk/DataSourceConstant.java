package com.sogou.upd.passport.zk;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-11-13
 * Time: 下午4:56
 */
public final class DataSourceConstant {


    /**
     * master jdbc url
     */
    public static final String masterJdbcUrl = "mju";

    /**
     * slave jdbc url
     */
    public static final String slaveJdbcUrl = "sju";

    /**
     * user
     */
    public static final String user = "u";

    /**
     * pwd
     */
    public static final String password = "p";

    /**
     * driver class
     */
    public static final String driverClass = "dc";

    /**
     * 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3
     */
    public static final String acquireIncrement = "ai";


    /**
     * 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数,目前配置 5
     */
    public static final int acquireIncrement_value = 5;

    /**
     * 定义在从数据库获取新连接失败后重复尝试的次数。Default: 30
     */
    public static final String acquireRetryAttempts = "ara";

    /**
     * 定义在从数据库获取新连接失败后重复尝试的次数 目前配置 5
     */
    public static final int acquireRetryAttempts_value = 5;


    /**
     * 每60秒检查所有连接池中的空闲连接。Default: 0
     */
    public static final String idleConnectionTestPeriod = "ictp";

    /**
     * 每60秒检查所有连接池中的空闲连接 目前配置：60秒
     */
    public static final int idleConnectionTestPeriod_value = 60;

    /**
     * 当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出
     * SQLException,如设为0则无限期等待。单位毫秒。Default: 0
     */
    public static final String checkoutTimeout = "coto";

    /**
     * 当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出
     * SQLException,如设为0则无限期等待。单位毫秒。目前配置：3000（3秒）
     */
    public static final int checkoutTimeout_value = 3000;

    /**
     * 连接池中保留的最大连接数。Default: 15
     */
    public static final String maxPoolSize = "maxPs";


    /**
     * 连接池中保留的最大连接数 ,目前配置：200
     */
    public static final int maxPoolSize_value = 200;

    /**
     * 连接池中保留的最小连接数
     */
    public static final String minPoolSize = "minPs";

    /**
     * 连接池中保留的最小连接数,目前配置 30
     */
    public static final int minPoolSize_value = 30;

    /**
     * JDBC的标准参数，用以控制数据源内加载的PreparedStatements数量。但由于预缓存的statements
     * 属于单个connection而不是整个连接池。所以设置这个参数需要考虑到多方面的因素。
     * 如果maxStatements与maxStatementsPerConnection均为0，则缓存被关闭。Default: 0-->
     * 异常：APPARENT DEADLOCK!!! Creating emergency threads for unassigned pending tasks!
     * 原因：c3p0在同时关闭statement和connection的时候，或者关闭他们之间的时
     */
    public static final String maxStatements = "msm";

    /**
     * JDBC的标准参数，用以控制数据源内加载的PreparedStatements数量，目前配置 0
     */
    public static final int maxStatements_value = 0;

    /**
     * 初始化时获取三个连接，取值应在minPoolSize与maxPoolSize之间。Default: 3
     */
    public static final String initialPoolSize = "initPs";

    /**
     * 初始化pool大小，目前配置 50
     */
    public static final int initialPoolSize_value = 50;

    /**
     * 最大空闲时间,300秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0
     */
    public static final String maxIdleTime = "mit";

    /**
     * 最大空闲时间,300秒内未使用则连接被丢弃,目前配置 300秒
     */
    public static final int maxIdleTime_value = 300;

    /**
     * 获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常。但是数据源仍有效
     * 保留，并在下次调用getConnection()的时候继续尝试获取连接。如果设为true，那么在尝试
     * 获取连接失败后该数据源将申明已断开并永久关闭。Default: false
     */
    public static final String breakAfterAcquireFailure = "baaf";

    /**
     * 获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常 ，目前配置 默认 false
     */
    public static final boolean breakAfterAcquireFailure_value = false;

    /**
     * 因性能消耗大请只在需要的时候使用它。如果设为true那么在每个connection提交的
     * 时候都将校验其有效性。建议使用idleConnectionTestPeriod或automaticTestTable
     * 等方法来提升连接测试的性能。Default: false
     */
    public static final String testConnectionOnCheckout = "tcoco";

    /**
     * 因性能消耗大请只在需要的时候使用它,目前配置 false
     */
    public static final boolean testConnectionOnCheckout_value = false;

    /**
     * c3p0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能
     * 通过多线程实现多个操作同时被执行。Default: 3
     */
    public static final String numHelperThreads = "nhts";

    /**
     * c3p0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能
     * 通过多线程实现多个操作同时被执行 目前配置 10
     */
    public static final int numHelperThreads_value = 10;


}
