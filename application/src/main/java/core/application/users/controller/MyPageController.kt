package core.application.users.controller

import core.application.api.response.ApiResponse
import core.application.security.auth.CustomUserDetails
import core.application.users.models.dto.MyPageRespDTO
import core.application.users.service.MyPageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "MyPage", description = "마이 페이지 API")
class MyPageController {
    private val myPageService: MyPageService? = null

    @Operation(summary = "유저 마이페이지")
    @GetMapping("/mypage")
    fun getMyPage(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<MyPageRespDTO?> {
        val userId = userDetails.userId
        val myPage = myPageService!!.getMyPage(userId)
        return ApiResponse.onSuccess(myPage)
    }
}
