package com.ethan.security.utils;

import com.ethan.common.common.SnowflakeIdWorker;
import com.ethan.common.utils.ApplicationContextHolder;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowFlakeIdGenerator implements IdentifierGenerator {
    @Override
    public String generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        SnowflakeIdWorker idWorker = ApplicationContextHolder.getBean(SnowflakeIdWorker.class);
        return idWorker.nextId() + "";
    }


}
