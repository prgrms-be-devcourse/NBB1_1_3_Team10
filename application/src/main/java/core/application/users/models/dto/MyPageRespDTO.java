package core.application.users.models.dto;

import core.application.users.models.entities.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyPageRespDTO {

    /*
    마이페이지 조회에 사용되는 DTO
     * userEmail : 유저 이메일
     * alias : 유저 별명
     * phoneNum : 유저 전화번호
     * role : 유저 역할
    */
    private String userEmail;
    private String alias;
    private String phoneNum;
    private String userName;
    private UserRole role;
    private List<DibDetailRespDTO> dibDTOList;

}