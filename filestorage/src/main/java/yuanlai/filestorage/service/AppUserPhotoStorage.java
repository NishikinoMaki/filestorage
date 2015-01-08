package yuanlai.filestorage.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface AppUserPhotoStorage {

	/**
	 * ����ͼƬ
	 * 
	 * @param bytes
	 *            ͼƬ����������
	 * @param userId
	 *            �û�id
	 * @return ͼƬ����������·����
	 * @throws IOException
	 */
	public String save(byte[] bytes, int userId) throws IOException;

	/**
	 * ����ͼƬ
	 * 
	 * @param bytes
	 *            ͼƬ����������
	 * @param userId
	 *            �û�id
	 * @return ͼƬ����������·����
	 * @throws IOException
	 */
	public void save(byte[] bytes, int userId, String name) throws IOException;

	/**
	 * 
	 * ����˵���� �������ڣ�2010-12-1,����11:09:50,hyc
	 * 
	 * @param userId
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param rotate
	 * @return
	 * @throws IOException
	 */
	public String cut(int userId, String name, int x, int y, int w, int h, int rotate) throws IOException;

	/**
	 * 
	 * ����˵���� �������ڣ�2010-12-1,����11:09:50,hyc
	 * 
	 * @param userId
	 * @param name
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param rotate
	 * @return
	 * @throws IOException
	 */
	public String cutAvatar(int userId, String name, int x, int y, int w, int h) throws IOException;

	/**
	 * 
	 * ����˵�����ж��Ƿ����й�����ͼ �������ڣ�2010-12-2,����08:05:21,hyc
	 * 
	 * @param userId
	 * @param name
	 * @return
	 */
	public boolean hasAvatar(int userId, String name);

	public String deleteAvatar(int userId, String name) throws IOException;

	/**
	 * �г��û�����Ƭ�б�
	 * 
	 * @param userId
	 *            �û�id
	 * @return �ļ������飬������·��
	 */
	public String[] list(int userId);

	/**
	 * ɾ����Ƭ
	 * 
	 * @param userId
	 *            �û�id
	 * @param name
	 *            ͼƬ��
	 */
	public void delete(int userId, String name);

	/**
	 * ȡ������ͼ��File���������ͼ�����ڣ���ô����һ��
	 * 
	 * @param userId
	 *            �û�id
	 * @param name
	 *            ͼƬ��
	 * @param width
	 *            ����ͼ���
	 * @param height
	 *            ����ͼ�߶�
	 * @param asThumbnail
	 *            �Ƿ�������ͼ
	 * @param resizeMode
	 *            ����ģʽ
	 * @param addWatermark
	 *            �Ƿ��ˮӡ
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public byte[] getPrivate(int userId, String name, String imageSizeId) throws IOException;

	/**
	 * ȡ������ͼ��File���������ͼ�����ڣ���ô����һ��
	 * 
	 * @param userId
	 *            �û�id
	 * @param name
	 *            ͼƬ��
	 * @param width
	 *            ����ͼ���
	 * @param height
	 *            ����ͼ�߶�
	 * @param asThumbnail
	 *            �Ƿ�������ͼ
	 * @param resizeMode
	 *            ����ģʽ
	 * @param addWatermark
	 *            �Ƿ��ˮӡ
	 * @return
	 * @throws IOException
	 */
	public byte[] get(int userId, String name, String imageSizeId) throws IOException;

	public byte[] getDefault(int userId, String name, String imageSizeId) throws IOException;

	/**
	 * ��ȡԭͼ�ߴ�
	 * 
	 * @param userId
	 * @param name
	 * @return ����һ�����飬�����0������width�������1������height
	 * @throws IOException
	 */
	public int[] getSize(int userId, String name) throws IOException;
}
