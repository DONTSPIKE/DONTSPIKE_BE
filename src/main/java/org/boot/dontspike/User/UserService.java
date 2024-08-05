package org.boot.dontspike.User;

import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Exception.InvalidCredentialsException;
import org.boot.dontspike.Exception.UsernameAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(String username, String password){

        boolean userExists = userRepository.findByUsername(username).isPresent();
        if (userExists) {
            throw new UsernameAlreadyExistsException("해당 닉네임은 이미 사용중입니다.");
        }

        User user = new User(username, password);
        userRepository.save(user);
    }

    public User login(String username, String password){
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        } else {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

}
