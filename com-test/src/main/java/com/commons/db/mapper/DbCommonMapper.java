package com.commons.db.mapper;

import com.commons.db.annotation.DataSource;
import com.commons.db.model.DbCommonEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DataSource(value = "xxx")
public interface DbCommonMapper {
    List<Object> selectDb(DbCommonEntity example);
}