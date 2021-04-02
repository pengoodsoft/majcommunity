package com.majcms.community.provider;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.majcms.community.dto.AccessTokenDTO;
import com.majcms.community.dto.GithubUser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class GithubProvider {
	public String getAccessToken(AccessTokenDTO accesstokenDTO) {
		MediaType mediaType = MediaType.get("application/json; charset=utf-8");
		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accesstokenDTO));
		Request request = new Request.Builder().url("https://github.com/login/oauth/access_token").post(body).build();
		try (Response response = client.newCall(request).execute()) {
			String string = response.body().string();
			String token = string.split("&")[0].split("=")[1];
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			// log.error("getAccessToken error,{}", accessTokenDTO, e);
		}
		return null;
	}

	public GithubUser getUser(String accessToken) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url("https://api.github.com/user")
				.header("Authorization", "token " + accessToken).build();
		try {
			Response response = client.newCall(request).execute();
			String string = response.body().string();
			GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
			return githubUser;
		} catch (Exception e) {
			e.printStackTrace();
			// log.error("getUser error,{}", accessToken, e);
		}
		return null;
	}
}
