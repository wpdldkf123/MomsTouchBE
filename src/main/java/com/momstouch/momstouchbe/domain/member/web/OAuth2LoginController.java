package com.momstouch.momstouchbe.domain.member.web;

import com.momstouch.momstouchbe.domain.member.model.Member;
import com.momstouch.momstouchbe.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class OAuth2LoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 로그인 페이지로 이동
    }

    @GetMapping("/admins")
    public String adminPage() {
        return "admins";
    }

    @GetMapping("/login/oauth2/code/google")
    public String oauth2Callback(@PathVariable String registrationId,
                                 OAuth2AuthenticationToken authenticationToken,
                                 HttpServletRequest request) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(registrationId, authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // 사용자 정보를 가져오는 API 호출 등의 추가 작업 수행
        // ...

        // 로그인 처리 및 리다이렉트
        // ...

        return "redirect:/admins"; // 로그인 성공 시 홈 페이지로 리다이렉트
    }

    private OAuth2AuthorizedClient getAuthorizedClient(String registrationId, OAuth2AuthenticationToken authenticationToken) {
        return authorizedClientService.loadAuthorizedClient(
                registrationId,
                authenticationToken.getName());
    }

    @GetMapping("/logout")
    public String logoutv2(HttpServletRequest request) {
        //세션을 삭제
        HttpSession session = request.getSession(true);
        // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
        // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/api/user/{id}")  //회원가입 확인용
    public ResponseEntity<Member> getUser(@PathVariable Long id) {
        Member user = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Account 객체의 name 필드를 Member 객체에 설정
        user.getAccount().setName(user.getName());

        return ResponseEntity.ok(user);
    }

    @GetMapping("/api/user/me")
    public ResponseEntity<Member> getCurrentUser(HttpSession httpSession) {
        Member currentUser = (Member) httpSession.getAttribute("user");


        return ResponseEntity.ok(currentUser);
    }


    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }


}



