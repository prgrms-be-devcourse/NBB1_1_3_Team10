package core.application.users.models.dto

import core.application.users.models.entities.UserRole

data class MyPageRespDTO (
    /*
       마이페이지 조회에 사용되는 DTO
        * userEmail : 유저 이메일
        * alias : 유저 별명
        * phoneNum : 유저 전화번호
        * role : 유저 역할
       */
    val userEmail: String? = null,
    val alias: String? = null,
    val phoneNum: String? = null,
    val userName: String? = null,
    val role: UserRole? = null,
    val dibDTOList: List<DibDetailRespDTO>? = null
)