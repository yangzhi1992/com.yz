package com.commons.test.db.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author 
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbCommonEntity implements Serializable {
    private static final long serialVersionUID = -8922742799986556001L;
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}