## KeyStore 和 TrustStore的区别及联系

KeyStore用于[服务器](https://cloud.tencent.com/product/cvm/?from_column=20065&from=20065)认证服务端，而TrustStore用于客户端认证服务器。

**keystore是存储密钥（公钥、私钥）的容器。**

**keystore和truststore其本质都是keystore。只不过二者存放的密钥所有者不同而已。本质都是相同的文件，只不过约定通过文件名称区分类型以及用途**

**对于keystore一般存储自己的私钥和公钥，而truststore则用来存储自己信任的对象的公钥。**

> 比如在客户端(服务请求方)对服务器(服务提供方)发起一次HTTPS请求时,服务器需要向客户端提供认证以便客户端确认这个服务器是否可信。这里，服务器向客户端提供的认证信息就是自身的证书和公钥，而这些信息，包括对应的私钥，服务器就是通过KeyStore来保存的。当服务器提供的证书和公钥到了客户端，客户端就要生成一个TrustStore文件保存这些来自[服务器证书](https://cloud.tencent.com/product/ssl?from_column=20065&from=20065)和公钥。

KeyStore 和 TrustStore的不同，也主要是通过上面所描述的使用目的的不同来区分的，在Java中这两种文件都可以通过keytool来完成。不过因为其保存的信息的敏感度不同，KeyStore文件通常需要密码保护。

正是因为 KeyStore 和 TrustStore Java中都可以通过 keytool 来管理的，所以在使用时多有混淆。记住以下几点，可以最大限度避免这些混淆 :

- 如果要保存你自己的密码，秘钥和证书，应该使用KeyStore，并且该文件要保持私密不外泄，不要传播该文件;
- 如果要保存你信任的来自他人的公钥和证书，应该使用TrustStore，而不是KeyStore;
- 在以上两种情况中的文件命名要尽量提示其安全敏感程度而不是有歧义或者误导 比如使用KeyStore的场景把文件命名为 truststore.jks,或者该使用TrustStore的情况下把文件命名为keystore.jks之类，这些用法都属于严重误导随后的使用者，有可能把比较私密的文件泄露出去；
- 拿到任何一个这样的文件时，确认清楚其内容然后决定怎样使用；

因为 KeyStore 文件既可以存储敏感信息，比如密码和私钥，也可以存储公开信息比如公钥，证书之类，所有实际上来讲，可以将KeyStore文件同样用做TrustStore文件,但这样做要确保使用者很明确自己永远不会将该KeyStore误当作TrustStore传播出去。

### **KeyStore**

内容 一个KeyStore文件可以包含私钥(private key)和关联的证书(certificate)或者一个证书链。证书链由客户端证书和一个或者多个CA证书。

KeyStore类型 KeyStore 文件有以下类型，一般可以通过文件扩展名部分来提示相应KeyStore文件的类型:

JCEKS JKS DKS PKCS11 PKCS12 Windows-MY BKS 以上KeyStore的类型并不要求在文件名上体现，但是使用者要明确所使用的KeyStore的格式。

### **TrustStore**

内容 一个TrustStore仅仅用来包含客户端信任的证书，所以，这是一个客户端*所信任的来自其他人或者组织的信息*的存储文件,而不能用于存储任何安全敏感信息，比如私钥(private key)或者密码。

客户端通常会包含一些大的CA机构的证书，这样当遇到新的证书时，客户端就可以使用这些CA机构的证书来验证这些新来的证书是否是合法的。



### 单向认证与双向认证

**单向认证：**

​	**单向认证是客户端验证服务端的真伪性，所以需要将服务器端的证书server.crt导出，导出的server.crt就是服务器端的公钥。然后将 server.crt 导入到客户端的 trustore 中。**

​	**这样服务器就被客户端信任了，连接时客户端使用服务器端的公钥去验证服务器。**

**双向认证：**

　　**服务器的公钥导入到客户端的truststore，客户端的公钥导入到服务器端的truststore中。**

　　**客户端请求服务器端，服务器端通过预置有客户端证书的 trust store 验证客户端的证书，如果证书被信任，则验证通过**

　　**服务器端响应客户端，客户端通过预置有服务端证书的 trust store 验证服务端的证书，如果证书被信任，则验证通过，完成一个双向认证过程。**

**java 在jdk 中已经默认在 $JAVA_HOME/lib/security/cacerts 这个文件中预置了常用的 证书**

## key store 与 trust store 常用的命令：

**3.1 创建证书**

```shell
keytool -genkeypair -alias "test1" -keyalg "RSA" -keystore test.keystore.jks   
```

- **genkeypair**：生成一对非对称密钥;

- **alias**：指定密钥对的别名，该别名是公开的;
- **keyalg**：指定加密算法，本例中的采用通用的RAS加密算法;

- **keystore**:密钥库的路径及名称，不指定的话，默认在操作系统的用户目录下生成一个".keystore"的文件

**3.2 查看 Keystore 的内容**

```shell
keytool -list -v -keystore test.keystore.jks
```

**3.3 添加一个信任根证书到keystore文件**

```
keytool -import -alias newroot -file root.crt -keystore test.keystore.jks
```

**3.4 导出 jks 的证书文件到指定文件** 

```
keytool -export -alias alias_name -keystore test.keystore.jks -rfc -file test.cer
```

**3.5 删除jks 中指定别名的证书**

```
keytool -delete -keystore test.keystore.jks -alias alias_name
```



## 什么是 SSL/TLS 单向认证，双向认证 ？

**单向认证**，指的是只有一个对象校验对端的证书合法性。通常都是 client 来校验服务器的合法性。那么 client 需要一个 ca.crt，服务器需要 server.crt，server.key；

**双向认证**，指的是相互校验，服务器需要校验每个 client，client 也需要校验服务器。server 需要 server.key、server.crt、ca.crt；client 需要 client.key、client.crt、ca.crt；

将jks转为x.509

```shell
keytool -exportcert  -keystore idbmy_ssojwt_truststore.jks  -alias gebmyssojwt -storepass sidbmyssojwttrust -file jwt-cer.der 
openssl x509 -inform der -in jwt-cer.der -out jwt-cer.pem
# 根据 .key .csr 生成 .crt
openssl x509 -signkey domain.key -in domain.csr -req -days 365 -out domain.crt
# 提取公钥
openssl x509 -in domain.crt -pubkey -noout > domain-public.key

# 1. 将jks转为 .key .crt
keytool -importkeystore -srckeystore idbmy_ssojwt_truststore.jks -destkeystore idbmy_ssojwt_truststore.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass sidbmyssojwttrust -deststorepass sidbmyssojwttrust -alias gebmyssojwt
# 2.1. 从 PKCS12提取私钥和证书
openssl pkcs12 -in idbmy_ssojwt_truststore.p12 -nocerts -nodes -out idbmy_ssojwt_truststore.key
# 2.2. 提取证书
openssl pkcs12 -in idbmy_ssojwt_truststore.p12 -clcerts -nokeys -out idbmy_ssojwt_truststore.crt

idbmy_gw_ca_key.jks   sidbmygwca
```

1. **生成RSA私钥**

   ```shell
   openssl req -newkey rsa:2048 -nodes -subj "/C=SG/OU=GTO-Group Banking Technology/O=United Overseas Bank Limited/CN=sg.uobnet.com" -keyout domain.key -out domain.csr
   #通过已有的 Private Key 来产生 CSR
   openssl req -key domain.key -new -out domain.csr
   #通过已有的 CRT 和 Private Key 来产生 CSR
   openssl x509 -in domain.crt -signkey domain.key -x509toreq -out domain.csr
   ```

2. **生成 CRTs 证书文件**

   如果你想要使用 SSL 来保证安全的连接，但是又不想去找 public CA 机构购买证书的话，那你就可以自己来生成 Self-signed Root CA 证书。

   Self-signed 的证书也可以用来加密连接，只不过你的用户在访问你的网站的时候，将会提示出警告，说你的网站是不被信任的。因此当你开发的服务，并不需要提供给其他用户使用的时候 (e.g. non-production or non-public servers)，你才可以使用 Self-signed 形式的证书。

   **生成 Self-Signed 的证书**

   以下命令将会生成一个 2048-bits 的 Private Key (`self-ca.key`) 和 Self-Signed 的 CRT 证书 (`self-ca.crt`)：

   ```shell
   #生成 Self-Signed 的证书
   openssl req -newkey rsa:2048 -nodes -keyout self-ca.key -x509 -days 365 -out self-ca.crt
   ```

   其中`-x509` 选项是为了告诉 `req`，生成一个 self-signed 的 X509 证书。而 `-days 365` 表明生成的证书有效时间为 365 天。这条命令执行过程中，会产生一个临时的 CSR 文件，但执行结束后就被删除了。

   

   **通过已有的密钥 Private Key 来产生 Self-Signed 证书**

   以下命令是通过已有的 Private Key (`self-ca.key`)，来生成一个 Self-Signed 的 CRT 证书 (`self-ca.crt`)：

   ```shell
   openssl req -key self-ca.key -new -x509 -days 365 -out self-ca.key
   ```

   

   **通过已有的密钥 Private Key & 请求文件 CSR 来产生 Self-Signed 证书**

   以下命令是通过已有的 Private Key (`self-ca.key`) 以及请求文件 (`self-ca.csr`)，来生成一个 Self-Signed 的 CRT 证书 (`self-ca.crt`)：

   ```shell
   openssl x509 -signkey self-ca.key -in self-ca.csr -req -days 365 -out self-ca.crt
   ```

   

   **通过已有的请求文件 CSR，通过 Self-Signed CA 来签发 CRT 证书**

   以下命令是通过已有的 Self-Signed CA 的证书 (`self-ca.crt`) 和密钥 (`self-ca.key`)，和请求文件 CSR (`domain.csr`) 来签发生成 CRT 证书 (`domain.crt`)：

   ```shell
   openssl x509 -req  -CAcreateserial -days 365 -CA self-ca.crt -CAkey self-ca.key -in nginx.csr -out nginx.crt
   ```

3. 生成公钥匙

##  Tomcat 配置 ssl 认证

打开server.xml，找到

```xml
<!--

　　<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"　　maxThreads="150" scheme="https" secure="true"　　clientAuth="false" sslProtocol="TLS" />

-->
```

这样一段注释，在这段注释下面添加如下一段代码：

```xml
<Connector SSLEnabled="true" acceptCount="100" clientAuth="false"
disableUploadTimeout="true"
enableLookups="false" maxThreads="25"
port="8443" keystoreFile="D:\developTools\apache-tomcat-idm\tomcat.keystore" keystorePass="111111"
protocol="org.apache.coyote.http11.Http11NioProtocol" scheme="https"
secure="true" sslProtocol="TLS" />
```

其中`clientAuth=”false”`表示是SSL单向认证，即服务端认证，port=”8443”是https的访问端口，keystoreFile="D:\developTools\apache-tomcat-idm\tomcat.keystore"是第一步中生成的keystore的保存路径，keystorePass="111111"是第一步生成的keystore的密码。



## PostMan添加新的客户端证书

1. 在**host**字段中，输入您要为其使用证书的请求 URL 的域（无协议），例如`https://postman-echo.com`. 您还可以在**端口**字段中指定与此域关联的自定义端口。这是可选的。如果留空，将使用默认的 HTTPS 端口 (443)。
2. **CRT file:** 为客户端密钥库的公钥。**（PEM 文件可以包含多个 CA 证书。）**
3. **KEY file:** 客户端密钥库的私钥。
4. **PFX file:** 密钥文件\*，或者\*为你的证书选择PFX 文件。
5. **filePassphrase:** Passphrase为私钥的密码（如果有的话）。

### .pfx 证书和 .cer 证书

- **PFX**：带有私钥的证书，由Public Key Cryptography Standards #12，PKCS#12标准定义，包含了公钥和私钥的二进制格式的证书形式，以.pfx作为证书文件后缀名。
- **CER**：**DER Encoded Binary (.cer)** 二进制编码的证书，证书中没有私钥，DER 编码二进制格式的证书文件，以.cer作为证书文件后缀名。
- **CER**：**Base64 Encoded(.cer)**，Base64编码的证书，证书中没有私钥，BASE64 编码格式的证书文件，也是以.cer作为证书文件后缀名。

**由定义可以看出，只有pfx格式的数字证书是包含有私钥的，cer格式的数字证书里面只有公钥没有私钥。**





参考：[玩转-SSL-证书](https://yakir-yang.github.io/2018/08/09/Tools-%E7%8E%A9%E8%BD%AC-SSL-%E8%AF%81%E4%B9%A6/)