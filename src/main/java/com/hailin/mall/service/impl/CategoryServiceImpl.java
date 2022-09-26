package com.hailin.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.dao.CategoryMapper;
import com.hailin.mall.model.pojo.Category;
import com.hailin.mall.model.request.AddCategoryReq;
import com.hailin.mall.model.vo.CategoryVO;
import com.hailin.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category=new Category();
        BeanUtils.copyProperties(addCategoryReq,category);//拷贝
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if(categoryOld!=null){
            throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if(count==0){
            throw new HailinMallException(HailinMallExceptionEnum.CREATE_FAILED);
        }
    }
    @Override
    public void update(Category updateCategory){
        if (updateCategory.getName()!=null) {
            Category categoryOld=categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null&&!categoryOld.getId().equals(updateCategory.getId())) {
                throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
            }
            int count=categoryMapper.updateByPrimaryKeySelective(updateCategory);
            if (count == 0) {
                throw new HailinMallException(HailinMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }
    @Override
    public void delete(Integer id){
        Category categoryOld=categoryMapper.selectByPrimaryKey(id);
        //查询不到记录，无法删除
        if(categoryOld==null){
            throw new HailinMallException(HailinMallExceptionEnum.DELETE_FAILED);
        }
        int count=categoryMapper.deleteByPrimaryKey(id);
        if(count==0){
            throw new HailinMallException(HailinMallExceptionEnum.DELETE_FAILED);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        List<Category> categoryList=categoryMapper.selectList();
        PageInfo pageInfo=new PageInfo(categoryList);
        return pageInfo;
    }
    @Override
    @Cacheable(value = "listCategoryForCustomer")//该方法使用缓存
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOArrayList=new ArrayList<>();
        recursivelyFindCategories(categoryVOArrayList,parentId);
        return categoryVOArrayList;
    }
    private void recursivelyFindCategories(List<CategoryVO> categoryVOList,Integer parentId){
        //递归获取所有子类别，并组合成为一个“目录树”
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (int i = 0; i < categoryList.size(); i++) {
                Category category=categoryList.get(i);
                CategoryVO categoryVO=new CategoryVO();
                BeanUtils.copyProperties(category,categoryVO);//拷贝
                categoryVOList.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(),categoryVO.getId());
            }
        }
    }
}
