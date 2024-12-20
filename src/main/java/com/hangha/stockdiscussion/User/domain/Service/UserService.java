package com.hangha.stockdiscussion.User.domain.Service;

import com.hangha.stockdiscussion.User.application.command.RegisterUserCommand;
import com.hangha.stockdiscussion.User.domain.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
 // 회원 가입 처리
 User registerUser(RegisterUserCommand command);

 // 이메일 중복 체크
 boolean isUserExists(String email);

 // 로그인 검증 추가
 boolean validateLogin(String email, String password, HttpServletResponse res);
}