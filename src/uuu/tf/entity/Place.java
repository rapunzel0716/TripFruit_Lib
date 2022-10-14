/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.entity;

/**
 *
 * @author Rapunzel_PC
 */
public class Place {
    private int id; //必要欄位，PKey，AUTO_INCREMENT
    private String lat;//必要屬性
    private String lng;//必要屬性
    private String name;//地方名稱 //必要屬性
    private PlaceType type;//旅遊類型 //必要屬性
    private String address; //必要屬性
    private String phone;
    private String website;
    private float rating; //必要屬性
    private String[] opening_hours;
    private String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) throws TFException{
        if(id>0)
            this.id = id;
        else{ 
            throw new TFException("id不得為0或負值");
        }
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) throws TFException{
        if(lat!=null && lat.length()>0)
            this.lat = lat;
        else
            throw new TFException("lat需符合座標值");
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) throws TFException{
        if(lng!=null && lng.length()>0)
            this.lng = lng;
        else
            throw new TFException("lng需符合座標值");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws TFException{
        if(name !=null && name.length()>0)
            this.name = name;
        else{
            throw new TFException("請輸入地點名稱");
        }
    }

    public PlaceType getType() {
        return type;
    }

    public void setType(PlaceType type) throws TFException{
        if(type != null)
            this.type = type;
        else{
            throw new TFException("地點類型不得為null");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) throws TFException{
        if(address != null)
            this.address = address;
        else
            throw new TFException("地址不得為null");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) throws TFException{
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) throws TFException{
        this.website = website;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) throws TFException{
        if(rating>=0 && rating<=5)
            this.rating = rating;
        else
            throw new TFException("須符合評分分數");
    }

    public String[] getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(String[] opening_hours) throws TFException{
        this.opening_hours = opening_hours;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) throws TFException{
        this.photo = photo;
    }
    
    @Override
    public String toString() {
        return "景點資訊 " + "id=" + id + ", 名稱=" + name + ", 緯度=" + lat + ", 經度=" + lng+ ", address=" + address 
                + ", phone=" + phone + ", website=" + website + ", rating=" + rating + ", opening_hours=" + opening_hours
                + ", photo=" + photo + ", type=" + type ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Place other = (Place) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
