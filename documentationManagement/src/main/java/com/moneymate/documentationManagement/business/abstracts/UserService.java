 package com.moneymate.documentationManagement.business.abstracts;
 import com.moneymate.documentationManagement.business.requests.LoginUserReq;
import com.moneymate.documentationManagement.business.requests.RegisterUserReq;
import com.moneymate.documentationManagement.business.responses.LoginUserRes;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
 
public interface UserService {

	Result register(RegisterUserReq registerUserReq);
	DataResult<LoginUserRes> login(LoginUserReq loginUserReq);
}
