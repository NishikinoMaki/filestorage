package yuanlai.filestorage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModulePhotoUriParser {

	// /m/moduleName/20110927/12ca04011ab.jpg
	// /m/moduleName/20110927/12ca04011ab-10.jpg
	private static final Pattern PATTERN = Pattern
			.compile("/\\w+/(\\w+)/(\\w+)/((\\w+)(\\-?(\\d+))?\\.(\\w+))$");
	private final String uri;

	private final String module;
	private final String subFolder;
	private final String path;
	private final String photoName;
	private final String photoBaseName;
	private final String photoExt;
	private final String imageSizeId;

	public ModulePhotoUriParser(String uri) {
		this.uri = uri;
		Matcher m = PATTERN.matcher(uri);
		if (m.matches()) {
			module = m.group(1);
			subFolder = m.group(2);
			photoBaseName = m.group(4);
			imageSizeId = m.group(6);
			photoExt = m.group(7);
			if (photoBaseName != null && photoExt != null) {
				photoName = photoBaseName + "." + photoExt;
			} else {
				photoName = null;
			}
			if (subFolder != null && photoName != null) {
				path = subFolder + "/" + photoName;
			} else {
				path = null;
			}
		} else {
			module = subFolder = path = imageSizeId = photoName = photoBaseName = photoExt = null;
		}
	}

	public String getUri() {
		return uri;
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

	public String getModule() {
		return module;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public String getPath() {
		return path;
	}

	public static void main(String[] args) {
		String uri = "/m/moduleName/20110927/12ca04011ab-10.jpg";
		ModulePhotoUriParser p = new ModulePhotoUriParser(uri);
		System.out.println("Module=" + p.getModule());
		System.out.println("Path=" + p.getPath());
		System.out.println("PhotoName=" + p.getPhotoName());
		System.out.println("ImageSizeId=" + p.getImageSizeId());
	}
}
