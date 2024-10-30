package core.application.movies.constant;

public enum CommentSort {
	LATEST,
	LIKE,
	DISLIKE;

	public static Boolean isValid(String value) {
		for (CommentSort sort : CommentSort.values()) {
			if (sort.name().equals(value)) {
				return true;
			}
		}
		return false;
	}
}
