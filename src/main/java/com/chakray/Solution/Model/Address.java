package com.chakray.Solution.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @JsonIgnore
    private Long Id;

    @JsonProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String Nombre;
    private String Street;
    private String Country_code;

    public Address(String Nombre, String Street, String Country_code) {
        this.Nombre = Nombre;
        this.Street = Street;
        this.Country_code = Country_code;
    }
    
    
}
