/**
 * @ Author: wangl
 * @ Create Time: 2025-02-11 14:55:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-15 13:30:07
 * @ Description: 资源Service
 */

package com.mysiteforme.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.Rescource;
import com.mysiteforme.admin.entity.request.PageListResourceRequest;

public interface RescourceService extends IService<Rescource> {

    IPage<Rescource> selectPageRescource(PageListResourceRequest request);

}
