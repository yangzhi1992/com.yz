package com.commons.es.simple.dto;

import com.commons.common.utils.BooleanPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MqMsg<T extends EsDoc> {
    /**
     * 数据产生项目
     */
    private String bornServiceName;
    /**
     * 数据产生IP
     */
    private String bornIp;
    /**
     * 数据产生时间
     */
    private Long bornTime;
    /**
     * 操作类型
     */
    private EsOperateType opType;
    /**
     * 数据实体
     */
    private T data;

    public BooleanPair<Void> isValid() {
        if (opType == null) {
            return new BooleanPair<>(false, null, "操作类型为空");
        }
        if (data.getPk() == null) {
            return new BooleanPair<>(false, null, "主键为空");
        }
        return new BooleanPair<>(true, null);
    }
}
