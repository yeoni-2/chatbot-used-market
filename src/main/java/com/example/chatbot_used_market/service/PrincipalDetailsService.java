package com.example.chatbot_used_market.service;

import com.example.chatbot_used_market.entity.PrincipalDetails;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public PrincipalDetailsService(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) return null;

    return new PrincipalDetails(user);
  }
}
