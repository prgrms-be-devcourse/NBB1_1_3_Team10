package core.application.movies.repositories.movie

import core.application.movies.constant.KmdbParameter
import lombok.RequiredArgsConstructor
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Repository
@RequiredArgsConstructor
class KmdbApiRepository (
    private val webClient: WebClient
){
    @Value("\${kmdb.api.key}")
    private val apiKey: String? = null

    fun getResponse(parameters: MutableMap<KmdbParameter, String?>): JSONObject {
        val response = webClient.get()
            .uri { uriBuilder: UriBuilder ->
                // 기본 파라미터 설정
                uriBuilder.path("/search_json2.jsp")
                    .queryParam("ServiceKey", apiKey)
                    .queryParam("detail", "Y")
                    .queryParam("collection", "kmdb_new2")
                    .queryParam("ratedYn", "Y")
                    .queryParam("listCount", 10)

                // 필요한 파라미터 추가
                parameters.forEach { (request: KmdbParameter?, value: String?) ->
                    uriBuilder.queryParam(request.PARAMETER, value)
                }
                uriBuilder.build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return JSONObject(response)
    }
}
