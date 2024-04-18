package com.greeting.greetingcloneback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="users")
public class UserEntity {

    @Id
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String role;

    public String getRole() {
        return "ROLE_USER";
    }

}
