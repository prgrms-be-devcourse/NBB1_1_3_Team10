package core.application.movies.constant;

public enum KmdbParameter {
	QUERY("query"),
	START_COUNT("startCount"),
	MOVIE_ID("movieId"),
	MOVIE_SEQ("movieSeq"),
	GENRE("genre"),
	SORT("sort");

	public final String PARAMETER;

	KmdbParameter(String parameter) {
		this.PARAMETER = parameter;
	}
}
