package com.mysiteforme.admin.entity.VO;

import java.io.Serializable;
import java.util.List;

/**
 * Ztree 树
 * @author Administrator
 *
 */
public class ZtreeVO implements Serializable{

	private Long id;

	private Long pid;

	private String name;
	
	private String url;
	
	private Boolean open =true;
	
	private Boolean isParent;
	
	private String icon;
	
	private List<ZtreeVO> children;
	


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}
	
	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public List<ZtreeVO> getChildren() {
		return children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setChildren(List<ZtreeVO> children) {
		this.children = children;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}
}
