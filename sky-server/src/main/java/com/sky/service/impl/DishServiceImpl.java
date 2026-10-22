package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sky.service.DishService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired

    private DishMapper dishMapper;
    @Autowired

    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条数据
        //不使用dishDto,因为dishDto中包含口味信息,而菜品表中不包含口味信息
        dishMapper.insert(dish);

        //向菜品口味表插入多条数据
        //获取菜品id
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors == null || flavors.isEmpty()){
            flavors.forEach(disFlavor ->
                    disFlavor.setDishId(dishId));
            log.info("菜品口味为空");
            dishFlavorMapper.insertBatch(flavors);
            return;
        }

    }
    @Transactional
    public void deleteBatch(List<Long> ids) {

        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //售卖的菜品不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            }}
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIds(ids);
        if(!setmealIds.isEmpty()){
            //套餐里面的菜品不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        for(Long id : ids){
            if(!dishFlavorMapper.getByDishId(id).isEmpty()){
                dishFlavorMapper.deleteByDishId(id);
            }}for(long id :ids){
            dishMapper.deleteById(id);}

    }//
    @Override
    public DishVO getByIdwithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        //根据菜品id查询菜品口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }
    @Override
    public void updateWithFlavor(DishVO dishVO) {
        //根据id更新菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishVO, dish);
        dishMapper.updateById(dish);

        //根据菜品id删除菜品口味数据
        dishFlavorMapper.deleteByDishId(dishVO.getId());

        //根据菜品id插入菜品口味数据
        List<DishFlavor> flavors = dishVO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(disFlavor ->
                    disFlavor.setDishId(dishVO.getId()));
            log.info("菜品口味为空");
            dishFlavorMapper.insertBatch(flavors);
            return;
        }
        dishFlavorMapper.insertBatch(flavors);
    }

}
