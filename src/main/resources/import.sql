/*  项目启动 自动执行的数据库脚本 ==> 密码是 111111 sql不能换行*/
INSERT INTO user (id, username, password, name, email) VALUES (1, 'milo','$2a$10$mu.Ezc2d7yfAPxf4oOqptuJFa9VCr4VXbXZrioz2b8cDdGH/cmAPm', 'milo', 'qcmzby@gmail.com');
INSERT INTO user (id, username, password, name, email)  VALUES (2, 'venna','$2a$10$mu.Ezc2d7yfAPxf4oOqptuJFa9VCr4VXbXZrioz2b8cDdGH/cmAPm', 'venna', 'venna@gmail.com');

/* 权限表 */
INSERT INTO authority (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authority (id, name) VALUES (2, 'ROLE_USER');

/* 用户权限中间表 */
INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);