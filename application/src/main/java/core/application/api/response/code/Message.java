package core.application.api.response.code;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
	private String message;

	public static Message createMessage(String message) {
		return Message.builder().message(message).build();
	}
}
