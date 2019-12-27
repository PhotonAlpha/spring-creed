package com.ethan.auth.utils;

public class AssertUtils {
    /**
     * @description 获取请求头的token
     * @param authToken
     * @return
     */
    public static String extracteToken(String authToken){
        String authTokenPrefix="Bearer";
        if(authToken.indexOf(authTokenPrefix)!=-1){
            return authToken.substring(7);
        }else {
            return authToken;
        }
    }
}
