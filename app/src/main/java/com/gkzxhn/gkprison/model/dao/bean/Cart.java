package com.gkzxhn.gkprison.model.dao.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by 方 on 2017/4/13.
 */

@Entity
public class Cart {
    @Id(autoincrement = true)
    private Long id;
    private String time;    //交易时间
    private String out_trade_no;    //订单号
    private Boolean isfinish;   //是否完成
    private String total_money; //总交易额
    private Integer count;  //交易数量
    private Boolean remittance; //是否是汇款
    private String payment_type;    //支付方式
    private String barcode;         //A: 亲情电话充值;  B: 家属服务汇款给;  其他: 商品条码
    // 对多。实体ID对应外联实体属性 referencedJoinProperty
    @ToMany(referencedJoinProperty = "cart_id")
    private List<LineItems> mLineItemses;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1743358803)
    private transient CartDao myDao;

    @Generated(hash = 1054171374)
    public Cart(Long id, String time, String out_trade_no, Boolean isfinish, String total_money,
            Integer count, Boolean remittance, String payment_type, String barcode) {
        this.id = id;
        this.time = time;
        this.out_trade_no = out_trade_no;
        this.isfinish = isfinish;
        this.total_money = total_money;
        this.count = count;
        this.remittance = remittance;
        this.payment_type = payment_type;
        this.barcode = barcode;
    }
    @Generated(hash = 1029823171)
    public Cart() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getOut_trade_no() {
        return this.out_trade_no;
    }
    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
    public Boolean getIsfinish() {
        return this.isfinish;
    }
    public void setIsfinish(Boolean isfinish) {
        this.isfinish = isfinish;
    }
    public String getTotal_money() {
        return this.total_money;
    }
    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }
    public Integer getCount() {
        return this.count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public Boolean getRemittance() {
        return this.remittance;
    }
    public void setRemittance(Boolean remittance) {
        this.remittance = remittance;
    }
    public String getPayment_type() {
        return this.payment_type;
    }
    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 263450854)
    public List<LineItems> getMLineItemses() {
        if (mLineItemses == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LineItemsDao targetDao = daoSession.getLineItemsDao();
            List<LineItems> mLineItemsesNew = targetDao._queryCart_MLineItemses(id);
            synchronized (this) {
                if (mLineItemses == null) {
                    mLineItemses = mLineItemsesNew;
                }
            }
        }
        return mLineItemses;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1803797828)
    public synchronized void resetMLineItemses() {
        mLineItemses = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    public String getBarcode() {
        return this.barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1619816777)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCartDao() : null;
    }
}
