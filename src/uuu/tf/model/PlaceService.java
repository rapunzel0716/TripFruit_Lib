/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.util.List;
import uuu.tf.entity.Place;
import uuu.tf.entity.Schedule;
import uuu.tf.entity.TFException;

/**
 *
 * @author Rapunzel_PC
 */
public class PlaceService {

    private PlaceDAO dao = new PlaceDAO();

    public List<Place> getAllPlaces() throws TFException {
        List<Place> list = dao.selectAllPlaces();
        return list;
    }

    public List<Place> searchPlacesByName(String search) throws TFException {
        return dao.selectPlacesByName(search);
    }

    public List<Place> selectPlacesByNameAndType(String search, String type) throws TFException {
        return dao.selectPlacesByNameAndType(search, type);
    }

    public Place getPlaceById(String id) throws TFException {
        return dao.selectPlaceById(id);
    }

    public List<Place> searchPlacesByTypeAndLatLng(String type, String[] latlng) throws TFException {
        if (latlng == null || latlng.length != 4) {
            throw new TFException("經緯度格式不正確");
        } else {
            return dao.selectPlacesByTypeAndLatLng(type, latlng);
        }
    }

}
