package com.maj.mall.util;

import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * jwt工具类
 * @param token
 * @param key
 * @param salt
 * @return
 */
public class JwtUtil {

	/**
	 * 编码器
	 * @param key
	 * @param param
	 * @param salt
	 * @return
	 */
	public static String encode(String key, Map<String, Object> param, String salt) {
		if (salt != null) {
			key += salt;
		}
		JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);

		jwtBuilder = jwtBuilder.setClaims(param);

		String token = jwtBuilder.compact();
		return token;

	}

	/**
	 * 解码器
	 * @param token
	 * @param key
	 * @param salt
	 * @return
	 */
	public static Map<String, Object> decode(String token, String key, String salt) {
		Claims claims = null;
		if (salt != null) {
			key += salt;
		}
		try {
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		} catch (JwtException e) {
			return null;
		}
		return claims;
	}
}