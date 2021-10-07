package com.tsengfhy.entry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tsengfhy.entry.domain.User;

public interface UserMapper extends BaseMapper<User> {

    int selectRawCount(Long id);
}
