// spring-creed CI/CD pipeline
//
// 依赖的 Jenkins 全局配置（已在 creed-jenkins 容器内就绪）：
//   - Maven 工具:      maven-3.9（自动安装器）
//   - Sonar 服务器:    creed-sonarqube -> http://host.docker.internal:9900（凭证 sonar-token）
//   - Maven settings:  /var/jenkins_home/maven-settings-artifactory.xml（Artifactory 凭证 + mirror）
//
// 注意：pom 的 distributionManagement 指向 localhost:8082（宿主机视角），
// Jenkins 容器内必须走 host.docker.internal，故 deploy 用 altDeploymentRepository 覆盖。
pipeline {
    agent any

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
        JACOCO               = 'org.jacoco:jacoco-maven-plugin:0.8.12'
        SONAR_MAVEN_PLUGIN   = 'org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751'
        DEPLOY_SNAPSHOT_REPO = 'creed-artifactory-snapshots::http://host.docker.internal:8082/artifactory/libs-snapshot-local'
        DEPLOY_RELEASE_REPO  = 'creed-artifactory-releases::http://host.docker.internal:8082/artifactory/libs-release-local'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/PhotonAlpha/spring-creed.git', branch: 'master'
            }
        }

        // 编译 + 单测 + 覆盖率（jacoco 未入 pom，先由 CLI 附加）
        stage('Build & Test') {
            steps {
                sh 'mvn -B -s "$MAVEN_SETTINGS" "$JACOCO:prepare-agent" clean verify "$JACOCO:report"'
            }
        }

        // 分析并上报（withSonarQubeEnv 注入服务器地址与 token）
        stage('Sonar Scan') {
            steps {
                withSonarQubeEnv('creed-sonarqube') {
                    sh 'mvn -B -s "$MAVEN_SETTINGS" "$SONAR_MAVEN_PLUGIN:sonar" -Dsonar.projectKey=spring-creed'
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

        // 门禁通过才上传 Artifactory
        stage('Deploy') {
            steps {
                sh 'mvn -B -s "$MAVEN_SETTINGS" deploy -DskipTests ' +
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
