package core.application.movies.constant;

public enum MovieSearch {
	RANK("RANK,1"),
	RATING("RATING,1"),
	LATEST("prodYear,1");

	public final String SORT;

	MovieSearch(String sort) {
		this.SORT = sort;
	}

	public static boolean isNotValid(String sort) {
		for (MovieSearch value : MovieSearch.values()) {
			if (value.name().equalsIgnoreCase(sort)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isRatingOrder(String sort) {
		return RATING.name().equalsIgnoreCase(sort);
	}
}
