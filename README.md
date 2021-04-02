# ylogin(单点登录系统)
## 1. 主要技术
1. SpringCloud ( 2020.0.2 )
2. SpringBoot ( 2.2.4 )
3. openfeign远程调用
4. nocos注册中心
5. nginx反向代理
6. gateway网关
6. redis
7. MySQL
8. HttpSession
9. Cookie
## 2. 部署流程
1. 建立数据库
    ```sql
    create table user
    (
       id                   bigint not null auto_increment comment 'id',
       username             char(64) comment '用户名',
       password             varchar(64) comment '密码',
       nickname             varchar(64) comment '昵称',
       profile_image_url    varchar(255) comment '头像地址',
       gender               tinyint comment '性别',
       create_time          datetime comment '注册时间',
       social_uid        varchar(255) comment '社交id',
       access_token        VARCHAR(255) COMMENT '社交令牌',
       primary key (id)
    );
    ```
2. 在主机中添加域名映射
    ```shell script
   nginx_ip ylogin.com
   nginx_ip ylogin.client1.com
   nginx_ip auth.ylogin.com
    ```  
3. nginx中添加配置文件
    1. 在nginx.conf中添加
    定义网关服务器
        ```shell script
       upstream ylogin{
            server 网关ip:88;
       }
        ```
   2. 在conf.d文件夹创建ylogin.conf
       ```shell script
      server {
          listen       80;
          listen  [::]:80;
          # 监听域名
          server_name  ylogin.com *.ylogin.com *.client1.com;
      
          location /static/ {
            root   /usr/share/nginx/html;
          }   
          # 带上host转发到网关
          location / {
              proxy_set_header Host $host;
              proxy_pass http://ylogin;
          }
      
          error_page   500 502 503 504  /50x.html;
          location = /50x.html {
              root   /usr/share/nginx/html;
          }
      }
        ```
4. 修改nacos服务地址
5. 修改数据源连接地址
6. 修改redis主机地址
7. 浏览器访问ylogin.com、ylogin.client1.com/abc
## 3. 关注我
如果对源码有疑问，欢迎关注我的公众号
<br>
![](https://blog-fyun.oss-cn-hangzhou.aliyuncs.com/2021-4-1/%E5%85%AC%E4%BC%97%E5%8F%B7%E4%BA%8C%E7%BB%B4%E7%A0%81.jpg)


