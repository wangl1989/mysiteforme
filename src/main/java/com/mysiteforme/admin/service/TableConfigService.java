package com.mysiteforme.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mysiteforme.admin.entity.TableConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysiteforme.admin.entity.request.PageListTableConfigRequest;
import com.mysiteforme.admin.entity.response.PageListTableConfigResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangl
 * @since 2025-04-19
 */
public interface TableConfigService extends IService<TableConfig> {

    IPage<PageListTableConfigResponse> selectPageTableConfig(PageListTableConfigRequest request);

    void saveOrUpdateTableConfig(TableConfig tableConfig);

    void deleteTableConfig(Long id);

    void recoverTableConfig(Long id);

    List<String> getTableNameList(String schemaName);

    List<String> getSchemaNameList();

    void downloadCode(List<Long> ids, HttpServletResponse response);
}
