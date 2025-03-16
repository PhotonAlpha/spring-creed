# Fastify 是一个高效且快速的Node.js web 框架

- [ ] Fastify 框架搭建

- [ ] Typebo: is a runtime type builder that creates in-memory Json Schema objects that infer as TypeScript types.

- [ ] 搭配ajv库，目的是返回response的时候，使用默认值

- [x] 自动执行type box验证json schema

  1. 见 [Manually change npm's default directory](#Manually change npm's default directory)

  2. npm install -g ts-node

  3. VS Code 安装run code 插件，修改配置 

     1. command + shift + P  

     2.  preferences:Open User Settings 

     3.  Extensions: Run Code Configuration 

     4.  Edit in Settings.json 

     5. ```json
        {"code-runner.executorMap": 
         {"typescript": "node_modules/.bin/ts-node"}
        }
        ```

  4. 在任意ts文件即可进行调试

     ```typescript
     import { Static, TSchema, Type } from '@sinclair/typebox';
     import json2typebox from 'json2typebox';
     import moment from 'moment';
     
     const Vector = <T extends TSchema>(T: T) => 
         Type.Object({                                      // type Vector<T> = {
           x: T,                                            //   x: T,
           y: T,                                            //   y: T,
           z: T                                             //   z: T
         })                                                 // }
     const NumberVector = Vector(Type.Number())           // type NumberVector = Vector<number>
     
     console.log(JSON.stringify(NumberVector, null, 2))
     console.log(moment().format('YYYY-MM-DDTHH:mm:ss.SSSZZ'))
     json2typebox(`{"countryCode": "MY",
                 "loginId": "abc",
                 "groupAbbvName": "def",
                 "alternateProfileDetails": {
                     "altUserId": "lkIbZjsV0qwYWSyAIf1LrlnHx67laUD-WGBai6sIyeJqZ432vAQ",
                     "altGroupId": "6_1oq1WbDNv-3Idh9nWVf1AGFGb0o5SSckYzcEfA1HvVEiZ5",
                     "altBankEntityId": "NbeO1MQggt778UX5BHOnYLLFNjg6Z0_xOyXjjbcNehENNyf_"
                 }}`, 'Root').then((code) => {
       console.log('code',code);
       /*
         import { Type, Static } from '@sinclair/typebox'
     
         export type Data = Static<typeof Data>
         export const Data = Type.Object({
           id: Type.Number(),
           name: Type.String()
         })
      */
     });
     ```

     

- [ ] todo

### Manually change npm's default directory

[REF: Manually change npm's default directory](https://docs.npmjs.com/resolving-eacces-permissions-errors-when-installing-packages-globally/#manually-change-npms-default-directory)

> To minimize the chance of permissions errors, you can configure npm to use a different directory. In this example, you will create and use hidden directory in your home directory.
>
> 1. Back up your computer.
>
> 2. On the command line, in your home directory, create a directory for global installations:
>
>    ```shell
>    mkdir -p ~/.npm-global/lib
>    ```
>
> 3. Configure npm to use the new directory path:
>
>    ```shell
>    npm config set prefix '~/.npm-global'
>    ```
>
> 4. In your preferred text editor, open or create a `~/.zshrc` file and add this line:
>
>    ```shell
>    export PATH=~/.npm-global/bin:$PATH
>    ```
>
> 5. On the command line, update your system variables:
>
>    ```shell
>    source ~/.zshrc
>    ```
>
> 6. To test your new configuration, install a package globally without using `sudo`:
>
>    ```shell
>    npm install -g jshint
>    ```