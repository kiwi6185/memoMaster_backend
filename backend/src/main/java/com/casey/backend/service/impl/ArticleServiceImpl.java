package com.casey.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.casey.backend.dto.PostFormDTO;
import com.casey.backend.dto.Result;
import com.casey.backend.entity.Article;
import com.casey.backend.mapper.ArticleMapper;
import com.casey.backend.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static com.casey.backend.utils.RedisConstants.LOGIN_USER_KEY;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> getArticle(int pageCode, int pageSize) {
        // 1.创建Page对象，传入两个参数：当前页和每页显示记录数
        Page<Article> page = new Page<Article>(pageCode,pageSize);
        // 2.将分页查询到的所有数据封装到Page对象中
        articleMapper.selectPage(page,null);
        return page;
    }

    @Override
    public Page<Article> getArticleByTitleOrContent(int pageCode, int pageSize, String target, int select) {
        // 1.创建Page对象，传入两个参数：当前页和每页显示记录数
        Page<Article> page = new Page<Article>(pageCode,pageSize);
//        2. qureyWrapper
        QueryWrapper<Article> qw = new QueryWrapper<>();
        if(select == 1)
            qw.like("title", target);
        else
            qw.like("content", target);
        // 2.将分页查询到的所有数据封装到Page对象中
        articleMapper.selectPage(page,qw);
        return page;
    }

    // 修改文章
    @Override
    public Result updateById(int id, PostFormDTO postFormDTO) {
        Article article = articleMapper.selectById(id);
        article.setTitle(postFormDTO.getTitle());
        article.setContent(postFormDTO.getContent());
        // 存进去
//        articleMapper.updateById(article);
        return Result.ok(articleMapper.updateById(article));
    }

    @Override
    public Result deleteById(int id) {
        int res = articleMapper.deleteById(id);
        if(res > 0){
            return Result.ok();
        }
        else{
            return Result.fail("删除失败");
        }
    }

    // 文章发布
    @Override
    public Result postArticle(HttpServletRequest request, PostFormDTO postFormDTO) {
        // 创建文章
        Article article = new Article();
        String token = LOGIN_USER_KEY + request.getHeader("authorization"); // 在这里先拿 token，然后查 redis
        System.out.println(token);
        Object id = stringRedisTemplate.opsForHash().get(token, "id");    // 报错是这里好像，刚刚是因为没拿到token 现在是类型错误好像
        System.out.println(Integer.parseInt((String) id));
        assert id != null;
        // 赋值
        article.setAuthor(Integer.parseInt((String) id));
        article.setTitle(postFormDTO.getTitle());
        article.setContent(postFormDTO.getContent());
        article.setCreatetime();
        // 保存文章
//        save(article);
        return Result.ok(save(article));
    }
}
