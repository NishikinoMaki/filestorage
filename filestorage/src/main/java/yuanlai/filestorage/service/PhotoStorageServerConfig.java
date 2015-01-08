package yuanlai.filestorage.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

public class PhotoStorageServerConfig implements InitializingBean, ServletContextAware {

	private final static String DEFAULT_AVATAR_USER_PHOTO_PREFIX = "photostorage.userphoto.default.avatar.";
	private final static String DEFAULT_NORMAL_USER_PHOTO_PREFIX = "photostorage.userphoto.default.normal.";
	private final static String USER_PHOTO_SIZE_PREFIX = "photostorage.userphoto.size.";
	private final static String MODULE_PHOTO_SIZE_PREFIX = "photostorage.modulephoto.";

	private final static String RESOURCE_FOLDER = "/WEB-INF/resources/";

	private Resource properties;

	private File fileRoot;
	private File userFolder;
	private File moduleFolder;
	private File appUserFolder;
	private File tempFolder;
	private boolean cacheDisk;
	private File cacheFolder;
	private Map<String, UserPhotoImageSize> userPhotoSizes;
	private Map<String, Map<String, ImageSize>> modulePhotoSizes;
	private Map<String, File> defaultAvatarUserPhotos;
	private Map<String, File> defaultNormalUserPhotos;

	private Date loadTime;
	private ServletContext servletContext;

	private static class ImageSize {
		public int width;
		public int height;
		public boolean crop;
		public int cropPosition;
		public boolean upscale;
		public BufferedImage watermark;
		public int watermarkPosition = 2;
		public int watermarkOffsetX;
		public int watermarkOffsetY;
	}

	private static class UserPhotoImageSize extends ImageSize {
		public boolean avatar = false;
	}

	public void setProperties(Resource properties) throws IOException {
		this.properties = properties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reload();
	}

	public void reload() throws Exception {
		Properties properties = new Properties();
		properties.load(this.properties.getInputStream());
		parseConfig(properties);

		userFolder = new File(fileRoot, "user");
		moduleFolder = new File(fileRoot, "module");
		tempFolder = new File(fileRoot, "temp");
		appUserFolder = new File(fileRoot , "appUser");
		if (cacheDisk) {
			cacheFolder = new File(fileRoot, "cache");
		}

		loadTime = new Date();
	}

	private void parseConfig(Properties properties) throws Exception {
		String fileRootValue = properties.getProperty("photostorage.fileroot");
		if (StringUtils.isEmpty(fileRootValue)) {
			throw new Exception("Property " + fileRootValue + " is empty.");
		} else {
			fileRoot = new File(fileRootValue);
			if (fileRoot.exists() == false) {
				throw new Exception("File root " + fileRootValue + " dose not exist.");
			}
		}
		cacheDisk = BooleanUtils.toBoolean(properties.getProperty("photostorage.cache.disk", "false"));

		Map<String, File> defaultAvatarUserPhotos = new HashMap<String, File>();
		Map<String, File> defaultNormalUserPhotos = new HashMap<String, File>();
		Map<String, UserPhotoImageSize> userPhotoSizes = new HashMap<String, UserPhotoImageSize>();
		Map<String, Map<String, ImageSize>> modulePhotoSizes = new HashMap<String, Map<String, ImageSize>>();
		for (String propertyName : properties.stringPropertyNames()) {
			parseDefaultAvatarUserPhoto(defaultAvatarUserPhotos, properties, propertyName);
			parseDefaultNormalUserPhoto(defaultNormalUserPhotos, properties, propertyName);
			parseUserPhotoSizes(userPhotoSizes, properties, propertyName);
			parseModulePhotoSizes(modulePhotoSizes, properties, propertyName);
		}
		this.defaultAvatarUserPhotos = defaultAvatarUserPhotos;
		this.defaultNormalUserPhotos = defaultNormalUserPhotos;
		this.userPhotoSizes = userPhotoSizes;
		this.modulePhotoSizes = modulePhotoSizes;
	}

	private void parseDefaultAvatarUserPhoto(Map<String, File> defaultAvatarUserPhotos, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, DEFAULT_AVATAR_USER_PHOTO_PREFIX)) {
			String defaultPhotoId = StringUtils.substringAfter(propertyName, DEFAULT_AVATAR_USER_PHOTO_PREFIX);
			String defaultPhotoName = properties.getProperty(propertyName);
			File defaultPhoto = new File(servletContext.getRealPath(RESOURCE_FOLDER + defaultPhotoName));
			if (defaultPhoto.exists() == false) {
				throw new RuntimeException("Default user photo " + defaultPhotoName + " dose not exist.");
			}
			defaultAvatarUserPhotos.put(defaultPhotoId, defaultPhoto);
		}
	}

	private void parseDefaultNormalUserPhoto(Map<String, File> defaultNormalUserPhotos, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, DEFAULT_NORMAL_USER_PHOTO_PREFIX)) {
			String defaultPhotoId = StringUtils.substringAfter(propertyName, DEFAULT_NORMAL_USER_PHOTO_PREFIX);
			String defaultPhotoName = properties.getProperty(propertyName);
			File defaultPhoto = new File(servletContext.getRealPath(RESOURCE_FOLDER + defaultPhotoName));
			if (defaultPhoto.exists() == false) {
				throw new RuntimeException("Default user photo " + defaultPhotoName + " dose not exist.");
			}
			defaultNormalUserPhotos.put(defaultPhotoId, defaultPhoto);
		}
	}

	private void parseUserPhotoSizes(Map<String, UserPhotoImageSize> userPhotoSizes, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, USER_PHOTO_SIZE_PREFIX)) {
			String sizeProperty = StringUtils.substringAfter(propertyName, USER_PHOTO_SIZE_PREFIX);
			String sizeId = null;
			String sizePropertyName = null;
			if (StringUtils.isNumeric(sizeProperty)) {
				sizeId = sizeProperty;
				sizePropertyName = "size";
			} else if (StringUtils.contains(sizeProperty, '.')) {
				String[] split = StringUtils.split(sizeProperty, '.');
				if (split.length < 2) {
					throw new RuntimeException("error config " + propertyName);
				}
				sizeId = split[0];
				sizePropertyName = split[1];
			}
			if (sizeId != null) {
				UserPhotoImageSize imageSize = userPhotoSizes.get(sizeId);
				if (imageSize == null) {
					imageSize = new UserPhotoImageSize();
					userPhotoSizes.put(sizeId, imageSize);
				}
				fillImageSizeConfig(imageSize, properties, propertyName, sizePropertyName);
			}
		}
	}

	private void parseModulePhotoSizes(Map<String, Map<String, ImageSize>> modulePhotoSizes, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, MODULE_PHOTO_SIZE_PREFIX)) {
			String[] sizeProperty = StringUtils.split(
					StringUtils.substringAfter(propertyName, MODULE_PHOTO_SIZE_PREFIX), '.');// moduleName.size.10.xxx=yyy
			if (sizeProperty.length < 3 || StringUtils.equals("size", sizeProperty[1]) == false
					|| StringUtils.isNumeric(sizeProperty[2]) == false) {
				throw new RuntimeException("error config " + propertyName);
			}
			String module = sizeProperty[0];
			String sizeId = sizeProperty[2];
			String sizePropertyName = sizeProperty.length >= 4 ? sizeProperty[3] : "size";
			if (module != null && sizeId != null && sizePropertyName != null) {
				Map<String, ImageSize> sizes = modulePhotoSizes.get(module);
				if (sizes == null) {
					sizes = new HashMap<String, ImageSize>();
					modulePhotoSizes.put(module, sizes);
				}
				ImageSize imageSize = sizes.get(sizeId);
				if (imageSize == null) {
					imageSize = new ImageSize();
					sizes.put(sizeId, imageSize);
				}
				fillImageSizeConfig(imageSize, properties, propertyName, sizePropertyName);
			}
		}
	}

	private void fillImageSizeConfig(ImageSize imageSize, Properties properties, String propertyName, String name) {
		String value = StringUtils.trim(StringUtils.lowerCase(properties.getProperty(propertyName)));
		if (StringUtils.equals("size", name)) {
			String[] split = StringUtils.split(value, 'x');
			if (split.length < 2) {
				throw new RuntimeException("error config " + propertyName);
			} else if ((StringUtils.isNumeric(split[0]) == false && "?".equals(split[0]) == false)
					|| (StringUtils.isNumeric(split[1]) == false && "?".equals(split[1]) == false)) {
				throw new RuntimeException("error config " + propertyName);
			}
			imageSize.width = NumberUtils.toInt(split[0]);
			imageSize.height = NumberUtils.toInt(split[1]);
		} else if (StringUtils.equals("crop", name)) {
			imageSize.crop = BooleanUtils.toBoolean(value);
		} else if (imageSize instanceof UserPhotoImageSize && StringUtils.equals("avatar", name)) {
			((UserPhotoImageSize) imageSize).avatar = BooleanUtils.toBoolean(value);
		} else if (StringUtils.equals("cropPosition", name)) {
			if (StringUtils.equals("top", value)) {
				imageSize.cropPosition = 1;
			} else if (StringUtils.equals("center", value)) {
				imageSize.cropPosition = 0;
			} else if (StringUtils.equals("bottom", value)) {
				imageSize.cropPosition = -1;
			}
		} else if (StringUtils.equals("upscale", name)) {
			imageSize.upscale = BooleanUtils.toBoolean(value);
		} else if (StringUtils.equals("watermark", name)) {
			try {
				String location = WebUtils.getRealPath(servletContext, RESOURCE_FOLDER + value);
				imageSize.watermark = ImageIO.read(new File(location));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("the watermark image dose not exist", e);
			} catch (IOException e) {
				throw new RuntimeException("an error occurred when reading the watermark image", e);
			}
		} else if (StringUtils.equals("watermarkPosition", name)) {
			if (StringUtils.equals("center", value)) {
				imageSize.watermarkPosition = 0;
			} else if (StringUtils.equals("topright", value)) {
				imageSize.watermarkPosition = 1;
			} else if (StringUtils.equals("bottomright", value)) {
				imageSize.watermarkPosition = 2;
			} else if (StringUtils.equals("bottomleft", value)) {
				imageSize.watermarkPosition = 3;
			} else if (StringUtils.equals("topleft", value)) {
				imageSize.watermarkPosition = 4;
			}
		} else if (StringUtils.equals("watermarkOffset", name)) {
			String[] split = StringUtils.split(value, ',');
			if (split.length < 2) {
				throw new RuntimeException("error config " + propertyName);
			} else if (StringUtils.isNumeric(StringUtils.stripStart(split[0], "-")) == false
					|| StringUtils.isNumeric(StringUtils.stripStart(split[1], "-")) == false) {
				throw new RuntimeException("error config " + propertyName);
			}
			imageSize.watermarkOffsetX = NumberUtils.toInt(split[0]);
			imageSize.watermarkOffsetY = NumberUtils.toInt(split[1]);
		}
	}

	public File getFileRoot() {
		return fileRoot;
	}

	public boolean isCacheDisk() {
		return cacheDisk;
	}

	public File getDefaultAvatarUserPhoto(String defaultAvatarPhotoId) {
		return defaultAvatarUserPhotos.get(defaultAvatarPhotoId);
	}

	public File getDefaultNormalUserPhoto(String defaultNormalPhotoId) {
		return defaultNormalUserPhotos.get(defaultNormalPhotoId);
	}

	public boolean existsUserPhotoSize(String sizeId) {
		return userPhotoSizes.containsKey(sizeId);
	}

	public int getUserPhotoWidth(String sizeId) {
		return userPhotoSizes.get(sizeId).width;
	}

	public int getUserPhotoHeight(String sizeId) {
		return userPhotoSizes.get(sizeId).height;
	}

	public boolean isUserPhotoAvatar(String sizeId) {
		return userPhotoSizes.get(sizeId).avatar;
	}

	public boolean isUserPhotoCrop(String sizeId) {
		return userPhotoSizes.get(sizeId).crop;
	}

	public int getUserPhotoCropPosition(String sizeId) {
		return userPhotoSizes.get(sizeId).cropPosition;
	}

	public boolean isUserPhotoUpscale(String sizeId) {
		return userPhotoSizes.get(sizeId).upscale;
	}

	public BufferedImage getUserPhotoWatermark(String sizeId) {
		return userPhotoSizes.get(sizeId).watermark;
	}

	public int getUserPhotoWatermarkPosition(String sizeId) {
		return userPhotoSizes.get(sizeId).watermarkPosition;
	}

	public int getUserPhotoWatermarkOffsetX(String sizeId) {
		return userPhotoSizes.get(sizeId).watermarkOffsetX;
	}

	public int getUserPhotoWatermarkOffsetY(String sizeId) {
		return userPhotoSizes.get(sizeId).watermarkOffsetY;
	}

	public boolean existsModulePhotoSize(String module, String sizeId) {
		return modulePhotoSizes.get(module).containsKey(sizeId);
	}

	public int getModulePhotoWidth(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).width;
	}

	public int getModulePhotoHeight(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).height;
	}

	public boolean isModulePhotoCrop(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).crop;
	}

	public int getModulePhotoCropPosition(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).cropPosition;
	}

	public boolean isModulePhotoUpscale(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).upscale;
	}

	public BufferedImage getModulePhotoWatermark(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).watermark;
	}

	public int getModulePhotoWatermarkPosition(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).watermarkPosition;
	}

	public int getModulePhotoWatermarkOffsetX(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).watermarkOffsetX;
	}

	public int getModulePhotoWatermarkOffsetY(String module, String sizeId) {
		return modulePhotoSizes.get(module).get(sizeId).watermarkOffsetY;
	}

	public File getUserFolder() {
		return userFolder;
	}

	public File getModuleFolder() {
		return moduleFolder;
	}

	public File getTempFolder() {
		return tempFolder;
	}

	public File getCacheFolder() {
		return cacheFolder;
	}

	public Date getLoadTime() {
		return loadTime;
	}

	public File getAppUserFolder() {
		return appUserFolder;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
