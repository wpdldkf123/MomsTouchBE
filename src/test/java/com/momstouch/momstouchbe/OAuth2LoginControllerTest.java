package com.momstouch.momstouchbe;

import com.momstouch.momstouchbe.domain.member.Service.CustomOAuth2UserService;
import com.momstouch.momstouchbe.domain.member.model.Account;
import com.momstouch.momstouchbe.domain.member.model.Member;
import com.momstouch.momstouchbe.domain.member.model.OAuthAttributes;
import com.momstouch.momstouchbe.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class OAuth2LoginControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean private MemberRepository memberRepository;




    @Test
    @WithMockUser(roles = "MEMBER")
    void 회원_가입() throws Exception {
        Member member =  Member.createMember("인증키","인증번호","이름","ROLE_MEMBER","email");
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(member));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", 1L));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.name").value(member.getAccount().getName()))
                .andExpect(jsonPath("$.role").value(member.getRole()));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void 어드민_테스트() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admins/1234"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 어드민_접속_테스트() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admins"))
                .andExpect(status().isOk());
    }


    @Test
    void 저장_수정_테스트() {
        // Mock 데이터 준비
        MemberRepository memberRepository = mock(MemberRepository.class);
        HttpSession httpSession = mock(HttpSession.class);
        CustomOAuth2UserService service = new CustomOAuth2UserService(memberRepository, httpSession);

        // Mock memberRepository.findByEmail
        OAuthAttributes attributes = OAuthAttributes.builder()
                .email("test@example.com")
                .name("Test Name")
                .role("ROLE_TEST")
                .build();
        Member existingMember = Member.builder()
                .email("test@example.com")
                .account(Account.builder().name("Existing Member").role("ROLE_TEST").build())
                .build();
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existingMember));

        // Mock memberRepository.save
        Member savedMember = Member.builder()
                .email("test@example.com")
                .account(Account.builder().name("Updated Member").role("ROLE_UPDATED").build())
                .build();
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // 테스트할 메서드 호출
        Member result = service.saveOrUpdate(attributes);

        // memberRepository.save가 호출되었는지 확인
        verify(memberRepository, times(1)).save(any(Member.class));

        // 결과 검증
        assertNotNull(result);
        assertEquals(savedMember, result);
    }













}
