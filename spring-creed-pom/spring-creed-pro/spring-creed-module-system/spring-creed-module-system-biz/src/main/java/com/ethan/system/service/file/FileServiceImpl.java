package com.ethan.system.service.file;


import cn.hutool.core.util.StrUtil;
import com.ethan.system.api.utils.FileTypeUtils;
import com.ethan.system.api.utils.FileUtilX;
import com.ethan.system.controller.admin.file.vo.file.FilePageReqVO;
import com.ethan.system.dal.entity.file.FileDO;
import com.ethan.system.dal.repository.file.FileRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.FILE_NOT_EXISTS;

/**
 * 文件 Service 实现类
 *
 *
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileConfigService fileConfigService;

    @Resource
    private FileRepository fileRepository;

    @Override
    public Page<FileDO> getFilePage(FilePageReqVO pageReqVO) {
        return fileRepository.findAll(getFilePageSpecification(pageReqVO), PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
    }

    private static Specification<FileDO> getFilePageSpecification(FilePageReqVO reqVO) {
        return (Specification<FileDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getPath())) {
                predicateList.add(cb.like(cb.lower(root.get("path").as(String.class)),
                        "%" + reqVO.getPath() + "%"));
            }
            if (Objects.nonNull(reqVO.getType())) {
                predicateList.add(cb.equal(root.get("type"), reqVO.getType()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    @SneakyThrows
    public String createFile(String name, String path, byte[] content) {
        // 计算默认的 path 名
        String type = FileTypeUtils.getMineType(content, name);
        if (StrUtil.isEmpty(path)) {
            path = FileUtilX.generatePath(content, name);
        }
        // 如果 name 为空，则使用 path 填充
        if (StrUtil.isEmpty(name)) {
            name = path;
        }

        // 上传到文件存储器
        // FileClient client = fileConfigService.getMasterFileClient();
        // Assert.notNull(client, "客户端(master) 不能为空");
        String url = "";//client.upload(content, path);

        // 保存到数据库
        FileDO file = new FileDO();
        // file.setConfigId(client.getId());
        file.setName(name);
        file.setPath(path);
        file.setUrl(url);
        file.setType(type);
        file.setSize(content.length);
        fileRepository.save(file);
        return url;
    }

    @Override
    public void deleteFile(Long id) throws Exception {
        // 校验存在
        FileDO file = this.validateFileExists(id);

        // 从文件存储器中删除
        // FileClient client = fileConfigService.getFileClient(file.getConfigId());
        // Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        // client.delete(file.getPath());

        // 删除记录
        fileRepository.deleteById(id);
    }

    private FileDO validateFileExists(Long id) {
        FileDO fileDO = fileRepository.findById(id).orElse(null);
        if (fileDO == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    @Override
    public byte[] getFileContent(Long configId, String path) throws Exception {
        // FileClient client = fileConfigService.getFileClient(configId);
        // Assert.notNull(client, "客户端({}) 不能为空", configId);
        // return client.getContent(path);
        return null;
    }

}
