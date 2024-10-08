package my.nbki_test.controllers;

import lombok.RequiredArgsConstructor;
import my.nbki_test.dto.UserDTO;
import my.nbki_test.entities.User;
import my.nbki_test.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if(userService.tryToCreateUser(user)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody UserDTO user) {
        if(userService.tryToUpdateUser(user, id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        if (userService.tryToDeleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
