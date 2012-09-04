package de.pictureedit.picture;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

public class Picture {

	private String photograph;
	private String albumname;
	private String date;
	private String photo;
	private String saveFolder;

	private Picture() {
		super();
	}

	public Picture(String albumn, String author, String dat, String phot,
			String save) {
		this();
		this.photograph = author;
		this.albumname = albumn;
		this.date = dat;
		this.photo = phot;
		this.saveFolder = save;
	}

	public void editPicture() {
		try {
			BufferedImage img = ImageIO.read(new File(this.photo));
			String photoName = new File(this.photo).getName();

			Integer imageWidth = img.getWidth();
			Integer imageHeight = img.getHeight();

			Float imageRelationWidth = 0f;
			Float imageRelationHeight = 0f;

			if (imageWidth > imageHeight) {
				imageRelationWidth = 800f / imageWidth;
				imageRelationHeight = 600f / imageHeight;
			} else {
				imageRelationWidth = 600f / imageWidth;
				imageRelationHeight = 800f / imageHeight;
			}

			int wNew = (int) (img.getWidth() * imageRelationWidth), hNew = (int) (img
					.getHeight() * imageRelationHeight) + 20;

			BufferedImage outImg = new BufferedImage(wNew, hNew,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D g = (Graphics2D) outImg.getGraphics();

			g.setColor(Color.WHITE);
			g.drawImage(getScaledInstance(img, wNew, hNew - 20, true), 0, 0,
					null);
			g.setFont(new Font("Verdana", Font.PLAIN, 12));
			g.drawString(this.date + " - " + this.albumname + " : Foto von "
					+ this.photograph, 20, hNew - 6);

			g.dispose();

			this.savePicture(this.saveFolder + "/" + photoName, outImg);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage getScaledInstance(BufferedImage img, int targetWidth,
			int targetHeight, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public void savePicture(String name, BufferedImage outImg) {
		Iterator<ImageWriter> iterator = ImageIO
				.getImageWritersBySuffix("jpeg");
		ImageWriter imageWriter = (ImageWriter) iterator.next();
		JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(
				Locale.getDefault());
		imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
		imageWriteParam.setCompressionQuality(0.8F);
		IIOImage iioImage = new IIOImage(outImg, null, null);
		try {
			imageWriter.setOutput(ImageIO
					.createImageOutputStream(new File(name)));
			imageWriter.write(null, iioImage, imageWriteParam);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			imageWriter.dispose();
		}

	}

}
