package com.channggou.service.impl;

import com.changgou.pojo.Admin;
import com.channggou.dao.AdminMapper;
import com.channggou.service.AdminService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    @Override
    public Admin findById(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Admin admin) {
        // 获取admin密码
        String password = admin.getPassword();

        // 设置状态为立即使用
        admin.setStatus("1");

        // 加密密码
        String passwordEncrypt = BCrypt.hashpw(password, BCrypt.gensalt());
        admin.setPassword(passwordEncrypt);

        // 插入数据库
        adminMapper.insertSelective(admin);
    }

    @Override
    public void update(Admin admin) {
        String password = admin.getPassword();

        String passwordEncrypt = BCrypt.hashpw(password, BCrypt.gensalt());
        admin.setPassword(passwordEncrypt);

        adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Override
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);


        return adminMapper.selectByExample(example);
    }

    @Override
    public Page<Admin> findPage(int page, int size) {
        PageHelper.startPage(page, size);

        return (Page<Admin>) adminMapper.selectAll();
    }

    @Override
    public Page<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        Example example = createExample(searchMap);

        Page<Object> objects = PageHelper.startPage(page, size);


        return (Page<Admin>) adminMapper.selectByExample(example);
    }

    @Override
    public boolean login(Admin admin) {
        Admin conditionAdmin = new Admin();
        conditionAdmin.setLoginName(admin.getLoginName());

        Admin dbUser = adminMapper.selectOne(conditionAdmin);

        if (dbUser != null) {
            // 密码解密
            return BCrypt.checkpw(admin.getPassword(), dbUser.getPassword());
        } else {
            return false;
        }
    }

    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Admin.class);

        Example.Criteria criteria = example.createCriteria();

        if (searchMap != null && searchMap.size() > 0) {
            if (searchMap.get("loginName") != null && !"".equals(searchMap.get("loginName"))) {
                criteria.andLike("loginName", "%" + searchMap.get("loginName") + "%");
            }

            if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                criteria.andLike("password", "%" + searchMap.get("password") + "%");
            }

            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
        }

        return example;
    }
}
