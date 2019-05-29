# Manage2
仓库管理系统v2，dao 层改用 mybatis 实现，代码结构进行了简单调整

使用 resources 内的 sql 文件新建数据库。

初始化账号：admin，密码：123456

# 部署运行

一：直接运行

```
mvn spring-boot:run

```

二：打包成 jar 在运行

```
# 1
mvn install

# 2.
# cd 项目跟目录（和pom.xml同级）
mvn clean package
## 或者执行下面的命令
## 排除测试代码后进行打包
mvn clean package  -Dmaven.test.skip=true

# 以上打包方式二选一
# 启动jar包命令,这种方式，只要控制台关闭，服务就不能访问了
java -jar xxx.jar

# 后台运行的方式来启动
nohup java -jar xxx.jar &

# 启动的时候选择读取不同的配置文件
nohup java -jar xxx.jar --spring.profiles.active=prod &
```

# api 文档
项目启动后

http://localhost:8090/api/swagger-ui.html#/

# 使用 sign 签名

**dev 环境下跳过 sign 验证**

将所有的 kv 参数，按照 key 进行字典序排序，然后使用 & 连接，再加上 `&secret=secretKey`，在计算 md5，然后转为 hex。

例如：

```
pageNum: 1
pageSize：10
name："test"
```

sign 计算如下：
`sign=hex(md5("name=test&pageNum=1&pageSize=10&timestamp=1539251693123&secret=key")`

签名结果是类似于"48f83bc7b4edb568e3bc47fb64eaf4c7"的字符串，如果转出来已经是这样就不用 hex 了，如果转出来是 buffer 或者其他格式，需要进行 hex 转换。

验证不通过同一返回如下：

```json
{
  "status": -1,
  "msg": "sign error",
  "timestamp": 1539841842628
}
```

说明：

1. 使用UTF-8编码，不要进行 urlEncode。
2. 注意 timestamp 精确到 ms，如果返回 s，可以乘以 1000。timestamp 与服务器时间差为 5 分钟内有效
3. hex 函数表示转换为 16 进制，如果md5库返回的已经是 16 进制，不用再转换了。
4. 所有请求的参数都参与 sign 计算。



