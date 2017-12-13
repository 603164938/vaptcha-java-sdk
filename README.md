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
 ```java
 package com.vaptcha;
 ```

#### Step3.DEMO
> demo的vid和key由vaptcha官方免费提供，只可在localhost:4396下使用，缺少一些限制，可能存在安全隐患。在实际生产环境中，我们建议你登陆vaptcha管理后台，在验证管理中添加对应的验证单元，并把domain参数设置为实际环境中的域名。
- 详细Demo请在[https://github.com/vaptcha/vaptcha-java-sdk](https://github.com/VAPTCHA/vaptcha-java-sdk)中查看
- Demo使用使用方式：
0. 项目需要maven构建，所以需要先配置maven环境，具体见`http://maven.apache.org/install.html`
1. 进入vaptcha-java-sdk目录执行：`mvn install`
2. 进入demo目录执行：`mvn clean package`
3. 进入demo目录下的target执行：`java -jar demo-1.0-SNAPSHOT.jar`
4. 访问`http://127.0.0.1:4396/index.html`

#### Step4.配置接口

- SDK中包含了三个需要配置的接口，分别是：getChallenge(获取流水号)，validate(二次验证)，downTime(宕机模式提供与前端sdk交互)，需要在站点中提供访问的url。

#### Step5.代码示例

- 初始化Vaptcha及备注

```java
//验证单元ID
String VID = "xxxxxxxxxxxxxxxxxxxxxxxx";
// 验证单元密钥
String KEY = "xxxxxxxxxxxxxxxxxxxxxxxx";
Vaptcha vaptcha = new Vaptcha(VaptchaConfig.VID,VaptchaConfig.KEY);

```
SDK提供以下三个接口：

- 获取流水号接口 `getChallenge(sceneId)` ，返回`json`字符串

  参数说明:

  `sceneId`： 选填，场景id，请在vaptcha管理后台查看
 
- 二次验证接口 `validate(challenge, token[, sceneId])`，返回`Boolen`值

  参数说明: 

  `challenge`： 必填，客户端验证通过后返回的流水号

  `token`： 必填， 客户端验证通过后返回的令牌

  `sceneId`： 选填，场景id，与`getChallenge(sceneId)`的场景id保持一致


- 宕机模式接口 `downTime(data)`，返回`json`字符串

  参数说明:

  `data`： GET请求返回的数据，`Request["data"]`;

