package org.tutorial.tutorial_platform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tutorial.tutorial_platform.pojo.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil - JWT工具类
 *
 * 提供JWT token的生成、解析和验证功能，包括：
 * - 生成token：基于用户信息创建JWT令牌
 * - 验证token：检查token的有效性和完整性
 * - 解析token：从token中提取用户信息
 *
 * 核心功能：
 * - token生成：使用用户ID、用户名和用户类型生成token
 * - token验证：验证token的签名和过期时间
 * - 信息提取：从token中获取用户信息
 *
 * 元信息：
 * @author zxf
 */
@Component  // 标注组件，可注入其他类
public class JwtUtil {
    @Value("${jwt.secret}")  // 注入application.properties的密钥
    private String secret;

    @Value("${jwt.expiration}")  // 注入application.properties的过期时间
    private Long expiration;

    /**
     * 生成JWT密钥
     * @return Key 用于签名和验证的密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);  // 使用UTF-8编码转换为字节数组
        return Keys.hmacShaKeyFor(keyBytes);  // 生成签名和验证的密钥
    }

    /**
     * 从token中提取用户名
     * @param token JWT令牌
     * @return String 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从token中提取过期时间
     * @param token JWT令牌
     * @return Date 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从token中提取指定信息
     * @param token JWT令牌
     * @param claimsResolver 信息提取函数，接受claim，返回T
     * @return T 提取的信息
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);  // 相当于claims.claimsResolver() / claimResolver(claims)
    }

    /**
     * 从token中提取所有信息
     * @param token JWT令牌
     * @return Claims 所有信息
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // 配置一个JWT解析器
                .setSigningKey(getSigningKey())  // 设置用于验证JWT签名的密钥。
                .build()  // 生成JwtParser实例对象
                .parseClaimsJws(token)  // 解析带签名的jwt，不正确则会抛出异常！
                .getBody();  // 返回payload部分
    }

    /**
     * 检查token是否过期
     * @param token JWT令牌
     * @return boolean 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 为用户生成token
     * @param user 用户信息
     * @return String JWT令牌
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("userType", user.getUserType().name());
        return createToken(claims, user.getUsername());
    }

    /**
     * 创建token
     * @param claims 要包含的信息（自定义），这里包括了userId和userType
     * @param subject 主题（用户名）
     * @return String JWT令牌
     * 注：其中令牌包括三部分内容：
     * Header: 包含算法和令牌类型：如:算法"alg":"HS256"，令牌类型"typ":"JWT"
     * Payload: 有效载荷，存储 JWT 的声明（claims），包括标准声明（如 sub, iat, exp）和自定义声明（如 userId）。
     * Signature: 依赖密钥的签名，生成过程是对Header和Payload拼接编码用密钥计算HMAC哈希，最后再进行Base64URL编码签名。
     * 因此，当服务端拿到JWT时，可以计算签名来验证令牌有效性。
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)  // 设置自定义信息
                .setSubject(subject)  // 主题
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))  // 设置有效期截止时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 使用 HMAC-SHA256算法对JWT进行签名。
                .compact();  // 生成的 JWT 格式：xxxxx.yyyyy.zzzzz（header.payload.signature）。
    }

    /**
     * 验证token
     * @param token JWT令牌
     * @param username 用户名
     * @return boolean 是否有效
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
} 