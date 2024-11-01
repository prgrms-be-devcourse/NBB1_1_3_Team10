package core.application.movies.repositories.mapper

import org.apache.ibatis.annotations.Mapper
import java.util.*

@Mapper
interface CommentLikeMapper {
    fun save(commentId: Long?, userId: UUID?)

    fun countLikeByUser(commentId: Long?, userId: UUID?): Int

    fun delete(commentId: Long?, userId: UUID?)
}
