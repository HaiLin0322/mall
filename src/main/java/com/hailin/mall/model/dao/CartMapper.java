package com.hailin.mall.model.dao;

import com.hailin.mall.model.pojo.Cart;
import com.hailin.mall.model.vo.CartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart row);

    int insertSelective(Cart row);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart row);

    int updateByPrimaryKey(Cart row);
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);
    List<CartVO> selectList(@Param("userId") Integer userId);
    Integer selectOrNot(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("selected") Integer selected);
}