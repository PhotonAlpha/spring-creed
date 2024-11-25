## 1. 清理target
`mvn clean -U`

## 2. 清理该项目依赖的本地仓库中的maven包
mvn dependency:purge-local-repository
> 这个命令会清理pom.xml中的包，并重新下载，但是并不清理不在pom.xml中的依赖包。

下面的是扩展：
`mvn dependency:purge-local-repository -DreResolve=false` reResolve是否重新解析依赖关系

`mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false ` actTransitively是否应该对所有传递依赖性起作用。默认值为true。

`mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false --fail-at-end ` 忽略错误（ --fail-at-end ）。 对于那些有一些依赖关系混乱的项目，或者依赖于一些内部的仓库（这种情况发生），这样做有时是有用的。

## 3. 如果你只想清理某个特定的依赖项，可以使用以下命令：
`mvn -f pom.xml dependency:purge-local-repository -Dinclude=com.ethan:*`
有两个参数需要注意：
    `manualInclude` 参数用于手动指定要包含的依赖项，通常以 `groupId:artifactId:version` 的形式提供。这样做将清理本地仓库中的指定依赖项，而不考虑其他匹配规则。
    `include` 参数用于指定一个 ANT 风格的通配符，用于匹配要包含的依赖项。通常，你可以使用 `groupId:artifactId:*` 这样的通配符来匹配指定 `groupId` 和 `artifactId` 下的所有版本。