package core.application.movies.constant;

public enum Genre {
	ACTION("액션"),
	SF("SF"),
	ROMANCE("로맨스"),
	ANIMATION("애니메이션"),
	DRAMA("드라마"),
	THRILLER("스릴러"),
	COMEDY("코메디");

	public final String PARAMETER;

	Genre(String parameter) {
		this.PARAMETER = parameter;
	}

	public static Boolean isNotValid(String parameter) {
		for (Genre value : Genre.values()) {
			if (value.name().equalsIgnoreCase(parameter)) {
				return false;
			}
		}
		return true;
	}
}
