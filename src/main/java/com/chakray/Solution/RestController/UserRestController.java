package com.chakray.Solution.RestController;

import com.chakray.Solution.Model.ResponseService;
import com.chakray.Solution.Model.UserAddressUpdateDTO;
import com.chakray.Solution.Model.UserCreateDTO;
import com.chakray.Solution.Model.User;
import com.chakray.Solution.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Documentación para especificación de los servicios programados para el modulos Users")
public class UserRestController {
    
    @Autowired
    private UserService userService;
    
    
    @Operation(
        summary = "Obtener todos los usuarios con opción de ordenamiento",
        description = """
                Este servicio permite obtener la lista completa de usuarios registrados en el sistema.
                Se puede ordenar por los siguientes campos opcionales: `name`, `email`, `id`, `created_at`. 
                Si no se especifica un parámetro válido, se devolverá la lista sin ordenamiento o se reportará un error.
            """,
        parameters = {
            @Parameter(
                    name = "sortedBy",
                    description = "Campo por el cual se desea ordenar: `name`, `email`, `id`, `created_at`.",
                    example = "name",
                    required = false
            )
        },
        responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseService.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay usuarios registrados",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campo de ordenamiento inválido. Solo se permiten: `id`, `name`, `email`, `created_at`",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            )
        }
    )
    @GetMapping
    public ResponseEntity GetUsers(@RequestParam(required = false) String sortedBy){
        ResponseService responseService;
        try {
            responseService = userService.GetAll(sortedBy);
            
        } catch (Exception ex){
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        
        
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
    
    @Operation(
        summary = "Obtener direcciones de un usuario",
        description = """
                      Devuelve todas las direcciones asociadas al usuario identificado por su ID.
                """,
        parameters = {
            @Parameter(
                name = "user_id",
                description = "ID del usuario del cual se desean obtener las direcciones",
                required = true,
                example = "5148091052106364696"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Direcciones encontradas correctamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuario no encontrado o sin direcciones",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseService.class)
                )
            )
        }
    )
    @GetMapping("/{user_id}/addresses")
    public ResponseEntity GetAddressesById(@PathVariable Long user_id){
        ResponseService responseService;
        try {
            responseService = userService.GetAddressesById(user_id);
        } catch (Exception ex) {
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
    
    @Operation(
        summary = "Registrar un nuevo usuario",
        description = """
                      Este servicio crea un nuevo usuario a partir de los datos enviados en el cuerpo de la petición. Valida campos obligatorios y estructura del objeto.
                      Los datos a validar en el body son: name, email, password
                """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos requeridos para crear un nuevo usuario",
            content = @Content(schema = @Schema(implementation = UserCreateDTO.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Usuario creado correctamente",
                content = @Content(schema = @Schema(implementation = ResponseService.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error de validación. Alguno de los campos requeridos está mal o vacío"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @PostMapping
    public ResponseEntity AddUser(@Valid @RequestBody UserCreateDTO userDTO, BindingResult bindingResult){
        ResponseService responseService;
        try{
            if (bindingResult.hasErrors()) {
                responseService = new ResponseService();
                responseService.statusCode = 400;
                responseService.correct = false;
                responseService.message = bindingResult.getFieldErrors()
                                        .stream()
                                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                                        .collect(Collectors.joining(" | "));   
            } else {
                responseService = userService.Add(userDTO);
            }
        } catch (Exception ex) {
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        
        
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
    
    @Operation(
        summary = "Actualizar una dirección de un usuario por su ID",
        description = """
                      Este endpoint actualiza una dirección específica de un usuario, validando los campos enviados para la dirección.
                """,
        parameters = {
            @Parameter(
                name = "user_id",
                description = "ID del usuario",
                example = "7255098274824733669",
                required = true
            ),
            @Parameter(
                name = "address_id",
                description = "ID de la dirección del usuario que se desea actualizar",
                example = "1234567890123456789",
                required = true
            )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos de la dirección a actualizar",
            content = @Content(schema = @Schema(implementation = UserAddressUpdateDTO.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Dirección actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = ResponseService.class))
            ),
            @ApiResponse(
                responseCode = "201",
                description = "Dirección creada porque no existía previamente",
                content = @Content(schema = @Schema(implementation = ResponseService.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Errores de validación en los campos enviados"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuario no encontrado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @PutMapping("/{user_id}/addresses/{address_id}")
    public ResponseEntity UpdateAddressUserById(@PathVariable Long user_id, @PathVariable Long address_id , @Valid @RequestBody UserAddressUpdateDTO direccionDTO, BindingResult bindingResult){
        
        ResponseService responseService;
        try{
            if (bindingResult.hasErrors()) {
                responseService = new ResponseService();
                responseService.statusCode = 400;
                responseService.correct = false;
                responseService.message = bindingResult.getFieldErrors()
                                        .stream()
                                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                                        .collect(Collectors.joining(" | "));   
            } else {
                responseService = userService.UpdateAddress(user_id, address_id, direccionDTO);
            }
        } catch (Exception ex){
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
    
    @Operation(
        summary = "Eliminar usuario por ID",
            description = """
                      Elimina un usuario específico usando su ID.
                """,
        parameters = {
            @Parameter(
                name = "id",
                description = "ID del usuario que se desea eliminar",
                required = true,
                example = "1234567890123456789"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario eliminado correctamente",
                content = @Content(schema = @Schema(implementation = ResponseService.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Usuario no encontrado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @DeleteMapping("{id}")
    public ResponseEntity Delete (@PathVariable Long id){
        ResponseService responseService;
        try{      
            responseService = userService.DeleteUser(id);
            
        } catch (Exception ex){
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
    
    @Operation(
        summary = "Actualizar parcialmente un usuario",
        description = """
                      Permite actualizar parcialmente los datos de un usuario existente usando su ID. Solo los campos enviados serán modificados.
                """,
        parameters = {
            @Parameter(
                name = "id",
                description = "ID del usuario a actualizar",
                required = true,
                example = "7255098274824733669"
            )
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos parciales a actualizar del usuario, los datos a utilizar son email, name, password",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario actualizado exitosamente",
                content = @Content(schema = @Schema(implementation = ResponseService.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Usuario no encontrado"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    @PatchMapping("{id}")
    public ResponseEntity UpdateUser (@PathVariable Long id, @RequestBody User user){
        ResponseService responseService;
        try{      
            responseService = userService.UpdateUser(user, id);
            
        } catch (Exception ex){
            responseService = new ResponseService();
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
            responseService.statusCode = 500;
        }
        
        return ResponseEntity.status(responseService.statusCode).body(responseService);
    }
}
