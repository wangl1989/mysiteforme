/**
 * @ Author: wangl
 * @ Create Time: 2025-02-14 02:38:13
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 12:44:26
 * @ Description: 权限服务实现类 提供权限的业务逻辑处理
 */

package com.mysiteforme.admin.service.impl;

import java.util.List;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysiteforme.admin.entity.*;
import com.mysiteforme.admin.entity.request.BasePermissionRequest;
import com.mysiteforme.admin.entity.request.PageListPermissionRequest;
import com.mysiteforme.admin.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysiteforme.admin.dao.PermissionDao;
import com.mysiteforme.admin.entity.DTO.PermissionApiDTO;
import com.mysiteforme.admin.entity.DTO.PermissionButtonDTO;
import com.mysiteforme.admin.entity.DTO.PermissionPageDTO;
import com.mysiteforme.admin.entity.VO.PermissionApiVO;
import com.mysiteforme.admin.exception.MyException;
import com.mysiteforme.admin.redis.CacheUtils;
import com.mysiteforme.admin.service.PermissionApiService;
import com.mysiteforme.admin.service.PermissionButtonService;
import com.mysiteforme.admin.service.PermissionPageService;
import com.mysiteforme.admin.service.PermissionService;
import com.mysiteforme.admin.util.MessageConstants;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = MyException.class)
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

    private final PermissionApiService permissionApiService;
    
    private final PermissionPageService permissionPageService;
    
    private final PermissionButtonService permissionButtonService;
    
    private final CacheUtils cacheUtils;

    @Override
    public IPage<Permission> selectPagePermission(PageListPermissionRequest request) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if(request != null){
            if(StringUtils.isNotBlank(request.getType())) {
                wrapper.eq(Permission::getPermissionType, request.getType());
            }
            if(StringUtils.isNotBlank(request.getPermissionName())){
                wrapper.like(Permission::getPermissionName,request.getPermissionName());
            }
            if(StringUtils.isNotBlank(request.getPermissionCode())){
                wrapper.like(Permission::getPermissionCode,request.getPermissionCode());
            }
            wrapper.orderBy(request.getSortByCreateDateAsc() != null, request.getSortByCreateDateAsc() != null && request.getSortByCreateDateAsc(), Permission::getCreateDate);
            wrapper.orderBy(request.getSortBySortAsc() != null, request.getSortBySortAsc() != null && request.getSortBySortAsc(), Permission::getSort);
            wrapper.groupBy(Permission::getMenuId);
        }else{
            request = new PageListPermissionRequest();
        }
        return this.page(new Page<>(request.getPage(),request.getLimit()),wrapper);
    }

    @Override
    public void saveOrUpdatePermission(BasePermissionRequest request) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(request,permission);
        // 设置排序值
        if (permission.getSort() == null || permission.getSort() == 0) {
            Integer maxSort = permissionMaxSort(permission.getMenuId());
            permission.setSort(maxSort == null ? 1 : maxSort + 1);
        }
        // 保存基础权限信息
        baseMapper.insertOrUpdate(permission);
        // 根据权限类型保存对应的具体权限数据
        switch (permission.getPermissionType()) {
            case Constants.TYPE_API: // API类型
                handlePermissionData(request.getApi(), permission,
                        this::saveOrUpdatePermissionApiDTO,
                        MessageConstants.Permission.API_DATA_INVALID);
                break;
            case Constants.TYPE_BUTTON: // 页面类型
                handlePermissionData(request.getButton(), permission,
                        this::saveOrUpdatePermissionButtonDTO,
                        MessageConstants.Permission.PAGE_DATA_INVALID);
                break;
            case Constants.TYPE_PAGE: // 按钮类型
                handlePermissionData(request.getPagePermission(), permission,
                        this::saveOrUpdatePermissionPageDTO,
                        MessageConstants.Permission.BUTTON_DATA_INVALID);
                break;
            default:
                throw MyException.builder().businessError(MessageConstants.Permission.DATA_INVALID).build();
        }
        //清除缓存
        cacheUtils.evictCacheOnPermissionChange(permission.getId());
    }

    // 在类中添加这个私有方法
    private <T> void handlePermissionData(T data, Permission permission, Consumer<T> saveFunction, String errorMessage) {
        if (data != null) {
            // 设置通用属性
            if (data instanceof PermissionApiDTO) {
                ((PermissionApiDTO) data).setPermissionId(permission.getId());
                ((PermissionApiDTO) data).setSort(permission.getSort());
                ((PermissionApiDTO) data).setPermissionId(permission.getId());
            } else if (data instanceof PermissionPageDTO) {
                ((PermissionPageDTO) data).setPermissionId(permission.getId());
                ((PermissionPageDTO) data).setSort(permission.getSort());
                ((PermissionPageDTO) data).setPermissionId(permission.getId());
            } else if (data instanceof PermissionButtonDTO) {
                ((PermissionButtonDTO) data).setPermissionId(permission.getId());
                ((PermissionButtonDTO) data).setSort(permission.getSort());
                ((PermissionButtonDTO) data).setPermissionId(permission.getId());
            }
            saveFunction.accept(data);
        } else {
            throw MyException.builder().businessError(errorMessage).build();
        }
    }

    private Integer permissionMaxSort(Long menuId){
        QueryWrapper<Permission> query = new QueryWrapper<>();
        query.eqSql("sort","select max(sort) from sys_permission")
                .eq("menu_id",menuId)
                .eq("del_flag",false);
        List<Permission> permissions = baseMapper.selectList(query);
        int sort = 0;
        if(permissions != null && !permissions.isEmpty()){
            sort =  permissions.get(0).getSort() +1;
        }
        return sort;
    }

    @Override
    public PermissionApiVO getApiByUrlAndMethod(String url, String method) {
        return baseMapper.selectApiByUrl(url, method);
    }

    @Override
    public void saveOrUpdatePermissionApiDTO(PermissionApiDTO permissionApiDTO) throws MyException{
        if (StringUtils.isBlank(permissionApiDTO.getApiUrl())) {
            throw MyException.builder().businessError(MessageConstants.Permission.API_URL_EMPTY).build();
        }
        if (StringUtils.isBlank(permissionApiDTO.getHttpMethod())) {
            throw MyException.builder().businessError(MessageConstants.Permission.API_METHOD_EMPTY).build();
        }

        // 检查API URL唯一性
        if (!permissionApiService.checkApiUrlUnique(permissionApiDTO.getApiUrl(), permissionApiDTO.getHttpMethod(), permissionApiDTO.getId())) {
            throw MyException.builder().businessError(MessageConstants.Permission.API_URL_EXISTS).build();
        }

        PermissionApi permissionApi = new PermissionApi();
        BeanUtils.copyProperties(permissionApiDTO, permissionApi);
        
        permissionApiService.saveOrUpdate(permissionApi);
    }

    @Override
    public void saveOrUpdatePermissionPageDTO(PermissionPageDTO permissionPageDTO) throws MyException{
        if (StringUtils.isBlank(permissionPageDTO.getPageUrl())) {
            throw MyException.builder().businessError(MessageConstants.Permission.PAGE_URL_EMPTY).build();
        }

        // 检查页面URL唯一性
        if (!permissionPageService.checkPageUrlUnique(permissionPageDTO.getPageUrl(), permissionPageDTO.getId())) {
            throw MyException.builder().businessError(MessageConstants.Permission.PAGE_URL_EXISTS).build();
        }

        PermissionPage permissionPage = new PermissionPage();
        BeanUtils.copyProperties(permissionPageDTO, permissionPage);

        permissionPageService.saveOrUpdate(permissionPage);
    }

    @Override
    public void saveOrUpdatePermissionButtonDTO(PermissionButtonDTO permissionButtonDTO) {
        if (StringUtils.isBlank(permissionButtonDTO.getButtonKey())) {
            throw MyException.builder().businessError(MessageConstants.Permission.BUTTON_KEY_EMPTY).build();
        }
        if (StringUtils.isBlank(permissionButtonDTO.getButtonName())) {
            throw MyException.builder().businessError(MessageConstants.Permission.BUTTON_NAME_EMPTY).build();
        }

        // 检查按钮标识唯一性
        if (!permissionButtonService.checkButtonKeyUnique(permissionButtonDTO.getButtonKey(), permissionButtonDTO.getId())) {
            throw MyException.builder().businessError(MessageConstants.Permission.BUTTON_KEY_EXISTS).build();
        }

        PermissionButton permissionButton = new PermissionButton();
        BeanUtils.copyProperties(permissionButtonDTO, permissionButton);
        permissionButtonService.saveOrUpdate(permissionButton);
    }

    @Override
    public void deletePermission(Long id) throws MyException {
        Permission permission = baseMapper.selectById(id);
        if (permission == null) {
            throw MyException.builder().businessError(MessageConstants.Permission.NOT_EXISTS).build();
        }
        permission.setDelFlag(true);
        baseMapper.insertOrUpdate(permission);
        cacheUtils.evictCacheOnPermissionChange(id);
    }

    @Override
    public Boolean checkPermissionCode(Long id, String permissionCode) {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("permission_code", permissionCode);
        if(id != null) {
            wrapper.ne("id", id);
        }
        return count(wrapper) == 0;
    }

}
