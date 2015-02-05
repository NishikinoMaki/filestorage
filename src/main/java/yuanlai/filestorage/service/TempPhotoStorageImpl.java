package yuanlai.filestorage.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class TempPhotoStorageImpl implements TempPhotoStorage {

	private PhotoStorageServerConfig config;

	public void setConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}

	private long cleanTime = 1;

	@Override
	public String save(byte[] bytes) throws IOException {
		clean();
		File folder = config.getTempFolder();
		if (folder.exists() == false) {
			folder.mkdir();
		}
		String name = getNewFileName(folder);
		File file = new File(folder, name);
		FileUtils.writeByteArrayToFile(file, bytes);
		return name;
	}

	@Override
	public String save(byte[] bytes, String baseName) throws IOException {
		clean();
		if (StringUtils.isEmpty(baseName)) {
			throw new IllegalArgumentException("baseName can not be empty");
		} else if (StringUtils.isAlphanumeric(baseName) != true) {
			throw new IllegalArgumentException("baseName must be alphanumeric");
		}
		String name = baseName + ".jpg";
		File file = new File(config.getTempFolder(), name);
		FileUtils.writeByteArrayToFile(file, bytes);
		return name;
	}

	private String getNewFileName(File folder) {
		String name;
		long id = System.currentTimeMillis();
		do {
			name = Long.toHexString(id) + ".jpg";
			File file = new File(folder, name);
			if (file.exists()) {
				id++;
			} else {
				break;
			}
		} while (true);
		return name;
	}

	private void clean() {
		final long now = System.currentTimeMillis();
		if (cleanTime > 0) {
			// 5��������һ��
			long fiveMinutes = 10 * 60 * 1000;
			if (now - cleanTime < fiveMinutes) {
				return;
			}
		}
		cleanTime = now;
		// �첽ɾ�����ļ�
		new Thread() {
			@Override
			public void run() {
				final long oneHour = 1 * 60 * 60 * 1000;
				File folder = config.getTempFolder();
				File[] filesToDelete = folder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						long lastModified = file.lastModified();
						// ɾ��1Сʱǰ���ļ�
						return now - lastModified >= oneHour ? true : false;
					}
				});
				for (File f : filesToDelete) {
					try {
						f.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public boolean exists(String nameOrBaseName) {
		if (StringUtils.isEmpty(nameOrBaseName)) {
			throw new IllegalArgumentException("nameOrBaseName can not be empty");
		}
		String name = nameOrBaseName;
		if (StringUtils.contains(name, '.') == false) {
			name = name + ".jpg";
		}
		File file = new File(config.getTempFolder(), name);
		return file.exists();
	}

	@Override
	public void delete(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name can not be empty");
		}
		if (StringUtils.contains(name, '.') == false) {
			name = name + ".jpg";
		}
		File file = new File(config.getTempFolder(), name);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public int[] getSize(String name) throws IOException {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name can not be empty");
		}
		if (StringUtils.contains(name, '.') == false) {
			name = name + ".jpg";
		}
		File file = new File(config.getTempFolder(), name);
		if (file.exists() == false) {
			return null;
		}
		BufferedImage image = ImageIO.read(file);
		int width = image.getWidth();
		int height = image.getHeight();
		return new int[] { width, height };
	}

	@Override
	public byte[] get(String name) throws IOException {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name can not be empty");
		}
		if (StringUtils.contains(name, '.') == false) {
			name = name + ".jpg";
		}
		File file = new File(config.getTempFolder(), name);
		if (file.exists()) {
			return FileUtils.readFileToByteArray(file);
		} else {
			return null;
		}
	}
}
