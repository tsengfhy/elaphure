package com.tsengfhy.elaphure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsengfhy.elaphure.domain.User;

public interface UserMapper extends BaseMapper<User> {

    int selectRawCount(Long id);
}
