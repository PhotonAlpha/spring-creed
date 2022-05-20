package com.ethan.creedmall.product.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.common.utils.Query;

import com.ethan.creedmall.product.dao.AttrGroupDao;
import com.ethan.creedmall.product.entity.AttrGroupEntity;
import com.ethan.creedmall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        IPage<AttrGroupEntity> page;
        if (catId == 0) {
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
        } else {
            String key = (String) params.get("key");
            QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catelog_id", catId);
            if (StringUtils.isNotBlank(key)) {
                queryWrapper.and(obj -> {
                    obj.like("attr_group_name", key)
                            .or().like("attr_group_id", key);
                });
            }
            page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );
        }
        return new PageUtils(page);
    }

}