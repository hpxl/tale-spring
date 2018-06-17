package com.tale.dto;

import com.tale.model.entity.Metas;

/**
 * Created by hpxl on 19/4/18.
 */
public class MetaDto extends Metas {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
