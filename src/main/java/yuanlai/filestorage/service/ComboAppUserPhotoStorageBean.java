package yuanlai.filestorage.service;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import yuanlai.filestorage.utils.PhotoStorageUtils;

public class ComboAppUserPhotoStorageBean implements AppUserPhotoStorage, InitializingBean {

	private PhotoStorageClientConfig config;
	private boolean chunkedPost;

	private Map<Integer, UserPhotoStorage> services;

	@Override
	public void afterPropertiesSet() throws Exception {
		services = new Hashtable<Integer, UserPhotoStorage>(config.getUserPhotoServerCount());
		for (int i = 1; i <= config.getUserPhotoServerCount(); i++) {
			services.put(i, initService(config.getUserPhotoHessian(i)));
		}
	}

	private UserPhotoStorage initService(String serviceUrl) {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl(serviceUrl);
		factory.setServiceInterface(UserPhotoStorage.class);
		factory.setOverloadEnabled(true);
		factory.setChunkedPost(chunkedPost);
		factory.afterPropertiesSet();
		return (UserPhotoStorage) factory.getObject();
	}

	private UserPhotoStorage getService(int userId) {
		int serverIndex = PhotoStorageUtils.choosePhotoStorageServer(config, userId);
		return services.get(serverIndex);
	}

	public void setChunkedPost(boolean chunkedPost) {
		this.chunkedPost = chunkedPost;
	}

	@Override
	public String save(byte[] bytes, int userId) throws IOException {
		return getService(userId).save(bytes, userId);
	}

	@Override
	public void save(byte[] bytes, int userId, String name) throws IOException {
		getService(userId).save(bytes, userId, name);
	}

	@Override
	public String cut(int userId, String name, int x, int y, int w, int h, int rotate) throws IOException {
		return getService(userId).cut(userId, name, x, y, w, h, rotate);
	}

	@Override
	public String cutAvatar(int userId, String name, int x, int y, int w, int h) throws IOException {
		return getService(userId).cutAvatar(userId, name, x, y, w, h);
	}

	@Override
	public boolean hasAvatar(int userId, String name) {
		return getService(userId).hasAvatar(userId, name);
	}

	@Override
	public String deleteAvatar(int userId, String name) throws IOException {
		return getService(userId).deleteAvatar(userId, name);
	}

	@Override
	public String[] list(int userId) {
		return getService(userId).list(userId);
	}

	@Override
	public void delete(int userId, String name) {
		getService(userId).delete(userId, name);
	}

	@Override
	public byte[] getPrivate(int userId, String name, String imageSizeId) throws IOException {
		return getService(userId).get(userId, name, imageSizeId);
	}

	@Override
	public byte[] get(int userId, String name, String imageSizeId) throws IOException {
		return getService(userId).get(userId, name, imageSizeId);
	}

	@Override
	public byte[] getDefault(int userId, String name, String imageSizeId) throws IOException {
		return getService(userId).getDefault(userId, name, imageSizeId);
	}

	@Override
	public int[] getSize(int userId, String name) throws IOException {
		return getService(userId).getSize(userId, name);
	}

	public void setConfig(PhotoStorageClientConfig config) {
		this.config = config;
	}
}
