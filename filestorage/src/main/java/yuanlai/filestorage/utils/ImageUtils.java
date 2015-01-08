package yuanlai.filestorage.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;

public class ImageUtils {

	public boolean isSupportedImage(File file) {
		try {
			ImageIO.read(file);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static int[] getSize(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		return getSize(image);
	}

	public static int[] getSize(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		return new int[] { width, height };
	}

	public static void writeImage(BufferedImage image, OutputStream out,
			String format, float quality) throws IOException {

		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(
				format).next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);// 0.0-1.0
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush();
		writer.dispose();
		try {
			ios.close();
		} catch (IOException e) {
		}
		IOUtils.closeQuietly(out);
	}

	public static void writeImage(BufferedImage image, File file,
			String format, float quality) throws IOException {
		// ���Ŀ¼�������򴴽�
		File folder = file.getParentFile();
		if (folder.exists() == false) {
			folder.mkdirs();
		}
		writeImage(image, new FileOutputStream(file), format, quality);
	}
}
