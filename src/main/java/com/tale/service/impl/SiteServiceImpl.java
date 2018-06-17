package com.tale.service.impl;

import com.tale.constant.TaleConst;
import com.tale.controller.admin.AttachController;
import com.tale.dao.AttachDao;
import com.tale.dao.CommentsDao;
import com.tale.dao.ContentsDao;
import com.tale.dao.MetasDao;
import com.tale.dto.LogActions;
import com.tale.exception.TipException;
import com.tale.model.Bo.ArchiveBo;
import com.tale.model.Bo.BackResponseBo;
import com.tale.model.Bo.StatisticsBo;
import com.tale.dto.Types;
import com.tale.model.entity.*;
import com.tale.service.*;
import com.tale.utils.DateKit;
import com.tale.utils.TaleUtils;
import com.tale.utils.ZipUtils;
import com.tale.utils.backup.Backup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by hpxl on 1/5/18.
 */
@Service("siteService")
public class SiteServiceImpl implements SiteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ContentsService contentsService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private ContentsDao contentsDao;

    @Autowired
    private CommentsDao commentsDao;

    @Autowired
    private AttachDao attachDao;

    @Autowired
    private MetasDao metasDao;

    public List<Comments> recentComments(int limit) {
        LOGGER.debug("Enter recentComments method:limit={}", limit);
        if (limit < 0 || limit > 10) {
            limit = 10;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", limit);
        map.put("order", "created desc");
        List<Comments> comments = commentsService.queryList(map);
        LOGGER.debug("Exit recentComments method");
        return comments;
    }

    public List<Contents> recentContents(int limit) {
        LOGGER.debug("Enter recentContents method:limit={}", limit);
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", limit);
        List<Contents> contents = contentsService.queryList(map);
        LOGGER.debug("Exit recentContents method");
        return contents;
    }

    @Override
    public StatisticsBo getStatistics() {
        LOGGER.debug("Enter getStatistics method");
        StatisticsBo statisticsBo = new StatisticsBo();

        Map<String, Object> map;
        map = new HashMap<String, Object>();
        map.put("type", Types.ARTICLE);
        map.put("status", Types.PUBLISH);
        Long articles = Long.valueOf(contentsDao.queryTotal(map));

        Long comments = Long.valueOf(commentsDao.queryTotal());

        Long attaches = Long.valueOf(attachDao.queryTotal());

        map = new HashMap<String, Object>();
        map.put("type", Types.LINK);
        Long links = Long.valueOf(metasDao.queryTotal(map));

        statisticsBo.setArticles(articles);
        statisticsBo.setComments(comments);
        statisticsBo.setAttachs(attaches);
        statisticsBo.setLinks(links);
        LOGGER.debug("Exit getStatistics method");
        return statisticsBo;
    }

    @Override
    public List<ArchiveBo> getArchives() {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveBo> archives = contentsDao.findReturnArchiveBo();
        if (null != archives) {
            Map<String, Object> map;
            for (ArchiveBo archive : archives) {
                map = new HashMap<String, Object>();
                map.put("type", Types.ARTICLE);
                map.put("status", Types.PUBLISH);
                map.put("order", "created desc");

                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start_time = DateKit.getUnixTimeByDate(sd);
                int end_time = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd , 1)) - 1;
                map.put("start_time", start_time);
                map.put("end_time", end_time);
                List<Contents> contents = contentsDao.queryList(map);
                archive.setArticles(contents);
            }
        }

        LOGGER.debug("Exit getArchives method");
        return archives;
    }

    @Override
    public void initSite(Users users) {
        String pwd = TaleUtils.MD5encode(users.getUsername() + users.getPassword());
        users.setPassword(pwd);
        users.setScreenName(users.getUsername());
        users.setCreated(DateKit.getCurrentUnixTime());
        Integer uid = userService.save(users);

        try {
            String cp = SiteService.class.getClassLoader().getResource("").getPath();
            File lock = new File(cp + "install.lock");
            lock.createNewFile();
            TaleConst.INSTALLED = Boolean.TRUE;
            logsService.insertLog(LogActions.INIT_SITE.getAction(), null, "", uid.intValue());
        } catch (Exception e) {
            throw new TipException("初始化站点失败");
        }
    }

    @Override
    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponseBo backResponse = new BackResponseBo();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if (!(new File(bk_path)).isDirectory()) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        }
        if (bk_type.equals("db")) {
            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if (!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(TaleUtils.getNewDataSource().getConnection());
            String sqlContent = backup.execute();

            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            final String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);
            if (!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();

            backResponse.setSqlPath("upload/" + zipFile);

            // 10秒后删除备份文件
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }
        return backResponse;
    }

    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch (IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if(null != os) {
                try {
                    os.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }
    }
}
