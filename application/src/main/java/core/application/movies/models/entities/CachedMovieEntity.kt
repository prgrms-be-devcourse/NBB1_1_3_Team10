package core.application.movies.models.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.*

@Entity
@Table(name = "cached_movie_table")
data class CachedMovieEntity (
    /**
     * `알파벳`-`숫자` 형태 `(KMDB 영화 ID 형태)`
     */
    @Id
    val movieId: String? = null,
    val title: String? = null,
    val posterUrl: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val plot: String? = null,
    val runningTime: String? = null,
    val actors: String? = null,
    val director: String? = null,
    var dibCount: Long = 0,
    var reviewCount: Long = 0,
    var commentCount: Long = 0,
    var sumOfRating: Long = 0
) {

    fun incrementDibCount() {
        dibCount++
    }

    fun decrementDibCount() {
        if (dibCount > 0) {
            dibCount--
        }
    }

    fun incrementReviewCount() {
        reviewCount++
    }

    fun decrementReviewCount() {
        if (reviewCount > 0) {
            reviewCount--
        }
    }

    fun isCommentedWithRating(rating: Int) {
        commentCount++
        sumOfRating += rating.toLong()
    }

    fun deleteComment(rating: Int) {
        if (commentCount > 0) {
            commentCount--
            sumOfRating -= rating.toLong()
        }
    }
}
