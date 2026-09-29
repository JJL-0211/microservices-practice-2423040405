本周主要创建一个可运行的Spring Boot工程，并计划未来模型的实现

启动命令：./mvnw spring-boot:run

接口回应：

1.monolith 单体项目

2.

```
{
  "groups": [
    "liveness",
    "readiness"
  ],
  "status": "UP"
}
```

测试命令：./mvnw test
测试结果：[INFO] Scanning for projects...
[INFO]
[INFO] -----------------------< com.zjgsu.ljj:monolith >-----------------------
[INFO] Building  0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- resources:3.3.1:resources (default-resources) @ monolith ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 0 resource from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.14.1:compile (default-compile) @ monolith ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- resources:3.3.1:testResources (default-testResources) @ monolith ---
[INFO] skip non existing resourceDirectory D:\microservices-practice-2423040405\monolith\src\test\resources
[INFO]
[INFO] --- compiler:3.14.1:testCompile (default-testCompile) @ monolith ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 1 source file with javac [debug parameters release 25] to target\test-classes
[INFO]
[INFO] --- surefire:3.5.6:test (default-test) @ monolith ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.zjgsu.ljj.MonolithApplicationTests
10:33:41.323 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.zjgsu.ljj.MonolithApplicationTests]: MonolithApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
10:33:41.587 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.zjgsu.ljj.MonolithApplication for test class com.zjgsu.ljj.MonolithApplicationTests
10:33:41.763 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.zjgsu.ljj.MonolithApplicationTests]: MonolithApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
10:33:41.768 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.zjgsu.ljj.MonolithApplication for test class com.zjgsu.ljj.MonolithApplicationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.8)

2026-09-29T10:33:42.410+08:00  INFO 53956 --- [           main] com.zjgsu.ljj.MonolithApplicationTests   : Starting MonolithApplicationTests using Java 26.0.2.1 with PID 53956 (started by joice in D:\microservices-practice-2423040405\monolith)
2026-09-29T10:33:42.414+08:00  INFO 53956 --- [           main] com.zjgsu.ljj.MonolithApplicationTests   : No active profile set, falling back to 1 default profile: "default"
2026-09-29T10:33:51.001+08:00  INFO 53956 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2026-09-29T10:33:51.116+08:00  INFO 53956 --- [           main] com.zjgsu.ljj.MonolithApplicationTests   : Started MonolithApplicationTests in 9.246 seconds (process running for 10.995)
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK. Please add Mockito as an agent to your build as described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
WARNING: A Java agent has been loaded dynamically (C:\Users\joice\.m2\repository\net\bytebuddy\byte-buddy-agent\1.17.8\byte-buddy-agent-1.17.8.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 11.52 s -- in com.zjgsu.ljj.MonolithApplicationTests
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  24.492 s
[INFO] Finished at: 2026-09-29T10:33:52+08:00
[INFO] ------------------------------------------------------------------------

这周主要做了主体框架，然后就是可以运行也可以测试

