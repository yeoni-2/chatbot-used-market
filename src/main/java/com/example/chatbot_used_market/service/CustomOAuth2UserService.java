package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.AuthProvider;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    try {
      return processOAuth2User(userRequest, oauth2User);
    } catch (Exception ex) {
      throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류 발생: " + ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
    // google이나 kakao처럼 어느 oauth2 서비스에 로그인하려 하는지에 대한 서비스 id 값을 가져오는 코드
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    Map<String, Object> attributes = oauth2User.getAttributes();

    // Google 사용자 정보 추출
    String id = (String) attributes.get("sub"); // google 고유 id
    String name = (String) attributes.get("name");
    String email = (String) attributes.get("email");  // DB 식별자로 사용하고 있어 필수

    if (email == null || email.isEmpty()) {
      throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
    }

    Optional<User> userOptional = userRepository.findByEmail(email);
    User user;

    if (userOptional.isPresent()) {
      user = userOptional.get();
      // 기존 사용자가 다른 OAuth 제공자로 가입한 경우 체크
      if (!user.getProvider().equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
        throw new OAuth2AuthenticationException(
                "이미 " + user.getProvider() + " 계정으로 가입된 이메일입니다."
        );
      }
      // 기존 사용자 정보 업데이트
      user.setUsername(name);
      user = userRepository.save(user);
    } else {
      // 새 사용자 생성
      user = new User();
      user.setUsername(name);
      user.setEmail(email);
      user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
      user.setProviderId(id);
      user = userRepository.save(user);
    }

    // 사용자 정보를 attributes에 추가
    Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
    modifiedAttributes.put("user", user);

    // OAuth2User 객체 반환 (권한과 함께)
    return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            modifiedAttributes,
            "sub" // nameAttributeKey
    );
  }
}

