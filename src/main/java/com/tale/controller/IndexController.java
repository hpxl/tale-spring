package com.tale.controller;

import com.tale.constant.TaleConst;
import com.tale.dto.ErrorCode;
import com.tale.dto.MetaDto;
import com.tale.model.Bo.ArchiveBo;
import com.tale.model.Bo.RestResponseBo;
import com.tale.dto.Types;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Metas;
import com.tale.service.CommentsService;
import com.tale.service.ContentsService;
import com.tale.model.Bo.CommentsBo;
import com.tale.service.MetasService;
import com.tale.service.SiteService;
import com.tale.utils.IPKit;
import com.tale.utils.PatternKit;
import com.tale.utils.TaleUtils;
import com.github.pagehelper.PageInfo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by hpxl on 8/5/18.
 */
@Controller
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private MetasService metasService;

    @Autowired
    private SiteService siteService;

    /**
     * 首页
     * @param request
     * @param limit
     * @return
     */
    @GetMapping(value = "/")
    public String index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.index(request, 1, limit);
    }

    /**
     * 首页分页
     * @param request request
     * @param p 第几页
     * @param limit 每页大小
     * @return
     */
    @GetMapping(value = "page/{p}")
    public String index(HttpServletRequest request, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 0 || p > TaleConst.MAX_PAGE ? 1 : p;
        PageInfo<Contents> articles = contentsService.getContents(p, limit);
        request.setAttribute("articles", articles);
        if (p > 1) {
            this.title(request, "第" + p + "页");
        }
        return this.render("index");
    }

    /**
     * 文章页
     * @param request 请求
     * @param cid 主键
     * @return
     */
    @GetMapping(value={"article/{cid}", "article/{cid}.html"})
    public String getArticle(HttpServletRequest request, @PathVariable String cid) {
        Contents contents = contentsService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);

        // update article hit
        updateArticleHit(contents);

        return this.render("post");
    }

    /**
     * 发表评论
     * @param request
     * @param response
     * @param cid
     * @param coid
     * @param author
     * @param mail
     * @param url
     * @param text
     * @param _csrf_token
     * @return
     */
    @PostMapping(value = "comment")
    @ResponseBody
    public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam Integer cid, @RequestParam Integer coid,
                                  @RequestParam String author, @RequestParam String mail,
                                  @RequestParam String url, @RequestParam String text, @RequestParam String _csrf_token) {

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        String token = cache.hget(Types.CSRF_TOKEN, _csrf_token);
        if (StringUtils.isBlank(token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        if (null == cid || StringUtils.isBlank(text)) {
            return RestResponseBo.fail("请输入完整后评论");
        }

        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return RestResponseBo.fail("姓名过长");
        }

        if (StringUtils.isNotBlank(mail) && !TaleUtils.isEmail(mail)) {
            return RestResponseBo.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
            return RestResponseBo.fail("请输入正确的URL格式");
        }

        if (text.length() > 200) {
            return RestResponseBo.fail("请输入200个字符以内的评论");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY, val);
        if (null != count && count > 0) {
            return RestResponseBo.fail("您发表评论太快了，请过会再试");
        }

        author = TaleUtils.cleanXSS(author);
        text = TaleUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        Comments comments = new Comments();
        comments.setAuthor(author);
        comments.setCid(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setContent(text);
        comments.setMail(mail);
        comments.setParent(coid);

        try {
            String result = commentsService.insertComment(comments);
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            cache.hset(Types.COMMENTS_FREQUENCY, val, 1, 60);
            if (!TaleConst.SUCCESS_RESULT.equals(result)) {
                return RestResponseBo.fail(result);
            }
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "评论发布失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
    }

    @GetMapping(value="links")
    public String links(HttpServletRequest request) {
        List<Metas> links = metasService.getMetas(Types.LINK);
        request.setAttribute("links", links);
        return this.render("links");
    }

    /**
     * 搜索页
     *
     * @param request
     * @param keyword
     * @param limit
     * @return
     */
    @GetMapping(value = {"/search/{keyword}", "/search/{keyword}.html"})
    public String search(HttpServletRequest request, @PathVariable String keyword,
                         @RequestParam(value="limit", defaultValue = "12") int limit) {
        return this.search(request, keyword, 1, limit);
    }

    @GetMapping(value = "search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @RequestParam int page,
                         @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > TaleConst.MAX_PAGE ? 1 : page;
        PageInfo<Contents> articles = contentsService.getArticles(keyword, page, limit);
        request.setAttribute("articles", articles);
        request.setAttribute("type", "搜索");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    @GetMapping(value={"/{cid}", "/{cid}.html"})
    public String page(HttpServletRequest request, @PathVariable String cid) {
        Contents contents = contentsService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }

        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);

        // update article hit
        updateArticleHit(contents);

        return this.render("page");
    }

    /**
     * 文章归档页
     * @param request
     * @return
     */
    @GetMapping(value="archives")
    public String getArchives(HttpServletRequest request) {
        List<ArchiveBo> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return this.render("archives");
    }

    private void completeArticle(HttpServletRequest request, Contents contents) {
        if (null != contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentsBo> commentsPaginator = commentsService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
    }

    private void updateArticleHit(Contents contents) {
        Contents temp = new Contents();
        temp.setCid(contents.getCid());
        temp.setHits(contents.getHits() + 1);
        contentsService.updateContentByCid(temp);
    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

    /**
     * 分类页
     *
     * @param request
     * @param keyword
     * @param limit
     * @return
     */
    @GetMapping(value="category/{keyword}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value="limit", defaultValue = "12") int limit) {
        return this.categories(request, keyword, 1, limit);
    }

    @GetMapping(value="category/{keyword}/{page}")
    public String categories(HttpServletRequest request, @PathVariable String keyword,
                             @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > TaleConst.MAX_PAGE ? 1 : page;
        MetaDto metaDto = metasService.getMetas(Types.CATEGORY, keyword);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<Contents> contentsPaginator = contentsService.getArticles(metaDto.getMid(), page, limit);
        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 标签页
     *
     * @param request
     * @param name
     * @param limit
     * @return
     */
    @GetMapping(value="tag/{name}")
    public String tags(HttpServletRequest request, @PathVariable String name,
                       @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, name, 1, limit);
    }

    /**
     * 标签分页
     *
     * @param request
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value="tag/{name}/{page}")
    public String tags(HttpServletRequest request, @PathVariable String name, @PathVariable int page,
                       @RequestParam(value="limit", defaultValue = "12") int limit) {
        page = page < 0 || page > TaleConst.MAX_PAGE ? 1 : page;
        // 对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        MetaDto metaDto = metasService.getMetas(Types.TAG, name);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<Contents> contentsPaginator = contentsService.getArticles(metaDto.getMid(), page, limit);
        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", name);
        return this.render("page-category");
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}