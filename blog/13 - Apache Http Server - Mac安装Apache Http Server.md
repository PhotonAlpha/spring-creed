# Apache Http Server - Mac安装Apache Http Server

官方文档对Apache Http Server的安装说明：http://httpd.apache.org/docs/2.4/install.html

## 下载源码

下载地址：http://httpd.apache.org/download.cgi
例如我们要下载2.4.35版本的，那么我们就下载它的源文件。



## 配置、编译、安装

### 1. 配置

```shell
cd httpd-2.4.35
./configure --prefix=/usr/local/httpd-2.4.35
```

没有出现Error，视为配置完成。

可能遇见的错误有

- **APR not found**
  打开终端找个合适的地方执行下面的命令

  ```shell
  wget http://archive.apache.org/dist/apr/apr-1.4.5.tar.gz
  tar -zxf apr-1.4.5.tar.gz
  cd apr-1.4.5
  ./configure --prefix=~/usr/local/apr(修改为自定义的文件地址)
  make && sudo make install
  ```

  重新执行配置命令即可。

- **APR-util not found**
  打开终端找个合适的地方执行下面的命令

  ```shell
  wget http://archive.apache.org/dist/apr/apr-util-1.3.12.tar.gz
  tar apr-util-1.3.12.tar.gz
  cd apr-util-1.3.12
  ./configure --prefix=/usr/local/apr-util(修改为自定义的文件地址) --with-apr=/usr/local/apr(修改为自定义的文件地址)
  make && make install
  
  ```

重新执行配置命令，发现还是会出现该问题。配置命令修改如下：

```shell
./configure --prefix=/usr/local/httpd-2.4.35 --with-apr-util=/usr/local/apr-util
```

- pcre-config for libpcre not found. PCRE is required and available from http://pcre.org/

打开终端找个合适的地方执行下面的命令

```shell
wget http://jaist.dl.sourceforge.net/project/pcre/pcre/8.10/pcre-8.10.zip
unzip pcre-8.10.zip
cd pcre-8.10
./configure --prefix=/usr/local/pcre  
make && sudo make install
```

修改配置命令如下并执行

```shell
./configure --prefix=/usr/local/httpd-2.4.35 --with-apr=/usr/local/apr --with-apr-util=/usr/local/apr-util --with-pcre=/usr/local/pcre/bin/pcre2-config
```


配置成功，如下图

### 2. 编译

```shell
make
```


大概过去了3分钟…
没提示错误，视为成功…

### 3. 安装

```shell
make install
```


成功后，在我们的`--prefix`指定的目录`/usr/local/httpd`下生成了下面的内容

### 4. 启动

```shell
cd /usr/local/httpd
sudo bin/apachectl -k start
```


启动成功后终端提示如下：

```tex
httpd (pid 74018) already running
```

访问http://localhost或http://127.0.0.1

### 可能遇见的错误

- Could not reliably determine the server’s fully qualified domain name, using 127.0.0.1. Set the ‘ServerName’ directive globally to suppress this message
  意思是缺少了了`ServerName`配置，打开`bin/httpd.conf`，修改如下：

```properties
#
# ServerName gives the name and port that the server uses to identify itself.
# This can often be determined automatically, but we recommend you specify
# it explicitly to prevent problems during startup.
#
# If your host doesn't have a registered DNS name, enter its IP address here.
#
#ServerName www.example.com:80
ServerName 127.0.0.1

```

