package com.tale.dao;

import com.tale.model.entity.Comments;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
@Component
public interface CommentsDao extends BaseDao<Comments> {

    /**
     * 根据条件查询
     * @param map
     * @return
     */
    public Comments queryByWhere(Map<String, Object> map);

    public void updateByCondition(Comments comments);
}
