package com.ethan.system.service.dict;

import cn.hutool.core.collection.CollUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.controller.admin.dict.vo.data.DictDataCreateReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataUpdateReqVO;
import com.ethan.system.convert.dict.DictDataConvert;
import com.ethan.system.dal.entity.dict.DictDataDO;
import com.ethan.system.dal.entity.dict.DictTypeDO;
import com.ethan.system.dal.repository.dict.DictDataRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_DATA_NOT_ENABLE;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_DATA_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_DATA_VALUE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_NOT_ENABLE;
import static com.ethan.system.constant.ErrorCodeConstants.DICT_TYPE_NOT_EXISTS;


/**
 * 字典数据 Service 实现类
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class DictDataServiceImpl implements DictDataService {

    /**
     * 排序 dictType > sort
     */
    private static final Comparator<DictDataDO> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictDataDO::getDictType)
            .thenComparingInt(DictDataDO::getSort);

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private DictDataRepository dictDataRepository;

    @Override
    public List<DictDataDO> getDictDatas() {
        List<DictDataDO> list = dictDataRepository.findAll();
        list.sort(COMPARATOR_TYPE_AND_SORT);
        return list;
    }

    @Override
    public PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO reqVO) {
        Page<DictDataDO> page = dictDataRepository.selectPage(reqVO);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public List<DictDataDO> getDictDatas(DictDataExportReqVO reqVO) {
        List<DictDataDO> list = dictDataRepository.selectList(reqVO);
        list.sort(COMPARATOR_TYPE_AND_SORT);
        return list;
    }

    @Override
    public DictDataDO getDictData(Long id) {
        return dictDataRepository.findById(id).orElse(null);
    }

    @Override
    public Long createDictData(DictDataCreateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(null, reqVO.getValue(), reqVO.getDictType());

        // 插入字典类型
        DictDataDO dictData = DictDataConvert.INSTANCE.convert(reqVO);
        dictDataRepository.save(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictDataUpdateReqVO reqVO) {
        // 校验正确性
        checkCreateOrUpdate(reqVO.getId(), reqVO.getValue(), reqVO.getDictType());

        // 更新字典类型
        DictDataDO updateObj = DictDataConvert.INSTANCE.convert(reqVO);
        dictDataRepository.save(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // 校验是否存在
        checkDictDataExists(id);

        // 删除字典数据
        dictDataRepository.deleteById(id);
    }

    @Override
    public long countByDictType(String dictType) {
        return dictDataRepository.countByDictType(dictType);
    }


    private void checkCreateOrUpdate(Long id, String value, String dictType) {
        // 校验自己存在
        checkDictDataExists(id);
        // 校验字典类型有效
        checkDictTypeValid(dictType);
        // 校验字典数据的值的唯一性
        checkDictDataValueUnique(id, dictType, value);
    }

    @VisibleForTesting
    public void checkDictDataValueUnique(Long id, String dictType, String value) {
        DictDataDO dictData = dictDataRepository.findByDictTypeAndValue(dictType, value);
        if (dictData == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典数据
        if (id == null) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
        if (!dictData.getId().equals(id)) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void checkDictDataExists(Long id) {
        if (id == null) {
            return;
        }
        Optional<DictDataDO> dictDataOptional = dictDataRepository.findById(id);
        if (dictDataOptional.isEmpty()) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    public void checkDictTypeValid(String type) {
        DictTypeDO dictType = dictTypeService.getDictType(type);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictType.getStatus())) {
            throw exception(DICT_TYPE_NOT_ENABLE);
        }
    }

    @Override
    public void validDictDatas(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        Map<String, DictDataDO> dictDataMap = CollUtils.convertMap(
                dictDataRepository.findByDictTypeAndValueIn(dictType, values), DictDataDO::getValue);
        // 校验
        values.forEach(value -> {
            DictDataDO dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw exception(DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw exception(DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @Override
    public DictDataDO getDictData(String dictType, String value) {
        return dictDataRepository.findByDictTypeAndValue(dictType, value);
    }

    @Override
    public DictDataDO parseDictData(String dictType, String label) {
        return dictDataRepository.findByDictTypeAndLabel(dictType, label);
    }

}
