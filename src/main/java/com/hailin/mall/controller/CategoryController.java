package com.hailin.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.common.ApiRestResponse;
import com.hailin.mall.common.Constant;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.pojo.Category;
import com.hailin.mall.model.pojo.User;
import com.hailin.mall.model.request.AddCategoryReq;
import com.hailin.mall.model.request.UpdateCategoryReq;
import com.hailin.mall.model.vo.CategoryVO;
import com.hailin.mall.service.CategoryService;
import com.hailin.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;
    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session,@Valid @RequestBody AddCategoryReq categoryReq){
        /**if(categoryReq.getName()==null||categoryReq.getType()==null||categoryReq.getParentId()==null||categoryReq.getOrderNum()==null){
            return ApiRestResponse.error(HailinMallExceptionEnum.PARAM_NOT_NULL);
        }
         使用@Valid开启校验并在对应实体类增加校验内容*/
        User currentUser=(User) session.getAttribute(Constant.HAILIN_MALL_USER);
        if(currentUser==null){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_LOGIN);
        }
        //校验是否为管理员，是则执行操作
        if (userService.checkAdminRole(currentUser)){
            categoryService.add(categoryReq);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_ADMIN);
        }
    }
    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,HttpSession session){
        User currentUser=(User) session.getAttribute(Constant.HAILIN_MALL_USER);
        if(currentUser==null){
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_LOGIN);
        }
        //校验是否为管理员，是则执行操作
        if (userService.checkAdminRole(currentUser)){
            Category category=new Category();
            BeanUtils.copyProperties(updateCategoryReq,category);
            categoryService.update(category);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(HailinMallExceptionEnum.NEED_ADMIN);
        }
    }
    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }
    @ApiOperation("后台目录列表")
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo=categoryService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    @ApiOperation("前台目录列表")
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOList =categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }
}
