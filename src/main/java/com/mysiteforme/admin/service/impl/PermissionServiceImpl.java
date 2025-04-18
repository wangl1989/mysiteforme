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
import com.mysiteforme.admin.entity.request.UpdatePermissionRequest;
import com.mysiteforme.admin.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdatePermission(BasePermissionRequest request) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(request,permission);
        if(request instanceof UpdatePermissionRequest){
            Permission oldPermission = baseMapper.selectById(((UpdatePermissionRequest) request).getId());
            if(oldPermission == null){
                throw MyException.builder().businessError(MessageConstants.Permission.NOT_EXISTS).build();
            }
            if(request.getPermissionType().intValue() != oldPermission.getPermissionType().intValue()){
                throw MyException.builder().businessError(MessageConstants.Permission.TYPE_CAN_NOT_CHANGE).build();
            }
        }
        // 设置排序值
        if (permission.getSort() == null || permission.getSort() == 0 || permission.getSort() == 1) {
            Integer maxSort = baseMapper.permissionMaxSort(permission.getMenuId(),request.getPermissionType());
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
                ((PermissionApiDTO) data).setSort(permission.getSort());
                ((PermissionApiDTO) data).setPermissionId(permission.getId());
                ((PermissionApiDTO) data).setRemarks(permission.getRemarks());
            } else if (data instanceof PermissionPageDTO) {
                ((PermissionPageDTO) data).setSort(permission.getSort());
                ((PermissionPageDTO) data).setPermissionId(permission.getId());
                ((PermissionPageDTO) data).setRemarks(permission.getRemarks());
            } else if (data instanceof PermissionButtonDTO) {
                ((PermissionButtonDTO) data).setSort(permission.getSort());
                ((PermissionButtonDTO) data).setPermissionId(permission.getId());
                ((PermissionButtonDTO) data).setRemarks(permission.getRemarks());
            }
            saveFunction.accept(data);
        } else {
            throw MyException.builder().businessError(errorMessage).build();
        }
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePermission(Long id) throws MyException {
        Permission permission = baseMapper.selectById(id);
        if (permission == null) {
            throw MyException.builder().businessError(MessageConstants.Permission.NOT_EXISTS).build();
        }
        permission.setDelFlag(true);
        baseMapper.insertOrUpdate(permission);
        switch (permission.getPermissionType()) {
            case Constants.TYPE_API: // API类型
                LambdaQueryWrapper<PermissionApi> apiWrapper = new LambdaQueryWrapper<>();
                apiWrapper.eq(PermissionApi::getPermissionId, id);
                apiWrapper.eq(PermissionApi::getDelFlag,false);
                List<PermissionApi> apiList = permissionApiService.list(apiWrapper);
                if(!apiList.isEmpty()){
                    PermissionApi api = apiList.get(0);
                    api.setDelFlag(true);
                    permissionApiService.saveOrUpdate(api);
                }
                break;
            case Constants.TYPE_BUTTON: // 页面类型
                LambdaQueryWrapper<PermissionButton> buttonWrapper = new LambdaQueryWrapper<>();
                buttonWrapper.eq(PermissionButton::getPermissionId, id);
                buttonWrapper.eq(PermissionButton::getDelFlag,false);
                List<PermissionButton> buttonList = permissionButtonService.list(buttonWrapper);
                if(!buttonList.isEmpty()){
                    PermissionButton button = buttonList.get(0);
                    button.setDelFlag(true);
                    permissionButtonService.saveOrUpdate(button);
                }
                break;
            case Constants.TYPE_PAGE: // 按钮类型
                LambdaQueryWrapper<PermissionPage> pageWrapper = new LambdaQueryWrapper<>();
                pageWrapper.eq(PermissionPage::getPermissionId, id);
                pageWrapper.eq(PermissionPage::getDelFlag,false);
                List<PermissionPage> pageList = permissionPageService.list(pageWrapper);
                if(!pageList.isEmpty()){
                    PermissionPage page = pageList.get(0);
                    page.setDelFlag(true);
                    permissionPageService.saveOrUpdate(page);
                }
                break;
            default:
                throw MyException.builder().businessError(MessageConstants.Permission.DATA_INVALID).build();
        }
        cacheUtils.evictCacheOnPermissionChange(id);
    }

    @Override
    public Boolean checkPermissionCode(Long id, String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        if(id != null) {
            wrapper.ne(Permission::getId, id);
        }
        return count(wrapper) == 0;
    }

}
