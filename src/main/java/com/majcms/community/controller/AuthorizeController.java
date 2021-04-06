package com.majcms.community.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.majcms.community.dto.AccessTokenDTO;
import com.majcms.community.dto.GithubUser;
import com.majcms.community.provider.GithubProvider;

@Controller
public class AuthorizeController {
	
	@Autowired
	private GithubProvider githubProvider;
	
	@Value("${github.client.id}")
	private String clientid;
	
	@Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;
	
	@GetMapping("/callback")
	public String callback(@RequestParam(name="code") String code,
			               @RequestParam(name="state") String state,
			               HttpServletRequest request) {
	    AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
	    accessTokenDTO.setClient_id(clientid);
	    accessTokenDTO.setCode(code);
	    accessTokenDTO.setRedirect_uri(redirectUri);
	    accessTokenDTO.setState(state);
	    accessTokenDTO.setClient_secret(clientSecret);
	    	    
		String accessToken = githubProvider.getAccessToken(accessTokenDTO);
	    GithubUser githubUser = githubProvider.getUser(accessToken);
	    
	    if (githubUser != null && githubUser.getId() != null) {
            //登录成功
	    	request.getSession().setAttribute("user", githubUser);
            return "redirect:/"; //跳转到/
        } else {
            //log.error("callback get github error,{}", githubUser);
            // 登录失败，重新登录
            return "redirect:/"; //跳转到/
        }
	}
	
	@GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
