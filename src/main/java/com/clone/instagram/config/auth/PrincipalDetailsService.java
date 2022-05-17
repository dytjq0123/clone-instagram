package com.clone.instagram.config.auth;

import com.clone.instagram.entity.User;
import com.clone.instagram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    /**
     * UserDetailsService를 상속 받으면 필수로 구현해야 하는 메서드
     * 로그인 요청시 loginProcessingUrl 호출시 자동으로 실행됨
     * password값이 일치하는지 확인하는 코드는 없지만 시큐리티가 자동적으로 확인해줌
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null){
            return null;
        }else {
            return new PrincipalDetails(user);
        }
    }
}
