package yuanlai.filestorage.utils;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageCutter {

	private BufferedImage source;

	private int rotateDegree;
	private int positionX;
	private int positionY;
	private int targetWidth;
	private int targetHeight;

	private int destinationWidth;
	private int destinationHeight;
	private BufferedImage destination;

	public ImageCutter(BufferedImage source) {
		this.source = source;
	}

	public BufferedImage cut() {
		if (destination != null) {
			return destination;
		}
		// ��ת
		rotate();
		// ����ͼ
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();
		if (positionX < 0 || positionX > sourceWidth) {
			positionX = 0;
		}
		if (positionY < 0 || positionY > sourceHeight) {
			positionY = 0;
		}
		if (positionX + targetWidth > sourceWidth) {
			destinationWidth = sourceWidth - positionX;
		} else {
			destinationWidth = targetWidth;
		}
		if (positionY + targetHeight > sourceHeight) {
			destinationHeight = sourceHeight - positionY;
		} else {
			destinationHeight = targetHeight;
		}

		destination = new BufferedImage((int) destinationWidth,
				(int) destinationHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = destination.getGraphics();
		g.drawImage(source.getSubimage(positionX, positionY, destinationWidth,
				destinationHeight), 0, 0, null);
		return destination;
	}

	private void rotate() {
		if (rotateDegree > 0) {
			int sourceWidth = source.getWidth();
			int sourceHeight = source.getHeight();
			double w = 0;
			double h = 0;
			double x = 0;
			double y = 0;
			double angle = rotateDegree * 0.0174532925;// ���Ƕ�תΪ����

			/**
			 * ȷ����ת���ͼ��ĸ߶ȺͿ��
			 */
			if (rotateDegree == 180 || rotateDegree == 0 || rotateDegree == 360) {
				w = sourceWidth;
				h = sourceHeight;
			} else if (rotateDegree == 90 || rotateDegree == 270) {
				w = sourceHeight;
				h = sourceWidth;
			} else {
				double d = sourceWidth + sourceHeight;
				w = (d * Math.abs(Math.cos(angle)));
				h = (int) (d * Math.abs(Math.sin(angle)));
			}
			x = (w / 2) - (sourceWidth / 2);// ȷ��ԭ������
			y = (h / 2) - (sourceHeight / 2);
			BufferedImage rotatedImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_3BYTE_BGR);
			AffineTransform at = new AffineTransform();
			at.rotate(angle, w / 2, h / 2);// ��תͼ��
			at.translate(x, y);
			AffineTransformOp op = new AffineTransformOp(at,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			op.filter(source, rotatedImage);
			source = rotatedImage;
		}
	}

	public void setTargetSize(int targetWidth, int targetHeight) {
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
	}

	public void setRotateDegree(int rotateDegree) {
		rotateDegree = rotateDegree % 360;
		if (rotateDegree < 0) {
			rotateDegree = 360 + rotateDegree;// ���Ƕ�ת����0-360��֮��
		}
		this.rotateDegree = rotateDegree;
	}

	public void setPosition(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}
}
