package com.vaptcha;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public  class Vaptcha {


    /// <summary>
    /// 宕机模式公钥
    /// </summary>
    private String publicKey;
    private long lastCheckDownTime = 0;
    private boolean isDown = false;
    private final static String AGLORITHM_NAME = "HmacSHA1";
    /// <summary>
    /// VID
    /// </summary>
    private String vid;
    /// <summary>
    /// KEY
    /// </summary>
    private String key;
    /// <summary>
    /// 宕机模式已通过标识
    /// </summary>
    private static List<String> passedSignatures;

    /// <summary>
    /// 初始化VaptchaSDK
    /// </summary>
    /// <param name="id">验证单元VID</param>
    /// <param name="key">验证单元密钥</param>
    public Vaptcha(String vid, String key) {
        this.vid = vid;
        this.key = key;
    }

    /// <summary>
    /// 获取流水号
    /// </summary>
    /// <param name="sceneId">场景id</param>
    /// <returns></returns>
    public String getChallenge(String sceneId) {
        if (sceneId == null) {
            sceneId = "";
        }
        String url = String.format("%s%s", VaptchaConfig.ApiUrl, VaptchaConfig.GetChallengeUrl);
        long now = System.currentTimeMillis();//ToUnixTime(DateTime.Now);
        String query = String.format("id=%s&scene=%s&time=%s&version=%s&sdklang=%s", vid, sceneId, now, VaptchaConfig.Version, VaptchaConfig.SdkLang);
        String signature = HMACSHA1(key, query);
        query += "&signature=" + signature;
        if (!isDown) {
            String challenge = readContentFromGet(url + "?" + query);
            if (challenge.equals(VaptchaConfig.RequestUsedUp)) {
                //进入宕机模式
                lastCheckDownTime = now;
                isDown = true;
                passedSignatures = new ArrayList<String>();
                return getDownTimeCaptcha();
            }
            if (stringIsEmpty(challenge)) {
                //判断宕机
                if (getIsDown()) {
                    //进入宕机模式
                    lastCheckDownTime = now;
                    isDown = true;
                    passedSignatures = new ArrayList<String>();
                }
                return getDownTimeCaptcha();
            }
            return "{" + String.format(
                    "\"vid\":\"%s\",\"challenge\":\"%s\"", vid, challenge) + "}";
        } else {
            if (now - lastCheckDownTime > VaptchaConfig.DownTimeCheckTime) {
                lastCheckDownTime = now;
                String challenge = readContentFromGet(url + "?" + query);
                if (!stringIsEmpty(challenge) && !challenge.equals(VaptchaConfig.RequestUsedUp)) {
                    //退出宕机模式
                    isDown = false;
                    if (passedSignatures != null) {
                        passedSignatures.clear();
                    }
                    return "{" + String.format(
                            "\"vid\":\"%s\",\"challenge\":\"%s\"", vid, challenge) + "}";
                }
            }
            return getDownTimeCaptcha();
        }
    }

    /// <summary>
    /// 二次验证
    /// </summary>
    /// <param name="challenge">流水号</param>
    /// <param name="token">标记</param>
    /// <param name="sceneId">场景id</param>
    /// <returns></returns>
    public Boolean validate(String challenge, String token, String sceneId) {
        if (!isDown && !stringIsEmpty(challenge))
            return normalValidate(challenge, token, sceneId);
        else
            return downTimeValidate(token);
    }

    /// <summary>
    /// 宕机模式交互
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    public String downTime(String data) {
        if (stringIsEmpty(data)) {
            return "{\"error\":\"parms error\"}";
        }
        String[] datas = data.split(",");
        switch (datas[0]) {
            case "request": {
                return getDownTimeCaptcha();
            }
            case "getsignature": {
                if (datas.length < 2) {
                    return "{\"error\":\"parms error\"}";
                } else {
                    try {
                        long time = Long.parseLong(datas[1]);
                        return getSignature(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "{\"error\":\"parms error\"}";
                    }
                }
            }
            case "check": {
                if (datas.length < 5) {
                    return "{\"error\":\"parms error\"}";
                } else {
                    try {
                        long time1 = Long.parseLong(datas[1]);
                        long time2 = Long.parseLong(datas[2]);
                        String signature = datas[3];
                        String captcha = datas[4];
                        return downTimeCheck(time1, time2, signature, captcha);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "{\"error\":\"parms error\"}";
                    }
                }
            }
            default:
                return "{\"error\":\"parms error\"}";
        }
    }
    private Boolean getIsDown() {
        String result = readContentFromGet(VaptchaConfig.IsDownPath);
        return result.equalsIgnoreCase("true");
    }

    private String getPublicKey() {
        String result = readContentFromGet(VaptchaConfig.PublicKeyPath);
        return result;
    }

    private String getSignature(long time) {
        long now = System.currentTimeMillis();
        if ((now - time) > VaptchaConfig.RequestAbateTime)
            return null;
        String signature = md5Encode(now + key);
        return "{" + String.format(
                "\"time\":\"%s\",\"signature\":\"%s\"", now, signature) + "}";
    }

    private String downTimeCheck(long time1, long time2, String signature, String captcha) {
        long now = System.currentTimeMillis();
        if ((now - time1) > VaptchaConfig.RequestAbateTime || !signature.equals(md5Encode(time2 + key)))
            return "{" + String.format(
                    "\"result\":\"%s\"", false) + "}";
        if (now - time2 < VaptchaConfig.ValidateWaitTime)
            return "{" + String.format(
                    "\"result\":\"%s\"", false) + "}";
        String trueCaptcha = md5Encode(time1 + key).substring(0, 3);
        if (trueCaptcha.equalsIgnoreCase(captcha))
            return "{" + String.format(
                    "\"result\":true,\"token\":\"%s\"", now + "," + md5Encode(now + key + "vaptcha")) + "}";
        else
            return "{\"result\":false}";
    }

    private Boolean normalValidate(String challenge, String token, String sceneId) {
        if (stringIsEmpty(token) || stringIsEmpty(challenge) || !token.equals(md5Encode(key + "vaptcha" + challenge))) {
            return false;
        }
        if (sceneId == null) {
            sceneId = "";
        }
        String url = String.format("%s%s", VaptchaConfig.ApiUrl, VaptchaConfig.ValidateUrl);
        String query = String.format("id=%s&scene=%s&token=%s&time=%s&version=%s&sdklang=%s", vid, sceneId, token,
                System.currentTimeMillis(), VaptchaConfig.Version, VaptchaConfig.SdkLang);
        String signature = HMACSHA1(key, query);
        String response = "";
        try {
            response = postValidate(url, query + "&signature=" + signature);
        } catch (Exception e) {
            System.out.println(e);
        }
        return "success".equals(response);
    }

    private boolean downTimeValidate(String token) {
        if (stringIsEmpty(token)) {
            return false;
        }
        String[] strs = token.split(",");
        if (strs.length < 2) {
            return false;
        } else {
            long time = Long.parseLong(strs[0]);
            String signature = strs[1];
            long now = System.currentTimeMillis();
            if (now - time > VaptchaConfig.ValidatePassTime)
                return false;
            else {
                String signatureTrue = md5Encode(time + key + "vaptcha");
                if (signatureTrue.equalsIgnoreCase(signature)) {
                    if (passedSignatures.contains(signature))
                        return false;
                    else {
                        passedSignatures.add(signature);
                        if (passedSignatures.size() >= VaptchaConfig.MaxLength) {
                            passedSignatures.subList(0, passedSignatures.size() - VaptchaConfig.MaxLength + 1).clear();
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

    }

    private String getDownTimeCaptcha() {
        long time = System.currentTimeMillis();
        String md5 = md5Encode(time + key);
        String captcha = md5.substring(0, 3);
        String verificationKey = md5.substring(30);
        if (publicKey == null) {
            publicKey = getPublicKey();
        }
        String url = md5Encode(captcha + verificationKey + publicKey) + VaptchaConfig.PicPostfix;
        url = VaptchaConfig.DownTimePath + url;
        return "{" + String.format(
                "\"time\":\"%s\",\"url\":\"%s\"", time, url) + "}";
    }

    private static String readContentFromGet(String url) {
        try {
            HttpRequest request = new HttpRequest(url);
            request.setMethod(MethodType.GET);
            request.setReadTimeout(5 * 1000);//5 * 1000;
            HttpResponse response = HttpResponse.getResponse(request);//var response = (HttpWebResponse)request.GetResponse();
//            var myResponseStream = response.GetResponseStream();

            String strResult = new String(response.getHttpContent(), response.getEncoding());
            if (strResult != null) {
                return strResult;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }

    }

    private static String postValidate(String url, String data) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(MethodType.POST);//        request.Method = "POST";

        // 发送数据
        try {
            request.setHttpContent(data.getBytes("UTF-8"), "UTF-8", FormatType.FORM);//"application/x-www-form-urlencoded"
            HttpResponse response = HttpResponse.getResponse(request);
            String strResult = new String(response.getHttpContent(), response.getEncoding());
            if (strResult != null) {
                return strResult;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String md5Encode(String text) {

//        String token = (ParameterHelper.md5Sum(text.getBytes()));
//        return token.replace("-", "").toLowerCase();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //加密无问题
    private String HMACSHA1(String key, String text) {
        byte[] byteHMAC;
        String byteHMACString = null;
        try {
            Mac mac = Mac.getInstance(AGLORITHM_NAME);
            SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"),
                    AGLORITHM_NAME);

            mac.init(secretKey);
            byteHMAC = mac.doFinal(text.getBytes("utf-8"));
            byteHMACString = Base64Helper.encode(byteHMAC);
            return byteHMACString.replace("/", "").replace("+", "").replace("=", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteHMACString;
    }


//    private static final Date Epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
//    public static long ToUnixTime(Date date) {
//        return Convert.ToInt64((date.ToUniversalTime() - Epoch).TotalMilliseconds);
//    }

    private boolean stringIsEmpty(String str) {
        return str == null && str.length() == 0;
    }
}

