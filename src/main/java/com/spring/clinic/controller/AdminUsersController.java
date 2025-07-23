package com.spring.clinic.controller;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.users.EmailDTO;
import com.spring.clinic.dto.users.PasswordUpdateDTO;
import com.spring.clinic.entity.Users;
import com.spring.clinic.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-users")
public class AdminUsersController {
    private final UsersService usersService;

    public AdminUsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = usersService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable long id) {
        Users users = usersService.findUserById(id);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> disableUser(@PathVariable long id) {
        return usersService.deleteUserById(id);
    }

    @PutMapping("/my-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return usersService.updateMyPassword(userDetails,passwordUpdateDTO);
    }
    @GetMapping("/email")
    public ResponseEntity<Users> getUserByEmail(@RequestBody EmailDTO emailDTO) {
        Users users=usersService.findUserByEmail(emailDTO.getEmail());
        return ResponseEntity.ok(users);
    }
    @GetMapping("/all-doctors-details")
    public ResponseEntity<List<DoctorDetailsDTO>> getAllDoctorsDetails() {
        List<DoctorDetailsDTO> doctorDetailsDTO=usersService.findAllDoctorsDetails();
        return ResponseEntity.ok(doctorDetailsDTO);
    }
    @GetMapping("/all-doctors")
    public ResponseEntity<List<Users>> getAllDoctors() {
        List<Users> users=usersService.findAllDoctors();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/all-patients")
    public  ResponseEntity<List<Users>> getAllPatients() {
        List<Users> users=usersService.findAllPatients();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/users/activate/{id}")
    public ResponseEntity<?> activateUser(@PathVariable long id) {
        usersService.setUserActive(id);
        return ResponseEntity.ok("User Activated Successfully");
    }


}
