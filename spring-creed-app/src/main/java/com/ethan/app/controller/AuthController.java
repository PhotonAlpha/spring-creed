package com.ethan.app.controller;

import com.ethan.app.dto.LoginUserDTO;
import com.ethan.app.dto.UserDTO;
import com.ethan.app.service.RoleService;
import com.ethan.app.service.UserService;
import com.ethan.auth.vo.AuthResponseVO;
import com.ethan.common.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description 用户权限管理
 * @author Zhifeng.Zeng
 * @date 2019/4/19 13:58
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    //@Autowired
    //private RedisTokenStore redisTokenStore;

    /**
     * @description 添加用户
     * @param userDTO
     * @return
     */
    @PostMapping("user")
    public ResponseEntity<ResponseVO> add(@Valid @RequestBody UserDTO userDTO) throws Exception {
        userService.addUser(userDTO);
        return AuthResponseVO.success().build();
    }

    /**
     * @description 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("user/{id}")
    public ResponseEntity<ResponseVO> deleteUser(@PathVariable("id")Long id) throws Exception {
        userService.deleteUser(id);
        return AuthResponseVO.success().build();
    }

    /**
     * @descripiton 修改用户
     * @param userDTO
     * @return
     */
    @PutMapping("user")
    public ResponseEntity<ResponseVO> updateUser(@Valid @RequestBody UserDTO userDTO){
        userService.updateUser(userDTO);
        return AuthResponseVO.success().build();
    }

    /**
     * @description 获取用户列表
     * @return
     */
    @GetMapping("user")
    public ResponseEntity<ResponseVO> findAllUser(){
        return userService.findAllUserVO().build();
    }

    /**
     * @description 用户登录
     * @param loginUserDTO
     * @return
     */
    @PostMapping("user/login")
    public ResponseEntity<ResponseVO> login(@RequestBody LoginUserDTO loginUserDTO){
        return userService.login(loginUserDTO).build();
    }


    /**
     * @description 用户注销
     * @param authorization
     * @return
     */
    @GetMapping("user/logout")
    public ResponseEntity<ResponseVO> logout(@RequestHeader("Authorization") String authorization){
        //redisTokenStore.removeAccessToken(AssertUtils.extracteToken(authorization));
        return AuthResponseVO.success().build();
    }

    /**
     * @description 获取所有角色列表
     * @return
     */
    @GetMapping("role")
    public ResponseEntity<ResponseVO> findAllRole(){
        return roleService.findAllRoleVO().build();
    }


}
