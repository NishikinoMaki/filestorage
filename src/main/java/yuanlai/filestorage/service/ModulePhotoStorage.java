package yuanlai.filestorage.service;

import java.io.IOException;

public interface ModulePhotoStorage {

	/**
	 * ����ͼƬ��ָ�����ļ���
	 * 
	 * @param bytes
	 *            ͼƬ����������
	 * @param module
	 *            ָ����ģ��
	 * @return ͼƬ·��
	 * @throws IOException
	 */
	public String save(String module, byte[] bytes) throws Exception;

	public byte[] get(String module, String path) throws IOException;

	/**
	 * ȡ������ͼ��File���������ͼ�����ڣ���ô����һ��
	 * 
	 * @param moduleName
	 *            ģ����
	 * @param subFolder
	 *            ���ļ�����
	 * @param name
	 *            ͼƬ
	 * @param thumbName
	 *            ����ͼ��
	 * @param width
	 *            ����ͼ���
	 * @param height
	 *            ����ͼ�߶�
	 * @return
	 * @throws IOException
	 */
	public byte[] get(String module, String path, String imageSizeId)
			throws IOException;

}
