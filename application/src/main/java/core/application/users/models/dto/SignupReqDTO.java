package core.application.users.models.dto;

import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignupReqDTO {

    @Email
    @NotNull(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 주소 양식을 확인해주세요")
    private String userEmail;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String userPw;

    @NotNull(message = "역할은 필수 입력 값입니다.")
    private UserRole role;

    @NotNull(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.")
    private String userName;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.")
    private String alias;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 양식을 확인해주세요")
    private String phoneNum;

    // UserDTO -> UserEntity 변환 메서드
    public UserEntity toEntity() {
        return UserEntity.builder()
                .userEmail(this.userEmail)
                .userPw(this.userPw)
                .role(this.role)
                .alias(this.alias)
                .phoneNum(this.phoneNum)
                .userName(this.userName)
                .build();
    }

    public void encodePassword() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.userPw = bCryptPasswordEncoder.encode(this.userPw);
    }
}