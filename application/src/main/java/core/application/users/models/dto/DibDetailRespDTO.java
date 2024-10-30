package core.application.users.models.dto;

import lombok.*;

@Builder
@Getter
public class DibDetailRespDTO {

    /*
    * movieId : 찜한 영화 아이디
    * movieTitle : 찜한 영화 제목
    * moviePost : 찜한 영화 포스터
     */
    private String movieId;
    private String movieTitle;
    private String moviePost;
}
