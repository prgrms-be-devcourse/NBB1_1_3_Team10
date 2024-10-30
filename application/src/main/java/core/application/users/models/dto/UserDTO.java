package core.application.users.models.dto;

import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class UserDTO {
    private UUID userId;

    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message="이메일 주소 양식을 확인해주세요.")
    private String userEmail;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 8자 이상 15자 이하이며, 영문자, 숫자 및 특수문자를 포함해야 합니다.")
    private String userPw;

    private UserRole role;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.")
    private String alias;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다.")
    private String phoneNum;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "이름에는 공백을 포함할 수 없습니다.")
    private String userName;

    // UserDTO -> UserEntity 변환 메서드
    public UserEntity toEntity() {
        return new UserEntity(
                this.userId,
                this.userEmail,
                this.userPw,
                this.role,
                this.alias,
                this.phoneNum,
                this.userName
        );
    }

    public void encodePassword() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.userPw = bCryptPasswordEncoder.encode(this.userPw);
    }
}
