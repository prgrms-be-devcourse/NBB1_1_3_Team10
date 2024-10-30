package core.application.users.service;

import core.application.users.models.dto.MyPageRespDTO;

import java.util.UUID;

public interface MyPageService {
    /*
     * 사용자의 마이페이지를 조회하는 기능
     * @return{@link MyPageRespnseDTO.MyPageDTO}
     */
    MyPageRespDTO getMyPage(UUID userId);
}
