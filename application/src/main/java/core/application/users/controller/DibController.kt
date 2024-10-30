package core.application.users.controller

import core.application.api.response.ApiResponse
import core.application.security.auth.CustomUserDetails
import core.application.users.models.dto.DibRespDTO
import core.application.users.service.DibService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@Tag(name = "Dib", description = "영화 찜 API")
class DibController {
    private val dibService: DibService? = null

    @Operation(summary = "영화 찜 등록/취소")
    @PutMapping("/{movieId}/dib")
    fun dibProcess(
        @PathVariable movieId: String?,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<DibRespDTO?> {
        val userId = userDetails.userId
        val dibRespDTO = dibService!!.dibProcess(userId, movieId)
        return ApiResponse.onSuccess(dibRespDTO)
    }
}
