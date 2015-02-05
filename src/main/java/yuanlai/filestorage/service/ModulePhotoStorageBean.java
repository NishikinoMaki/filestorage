package yuanlai.filestorage.service;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

public class ModulePhotoStorageBean implements ModulePhotoStorage, InitializingBean {

	private PhotoStorageClientConfig config;
	private boolean chunkedPost;

	private Map<String, ModulePhotoStorage> services;

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<String> modules = config.getModules();
		services = new Hashtable<String, ModulePhotoStorage>(modules.size());
		for (String module : modules) {
			services.put(module, initService(config.getModulePhotoHessian(module)));
		}
	}

	private ModulePhotoStorage initService(String serviceUrl) {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl(serviceUrl);
		factory.setServiceInterface(ModulePhotoStorage.class);
		factory.setOverloadEnabled(true);
		factory.setChunkedPost(chunkedPost);
		factory.afterPropertiesSet();
		return (ModulePhotoStorage) factory.getObject();
	}

	public void setChunkedPost(boolean chunkedPost) {
		this.chunkedPost = chunkedPost;
	}

	private ModulePhotoStorage getService(String module) {
		return services.get(module);
	}

	@Override
	public String save(String module, byte[] bytes) throws Exception {
		return getService(module).save(module, bytes);
	}

	@Override
	public byte[] get(String module, String path) throws IOException {
		return getService(module).get(module, path);
	}

	@Override
	public byte[] get(String module, String path, String imageSizeId) throws IOException {
		return getService(module).get(module, path, imageSizeId);
	}

	public void setConfig(PhotoStorageClientConfig config) {
		this.config = config;
	}
}
