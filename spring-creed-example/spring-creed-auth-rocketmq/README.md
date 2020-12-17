# console踩坑记
http://appblog.cn/2019/11/14/rocketmq-console%20%E9%83%A8%E7%BD%B2%E9%87%87%E5%9D%91%E8%AE%B0%E5%BD%95/#%E5%8D%87%E7%BA%A7%E4%BE%9D%E8%B5%96

# 启动方式
1. unzip rocketmq-all-4.6.0-source-release.zip
2. cd rocketmq-all-4.6.0-source-release
3. mvn -Prelease-all -DskipTests clean install -U
4. cd distribution/target/rocketmq-4.6.0/rocketmq-4.6.0
5. mqnamesrv  (nohup sh bin/mqnamesrv &)
6. mqbroker -c ../conf/broker.conf  -n 127.0.0.1:9876 (nohup sh bin/mqbroker -c conf/broker.conf  -n 127.0.0.1:9876 &)
7. 