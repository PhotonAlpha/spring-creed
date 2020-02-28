INSERT INTO um_t_role(id,`name`,description,created_time,role) VALUES(1,'管理员','管理员拥有所有接口操作权限',CURRENT_TIMESTAMP,'ADMIN'),(2,'普通用户','普通拥有查看用户列表与修改密码权限，不具备对用户增删改权限',CURRENT_TIMESTAMP,'USER');

INSERT INTO `um_t_user`(id,account,`password`,`name`,description) VALUES(1,'admin','123456','小小丰','系统默认管理员');

INSERT INTO `um_t_role_user`(role_id,user_id)VALUES(1,1);
