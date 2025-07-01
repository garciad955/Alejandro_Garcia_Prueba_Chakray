package com.chakray.Solution.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

    @JsonIgnore
    private Long Id;

    @JsonProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String Email;
    private String Name;
    @JsonIgnore
    private String Password;
    private String Created_at;
    private List<Address> Addresses;

    public User(String Email, String Name, String Password, List<Address> Addresses) {
        this.Email = Email;
        this.Name = Name;
        this.Password = Password;
        this.Addresses = Addresses;
    }

}
