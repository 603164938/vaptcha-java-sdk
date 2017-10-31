package com.vaptcha;

public class VaptchaConfig {
    /// <summary>
    /// 验证单元ID
    /// </summary>
    public static final String VID = "59f81616a485d5214ce5901e";
    /// <summary>
    /// 验证单元密钥
    /// </summary>
    public static final String KEY = "e3ecac73e8dd4acfbb23b4e07e791a94";
    /// <summary>
    /// SDK版本号
    /// </summary>
    public static final String Version = "1.0.0";
    /// <summary>
    /// SDK语言
    /// </summary>
    public static final String SdkLang = "java";
    /// <summary>
    /// VaptchaAPI Url
    /// </summary>
    public static final String ApiUrl = "http://api.vaptcha.com";
    /// <summary>
    /// 获取流水号 Url
    /// </summary>
    public static final String GetChallengeUrl = "/challenge";
    /// <summary>
    /// 验证 Url
    /// </summary>
    public static final String ValidateUrl = "/validate";
    /// <summary>
    /// 验证数量使用完
    /// </summary>
    public static final String RequestUsedUp = "0209";
    /// <summary>
    /// 宕机模式检验恢复时间185000ms
    /// </summary>
    public static final long DownTimeCheckTime = 185000;
    /// <summary>
    /// 宕机模式二次验证失效时间十分钟
    /// </summary>
    public static final long ValidatePassTime = 600000;
    /// <summary>
    /// 宕机模式请求失效的时间25秒
    /// </summary>
    public static final long RequestAbateTime = 250000;
    /// <summary>
    /// 宕机模式验证等待时间2秒
    /// </summary>
    public static final long ValidateWaitTime = 2000;
    /// <summary>
    /// 宕机模式保存通过数量最大值50000
    /// </summary>
    public static final int MaxLength = 50000;
    /// <summary>
    /// 验证图的后缀png
    /// </summary>
    public static final String PicPostfix = ".png";
    /// <summary>
    /// 宕机模式key路径
    /// </summary>
    public static final String PublicKeyPath = "http://down.vaptcha.com/publickey";
    /// <summary>
    /// 是否宕机路径
    /// </summary>
    public static final String IsDownPath = "http://down.vaptcha.com/isdown";
    /// <summary>
    /// 宕机模式图片路径
    /// </summary>
    public static final String DownTimePath = "downtime/";
}
