package com.gkzxhn.gkprison.model.dao.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by 方 on 2017/4/13.
 */

@Entity
public class LineItems {
    @Id(autoincrement = true)
    private Long id;
    private Integer Items_id;     //列表的Item id
    private Long cart_id;      //购物车id
    private Integer qty;        //数量
    private String total_money;   //总交易额
    private Integer position;      //列表位置position
    private String price;       //单价
    private String title;       //标题
    @ToOne(joinProperty = "cart_id")
    private Cart cart;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1384567995)
    private transient LineItemsDao myDao;
    @Generated(hash = 1185776951)
    public LineItems(Long id, Integer Items_id, Long cart_id, Integer qty, String total_money,
            Integer position, String price, String title) {
        this.id = id;
        this.Items_id = Items_id;
        this.cart_id = cart_id;
        this.qty = qty;
        this.total_money = total_money;
        this.position = position;
        this.price = price;
        this.title = title;
    }
    @Generated(hash = 832398772)
    public LineItems() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getItems_id() {
        return this.Items_id;
    }
    public void setItems_id(Integer Items_id) {
        this.Items_id = Items_id;
    }
    public Long getCart_id() {
        return this.cart_id;
    }
    public void setCart_id(Long cart_id) {
        this.cart_id = cart_id;
    }
    public Integer getQty() {
        return this.qty;
    }
    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getTotal_money() {
        return this.total_money;
    }
    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }
    public Integer getPosition() {
        return this.position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Generated(hash = 1891412980)
    private transient Long cart__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1081294486)
    public Cart getCart() {
        Long __key = this.cart_id;
        if (cart__resolvedKey == null || !cart__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CartDao targetDao = daoSession.getCartDao();
            Cart cartNew = targetDao.load(__key);
            synchronized (this) {
                cart = cartNew;
                cart__resolvedKey = __key;
            }
        }
        return cart;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1054085037)
    public void setCart(Cart cart) {
        synchronized (this) {
            this.cart = cart;
            cart_id = cart == null ? null : cart.getId();
            cart__resolvedKey = cart_id;
        }
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1567731477)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLineItemsDao() : null;
    }
    
}
