package com.spring.clinic.service;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorProfileDTO;
import com.spring.clinic.dto.doctorDetails.DoctorRegisterDTO;
import com.spring.clinic.dto.patient.PatientProfileDTO;
import com.spring.clinic.dto.users.*;
import com.spring.clinic.entity.DoctorDetails;
import com.spring.clinic.entity.EmailVerificationCode;
import com.spring.clinic.entity.Specialties;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.exception.UserNotVerifiedException;
import com.spring.clinic.repository.DoctorDetailsRepository;
import com.spring.clinic.repository.EmailVerificationCodeRepository;
import com.spring.clinic.repository.SpecialtyRepository;
import com.spring.clinic.repository.UsersRepository;
import com.spring.clinic.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.spring.clinic.service.SpecialtyServiceImpl.getSpecialties;

@Service
public class UsersServiceImpl implements UsersService{
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationCodeRepository codeRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final EmailVerificationCodeService emailVerificationCodeService;

    private final DoctorDetailsRepository doctorDetailsRepository;
    private final SpecialtyRepository specialtyRepository;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, EmailVerificationCodeRepository codeRepository, EmailService emailService, JwtUtil jwtUtil, EmailVerificationCodeService emailVerificationCodeService, DoctorDetailsRepository doctorDetailsRepository, SpecialtyRepository specialtyRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.codeRepository = codeRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.emailVerificationCodeService = emailVerificationCodeService;
        this.doctorDetailsRepository = doctorDetailsRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public ResponseEntity<?> loginUser(UserLoginDTO user) {
        try {
            Users users = usersRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
            if (!passwordEncoder.matches(user.getPassword(), users.getPassword())) {
                throw new ApiException("Invalid password", HttpStatus.BAD_REQUEST);
            }
            if (!users.isVerified()) {
                throw new ApiException("User is not verified", HttpStatus.UNAUTHORIZED);
            }
            String token = jwtUtil.generateToken(user.getEmail(), String.valueOf(users.getRole()));
            if (users.isActive()) {
                UserLoginResponseDTO response = new UserLoginResponseDTO();
                response.setUserName(users.getName());
                response.setEmail(user.getEmail());
                response.setToken(token);
                response.setUserRole(users.getRole().name());

                return ResponseEntity.ok(response);
            } else
                throw new ApiException("The Clinic disabled your account contact the support to know more information.", HttpStatus.FORBIDDEN);
        }catch (UserNotVerifiedException ex) {
            emailVerificationCodeService.resendCode(user.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Account not verified. Verification code resent.");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }

    @Override
    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users findUserById(long id) {
        return usersRepository.findById(id).orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public void registerUserAsPatient(UserRegisterDTO user) {
        if (usersRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ApiException("Email is already in use", HttpStatus.CONFLICT);
        }
        Users users = new Users();
        users.setName(user.getUsername());
        users.setPassword(passwordEncoder.encode(user.getPassword()));
        users.setEmail(user.getEmail());
        users.setPhoneNumber(user.getPhoneNumber());
        users.setVerified(false);
        users.setRole(Users.UerRole.PATIENT);
        users.setActive(true);
        users.setAge(user.getAge());

        Users savedUsers = usersRepository.save(users);

        createAndSendVerificationCode(savedUsers,user.getEmail());
    }
    @Override
    @Transactional
    public void registerUserAsDoctor(DoctorRegisterDTO userDto) {
        usersRepository.findByEmail(userDto.getEmail())
                .ifPresent(u -> {
                    throw new ApiException("Email is already in use",HttpStatus.CONFLICT);
                });

        if (userDto.getSpecialtyIds() == null || userDto.getSpecialtyIds().isEmpty()) {
            throw new ApiException("At least one specialty must be selected", HttpStatus.BAD_REQUEST);
        }
        Users doctor = new Users();
        doctor.setName(userDto.getFullName());
        doctor.setPassword(passwordEncoder.encode(userDto.getPassword()));
        doctor.setEmail(userDto.getEmail());
        doctor.setPhoneNumber(userDto.getPhoneNumber());
        doctor.setVerified(false);
        doctor.setRole(Users.UerRole.DOCTOR);
        doctor.setActive(true);
        doctor.setAge(0);
        Set<Specialties> specialties = fetchAndValidateSpecialties(userDto.getSpecialtyIds());
        doctor.setSpecialties(specialties);
        Users savedUser = usersRepository.save(doctor);
        createDoctorDetails(savedUser, userDto);
        createAndSendVerificationCode(savedUser, userDto.getEmail());
    }

    @Override
    public  UserDTO getPatientPrivateProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users user=fetchUserByToken(userDetails);
        UserDTO userDTO=new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setPassword(user.getPassword());
        userDTO.setAge(user.getAge());
        userDTO.setUserRole(String.valueOf(user.getRole()));
        return userDTO;
    }

    @Override
    public PatientProfileDTO getPatientPublicProfile(long id) {
        Users user = usersRepository.findPatientByUserId(id);
        if(user==null){
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        PatientProfileDTO patientProfileDTO = new PatientProfileDTO();
        patientProfileDTO.setId(user.getUserId());
        patientProfileDTO.setFullName(user.getName());
        patientProfileDTO.setEmail(user.getEmail());
        patientProfileDTO.setPhone(user.getPhoneNumber());
        patientProfileDTO.setAge(user.getAge());
        return patientProfileDTO;
    }

    @Override
    public DoctorProfileDTO getDoctorPublicProfile(long id) {
        DoctorDetails doctorDetails = doctorDetailsRepository.findByDoctorId(id);
        if(doctorDetails==null){
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        DoctorProfileDTO doctorProfileDTO = new DoctorProfileDTO();
        doctorProfileDTO.setId(doctorDetails.getDoctorId());
        doctorProfileDTO.setFullName(doctorDetails.getDoctor().getName());
        doctorProfileDTO.setSpecialties(doctorDetails.getDoctor().getSpecialties());
        doctorProfileDTO.setEmail(doctorDetails.getDoctor().getEmail());
        doctorProfileDTO.setPhone(doctorDetails.getDoctor().getPhoneNumber());
        doctorProfileDTO.setYearsOfExperience(calculateYearsOfExperience(id));
        doctorProfileDTO.setBio(doctorDetails.getBio());
        doctorProfileDTO.setNotes(doctorDetails.getNotes());
        return doctorProfileDTO;
    }

    @Override
    public DoctorDetails getDoctorPrivateProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users user=fetchUserByToken(userDetails);
        return doctorDetailsRepository.findByDoctorId(user.getUserId());
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUserById(long id) {
        Users user = usersRepository.findPatientByUserId(id);
        if (user==null){
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        user.setActive(false);
        usersRepository.save(user);
        return ResponseEntity.ok("Disabled Successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users users1=fetchUserByToken(userDetails);
        users1.setActive(false);
        usersRepository.save(users1);
        return ResponseEntity.ok("Disabled Successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<UserDTO> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails, UserUpdateDTO userUpdateDTO) {
        Users user=fetchUserByToken(userDetails);
        if(userUpdateDTO.getName() != null){
            user.setName(userUpdateDTO.getName());
        }
        if(userUpdateDTO.getPhoneNumber() != null){
            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if(userUpdateDTO.getAge() != null){
            user.setAge(userUpdateDTO.getAge());
        }
        if(userUpdateDTO.getSpecialtyIds() != null){
            Set<Specialties> specialties = fetchAndValidateSpecialties(userUpdateDTO.getSpecialtyIds());
            user.setSpecialties(specialties);
        }else {
            user.setSpecialties(user.getSpecialties());
        }
        usersRepository.save(user);
        UserDTO userDTO=new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setAge(user.getAge());
        userDTO.setPassword(user.getPassword());
        userDTO.setUserRole(String.valueOf(user.getRole()));
        return ResponseEntity.ok(userDTO);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateMyPassword(@AuthenticationPrincipal UserDetails userDetails, PasswordUpdateDTO userUpdateDTO) {
        Users user=fetchUserByToken(userDetails);
        if (userUpdateDTO.getOldPassword() == null || userUpdateDTO.getOldPassword().isBlank() ||
                userUpdateDTO.getNewPassword() == null || userUpdateDTO.getNewPassword().isBlank()) {
            throw new ApiException("Password fields cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(userUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new ApiException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(userUpdateDTO.getNewPassword(), user.getPassword())) {
            throw new ApiException("new password must be different", HttpStatus.CONFLICT);
        }
        String hashedPassword = passwordEncoder.encode(userUpdateDTO.getNewPassword());
        user.setPassword(hashedPassword);
        usersRepository.save(user);
        emailService.sendChangingEmail(user.getEmail(),userUpdateDTO.getNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

    @Override
    public Users fetchUserByToken(UserDetails userDetails) {
        Optional<Users> users = usersRepository.findByEmail(userDetails.getUsername());
        if (users.isEmpty()) {
            throw new ApiException("User not found with email: " + userDetails.getUsername(), HttpStatus.NOT_FOUND);
        }
        return users.get();
    }

    @Override
    public List<DoctorDetailsDTO> findAllDoctorsDetails() {
        List<DoctorDetails> doctors = doctorDetailsRepository.findAll();
        if (doctors.isEmpty()) {
            throw new ApiException("No doctors found", HttpStatus.NOT_FOUND);
        }
        return doctors.stream().map(doctorDetails ->{
          DoctorDetailsDTO doctorDetailsDTO = new DoctorDetailsDTO();
          doctorDetailsDTO.setId(doctorDetails.getDoctorId());
          doctorDetailsDTO.setStartedWorkingAt(doctorDetails.getStartedWorkingAt());
          doctorDetailsDTO.setBio(doctorDetails.getBio());
          doctorDetailsDTO.setNotes(doctorDetails.getNotes());
            DoctorDetailsDTO.DoctorInfo doctorInfo = new DoctorDetailsDTO.DoctorInfo();
            doctorInfo.setId(doctorDetails.getDoctorId());
            doctorInfo.setDoctorName(doctorDetails.getDoctor().getName());
            doctorInfo.setDoctorEmail(doctorDetails.getDoctor().getEmail());
            doctorInfo.setDoctorPhone(doctorDetails.getDoctor().getPhoneNumber());
            doctorInfo.setSpecialties(doctorDetails.getDoctor().getSpecialties());
            doctorDetailsDTO.setDoctorInfo(doctorInfo);
            return doctorDetailsDTO;
        }).collect(Collectors.toList());

    }

    @Override
    public Users findUserByEmail(String email) {
        Optional<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty()) {
            throw new ApiException("User not found with email: " + email,HttpStatus.NOT_FOUND);
        }
        return users.get();
    }
    @Override
    public void setUserActive(long id){
        Users user = usersRepository.findPatientByUserId(id);
        if (user==null){
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        user.setActive(true);
        usersRepository.save(user);
    }

    @Override
    public List<Users> findAllPatients() {
        List<Users> users = usersRepository.findAllByRole(Users.UerRole.PATIENT);
        if (users.isEmpty()) {
            throw new ApiException("No patients found", HttpStatus.NOT_FOUND);
        }
        return users;
    }

    @Override
    public List<Users> findAllDoctors() {
        List<Users> users = usersRepository.findAllByRole(Users.UerRole.DOCTOR);
        if (users.isEmpty()) {
            throw new ApiException("No Doctors found", HttpStatus.NOT_FOUND);
        }
        return users;
    }



    private Set<Specialties> fetchAndValidateSpecialties(Set<Long> specialtyIds) {
        return getSpecialties(specialtyIds, specialtyRepository);
    }
    private void createDoctorDetails(Users savedUser, DoctorRegisterDTO userDto) {
        DoctorDetails doctorDetails = new DoctorDetails();
        doctorDetails.setDoctor(savedUser);
        doctorDetails.setBio(userDto.getBio());
        doctorDetails.setNotes(userDto.getNotes());
        doctorDetails.setStartedWorkingAt(userDto.getStartedWorkingAt());
        doctorDetailsRepository.save(doctorDetails);
    }

    private void createAndSendVerificationCode(Users savedUser, String email) {
        String code = generateCode();
        EmailVerificationCode emailVerificationCode = new EmailVerificationCode();
        emailVerificationCode.setCode(code);
        emailVerificationCode.setUserId(savedUser);
        emailVerificationCode.setExpiry(LocalDateTime.now().plusMinutes(10));
        codeRepository.save(emailVerificationCode);
        emailService.sendVerificationEmail(email, code);
    }
    private int calculateYearsOfExperience(long id){
        int yearsOfExperience;
        DoctorDetails doctorDetails=doctorDetailsRepository.findByDoctorId(id);
        LocalDate startDate= doctorDetails.getStartedWorkingAt();
        LocalDate now = LocalDate.now();
        yearsOfExperience =  now.getYear() - startDate.getYear() ;
        return yearsOfExperience;

    }
    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
