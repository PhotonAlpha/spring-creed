package com.ethan.app.service;

import com.ethan.app.dto.LoginUserDTO;
import com.ethan.app.dto.UserDTO;
import com.ethan.app.vo.UserVO;
import com.ethan.common.vo.ResponseVO;

import java.util.List;

/**
 * @description 用户业务接口
 * @author Zhifeng.Zeng
 * @date 2019/2/21 14:06
 */
public interface UserService {

    /**
     * @description 添加用户
     */
    void addUser(UserDTO userDTO) throws Exception;

    /**
     * 删除用户
     * @param id
     */
    void deleteUser(Long id) throws Exception;

    /**
     * @description 修改用户信息
     * @param userDTO
     */
    void updateUser(UserDTO userDTO);

    /**
     * @description 获取所有用户列表VO
     * @return
     */
    ResponseVO<List<UserVO>> findAllUserVO();

    /**
     * @description 用户登录
     * @return
     */
    ResponseVO login(LoginUserDTO loginUserDTO);

}
