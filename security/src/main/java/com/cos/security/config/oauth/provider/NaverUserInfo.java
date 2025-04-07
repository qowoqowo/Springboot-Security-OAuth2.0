package com.cos.security.config.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

	// {id=pis6TPIqh2vx-7YluTNkQDRU5J1hE_eVOKMs5RldIO0, email=dnlem55@naver.com, name=배재현}
	private Map<String, Object> attributes; // oauth2User.getAttributes()
	
	public NaverUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProviderId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

}
