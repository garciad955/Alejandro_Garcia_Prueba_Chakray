package com.chakray.Solution;

import com.chakray.Solution.Model.ResponseService;
import com.chakray.Solution.Model.User;
import com.chakray.Solution.Model.UserCreateDTO;
import com.chakray.Solution.Service.UserService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SolutionApplicationTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("Agregar usuario correctamente")
    void testAddUserSuccessfully() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Juan Pérez");
        dto.setEmail("juan@test.com");
        dto.setPassword("123456");

        ResponseService response = userService.Add(dto);

        assertTrue(response.correct);
        assertEquals(201, response.statusCode);
        assertNotNull(response.object);
        assertTrue(response.object instanceof User);
        assertEquals("juan@test.com", ((User) response.object).getEmail());
    }

    @Test
    @DisplayName("Eliminar usuario inexistente")
    void testDeleteNonExistingUser() {
        ResponseService response = userService.DeleteUser(999999L);
        assertFalse(response.correct);
        assertEquals(400, response.statusCode);
        assertEquals("Usuario con el id : 999999 no pudo ser eliminado", response.message);
    }

    @Test
    @DisplayName("Ordenar usuarios por fecha y hora de creación")
    void testSortedByName() {
        userService.Add(new UserCreateDTO("aruelas@test.com", "Antonio Ruelas", "aruelas123", null));
        userService.Add(new UserCreateDTO("jguzman@test.com", "Josafat Guzman", "jguzman123", null));
        userService.Add(new UserCreateDTO("mafer@test.com", "Maria Fernanda", "mafer123", null));

        ResponseService response = userService.GetAll("created_at");
        assertTrue(response.correct);

        List<User> result = (List<User>) response.object;
        assertEquals("Antonio Ruelas", result.get(3).getName());
        assertEquals("Josafat Guzman", result.get(4).getName());
        assertEquals("Maria Fernanda", result.get(5).getName());
    }

    @Test
    @DisplayName("Actualizar datos de usuario existente")
    void testUpdateUser() {
        UserCreateDTO dto = new UserCreateDTO("ana@test.com", "Ana", "123", null);
        User user = (User) userService.Add(dto).object;

        User update = new User();
        update.setName("Ana María");
        update.setPassword("abcdef");

        ResponseService response = userService.UpdateUser(update, user.getId());

        assertTrue(response.correct);
        assertEquals(200, response.statusCode);
        User updated = (User) response.object;
        assertEquals("Ana María", updated.getName());
    }

    @Test
    @DisplayName("Obtener direcciones de usuario inexistente")
    void testGetAddressesOfNonExistentUser() {
        ResponseService response = userService.GetAddressesById(123456789L);

        assertFalse(response.correct);
        assertEquals(400, response.statusCode);
        assertEquals("No se encontro usuario con el id : 123456789", response.message);
    }
}
