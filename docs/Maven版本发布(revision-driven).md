# Maven 版本发布指南（revision-driven / CI-friendly）

> 适用范围：spring-creed 多模块工程。工程采用 `${revision}` CI-friendly 版本 + `flatten-maven-plugin` + `-Prelease` enforcer 校验。

## 1. 为什么不用 `maven-release-plugin` 的 `release:prepare`

经典 `maven-release-plugin`（`release:prepare` → `release:perform`）的工作方式是**逐个改写每个 pom 里的字面量 `<version>`**：

```
X-SNAPSHOT  →  X  →  提交打 tag  →  X+1-SNAPSHOT  →  再提交
```

而本工程的版本是属性 `${revision}`（定义在根 pom：`<revision>2026.07-SNAPSHOT</revision>`），不是字面量。release 插件不会改写任意属性，因此 `release:prepare` 对 `${revision}` 无能为力。这正是 [Maven CI-friendly 官方文档](https://maven.apache.org/maven-ci-friendly.html) 明确指出「此时不需要 maven-release-plugin」的场景。

所以「release 插件」的职责被拆成两个能真正配合 `${revision}` 的插件：

| 职责 | 经典 release 插件 | 本工程 revision-driven 落地 |
|------|------------------|------------------------------|
| 定版本号 | 改写 pom | `-Drevision=2026.07.0`（单一版本源） |
| 打 tag / 推送 | `release:prepare` | **maven-scm-plugin** 的 `scm:tag` |
| 递增下个开发版本 | 自动 `+1-SNAPSHOT` | **versions-maven-plugin** 改 `revision` 属性 |
| 用 tag 出包 | `release:perform` | CI 直接 `deploy`（已在 release commit 上） |

## 2. 已落地的 pom 改动（根 `pom.xml`）

### 2.1 `<properties>` 新增插件版本

```xml
<maven-scm-plugin.version>2.1.0</maven-scm-plugin.version>
<versions-maven-plugin.version>2.18.0</versions-maven-plugin.version>
```

### 2.2 填充 `<scm>`（`scm:tag` 依赖它）

```xml
<scm>
    <connection>scm:git:https://github.com/PhotonAlpha/spring-creed.git</connection>
    <developerConnection>scm:git:https://github.com/PhotonAlpha/spring-creed.git</developerConnection>
    <url>https://github.com/PhotonAlpha/spring-creed</url>
    <tag>HEAD</tag>
</scm>
```

### 2.3 `<build><pluginManagement>` 登记两个插件

```xml
<!-- 打 release tag：<tag> 即 tag 名，${revision} 解析为正式版本号 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-scm-plugin</artifactId>
    <version>${maven-scm-plugin.version}</version>
    <configuration>
        <tag>v${revision}</tag>
    </configuration>
</plugin>

<!-- 递增 revision 属性到下一个 SNAPSHOT -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>${versions-maven-plugin.version}</version>
    <configuration>
        <generateBackupPoms>false</generateBackupPoms>
    </configuration>
</plugin>
```

> 放 `pluginManagement` 即可——两者都是命令行按需调用的目标（`scm:tag` / `versions:set-property`），无需绑定生命周期 phase。

### 2.4 `-Prelease` enforcer 无需改动

已有的 `-Prelease` profile 规则与本流程天然配套：

- `requireReleaseVersion`：要求自身版本非 SNAPSHOT → 只要发布时传 `-Drevision=2026.07.0`（不带 `-SNAPSHOT`）即可通过。
- `requireReleaseDeps`：禁止 SNAPSHOT 依赖。
- `dependencyConvergence`：依赖版本收敛。
- `requireEnvironmentVariable(BUILD_NUMBER)`：封死本机发 release，只允许 Jenkins。

## 3. 发布流程（命令）

```bash
# 1. 出正式包并推 Artifactory（-Prelease 触发 enforcer 校验：无 SNAPSHOT 依赖、必须在 Jenkins 等）
mvn -Prelease -Drevision=2026.07.0 clean deploy

# 2. 用正式版本号打 tag 并推送（v2026.07.0）
mvn -Drevision=2026.07.0 scm:tag

# 3. 递增开发版本并提交回 master（下一轮用 2026.08-SNAPSHOT）
mvn versions:set-property -Dproperty=revision -DnewVersion=2026.08-SNAPSHOT
git commit -am "chore: prepare for next development iteration 2026.08-SNAPSHOT"
git push origin master
```

**核心**：版本号只有 `revision` 一个来源；`deploy` / `scm:tag` / `versions:set-property` 全部围绕它，不产生任何 pom 字面量改写，与 flatten `oss` 模式零冲突。

## 4. Jenkinsfile 片段

```groovy
pipeline {
  parameters {
    string(name: 'RELEASE_VERSION', defaultValue: '2026.07.0')
    string(name: 'NEXT_VERSION',    defaultValue: '2026.08-SNAPSHOT')
  }
  stages {
    stage('Release deploy') {
      steps {
        // BUILD_NUMBER 由 Jenkins 注入 → 满足 enforcer 的 requireEnvironmentVariable
        sh "mvn -Prelease -Drevision=${params.RELEASE_VERSION} clean deploy"
      }
    }
    stage('Tag') {
      steps { sh "mvn -Drevision=${params.RELEASE_VERSION} scm:tag" }
    }
    stage('Bump next iteration') {
      steps {
        sh "mvn versions:set-property -Dproperty=revision -DnewVersion=${params.NEXT_VERSION}"
        sh "git commit -am 'chore: next iteration ${params.NEXT_VERSION}' && git push origin master"
      }
    }
  }
}
```

## 5. 常见问题

- **本机执行 `-Prelease` 报缺 `BUILD_NUMBER`**：预期行为，release 只允许在 Jenkins 跑。本机自测可临时 `export BUILD_NUMBER=local`，但不要用它真正 deploy。
- **`scm:tag` 需要推送凭证**：Jenkins 上配置 Git 凭证；tag 名由 `<tag>v${revision}</tag>` 决定。
- **想拉维护分支**：不要用 `maven-release-plugin` 的 `release:branch`（见下）。用 git 直接拉即可。

## 6. 维护分支：为什么不用 `release:branch`（实测结论）

曾尝试引入 `maven-release-plugin:3.1.1` 用 `release:branch` 拉维护分支，即使把版本改写全关掉：

```xml
<updateBranchVersions>false</updateBranchVersions>
<updateWorkingCopyVersions>false</updateWorkingCopyVersions>
```

实际执行 `mvn release:branch -DbranchName=maint-2026.07 -DdryRun=true` 仍然失败：

```
Failed to execute goal ...maven-release-plugin:3.1.1:branch:
The artifact (com.ethan:spring-creed-starter-security) requires a different version
(2026.07-SNAPSHOT) than what is found (${revision}) for the expression
(${spring-creed-boot.version}) in the project (com.ethan:spring-creed-dependencies).
```

**原因**：`release:branch` 在拉分支前会先跑 `check-poms` 阶段，逐模块校验版本表达式一致性。本工程用 `${spring-creed-boot.version}` → `${revision}` 的间接引用，release 插件无法把解析出的 `2026.07-SNAPSHOT` 和字面量 `${revision}` 对上，直接报错。这个校验阶段无法绕过 —— **`maven-release-plugin` 与 `${revision}` 从根本上不兼容**。

**正确做法：用 git 直接从 release tag 拉维护分支**

```bash
# 从已发布的 tag 拉一条只收 bugfix 的维护分支
git checkout -b maint-2026.07 v2026.07.0
git push -u origin maint-2026.07

# 在维护分支上出补丁版本（版本源依然是 revision）
mvn -Prelease -Drevision=2026.07.1 clean deploy
mvn -Drevision=2026.07.1 scm:tag
```

一句话：CI-friendly（`${revision}`）工程里，**发布用 `scm:tag` + `versions:set-property`，分支用原生 git**，全程不需要 `maven-release-plugin`。