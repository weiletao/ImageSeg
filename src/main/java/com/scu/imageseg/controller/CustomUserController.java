package com.scu.imageseg.controller;


import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.entity.JSONResult;
import com.scu.imageseg.entity.LoginForm;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.ICustomUserService;
import com.scu.imageseg.service.IJwtRedisService;
import com.scu.imageseg.service.IVerifyCodeService;
import com.scu.imageseg.utils.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
@Slf4j
@RestController
public class CustomUserController {


    @Autowired
    private ICustomUserService iCustomUserService;

    @Autowired
    private IVerifyCodeService iVerifyCodeService;

    @Autowired
    private IJwtRedisService iJwtRedisService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public JSONResult<Object> userRegister(CustomUser customUser){ // @RequestBody不支持前端请求为multipart/form-data，可以直接用类接收
        log.info("前端返回：" + customUser);
        /** 密码加密及相关信息补充 */
        customUser.setPassword(new BCryptPasswordEncoder().encode(customUser.getPassword())); // 密码加密
        customUser.setIsActive(true); // 修改激活状态
        customUser.setIsStaff(false); // 是否为管理员（默认普通用户）
        customUser.setIsSuperuser(false); // 是否为超级管理员
        customUser.setDateJoined(LocalDateTime.now()); //LocalDateTime 是Java 8中引入的一个日期时间类，用于表示不带时区的日期和时间。其new方法为调用类的静态方法 now()
        log.info("最终信息：" + customUser);
        /** 尝试写入数据库 */
        log.info("-----开始写入数据库....-------");
        try{
            iCustomUserService.save(customUser);
        }catch (Exception e){
            // todo 可补充对不同情况（如：用户已存在）的判断
            log.error("写入数据库失败！异常信息：" + e.getMessage());
            throw new ServiceException(201, "注册失败！");
        }
        log.info("写入数据库成功！");
        return new JSONResult<>(200, "注册成功！");
    }

    /**
     * 验证码生成接口
     */
    @GetMapping("/verifycode")
    public void generateVerifyCode(HttpServletRequest request, HttpServletResponse response) {
        /** 生成验证码 */
        String sessionId = request.getSession().getId(); // 获取sessionId
        BufferedImage verifyPicture = iVerifyCodeService.generateVerifyCode(sessionId); // 生成二维码
        log.info("验证码生成成功！SessionId:{}", sessionId);

        /** 输出验证码 */
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(verifyPicture,"png",os);
        } catch (IOException e) {
            iVerifyCodeService.deleteVerifyCode(sessionId); // 销毁验证码
            throw new ServiceException(202, "验证码输出失败");
        }

    }

    /**
     * 登录接口
     * 接口主要实现用户登录功能，同时在登录成功后调用JwtUtil 生成Jwt Token，存储到redis数据库中，用于记录用户登录状态以及访问权限
     */
    @PostMapping("/login")
    public JSONResult<Object> login(@RequestBody LoginForm loginForm, HttpServletRequest request){
        String sessionId = request.getSession().getId(); // 获取sessionId
        log.info("成功获取sessionId{}", sessionId);
        /** 进行验证码验证 */
        if(!iVerifyCodeService.validateVerifyCode(sessionId, loginForm.getVerifycode())){ // service里如果验证成功会自动销毁验证码
            log.error("验证码输入错误！");
            throw new ServiceException(203, "验证码错误！");
        }

        /** 进行账户密码认证 */
        CustomUser customUser = new CustomUser();
        customUser.setUsername(loginForm.getUsername()); // 为实体类赋值用户名
        customUser.setPassword(loginForm.getPassword()); // todo 为实体类赋值同样加密后的密码的方法不可行，与MD5加密不同，Bcrypt对密码加密方式具有随机性，所以需要调用该类的match方法进行比较
        customUser = iCustomUserService.verifyUsernamePassword(customUser);
        if(customUser == null){
            log.error("用户不存在！");
            throw new ServiceException(205, "用户不存在！"); // 用户名或密码错误的异常抛出封装在iCustomUserService中
        }

        /** 生成Jwt Token 同时存储到Redis数据库中*/
        String token = iJwtRedisService.generateRedisJwtToken(customUser);
        if(token == null){
            log.error("生成JwtRedisToken失败！");
            throw new ServiceException(206, "生成JwtToken服务异常！");
        }
        Map<String, String> re = new HashMap<>();
        re.put("token", token);
        re.put("role", customUser.getRole());
        re.put("username", customUser.getUsername());
        return new JSONResult<>(re, "登录成功！", 200);

    }

    /**
     * 测试JwtFilter接口 看看能否正确地过滤错误的Jwt和没有存在Redis中的Jwt
     */
    @GetMapping("/jwtFilter/test")
    public String testJwt(){
        return "sccusses";
    }

}
