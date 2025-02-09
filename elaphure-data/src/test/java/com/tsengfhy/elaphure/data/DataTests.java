package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsengfhy.entry.Application;
import com.tsengfhy.entry.constant.Sex;
import com.tsengfhy.entry.domain.User;
import com.tsengfhy.entry.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
class DataTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testAutoFill() {
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com");
        userMapper.insert(user);
        Assertions.assertNotNull(userMapper.selectById(user.getId()).getCreatedDate());
    }

    @Test
    void testEnum() {
        Sex sex = Sex.MALE;
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com").setSex(Sex.MALE);
        userMapper.insert(user);
        Assertions.assertEquals(sex, userMapper.selectById(user.getId()).getSex());
    }

    @Test
    void testOptimisticLock() {
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com");
        userMapper.insert(user);
        user = userMapper.selectById(user.getId()).setAge(24);
        Integer version = user.getVersion();
        Assertions.assertNotEquals(0, userMapper.updateById(user));
        Assertions.assertNotEquals(version, user.getVersion());
    }

    @Test
    void testPagination() {
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com");
        userMapper.insert(user);
        Wrapper<User> wrapper = Wrappers.<User>lambdaQuery().isNotNull(User::getName);
        Page<User> page = userMapper.selectPage(new Page<>(1, 1), wrapper);
        Assertions.assertTrue(page.getPages() > 0);
    }

    @Test
    void testSoftDelete() {
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com");
        userMapper.insert(user);
        userMapper.deleteById(user.getId());
        Assertions.assertEquals(0, userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getId, user.getId())));
        Assertions.assertNotEquals(0, userMapper.selectRawCount(user.getId()));
    }

    @Test
    void testBlockFullDelete() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            userMapper.delete(Wrappers.lambdaQuery());
        });
    }
}
