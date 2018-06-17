package com.tale.dao;

import com.tale.model.Bo.ArchiveBo;
import com.tale.model.entity.Contents;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by hpxl on 4/3/18.
 */
@Component
public interface ContentsDao extends BaseDao<Contents> {

    /**
     * 根据条件查询
     * @param map
     * @return
     */
    public Contents queryByWhere(Map<String, Object> map);

    public void updateByCondition(Contents contents);

    List<ArchiveBo> findReturnArchiveBo();

    List<Contents> findByCatalog(Integer mid);
}
