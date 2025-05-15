package com.ethan.identity.server.service;

import com.ethan.identity.core.IDGen;
import com.ethan.identity.core.common.Result;
import com.ethan.identity.core.common.ZeroIDGen;
import com.ethan.identity.core.snowflake.SnowflakeIDGenImpl;
import com.ethan.identity.server.config.IdentityProperties;
import com.ethan.identity.server.exception.InitException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Slf4j
public class SnowflakeService {

    private final IDGen idGen;
    private final IdentityProperties identityProperties;

    public SnowflakeService(IdentityProperties identityProperties) throws InitException {
        this.identityProperties = identityProperties;
        boolean flag = Optional.ofNullable(this.identityProperties.getSnowflake()).map(IdentityProperties.SnowflakeProperties::getEnable).orElse(Boolean.FALSE);
        if (flag) {
            var zkProperties = Optional.ofNullable(this.identityProperties.getSnowflake()).map(IdentityProperties.SnowflakeProperties::getZk);
            var address = zkProperties.map(IdentityProperties.ZkProperties::getAddress).orElse(StringUtils.EMPTY);
            Integer port = zkProperties.map(IdentityProperties.ZkProperties::getPort).orElse(-1);
            idGen = new SnowflakeIDGenImpl(address, port);
            if(idGen.init()) {
                log.info("Snowflake Service Init Successfully");
            } else {
                throw new InitException("Snowflake Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            log.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }
}
