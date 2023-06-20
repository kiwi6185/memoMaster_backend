package com.casey.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.casey.backend.entity.Article;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
//    @Select("SELECT * FROM `tb_article` WHERE title LIKE CONCAT('%',#{usercode},'%')")
//    List
}
