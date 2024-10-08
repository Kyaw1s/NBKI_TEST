package my.nbki_test.services;

import lombok.RequiredArgsConstructor;
import my.nbki_test.dto.UserDTO;
import my.nbki_test.entities.User;
import my.nbki_test.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean tryToUpdateUser(UserDTO userDTO, Integer id) {
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            User updatedUser = optionalUser.get();
            updatedUser.update(userDTO.getFirstName(), userDTO.getLastName());
            return true;
        }
        return false;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean tryToCreateUser(User user) {
        Optional<User> optionalUser = getUserById(user.getId());
        if(optionalUser.isPresent()) {
            return false;
        }

        userRepository.save(user);
        return true;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean tryToDeleteUser(Integer id) {
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return true;
        }
        return false;
    }
}
