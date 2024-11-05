package core.application.users.models.entities

import jakarta.persistence.*
import lombok.*
import java.util.*

@Entity
@Table(name = "dib_table")
data class DibEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val dibId: Long? = null,

    @Column(nullable = false, columnDefinition = "binary(16)", length = 16)
    var userId: UUID? = null,

    @Column(nullable = false, length = 50)
    var movieId: String? = null
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val dibEntity = o as DibEntity
        return dibId == dibEntity.dibId && userId == dibEntity.userId && movieId == dibEntity.movieId
    }

    override fun hashCode(): Int {
        var result = Objects.hashCode(dibId)
        result = 31 * result + Objects.hashCode(userId)
        result = 31 * result + Objects.hashCode(movieId)
        return result
    }
}
