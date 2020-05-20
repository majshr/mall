package com.maj.mall.passport.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.maj.mall.bean.UmsMember;
import com.maj.mall.passport.emun.UserGender;
import com.maj.mall.passport.emun.UserSourceType;
import com.maj.mall.service.CartService;
import com.maj.mall.service.UserService;
import com.maj.mall.util.HttpclientUtil;
import com.maj.mall.util.JwtUtil;

@Controller
@CrossOrigin
public class PassportController {

    public static org.slf4j.Logger LOG = LoggerFactory.getLogger(PassportController.class);

    public static String VB_AUTH_URL = "oauth2/access_token";
    public static String VB_USER_URL = "2/users/show.json";

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Value("${config.jwt.key}")
    private String JWT_KEY = "";

    @Value("${config.vb.mallapp.key}")
    private String vbAppkey = "";

    @Value("${config.vb.mallapp.secret}")
    private String vbAppSecret = "";

    @Value("${config.vb.url}")
    private String vbUrl = "";

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token;

        // 调用用户服务验证用户名和密码
        UmsMember umsMemberLogin = userService.login(umsMember);

        if (umsMemberLogin != null) {
            token = generateJwtToken(umsMemberLogin, request);

            // 将token存入redis一份
            userService.addUserToken(token, String.valueOf(umsMemberLogin.getId()));

        } else {
            // 登录失败
            token = "fail";
        }

        return token;
    }

    /**
     * 生成token
     * 
     * @param umsMember
     * @param request
     * @return String
     * @date: 2019年12月16日 下午2:39:10
     */
    private String generateJwtToken(UmsMember umsMember, HttpServletRequest request) {

        // 如果nginx转发请求，remoteAddr为nginx的ip
        // x-forwarded-for 为nginx转发的之前请求的ip
        String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();// 从request中获取ip
        }
        if (StringUtils.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        return generateJwtToken(umsMember, ip);
    }

    /**
     * 生成token
     * 
     * @param umsMember
     * @param ip
     * @return String
     * @date: 2019年12月16日 下午2:39:36
     */
    private String generateJwtToken(UmsMember umsMember, String ip) {
        // 登录成功
        // 用jwt制作token
        Long memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);
        userMap.put("nickname", nickname);

        // 盐值salt要经过加密之后，再调用jwt工具
        // 按照设计的算法对参数进行加密后，生成token
        return JwtUtil.encode(JWT_KEY, userMap, ip);
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);

        return "index";
    }

    @RequestMapping("verify")
	@ResponseBody
	public String verify(String token, String currentIp, HttpServletRequest request) {
		// 通过jwt校验token真假
		Map<String, String> map = new HashMap<String, String>();
        Map<String, Object> decode = JwtUtil.decode(token, JWT_KEY, currentIp);

        // 验证成功，设置用户信息，返回
		if (decode != null) {
			map.put("status", "success");
            map.put("memberId", String.valueOf(Optional.ofNullable(decode.get("memberId")).orElse("")));
            map.put("nickname", String.valueOf(Optional.ofNullable(decode.get("nickname")).orElse("")));
		} else {
			map.put("status", "fail");
		}

		return JSON.toJSONString(map);
	}

    /**
     * 微博回调地址（用户在微博页面授权登录后，回调此地址，并返回用户通过认证的code）
     * 
     * @param code
     * @return String
     * @date: 2019年12月16日 上午11:00:12
     */
    @RequestMapping("vlogin")
    public String vLogin(String code, HttpServletRequest request) {
        // 1, 微博返回code

        // 2, 根据code向微博请求访问access_token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", vbAppkey);
        params.put("client_secret", vbAppSecret);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", "http://passport.gmall.com:7085/vlogin");
        params.put("code", code);
        // 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String tokenResJsonStr = HttpclientUtil.doPost(vbUrl + VB_AUTH_URL, params);
        
        if (StringUtils.isBlank(tokenResJsonStr)) {

            return "";
        }

        // 3, 获取token，之后便可以根据微博token访问微博信息
        Map<String, String> tokenResMap = JSON.parseObject(tokenResJsonStr, Map.class);
        String accessToken = tokenResMap.get("access_token");
        String uid = tokenResMap.get("uid");

        // 4, access_token获取微博用户信息，从微博获取用户信息，来源设置为微博用户
        String userJsonStr = HttpclientUtil.doGet(vbUrl + VB_USER_URL + "?access_token=" + accessToken + "&uid=" + uid);
        Map<String, String> userMap = JSON.parseObject(userJsonStr, Map.class);
        // 将用户信息保存数据库，用户类型设置为微博用户
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType(UserSourceType.vb.getSourceType());
        umsMember.setAccessCode(code);
        umsMember.setAccessToken(accessToken);
        umsMember.setSourceUid((String) userMap.get("idstr"));
        umsMember.setCity((String) userMap.get("location"));
        umsMember.setNickname((String) userMap.get("screen_name"));
        int g = UserGender.woman.getType();
        String gender = (String) userMap.get("gender");
        if ("m".equals(gender)) {
            g = UserGender.man.getType();
        }
        umsMember.setGender(g);

        // 判断数据库是否已经存在此用户。如果已经存在，使用数据库的；没有存在保存
        UmsMember umsCheck = new UmsMember();
        umsCheck.setSourceUid(umsMember.getSourceUid());
        umsCheck.setSourceType(UserSourceType.vb.getSourceType());
        UmsMember umsMemberCheck = userService.checkOauthUser(umsCheck);

        if (umsMemberCheck == null) {
            umsMember = userService.addOauthUser(umsMember);
        } else {
            umsMember = umsMemberCheck;
        }

        // 5, 生成 jwt token，访问
        String token = generateJwtToken(umsMember, request);

        // 将token存入redis一份
        userService.addUserToken(token, String.valueOf(umsMember.getId()));

        return "redirect:http://search.gmall.com:8083/index?token=" + token;
    }
}
