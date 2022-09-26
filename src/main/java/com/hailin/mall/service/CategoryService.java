package com.hailin.mall.service;

import com.github.pagehelper.PageInfo;
import com.hailin.mall.model.pojo.Category;
import com.hailin.mall.model.request.AddCategoryReq;
import com.hailin.mall.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
