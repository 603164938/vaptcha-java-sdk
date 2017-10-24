package com.vaptcha.demo;

import com.vaptcha.Vaptcha;
import com.vaptcha.VaptchaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@SpringBootApplication
@RestController//返回json格式数据
public class Example {
    private Vaptcha vaptcha = new Vaptcha(VaptchaConfig.VID,VaptchaConfig.KEY);
    @RequestMapping("/home")
    public String home() {
        return "hello Spring boot";
    }

    @RequestMapping("/getVaptcha")
    public String getVaptcha(){
        String challenge = vaptcha.getChallenge(null);
        return challenge;
    }

    @RequestMapping("/getDownTime")
    public String getDownTime(String data){
        String dowTime = vaptcha.downTime(data);
        return dowTime;
    }

    @RequestMapping("/validate")
    public String validate(String challenge, String token, String sceneId){
        vaptcha.validate(challenge,token,sceneId);
        return "true";
    }

    /**
     * 获取用户的请求
     * @param entity(challenge,token)
     * @return
     */
    @RequestMapping("/login")
    public String login(Entity entity){
        Boolean status = vaptcha.validate(entity.getChallenge(),entity.getToken(),null);
        if(status){
            return "success";
        }else {
            return "faild";
        }

    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Example.class, args);
    }
}
