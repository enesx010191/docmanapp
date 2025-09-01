package com.moneymate.documentationManagement.business.concretes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.moneymate.documentationManagement.business.abstracts.UserService;
import com.moneymate.documentationManagement.business.requests.LoginUserReq;
import com.moneymate.documentationManagement.business.requests.RegisterUserReq;
import com.moneymate.documentationManagement.business.responses.LoginUserRes;
import com.moneymate.documentationManagement.core.utilities.Messages;
import com.moneymate.documentationManagement.core.utilities.SecurityConfig.JwtService;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessResult;
import com.moneymate.documentationManagement.core.utilities.mappers.ModelMapperConfig;
import com.moneymate.documentationManagement.dataAccess.abstracts.UserRepository;
import com.moneymate.documentationManagement.entities.concretes.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {

	private final UserRepository userRepository;
	private final ModelMapperConfig modelMapperService;
	private final HttpServletRequest request;
	private final PasswordEncoder  passwordEncoder;
    private JwtService jwtService;

	@Override
	public Result register(RegisterUserReq registerUserReq) {

		Optional<User> existingUser = userRepository.findByEmail(registerUserReq.getEmail());

		if (existingUser.isEmpty()) {
			User registerUser = this.modelMapperService.modelMapper().map(registerUserReq, User.class);
			String hashedPassword = passwordEncoder.encode(registerUserReq.getPassword());
			registerUser.setPassword(hashedPassword);
			registerUser.setUserId(UUID.randomUUID().toString());
			registerUser.setCreatedAt(LocalDateTime.now());
			registerUser.setUpdateAt(LocalDateTime.now());
			registerUser.setCreatedIp(getClientIpAddress()); // IP adresini alma metodu
			registerUser.setStatus((byte) 1);
			registerUser.setLoginAttemptCount((byte) 0);
			registerUser.setIsActive(true);
			registerUser.setIsMultipleSession(false);
			registerUser.setFirstLoginStatus(true);
			registerUser.setPasswordCreateAt(LocalDateTime.now().toString());
			registerUser.setPasswordUpdateAt(LocalDateTime.now().toString());
			userRepository.save(registerUser);
			return new SuccessResult(Messages.UserCreated);
		}

		return new ErrorResult(Messages.AlreadyExistUser);
	}

	@Override
	public DataResult<LoginUserRes> login(LoginUserReq loginUserReq) {
	    Optional<User> user = userRepository.findByEmail(loginUserReq.getEmail());
	    
	    if (user.isEmpty() || !passwordEncoder.matches(loginUserReq.getPassword(), user.get().getPassword())) {
	    	var result = new LoginUserRes(401);
	 
	        return new ErrorDataResult<LoginUserRes>(result,Messages.LoginInformationIsIncorrect);
	    }

	    String token = jwtService.generateToken(
	        user.get().getEmail(), 
	        user.get().getFirstName(), 
	        user.get().getLastName()
	    );
	    
	    var loginUserRes = new LoginUserRes(
		        token
		    );
	    
	    return new SuccessDataResult<LoginUserRes>(loginUserRes, Messages.LoginSuccessful);
	}


	private String getClientIpAddress() {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}

}