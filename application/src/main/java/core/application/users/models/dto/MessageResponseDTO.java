package core.application.users.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor

public class MessageResponseDTO {
    private UUID userId;
    private String message;
}
