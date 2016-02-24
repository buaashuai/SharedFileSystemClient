package pers.sharedFileSystem.convenientUtil;

import pers.sharedFileSystem.configManager.Config;
import pers.sharedFileSystem.entity.DirectoryNode;
import pers.sharedFileSystem.entity.IntervalProperty;
import pers.sharedFileSystem.entity.Node;
import pers.sharedFileSystem.entity.NodeNameType;
import pers.sharedFileSystem.logManager.LogRecord;
import pers.sharedFileSystem.shareInterface.DirectoryAdapter;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 文件系统基础工具包
 * 
 * @author buaashuai
 *
 */
public class CommonUtil {
	/**
	 * 验证字符串的有效性
	 * 
	 * @param string
	 *            待验证的字符串
	 * @return 字符串不为null 且 不为空字符串 则返回true
	 */
	public static boolean validateString(String string) {
		return string != null && !string.isEmpty();
	}

	/**
	 * 深层拷贝 - 需要类继承序列化接口
	 * 
	 * @param <T>
	 *            待拷贝对象的类型
	 * @param obj
	 *            待拷贝的对象
	 * @return 拷贝的另一个对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyImplSerializable(T obj){
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		Object o = null;
		// 如果子类没有继承该接口，这一步会报错
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);

			o = ois.readObject();
			return (T) o;
		} catch (Exception e) {
			LogRecord.RunningErrorLogger.error("object not contains object that  implements Serializable. ");
			return null;
		} finally {
			try {
				baos.close();
				oos.close();
				bais.close();
				ois.close();
			} catch (Exception e2) {
				// 这里报错不需要处理
			}
		}
	}

	/**
	 * 将long类型的数值转成无符号数
	 *
	 * @param value
	 *            需要转换的数
	 * @return 转换之后的值
	 */
	public static BigDecimal readUnsignedLong(long value){
		if (value >= 0)
			return new BigDecimal(value);
		long lowValue = value & 0x7fffffffffffffffL;
		return BigDecimal.valueOf(lowValue)
				.add(BigDecimal.valueOf(Long.MAX_VALUE))
				.add(BigDecimal.valueOf(1));
	}

	/**
	 * 判断ip地址是否是远程主机
	 * @param ip
	 * @return 是远程主机返回true，否则返回false
	 */
	public static  boolean isRemoteServer(String ip){
		return !ip.equals("127.0.0.1") && !ip.equals("localhost");
	}

	/**
	 * 根据目录结点的扩展区间属性，获取存储文件的实际目录结点编号
	 * @param node 源目的结点
	 * @param flag    true 表示对静态/动态目录结点都生成重定向编号；  false表示只对动态目录结点生成重定向编号
	 * @return
	 */
	public static  String getDestDirectoryNode(DirectoryNode node,Map<String, String> parms,boolean flag){
		String destNodeId=node.Id;//默认保存到当前结点
		String orignPath = "";// 根节点到nodeId节点的相对路径
		orignPath = node.Path;
		String[] paths = orignPath.split("/");
		String key = "";
		String alt = "";
		for (int i = 0; i < paths.length; i++) {
			// 找到动态命名节点
			if (paths[i].contains(Config.getPREFIX())) {
				key = paths[i].substring(Config.getPREFIX().length());
				DirectoryNode keyNode=node;
				boolean notFind=true;
				//查找key对应的结点对象
				while(notFind){
					if(keyNode.NameType== NodeNameType.DYNAMIC&&keyNode.Property.equals(key)) {
						notFind=false;
					}else{
						Node nn=keyNode.Parent;
						if(nn instanceof DirectoryNode){
							keyNode=(DirectoryNode)nn;
						}else
							break;
					}
				}
				if(notFind){
					LogRecord.RunningErrorLogger.error("miss parm[" + key
							+ "]");
					return "";
				}
				List<IntervalProperty> Intervals=keyNode.Intervals;
				alt = parms.get(key);
				if (CommonUtil.validateString(alt)&&Intervals!=null) {
					for(IntervalProperty intervalProperty : Intervals){//按照字典序比较大小
						if(alt.compareTo(intervalProperty.Min)>0&&alt.compareTo(intervalProperty.Max)<=0){
							return  intervalProperty.DirectoryNodeId;
						}
					}
				}
				else {
					LogRecord.RunningErrorLogger.error("miss parm[" + key
							+ "]");
					return "";
				}
			}
		}
		if(node.NameType==NodeNameType.STATIC && flag) {
			List<IntervalProperty> Intervals=node.Intervals;
			if(Intervals.size()>0) {
				double max = Double.parseDouble(Intervals.get(0).Max);
				DirectoryAdapter dicAdapter = new DirectoryAdapter(destNodeId, parms);
				int count = dicAdapter.getAllFileNames().size();
				if (count >= max) {
					return Intervals.get(0).DirectoryNodeId;
				}
			}
		}
		return  destNodeId;
	}
}
