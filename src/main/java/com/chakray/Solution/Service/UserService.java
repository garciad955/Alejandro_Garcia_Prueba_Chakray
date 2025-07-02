package com.chakray.Solution.Service;

import com.chakray.Solution.Model.Address;
import com.chakray.Solution.Model.ResponseService;
import com.chakray.Solution.Model.User;
import com.chakray.Solution.Model.UserAddressUpdateDTO;
import com.chakray.Solution.Model.UserCreateDTO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final List<User> users = new ArrayList<>();

    public ResponseService Add(UserCreateDTO userCreateDTO) {

        ResponseService responseService = new ResponseService();

        try {
            User user = new User();

            user.setName(userCreateDTO.getName());
            user.setEmail(userCreateDTO.getEmail());
            user.setPassword(userCreateDTO.getPassword());
            user.setAddresses(userCreateDTO.getAdresses());

            user.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
            String createdAt = ZonedDateTime.now(ZoneId.of("Europe/London")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            user.setCreated_at(createdAt);
            if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
                user.getAddresses().stream().forEach(address -> address.setId(Math.abs(UUID.randomUUID().getMostSignificantBits())));
            }
            user.setPassword(sha1(user.getPassword()));

            users.add(user);

            responseService.correct = true;
            responseService.object = user;
            responseService.statusCode = 201;

        } catch (Exception ex) {
            responseService.statusCode = 500;
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService UpdateAddress(Long user_id, Long address_id, UserAddressUpdateDTO direccionDTO) {

        ResponseService responseService = new ResponseService();
        try {
            ResponseService userDirections = GetAddressesById(user_id);

            if (userDirections.correct && userDirections.object != null) {
                List<Address> addresses = ((List<Address>) (userDirections.object));
                Optional<Address> addressToUpdate = addresses.stream().filter(address -> address.getId().toString().equals(address_id.toString())).findFirst();

                Address address;

                if (addressToUpdate.isPresent()) {
                    address = addressToUpdate.get();
                    responseService.statusCode = 200;
                } else {
                    address = new Address();
                    address.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));

                    List<Address> addressesNew = new ArrayList<>(addresses);
                    addressesNew.add(address);

                    users.stream().filter(user -> user.getId() == user_id).findFirst().get().setAddresses(addressesNew);

                    responseService.statusCode = 201;
                }

                address.setNombre(direccionDTO.getNombre());
                address.setStreet(direccionDTO.getStreet());
                address.setCountry_code(direccionDTO.getCountry_code());

                responseService.correct = true;
                responseService.object = address;
            } else {
                Optional<User> userToUse = users.stream().filter(u -> u.getId().toString().equals(user_id.toString())).findFirst();
                if (userToUse.isPresent()) {
                    User user = userToUse.get();
                    Address address = new Address(direccionDTO.getNombre(), direccionDTO.getStreet(), direccionDTO.getCountry_code());
                    address.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
                    user.setAddresses(new ArrayList<Address>());
                    user.getAddresses().add(address);
                    responseService.correct = true;
                    responseService.object = user.getAddresses().get(0);
                    responseService.statusCode = 200;
                } else {
                    responseService.correct = false;
                    responseService.message = "Usuario con id : " + user_id + " inexistente";
                    responseService.statusCode = 400;
                }

            }

        } catch (Exception ex) {
            responseService.correct = false;
            responseService.statusCode = 500;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService DeleteUser(Long user_id) {
        ResponseService responseService = new ResponseService();
        try {
            responseService.correct = users.removeIf(user -> user.getId().toString().equals(user_id.toString()));

            if (responseService.correct) {
                responseService.message = "Usuario con el id : " + user_id + " eliminado";
                responseService.statusCode = 200;
            } else {
                responseService.statusCode = 400;
                responseService.message = "Usuario con el id : " + user_id + " no pudo ser eliminado";
            }

        } catch (Exception ex) {
            responseService.correct = false;
            responseService.statusCode = 500;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService UpdateUser(User userUpdate, Long user_id) {
        ResponseService responseService = new ResponseService();
        try {

            Optional<User> user = users.stream().filter(u -> u.getId().toString().equals(user_id.toString())).findFirst();

            if (user.isPresent()) {
                if (userUpdate.getEmail() != null) {
                    user.get().setEmail(userUpdate.getEmail());
                }
                if (userUpdate.getName() != null) {
                    user.get().setName(userUpdate.getName());
                }
                if (userUpdate.getPassword() != null) {
                    user.get().setPassword(sha1(userUpdate.getPassword()));
                }
                responseService.object = user.get();
                responseService.correct = true;
                responseService.statusCode = 200;
            } else {
                responseService.correct = false;
                responseService.statusCode = 400;
                responseService.message = "Usuario con el id : " + user_id + " no pudo ser actualizado (verifica la existencia del recurso))";
            }

        } catch (Exception ex) {
            responseService.correct = false;
            responseService.statusCode = 500;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService GetAll(String sortedBy) {

        ResponseService responseService = new ResponseService();

        try {

            responseService = sorted(responseService, sortedBy);

        } catch (Exception ex) {
            responseService.correct = false;
            responseService.statusCode = 500;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService GetAddressesById(long user_id) {

        ResponseService responseService = new ResponseService();

        try {

            Optional<User> user = users.stream().filter(u -> u.getId() == user_id).findFirst();
            if (user.isPresent()) {
                responseService.object = user.get().getAddresses();
                responseService.correct = true;
                if (responseService.object == null) {
                    responseService.statusCode = 200;
                    responseService.message = "Usuario con el id : " + user_id + " no tiene direcciones";
                } else {
                    responseService.statusCode = 200;
                }
            } else {
                responseService.correct = false;
                responseService.statusCode = 400;
                responseService.message = "No se encontro usuario con el id : " + user_id;
            }

        } catch (Exception ex) {
            responseService.statusCode = 500;
            responseService.correct = false;
            responseService.message = ex.getLocalizedMessage();
        }

        return responseService;
    }

    public ResponseService sorted(ResponseService responseService, String sortedBy) {

        if (sortedBy == null || sortedBy.equals("")) {
            if (users.size() != 0 && !users.isEmpty()) {
                responseService.correct = true;
                responseService.object = users;
                responseService.statusCode = 200;
            } else {
                responseService.correct = true;
                responseService.statusCode = 204;
                responseService.message = "Lista de usuarios vacía";
            }
        } else {
            if (users.size() != 0 && !users.isEmpty()) {
                responseService.correct = true;
                Comparator<User> comparator
                        = switch (sortedBy) {
                    case "id" ->
                        Comparator.comparing(User::getId);
                    case "name" ->
                        Comparator.comparing(User::getName);
                    case "email" ->
                        Comparator.comparing(User::getEmail);
                    case "created_at" ->
                        Comparator.comparing(User::getCreated_at);
                    default ->
                        null;
                };

                if (comparator == null) {

                    responseService.correct = false;
                    responseService.statusCode = 400;
                    responseService.message = "Opción invalida, selecciona una que sea valida: (id, email, name, created_at, ' ')";

                } else {
                    responseService.correct = true;
                    responseService.statusCode = 200;
                    responseService.object = users.stream().sorted(comparator).toList();
                }

            } else {
                responseService.correct = true;
                responseService.statusCode = 204;
                responseService.message = "Lista de usuarios vacía";
            }
        }

        return responseService;
    }

    public static String sha1(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = messageDigest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }

}
