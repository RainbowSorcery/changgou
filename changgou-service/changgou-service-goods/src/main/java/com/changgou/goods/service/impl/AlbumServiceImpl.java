package com.changgou.goods.service.impl;

import com.changgou.goods.dao.AlbumMapper;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.AlbumService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    @Override
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Album album) {
        albumMapper.insertSelective(album);
    }

    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKeySelective(album);
    }

    @Override
    public void deleteById(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Album> findList(Map<String, Object> seachMap) {
        return albumMapper.selectByExample(createExample(seachMap));
    }

    @Override
    public Page<Album> findPage(Map<String, Object> seachMap, Integer page, Integer size) {
        PageHelper.startPage(page, size);

        Example example = createExample(seachMap);

        return (Page<Album>) albumMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null && searchMap.size() > 0) {
            // 相册名称
            if(searchMap.get("title")!=null && !"".equals(searchMap.get("title"))){
                criteria.andLike("title","%"+searchMap.get("title")+"%");
            }
            // 相册封面
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("image_items")!=null && !"".equals(searchMap.get("image_items"))){
                criteria.andLike("image_items","%"+searchMap.get("image_items")+"%");
            }
        }

        return example;
    }
}
