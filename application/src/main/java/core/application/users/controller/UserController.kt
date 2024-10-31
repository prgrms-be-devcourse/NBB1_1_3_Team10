package core.application.users.controller

import core.application.api.response.ApiResponse
import core.application.api.response.code.Message
import core.application.security.token.TokenService
import core.application.users.models.dto.MessageResponseDTO
import core.application.users.models.dto.SignupReqDTO
import core.application.users.models.dto.UserUpdateReqDTO
import core.application.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 * 사용자 로그인, 회원가입, 로그아웃, 정보 변경 및 삭제 등의 요청을 처리
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "유저 관련 API")
class UserController
/**
 * UserController 생성자.
 *
 * @param userService 사용자 서비스
 * @param tokenService 토큰 서비스
 */ @Autowired constructor(private val userService: UserService, private val tokenService: TokenService) {
    /**
     * 사용자 로그인
     * /users/signin
     */
    @Operation(summary = "로그인")
    @PostMapping("/signin")
    fun login(): ApiResponse<Message> {
        return ApiResponse.onSuccess(Message.createMessage("성공적으로 로그인하였습니다."))
    }

    /**
     * 사용자 회원가입
     * /users/signup
     */
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun singUp(@RequestBody userRequestDTO: @Valid SignupReqDTO): ApiResponse<MessageResponseDTO?> {
        val messageResponseDTO = userService.signup(userRequestDTO)
        return ApiResponse.onCreateSuccess(messageResponseDTO)
    }

    /**
     * 사용자 로그아웃
     * /users/signout
     */
    @Operation(summary = "로그아웃")
    @DeleteMapping("/signout")
    fun logout(): ApiResponse<Message> {
        return ApiResponse.onSuccess(Message.createMessage("성공적으로 로그아웃하였습니다."))
    }

    /**
     * 사용자 정보 변경
     * /users/update
     */
    @Operation(summary = "유저 정보 업데이트")
    @PatchMapping("/update")
    fun updateUser(@RequestBody userUpdateRequestDTO: @Valid UserUpdateReqDTO): ApiResponse<MessageResponseDTO?> {
        val messageResponseDTO = userService.updateUserInfo(userUpdateRequestDTO)
        return ApiResponse.onSuccess(messageResponseDTO)
    }

    /**
     * 사용자 삭제
     * /users/delete
     */
    @Operation(summary = "유저 삭제")
    @DeleteMapping("/delete")
    fun deleteUser(request: HttpServletRequest): ApiResponse<MessageResponseDTO?> {
        val messageResponseDTO = userService.deleteUser()
        if (messageResponseDTO != null) {
            tokenService.inactiveRefreshToken(request)
        }
        return ApiResponse.onDeleteSuccess(messageResponseDTO)
    }

    /**
     * access token 재발급
     * /users/reissue
     */
    @Operation(summary = "Access Token 재발급")
    @GetMapping("/reissue") // 추후 반환 값에 수정
    fun reissueAccessToken(request: HttpServletRequest?, response: HttpServletResponse): ApiResponse<Message> {
        val reissuedAccessToken = tokenService.reissueAccessToken(request)

        if (reissuedAccessToken != null) {
            response.setHeader("accessToken", reissuedAccessToken)
        }
        return ApiResponse.onSuccess(Message.createMessage("Access Token 재발급 완료"))
    }
}
