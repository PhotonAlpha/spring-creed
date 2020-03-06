package com.ethan.auth.service.impl;

import com.ethan.auth.config.ServerConfig;
import com.ethan.auth.constants.ResponseEnum;
import com.ethan.auth.constants.UrlEnum;
import com.ethan.auth.domain.AccessToken;
import com.ethan.auth.dto.LoginUserDTO;
import com.ethan.auth.dto.UserDTO;
import com.ethan.auth.model.Role;
import com.ethan.auth.model.User;
import com.ethan.auth.repository.UserRepository;
import com.ethan.auth.service.RoleService;
import com.ethan.auth.service.UserService;
import com.ethan.auth.utils.BeanUtils;
import com.ethan.auth.utils.RedisUtil;
import com.ethan.auth.vo.LoginUserVO;
import com.ethan.auth.vo.ResponseVO;
import com.ethan.auth.vo.RoleVO;
import com.ethan.auth.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ethan.auth.config.OAuth2Config.*;
import static com.ethan.auth.config.OAuth2Config.CLIENT_ID;
import static com.ethan.auth.config.OAuth2Config.CLIENT_SECRET;
import static com.ethan.auth.config.OAuth2Config.GRANT_TYPE;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTokenStore redisTokenStore;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO userDTO)  {
        User userPO = new User();
        User userByAccount = userRepository.findUserByAccount(userDTO.getAccount());
        if(userByAccount != null){
            //此处应该用自定义异常去返回，在这里我就不去具体实现了
            try {
                throw new Exception("This user already exists!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userPO.setCreatedTime(new Date());
        //添加用户角色信息
        Role rolePO = roleService.findById(userDTO.getRoleId());
        userPO.setRole(rolePO);
        BeanUtils.copyPropertiesIgnoreNull(userDTO,userPO);
        userRepository.save(userPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id)  {
        User userPO = userRepository.findById(id).get();
        if(userPO == null){
            //此处应该用自定义异常去返回，在这里我就不去具体实现了
            try {
                throw new Exception("This user not exists!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userRepository.delete(userPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        User userPO = userRepository.findById(userDTO.getId()).get();
        if(userPO == null){
            //此处应该用自定义异常去返回，在这里我就不去具体实现了
            try {
                throw new Exception("This user not exists!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BeanUtils.copyPropertiesIgnoreNull(userDTO, userPO);
        //修改用户角色信息
        Role rolePO = roleService.findById(userDTO.getRoleId());
        userPO.setRole(rolePO);
        userRepository.saveAndFlush(userPO);
    }

    @Override
    public ResponseVO<List<UserVO>> findAllUserVO() {
        List<User> userPOList = userRepository.findAll();
        List<UserVO> userVOList = new ArrayList<>();
        userPOList.forEach(userPO->{
            UserVO userVO = new UserVO();
            BeanUtils.copyPropertiesIgnoreNull(userPO,userVO);
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyPropertiesIgnoreNull(userPO.getRole(),roleVO);
            userVO.setRole(roleVO);
            userVOList.add(userVO);
        });
        return ResponseVO.success(userVOList);
    }

    /**
     * {@link org.springframework.security.oauth2.provider.token.DefaultTokenServices#createRefreshToken(OAuth2Authentication)}
     *  will create a refresh token if  RefreshTokenValiditySeconds set up.
     * @param loginUserDTO
     * @return
     */
    @Override
    public ResponseVO login(LoginUserDTO loginUserDTO) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", CLIENT_ID);
        paramMap.add("client_secret", CLIENT_SECRET);
        paramMap.add("username", loginUserDTO.getAccount());
        paramMap.add("password", loginUserDTO.getPassword());
        paramMap.add("grant_type", GRANT_TYPE[0]);
        //Token token = null;
        //try {
            //因为oauth2本身自带的登录接口是"/oauth/token"，并且返回的数据类型不能按我们想要的去返回
            //但是我的业务需求是，登录接口是"user/login"，由于我没研究过要怎么去修改oauth2内部的endpoint配置
            //所以这里我用restTemplate(HTTP客户端)进行一次转发到oauth2内部的登录接口，比较简单粗暴
            HttpEntity<String> body= new HttpEntity<>("");
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl())
                .queryParams(paramMap).build();
        DefaultOAuth2AccessToken tokenObj = restTemplate.postForObject(uriComponents.toUri(), body, DefaultOAuth2AccessToken.class);
        OAuth2AccessToken refreshToken = redisTokenStore.readAccessToken(tokenObj.getValue());
            //OAuth2AccessToken loginUser = redisTokenStore.readAccessToken(tokenObj.getValue());
            //LoginUserVO loginUserVO = redisUtil.get(tokenObj.getValue(), LoginUserVO.class);
            /** update refresh token for a api
             * if(loginUser != null){
                //登录的时候，判断该用户是否已经登录过了
                //如果redis里面已经存在该用户已经登录过了的信息
                //我这边要刷新一遍token信息，不然，它会返回上一次还未过时的token信息给你
                //不便于做单点维护
                OAuth2Authentication auth = redisTokenStore.readAuthentication(loginUser.getValue());
                OAuth2RefreshToken refreshToken = redisTokenStore.readRefreshToken(loginUser.getValue());
                redisTokenStore.storeRefreshToken(refreshToken, auth);

                // token = oauthRefreshToken(loginUser.getRefreshToken().getValue());
                // redisUtil.deleteCache(loginUserVO.getAccessToken());
            }*/
        //} catch (RestClientException e) {
        //    try {
        //        e.printStackTrace();
        //        //此处应该用自定义异常去返回，在这里我就不去具体实现了
        //        //throw new Exception("username or password error");
        //    } catch (Exception e1) {
        //        e1.printStackTrace();
        //    }
        //}
        //这里我拿到了登录成功后返回的token信息之后，我再进行一层封装，最后返回给前端的其实是LoginUserVO
        LoginUserVO loginUserVO = new LoginUserVO();
        User userPO = userRepository.findUserByAccount(loginUserDTO.getAccount());
        BeanUtils.copyPropertiesIgnoreNull(userPO, loginUserVO);
        loginUserVO.setPassword(userPO.getPassword());
        loginUserVO.setAccessToken(refreshToken.getValue());
        loginUserVO.setAccessTokenExpiresIn(refreshToken.getExpiresIn());
        loginUserVO.setAccessTokenExpiration(localDateFormat(refreshToken.getExpiration()));
        loginUserVO.setExpired(refreshToken.isExpired());
        loginUserVO.setScope(new ArrayList<>(refreshToken.getScope()));
        loginUserVO.setTokenType(refreshToken.getTokenType());
        loginUserVO.setRefreshToken(refreshToken.getRefreshToken().getValue());

        Date refreshExpireDate = ((DefaultExpiringOAuth2RefreshToken) refreshToken.getRefreshToken()).getExpiration();
        loginUserVO.setRefreshTokenExpiration(localDateFormat(refreshExpireDate));
        //存储登录的用户
        redisUtil.set(loginUserVO.getAccessToken(),loginUserVO,TimeUnit.HOURS.toSeconds(1));
        return ResponseVO.success(loginUserVO);
    }

    /**
     * @description oauth2客户端刷新token
     * @param refreshToken
     * @date 2019/03/05 14:27:22
     * @author Zhifeng.Zeng
     * @return
     */
    private AccessToken oauthRefreshToken(String refreshToken) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", CLIENT_ID);
        paramMap.add("client_secret", CLIENT_SECRET);
        paramMap.add("refresh_token", refreshToken);
        paramMap.add("grant_type", GRANT_TYPE[1]);
        AccessToken token = null;
        try {
            token = restTemplate.postForObject(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl(), paramMap, AccessToken.class);
        } catch (RestClientException e) {
            try {
                //此处应该用自定义异常去返回，在这里我就不去具体实现了
                throw new Exception(ResponseEnum.REFRESH_TOKEN_INVALID.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return token;
    }

    private String localDateFormat(Date date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ssS");
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldt.format(dtf);
    }


}
