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
public enum PlaceType {
    view("景點"),food("美食"),shop("購物"),stay("住宿");    
    
    private final String description;

    private PlaceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
