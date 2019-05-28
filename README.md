# Manage2
仓库管理系统v2，dao 层改用 mybatis 实现，代码结构进行了简单调整

# 使用 sign 签名

将所有的 kv 参数，按照 key 进行字典序排序，然后使用 & 连接，再加上 `&secret=secretKey`，在计算 md5，然后转为 hex。

例如：

```
pageNum: 1
pageSize：10
name："test"
```

sign 计算如下：
`sign=hex(md5("name=test&pageNum=1&pageSize=10&timestamp=1539251693123&secretkey=key")`

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



