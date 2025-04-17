package com.ethan.system.service.auth;

import com.ethan.server.ServerApplication;
import com.ethan.system.constant.logger.LoginLogTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 17/2/25
 */
@SpringBootTest(classes = ServerApplication.class)
public class AdminAuthServiceTest {
    @Resource
    AdminAuthService adminAuthService;

    @Test
    void createLogoutLogTest() {
        adminAuthService.logout("eyJraWQiOiIwM2NkMjJhOS0xNTUwLTQ1ZWUtOWI4Ni02Y2U2ZTYxOTZhMGIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3MjkxNDk5NzQsInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTcyOTIzNjM3NCwiaWF0IjoxNzI5MTQ5OTc0LCJ1dWlkIjoiZjQ2Y2RhMzctYzQ0Mi00OGY0LTkyNjgtNjk0MTE1NDVhNmFmIiwianRpIjoiYjdkN2NhNjgtN2U0Yy00ZTE5LTg4Y2ItZGIyNTdkNjIzMmY3In0.eIivjPB703UO3qulr3H9W0dtsCt7kvpWfsLNDTQwoXBn0wTfr_Xegnwe7f1015KZtEc3pqrHDHh5MPsMFRDht3PwJL9eEcLS0kuW-dGid-e_J3qc0_tUB18op9CGD31FNbvrnOB-dxrvWFwoGUuzzJXbCoNb_Thhgx99l_E-72fbBRkJywSsJyWQV326TdtsgAilt3a8788SCxT3tsyjeOvd9BsgoJslc-3qnEUnWlY7uQjhd8R4qXf312_RI35cg4BNhGQLt19IHiPPZipoiBgwz47fbLcQXHVH4YcFqRKiTA2lh4ec2AOJOeeEIu8xboX6-jRdGrjQzD-5qanEQQ", LoginLogTypeEnum.LOGOUT_DELETE.getType());
    }
}
