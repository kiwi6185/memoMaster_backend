package com.casey.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.casey.backend.dto.ArticleFormDTO;
import com.casey.backend.dto.PostFormDTO;
import com.casey.backend.dto.Result;
import com.casey.backend.entity.Article;
import com.casey.backend.mapper.ArticleMapper;
import com.casey.backend.mapper.UserMapper;
import com.casey.backend.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {
    @Resource
    private IArticleService articleService;

    @Autowired(required = false)
    private UserMapper userMapper;

    // 删除文章
    @PutMapping("/update/{id}")
    public Result updateArticle(@PathVariable int id, @RequestBody PostFormDTO postFormDTO){
        return articleService.updateById(id, postFormDTO);
    }

    // 删除文章
    @DeleteMapping("/delete/{id}")
    public Result deleteArticle(@PathVariable int id){
        return articleService.deleteById(id);
    }

    // 浏览文章
//    @RequestMapping(value = "/get/{pageCode}/{pageSize}",method = RequestMethod.GET)
    @GetMapping("/get/{pageCode}/{pageSize}")
    @ResponseBody
    public Page<ArticleFormDTO> getArticle(@PathVariable(value = "pageCode") int pageCode, @PathVariable(value = "pageSize") int pageSize) {
        System.out.println(pageCode + "...." + pageSize);
        Page<Article> pageInfo = articleService.getArticle(pageCode, pageSize);
        System.out.println(pageInfo);
        // 用户 id 换成 nickname
        Page<ArticleFormDTO> pageInfo_res = new Page<>();
        List<ArticleFormDTO> articleFormDTOList = new ArrayList<>();
        pageInfo_res.setTotal(pageInfo.getTotal());
        pageInfo_res.setSize(pageInfo.getSize());
        pageInfo_res.setCurrent(pageInfo.getCurrent());
        for(int i = 0; i < pageInfo.getRecords().size(); i++) {
            ArticleFormDTO articleFormDTO = new ArticleFormDTO();
            articleFormDTO.setId(pageInfo.getRecords().get(i).getId());
            articleFormDTO.setTitle(pageInfo.getRecords().get(i).getTitle());
            articleFormDTO.setContent(pageInfo.getRecords().get(i).getContent());
            articleFormDTO.setCreatetime(pageInfo.getRecords().get(i).getCreatetime());
            articleFormDTO.setEmail(userMapper.selectByUid(pageInfo.getRecords().get(i).getAuthor()));
            articleFormDTOList.add(articleFormDTO);
        }
        pageInfo_res.setRecords(articleFormDTOList);
        return pageInfo_res;
    }

    @GetMapping("/getByTitleOrContent/{pageCode}/{pageSize}/{select}/{target}")
    @ResponseBody
    public Page<ArticleFormDTO> getArticleByTitleOrContent(
            @PathVariable(value = "pageCode") int pageCode,
            @PathVariable(value = "pageSize") int pageSize,
            @PathVariable(value = "target") String target,
            @PathVariable(value = "select") int select) {
        System.out.println(pageCode + "...." + pageSize);
        Page<Article> pageInfo = articleService.getArticleByTitleOrContent(pageCode, pageSize, target, select);
        System.out.println(pageInfo);
        // 用户 id 换成 nickname
        Page<ArticleFormDTO> pageInfo_res = new Page<>();
        List<ArticleFormDTO> articleFormDTOList = new ArrayList<>();
        pageInfo_res.setTotal(pageInfo.getTotal());
        pageInfo_res.setSize(pageInfo.getSize());
        pageInfo_res.setCurrent(pageInfo.getCurrent());
        for(int i = 0; i < pageInfo.getRecords().size(); i++) {
            ArticleFormDTO articleFormDTO = new ArticleFormDTO();
            articleFormDTO.setId(pageInfo.getRecords().get(i).getId());
            articleFormDTO.setTitle(pageInfo.getRecords().get(i).getTitle());
            articleFormDTO.setContent(pageInfo.getRecords().get(i).getContent());
            articleFormDTO.setCreatetime(pageInfo.getRecords().get(i).getCreatetime());
            articleFormDTO.setEmail(userMapper.selectByUid(pageInfo.getRecords().get(i).getAuthor()));
            articleFormDTOList.add(articleFormDTO);
        }
        pageInfo_res.setRecords(articleFormDTOList);
        return pageInfo_res;
    }

    // 发表文章
    @PostMapping("/post")
    public Result postArticle(HttpServletRequest request, @RequestBody PostFormDTO postFormDTO){
        return articleService.postArticle(request, postFormDTO);
    }
}
