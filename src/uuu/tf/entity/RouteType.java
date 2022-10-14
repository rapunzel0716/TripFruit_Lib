/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.entity;

/**
 *
 * @author Admin
 */
public enum RouteType {
    DRIVING("開車"),TRANSIT("大眾運輸"),WALKING("走路"),CUSTOM("自訂");
    
    private final String description;

    private RouteType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
}
