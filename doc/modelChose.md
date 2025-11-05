# 用于练习不同库的项目
# 有三种可行的组织方式
针对包含多个 main 类的本地测试工具项目，以下是推荐的几种项目结构方案：
方案一：单模块多包结构（推荐） 当前使用
方案二：多模块结构（适合大型工具集）
方案三：使用 Gradle Source Sets（灵活分离）
# 方案一：单模块多包结构（推荐）
## 项目结构
```
tool/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── yourname/
│       │           └── tool/
│       │               ├── common/           # 公共代码
│       │               │   ├── util/
│       │               │   └── config/
│       │               ├── server/           # 服务器相关
│       │               │   ├── ServerMain.java
│       │               │   └── handler/
│       │               ├── client/           # 客户端相关
│       │               │   ├── ClientMain.java
│       │               │   └── ui/
│       │               ├── proto/            # Protobuf 测试
│       │               │   ├── ProtoTestMain.java
│       │               │   └── message/
│       │               └── demo/             # 各种框架 demo
│       │                   ├── SpringDemoMain.java
│       │                   └── NettyDemoMain.java
│       ├── resources/
│       └── proto/                            # Proto 文件
├── build.gradle
└── settings.gradle
```

## build.gradle 配置
```gradlew
// 共用dependencies 和一些初始化任务
dependencies {
    // 基础依赖
    implementation 'com.google.protobuf:protobuf-java:3.25.1'
    implementation 'io.grpc:grpc-all:1.59.0'
    implementation 'javax.annotation:javax.annotation-api:1.3.2'
    
    // 网络框架
    implementation 'io.netty:netty-all:4.1.86.Final'
    
    // 工具库
    implementation 'com.google.guava:guava:31.1-jre'
    implementation 'org.slf4j:slf4j-simple:2.0.6'
    
    // 测试框架
    testImplementation 'junit:junit:4.13.2'
}

// 为每个 Main 类创建独立的运行任务
task runServer(type: JavaExec) {
    group = 'application'
    description = '运行服务器'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.yourname.tool.server.ServerMain'
    standardInput = System.in
}

task runClient(type: JavaExec) {
    group = 'application'
    description = '运行客户端'
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.yourname.tool.client.ClientMain'
    standardInput = System.in
}
```

# 方案二：多模块结构（适合大型工具集）
## 项目结构
```
tool/
├── build.gradle
├── settings.gradle
├── core/                          # 核心模块
│   ├── build.gradle
│   └── src/main/java/
├── server/                        # 服务器模块
│   ├── build.gradle
│   └── src/main/java/
├── client/                        # 客户端模块
│   ├── build.gradle
│   └── src/main/java/
├── proto-tests/                   # Protobuf 测试模块
│   ├── build.gradle
│   └── src/main/java/
└── demos/                         # 各种 Demo 模块
├── build.gradle
└── src/main/java/
```
settings.gradle
```gradle
rootProject.name = 'java-tool'

include 'core'
include 'server'
include 'client'
include 'proto-tests'
include 'demos'
```

## 根项目 build.gradle
```gradle
// 公共配置
subprojects {
    apply plugin: 'java'
    apply plugin: 'com.google.protobuf'
    
    group = 'com.yourname.tool'
    version = '1.0.0'
    
    repositories {
        mavenCentral()
    }
    
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// 项目特定配置
project(':core') {
    dependencies {
        implementation 'com.google.protobuf:protobuf-java:3.25.1'
    }
}

project(':server') {
    dependencies {
        implementation project(':core')
        implementation 'io.netty:netty-all:4.1.86.Final'
    }
}

project(':client') {
    dependencies {
        implementation project(':core')
    }
}
```
# 方案三：使用 Gradle Source Sets（灵活分离）
## build.gradle 配置
```gradle
plugins {
    id 'java'
    id 'application'
}

// 定义多个源集
sourceSets {
    main {
        java {
            srcDirs = ['src/main/java']
        }
        resources {
            srcDirs = ['src/main/resources']
        }
    }
    
    server {
        java {
            srcDirs = ['src/server/java']
        }
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
    
    client {
        java {
            srcDirs = ['src/client/java']
        }
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
    
    demos {
        java {
            srcDirs = ['src/demos/java']
        }
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
    }
}

// 配置依赖
dependencies {
    // 主源集依赖
    implementation 'com.google.protobuf:protobuf-java:3.25.1'
    
    // 各源集特定依赖
    serverImplementation 'io.netty:netty-all:4.1.86.Final'
    clientImplementation 'com.fasterxml.jackson.core:jackson-databind:2.14.2'
    demosImplementation 'org.springframework.boot:spring-boot-starter:2.7.8'
}

// 为每个源集创建运行任务
task runServer(type: JavaExec) {
    group = 'application'
    description = '运行服务器'
    classpath = sourceSets.server.runtimeClasspath
    mainClass = 'com.yourname.tool.server.ServerMain'
}

task runClient(type: JavaExec) {
    group = 'application'
    description = '运行客户端'
    classpath = sourceSets.client.runtimeClasspath
    mainClass = 'com.yourname.tool.client.ClientMain'
}
```
## 对应的目录结构
```
tool/
├── src/
│   ├── main/           # 公共代码
│   │   ├── java/
│   │   └── resources/
│   ├── server/         # 服务器代码
│   │   └── java/
│   ├── client/         # 客户端代码
│   │   └── java/
│   └── demos/          # Demo 代码
│       └── java/
├── build.gradle
└── settings.gradle
```