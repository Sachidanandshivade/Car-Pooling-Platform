package com.CarPooling.CarPoolingPlatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
     private String name;
     @Column(unique = true)
    private String email;
     private String password;
     private String Phone;
<<<<<<< HEAD
    private String role;
=======
    private String Role;
>>>>>>> f23408174ba710020cb531aa74c34512142e947a


}
