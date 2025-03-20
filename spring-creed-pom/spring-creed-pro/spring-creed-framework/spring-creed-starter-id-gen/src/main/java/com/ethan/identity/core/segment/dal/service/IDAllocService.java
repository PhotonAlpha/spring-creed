package com.ethan.identity.core.segment.dal.service;

import com.ethan.identity.core.segment.dal.entity.SystemLeafAlloc;

import java.util.List;

public interface IDAllocService {
     List<SystemLeafAlloc> getAllLeafAllocs();

     SystemLeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
     SystemLeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(SystemLeafAlloc leafAlloc);

     SystemLeafAlloc insertLeafAlloc(SystemLeafAlloc leafAlloc);

     int deleteLeafAlloc(String tag);

     List<String> getAllTags();
}
