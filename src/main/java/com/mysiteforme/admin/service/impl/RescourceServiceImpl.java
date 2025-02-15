/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:25:09
 * @ Description: 资源服务实现类 提供资源的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.service.RescourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RescourceServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements RescourceService {

}
