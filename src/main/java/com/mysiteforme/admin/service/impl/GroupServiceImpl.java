/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:22:38
 * @ Description: 组服务实现类 提供组的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.GroupDao;
import com.mysiteforme.admin.entity.Group;
import com.mysiteforme.admin.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class GroupServiceImpl extends ServiceImpl<GroupDao, Group> implements GroupService {
	
}
