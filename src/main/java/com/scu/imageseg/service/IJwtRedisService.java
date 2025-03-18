package com.scu.imageseg.service;

import com.scu.imageseg.entity.CustomUser;

/**
 * <p>
 *  Jwt服务类 主要负责Jwt Token 在Redis数据库中的生成和校验
 *  （由于JwtFilter在经过请求Controller层前调用静态方法实现Jwt初步校验的过程无法同时进行对Redis数据库的操作，因此需要对经过过滤器的token进行进一步的Redis校验。）
 *  将Jwt Token存储到Redis数据库是为了记录用户登录状态，实现退出登录、修改密码需要重新登录等功能
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */
public interface IJwtRedisService {
    String generateRedisJwtToken(CustomUser customUser); // 生成Jwt Token并存储到Redis数据库
    Boolean validateRedisJwtToken(String id, String token); // 验证Redis数据库中是否存在id token对
    Boolean deleteRedisJwtToken(String id); // 删除Redis数据库中对应用户的JwtToken 以满足修改密码 退出登录功能

}
