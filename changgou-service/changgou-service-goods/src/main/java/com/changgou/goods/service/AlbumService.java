package com.changgou.goods.service;

import com.changgou.goods.pojo.Album;
import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AlbumService {
    /**
     * 查询所有相册
     * @return 相册列表
     */
    List<Album> findAll();

    /**
     * 根据id 查询相册
     * @param id 相册id
     * @return 相册对象
     */
    Album findById(Long id);

    /**
     * 相册新增
     * @param album 相册对象
     */
    void add(Album album);

    /**
     * 更新相册
     * @param album 相册对象
     */
    void update(Album album);

    /**
     * 根据id删除相册
     * @param id 相册id
     */
    void deleteById(Long id);

    /**
     * 多条件搜索
     * @param seachMap 搜索条件
     * @return 符合索树条件的相册列表
     */
    List<Album> findList(Map<String, Object> seachMap);

    /**
     * 分页条件查询
     * @param seachMap 查询条件
     * @param page 页数
     * @param size 每页列表数
     * @return 分页列表
     */
    Page<Album> findPage(Map<String, Object> seachMap, Integer page, Integer size);
}
