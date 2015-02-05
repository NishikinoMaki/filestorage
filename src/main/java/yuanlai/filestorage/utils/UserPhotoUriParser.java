package yuanlai.filestorage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserPhotoUriParser {

	// /u/1/000000-50.jpg
	// /private/u/1/000000.jpg
	private static final Pattern PATTERN = Pattern
			.compile("(/\\w+)?/\\w+/(\\w+)/((\\w+)(\\-?(\\d+))?\\.(\\w+))$");
	private final String uri;

	private final int userId;

	private final String imageSizeId;
	private final String photoName;
	private final String photoBaseName;
	private final String photoExt;
	private final boolean defaultPhoto;

	public UserPhotoUriParser(String uri) {
		this.uri = uri;
		Matcher m = PATTERN.matcher(uri);
		if (m.matches()) {
			int userId = 0;
			try {
				userId = Integer.parseInt(m.group(2), 16);
			} catch (NumberFormatException e) {
			}
			this.userId = userId;
			imageSizeId = m.group(6);
			photoBaseName = m.group(4);
			photoExt = m.group(7);
			if (photoBaseName != null && photoExt != null) {
				photoName = photoBaseName + "." + photoExt;
			} else {
				photoName = null;
			}
			if (userId == 0 && "default".equals(m.group(2))) {
				defaultPhoto = true;
			} else {
				defaultPhoto = false;
			}
		} else {
			userId = 0;
			imageSizeId = photoName = photoBaseName = photoExt = null;
			defaultPhoto = false;
		}
	}

	public String getUri() {
		return uri;
	}

	public int getUserId() {
		return userId;
	}

	public String getImageSizeId() {
		return imageSizeId;
	}

	public String getPhotoName() {
		return photoName;
	}

	public String getPhotoBaseName() {
		return photoBaseName;
	}

	public String getPhotoExt() {
		return photoExt;
	}

	public boolean isDefaultPhoto() {
		return defaultPhoto;
	}
}
