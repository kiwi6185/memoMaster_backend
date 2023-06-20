package com.casey.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.casey.backend.dto.ArticleFormDTO;
import com.casey.backend.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT tb_user.email " +
            "FROM tb_article " +
            "JOIN tb_user ON tb_user.id = tb_article.author " +
            "WHERE tb_user.id = #{uid} " +
            "LIMIT 1;")
    String selectByUid(int uid);
}
