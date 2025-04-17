package com.ethan.validator.repository;

import com.ethan.validator.context.JsonSchemaValidated;
import com.ethan.validator.controller.vo.AccountInfoVO;
import com.ethan.validator.controller.vo.AddressManagementVO;
import com.ethan.validator.controller.vo.AddressVO;
import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import com.ethan.validator.controller.vo.ProfileVO;
import com.ethan.validator.controller.vo.RealNameAuthenticationVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Service
@Validated
public class ArtisanDao {
    public static final List<MyAccountDetailsVO> MAPPING = new ArrayList<>();
    static {
        var am = AddressManagementVO.builder()
                .tags(Arrays.asList("家",
                        "公司",
                        "学校"))
                .customTags(Collections.singletonList("乌托邦"))
                .address(
                        Collections.singletonList(
                                AddressVO.builder().consignee("xxx")
                                        .contactCountryCode("+86")
                                        .contactNo("130")
                                        .location("上海汉庭酒店")
                                        .addressDetail("6栋620号")
                                        .defaultX(true)
                                        .tag("乌托邦").build()
                        )
                ).build();
        var accountInfo = AccountInfoVO.builder().realNameAuthentication(
                RealNameAuthenticationVO.builder().nameX("xxx").identityNo("32xx").build()
        ).build();
        var profileVO = ProfileVO.builder()
                .avatar("https://xxxx.jpg")
                .accountName("journey")
                .nickName("journey")
                .accountInfo(accountInfo)
                .addressManagement(am).build();

        MAPPING.add(new MyAccountDetailsVO("1", "SPORT", "arb", "pwd", "abc@gmail.com", "male", "1234656", profileVO));
        MAPPING.add(new MyAccountDetailsVO("2", "SINGER", "berry", "pwd", "abc@gmail.com", "male", "1234656", profileVO));
    }
    public boolean existsByNameOrEmailOrPhone(String name, String email, String phone) {

        return MAPPING.stream().anyMatch(a -> StringUtils.equals(name, a.getName())
                || StringUtils.equals(email, a.getEmail())
                || StringUtils.equals(phone, a.getPhone())
        );
    }

    public List<MyAccountDetailsVO> findByNameOrEmailOrPhone(String name, String email, String phone) {
        return MAPPING.stream().filter(a -> StringUtils.equals(name, a.getName())
                || StringUtils.equals(email, a.getEmail())
                || StringUtils.equals(phone, a.getPhone())).toList();
    }
    public MyAccountDetailsVO findById(String id) {
        return MAPPING.stream().filter(a -> StringUtils.equals(id, a.getId())).findFirst().orElse(null);

    }

    public MyAccountDetailsVO save(@JsonSchemaValidated(schemaUri = "my-account-create") MyAccountDetailsVO user) {
        if (StringUtils.isNotBlank(user.getId())) {
            MyAccountDetailsVO artisan = findById(user.getId());
            if (Objects.nonNull(artisan)) {
                // update
                BeanUtils.copyProperties(user, artisan);
                return artisan;
            }
        }
        var comparator = new Comparator<MyAccountDetailsVO>() {
            @Override
            public int compare(MyAccountDetailsVO o1, MyAccountDetailsVO o2) {
                return NumberUtils.compare(Long.parseLong(o1.getId()), Long.parseLong(o2.getId()));
            }
        };
        var maxId = MAPPING.stream().max(comparator).map(MyAccountDetailsVO::getId).orElse("0");
        user.setId((NumberUtils.toLong(maxId) + 1) + "");
        MAPPING.add(user);
        return user;
    }
}
