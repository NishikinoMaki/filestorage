package yuanlai.filestorage.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import yuanlai.filestorage.service.PhotoStorageClientConfig;

public class PhotoStorageUtils {

	private PhotoStorageUtils() {
		
	}

	public static File getModuleSubFolder(File moduleFolder, String module,
			String subFolderOrPath) {
		File subFolder = new File(moduleFolder, module + "/" + subFolderOrPath);
		if (StringUtils.contains(subFolderOrPath, '.')) {
			return subFolder.getParentFile();
		} else {
			return subFolder;
		}
	}

	public static File getNewPhotoFile(File userFolder) {
		String name;
		long id = System.currentTimeMillis();
		do {
			name = Long.toHexString(id) + ".jpg";
			File file = new File(userFolder, name);
			if (file.exists()) {
				id++;
			} else {
				return file;
			}
		} while (true);
	}

	public static String getUserFolder(int userId) {
		/*
		 * ���û�idת����16���Ƹ�ʽ����3��Ŀ¼���� ��id=350076��Ӧ��16���Ƹ�ʽ��5577c��Ӧ��Ŀ¼��/05/57/7c/
		 */
		final String hex = StringUtils.leftPad(Integer.toHexString(userId), 6,
				'0');
		final String folder1 = hex.substring(0, hex.length() - 4);
		final String folder2 = hex
				.substring(hex.length() - 4, hex.length() - 2);
		final String folder3 = hex.substring(hex.length() - 2);
		return folder1 + "/" + folder2 + "/" + folder3 + "/";
	}

	public static File getUserFolderFile(File parent, int userId) {
		return new File(parent, getUserFolder(userId));
	}

	public static File getPhotoFile(File userFolder, String name) {
		return new File(userFolder, name);
	}

	public static String getAvatarName(String photoName) {
		return StringUtils.substringBeforeLast(photoName, ".") + "-."
				+ StringUtils.substringAfterLast(photoName, ".");
	}

	public static String getPhotoNameWithSize(String photoName, String sizeId) {
		if (StringUtils.contains(photoName, '.') == false) {
			return photoName + "-" + sizeId;
		} else {
			return StringUtils.substringBeforeLast(photoName, ".") + "-"
					+ sizeId + "."
					+ StringUtils.substringAfterLast(photoName, ".");
		}
	}

	public static int choosePhotoStorageServer(PhotoStorageClientConfig config,
			int userId) {
		int serverIndex = 1;
		/**
		 * ����1�����������ʹ�÷�����
		 */
		// for (int i = 1; i <= config.getUserPhotoServerCount(); i++) {
		// if (userId < config.getUserPhotoMinUserId(i)) {
		// serverIndex = i - 1;
		// break;
		// }
		// }
		// if (serverIndex <= 0) {
		// serverIndex = 1;
		// }
		/**
		 * ����2��ȡģƽ��ʹ�÷�����
		 */
		serverIndex = userId % config.getUserPhotoServerCount() + 1;
		return serverIndex;
	}
}
