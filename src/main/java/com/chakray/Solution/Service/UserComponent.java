package com.chakray.Solution.Service;

import com.chakray.Solution.Model.Address;
import com.chakray.Solution.Model.User;
import com.chakray.Solution.Model.UserCreateDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.Arrays;


@Component
public class UserComponent {

    private final UserService userService;

    public UserComponent(UserService userService) {
        this.userService = userService;
    }
    
    @PostConstruct
    public void InitUsers(){
        
        userService.Add(new UserCreateDTO("dgarcia@email.com", 
                "Daniel Alejandro García Torres", 
                "dgarcia123", 
                Arrays.asList(
                        new Address("Casa", 
                                "Calle A No.1", 
                                "MX"),
                        new Address(
                                "Trabajo", 
                                "Avenida A No.25", 
                                "MX"))));
        
        
        userService.Add(new UserCreateDTO("rwiliams@email.com", 
                "Robert Williams", 
                "rwilliams123", 
                Arrays.asList(
                        new Address(
                                "House", 
                                "123 Revolution Street", 
                                "US"))));
        
        
        
        userService.Add(new UserCreateDTO("eruelas@email.com", 
                "Erick Ruelas", 
                "eruelas123", 
                Arrays.asList(
                        new Address(
                                "Casa mamá", 
                                "Calle B No. 2", 
                                "MX"),
                        new Address(
                                "Casa", 
                                "Calle C No. 3", 
                                "MX"))));
    }
}
