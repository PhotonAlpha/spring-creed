package com.ethan.identity.core.segment.dal.service.impl;

import com.ethan.identity.core.segment.dal.entity.SystemLeafAlloc;
import com.ethan.identity.core.segment.dal.repository.SystemLeafAllocRepository;
import com.ethan.identity.core.segment.dal.service.IDAllocService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class IDAllocServiceImpl implements IDAllocService {
    private final SystemLeafAllocRepository allocRepository;

    public IDAllocServiceImpl(SystemLeafAllocRepository allocRepository) {
        this.allocRepository = allocRepository;
    }

    @Override
    public List<SystemLeafAlloc> getAllLeafAllocs() {
        return allocRepository.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemLeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        allocRepository.updateMaxId(tag);
        return allocRepository.findByBizTag(tag).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemLeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(SystemLeafAlloc leafAlloc) {
        allocRepository.updateMaxIdByCustomStep(leafAlloc);
        return allocRepository.findByBizTag(leafAlloc.getBizTag()).orElse(null);
    }

    @Override
    public List<String> getAllTags() {
        return allocRepository.getAllTags();
    }

    @Override
    public SystemLeafAlloc insertLeafAlloc(SystemLeafAlloc leafAlloc) {
        return allocRepository.save(leafAlloc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLeafAlloc(String tag) {
        allocRepository.deleteSystemLeafAlloc(tag);
        return 1;
    }

}
