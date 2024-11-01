package core.application.movies.repositories.movie;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import core.application.movies.constant.KmdbParameter;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KmdbApiRepository {
	@Value("${kmdb.api.key}")
	private String apiKey;

	private final WebClient webClient;

	public JSONObject getResponse(Map<KmdbParameter, String> parameters) {
		String response = webClient.get()
			.uri(uriBuilder -> {
				// 기본 파라미터 설정
				uriBuilder.path("/search_json2.jsp")
					.queryParam("ServiceKey", apiKey)
					.queryParam("detail", "Y")
					.queryParam("collection", "kmdb_new2")
					.queryParam("ratedYn", "Y")
					.queryParam("listCount", 10);

				// 필요한 파라미터 추가
				parameters.forEach((request, value) -> {
					uriBuilder.queryParam(request.PARAMETER, value);
				});

				return uriBuilder.build();
			})
			.retrieve()
			.bodyToMono(String.class)
			.block();

		return new JSONObject(response);
	}
}
