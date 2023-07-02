package com.todo.authservice.service;

import com.todo.authservice.exception.UserAlreadyExistsException;
import com.todo.authservice.model.LocalUser;
import com.todo.authservice.model.LoginBody;
import com.todo.authservice.model.RegistrationBody;
import com.todo.authservice.model.dao.LocalUserDao;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private LocalUserDao localUserDao;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;

    public AuthService(LocalUserDao localUserDao, EncryptionService encryptionService, JWTService jwtService) {
        this.localUserDao = localUserDao;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {
        if (localUserDao.findByEmailIgnoreCase(registrationBody.getEmail())
                .isPresent()
                || localUserDao.findByUsernameIgnoreCase(registrationBody.getUsername())
                .isPresent()) {
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setId(UUID.randomUUID()
                .toString());
        user.setEmail(registrationBody.getEmail());
        user.setUsername(registrationBody.getUsername());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setRole("EMPLOYEE");
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
//        VerificationToken verificationToken = createVerificationToken(user);
//        emailService.sendVerificationEmail(verificationToken);
        return localUserDao.save(user);
    }

    public String loginUser(LoginBody loginBody) {
        Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()) {
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if(user.getEmailVerified()) {
                    return jwtService.generateJWT(user);
                } else {
//                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
//                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0)
//                            .getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
//                    if(resend) {
//                        VerificationToken verificationToken = createVerificationToken(user);
//                        verificationTokenDao.save(verificationToken);
//                        emailService.sendVerificationEmail(verificationToken);
//                    }
//                    throw new UserNotVerifiedException(resend);

                }
            }
            System.out.println(loginBody.getPassword());
            System.out.println(user.getPassword());
            return jwtService.generateJWT(user);
        }
        return null;
    }


}
