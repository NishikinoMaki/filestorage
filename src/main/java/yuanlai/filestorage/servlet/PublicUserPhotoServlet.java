package yuanlai.filestorage.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import yuanlai.filestorage.service.PhotoStorageServerConfig;
import yuanlai.filestorage.service.UserPhotoStorage;
import yuanlai.filestorage.utils.UserPhotoUriParser;

public class PublicUserPhotoServlet extends ImageServlet {

	private static final long serialVersionUID = -4698199806021565332L;

	private UserPhotoStorage userPhotoStorage;

	private PhotoStorageServerConfig config;

	@Override
	protected byte[] generateImage(HttpServletRequest request)
			throws IOException {
		// http://photo.yuanlai.com/u/104a14/13205142e8e-10.jpg
		UserPhotoUriParser uriParser = new UserPhotoUriParser(
				request.getRequestURI());
		if (uriParser.getUserId() <= 0
				|| uriParser.getPhotoName() == null
				|| config.existsUserPhotoSize(uriParser.getImageSizeId()) == false) {
			if (uriParser.isDefaultPhoto() && uriParser.getPhotoName() != null
					|| config.existsUserPhotoSize(uriParser.getImageSizeId())) {
				return userPhotoStorage.getDefault(uriParser.getUserId(),
						uriParser.getPhotoName(), uriParser.getImageSizeId());
			} else {
				return null;
			}
		}
		return userPhotoStorage.get(uriParser.getUserId(),
				uriParser.getPhotoName(), uriParser.getImageSizeId());
	}

	public void setUserPhotoStorage(UserPhotoStorage userPhotoStorage) {
		this.userPhotoStorage = userPhotoStorage;
	}

	public void setPhotoStorageServerConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}
}
