package yuanlai.filestorage.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import yuanlai.filestorage.utils.ImageResizer;
import yuanlai.filestorage.utils.PhotoStorageUtils;

public class ModulePhotoStorageImpl implements ModulePhotoStorage {

	private PhotoStorageServerConfig config;

	public void setConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}

	@Override
	public String save(String module, byte[] bytes) throws Exception {
		if (StringUtils.isEmpty(module)) {
			throw new Exception("ģ��������Ϊ��");
		} else if (StringUtils.isAlphanumeric(module) == false) {
			throw new Exception("ģ����ֻ�������ֻ���ĸ");
		} else if (Character.isLowerCase(module.charAt(0)) == false) {
			throw new Exception("ģ������������ĸСд");
		}
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		String subFolder = format.format(new Date());
		File folder = PhotoStorageUtils.getModuleSubFolder(config.getModuleFolder(), module, subFolder);
		File file = PhotoStorageUtils.getNewPhotoFile(folder);
		FileUtils.writeByteArrayToFile(file, bytes);
		return subFolder + "/" + file.getName();
	}

	@Override
	public byte[] get(String module, String path) throws IOException {
		File sourceFile = new File(config.getModuleFolder(), module + "/" + path);
		if (sourceFile.exists() == false) {
			throw new FileNotFoundException();
		}
		return FileUtils.readFileToByteArray(sourceFile);
	}

	@Override
	public byte[] get(String module, String path, String imageSizeId) throws IOException {
		File sourceFile = new File(config.getModuleFolder(), module + "/" + path);
		File cacheFolder = null;
		File cacheFile = null;
		if (config.isCacheDisk()) {
			cacheFolder = PhotoStorageUtils.getModuleSubFolder(config.getCacheFolder(), module, path);
			cacheFile = new File(cacheFolder, sourceFile.getName());
			if (cacheFile.exists()) {
				return FileUtils.readFileToByteArray(cacheFile);
			}
		}

		if (sourceFile.exists() == false) {
			return null;
		}
		BufferedImage sourceImage = ImageIO.read(sourceFile);

		ImageResizer resizer = new ImageResizer(sourceImage);
		resizer.setTargetSize(config.getModulePhotoWidth(module, imageSizeId),
				config.getModulePhotoHeight(module, imageSizeId));
		resizer.setCrop(config.isModulePhotoCrop(module, imageSizeId));
		resizer.setCropPosition(config.getModulePhotoCropPosition(module, imageSizeId));
		resizer.setUpscale(config.isModulePhotoUpscale(module, imageSizeId));
		resizer.setWatermark(config.getModulePhotoWatermark(module, imageSizeId));
		resizer.setWatermarkPosition(config.getModulePhotoWatermarkPosition(module, imageSizeId));
		resizer.setWatermarkOffset(config.getModulePhotoWatermarkOffsetX(module, imageSizeId),
				config.getModulePhotoWatermarkOffsetY(module, imageSizeId));

		BufferedImage destinationImage = resizer.resize();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(destinationImage, "jpg", out);
		byte[] result = out.toByteArray();
		if (config.isCacheDisk()) {
			if (cacheFolder.exists() == false) {
				cacheFolder.mkdirs();
			}
			FileUtils.writeByteArrayToFile(cacheFile, result);
		}
		return result;
	}
}
