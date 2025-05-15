# 模拟正常的带CA认证的双端认证

### 添加 alt_names 配置

```shell
#初始化配置，一般用于添加alt_names
echo "[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
prompt = no

[req_distinguished_name]
C = DE
ST = Thuringia
L = Erfurt
O = Alice Corp
OU = Team Foo
CN = server-alice

[v3_req]
keyUsage = keyEncipherment, digitalSignature
extendedKeyUsage = serverAuth
subjectAltName = @alt_names
[alt_names]
DNS.1 = server.ethan.com
DNS.2 = localhost
IP.1 = 127.0.0.1" > openssl.conf

echo "[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
prompt = no

[req_distinguished_name]
C = DE
ST = Thuringia
L = Erfurt
O = Alice Corp
OU = Team Foo
CN = server-alice

[v3_req]
keyUsage = keyEncipherment, digitalSignature
extendedKeyUsage = clientAuth" > openssl-cli.conf
```




1. 生成 CA private key and csr

  ```shell
  openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=ethan.com" -keyout creed-mall-ca.key -out creed-mall-ca.csr
  
  #注意⚠️  CA证书如果添加 -addext 或者使用 -extfile openssl.conf -extensions v3_req 会导致服务端无法仍证 【sslv3 alert certificate unknown】
  # openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=ethan.com" -addext "subjectAltName = DNS:example.com, DNS:localhost, IP:127.0.0.1" -keyout creed-mall-ca.key -out creed-mall-ca.csr
  ```

  验证

  ```shell
  openssl req -text -noout -verify -in creed-mall-ca.csr
  ```

2. 生成CA certificate

  ```shell
  openssl x509 -req -days 365 -in creed-mall-ca.csr -signkey creed-mall-ca.key -out creed-mall-ca.crt
  
  #注意⚠️  CA证书如果添加 -extfile openssl.conf -extensions v3_req 会导致服务端无法仍证 【sslv3 alert certificate unknown】
  # openssl x509 -req -days 365 -in creed-mall-ca.csr -signkey creed-mall-ca.key -out creed-mall-ca.crt -extfile openssl.conf -extensions v3_req
  ```

  > [!TIP]
  >
  > 一步到位
  >
  > ```shell
  > openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=ethan.com" -keyout creed-mall-ca.key -out creed-mall-ca.csr
  > openssl x509 -req -days 365 -in creed-mall-ca.csr -signkey creed-mall-ca.key -out creed-mall-ca.crt
  > ```

  验证

  ```shell
  openssl x509 -in creed-mall-ca.crt -text -noout
  ```

3. 提取public key (optional)

  ```shell
  openssl x509 -in creed-mall-ca.crt -pubkey -noout  > creed-mall-ca-public.key
  ```

4. 生成client需要的 private key and csr **(客户端访问：对应postman KEY file)**

   ```shell
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=nginx.ethan.com" -keyout creed-mall-client.key -out creed-mall-client.csr
   ```

5. 生成 client 需要的 certificate **(客户端访问：对应postman CRT file)**

   ```shell
   #模拟向CA机构获取可以被浏览器信任的证书
   openssl x509 -req  -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key -in creed-mall-client.csr -out creed-mall-client.crt
   
   # openssl x509 -req -days 365 -in creed-mall-client.csr -signkey creed-mall-client.key -out creed-mall-client-na.crt
   ```

6. 生成server需要的 private key and csr

   ```shell
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=server.ethan.com" -keyout creed-mall-server.key -out creed-mall-server.csr
   ```

7. 生成 server端的certificate

   ```shell
   #模拟向CA机构获取可以被浏览器信任的证书,并添加 SAN
   openssl x509 -req  -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key -in creed-mall-server.csr -out creed-mall-server.crt -extfile openssl.conf -extensions v3_req
   
   # openssl x509 -req  -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key -in creed-mall-server.csr -out creed-mall-server.crt
   ```

8. 将x.509转为jks

   ```shell
   openssl pkcs12 -export -inkey creed-mall-server.key -in creed-mall-server.crt -out creed-mall-server.p12 -name server-creed-mall -password pass:changeit
   
   # 可选
   openssl pkcs12 -export -inkey creed-mall-client.key -in creed-mall-client.crt -out creed-mall-client.p12 -name client-creed-mall -password pass:changeit
   ```

   

9. keytool p12转为jks格式

  ```shell
  keytool -importkeystore -srckeystore creed-mall-server.p12 -srcstoretype PKCS12 -destkeystore creed-mall-server.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
  
  # 可选
  keytool -importkeystore -srckeystore creed-mall-client.p12 -srcstoretype PKCS12 -destkeystore creed-mall-client.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
  ```

  

10. 将证书导入trust store

    ```shell
    keytool -import -trustcacerts -file creed-mall-ca.crt -keystore creed-mall-server-truststore.jks -storepass changeit -alias creed-mall-ca
    
    # 可选
    keytool -import -trustcacerts -file creed-mall-server.crt -keystore creed-mall-client-truststore.jks -storepass changeit -alias creed-mall-server
    ```

    

11. 将客户端trust store转为p12

    ```shell
    keytool -importkeystore -srckeystore creed-mall-client-truststore.jks -destkeystore creed-mall-client-truststore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass changeit -deststorepass changeit -alias creed-mall-server
    ```

    

12. 从.p12中提取private key 和 证书

    ```shell
    openssl pkcs12 -in keystore.p12 -nocerts -nodes -out keystore.key
    openssl pkcs12 -in keystore.p12 -clcerts -nokeys -out keystore.crt
    ```

### 总结

> [!TIP]
>
> ```shell
> openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=ethan.com" -keyout creed-mall-ca.key -out creed-mall-ca.csr
> openssl x509 -req -days 365 -in creed-mall-ca.csr -signkey creed-mall-ca.key -out creed-mall-ca.crt
> 
> openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=nginx.ethan.com" -keyout creed-mall-client.key -out creed-mall-client.csr
> openssl x509 -req  -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key -in creed-mall-client.csr -out creed-mall-client.crt 
> 
> openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=server.ethan.com" -keyout creed-mall-server.key -out creed-mall-server.csr
> openssl x509 -req  -CAcreateserial -days 365 -CA creed-mall-ca.crt -CAkey creed-mall-ca.key -in creed-mall-server.csr -out creed-mall-server.crt -extfile openssl.conf -extensions v3_req
> 
> openssl pkcs12 -export -inkey creed-mall-server.key -in creed-mall-server.crt -out creed-mall-server.p12 -name server-creed-mall -password pass:changeit
> keytool -importkeystore -srckeystore creed-mall-server.p12 -srcstoretype PKCS12 -destkeystore creed-mall-server.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
> keytool -import -trustcacerts -file creed-mall-ca.crt -keystore creed-mall-server-truststore.jks -storepass changeit -alias creed-mall-ca
> ```
>
> Spring boot 配置
>
> ```yaml
> server:
>   ssl:
>     client-auth: need
>     enabled: true
>     key-store: classpath:ssl/creed-mall-server.jks
>     key-store-password: changeit
>     trust-store: classpath:ssl/creed-mall-server-truststore.jks
>     trust-store-password: changeit
>     enabled-protocols: TLSv1.2,TLSv1.3
>     protocol: TLS
> ```
>
> 使用curl命令认证
>
> ```shell
> #跳过客户端认证
> curl -k --cert ./creed-mall-client.crt --key ./creed-mall-client.key -X POST 'https://localhost:8080/buziVa/artisan' \
> -H 'Content-Type: application/json' \
> -d '{"code":"code_1af7a7c4e6b7","name":"name_8829441c9599","password":"password_529900b1b6df","email":"email_c0ef7355a413","sex":"sex_6dd875ec820b","phone":"","profile":{"avatar":"https://xxxx.jpg","accountName":"journey","nickName":"journey","accountInfo":{"realNameAuthentication":{}},"addressManagement":{"tags":["家","公司","学校"],"customTags":["乌托邦"],"address":[]}}}'
> 
> #带客户端仍证
> curl --cacert ./creed-mall-ca.crt --cert ./creed-mall-client.crt --key ./creed-mall-client.key -X POST 'https://localhost:8080/buziVa/artisan' \
> -H 'Content-Type: application/json' \
> -d '{"code":"code_1af7a7c4e6b7","name":"name_8829441c9599","password":"password_529900b1b6df","email":"email_c0ef7355a413","sex":"sex_6dd875ec820b","phone":"","profile":{"avatar":"https://xxxx.jpg","accountName":"journey","nickName":"journey","accountInfo":{"realNameAuthentication":{}},"addressManagement":{"tags":["家","公司","学校"],"customTags":["乌托邦"],"address":[]}}}'
> ```



# 剔除CA的双端认证

> [!NOTE]
>
> 该配置剔除了CA，因此访问server需要client的**private key** 和 **certificate**

1. 生成 **server** private key and csr
   
   ```shell
   #无CA的情况下，该项配置不需要
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=server.ethan.com" -addext "subjectAltName = DNS:server.ethan.com, DNS:localhost, IP:127.0.0.1" -keyout creed-mall-server.key -out creed-mall-server.csr
   
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=server.ethan.com" -keyout creed-mall-server.key -out creed-mall-server.csr
   ```
   
   验证
   
   ```shell
   openssl req -text -noout -verify -in creed-mall-server.csr
   ```

2. 生成 **server** certificate证书

   ```shell
   openssl x509 -req -days 365 -in creed-mall-server.csr -signkey creed-mall-server.key -out creed-mall-server.crt  -extensions v3_req
   ```

   验证

   ```shell
   openssl x509 -in creed-mall-server.crt -text -noout
   ```

3. 生成**client**需要的 private key and csr **(客户端访问：对应postman KEY file)**

   ```shell
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=nginx.ethan.com" -keyout creed-mall-client.key -out creed-mall-client.csr
   ```

4. 生成 certificate **(客户端访问：对应postman CRT file)**

   ```shell
   openssl x509 -req -days 365 -in creed-mall-client.csr -signkey creed-mall-client.key -out creed-mall-client.crt
   ```

5. 将x.509转为p12

   ```shell
    openssl pkcs12 -export -inkey creed-mall-server.key -in creed-mall-server.crt -out creed-mall-server.p12 -name server-creed-mall -password pass:changeit
    #可选
    openssl pkcs12 -export -inkey creed-mall-client.key -in creed-mall-client.crt -out creed-mall-client.p12 -name client-creed-mall -password pass:changeit
   ```

8. keytool p12转为jks格式

   ```shell
   keytool -importkeystore -srckeystore creed-mall-server.p12 -srcstoretype PKCS12 -destkeystore creed-mall-server.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
   #可选
   keytool -importkeystore -srckeystore creed-mall-client.p12 -srcstoretype PKCS12 -destkeystore creed-mall-client.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
   ```

9. 将证书导入trust store

   ```shell
   keytool -import -trustcacerts -file creed-mall-client.crt -keystore creed-mall-server-truststore.jks -storepass changeit -alias creed-mall-client
   #可选
   keytool -import -trustcacerts -file creed-mall-server.crt -keystore creed-mall-client-truststore.jks -storepass changeit -alias creed-mall-server
   ```

10. 将客户端JKS转为p12 (**optional** 互相转换)

    ```shell
    keytool -importkeystore -srckeystore creed-mall-client-truststore.jks -destkeystore creed-mall-client-truststore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass changeit -deststorepass changeit -alias creed-mall-server
    ```

11. 从.p12中提取private key 和 证书(**optional** 互相转换)

    ```shell
    openssl pkcs12 -in keystore.p12 -nocerts -nodes -out keystore.key
    openssl pkcs12 -in keystore.p12 -clcerts -nokeys -out keystore.crt
    ```

12. 测试example p12,无效证书测试(**optional**)

    ```shell
    openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=myself.example.com" -keyout myself.example.key -out myself.example.csr
    openssl x509 -req -days 365 -in myself.example.csr -signkey myself.example.key -out myself.example.crt
    ```

### 总结

```shell
openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Root CA Ltd/O=Ethan Creed Limited/CN=server.ethan.com" -keyout creed-mall-server.key -out creed-mall-server.csr
openssl x509 -req -days 365 -in creed-mall-server.csr -signkey creed-mall-server.key -out creed-mall-server.crt  -extensions v3_req

openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=Creed Mall CA Ltd/O=Ethan Creed Limited/CN=nginx.ethan.com" -keyout creed-mall-client.key -out creed-mall-client.csr
openssl x509 -req -days 365 -in creed-mall-client.csr -signkey creed-mall-client.key -out creed-mall-client.crt

openssl pkcs12 -export -inkey creed-mall-server.key -in creed-mall-server.crt -out creed-mall-server.p12 -name server-creed-mall -password pass:changeit
keytool -importkeystore -srckeystore creed-mall-server.p12 -srcstoretype PKCS12 -destkeystore creed-mall-server.jks -deststoretype JKS -srcstorepass changeit -deststorepass changeit
keytool -import -trustcacerts -file creed-mall-client.crt -keystore creed-mall-server-truststore.jks -storepass changeit -alias creed-mall-client
```



```shell
# working
curl -k --cert ./creed-mall-client.crt --key ./creed-mall-client.key -X POST 'https://localhost:8080/buziVa/artisan' \
-H 'Content-Type: application/json' \
-d '{"code":"code_1af7a7c4e6b7","name":"name_8829441c9599","password":"password_529900b1b6df","email":"email_c0ef7355a413","sex":"sex_6dd875ec820b","phone":"","profile":{"avatar":"https://xxxx.jpg","accountName":"journey","nickName":"journey","accountInfo":{"realNameAuthentication":{}},"addressManagement":{"tags":["家","公司","学校"],"customTags":["乌托邦"],"address":[{"consignee":"xxx","contactCountryCode":"+86","contactNo":"130","location":"上海汉庭酒店","addressDetail":"6栋620号","default":true,"tag":"x"}]}}}'

#not working- trust store中不存在该cert
#curl: (56) LibreSSL SSL_read: LibreSSL/3.3.6: error:1404C416:SSL routines:ST_OK:sslv3 alert certificate unknown, errno 0
curl -k --cert ./creed-mall-server.crt --key ./creed-mall-server.key -X POST 'https://localhost:8080/buziVa/artisan' \
-H 'Content-Type: application/json' \
-d '{"code":"code_1af7a7c4e6b7","name":"name_8829441c9599","password":"password_529900b1b6df","email":"email_c0ef7355a413","sex":"sex_6dd875ec820b","phone":"","profile":{"avatar":"https://xxxx.jpg","accountName":"journey","nickName":"journey","accountInfo":{"realNameAuthentication":{}},"addressManagement":{"tags":["家","公司","学校"],"customTags":["乌托邦"],"address":[{"consignee":"xxx","contactCountryCode":"+86","contactNo":"130","location":"上海汉庭酒店","addressDetail":"6栋620号","default":true,"tag":"x"}]}}}'

#not working- trust store中不存在该cert
#curl: (56) LibreSSL SSL_read: LibreSSL/3.3.6: error:1404C416:SSL routines:ST_OK:sslv3 alert certificate unknown, errno 0
curl -k --cert ./myself.example.crt --key ./myself.example.key -X POST 'https://localhost:8080/buziVa/artisan' \
-H 'Content-Type: application/json' \
-d '{"code":"code_1af7a7c4e6b7","name":"name_8829441c9599","password":"password_529900b1b6df","email":"email_c0ef7355a413","sex":"sex_6dd875ec820b","phone":"","profile":{"avatar":"https://xxxx.jpg","accountName":"journey","nickName":"journey","accountInfo":{"realNameAuthentication":{}},"addressManagement":{"tags":["家","公司","学校"],"customTags":["乌托邦"],"address":[{"consignee":"xxx","contactCountryCode":"+86","contactNo":"130","location":"上海汉庭酒店","addressDetail":"6栋620号","default":true,"tag":"x"}]}}}'
```
