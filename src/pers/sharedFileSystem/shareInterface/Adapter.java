package pers.sharedFileSystem.shareInterface;

import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.Node;

import java.util.Map;

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
	 * <p>为空表示文件不存在</p>
	 */
	public  String FILEPATH;
	/**
	 * 相对路径（以‘/’结尾）
	 */
	public  String RELATIVE_FILEPATH;
	/**
	 * 相关参数
	 */
	protected Map<String, String> PARM;

	/**
	 * 获取操作目录或者文件的操作者信息
	 * @return
	 */
	protected String getOperationInfo(){
		String info="[";
		if(PARM!=null){
			if(PARM.get("USERTYPR")!=null){
				info+=PARM.get("USERTYPR");
			}
			if(PARM.get("USERID")!=null){
				info+="_"+PARM.get("USERID");
			}
		}
		info+="]";
		return info;
	}
}