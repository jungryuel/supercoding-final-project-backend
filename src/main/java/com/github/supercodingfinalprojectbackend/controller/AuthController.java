package com.github.supercodingfinalprojectbackend.controller;

import com.github.supercodingfinalprojectbackend.dto.Login;
import com.github.supercodingfinalprojectbackend.dto.TokenDto;
import com.github.supercodingfinalprojectbackend.entity.type.UserRole;
import com.github.supercodingfinalprojectbackend.exception.errorcode.ApiErrorCode;
import com.github.supercodingfinalprojectbackend.service.Oauth2Service;
import com.github.supercodingfinalprojectbackend.util.ResponseUtils;
import com.github.supercodingfinalprojectbackend.util.ValidateUtils;
import com.github.supercodingfinalprojectbackend.util.auth.AuthUtils;
import com.github.supercodingfinalprojectbackend.util.jwt.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Oauth2Service oauth2Service;

    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 로그인")
    public ResponseEntity<ResponseUtils.ApiResponse<Login.Response>> kakaoLogin(
            @RequestParam(name = "code") @Parameter(name = "카카오 인가 코드", required = true) String code
    ){
        Login login = oauth2Service.kakaoLogin(code);
        Login.Response response = Login.Response.from(login);
        return ResponseUtils.ok("로그인에 성공했습니다.", response);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<ResponseUtils.ApiResponse<Void>> logout() {
        Long userId = AuthUtils.getUserId();
        oauth2Service.logout(userId);
        return ResponseUtils.ok("로그아웃에 성공했습니다.", null);
    }

    @GetMapping("/switch/{roleName}")
    @Operation(summary = "역할 전환")
    public ResponseEntity<ResponseUtils.ApiResponse<Login.Response>> switchRole(
            @PathVariable(name = "roleName") @Parameter(name = "역할 이름", required = true) String roleName
    ) {
        Long userId = AuthUtils.getUserId();
        UserRole userRole = ValidateUtils.requireApply(roleName, r->UserRole.valueOf(r.toUpperCase()), 400, "존재하지 않는 roleName입니다.");
        Login login = oauth2Service.switchRole(userId, userRole);
        Login.Response response = Login.Response.from(login);
        return ResponseUtils.ok("역할을 성공적으로 전환했습니다.", response);
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "액세스 토큰 갱신")
    public ResponseEntity<ResponseUtils.ApiResponse<TokenDto.Response>> renewTokens(
            @RequestBody @Parameter(name = "토큰 갱신 요청 객체", required = true) TokenDto.RefreshTokenRequest request
    ) {
        String bearerToken = request.getRefreshToken();

        ValidateUtils.requireNotNull(bearerToken, 401, "리프레쉬 토큰이 존재하지 않습니다.");
        String refreshToken = JwtUtils.cutPrefix(bearerToken);

        TokenDto tokenDto =  oauth2Service.renewTokens(refreshToken);
        TokenDto.Response response = TokenDto.Response.from(tokenDto);
        return ResponseUtils.ok("토큰이 성공적으로 갱신되었습니다.", response);
    }
}