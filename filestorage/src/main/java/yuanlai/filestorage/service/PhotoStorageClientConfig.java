package yuanlai.filestorage.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import yuanlai.filestorage.utils.ElFunctions;

public class PhotoStorageClientConfig implements InitializingBean {

	private final static String USER_PHOTO_SERVER_PREFIX = "photostorage.userphoto.server.";
	private final static String MODULE_PHOTO_SERVER_PREFIX = "photostorage.modulephoto.";

	private Resource properties;

	private boolean asElFunctionsConfig;
	private Map<Integer, UserPhotoServer> userPhotoServers;
	private Map<String, PhotoServer> modulePhotoServers;

	private Date loadTime;

	private class PhotoServer {
		public String hessian;
		public String url;
	}

	private class UserPhotoServer extends PhotoServer {
		public int minUserId;
		public String privateUrl;
		public String appurl;
		public String appprivateUrl;
	}

	public void setProperties(Resource properties) throws IOException {
		this.properties = properties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reload();
		if (asElFunctionsConfig) {
			ElFunctions.setConfig(this);
		}
	}

	public void reload() throws Exception {
		Properties properties = new Properties();
		properties.load(this.properties.getInputStream());
		parseConfig(properties);

		loadTime = new Date();
	}

	private void parseConfig(Properties properties) throws Exception {
		Map<Integer, UserPhotoServer> userPhotoServers = new HashMap<Integer, UserPhotoServer>();
		Map<String, PhotoServer> modulePhotoServers = new HashMap<String, PhotoServer>();
		for (String propertyName : properties.stringPropertyNames()) {
			parseUserPhotoServer(userPhotoServers, properties, propertyName);
			parseModulePhotoServer(modulePhotoServers, properties, propertyName);
		}
		Integer minServerIndex = Collections.min(userPhotoServers.keySet());
		Integer maxServerIndex = Collections.max(userPhotoServers.keySet());
		if (minServerIndex != 1) {
			throw new RuntimeException("User photo server index error, minServerIndex!=1,current minServerIndex="
					+ minServerIndex);
		}
		if (maxServerIndex != userPhotoServers.size()) {
			throw new RuntimeException("User photo server count error, maxServerIndex!=serverCount,maxServerIndex="
					+ maxServerIndex + ",serverCount=" + userPhotoServers.size());
		}
		this.userPhotoServers = userPhotoServers;
		this.modulePhotoServers = modulePhotoServers;
	}

	private void parseUserPhotoServer(Map<Integer, UserPhotoServer> userPhotoServers, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, USER_PHOTO_SERVER_PREFIX)) {
			String[] splits = StringUtils
					.split(StringUtils.substringAfter(propertyName, USER_PHOTO_SERVER_PREFIX), '.');// 0.xxx
			if (splits.length < 2) {
				throw new RuntimeException("error config " + propertyName);
			}
			if (StringUtils.isNumeric(splits[0])) {
				int serverIndex = NumberUtils.toInt(splits[0]);
				UserPhotoServer userPhotoServer = userPhotoServers.get(serverIndex);
				if (userPhotoServer == null) {
					userPhotoServer = new UserPhotoServer();
					userPhotoServers.put(serverIndex, userPhotoServer);
				}
				fillPhotoServer(userPhotoServer, properties, propertyName, splits[1]);
			}
		}
	}

	private void parseModulePhotoServer(Map<String, PhotoServer> modulePhotoServers, Properties properties,
			String propertyName) {
		if (StringUtils.startsWith(propertyName, MODULE_PHOTO_SERVER_PREFIX)) {
			String[] splits = StringUtils.split(StringUtils.substringAfter(propertyName, MODULE_PHOTO_SERVER_PREFIX),
					'.');// news.xxx
			if (splits.length < 2) {
				throw new RuntimeException("error config " + propertyName);
			}
			if (splits.length >= 2) {
				String module = splits[0];
				PhotoServer photoServer = modulePhotoServers.get(module);
				if (photoServer == null) {
					photoServer = new PhotoServer();
					modulePhotoServers.put(module, photoServer);
				}
				fillPhotoServer(photoServer, properties, propertyName, splits[1]);
			}
		}
	}

	private void fillPhotoServer(PhotoServer server, Properties properties, String propertyName, String name) {
		String value = StringUtils.trim(StringUtils.lowerCase(properties.getProperty(propertyName)));
		if (server instanceof UserPhotoServer && "minUserId".equals(name)) {
			((UserPhotoServer) server).minUserId = NumberUtils.toInt(value);
		} else if ("hessian".equals(name)) {
			server.hessian = value;
		} else if ("url".equals(name)) {
			server.url = value;
		} else if (server instanceof UserPhotoServer && "privateUrl".equals(name)) {
			((UserPhotoServer) server).privateUrl = value;
		}else if(server instanceof UserPhotoServer && "appurl".equals(name)){//����ģ��(appר��)
			((UserPhotoServer) server).appurl = value;
		}else if(server instanceof UserPhotoServer && "appprivateUrl".equals(name)){//����ģ��(appר��)
			((UserPhotoServer) server).appprivateUrl = value;
		}
	}

	public int getUserPhotoServerCount() {
		return userPhotoServers.size();
	}

	public int getUserPhotoMinUserId(int serverIndex) {
		return userPhotoServers.get(serverIndex).minUserId;
	}

	public String getUserPhotoHessian(int serverIndex) {
		return userPhotoServers.get(serverIndex).hessian;
	}

	public String getUserPhotoUrl(int serverIndex) {
		return userPhotoServers.get(serverIndex).url;
	}
	
	public String getUserPhotoAppUrl(int serverIndex) {
		return userPhotoServers.get(serverIndex).appurl;
	}

	public String getUserPhotoAppPrivateUrl(int serverIndex) {
		return userPhotoServers.get(serverIndex).appprivateUrl;
	}

	public String getUserPhotoPrivateUrl(int serverIndex) {
		return userPhotoServers.get(serverIndex).privateUrl;
	}

	public Set<String> getModules() {
		return modulePhotoServers.keySet();
	}

	public String getModulePhotoHessian(String module) {
		return modulePhotoServers.get(module).hessian;
	}

	public String getModulePhotoUrl(String module) {
		return modulePhotoServers.get(module).url;
	}

	public void setAsElFunctionsConfig(boolean asElFunctionsConfig) {
		this.asElFunctionsConfig = asElFunctionsConfig;
	}

	public Date getLoadTime() {
		return loadTime;
	}
}