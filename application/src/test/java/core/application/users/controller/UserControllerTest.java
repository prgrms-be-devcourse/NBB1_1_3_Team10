package core.application.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.application.security.token.TokenService;
import core.application.users.models.dto.MessageResponseDTO;
import core.application.users.models.dto.SignupReqDTO;
import core.application.users.models.dto.UserUpdateReqDTO;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    SignupReqDTO signupReqDTO;

    UserUpdateReqDTO userUpdateReqDTO;

    UserEntity userEntity;

    MessageResponseDTO messageResDTO;

    HttpServletRequest request;

    @BeforeEach
    public void init() {
        signupReqDTO = new SignupReqDTO(
                "anyone@naver.com",
                "ilive123!123",
                UserRole.USER,
                "anyany",
                "anyone",
                "010-1111-1111"
        );

        userUpdateReqDTO = new UserUpdateReqDTO(
                "anyone@naver.com",
                "ilive123!123",
                "anyany",
                "anyone",
                "010-1111-1111"
        );

        userEntity = new UserEntity(
                null,
                "anyone@naver.com",
                "ilive123!123",
                UserRole.USER,
                "anyone",
                "010-1111-1111",
                "anyany"
        );

        messageResDTO = new MessageResponseDTO(null, "생성에 성공했습니다.");
    }

    @Test
    @DisplayName("로그인")
    public void userController_signin_returnApiResponse() throws Exception {
        ResultActions response = mockMvc.perform(post("/users/signin"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("회원가입")
    public void userController_signup_returnApiResponse() throws Exception {
        when(userService.signup(ArgumentMatchers.any(SignupReqDTO.class))).thenReturn(messageResDTO);

        ResultActions response = mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupReqDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("로그아웃")
    public void userController_signout_returnApiResponse() throws Exception {
        ResultActions response = mockMvc.perform(delete("/users/signout"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 정보 업데이트")
    public void userController_updateUser_returnApiResponse() throws Exception {
        when(userService.updateUserInfo(userUpdateReqDTO)).thenReturn(messageResDTO);

        ResultActions response = mockMvc.perform(patch("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateReqDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 삭제")
    public void userController_deleteUser_returnApiResponse() throws Exception {
        when(userService.updateUserInfo(userUpdateReqDTO)).thenReturn(messageResDTO);

        ResultActions response = mockMvc.perform(patch("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateReqDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("Access Token 재발급")
    public void userController_reissueAT_returnApiResponse() throws Exception {
        when(tokenService.reissueAccessToken(request)).thenReturn("AT");

        ResultActions response = mockMvc.perform(get("/users/reissue"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}