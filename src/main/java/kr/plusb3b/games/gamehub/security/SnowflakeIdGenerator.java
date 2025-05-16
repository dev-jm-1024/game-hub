package kr.plusb3b.games.gamehub.security;


public class SnowflakeIdGenerator {
    private final long workerId;
    private final long datacenterId;
    private final long sequenceBits = 12L;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & ((1L << sequenceBits) - 1);
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - 1609459200000L) << 22) | (datacenterId << 17) | (workerId << 12) | sequence;
    }
}
