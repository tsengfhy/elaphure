package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsengfhy.entry.constants.Sex;
import com.tsengfhy.entry.domain.User;
import com.tsengfhy.entry.mapper.UserMapper;
import com.tsengfhy.entry.Application;
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
        Assertions.assertEquals(userMapper.selectById(user.getId()).getSex(), sex);
    }

    @Test
    void testOptimisticLock() {
        User user = new User().setName("tsengfhy").setAge(18).setEmail("tsengfhy@gmail.com");
        userMapper.insert(user);
        user = userMapper.selectById(user.getId()).setAge(24);
        Integer version = user.getVersion();
        Assertions.assertNotEquals(userMapper.updateById(user), 0);
        Assertions.assertNotEquals(user.getVersion(), version);
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
        Assertions.assertEquals(userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getId, user.getId())), 0);
        Assertions.assertNotEquals(userMapper.selectRawCount(user.getId()), 0);
    }

    @Test
    void testBlockFullDelete() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            userMapper.delete(Wrappers.lambdaQuery());
        });
    }
}
