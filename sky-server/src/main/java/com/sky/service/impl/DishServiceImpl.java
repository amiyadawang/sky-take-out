package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DishServiceImpl {
    @Autowired
    private DishMapper dishMapper;
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        dish.setName(dishDTO.getName());
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条数据
        //不使用dishDto,因为dishDto中包含口味信息,而菜品表中不包含口味信息
        dishMapper.insert(dish);

        //向菜品口味表插入多条数据
    }
}
