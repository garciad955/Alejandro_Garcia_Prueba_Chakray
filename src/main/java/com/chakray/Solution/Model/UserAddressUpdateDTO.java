package com.chakray.Solution.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressUpdateDTO {
    
    private long Id;
    @NotBlank (message = "Nombre es un campo obligatorio")
    private String Nombre;
    @NotBlank (message = "Street es un campo obligatorio")
    private String Street;
    @NotBlank (message = "Country code es un campo obligatorio")
    private String Country_code;

    public UserAddressUpdateDTO(String Nombre, String Street, String Country_code) {
        this.Nombre = Nombre;
        this.Street = Street;
        this.Country_code = Country_code;
    }
    
    
}