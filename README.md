# ServiceLoader Code Generator

Service Provider Intercface Impletation , Generate code to source path , and reduce read META-INF/services performance issue

## Usage

An Application stucture below

![](https://ws1.sinaimg.cn/large/6e1c877fgy1fqrevz8fn9j20ea0abaa2.jpg)


ModuleA

Java
```Java
@Service(Inteface.class)
public class ImplA implements Inteface {
    @Override
    public void test() {
        System.out.println(getClass().getName());
    }
}
```

build.gradle
```Gradle
compile "com.nd.sdp.android.serviceloader:service-loader-annotation:1.0.0-develop-SNAPSHOT"
annotationProcessor "com.nd.sdp.android.serviceloader:service-loader-compiler:1.0.0-develop-SNAPSHOT"
```

ModuleB

```Java
@Service(Inteface.class)
public class ImplB implements Inteface {
    @Override
    public void test() {
        System.out.println(getClass().getName());
    }
}
```

build.gradle
```Gradle
compile "com.nd.sdp.android.serviceloader:service-loader-annotation:1.0.0-develop-SNAPSHOT"
annotationProcessor "com.nd.sdp.android.serviceloader:service-loader-compiler:1.0.0-develop-SNAPSHOT"
```

Application

Java
```Java
Iterator<ISampleList> iterator = AnnotationServiceLoader.load(ISampleList.class)
        .iterator();
while (iterator.hasNext()) {
    ISampleList next = iterator.next();
    next.test();
}
// will get ImplA and ImplB
```

build.gradle

```Gradle
compile "com.nd.sdp.android.serviceloader:service-loader:1.0.0-develop-SNAPSHOT"
annotationProcessor "com.nd.sdp.android.serviceloader:service-loader-compiler-app:1.0.0-develop-SNAPSHOT"
```