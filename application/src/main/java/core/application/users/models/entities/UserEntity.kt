package core.application.users.models.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_table")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "binary(16)")
    var userId: UUID? = null,
    val userEmail: String? = null,
    val userPw: String? = null,

    @Enumerated(EnumType.STRING)
    val role: UserRole? = null,
    val alias: String? = null,
    val phoneNum: String? = null,
    val userName: String? = null
) {
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as UserEntity
        return userId == that.userId && userEmail == that.userEmail && userPw == that.userPw && role == that.role && alias == that.alias && phoneNum == that.phoneNum && userName == that.userName
    }

    override fun hashCode(): Int {
        var result = Objects.hashCode(userId)
        result = 31 * result + Objects.hashCode(userEmail)
        result = 31 * result + Objects.hashCode(userPw)
        result = 31 * result + Objects.hashCode(role)
        result = 31 * result + Objects.hashCode(alias)
        result = 31 * result + Objects.hashCode(phoneNum)
        result = 31 * result + Objects.hashCode(userName)
        return result
    }
}
