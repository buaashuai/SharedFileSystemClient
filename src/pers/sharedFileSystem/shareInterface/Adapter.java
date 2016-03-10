package pers.sharedFileSystem.shareInterface;

import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Node;

public class Adapter {
	/**
	 * 节点ID
	 */
	public  String NODEID;
	/**
	 * 节点对象
	 */
	public DirectoryNode NODE;
	/**
	 * <p>
	 * FileAdapter中filePath是带文件名的文件绝对路径
	 * </p>
	 * <p>
	 * DirectoryAdapter中filePath是带目录绝对路径
	 * </p>
	 */
	public  String FILEPATH;
	/**
	 * 相对路径（以‘/’结尾）
	 */
	public  String RELATIVE_FILEPATH;
}