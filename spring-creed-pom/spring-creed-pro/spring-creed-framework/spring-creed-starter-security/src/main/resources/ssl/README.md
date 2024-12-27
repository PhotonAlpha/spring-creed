```shell
#生成pivate key & CSR
openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=ethan.com" -keyout creed-mall-ca.key -out creed-mall-ca.csr
# 私钥加密
openssl rsa -in creed-mall-ca.key -aes256 -passout pass:changeit -out creed-mall-ca.key
#私钥去除加密：
openssl rsa -in creed-mall-ca.key -passin pass:changeit -out creed-mall-ca.key

#通过已有的密钥 Private Key 来产生 Self-Signed crt 证书
openssl x509 -req -days 365 -in creed-mall-ca.csr -signkey creed-mall-ca.key -out creed-mall-ca.crt
#提取crt public key
openssl x509 -in creed-mall-ca.crt -pubkey -noout  > public-creed-mall-ca.key

#生成 Nginx Web Server 需要的 Private Key & CSR ：
openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=nginx.ethan.com" -keyout creed-mall-nginx.key -out creed-mall-nginx.csr
# 利用 Self-Signed CA 证书，来签发业务所需要的 CRT 证书：
openssl x509 -req -in creed-mall-nginx.csr -out creed-mall-nginx.crt -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key
#提取crt public key
openssl x509 -in creed-mall-nginx.crt -pubkey -noout  > public-creed-mall-nginx.key


#将private key 和CRT转为.p12文件
openssl pkcs12 -export -inkey creed-mall-ca.key -in creed-mall-ca.crt -out creed-mall-ca.p12 -name root-creed-mall -password pass:changeit

openssl pkcs12 -export -inkey creed-mall-nginx.key -in creed-mall-nginx.crt -out creed-mall-nginx.p12 -name nginx-creed-mall -password pass:changeit
#导入第一个.p12
keytool -importkeystore -srckeystore creed-mall-ca.p12 -srcstoretype PKCS12 -srcstorepass changeit \
-destkeystore keystore.jks -deststoretype JKS  -deststorepass changeit -alias root-creed-mall
#导入第二个.p12
keytool -importkeystore -srckeystore creed-mall-nginx.p12 -srcstoretype PKCS12 -srcstorepass changeit \
-destkeystore keystore.jks -deststoretype JKS -deststorepass changeit -alias nginx-creed-mall

keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass changeit -deststorepass changeit <-alias root-creed-mall>(可选，如果不选则导出所有)
```