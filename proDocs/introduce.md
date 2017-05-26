最近写了一个轻量级的服务器库 MockNet，可以在 Android 上或者 Java 平台快速搭建服务器来开发测试网络接口，不需要服务器知识就可以使用。

### 写这个框架的原因
平时写项目的时候，总会遇到这些情况
1. 服务器接口还没有写好，Android 客户端只能先做一些界面的开发工作，遇到需要展示数据的地方只能先留着或者用一些假数据填充，服务器接口写好以后，这些代码又得删掉或者再改。
2. 在调试网络接口的时候，往往需要后台客户端一起配合，有时候会花不少时间确认是服务器还是客户端的问题，之后联调也很花时间。
3. 在没有网络但是又需要展示应用功能的时候，只能用一堆 if else 来添加假数据或者数据库里的数据。代码写起来很不方便。

写这个框架就是想解决这些问题。
* 首先是网络接口开发的问题，通过 MockNet 这个库，无需后台接口完成就可以进行网络接口的开发，开发流程要更顺畅一点。  
* 其次是网络接口调试的问题，通过 MockNet 这个库，在没有后台同学的帮助下也可以对 Android 客户端网络接口进行调试。
* 最后是没有网络需要展示功能的问题，通过 MockNet 这个库，没有网络一样可以展示应用的网络功能。

### 框架介绍
MockNet 是为了方便客户端网络接口的开发和测试而写的，简单来说就是在本地启动服务器用来响应客户端的网络请求。但是不需要有服务器开发知识，几行代码就可以添加此功能。
代码上传到了 github 上，网址：https://github.com/5A59/MockNet

### 框架下载
Gradle 
``` gradle
// 在模块的 gradle 文件中添加下面的代码  
compile 'com.zy.mocknet:mocknet:1.0'
```

Maven
``` xml
<dependency>
  <groupId>com.zy.mocknet</groupId>
  <artifactId>mocknet</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

jar包下载
[mocknet_1_0_0.jar](https://github.com/5A59/MockNet/blob/master/jar/mocknet_1_0_0.jar)


### 框架使用
[使用示例](https://github.com/5A59/MockNet/blob/master/demo/src/main/java/com/zy/demo/MainActivity.java)  
MockNet 使用简单，步骤如下：
1. 修改网络访问 ip 为本地 ip (127.0.0.1:port 或 本地真实ip:port)，因为服务器是在本地搭建的。
2. 初始化。
``` java
// 创建 MockNet
MockNet mockNet = MockNet.create();
```
3. 添加对请求的处理
MockNet 中对每一个 request 都会对应一个 response，request 和 response 和起来称为一个 MockConnection，添加对请求的处理就是添加 MockConnection 实例。
``` java
MockConnection conn = MockConnectionFactory.getInstance()
    .createGeneralConnection("/*", "general connection");
mockNet.addConnection(conn);
```
createGeneralConnection(String, String) 第一个参数是请求 url，例：/test，第二个参数是返回内容。这是最简单的创建方式，更多的 MockConnection 创建方式可以参看 [MockNet介绍](https://github.com/5A59/MockNet) 或者 [java文档](https://5a59.github.io/MockNet/proDocs/javadoc/index.html)
4. 启动服务
``` java
// 默认使用 8088 端口
mockNet.start();
// 使用指定的端口
mockNet.start(int port);
```
5. 关闭服务
``` java
mockNet.stop();
```

以上就是 MockNet 的使用方法。还可以通过链式调用来写，代码更简洁。
``` java
MockNet mockNet = MockNet.create()
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/test", "{'res':'ok'}"))
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/*", "{'res':'ok'}"))
                .start();
```

### 进阶使用
上面是 MockNet 的简单使用，还有更多的扩展用法。 
1. 自定义 MockConnection  
通过 MockConnection.Builder 生成 Builder，并通过 Builder 相关方法构建 MockConnection。Builder 常用的方法可以参看文档:[Builder](https://5a59.github.io/MockNet/proDocs/javadoc/index.html)

2. 随机对请求做出响应 
MockNet 内部通过 url 和 method(GET，POST等)来对请求做出区分，在 addConnection 时如果添加了多个相同的 url 和 method 的 MockConnection，会通过 IConnectionSelector 来选择其中之一进行返回，默认使用 RandomSelector 随机返回。
可以通过 实现 IConnectionSelector 接口并通过 MockNet.setSelector() 设置返回规则。

3. Log 设置  
默认对每个 MockConnection 都会输出 Log 以帮助调试，如果想关闭 Log，可以在构建 MockConnection 时设置 isLog(false)。  
MockNet 的 Log 输出是由 Logger 和 Printer 完成的，默认设置了 AndroidPrinter 和 JavaPrinter，如果想自定义 Log，可以实现 Printer 接口，并调用 Logger.init(yourPrinter) 设置，但是要在调用了 MockConnection.create() 之后，否则设置会被覆盖为默认设置。

4. 自定义 Handler 对请求响应进行处理  
MockNet 中对 Request 和 Response 的处理采用了责任链模式(可查看后面的框架介绍)，通过添加 Handler 增加处理环节，框架自带的 Handler 有 BlockHandler，LogHandler，VerifyHeaderHandler，VerifyParamHandler，ConnectionHandler。
如果想增加自己的处理环节，请实现 Handler 接口，并通过 MockNet.addHandler(Handler h) 来设置。具体实现方法可参照框架默认实现的 Handler 代码。

5. 支持 https  
支持 https 可以通过下面代码来开启:
``` java
MockNet mockNet = MockNet.create();
mockNet.start(ServerSocketFactory.createHttpsServerSocket(int port, String jksPath, String storePwd));
```

6. 动态处理数据
为了简化使用方法和加快开发速度，默认只支持了返回静态数据，暂时没有对数据进行动态处理。如果想动态处理请求数据，可以继承 RequestExecutor 接口并实现 execute 方法。在 execute 方法中对请求进行动态处理，并创建 Reponse 返回。
之后通过 Server 构造函数构造 Server 对象并传入实现 RequestExecutor 接口的类对象。
具体可以参考 MockRequestExecutor 的实现以及 Server 构造函数。

更多使用方法可查看 [MockNet](https://github.com/5A59/MockNet) 和 [java文档](https://5a59.github.io/MockNet/proDocs/javadoc/index.html)

### 库架构
上面介绍了框架的使用，这里对框架的实现做一些介绍。  
先上一张架构图。
![架构图](https://github.com/5A59/MockNet/blob/master/proDocs/mocknet.png)  

自己认为整个架构还是不错的(-_-)。
整体可分为两层， Server 层和 Application 层。
##### Server 层
Server 层主要做的工作是 socket 通信和对 Request，Response 的解析。 
Server 类主要是对端口的监听，接受 socket 请求并创建 RequestRunnable 处理请求。 
RequestRunnable 创建后会加入到线程池中。RequestRunnable 类中对请求进行解析，并把请求发送到 RequestExecutor 去处理，RequestExecutor 会返回 Response，RequestRunnable 再负责把 Response 写入到 socket 中。

##### Application 层
Application 层工作是对 Request 的处理。通过实现 RequestExecutor 接口处理 Request 并返回 Response。
框架中默认实现 RequestRunnable 接口的是 MockRequestExecutor 类，用来返回静态消息，如果想动态处理请求，需要自己实现 RequestExecutor 接口，框架后续看情况也可能会增加动态处理请求的功能。
MockRequestExecutor 中主要采用责任链模式处理请求，每一个 Handler 都是一个处理环节。这个链式的实现是之前看过 OkHttp 代码学到的。这里贴代码来看一下。
``` java
HandlerChain chain = new RealHandlerChain();

public Response execute(Request request) {
    for (Handler h : userHandlers) {
        chain.addHandler(h);
    }

    if (initHandler) {
        chain.addHandler(new BlockHandler());
        chain.addHandler(new VerifyParamHandler());
        chain.addHandler(new VerifyHeaderHandler());
        chain.addHandler(new LogHandler());
        chain.addHandler(new ConnectionHandler());
    }

    return chain.start(request);
}
```

上面是 MockRequestExecutor 的 execute() 函数的实现，首先将 Handler 加入到 chain 中，之后调用 chain.start() 开始调用 Handler 进行处理。
再看一下 HandlerChain 的 start() 方法实现。
``` java
public Response start(Request request) {
    Handler handler = handlers.get(index);
    Response response = handler.handle(request, this, index);
    return response;
}
```
直接调用了 handler.handle()进行处理，在 Handler 的 handle 方法中需要调用下一个 Handler。
框架默认实现了几个 Handler：BlockHandler，VerifyParamHandler，VeriffyHeaderHandler，LogHandler，ConnectionHandler。
这些通过名字可以看出功能，这里重点看一下 ConnectionHandler。

ConnectionHandler 是处理链调用的终点，负责生成 Response。
ConnectionHandler 的 handle 方法如下：
``` java
String method = request.getMethod();
String url = request.getRequestUri();
MockConnection connection =
    ConnectionStore.getInstance().getConnection(method, url);
if (connection == null) {
    return Response.create404Response();
}
Response response = new Response();
// 省略对 Response 的设置
return response;
```
通过请求的 url 和 method 从 ConnectionStore 中获取到 MockConnection，之后用 MockConnection 中的响应信息创建并填充 Response。
那 ConnectionStore 中的 MockConnection 是什么时候放进去的呢？就是在我们开始通过 MockNet.addConnection() 添加的。

这样整个框架就差不多讲完了，具体的细节就请查看[代码](https://github.com/5A59/MockNet)吧。感觉整个框架实现不难，模块化和扩展性都还不错(自夸一下)。

### 其他
如果有什么问题或者想法可以上[github](https://github.com/5A59/MockNet)提 issue，或者可以邮箱交流 zy5a59@outlook.com。