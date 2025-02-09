package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Ztree 树
 * @author Administrator
 *
 */
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
