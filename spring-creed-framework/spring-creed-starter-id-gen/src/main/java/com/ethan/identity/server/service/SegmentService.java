package com.ethan.identity.server.service;

import com.ethan.identity.core.IDGen;
import com.ethan.identity.core.common.Result;
import com.ethan.identity.core.common.ZeroIDGen;
import com.ethan.identity.core.segment.SegmentIDGenImpl;
import com.ethan.identity.core.segment.dal.service.IDAllocService;
import com.ethan.identity.server.config.IdentityProperties;
import com.ethan.identity.server.exception.InitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
public class SegmentService {

    private final IDGen idGen;
    private final IdentityProperties identityProperties;

    public SegmentService(IdentityProperties identityProperties, @Autowired(required = false) IDAllocService allocService) throws InitException {
        this.identityProperties = identityProperties;
        boolean flag = Optional.ofNullable(this.identityProperties.getSegment()).map(IdentityProperties.SegmentProperties::getEnable).orElse(Boolean.FALSE);
        if (flag) {
            idGen = new SegmentIDGenImpl(allocService);
            if (idGen.init()) {
                log.info("Segment Service Init Successfully");
            } else {
                throw new InitException("Segment Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            log.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }
}
