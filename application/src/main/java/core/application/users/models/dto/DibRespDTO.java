package core.application.users.models.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DibRespDTO {

    /*
    * message : 찜 삭제 완료 메시지
    * userId : 유저 아이디
    * movieId : 찜 삭제한 영화 아이디
     */
    private String message;
    private UUID userId;
    private String movieId;
}
