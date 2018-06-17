package com.tale.service;

import com.tale.model.Bo.ArchiveBo;
import com.tale.model.Bo.StatisticsBo;
import com.tale.model.Bo.BackResponseBo;
import com.tale.model.entity.Comments;
import com.tale.model.entity.Contents;
import com.tale.model.entity.Users;

import java.util.List;

/**
 * Created by hpxl on 1/5/18.
 */
public interface SiteService {

    public List<Comments> recentComments(int limit);

    public List<Contents> recentContents(int limit);

    public StatisticsBo getStatistics();

    public List<ArchiveBo> getArchives();

    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception;

    public void initSite(Users users);
}
