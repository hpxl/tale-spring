package com.tale.service.impl;

import com.tale.dao.UsersDao;
import com.tale.exception.TipException;
import com.tale.model.entity.Users;
import com.tale.service.UserService;
import com.tale.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by hpxl on 3/5/18.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersDao usersDao;

    @Override
    public Users queryUserById(Integer uid) {
        if (null != uid) {
            return usersDao.queryObject(uid);
        }
        return null;
    }

    @Override
    public Integer updateByUid(Users users) {
        if (null != users && null != users.getUid()) {
            return usersDao.update(users);
        }
        return null;
    }

    @Override
    public Users login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        int count = usersDao.queryTotal();
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);
        map.put("password", pwd);
        List<Users> users = usersDao.queryList(map);
        if (users.size() != 1) {
            throw new TipException("用户名或密码错误");
        }
        return users.get(0);
    }

    @Override
    public Integer save(Users users) {
        if (null != users) {
            usersDao.save(users);
            return users.getUid();
        }
        return null;
    }

}
