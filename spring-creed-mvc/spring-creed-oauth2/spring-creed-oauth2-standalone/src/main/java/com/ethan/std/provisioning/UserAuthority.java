package com.ethan.std.provisioning;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 5:21 PM
 */
@Data
@Entity
@Table(name = "e_users")
public class UserAuthority implements Serializable {
}
