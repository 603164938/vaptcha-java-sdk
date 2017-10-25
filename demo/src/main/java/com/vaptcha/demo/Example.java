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

    /**
     * 获取用户的请求
     * @param entity(challenge,token)
     * 这里前端是通过formdata的数据发送的，所以接受参数的时候可以不用注解
     * 如果是payload里面需要用@requestBody的方式接收，写过springmvc都知道吧
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
