# vaptcha-java-sdk
#### Step1.环境准备

- Vaptcha Java SDK 适用于 Java 7 及以上版本。demo使用的是springboot,同时maven使用建议在3以上


- 要使用Vaptcha Java SDK，您需要一个Vaptcha账号、一个验证单元以及一对VID和Key。请在Vaptcha验证管理后台查看。

#### Step2.SDK 获取和安装

- 使用命令从Github获取:

```shell
git clone https://github.com/VAPTCHA/vaptcha-java-sdk.git
```

[github下载地址](https://github.com/VAPTCHA/vaptcha-java-sdk)

 

手动下载获取:

- 引用Vaptcha SDK项目，在你的项目中使用以下代码即可导入SDK:

#### Step3.配置接口

- SDK中包含了三个需要配置的接口，分别是：getChallenge(获取流水号)，validate(二次验证)，downTime(宕机模式提供与前端sdk交互)，需要在站点中提供访问的url。

#### Step4.代码示例

- 初始化Vaptcha及备注

```java
//验证单元ID
String VID = "xxxxxxxxxxxxxxxxxxxxxxxx";
// 验证单元密钥
String KEY = "xxxxxxxxxxxxxxxxxxxxxxxx";
Vaptcha vaptcha = new Vaptcha(VaptchaConfig.VID,VaptchaConfig.KEY);

```

- 获取流水号

```java
//获取流水号
String challenge=vaptcha.getChallenge();
```

- 二次验证

```java
//获取流水号字段
String challenge = Request["challenge"];//这里需要用户自己实现获取参数的代码
//获取token字段
String token = Request["challenge"];//这里需要用户自己实现获取参数的代码
//获取场景字段
String sceneId = Request["sceneId"];//这里需要用户自己实现获取参数的代码
//二次验证获取结果
boolean result = vaptcha.validate(challenge, token, sceneId);
```

- 宕机模式 使用GET请求

```java
//获取数据
string data = Request["data"];//这里需要用户自己实现获取参数的代码
//执行宕机模式交互操作
Response.Write(vaptcha.downTime(data));
```

#### Step5.DEMO

- 详细Demo请在[https://github.com/vaptcha/vaptcha-java-sdk](https://github.com/VAPTCHA/vaptcha-java-sdk)中查看
- Demo使用使用方式：
0. 项目需要maven构建，所以需要先配置maven环境，具体见`http://maven.apache.org/install.html`
1. 进入vaptcha-java-sdk目录执行：`mvn install`
2. 进入demo目录执行：`mvn clean package`
3. 进入demo目录下的target执行：`java -jar demo-1.0-SNAPSHOT.jar`
4. 访问`http://127.0.0.1:4396/index.html`

