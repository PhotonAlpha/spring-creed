package com.ethan.system.dal.entity.file;

import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 文件内容表
 * <p>
 * 专门用于存储 {@link cn.iocoder.yudao.framework.file.core.client.db.DBFileClient} 的文件内容
 *
 * 
 */
@Table(name = "infra_file_content")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileContentDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    /**
     * 配置编号
     * <p>
     * 关联 {@link FileConfigDO#getId()}
     */
    private Long configId;
    /**
     * 路径，即文件名
     */
    private String path;
    /**
     * 文件内容
     */
    private byte[] content;

}
