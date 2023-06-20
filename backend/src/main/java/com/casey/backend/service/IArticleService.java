package com.casey.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.casey.backend.dto.ArticleFormDTO;
import com.casey.backend.dto.PostFormDTO;
import com.casey.backend.dto.Result;
import com.casey.backend.entity.Article;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IArticleService {
    // 删除文章
    Result deleteById(int id);

    Result postArticle(HttpServletRequest request, PostFormDTO postFormDTO);

    // 带分页的浏览文章
    Page<Article> getArticle(int pageCode, int pageSize);

    // 带分页的搜索栏浏览文章
    Page<Article> getArticleByTitleOrContent(int pageCode, int pageSize, String target, int select);

    Result updateById(int id, PostFormDTO postFormDTO);
}
