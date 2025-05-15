package com.ethan.system.service.file;


import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.common.utils.validation.ValidationUtils;
import com.ethan.system.constant.file.FileStorageEnum;
import com.ethan.system.controller.admin.file.vo.config.FileClientConfig;
import com.ethan.system.controller.admin.file.vo.config.FileConfigCreateReqVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigPageReqVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigUpdateReqVO;
import com.ethan.system.convert.file.FileConfigConvert;
import com.ethan.system.dal.entity.file.FileConfigDO;
import com.ethan.system.dal.repository.file.FileConfigRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.ethan.system.constant.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;


/**
 * 文件配置 Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class FileConfigServiceImpl implements FileConfigService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 缓存菜单的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    private volatile ZonedDateTime maxUpdateTime;

    // @Resource
    // private FileClientFactory fileClientFactory;
    /**
     * Master FileClient 对象，有且仅有一个，即 {@link FileConfigDO#getMaster()} 对应的
     */
    // @Getter
    // private FileClient masterFileClient;

    @Resource
    private FileConfigRepository fileConfigRepository;

    // @Resource
    // private FileConfigProducer fileConfigProducer;

    @Resource
    private Validator validator;

    @Resource
    @Lazy // 注入自己，所以延迟加载
    private FileConfigService self;

    @Override
    @PostConstruct
    public void initFileClients() {
        // 获取文件配置，如果有更新
        List<FileConfigDO> configs = loadFileConfigIfUpdate(maxUpdateTime);
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }

        // 创建或更新支付 Client
        // configs.forEach(config -> {
        //     fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(), config.getConfig());
        //     // 如果是 master，进行设置
        //     if (Boolean.TRUE.equals(config.getMaster())) {
        //         masterFileClient = fileClientFactory.getFileClient(config.getId());
        //     }
        // });

        // 写入缓存 TODO
        maxUpdateTime = ZonedDateTime.ofInstant(CollUtils.getMaxValue(configs, FileConfigDO::getUpdateTime), ZoneId.systemDefault());
        // maxUpdateTime = CollUtils.getMaxValue(configs, FileConfigDO::getUpdateTime);
        log.info("[initFileClients][初始化 FileConfig 数量为 {}]", configs.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        self.initFileClients();
    }

    /**
     * 如果文件配置发生变化，从数据库中获取最新的全量文件配置。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前文件配置的最大更新时间
     * @return 文件配置列表
     */
    private List<FileConfigDO> loadFileConfigIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadFileConfigIfUpdate][首次加载全量文件配置]");
        } else { // 判断数据库中是否有更新的文件配置
            if (fileConfigRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadFileConfigIfUpdate][增量加载全量文件配置]");
        }
        // 第二步，如果有更新，则从数据库加载所有文件配置
        return fileConfigRepository.findAll();
    }

    @Override
    public Long createFileConfig(FileConfigCreateReqVO createReqVO) {
        // 插入
        FileConfigDO fileConfig = FileConfigConvert.INSTANCE.convert(createReqVO);
        // fileConfig.setConfig(parseClientConfig(createReqVO.getStorage(), createReqVO.getConfig()));
        fileConfig.setMaster(false); // 默认非 master
        fileConfigRepository.save(fileConfig);
        // 发送刷新配置的消息
        // fileConfigProducer.sendFileConfigRefreshMessage();
        // 返回
        return fileConfig.getId();
    }

    @Override
    public void updateFileConfig(FileConfigUpdateReqVO updateReqVO) {
        // 校验存在
        FileConfigDO config = this.validateFileConfigExists(updateReqVO.getId());
        // 更新
        FileConfigDO updateObj = FileConfigConvert.INSTANCE.convert(updateReqVO);
        // updateObj.setConfig(parseClientConfig(config.getStorage(), updateReqVO.getConfig()));
        fileConfigRepository.save(updateObj);
        // 发送刷新配置的消息
        // fileConfigProducer.sendFileConfigRefreshMessage();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfigMaster(Long id) {
        // 校验存在
        this.validateFileConfigExists(id);
        // 更新其它为非 master
        fileConfigRepository.save(new FileConfigDO().setMaster(false));
        // 更新
        fileConfigRepository.save(new FileConfigDO().setId(id).setMaster(true));
        // 发送刷新配置的消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                // fileConfigProducer.sendFileConfigRefreshMessage();
            }

        });
    }

    private FileClientConfig parseClientConfig(Integer storage, Map<String, Object> config) {
        // 获取配置类
        Class<? extends FileClientConfig> configClass = (Class<? extends FileClientConfig>) FileStorageEnum.getByStorage(storage)
                .getConfigClass();
        FileClientConfig clientConfig = JacksonUtils.parseObject2(JacksonUtils.toJsonString(config), configClass);
        // 参数校验
        ValidationUtils.validate(validator, clientConfig);
        // 设置参数
        return clientConfig;
    }

    @Override
    public void deleteFileConfig(Long id) {
        // 校验存在
        FileConfigDO config = this.validateFileConfigExists(id);
        if (Boolean.TRUE.equals(config.getMaster())) {
             throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        // 删除
        fileConfigRepository.deleteById(id);
        // 发送刷新配置的消息
        // fileConfigProducer.sendFileConfigRefreshMessage();
    }

    private FileConfigDO validateFileConfigExists(Long id) {
        Optional<FileConfigDO> configOptional = fileConfigRepository.findById(id);
        if (configOptional.isEmpty()) {
            throw exception(FILE_CONFIG_NOT_EXISTS);
        }
        return configOptional.get();
    }

    @Override
    public FileConfigDO getFileConfig(Long id) {
        return fileConfigRepository.findById(id).orElse(null);
    }

    @Override
    public List<FileConfigDO> getFileConfigList(Collection<Long> ids) {
        return fileConfigRepository.findAllById(ids);
    }

    @Override
    public Page<FileConfigDO> getFileConfigPage(FileConfigPageReqVO pageReqVO) {
        return fileConfigRepository.findAll(getFileConfigPageSpecification(pageReqVO), PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
    }

    private static Specification<FileConfigDO> getFileConfigPageSpecification(FileConfigPageReqVO reqVO) {
        return (Specification<FileConfigDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                        "%" + reqVO.getName().toLowerCase() + "%"));
            }
            if (Objects.nonNull(reqVO.getStorage())) {
                predicateList.add(cb.equal(root.get("storage"), reqVO.getStorage()));
            }
            if (Objects.nonNull(reqVO.getCreateTime())) {
                predicateList.add(cb.greaterThan(root.get("createTime"), reqVO.getCreateTime()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public String testFileConfig(Long id) throws Exception {
        // 校验存在
        // this.validateFileConfigExists(id);
        // 上传文件
        // byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        // return fileClientFactory.getFileClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg");
        return null;
    }

    // @Override
    // public FileClient getFileClient(Long id) {
    //     return fileClientFactory.getFileClient(id);
    // }

}
