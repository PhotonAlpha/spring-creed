package com.ethan.creedmall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.common.utils.Query;

import com.ethan.creedmall.product.dao.CategoryDao;
import com.ethan.creedmall.product.entity.CategoryEntity;
import com.ethan.creedmall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //组装成父子结构
        //1.找到所有一级分类
        List<CategoryEntity> leve1Menu = categoryEntities.stream().filter(cate -> cate.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, categoryEntities));
                    return menu;
                })
                .sorted(this::sortComparator)
                .collect(Collectors.toList());

        return leve1Menu;
    }

    @Override
    public boolean removeMenuByIds(List<Long> ids) {
        //检查当前菜单是否被引用


        //逻辑删除
        baseMapper.deleteBatchIds(ids);
        return false;
    }

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        return null;
    }

    private List<Long> findParentPath(Long catelogId) {
        var catePath = new Arrayon;
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0 && categoryEntity.getParentCid() != null) {
            this.findParentPath(categoryEntity.getCatId())
        }
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        if (root == null) {
            return null;
        }
        return all.stream().filter(cat -> cat.getParentCid() == root.getCatId())
                .map(menu -> {
                    //递归查找子菜单
                    menu.setChildren(getChildren(menu, all));
                    return menu;
                })
                .sorted(this::sortComparator)
                .collect(Collectors.toList());

    }

    private int sortComparator(CategoryEntity c1, CategoryEntity c2) {
        return (c1.getSort() == null ? 0 : c1.getSort()) - (c2.getSort() == null ? 0 : c2.getSort());
    }
}