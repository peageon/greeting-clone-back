package com.greeting.greetingcloneback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
public class User {

    @Id @GeneratedValue
    private long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;

}
