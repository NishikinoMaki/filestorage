package yuanlai.filestorage.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageResizer {

	private final BufferedImage source;

	private int sourceWidth;
	private int sourceHeight;

	private int targetWidth;
	private int targetHeight;
	private boolean crop;
	private int cropPosition;
	private boolean upscale;
	private BufferedImage watermark;
	private int watermarkPosition;
	private int watermarkOffsetX;
	private int watermarkOffsetY;

	private int scaledWidth;
	private int scaledHeight;
	private int destinationWidth;
	private int destinationHeight;
	private int positionX;
	private int positionY;
	private BufferedImage destination;

	public ImageResizer(BufferedImage source) {
		this.source = source;
		sourceWidth = source.getWidth();
		sourceHeight = source.getHeight();
	}

	private void computePhotoSize() {
		float widthScale = (float) targetWidth / sourceWidth;
		float heightScale = (float) targetHeight / sourceHeight;

		if ((sourceWidth < targetWidth || sourceHeight < targetHeight)
				&& upscale == false) {
			// ����Ҫ���ŵ����
			if (sourceWidth < targetWidth && sourceHeight < targetHeight) {
				// �ߺͿ���Ҫ��ȡ
				destinationWidth = scaledWidth = sourceWidth;
				destinationHeight = scaledHeight = sourceHeight;
			} else if ((sourceWidth < targetWidth && targetWidth <= 0 && upscale == false)
					|| (sourceHeight < targetHeight && targetWidth <= 0 && upscale == false)) {
				destinationWidth = scaledWidth = sourceWidth;
				destinationHeight = scaledHeight = sourceHeight;
			} else if (sourceWidth < targetWidth) {
				if (crop) {
					// �Ѹ߽ص�
					destinationWidth = scaledWidth = sourceWidth;
					scaledHeight = sourceHeight;
					destinationHeight = targetHeight;
				} else {
					// ���Ÿ߶�
					destinationHeight = scaledHeight = targetHeight;
					destinationWidth = scaledWidth = (int) (sourceWidth * heightScale);
				}
			} else if (sourceHeight < targetHeight) {
				if (crop) {
					// �ѿ�ص�
					destinationWidth = targetWidth;
					scaledWidth = sourceWidth;
					destinationHeight = scaledHeight = sourceHeight;
				} else {
					// ���ſ��
					destinationWidth = scaledWidth = targetWidth;
					destinationHeight = scaledHeight = (int) (sourceHeight * widthScale);
				}
			}
		} else {
			// ��Ҫ���ŵ����
			if (targetWidth > 0 && targetHeight <= 0) {
				// ���ſ�ȣ��߶Ȳ�������
				destinationWidth = scaledWidth = targetWidth;
				destinationHeight = scaledHeight = (int) (sourceHeight * widthScale);
			} else if (targetWidth <= 0 && targetHeight > 0) {
				// ���Ÿ߶ȣ���Ȳ�������
				destinationWidth = scaledWidth = (int) (sourceWidth * heightScale);
				destinationHeight = scaledHeight = targetHeight;
			} else {
				// �߶ȺͿ�ȶ�������
				if (crop == false) {
					// ����Ҫ����
					if (widthScale > heightScale) {
						// ���Ÿ߶�
						destinationHeight = scaledHeight = targetHeight;
						destinationWidth = scaledWidth = (int) (sourceWidth * heightScale);
					} else {
						// ���ſ��
						destinationWidth = scaledWidth = targetWidth;
						destinationHeight = scaledHeight = (int) (sourceHeight * widthScale);
					}
				} else {
					// ��Ҫ����
					if (widthScale > heightScale) {
						// ���ſ��
						destinationWidth = scaledWidth = targetWidth;
						destinationHeight = scaledHeight = (int) (sourceHeight * widthScale);
						if (destinationHeight > targetHeight) {
							destinationHeight = targetHeight;
						}
					} else {
						// ���Ÿ߶�
						destinationHeight = scaledHeight = targetHeight;
						destinationWidth = scaledWidth = (int) (sourceWidth * heightScale);
						if (destinationWidth > targetWidth) {
							destinationWidth = targetWidth;
						}
					}
				}
			}
		}
		if (cropPosition == 0) {
			// ����ͷ���͵ײ�
			positionX = (destinationWidth - scaledWidth) / 2;
			positionY = (destinationHeight - scaledHeight) / 2;
		} else if (cropPosition < 0) {
			// ���еײ�
			positionX = (destinationWidth - scaledWidth) / 2;
			positionY = 0;
		} else if (cropPosition > 0) {
			// ����ͷ��
			positionX = (destinationWidth - scaledWidth) / 2;
			positionY = destinationHeight - scaledHeight;
		}
	}

	public BufferedImage resize() {
		if (destination != null) {
			return destination;
		}
		computePhotoSize();
		destination = new BufferedImage(destinationWidth, destinationHeight,
				BufferedImage.TYPE_INT_BGR);
		Graphics g = destination.createGraphics();
		Image newImage = source;
		// ʹ��Image.SCALE_SMOOTH����Ϊƽ��������
		if (scaledWidth != sourceWidth || scaledHeight != sourceHeight) {
			newImage = source.getScaledInstance(scaledWidth, scaledHeight,
					Image.SCALE_SMOOTH);
		}
		g.drawImage(newImage, positionX, positionY, null);
		addWatermark(g);
		g.dispose();
		return destination;
	}

	/**
	 * ��ˮӡ
	 * 
	 * @param g
	 */
	private void addWatermark(Graphics g) {
		if (watermark != null) {
			int watermarkWidth = watermark.getWidth();
			int watermarkHeight = watermark.getHeight();
			int watermarkPositionX = 0;
			int watermarkPositionY = 0;
			switch (watermarkPosition) {
			case 0:
				// ͼƬ����
				watermarkPositionX = (int) (((float) destinationWidth - watermarkWidth) / 2)
						+ watermarkOffsetX;
				watermarkPositionY = (int) (((float) destinationHeight - watermarkHeight) / 2)
						+ watermarkOffsetY;
				break;
			case 1:
				// ���Ͻ�
				watermarkPositionX = (int) (destinationWidth - watermarkWidth)
						+ watermarkOffsetX;
				watermarkPositionY = watermarkOffsetY;
				break;
			case 3:
				// ���½�
				watermarkPositionX = watermarkOffsetX;
				watermarkPositionY = (int) (destinationHeight - watermarkHeight)
						+ watermarkOffsetY;
				break;
			case 4:
				// ���Ͻ�
				watermarkPositionX = watermarkOffsetX;
				watermarkPositionY = watermarkOffsetY;
				break;
			default:
				// �������0-4���Ǿ���ˮӡλ��Ϊ���½�
				watermarkPositionX = (int) (destinationWidth - watermarkWidth)
						+ watermarkOffsetX;
				watermarkPositionY = (int) (destinationHeight - watermarkHeight)
						+ watermarkOffsetY;
			}
			g.drawImage(watermark, watermarkPositionX, watermarkPositionY, null);
		}
	}

	public void setTargetSize(int targetWidth, int targetHeight) {
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public void setCrop(boolean crop) {
		this.crop = crop;
	}

	public void setCropPosition(int cropPosition) {
		this.cropPosition = cropPosition;
	}

	public void setUpscale(boolean upscale) {
		this.upscale = upscale;
	}

	public void setWatermark(BufferedImage watermark) {
		this.watermark = watermark;
	}

	public void setWatermarkPosition(int watermarkPosition) {
		this.watermarkPosition = watermarkPosition;
	}

	public void setWatermarkOffset(int watermarkOffsetX, int watermarkOffsetY) {
		this.watermarkOffsetX = watermarkOffsetX;
		this.watermarkOffsetY = watermarkOffsetY;
	}
}
