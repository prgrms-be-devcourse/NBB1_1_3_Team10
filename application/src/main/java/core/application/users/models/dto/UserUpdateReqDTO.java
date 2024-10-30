package core.application.users.models.dto;

import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdateReqDTO {
    @NotNull
    private String userEmail;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String userPw;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.")
    private String userName;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.")
    private String alias;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 양식을 확인해주세요")
    private String phoneNum;

    // UserDTO -> UserEntity 변환 메서드
    public UserEntity toEntity() {
        return new UserEntity(
                null,
                this.userEmail,
                this.userPw,
                null,
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