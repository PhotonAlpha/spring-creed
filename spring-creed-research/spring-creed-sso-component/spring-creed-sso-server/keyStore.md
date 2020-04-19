https://www.cnblogs.com/benwu/articles/4891758.html

https://github.com/downgoon/hello-world/wiki/keytoos-and-openssl

1. 使用 keytool 生成 JKS，并且生成公钥和私钥对
keytool -genkey -alias ethan-jwt -validity 3650 -keyalg RSA -dname "CN=jwt,OU=ethan,O=ethan,L=jiangsu,S=jiangsu,C=CH" -keypass ethan123 -keystore ethan-jwt.jks -storepass ethan123

2. 导出证书文件，一般就是 X509v3 的证书 （只有公钥和签名等信息，没有私钥），其编码可以是 PEM 和 DER。-rfc后，导出的是文本文件(PEM)格式, 导出的公钥存放在当前目录的 ethan-jwt.crt 文件中
keytool -export -alias ethan-jwt -storepass ethan123 -file ethan-jwt.crt -keystore ethan-jwt.jks -rfc

keytool -genkey -alias yushan -keypass yushan -keyalg RSA -keysize 1024 -validity 365 -keystore e:\yushan.keystore -storepass 123456 -dname "CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称), ST=(州或省份名称), C=(单位的两字母国家代码)";(中英文即可)

3. 证书的导入：
keytool -genkey -alias shuany -keypass shuany -keyalg RSA -keysize 1024 -validity 365 -keystore  e:\shuany.keystore -storepass 123456 -dname "CN=shuany, OU=xx, O=xx, L=xx, ST=xx, C=xx"


3. jks 生成 key, key 该文件一般存放私钥 我们可以通过 keytool 从 jks 中提取出私钥。  
> 通过这两个命令发现，其目标的keystore 可以是任意文件扩展，虽然扩展名一样 （都是 jks），但是实际上内容格式已经不一样，因为如果使用 keytool 查看该 jks 发现，旧的 jks 会有一个警告，但是新的 jks 没有警告，然后我们还可以指定成 p12 格式的文件。
>  
> 有了 p12，我们就可以使用 openssl 生成 key 了。

    keytool -importkeystore -srckeystore mxsci.jks -storepass changeit1 -destkeystore mxsci.jks -deststoretype pkcs12    
    keytool -importkeystore -srckeystore ethan-jwt.jks -storepass ethan123 -destkeystore ethan-jwt.p12 -deststoretype pkcs12

> 通过这两个命令发现，其目标的keystore 可以是任意文件扩展，虽然扩展名一样 （都是 jks），但是实际上内容格式已经不一样，因为如果使用 keytool 查看该 jks 发现，旧的 jks 会有一个警告，但是新的 jks 没有警告，然后我们还可以指定成 p12 格式的文件。
>
>有了 p12，我们就可以使用 openssl 生成 key 了。

    openssl pkcs12 -in keystore.p12 -nodes -nocerts -out mydomain.key

导入证书
一般为导入信任证书(SSL客户端使用)

keytool -importcert -keystore client_trust.keystore -file server.cer -alias client_trust_server -storepass 111111 -noprompt


keytool -importkeystore -srckeystore mxsci.jks -storepass changeit1 -destkeystore mxsci.jks -deststoretype pkcs12

keytool -importkeystore -srckeystore mxsci.jks -storepass changeit1 -destkeystore mxsci.p12 -deststoretype pkcs12

keytool -certreq -alias  ethan-jwt  -keystore  ethan-jwt.jks  -file  ethan-jwt.csr


keytool -importkeystore -srckeystore ethan-jwt.jks -destkeystore ethan-jwt.jks -deststoretype pkcs12