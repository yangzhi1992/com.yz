package com.commons.elasticsearch.simple.dto;

import com.commons.common.utils.BooleanPair;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;

@Getter
@Setter
@Builder
public class IndexOption {
    /**
     * ES 客户端
     */
    private RestHighLevelClient client;
    /**
     * 消息延迟预警
     */
    private Integer delayWarnSecond;
    /**
     * 消息延迟预警
     */
    private Integer delaySeriousSecond;
    /**
     * 索引名字
     */
    private String indexName;
    /**
     * 单个索引
     */
    private MqMsg msg;
    /**
     * 批量索引
     */
    private List<MqMsg> msgList;

    public List<MqMsg> getMsgList() {
        if (msgList != null) {
            return msgList;
        }
        return msg == null ? null : Lists.newArrayList(msg);
    }

    public BooleanPair<Void> isValid() {
        if (CollectionUtils.isEmpty(getMsgList()) || getMsgList().stream().anyMatch(msg -> {
            return msg == null || msg.getData() == null || !msg.isValid().isSuccess();
        })) {
            return new BooleanPair<>(false, null, "索引数据为空或不完整");
        } else if (StringUtils.isBlank(indexName)) {
            return new BooleanPair<>(false, null, "索引名称为空");
        } else if (client == null) {
            return new BooleanPair<>(false, null, "ES客户端未注入");
        }
        return new BooleanPair<>(true, null);
    }

    public BooleanPair<Void> isWithoutPKValid() {
        if (CollectionUtils.isEmpty(getMsgList()) || getMsgList().stream().anyMatch(msg -> {
            return msg == null || msg.getData() == null;
        })) {
            return new BooleanPair<>(false, null, "索引数据为空或不完整");
        } else if (StringUtils.isBlank(indexName)) {
            return new BooleanPair<>(false, null, "索引名称为空");
        } else if (client == null) {
            return new BooleanPair<>(false, null, "ES客户端未注入");
        }
        return new BooleanPair<>(true, null);
    }
}
