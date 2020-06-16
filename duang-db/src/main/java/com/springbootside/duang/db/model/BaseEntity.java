package com.springbootside.duang.db.model;

import com.springbootside.duang.db.annotation.Param;

import java.io.Serializable;
import java.util.Date;

/**
 * 所有Entity类的基类
 *
 * @author Laotang
 * @version 1.0
 */
public abstract class BaseEntity extends IdEntity {

    public final static String CREATEUSERID_FIELD = "createUserId";
    public final static String CREATETIME_FIELD = "createTime";
    public final static String UPDATEUSERID_FIELD = "updateUserId";
    public final static String UPDATETIME_FIELD = "updateTime";
    public final static String SOURCE_FIELD = "source";
    public final static String STATUS_FIELD = "status";

    @Param(name = "创建者ID", label = "创建者ID", desc = "该记录的创建者ID", isHidden = true)
    private Serializable createUserId;

    @Param(name = "创建时间", label = "创建时间", desc = "该记录的创建时间", isHidden = true)
    private Date createTime;

    @Param(name = "更新者ID", label = "更新者ID", desc = "该记录的更新者ID", isHidden = true)
    private Serializable updateUserId;

    @Param(name = "更新时间", label = "更新时间", desc = "该记录的更新时间", isHidden = true)
    private Date updateTime;

    @Param(name = "记录来源", label = "记录来源", desc = "该记录的来源", isHidden = true)
    private Integer source;

    @Param(name = "记录状态", label = "记录状态", desc = "该记录的最新状态，1代表记录已经删除", isHidden = true)
    private Integer status = 0;

    public BaseEntity() {
    }

    public BaseEntity(Serializable createUserId, Date createTime, Serializable updateUserId, Date updateTime, Integer source, Integer status) {
        this.createUserId = createUserId;
        this.createTime = createTime;
        this.updateUserId = updateUserId;
        this.updateTime = updateTime;
        this.source = source;
        this.status = status;
    }

    public Serializable getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Serializable createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Serializable getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Serializable updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
