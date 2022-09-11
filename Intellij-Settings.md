# 添加Live Template 模板
### File -> Settings -> Editor -> Live Templates
1. add MyGroup
2. add Live Template
3. abbv: psl desc: private static final log
   template text `private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger($CLASS_NAME$.class)$END$;`
4. Edit variables: give className()

# File and Code Templates 文件创建模板
### File -> Settings -> Editor -> File and Code Templates
1. Includes
2. default as
    ```
    #set( $AUTHOR = "EthanCao" )
    #set( $EMAIL = "xxxx@xxx.com" )
    /**
    * @description: ${PROJECT_NAME}
    * @author: ${AUTHOR}
    * @email: ${EMAIL}
    * @date: ${DATE} ${TIME}
    */
    ```
3. project as
   ```
    #set( $AUTHOR = "EthanCao" )
    #set( $EMAIL = "ethan.caoq@foxmail.com" )
    /**
    * @description: ${PROJECT_NAME}
    * @author: ${AUTHOR}
    * @email: ${EMAIL}
    * @date: ${DATE} ${TIME}
    */
   ```    