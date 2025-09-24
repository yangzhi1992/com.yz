package com.commons.snowflake;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnowflakeWorker {
    private String name;
    private Long heartbeat;

    public boolean isExpire(int expireSecond) {
        return System.currentTimeMillis() - heartbeat > 1000L * expireSecond;
    }
}
