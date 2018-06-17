package com.tale.service;

import com.tale.model.entity.Attach;
import java.util.List;

/**
 * Created by hpxl on 2/4/18.
 */
public interface AttachService {
    public void save(String fname, String ftype, String fkey, Integer authorId);

    public List<Attach> getAttachs(Integer page, Integer limit);

    public Attach selectById(Integer id);

    public void deleteById(Integer id);
}
