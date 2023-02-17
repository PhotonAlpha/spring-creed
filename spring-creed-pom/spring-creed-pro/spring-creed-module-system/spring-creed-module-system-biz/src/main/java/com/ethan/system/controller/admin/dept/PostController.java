package com.ethan.system.controller.admin.dept;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostExcelVO;
import com.ethan.system.controller.admin.dept.vo.post.PostExportReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostRespVO;
import com.ethan.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.ethan.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.ethan.system.convert.dept.PostConvert;
import com.ethan.system.dal.entity.dept.PostDO;
import com.ethan.system.service.dept.PostService;
import com.ethan.web.operatelog.annotations.OperateLog;
import com.ethan.web.operatelog.constant.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - 岗位")
@RestController
@RequestMapping("/system/post")
@Validated
public class PostController {

    @Resource
    private PostService postService;

    @PostMapping("/create")
    @Schema(name = "创建岗位")
    @PreAuthorize("@ss.hasPermission('system:post:create')")
    public R<Long> createPost(@Valid @RequestBody PostCreateReqVO reqVO) {
        Long postId = postService.createPost(reqVO);
        return success(postId);
    }

    @PutMapping("/update")
    @Schema(name = "修改岗位")
    @PreAuthorize("@ss.hasPermission('system:post:update')")
    public R<Boolean> updatePost(@Valid @RequestBody PostUpdateReqVO reqVO) {
        postService.updatePost(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除岗位")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public R<Boolean> deletePost(@RequestParam("id") Long id) {
        postService.deletePost(id);
        return success(true);
    }

    @GetMapping(value = "/get")
    @Schema(name = "获得岗位信息")
    @Parameter(name = "id", description = "岗位编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public R<PostRespVO> getPost(@RequestParam("id") Long id) {
        return success(PostConvert.INSTANCE.convert(postService.getPost(id)));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获取岗位精简信息列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public R<List<PostSimpleRespVO>> getSimplePosts() {
        // 获得岗位列表，只要开启状态的
        List<PostDO> list = postService.getPosts(null, Collections.singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 排序后，返回给前端
        list.sort(Comparator.comparing(PostDO::getSort));
        return success(PostConvert.INSTANCE.convertList02(list));
    }

    @GetMapping("/page")
    @Schema(name = "获得岗位分页列表")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public R<Page<PostRespVO>> getPostPage(@Validated PostPageReqVO reqVO) {
        return success(PostConvert.INSTANCE.convertPage(postService.getPostPage(reqVO)));
    }

    @GetMapping("/export")
    @Schema(name = "岗位管理")
    @PreAuthorize("@ss.hasPermission('system:post:export')")
    @OperateLog(type = OperateTypeEnum.EXPORT)
    public void export(HttpServletResponse response, @Validated PostExportReqVO reqVO) throws IOException {
        List<PostDO> posts = postService.getPosts(reqVO);
        List<PostExcelVO> data = PostConvert.INSTANCE.convertList03(posts);
        // 输出
        // ExcelUtils.write(response, "岗位数据.xls", "岗位列表", PostExcelVO.class, data);
    }

}
