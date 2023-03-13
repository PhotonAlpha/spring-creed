package com.ethan.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowFlakeIdGenerator implements IdentifierGenerator {
    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return snowflake.nextId() + "";
    }


}
