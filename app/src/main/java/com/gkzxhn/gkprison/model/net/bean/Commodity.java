package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by admin on 2015/12/15.
 */
public class Commodity {

    private int category_id;
    private String price;
    private int id;
    private int qty;
    private int ranking;
    private String avatar_url;
    private String description;
    private String title;
    private String barcode;     //A: 亲情电话充值;  B: 家属服务汇款给;  其他: 商品条码

    @Override
    public String toString() {
        return "Commodity{" +
                "category_id=" + category_id +
                ", price='" + price + '\'' +
                ", id=" + id +
                ", qty=" + qty +
                ", ranking=" + ranking +
                ", avatar_url='" + avatar_url + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
