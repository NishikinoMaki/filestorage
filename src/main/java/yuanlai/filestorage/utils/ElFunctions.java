package yuanlai.filestorage.utils;

import org.apache.commons.lang3.StringUtils;

import yuanlai.filestorage.service.PhotoStorageClientConfig;

public class ElFunctions {

	private static PhotoStorageClientConfig config;

	public static void setConfig(PhotoStorageClientConfig config) {
		if (ElFunctions.config == null) {
			ElFunctions.config = config;
		} else {
			throw new RuntimeException("ElFunctions config has already set. It can be set only once.");
		}
	}

	private ElFunctions() {
	}

	private static String getUserPhotoServerIndex(int userId, int imageSizeId) {
		int serverIndex = PhotoStorageUtils.choosePhotoStorageServer(config, userId);
		return imageSizeId > 0 ? config.getUserPhotoUrl(serverIndex) : config.getUserPhotoPrivateUrl(serverIndex);
	}
	private static String getAppUserPhotoServerIndex(int userId, int imageSizeId) {
		int serverIndex = PhotoStorageUtils.choosePhotoStorageServer(config, userId);
		return imageSizeId > 0 ? config.getUserPhotoAppUrl(serverIndex) : config.getUserPhotoAppPrivateUrl(serverIndex);
	}
	public static String getUserPhotoUrl(int userId, String photoName, int gender, int imageSizeId) {
		int absImageSizeId = Math.abs(imageSizeId);
		if (userId <= 0 || StringUtils.isEmpty(photoName)) {
			return getUserPhotoServerIndex(0, imageSizeId) + "/default/" + gender + "-" + absImageSizeId + ".jpg";
		}
		if("normal".equals(photoName)){
			
			if(absImageSizeId==10||absImageSizeId==30||absImageSizeId==40||absImageSizeId==57){
				return "http://www.ylstatic.com/web/images/public/default/default-normal-"+gender+"-"+absImageSizeId+".jpg";
			}
			else{
				return getUserPhotoServerIndex(0, imageSizeId) + "/default/" + gender + "-" + absImageSizeId + ".jpg";
			}
		}
		if (absImageSizeId != 0) {
			photoName = PhotoStorageUtils.getPhotoNameWithSize(photoName, String.valueOf(absImageSizeId));
		}
		return getUserPhotoServerIndex(userId, imageSizeId) + "/" + Integer.toHexString(userId) + "/" + photoName;
	}
	
	public static String getAppUserPhotoUrl(int userId, String photoName, int gender, int imageSizeId) {
		int absImageSizeId = Math.abs(imageSizeId);
		if (userId <= 0 || StringUtils.isEmpty(photoName)) {
			return getAppUserPhotoServerIndex(0, imageSizeId) + "/default/" + gender + "-" + absImageSizeId + ".jpg";
		}
		if (absImageSizeId != 0) {
			photoName = PhotoStorageUtils.getPhotoNameWithSize(photoName, String.valueOf(absImageSizeId));
		}
		return getAppUserPhotoServerIndex(userId, imageSizeId) + "/" + Integer.toHexString(userId) + "/" + photoName;
	}

	public static String getModulePhotoUrl(String module, String path, int imageSizeId) {
		if (StringUtils.isEmpty(module) || StringUtils.isEmpty(path)) {
			return config.getModulePhotoUrl(module);
		}
		if (imageSizeId > 0) {
			path = PhotoStorageUtils.getPhotoNameWithSize(path, String.valueOf(imageSizeId));
		}
		return config.getModulePhotoUrl(module) + "/" + module + "/" + path;
	}
}
