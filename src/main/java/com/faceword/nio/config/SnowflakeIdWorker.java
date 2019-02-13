package com.faceword.nio.config;


/**
 * Twitter 雪花算法生成分布式唯一ID<br>
 * 算法说明：SnowFlake的结构说明：
 * 示例：0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * （1）、1位标识为是符号位，正数是0，负数是1，所以id一般是正数，最高位是默认为0 <br>
 * （2）、41位时间截（毫秒级），41位时间截存储当前时间和开始时间的时间戳差值（当前时间截 - 开始时间截）<br>
 *        开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（即程序IdWorker类的startTime属性） <br>
 *        41位的时间截，可以使用69年 <br>
 * （3）、10位的数据机器位，可以部署在1024个节点，包括5位数据中心ID（datacenterId）和5位服务器标示ID（workerId）<br>
 * （4）、12位序列，毫秒内的计数器，12位的计数顺序号支持每个节点每毫秒（同一机器，同一时间截）产生4096个ID序号，加起来刚好64位，为一个Long型<br>
 *
 * 算法优势：整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由数据中心ID和机器ID作区分），
 *          并且效率较高，SnowFlake每秒能够产生4096000个ID左右 <br>
 */
public class SnowflakeIdWorker {

    /**
     * 开始时间截 （2018-09-03） <br>
     */
    private final long twepoch = 1535982635L;

    /**
     * 机器id所占的位数 <br>
     */
    private final long workerIdBits = 5L;

    /**
     * 数据中心标识id所占的位数 <br>
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 （这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数） <br>
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31  <br>
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数  <br>
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位 <br>
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据中心标识id向左移17位（12+5） <br>
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位（5+5+12）
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 （0b111111111111=0xfff=4095） <br>
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID（0~31） <br>
     */
    private long workerId;

    /**
     * 数据中心ID（0~31） <br>
     */
    private long datacenterId;

    /**
     * 毫秒内序列（0~4095）<br>
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截 <br>
     */
    private long lastTimestamp = -1L;


    /**
     * 构造函数 （标示数据中心和机器ID码） <br>
     * @param workerId 工作ID （0~31）
     * @param datacenterId 数据中心ID （0~31）
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("机器标示ID不能大于最大机器ID也不能小于0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("数据中心标示ID不能大于数据中心ID最大值也不能小于0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 生成下一个ID （线程安全）
     * @return SnowflakeId 返回唯一标示ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //时间回退抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else { //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;

    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳 <br>
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回当前时间毫秒数 <br>
     * @return 当前时间毫秒数
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}