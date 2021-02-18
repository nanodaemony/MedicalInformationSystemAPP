package com.nano.device;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 仪器类型枚举
 * @author cz
 */
@Getter
public enum DeviceTypeEnum {

	/**
	 * 麻醉机
	 */
	ANESTHESIA_MACHINE("1", "麻醉机"),

	/**
	 * 呼吸机
	 */
	RESPIRATOR_MACHINE("2", "呼吸机"),

	/**
	 * 无创血压监测仪
	 */
	BLOOD_PRESSURE_MONITOR("3", "无创血压监测仪(监护仪)"),

	/**
	 * 麻醉深度监护仪
	 */
	ANESTHESIA_DEPTH_MONITOR("4", "麻醉深度监测仪"),

	/**
	 * 血红蛋白监测仪
	 */
	HEMOGLOBIN_MONITOR("5", "无创血红蛋白监测仪"),


	/**
	 * 无创脑氧饱和度监测仪
	 */
	BRAIN_OXYGEN_MONITOR("6", "无创脑氧饱和度监测仪"),

	;

	private String code;

	private String typeName;

	DeviceTypeEnum(String code, String typeName) {
		this.code = code;
		this.typeName = typeName;
	}

	/**
	 * 传入类型数组
	 *
	 * @param types 类型数组
	 * @return 得到类型字符串
	 */
	public static String getTypeString(DeviceTypeEnum... types) {
		if (types.length == 1) {
			return types[0].getCode();
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (DeviceTypeEnum typeEnum : types) {
			stringBuffer.append(typeEnum.code).append("#");
		}
		// 去掉最后一个#号
		return stringBuffer.toString().substring(0, stringBuffer.length() - 1);
	}

	/**
	 * 根据传入的类别字符串获取仪器类别枚举
	 *
	 * @param deviceTypeString 类别字符串,如 2
	 * @return 枚举
	 */
	public static DeviceTypeEnum findDeviceType(String deviceTypeString) {
		for (DeviceTypeEnum typeEnum : DeviceTypeEnum.values()) {
			if(typeEnum.getCode().equals(deviceTypeString)) {
				return typeEnum;
			}
		}
		return null;
	}

	/**
	 * 根据传入的类别字符串获取仪器类别枚举
	 *
	 * @param deviceTypeString 类别字符串,如 1#2#3
	 * @return 枚举
	 */
	public static List<DeviceTypeEnum> findDeviceTypes(String deviceTypeString) {
		List<DeviceTypeEnum> list = new ArrayList<>(6);
		String[] nums = deviceTypeString.split("#");
		for (String type : nums) {
			for (DeviceTypeEnum typeEnum : DeviceTypeEnum.values()) {
				if(typeEnum.getCode().equals(type)) {
					list.add(typeEnum);
				}
			}
		}
		return list;
	}

	/**
	 * 判断是否是合格的类型
	 *
	 * @param type 类型字符串
	 * @return 是否合格
	 */
	public static boolean isValidDeviceType(String type) {
		for (DeviceTypeEnum typeEnum : DeviceTypeEnum.values()) {
			if(typeEnum.getCode().equals(type)) {
				return true;
			}
		}
		return false;
	}

}

