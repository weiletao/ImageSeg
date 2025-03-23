package com.scu.imageseg.controller;


import com.scu.imageseg.entity.CustomUser;
import com.scu.imageseg.entity.JSONResult;
import com.scu.imageseg.entity.LoginForm;
import com.scu.imageseg.entity.PasswordData;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.ICustomUserService;
import com.scu.imageseg.service.IJwtRedisService;
import com.scu.imageseg.service.IVerifyCodeService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            if(!iCustomUserService.save(customUser)) throw new Exception();
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

    /**
     * 退出登录接口 实现用户退出登录功能 删除其在Redis存储的Jwt
     */
    @GetMapping("/jwtFilter/exit")
    public JSONResult<Object> exit(HttpServletRequest httpServletRequest){
        /** 从经过JwtFilter拦截的request中获取 id*/
        String id = String.valueOf((Integer) httpServletRequest.getAttribute("id"));
        if(iJwtRedisService.deleteRedisJwtToken(id)){
            return new JSONResult<>(200, "退出成功！");
        }else {
            throw new ServiceException(208, "退出失败！");
        }
    }

    /**
     * 获取当前用户信息接口 实现个人信息的获取
     */
    @GetMapping("/jwtFilter/profile")
    public JSONResult<CustomUser> getUserProfile(HttpServletRequest request){
        try{
            return new JSONResult<>(iCustomUserService.getUserProfile((String)request.getAttribute("username"))); // todo 此处是使用用户名做检索 最好使用用户id做检索 方便重用
        }catch (Exception e){
            throw new ServiceException(209, "获取用户信息失败！");
        }
    }

    /**
     * 修改用户信息接口 实现用户信息的修改
     */
    @PutMapping("/jwtFilter/profile/update")
    public JSONResult<Object> updateUserProfile(HttpServletRequest request, CustomUser userData){
        try{
            return new JSONResult<>(iCustomUserService.updateUserProfile((Integer) request.getAttribute("id"), userData));
        }catch (Exception e){
            throw new ServiceException(210, "修改用户信息失败！");
        }
    }

    /**
     * 修改密码接口 实现用户密码的修改 直接调用用户接口服务的两个方法实现 不需要进一步封装
     * 可能抛出3个异常
     * 205，用户不存在ps
     * 204，用户名或密码错误
     * 211，修改密码失败
     */
    @PutMapping("jwtFilter/profile/pswUpdate")
    public JSONResult<Object> updatePassword(HttpServletRequest request, PasswordData passwordData){
        /** 验证用户名和密码是否正确 */
        CustomUser customUser = new CustomUser();
        customUser.setUsername((String) request.getAttribute("username")); // 设置用户名
        customUser.setPassword(passwordData.getCurrentPassword()); // 设置未加密的密码
        customUser = iCustomUserService.verifyUsernamePassword(customUser); // 验证后返回原数据库实体类
        if (customUser == null){
            log.error("用户不存在！");
            throw new ServiceException(205, "用户不存在！"); // 用户名或密码错误的异常抛出封装在iCustomUserService中，说明原密码不对
        }
        /** 原密码正确后修改用户密码 */
        customUser.setPassword(new BCryptPasswordEncoder().encode(passwordData.getNewPassword())); // 修改原实体类密码为加密后的新密码
        log.info("开始修改密码...");
        try{
            iCustomUserService.updateUserProfile((Integer) request.getAttribute("id"), customUser);
        }catch (Exception e){
            log.info("修改密码失败！");
            throw new ServiceException(211, "修改密码失败！");
        }
        log.info("修改密码成功！");
        return new JSONResult<>(200, "修改密码成功！");
    }

    /**
     * 查询医生列表接口 实现新建诊断申请界面可选医生列表功能
     * 可能抛出2个异常
     * 212，用户名不合法
     * 213，查询医生列表失败
     */
    @GetMapping("jwtFilter/doctors")
    public JSONResult<Object> getDoctorsList(){
        /** 获取医生用户列表 */
        List<CustomUser> doctorsList;
        try {
            doctorsList = iCustomUserService.getUsersListByRole("doctor");
            log.info("获取医生列表成功！");
        }catch (Exception e){
            log.info("查询医生列表失败：" + e.getMessage());
            throw new ServiceException(213, "查询医生列表失败！");
        }
        /** 构造返回List<map> */
        List<Map<String,Object>> re = new ArrayList<>();
        for(CustomUser doctor: doctorsList){
            Map<String, Object> map = new HashMap<>();
            map.put("fullName", doctor.getFullName());
            map.put("email", doctor.getEmail());
            map.put("bio", doctor.getBio());
            re.add(map);
        }
        return new JSONResult<>(re);
    }


}
