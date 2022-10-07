package com.ethan.std;

import com.ethan.std.provisioning.OauthAccessToken;
import com.ethan.std.provisioning.OauthAccessTokenRepository;
import com.ethan.std.provisioning.UserProfile;
import com.ethan.std.provisioning.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/3/2022 4:48 PM
 */
@SpringBootTest(classes = OAuthStandApplication.class)
public class OAuthStandApplicationTest {
    @Autowired
    private UserProfileRepository profileRepository;
    @Autowired
    private OauthAccessTokenRepository accessTokenRepository;

    @Test
    void findByName() {

        Optional<UserProfile> userProfileOptional = profileRepository.findByUsername("ethan");
        System.out.println(userProfileOptional.get());
    }
    @Test
    void findTokenByAuthenticationId() {

        // Optional<OauthAccessToken> tokenByAuthenticationId = accessTokenRepository.findFirstByAuthenticationIdOrderByCreateTimeAsc("73e845d06771e5914666f938f07d478d");
        // System.out.println(tokenByAuthenticationId.get());
    }
}
