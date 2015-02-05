package yuanlai.filestorage.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;

import yuanlai.filestorage.utils.ImageCutter;
import yuanlai.filestorage.utils.ImageResizer;
import yuanlai.filestorage.utils.ImageUtils;
import yuanlai.filestorage.utils.PhotoStorageUtils;

public class UserPhotoStorageImpl implements UserPhotoStorage {

	public static int MAX_PHOTO_WIDTH = 1280;
	public static int MAX_PHOTO_HEIGHT = 1280;

	private PhotoStorageServerConfig config;

	private Map<String, byte[]> defaultUserPhotoCache;
	private Date defaultUserPhotoCacheTime;

	public void setConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}

	@Override
	public String save(byte[] bytes, int userId) throws IOException {
		if (userId <= 0) {
			throw new IOException("userId <= 0");
		}
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		if (userFolder.exists() == false) {
			userFolder.mkdirs();
		}
		File file = PhotoStorageUtils.getNewPhotoFile(userFolder);
		doSave(bytes, file);
		return file.getName();
	}

	@Override
	public void save(byte[] bytes, int userId, String name) throws IOException {
		if (userId <= 0) {
			throw new IOException("userId <= 0");
		}
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		if (userFolder.exists() == false) {
			userFolder.mkdirs();
		}
		File file = PhotoStorageUtils.getPhotoFile(userFolder, name);
		doSave(bytes, file);
	}

	private String doSave(byte[] bytes, File file) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		// ����ԭͼ��С
		if (image.getWidth() > MAX_PHOTO_WIDTH || image.getHeight() > MAX_PHOTO_HEIGHT) {
			ImageResizer resizer = new ImageResizer(image);
			resizer.setTargetSize(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT);
			image = resizer.resize();
		}
		ImageUtils.writeImage(image, file, "jpg", 0.95F);
		return file.getName();
	}

	private void cut(int userId, String name, int x, int y, int w, int h, int rotate, File newFile) throws IOException {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File oldFile = PhotoStorageUtils.getPhotoFile(userFolder, name);
		if (oldFile.exists() == false) {
			throw new FileNotFoundException("file " + name + " dose not exist in " + userId + "'s folder");
		}

		BufferedImage oldPhoto = ImageIO.read(oldFile);
		ImageCutter cutter = new ImageCutter(oldPhoto);
		cutter.setRotateDegree(rotate);
		cutter.setPosition(x, y);
		cutter.setTargetSize(w, h);
		BufferedImage newPhoto = cutter.cut();
		ImageUtils.writeImage(newPhoto, newFile, "jpg", 0.95F);
	}

	@Override
	public String cut(int userId, String name, int x, int y, int w, int h, int rotate) throws IOException {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File newPhoto = PhotoStorageUtils.getNewPhotoFile(userFolder);
		File oldAvatar = PhotoStorageUtils.getPhotoFile(userFolder, PhotoStorageUtils.getAvatarName(name));
		if (oldAvatar.exists()) {
			File newAvatar = PhotoStorageUtils.getPhotoFile(userFolder,
					PhotoStorageUtils.getAvatarName(newPhoto.getName()));
			FileUtils.moveFile(oldAvatar, newAvatar);
		}
		cut(userId, name, x, y, w, h, rotate, newPhoto);
		delete(userId, name);
		return newPhoto.getName();
	}

	@Override
	public String cutAvatar(int userId, String name, int x, int y, int w, int h) throws IOException {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File oldPhoto = PhotoStorageUtils.getPhotoFile(userFolder, name);
		if (oldPhoto.exists() == false) {
			throw new FileNotFoundException();
		}
		File newPhoto = PhotoStorageUtils.getNewPhotoFile(userFolder);
		String avatarName = PhotoStorageUtils.getAvatarName(newPhoto.getName());
		File avatarFile = PhotoStorageUtils.getPhotoFile(userFolder, avatarName);
		FileUtils.moveFile(oldPhoto, newPhoto);
		delete(userId, name);
		cut(userId, newPhoto.getName(), x, y, w, h, 0, avatarFile);
		return newPhoto.getName();
	}

	public boolean hasAvatar(int userId, String name) {
		String avatarName = PhotoStorageUtils.getAvatarName(name);
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File avatarFile = PhotoStorageUtils.getPhotoFile(userFolder, avatarName);
		return avatarFile.exists();
	}

	public String deleteAvatar(int userId, String name) throws IOException {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File oldPhoto = PhotoStorageUtils.getPhotoFile(userFolder, name);
		if (oldPhoto.exists() == false) {
			throw new FileNotFoundException();
		}
		File newPhoto = PhotoStorageUtils.getNewPhotoFile(userFolder);
		FileUtils.moveFile(oldPhoto, newPhoto);
		delete(userId, name);
		return newPhoto.getName();
	}

	@Override
	public String[] list(int userId) {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		String[] files = null;
		if (userFolder.isDirectory() && userFolder.exists()) {
			files = userFolder.list();
		}
		if (files != null) {
			Arrays.sort(files, new Comparator<String>() {
				public int compare(String s1, String s2) {
					if (s1.length() == s2.length()) {
						return s1.compareTo(s2);
					} else {
						return s1.length() - s2.length();
					}
				}
			});
		}
		return files;
	}

	@Override
	public void delete(int userId, String name) {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File cacheFolder = PhotoStorageUtils.getUserFolderFile(config.getCacheFolder(), userId);
		File file = PhotoStorageUtils.getPhotoFile(userFolder, name);
		if (file.exists()) {
			file.delete();
		}
		String avatarName = PhotoStorageUtils.getAvatarName(name);
		File avatarFile = PhotoStorageUtils.getPhotoFile(userFolder, avatarName);
		if (avatarFile.exists()) {
			avatarFile.delete();
		}
		if (config.isCacheDisk() && cacheFolder.exists()) {
			String profix = StringUtils.substringBefore(name, ".");
			IOFileFilter fileFilter = new RegexFileFilter("^" + profix + "\\-.+$");
			Collection<?> fl = FileUtils.listFiles(cacheFolder, fileFilter, null);
			Iterator<?> it = fl.iterator();
			while (it.hasNext()) {
				file = (File) it.next();
				if (file.exists() && file.isFile()) {
					file.delete();
				}
			}
		}
	}

	@Override
	public byte[] getPrivate(int userId, String name, String imageSizeId) throws IOException {
		if (StringUtils.isEmpty(imageSizeId) || "0".equals(imageSizeId)) {
			File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
			File sourceFile = PhotoStorageUtils.getPhotoFile(userFolder, name);
			if (sourceFile.exists() == false) {
				throw new FileNotFoundException();
			}
			return FileUtils.readFileToByteArray(sourceFile);
		} else {
			return get(userId, name, imageSizeId, true);
		}
	}

	@Override
	public byte[] get(int userId, String name, String imageSizeId) throws IOException {
		return get(userId, name, imageSizeId, false);
	}

	public byte[] get(int userId, String name, String imageSizeId, boolean forceNoWatermark) throws IOException {
		File userFolder = PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId);
		File cacheFolder = null;
		File cacheFile = null;
		if (config.isCacheDisk()) {
			cacheFolder = PhotoStorageUtils.getUserFolderFile(config.getCacheFolder(), userId);
			cacheFile = PhotoStorageUtils.getPhotoFile(cacheFolder,
					PhotoStorageUtils.getPhotoNameWithSize(name, imageSizeId));
			if (cacheFile.exists()) {
				return FileUtils.readFileToByteArray(cacheFile);
			}
		}
		File sourceFile = PhotoStorageUtils.getPhotoFile(userFolder, name);
		if (config.isUserPhotoAvatar(imageSizeId)) {
			String avatarName = PhotoStorageUtils.getAvatarName(name);
			File avatarFile = PhotoStorageUtils.getPhotoFile(userFolder, avatarName);
			if (avatarFile.exists()) {
				sourceFile = avatarFile;
			}
		}
		if (sourceFile.exists() == false) {
			return null;
		}
		BufferedImage sourceImage = ImageIO.read(sourceFile);

		ImageResizer resizer = new ImageResizer(sourceImage);
		resizer.setTargetSize(config.getUserPhotoWidth(imageSizeId), config.getUserPhotoHeight(imageSizeId));
		resizer.setCrop(config.isUserPhotoCrop(imageSizeId));
		resizer.setCropPosition(config.getUserPhotoCropPosition(imageSizeId));
		resizer.setUpscale(config.isUserPhotoUpscale(imageSizeId));
		if (forceNoWatermark == false) {
			BufferedImage watermark = config.getUserPhotoWatermark(imageSizeId);
			if (watermark != null && watermark.getWidth() * 3 > sourceImage.getWidth()
					&& watermark.getHeight() * 3 > sourceImage.getHeight()) {
				// ���ͼƬС��ˮӡ��3������ô�Ͳ���ˮӡ
			} else {
				resizer.setWatermark(watermark);
				resizer.setWatermarkPosition(config.getUserPhotoWatermarkPosition(imageSizeId));
				resizer.setWatermarkOffset(config.getUserPhotoWatermarkOffsetX(imageSizeId),
						config.getUserPhotoWatermarkOffsetY(imageSizeId));
			}
		}

		BufferedImage destinationImage = resizer.resize();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageUtils.writeImage(destinationImage, out, "jpg", 0.88F);
		byte[] result = out.toByteArray();
		if (config.isCacheDisk()) {
			if (cacheFolder.exists() == false) {
				cacheFolder.mkdirs();
			}
			FileUtils.writeByteArrayToFile(cacheFile, result);
		}
		return result;
	}

	@Override
	public byte[] getDefault(int userId, String name, String imageSizeId) throws IOException {
		if (defaultUserPhotoCacheTime == null || config.getLoadTime().after(defaultUserPhotoCacheTime)) {
			defaultUserPhotoCacheTime = config.getLoadTime();
			defaultUserPhotoCache = new ConcurrentHashMap<String, byte[]>();
		}
		String cacheKey = name + imageSizeId;
		byte[] result = defaultUserPhotoCache.get(cacheKey);
		if (result != null) {
			return result;
		}

		File defaultPhoto = null;
		if (config.isUserPhotoAvatar(imageSizeId)) {
			defaultPhoto = config.getDefaultAvatarUserPhoto(StringUtils.substringBeforeLast(name, "."));
		} else {
			defaultPhoto = config.getDefaultNormalUserPhoto(StringUtils.substringBeforeLast(name, "."));
		}
		BufferedImage sourceImage = ImageIO.read(defaultPhoto);

		ImageResizer resizer = new ImageResizer(sourceImage);
		resizer.setTargetSize(config.getUserPhotoWidth(imageSizeId), config.getUserPhotoHeight(imageSizeId));
		resizer.setCrop(config.isUserPhotoCrop(imageSizeId));
		resizer.setCropPosition(0);
		resizer.setUpscale(true);
		BufferedImage destinationImage = resizer.resize();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageUtils.writeImage(destinationImage, out, "jpg", 0.88F);
		result = out.toByteArray();

		defaultUserPhotoCache.put(cacheKey, result);
		return result;
	}

	@Override
	public int[] getSize(int userId, String name) throws IOException {
		File file = PhotoStorageUtils.getPhotoFile(PhotoStorageUtils.getUserFolderFile(config.getUserFolder(), userId),
				name);
		if (file.exists() == false) {
			return null;
		}
		return ImageUtils.getSize(file);
	}
}
