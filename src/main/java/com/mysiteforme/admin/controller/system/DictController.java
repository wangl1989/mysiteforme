package com.mysiteforme.admin.controller.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.base.BaseController;
import com.mysiteforme.admin.entity.Dict;
import com.mysiteforme.admin.util.LayerData;
import com.mysiteforme.admin.util.RestResponse;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * Created by wangl on 2018/1/1.
 * todo:
 */
@Controller
@RequestMapping("admin/system/dict")
public class DictController extends BaseController{
    private static final Log log = LogFactory.get();

    @PostMapping("deleteById")
    @ResponseBody
    public RestResponse deleteById(@RequestParam(value = "id",required = false)Long id){
        if(id == null || id == 0){
            return RestResponse.failure("字典ID错误");
        }
        dictService.deleteDict(id);
        return RestResponse.success();
    }

    @GetMapping("list")
    @SysLog("跳转系统字典页面")
    public String list(){
        return "admin/system/dict/list";
    }

    @PostMapping("list")
    @ResponseBody
    @SysLog("请求系统字典列表数据")
    public LayerData<Dict> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                                @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                                ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        LayerData<Dict> layerData = new LayerData<>();
        EntityWrapper<Dict> wrapper = new EntityWrapper<>();
        if(!map.isEmpty()){
            String type = (String) map.get("type");
            if(StringUtils.isNotBlank(type)) {
                wrapper.eq("type", type);
            }
            String label = (String)map.get("label");
            if(StringUtils.isNotBlank(label)){
                wrapper.like("label",label);
            }
        }
        wrapper.orderBy("type",false).orderBy("sort",false);
        Page<Dict> dataPage = dictService.selectPage(new Page<>(page,limit),wrapper);
        layerData.setCount(dataPage.getTotal());
        layerData.setData(dataPage.getRecords());
        return layerData;
    }

    @GetMapping("add")
    public String add(@RequestParam(value = "type",required = false)String type,Model model){
        if(StringUtils.isNotBlank(type)){
            model.addAttribute("type",type);
        }
        return "admin/system/dict/add";
    }

    @PostMapping("add")
    @ResponseBody
    public RestResponse add(Dict dict){
        if(StringUtils.isBlank(dict.getType())){
            return RestResponse.failure("字典类型不能为空");
        }
        if(StringUtils.isBlank(dict.getLabel())){
            return RestResponse.failure("字典标签不能为空");
        }
        if(StringUtils.isBlank(dict.getValue())){
            return RestResponse.failure("字典标签对应的值不能为空");
        }
        if(dictService.getCountByType(dict.getType())==0){
            dict.setSort(0);
        }else{
            if(dictService.getCountByAll(dict.getType(),dict.getLabel(),null)>0){
                return RestResponse.failure("已经存在【"+dict.getType()+"】的label值【"+dict.getLabel()+"】");
            }
            if(dictService.getCountByAll(dict.getType(),dict.getLabel(),dict.getValue())>0){
                return RestResponse.failure("已经存在【"+dict.getType()+"】的label标签【"+dict.getLabel()+"】的value值【"+dict.getValue()+"】");
            }
            dict.setSort(dictService.getMaxSortByType(dict.getType()));
        }
        dictService.saveOrUpdateDict(dict);
        return RestResponse.success();
    }

    @GetMapping("edit")
    public String edit(Long id,Model model){
        Dict dict = dictService.selectById(id);
        model.addAttribute("dict",dict);
        return "admin/system/dict/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    public RestResponse edit(Dict dict){
        if(dict.getId()==null || dict.getId() == 0){
            return RestResponse.failure("字典ID不能为空");
        }
        if(StringUtils.isBlank(dict.getType())){
            return RestResponse.failure("字典类型不能为空");
        }
        if(StringUtils.isBlank(dict.getLabel())){
            return RestResponse.failure("字典标签不能为空");
        }
        if(StringUtils.isBlank(dict.getValue())){
            return RestResponse.failure("字典标签对应的值不能为空");
        }
        if(dict.getSort() == null || dict.getSort() < 0){
            return RestResponse.failure("排序值不正确");
        }
        Dict oldDict = dictService.selectById(dict.getId());
        if(!oldDict.getType().equals(dict.getType())){
            return RestResponse.failure("字典类型不能修改");
        }
        if(!oldDict.getLabel().equals(dict.getLabel())){
            if(dictService.getCountByAll(dict.getType(),dict.getLabel(),null)>0){
                return RestResponse.failure("已经存在【"+dict.getType()+"】的label值【"+dict.getLabel()+"】");
            }
        }
        if(!oldDict.getValue().equals(dict.getValue())) {
            if (dictService.getCountByAll(dict.getType(), dict.getLabel(), dict.getValue()) > 0) {
                return RestResponse.failure("已经存在【" + dict.getType() + "】的label标签【" + dict.getLabel() + "】的value值【" + dict.getValue() + "】");
            }
        }
        dictService.saveOrUpdateDict(dict);
        return RestResponse.success();
    }

    @PostMapping("editType")
    @ResponseBody
    public RestResponse editType(@RequestParam(value="oldType",required = false)String oldType,
                                 @RequestParam(value = "newType",required = false)String newType){
        if(StringUtils.isBlank(oldType)){
            return RestResponse.failure("原类型不能为空");
        }
        if (StringUtils.isBlank(newType)){
            return RestResponse.failure("新类型不能为空");
        }
        if(oldType.equals(newType)){
            return RestResponse.failure("TYPE值相等就没必要替换了吧");
        }
        if(dictService.getCountByType(newType)>0){
            return RestResponse.failure("TYPE值已经被使用了");
        }
        dictService.updateByType(oldType,newType);
        return RestResponse.success();
    }


}
