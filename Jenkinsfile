// spring-creed CI/CD pipeline
//
// 依赖的 Jenkins 全局配置（已在 creed-jenkins 容器内就绪）：
//   - Maven 工具:      maven-3.9（自动安装器）
//   - Sonar 服务器:    creed-sonarqube -> http://host.docker.internal:9900（凭证 sonar-token）
//   - Maven settings:  /var/jenkins_home/maven-settings-artifactory.xml（Artifactory 凭证 + mirror）
//
// 注意：pom 的 distributionManagement 指向 localhost:8082（宿主机视角），
// Jenkins 容器内必须走 host.docker.internal，故 deploy 用 altDeploymentRepository 覆盖。
//
// 发布正式版：Build with Parameters → 勾选 RELEASE、填 RELEASE_VERSION（如 2026.07.0）。
// 发布后打 tag / bump 下一个 SNAPSHOT 暂为手动，见 docs/Maven版本发布(revision-driven).md 第 3 节。
pipeline {
    agent any

    parameters {
        booleanParam(name: 'RELEASE', defaultValue: false, description: '勾选则以 -Prelease 发布正式版（enforcer 校验 + libs-release-local）')
        string(name: 'RELEASE_VERSION', defaultValue: '', description: '正式版本号，如 2026.07.0；RELEASE 勾选时必填，不填日常构建走 pom 里的 SNAPSHOT')
    }

    tools {
        maven 'maven-3.9'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        MAVEN_SETTINGS       = '/var/jenkins_home/maven-settings-artifactory.xml'
        SONAR_MAVEN_PLUGIN   = 'org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751'
        DEPLOY_SNAPSHOT_REPO = 'creed-artifactory-snapshots::http://host.docker.internal:8082/artifactory/libs-snapshot-local'
        DEPLOY_RELEASE_REPO  = 'creed-artifactory-releases::http://host.docker.internal:8082/artifactory/libs-release-local'
        // release 时全部 mvn 命令统一注入 -Drevision，保证扫描与发布的是同一版本的字节码；
        // 日常构建两者为空串，行为与参数化前完全一致
        REVISION_ARG         = "${params.RELEASE ? '-Drevision=' + params.RELEASE_VERSION.trim() : ''}"
        RELEASE_PROFILE_ARG  = "${params.RELEASE ? '-Prelease' : ''}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    if (params.RELEASE) {
                        if (!params.RELEASE_VERSION?.trim()) {
                            error 'RELEASE 模式必须填写 RELEASE_VERSION（如 2026.07.0）'
                        }
                        if (params.RELEASE_VERSION.contains('SNAPSHOT')) {
                            error 'RELEASE_VERSION 不能是 SNAPSHOT 版本'
                        }
                    }
                }
                git url: 'https://github.com/PhotonAlpha/spring-creed.git', branch: 'master'
            }
        }

        // 编译 + 单测 + 覆盖率（jacoco 已配置在根 pom，verify 阶段自动生成报告）
        stage('Build & Test') {
            steps {
                sh 'mvn -B -s "$MAVEN_SETTINGS" $REVISION_ARG clean verify'
            }
        }

        // 分析并上报（withSonarQubeEnv 注入服务器地址与 token）
        stage('Sonar Scan') {
            steps {
                withSonarQubeEnv('creed-sonarqube') {
                    sh 'mvn -B -s "$MAVEN_SETTINGS" $REVISION_ARG "$SONAR_MAVEN_PLUGIN:sonar" -Dsonar.projectKey=spring-creed'
                }
            }
        }

        // 门禁不过则中止（依赖 SonarQube webhook: http://host.docker.internal:8180/sonarqube-webhook/）
        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // 门禁通过才上传 Artifactory；release 时 -Prelease 触发 enforcer
        // （requireReleaseDeps / requireReleaseVersion / BUILD_NUMBER 由 Jenkins 注入）
        stage('Deploy') {
            steps {
                sh 'mvn -B -s "$MAVEN_SETTINGS" $REVISION_ARG $RELEASE_PROFILE_ARG deploy -DskipTests ' +
                   '-DaltSnapshotDeploymentRepository=$DEPLOY_SNAPSHOT_REPO ' +
                   '-DaltReleaseDeploymentRepository=$DEPLOY_RELEASE_REPO'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
