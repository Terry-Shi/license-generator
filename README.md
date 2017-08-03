# license-generator
this tool generates a software license


基本步骤
1. 事先通过 jdk 附带的 keytool 生成 privateKeyStore 和 publicKeyStore。
  其中privateKeyStore妥善保存在自己这里，publicKeyStore分发到用户服务器上。

2. 用户在我们的 dashboard 或某控制台上提供，采集licenseContent（例如用户名，公司名，如果要绑定机器可以带上mac地址等不易修改的硬件信息），发送给我们。

3. 我们用 privateKeyStore 加上用户提供的licenseContent生成（本质上是加密） xxx.lic 文件。并且发给用户。

4. 在用户端用 publicKeyStore 验证 xxx.lic 文件合法。同时解密取得licenseContent内容。如果需要可以比对硬件信息是否匹配。

*注意：利用硬件信息绑定 xxx.lic 只能在某些特征的 server 上有效。


*不使用keytool，简单利用RSA等非对称加密算法也可实现上面功能。
注意：
RSA加密明文最大长度117字节，解密要求密文最大长度为128字节，所以在加密和解密的过程中需要分块进行。
RSA加密对明文的长度是有限制的，如果加密数据过大会抛出异常：

参考资料:
[代码来源](https://alvinalexander.com/java/java-license-key-generator-manager-free)
[keytool](https://alvinalexander.com/java/java-keytool-keystore-certificates)
