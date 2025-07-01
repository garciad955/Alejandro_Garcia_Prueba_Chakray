package com.chakray.Solution.Model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserCreateDTO {
    
    private Long id;
    @NotBlank (message = "Email es un campo obligatorio")
    private String Email;
    @NotBlank (message = "Name es un campo obligatorio")
    private String Name;
    @NotBlank (message = "Password es un campo obligatorio")
    private String Password;
    
    private List<Address> Adresses;

    public UserCreateDTO(String Email, String Name, String Password, List<Address> Adresses) {
        this.Email = Email;
        this.Name = Name;
        this.Password = Password;
        this.Adresses = Adresses;
    }

}
