package com.hailin.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hailin.mall.common.Constant;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import com.hailin.mall.model.dao.ProductMapper;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.query.ProductListQuery;
import com.hailin.mall.model.request.AddProductReq;
import com.hailin.mall.model.request.ProductListReq;
import com.hailin.mall.model.vo.CategoryVO;
import com.hailin.mall.service.CategoryService;
import com.hailin.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Override
    public void add(AddProductReq addProductReq){
        Product product=new Product();
        BeanUtils.copyProperties(addProductReq,product);
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld!=null){
            throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
        }
        int count=productMapper.insertSelective(product);
        if (count == 0) {
            throw new HailinMallException(HailinMallExceptionEnum.CREATE_FAILED);
        }
    }
    @Override
    public void update(Product updateProduct){
        Product productOld=productMapper.selectByName(updateProduct.getName());
        //禁止重名，即同名不同ID不予修改（要么名称查不到，如果能查到必须ID一致）
        if(productOld!=null&&!productOld.getId().equals(updateProduct.getId())){
            throw new HailinMallException(HailinMallExceptionEnum.NAME_EXISTED);
        }
        int count=productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new HailinMallException(HailinMallExceptionEnum.UPDATE_FAILED);
        }
    }
    @Override
    public void delete(Integer id){
        Product productOld=productMapper.selectByPrimaryKey(id);
        if (productOld == null) {
            throw new HailinMallException(HailinMallExceptionEnum.DELETE_FAILED);
        }
        int count=productMapper.deleteByPrimaryKey(id);
        if(count==0){
            throw new HailinMallException(HailinMallExceptionEnum.DELETE_FAILED);
        }
    }
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products=productMapper.selectListForAdmin();
        return new PageInfo(products);
    }

    @Override
    public Product detail(Integer id){
        Product product=productMapper.selectByPrimaryKey(id);
        return product;
    }
    @Override
    public PageInfo list(ProductListReq productListReq){
        //构建query对象
        ProductListQuery productListQuery=new ProductListQuery();
        //搜索处理
        if(StringUtils.hasLength(productListReq.getKeyword())){
            String keyword=new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        //目录处理：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的list
        if (productListReq.getCategoryId()!=null) {
            List<CategoryVO> categoryVOList=categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds=new ArrayList();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList,categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //排序处理
        String orderBy=productListReq.getOrderBy();
        if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize(),orderBy);
        }else {
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize());
        }
        List<Product> productList=productMapper.selectList(productListQuery);
        PageInfo pageInfo=new PageInfo(productList);
        return pageInfo;
    }
    private void getCategoryIds(List<CategoryVO> categoryVOList,ArrayList<Integer> categoryIds){
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(),categoryIds);
            }
        }
    }
}
