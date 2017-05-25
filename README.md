## MockNet使用指南

### 框架介绍
通过 MockNet，不需要服务器相关知识就可以在 Android 本地创建服务器用于接口快速开发和网络测试。通过简单的代码就可以快速模拟后台返回数据，而不需要调整现有的网络接口。

### 效果演示

### 下载

### 使用方法

MockNet 中，对每一个请求及其响应称为一个 Connection，具体对应的类是 MockConnection

#### 1. 初始化
``` java
// 创建 MockNet
MockNet mockNet = MockNet.create();
```

#### 2. 添加对请求的处理

* 通过 MockConnectionFactory 构建一个 MockConnection，createGeneralConnection(String url, String body) 会创建一个处理 GET 请求的 MockConnection，并返回 body，默认 Content-Type 是 "text/plain"。

``` java
MockConnection conn = MockConnectionFactory.getInstance().createGeneralConnection("/*", "general connection");
mockNet.addConnection(conn);
```


* 通过 MockConnectionFactory 构建一个 MockConnection，createGeneralConnection(String method, String url, String body) 会创建一个处理 method 指定的请求的 MockConnection，并返回 body，默认 Content-Type 是 "text/plain"。

``` java
MockConnectio conn = MockConnectionFactory.getInstance().createGeneralConnection(MockConnection.POST, "/*", "general connection");
mockNet.addConnection(conn);
```

* 通过 MockConnectionFactory 构建一个 MockConnection，createGeneralConnection(String method, String url, String body) 会创建一个处理 method 指定的请求的 MockConnection，并返回 body，默认 Content-Type 是 "text/plain"。

``` java
MockConnection conn = MockConnectionFactory.getInstance().createGeneralConnection(MockConnection.POST, "/*", "general connection");
mockNet.addConnection(conn);
```

* 添加返回 json 的请求处理  
通过 MockConnectionFactory 构建一个 MockConnection，createGeneralConnection(String method, String url, String contentType, String body) 会创建一个处理 method 指定的请求的 MockConnection，并返回 body。  

``` java
MockConnection conn = MockConnectionFactory.getInstance().createGeneralConnection(MockConnection.POST, "/*", "text/json", "{'code':'success'}");
mockNet.addConnection(conn);

```

* 自己构建 MockConnection  
通过 MockConnection.Builder 来创建 MockConnection，添加自定义的返回信息

``` java
MockConnection conn = new MockConnection.Builder()
        .setMethod(MockConnection.GET)
        .setUrl("/test")
        .setResponseBody("text/json", "first test")
        .addResponseHeader("Content-Length", "" + "first test".length())
        .addRequestHeader("Content-Length", "" + con.length)
        .setVerifyHeaders(true)
        .build();
mockNet.addConnection(conn);

// 或者直接传入 builder
MockConnection.Builder builder = new MockConnection.Builder()
        .setMethod(MockConnection.GET)
        .setUrl("/test")
        .setResponseBody("text/json", "first test")
        .addResponseHeader("Content-Length", "" + "first test".length())
        .addRequestHeader("Content-Length", "" + con.length)
        .setVerifyHeaders(true);

mockNet.addConnection(builder);
```

#### 3. 启动服务

``` java
mockNet.start();
```

#### 4. 关闭服务

``` java
mockNet.stop();
```

#### 5. 链式调用
以上方法可以用链式调用来写，更方便一些
``` java

MockNet mockNet = MockNet.create()
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/*", "general connection"))
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection(MockConnection.POST, "/*", "general connection"))
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection(MockConnection.POST, "/*", "text/json", "{'code':'success'}"))
                .addConnection(new MockConnection.Builder()
                        .setMethod(MockConnection.GET)
                        .setUrl("/test")
                        .setResponseBody("text/json", con, con.length)
                        .addResponseHeader("Content-Length", "" + con.length)
                        .addRequestHeader("Content-Length", "" + con.length)
                        .setVerifyHeaders(true)
                )
                .start();
```

### 进阶使用

#### 1. 自定义 MockConnection 的使用
通过 MockConnection.Builder 生成 Builder，并通过 Builder 相关方法构建 MockConnection。Builder 常用的方法有：

* setUrl(String url)  
设置请求 url

* setMethod(String method)  
设置请求方法，参数选择有 MockConnection.GET，MockConnection.POST等

* setHttpVersion(String version)  
设置 http version

* setResponseStatusCode(int code)  
设置响应状态码 例子: 200

* setResponseReasonPhrase(String phrase)  
设置响应原因 例: OK

* setVerifyHeaders(boolean verify)  
设置是否验证请求头

* setRequestHeaders(Map<String, String> headers, boolean verifyHeaders)  
设置需要验证的请求头，verifyHeaders 为 true 时会开启请求头验证

* addRequestHeader(String name, String val)  
添加需要验证的请求头

* setVerifyParams(boolean verify)  
设置是否验证请求参数

* setRequestParams(Map<String, String> params, boolean verifyParams) 
设置需要验证的请求参数，verifyParams 为 true 时，会开启请求参数验证

* addRequestParam(String name, String val)  
添加需要验证的请求参数

* setResponseHeaders(Map<String, String> headers)  
设置响应头

* addResponseHeader(String name, String val)  
添加响应头

* addResponseHeader(Map<String, String> headers)  
添加响应头


* setResponseParams(Map<String, String> params)  
设置响应参数，以键值对的方式放在 body 中

* setResponseBody(String contentType, byte[] body, int len)  
设置响应 body

* setResponseBody(String contentType, String body)  
设置响应 body

* setResponseBody(String contentType, File file)  
设置响应 body

* setBlockTime(int time)  
设置后台阻塞时间，可用于测试请求超时等情况

* isLog(boolean log)  
设置是否输出 log

#### 2. 随机对请求做出响应
MockNet 内部通过 url 和 method(GET，POST等)来对请求做出区分，在 addConnection 时如果添加了相同的 url 和 method 的 MockConnection，会通过 IConnectionSelector 来选择其中之一进行返回，默认使用 RandomSelector 随机返回。
可以通过 实现 IConnectionSelector 接口并通过 MockNet.setSelector() 设置返回规则

#### 3. Log 设置
默认对每个 MockConnection 都会输出 Log 以帮助调试，如果想关闭 Log，可以在构建 MockConnection 时设置 isLog(false)

MockNet 的 Log 输出是由 Logger 和 Printer 完成的，默认设置了 AndroidPrinter 和 JavaPrinter，如果想自定义 Log，可以实现 Printer 接口，并调用 Logger.init(yourPrinter) 设置，但是要在调用了 MockConnection.create() 之后，否则设置会被覆盖为默认设置

#### 4. 自定义 Handler 对请求响应进行处理
MockNet 中对 Request 和 Response 的处理采用了责任链模式(具体可查看整体架构中的介绍)，通过添加 Handler 增加处理环节，框架自带的 Handler 有 BlockHandler，LogHandler，VerifyHeaderHandler，VerifyParamHandler，ConnectionHandler。
如果想增加自己的处理环节，请实现 Handler 接口，并通过 MockNet.addHandler(Handler h) 来设置。具体实现方法可参照框架默认实现的 Handler 代码。

#### 5. 支持 https
支持 https 可以通过下面代码来开启:
``` java
MockNet mockNet = MockNet.create();
mockNet.start(ServerSocketFactory.createHttpsServerSocket(int port, String jksPath, String storePwd));
```

#### 6. 动态处理数据
为了简化使用方法和加快开发速度，默认只支持返回静态数据，暂时没有对数据进行动态处理。如果想动态处理请求数据，可以继承 RequestExecutor 接口并实现 execute 方法。在 execute 方法中对请求进行动态处理，并创建 Reponse 返回。
之后通过 Server 构造函数构造 Server 对象并传入实现 RequestExecutor 接口的类对象。
具体可以参考 MockRequestExecutor 的实现以及 Server 构造函数。

### 整体架构简析 
![架构图](./proDocs/mocknet.png)
整体架构可以分为两层，server 层和 application 层。
server 层主要做的是 socket 通信以及 Request 和 Response 的解析，解析结果会传入 RequestExecutor 中做处理。
application 层主要是对 request 的处理和对 response 的生成，MockRequestExecutor 实现了 RequestExecutor 接口，其中主要通过责任链模式来对 Request 处理。

### 其他
有关项目 bug 反馈或者希望项目增加什么功能或者单纯想和我交流，欢迎邮件交流或者添加 issue。
邮箱: zy5a59@outlook.com