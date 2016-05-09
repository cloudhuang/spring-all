How to run groovy unit tests in Maven
=====================================

1. 在pom.xml中加入groovy的依赖
```
<dependency>
	<groupId>org.codehaus.groovy</groupId>
	<artifactId>groovy-all</artifactId>
	<version>2.4.5</version>
</dependency>
```
2. 配置maven-compiler-plugin 插件
```
<plugin> 
  <artifactId>maven-compiler-plugin</artifactId>  
  <version>3.1</version>  
  <configuration> 
    <compilerId>groovy-eclipse-compiler</compilerId> 
  </configuration>  
  <dependencies> 
    <dependency> 
      <groupId>org.codehaus.groovy</groupId>  
      <artifactId>groovy-eclipse-compiler</artifactId>  
      <version>2.9.1-01</version> 
    </dependency>  
    <dependency> 
      <groupId>org.codehaus.groovy</groupId>  
      <artifactId>groovy-eclipse-batch</artifactId>  
      <version>2.3.7-01</version> 
    </dependency> 
  </dependencies> 
</plugin>
```
3. 默认情况下, IDEA不会自动的将src/test/groovy目录设置类test目录，在pom.xml中添加如下插件,将src/test/groovy作为测试源文件目录

```
<?xml version="1.0" encoding="utf-8"?>

<plugin> 
  <groupId>org.codehaus.mojo</groupId>  
  <artifactId>build-helper-maven-plugin</artifactId>  
  <executions> 
    <execution> 
      <phase>generate-sources</phase>  
      <goals> 
        <goal>add-test-source</goal> 
      </goals>  
      <configuration> 
        <sources> 
          <source>src/test/groovy</source> 
        </sources> 
      </configuration> 
    </execution> 
  </executions> 
</plugin>
```
