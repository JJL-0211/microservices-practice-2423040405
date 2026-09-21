环境检查
C:\Users\joice>wsl -d Ubuntu
jjl@jjl:/mnt/c/Users/joice$ java --version
java 26.0.2.1 2026-08-18
Java(TM) SE Runtime Environment (build 26.0.2.1+1-7)
Java HotSpot(TM) 64-Bit Server VM (build 26.0.2.1+1-7, mixed mode, sharing)
jjl@jjl:/mnt/c/Users/joice$ mvn --version
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: /opt/maven
Java version: 26.0.2.1, vendor: Oracle Corporation, runtime: /opt/jdk-26
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.18.33.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
jjl@jjl:/mnt/c/Users/joice$ git --version
git version 2.53.0
jjl@jjl:/mnt/c/Users/joice$ docker version
Client:
 Version:           29.8.0
 API version:       1.56
 Go version:        go1.26.8
 Git commit:        88096ef
 Built:             Thu Sep  3 21:53:38 2026
 OS/Arch:           windows/amd64
 Context:           desktop-linux

Server: Docker Desktop 4.91.0 (239619)
 Engine:
  Version:          29.8.0
  API version:      1.56 (minimum version 1.40)
  Go version:       go1.26.8
  Git commit:       3ce5872
  Built:            Thu Sep  3 21:51:20 2026
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          v2.3.4
  GitCommit:        db8809540e1a7a9da5d518876894933ff55692ab
 runc:
  Version:          1.4.3
  GitCommit:        v1.4.3-0-gbb14dabe
 docker-init:
  Version:          0.19.0
  GitCommit:        de40ad0
jjl@jjl:/mnt/c/Users/joice$ docker compose version
Docker Compose version v5.5.1
概念回答
1.把应用拆成多个独立小服务，每个服务单独部署、自治，通过网络接口通信，各自负责单一业务能力。
2.单体是所有功能打包在一个项目里，统一部署；微服务拆成多个独立服务，分开开发、部署、扩缩容。单体简单易上手，微服务更适合大型复杂项目，但运维复杂度更高。
3.先通过单体快速理解完整业务逻辑，避免过早引入分布式复杂性；再基于已有系统做拆分，直观体会微服务解决的问题与带来的代价。
4.方便验证功能正确性，每次修改后一键复现结果，保证作业可核验、减少人工验证误差
