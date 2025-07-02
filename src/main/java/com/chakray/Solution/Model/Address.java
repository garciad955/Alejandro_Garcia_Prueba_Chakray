package com.chakray.Solution.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Long Id;
    private String Nombre;
    private String Street;
    private String Country_code;

    public Address(String Nombre, String Street, String Country_code) {
        this.Nombre = Nombre;
        this.Street = Street;
        this.Country_code = Country_code;
    }
    
    
}
