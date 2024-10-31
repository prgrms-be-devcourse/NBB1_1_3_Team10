package core.application.security.service

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException

@Component
@Slf4j
class JwtAuthenticationEntryPoint(@param:Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver) :
    AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        JwtAuthenticationEntryPoint.log.info("[ERROR ENTRY POINT]")
        resolver.resolveException(
            request, response, null,
            (request.getAttribute("exception") as Exception)
        )
    }
}
