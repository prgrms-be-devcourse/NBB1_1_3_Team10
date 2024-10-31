package core.application.movies.models.dto.request;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "한줄평 작성 요청")
public class CommentWriteReqDTO {
	@NotBlank(message = "공백은 입력할 수 없습니다.")
	@Schema(description = "한줄평 작성 내용", example = "정말 재밌는 영화네요.")
	private String content;

	@Range(min = 1, max = 10, message = "1점에서 10점 이내로 평점을 입력해주세요.")
	@Schema(description = "영화 평점", example = "9")
	private int rating;
}
