# Gradle 设置代理的方式



1. gradle/wrapper/gradle-wrapper.properties

   修改为本地路径

   ```properties
   distributionUrl=file\:/xxxx/tools/gradle/gradle-8.5-bin.zip
   ```

2. settings.gradle.kts

   ```kotlin
   pluginManagement {
       repositories {
           mavenCentral()
           maven {
               credentials {
                   username= "username"
                   password= "encrypted passowrd"
               }
               url = uri("https://artifactor.xxxx.com/artifactory/virtual-maven")
           }
       }
   }
   ```

3. build.gradle.kts

   ```kotlin
   buildscript {
       repositories {
           mavenCentral()
           maven {
               credentials {
                   username= "username"
                   password= "encrypted passowrd"
               }
               url = uri("https://artifactor.xxxx.com/artifactory/virtual-maven")
           }
       }
   }
   repositories {
       gradlePluginPortal()
       mavenCentral()
       maven {
           credentials {
               username= "username"
               password= "encrypted passowrd"
           }
           url = uri("https://artifactor.xxxx.com/artifactory/virtual-maven")
       }
   }
   ```

4. 检查是否起作用, 如果需要链接互联网的插件下载，则需要添加代理配置（可选）

   gradle.properties

   ```properties
   systemProp.http.proxyHost=
   systemProp.http.proxyPort=8081
   systemProp.http.proxyUser=
   systemProp.http.proxyPassword=
   systemProp.http.nonProxyHosts=*.nonproxyrepos.com|localhost
   
   systemProp.https.proxyHost=
   systemProp.https.proxyPort=8081
   systemProp.https.proxyUser=
   systemProp.https.proxyPassword=
   systemProp.https.nonProxyHosts=*.nonproxyrepos.com|localhost
   ```

   