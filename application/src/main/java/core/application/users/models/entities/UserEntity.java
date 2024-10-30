package core.application.users.models.entities;


import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "binary(16)")
    private UUID     userId;
    private String   userEmail;
    private String   userPw;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String   alias;
    private String   phoneNum;
    private String   userName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(userEmail,
                that.userEmail) && Objects.equals(userPw, that.userPw) && role == that.role
                && Objects.equals(alias, that.alias) && Objects.equals(phoneNum,
                that.phoneNum) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(userEmail);
        result = 31 * result + Objects.hashCode(userPw);
        result = 31 * result + Objects.hashCode(role);
        result = 31 * result + Objects.hashCode(alias);
        result = 31 * result + Objects.hashCode(phoneNum);
        result = 31 * result + Objects.hashCode(userName);
        return result;
    }
}
