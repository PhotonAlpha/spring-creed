package com.ethan.creedmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ethan.creedmall.product.entity.CategoryEntity;
import com.ethan.creedmall.product.service.CategoryService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.common.utils.R;



/**
 * 商品三级分类
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:46:13
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类以及子分类,以树形结构列出来
     */
    @RequestMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R listTree(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();

        return R.ok().put("category", categoryEntities);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    // @RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody List<CategoryEntity> category){
		categoryService.updateBatchById(category);

        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeMenuByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
