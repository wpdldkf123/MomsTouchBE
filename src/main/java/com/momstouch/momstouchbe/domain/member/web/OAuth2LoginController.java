package com.momstouch.momstouchbe.domain.member.web;

import com.momstouch.momstouchbe.domain.member.model.Member;
import com.momstouch.momstouchbe.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


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

    @GetMapping("/admins/1234")
    public String adminPage() {
        return "admins";
    }

    @GetMapping("/api/login/oauth2/code/google")
    public String oauth2Callback(@PathVariable String registrationId,
                                 OAuth2AuthenticationToken authenticationToken,
                                 HttpServletRequest request) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(registrationId, authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // 사용자 정보를 가져오는 API 호출 등의 추가 작업 수행
        // ...

        // 로그인 처리 및 리다이렉트
        // ...

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:3000");

        return String.valueOf(redirectView); // 로그인 성공 시 홈 페이지로 리다이렉트
    }

    private OAuth2AuthorizedClient getAuthorizedClient(String registrationId, OAuth2AuthenticationToken authenticationToken) {
        return authorizedClientService.loadAuthorizedClient(
                registrationId,
                authenticationToken.getName());
    }

//    @GetMapping("/api/logout")
//    public String logoutv2(HttpServletRequest request) {
//        // 세션을 삭제
//        HttpSession session = request.getSession(true);
//
//        if (session != null) {
//            session.invalidate();
//        }
//
//        // 리다이렉션 URL 반환
//        return "redirect:http://localhost:3000";
//    }

    @GetMapping("/api/user/{id}")  //회원가입 확인용
    public ResponseEntity<Member> getUser(@PathVariable Long id) {
        Member user = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Account 객체의 name 필드를 Member 객체에 설정
        user.getAccount().setName(user.getName());

        return ResponseEntity.ok(user);
    }

    @GetMapping("/api/user/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession httpSession) {
        Member currentUser = (Member) httpSession.getAttribute("user");

        Map<String, Object> response = new HashMap<>();
        response.put("email", currentUser.getEmail());
        response.put("name", currentUser.getAccount().getName());
        response.put("role", currentUser.getRole());

        return ResponseEntity.ok(response);
    }



    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }


}



