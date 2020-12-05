package com.channggou.controller;

import com.changgou.goods.entity.PageResult;
import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.pojo.Admin;
import com.channggou.service.AdminService;
import com.channggou.utils.JwtUtil;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/findAll")
    public Result<List<Admin>> findAll() {
        List<Admin> adminList = adminService.findAll();

        return new Result<>(true, StatusCode.OK, "查询全部管理员用户成功.", adminList);
    }

    @GetMapping("/{id}")
    public Result<Admin> findById(@PathVariable Integer id) {
        Admin adminById = adminService.findById(id);

        return new Result<>(true, StatusCode.OK, "根据id查询管理员成功", adminById);
    }

    @PostMapping
    public Result<String> insertAdmin(@RequestBody Admin admin) {
        adminService.add(admin);

        return new Result<>(true, StatusCode.OK, "管理员添加成功.");
    }

    @PutMapping("/{id}")
    public Result<String> updateAdminByPrimaryKey(@RequestBody Admin admin, @PathVariable Integer id) {
        admin.setId(id);
        adminService.update(admin);

        return new Result<>(true, StatusCode.OK , "管理员更新成功.");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteAdminByPrimaryKey(@PathVariable Integer id) {
        adminService.delete(id);

        return new Result<>(true, StatusCode.OK, "管理员删除成功.");
    }

    @GetMapping("/search")
    public Result<List<Admin>> conditionSearchAdmin(Map<String, Object> searchMap) {
        List<Admin> list = adminService.findList(searchMap);

        return new Result<>(true, StatusCode.OK, "多条件查询成功", list);
    }

    @GetMapping("/search/findPage/{page}/{size}")
    public Result<Page<Admin>> findPage(@PathVariable Integer page, @PathVariable Integer size) {
        Page<Admin> adminPage = adminService.findPage(page, size);

        PageResult<Admin> pageResult = new PageResult<>(adminPage.getTotal(), adminPage.getResult());

        return new Result<>(true, StatusCode.OK, "管理员分页查询成功", pageResult);
    }

    @PostMapping("/login")
    public Result<Boolean> login(@RequestBody Admin admin) {
        if (StringUtils.isEmpty(admin.getLoginName())) {
            return new Result<>(false, StatusCode.LOGINERROR, "用户名不能为空.");
        }   
        if (StringUtils.isEmpty(admin.getPassword())) {
            return new Result<>(false, StatusCode.LOGINERROR, "用户密码不能为空.");
        }
        if(adminService.login(admin)) {
            // 创建token
            String token = JwtUtil.createJWT(UUID.randomUUID().toString(), admin.getLoginName(), null);

            Map<String, Object> userinfo = new HashMap<>();
            userinfo.put("username", admin.getLoginName());
            userinfo.put("token", token);

            return new Result<>(true, StatusCode.OK, "登录成功.", userinfo);
        } else {
            return new Result<>(false, StatusCode.ERROR, "登录失败");
        }
    }

}
