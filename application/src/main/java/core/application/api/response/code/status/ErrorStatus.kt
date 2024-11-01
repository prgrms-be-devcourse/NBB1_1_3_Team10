package core.application.api.response.code.status

import org.springframework.http.HttpStatus

enum class ErrorStatus(val httpStatus: HttpStatus, val code: String) {
    // Common-related errors
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "COMMON400"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404"),

    // Comment-related errors
    INVALID_REACTION(HttpStatus.BAD_REQUEST, "COMMENT4000"),
    INVALID_WRITE_COMMENT(HttpStatus.BAD_REQUEST, "COMMENT4001"),
    NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST, "COMMENT4002"),
    NOT_COMMENT_WRITER(HttpStatus.BAD_REQUEST, "COMMENT4003"),
    NOT_MATCH_MOVIECOMMENT(HttpStatus.BAD_REQUEST, "COMMENT4004"),

    // Movie-related errors
    NO_SEARCH_RESULT(HttpStatus.BAD_REQUEST, "MOVIE4000"),
    NO_MOVIE(HttpStatus.BAD_REQUEST, "MOVIE4001"),

    // User-related errors
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER4000"),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "USER4001"),

    // Security-related errors
    INVALID_TOKEN_CATEGORY(HttpStatus.BAD_REQUEST, "SECURITY4000"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SECURITY4001"),
    NOT_FOUND_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "SECURITY4002"),
    UNAUTHORIZED_USER(HttpStatus.BAD_REQUEST, "SECURITY4003"),
    NOT_FOUND_VALUE(HttpStatus.BAD_REQUEST, "SECURITY4004"),

    // Review-related errors
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "REVIEW4000"),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "REVIEW4001"),
    NO_REVIEW_COMMENT(HttpStatus.BAD_REQUEST, "REVIEW4002"),
    NO_REVIEW(HttpStatus.BAD_REQUEST, "REVIEW4003"),
    NOT_COMMENT_OWNER(HttpStatus.BAD_REQUEST, "REVIEW4004");
}
