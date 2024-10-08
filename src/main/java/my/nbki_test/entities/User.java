package my.nbki_test.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "app_users")
@Data
@AllArgsConstructor
public class User {
    @Id
    private int id;

    private String firstName;
    private String lastName;

    public User() {}

    public void update(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
