package com.chakray.Solution.Model;

import io.swagger.v3.oas.annotations.media.Schema;

public class ResponseService {
    @Schema(example = "true")
    public boolean correct;
    @Schema(example = "Usuario creado exitosamente")
    public String message;
    @Schema(description = "Objeto con los datos del recurso creado", anyOf = {User.class})
    public Object object;
    @Schema(example = "200")
    public int statusCode;
    
}
