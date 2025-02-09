package com.mysiteforme.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.dao.RescourceDao;
import com.mysiteforme.admin.service.RescourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统资源 服务实现类
 * </p>
 *
 * @author wangl
 * @since 2018-01-14
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RescourceServiceImpl extends ServiceImpl<RescourceDao, Rescource> implements RescourceService {

}
