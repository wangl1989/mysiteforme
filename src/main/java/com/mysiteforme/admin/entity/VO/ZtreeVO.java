/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 12:49:42
 * @ Description: ztree树形结构数据
 */

package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class ZtreeVO implements Serializable{

	private static final long serialVersionUID = 6962439201546719734L;

	private Long id;

	private Long pid;

	private String name;
	
	private String url;
	
	private Boolean open =true;
	
	private Boolean isParent;
	
	private String icon;
	
	private List<ZtreeVO> children;

}
