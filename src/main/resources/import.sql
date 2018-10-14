/*  项目启动 自动执行的数据库脚本 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'*/

INSERT INTO user (id, username, password, name, email) VALUES (1, 'admin', '123456', '老卫', 'i@waylau.com');
INSERT INTO user (id, username, password, name, email)  VALUES (2, 'waylau', '123456', 'Way Lau', 'waylau@waylau.com');

INSERT INTO authority (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authority (id, name) VALUES (2, 'ROLE_USER');

/* 用户权限中间表 */
INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authority (user_id, authority_id) VALUES (2, 2);