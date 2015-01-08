package yuanlai.filestorage.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import yuanlai.filestorage.service.PhotoStorageServerConfig;
import yuanlai.filestorage.service.UserPhotoStorage;
import yuanlai.filestorage.utils.UserPhotoUriParser;

public class PrivateUserPhotoServlet extends ImageServlet {

	private static final long serialVersionUID = -8782887508314786439L;

	private UserPhotoStorage userPhotoStorage;

	private PhotoStorageServerConfig config;

	@Override
	protected byte[] generateImage(HttpServletRequest request) throws IOException {
		// http://10.10.10.9/r/17/12ca04011ab.jpg
		// http://10.10.10.9/r/17/12ca04011ab-10.jpg
		UserPhotoUriParser uriParser = new UserPhotoUriParser(request.getRequestURI());

		// TODO ����ip����

		if (uriParser.getUserId() <= 0 || uriParser.getPhotoName() == null) {
			return null;
		} else if (uriParser.getImageSizeId() != null
				&& config.existsUserPhotoSize(uriParser.getImageSizeId()) == false) {
			return null;
		}
		return userPhotoStorage.getPrivate(uriParser.getUserId(), uriParser.getPhotoName(), uriParser.getImageSizeId());
	}

	public void setUserPhotoStorage(UserPhotoStorage userPhotoStorage) {
		this.userPhotoStorage = userPhotoStorage;
	}

	public void setPhotoStorageServerConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}
}
