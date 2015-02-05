package yuanlai.filestorage.service;

import java.io.IOException;

public interface TempPhotoStorage {

	/**
	 * ������ʱͼƬ���Զ������ļ���
	 * 
	 * @param bytes
	 *            ͼƬ����������
	 * @return ͼƬ����������·����
	 * @throws IOException
	 */
	public String save(byte[] bytes) throws IOException;

	/**
	 * ������ʱͼƬ��ʹ��ָ�����ļ������棬�ļ����32���ַ���ֻ�������ֺ���ĸ
	 * 
	 * @param bytes
	 *            ͼƬ����������
	 * @param baseName
	 *            ָ��ͼƬ������������׺
	 * @return ͼƬ����������·����
	 * @throws IOException
	 */
	public String save(byte[] bytes, String baseName) throws IOException;

	/**
	 * ����ͼƬ���ж�ͼƬ�Ƿ����
	 * 
	 * @param nameOrBaseName
	 * @return �Ƿ����
	 */
	public boolean exists(String nameOrBaseName);

	/**
	 * ɾ����ʱͼƬ
	 * 
	 * @param name
	 *            ͼƬ��
	 */
	public void delete(String name);

	/**
	 * ��ȡԭͼ�ߴ�
	 * 
	 * @param name
	 * @return ����һ�����飬�����0������width�������1������height
	 * @throws IOException
	 */
	public int[] getSize(String name) throws IOException;

	/**
	 * ��ȡԭͼ����������
	 * 
	 * @param name
	 * @return ����ļ������ڣ��᷵��null
	 * @throws IOException
	 */
	public byte[] get(String name) throws IOException;
}