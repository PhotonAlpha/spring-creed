package com.ethan.system.service.dict;

import cn.hutool.core.util.StrUtil;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeCreateReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeExportReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeUpdateReqVO;
import com.ethan.system.convert.dict.DictTypeConvert;
import com.ethan.system.dal.entity.dict.DictTypeDO;
import com.ethan.system.dal.repository.dict.DictTypeRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_HAS_CHILDREN;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_TYPE_DUPLICATE;

/**
 * 字典类型 Service 实现类
 */
@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeRepository dictTypeRepository;

    @Override
    public PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO reqVO) {
        Page<DictTypeDO> page = dictTypeRepository.findPage(reqVO);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public List<DictTypeDO> getDictTypeList(DictTypeExportReqVO reqVO) {
        return dictTypeRepository.findList(reqVO);
    }

    @Override
    public DictTypeDO getDictType(Long id) {
        return dictTypeRepository.findById(id).orElse(null);
    }

    @Override
    public DictTypeDO getDictType(String type) {
        return dictTypeRepository.findByType(type);
    }

    @Override
    public Long createDictType(DictTypeCreateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(null, reqVO.getName(), reqVO.getType());
        // 插入字典类型
        DictTypeDO dictType = DictTypeConvert.INSTANCE.convert(reqVO);
        dictTypeRepository.save(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictTypeUpdateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(reqVO.getId(), reqVO.getName(), null);
        // 更新字典类型
        DictTypeDO updateObj = DictTypeConvert.INSTANCE.convert(reqVO);
        dictTypeRepository.save(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictTypeDO dictType = checkDictTypeExists(id);
        // 校验是否有字典数据
        if (dictDataService.countByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // 删除字典类型
        dictTypeRepository.deleteById(id);
    }

    @Override
    public List<DictTypeDO> getDictTypeList() {
        return dictTypeRepository.findAll();
    }

    private void checkCreateOrUpdate(Long id, String name, String type) {
        // 校验自己存在
        checkDictTypeExists(id);
        // 校验字典类型的名字的唯一性
        checkDictTypeNameUnique(id, name);
        // 校验字典类型的类型的唯一性
        checkDictTypeUnique(id, type);
    }

    @VisibleForTesting
    public void checkDictTypeNameUnique(Long id, String name) {
        DictTypeDO dictType = dictTypeRepository.findByName(name);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void checkDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictTypeDO dictType = dictTypeRepository.findByType(type);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public DictTypeDO checkDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = dictTypeRepository.findById(id).orElse(null);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

}
