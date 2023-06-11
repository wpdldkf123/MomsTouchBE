package com.momstouch.momstouchbe.domain.member.model;

import lombok.*;
import org.springframework.security.core.Authentication;

import javax.persistence.*;


@Entity
@Builder
@Data
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; //가입 이메일

    @Embedded
    private Account account;

    public static Member createMember(String loginId, String password, String name, String role,String email) {
        return Member.builder()
                .account(Account.builder()
                        .loginId(loginId)
                        .password(password)
                        .name(name)
                        .role(role)
                        .build())
                //TODO: 수정
                .email(email)
                .build();
    }
    public String getRole(){
        return account.getRole();
    }

    public Member update(String name){
        return Member.builder()
                .account(Account.builder()
                        .name(name)
                        .role("ROLE_MEMBER")
                        .build())
                .build();
    }


    public String getName() {
        return  account.getName();
    }

    public boolean equals(Authentication authentication) {
        return account.equals(authentication);
    }


}
