package com.hailin.mall.model.dao;

import com.hailin.mall.model.pojo.Order;
import com.hailin.mall.model.pojo.Product;
import com.hailin.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product row);

    int insertSelective(Product row);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product row);

    int updateByPrimaryKey(Product row);
    Product selectByName(String name);
    int batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);
    List<Product> selectListForAdmin();
    List<Product> selectList(@Param("query") ProductListQuery query);
}