package com.winhou.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @Author wiho
 * @Date 2023/5/10 23:23
 * @Description JWT工具类
 * @Since version-1.0
 */
public class JwtHelper {
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;    // token有效期
    private static String tokenSignKey = "123456";      // token签名

    // 根据用户id和用户名生成token
    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                // 分类
                .setSubject("AUTH-USER")
                // 有效时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                // 主体部分
                .claim("userId", userId)
                .claim("username", username)
                // 签名部分
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    // 从token中获取用户id
    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 从token中获取用户名
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "admin");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUsername(token));
    }


}
