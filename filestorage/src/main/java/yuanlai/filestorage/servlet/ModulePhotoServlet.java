package yuanlai.filestorage.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import yuanlai.filestorage.service.ModulePhotoStorage;
import yuanlai.filestorage.service.PhotoStorageServerConfig;
import yuanlai.filestorage.utils.ModulePhotoUriParser;

public class ModulePhotoServlet extends ImageServlet {

	private static final long serialVersionUID = 2046938748154932939L;

	private ModulePhotoStorage modulePhotoStorage;

	private PhotoStorageServerConfig config;

	public void setModulePhotoStorage(ModulePhotoStorage modulePhotoStorage) {
		this.modulePhotoStorage = modulePhotoStorage;
	}

	public void setPhotoStorageServerConfig(PhotoStorageServerConfig config) {
		this.config = config;
	}

	@Override
	protected byte[] generateImage(HttpServletRequest request)
			throws IOException {
		// http://10.10.11.170/m/moduleName/20110927/12ca04011ab.jpg
		// http://10.10.11.170/m/moduleName/20110927/12ca04011ab-10.jpg
		ModulePhotoUriParser uriParser = new ModulePhotoUriParser(
				request.getRequestURI());
		if (StringUtils.isEmpty(uriParser.getModule())) {
			return null;
		}
		if (config.existsModulePhotoSize(uriParser.getModule(),
				uriParser.getImageSizeId()) == false) {
			if (uriParser.getImageSizeId() == null) {
				return modulePhotoStorage.get(uriParser.getModule(),
						uriParser.getPath());
			} else {
				return null;
			}
		}
		return modulePhotoStorage.get(uriParser.getModule(),
				uriParser.getPath(), uriParser.getImageSizeId());

	}
}
