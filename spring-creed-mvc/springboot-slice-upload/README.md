主要实现的的是springboot与百度前端组件WebUploader配合实现上传功能，本例实现功能如下：

1. 分片上传
2. 断点续传
3. 秒传
4. oss表单上传
5. 分片下载

参考文章：  
[文件 IO 操作的一些最佳实践](https://www.cnkirito.moe/file-io-best-practise/#MMAP-%E8%AF%BB%E5%86%99)

[论最强IO：MappedByteBuffer VS FileChannel](https://blog.csdn.net/alex_xfboy/article/details/90174840)


上传实现思路：https://juejin.cn/post/6977555547570569223
下载方案：- https://juejin.cn/post/6954868879034155022?fireglass_rsn=true#heading-11
- https://juejin.cn/post/7025885508748181512