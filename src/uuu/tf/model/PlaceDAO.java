/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uuu.tf.entity.Place;
import uuu.tf.entity.PlaceType;
import uuu.tf.entity.Schedule;
import uuu.tf.entity.TFException;

/**
 *
 * @author Rapunzel_PC
 */
class PlaceDAO {

    private static final String SELECT_ALL_PLACES = "SELECT id, lat, lng, name, type, address, phone, website, rating, opening_hours, photo FROM places;";

    //查詢所有產品
    List<Place> selectAllPlaces() throws TFException {
        List<Place> list = new ArrayList<>();

        try (
                Connection connection = RDBConnection.getConnection();//1.2.取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_PLACES);//3.準備SQL指令
                ResultSet rs = pstmt.executeQuery();//4執行指令			
                ) {
            //5處理rs
            while (rs.next()) {
                Place p = new Place();
                p.setId(rs.getInt("id"));
                p.setLat(rs.getString("lat"));
                p.setLng(rs.getString("lng"));
                p.setName(rs.getString("name"));
                p.setType(PlaceType.valueOf(rs.getString("type")));
                p.setAddress(rs.getString("address"));
                p.setPhone((rs.getString("phone") != null && !(rs.getString("phone").equals(""))) ? rs.getString("phone") : null);
                p.setWebsite((rs.getString("website") != null && !(rs.getString("website").equals(""))) ? rs.getString("website") : null);
                p.setRating(rs.getFloat("rating"));
                p.setOpening_hours((rs.getString("opening_hours") != null && !(rs.getString("opening_hours").equals(""))) ? rs.getString("opening_hours").split(",") : null);
                p.setPhoto(rs.getString("photo"));

                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new TFException("查詢產品失敗", e);
        }

        return list;
    }

    private static final String SELECT_Places_BY_NAME = "SELECT id, lat, lng, name, type, address, phone, website, rating, opening_hours, photo FROM places WHERE name like ?";

    List<Place> selectPlacesByName(String search) throws TFException {
        List<Place> list = new ArrayList<>();

        try (
                Connection connection = RDBConnection.getConnection();//1.2.取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_Places_BY_NAME);//3.準備SQL指令
                ) {
            //3.1傳入?的值
            pstmt.setString(1, '%' + search + '%');
            try (
                    ResultSet rs = pstmt.executeQuery();//4執行指令	
                    ) {
                while (rs.next()) {
                    Place p = new Place();
                    p.setId(rs.getInt("id"));
                    p.setLat(rs.getString("lat"));
                    p.setLng(rs.getString("lng"));
                    p.setName(rs.getString("name"));
                    p.setType(PlaceType.valueOf(rs.getString("type")));
                    p.setAddress(rs.getString("address"));
                    p.setPhone((rs.getString("phone") != null && !(rs.getString("phone").equals(""))) ? rs.getString("phone") : null);
                    p.setWebsite((rs.getString("website") != null && !(rs.getString("website").equals(""))) ? rs.getString("website") : null);
                    p.setRating(rs.getFloat("rating"));
                    p.setOpening_hours((rs.getString("opening_hours") != null && !(rs.getString("opening_hours").equals(""))) ? rs.getString("opening_hours").split(",") : null);
                    p.setPhoto(rs.getString("photo"));

                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            
            throw new TFException("使用搜尋名稱查詢景點失敗", ex);
        }
        return list;
    }

    private static final String SELECT_Places_BY_NAME_AND_TYPE = "SELECT id, lat, lng, name, type, address, phone, website, rating, opening_hours, photo FROM places WHERE name like ? AND type = ?";

    List<Place> selectPlacesByNameAndType(String search, String type) throws TFException {
        List<Place> list = new ArrayList<>();

        try (
                Connection connection = RDBConnection.getConnection();//1.2.取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_Places_BY_NAME_AND_TYPE);//3.準備SQL指令
                ) {
            //3.1傳入?的值
            pstmt.setString(1, '%' + search + '%');
            pstmt.setString(2, type);
            try (
                    ResultSet rs = pstmt.executeQuery();//4執行指令	
                    ) {
                while (rs.next()) {
                    Place p = new Place();
                    p.setId(rs.getInt("id"));
                    p.setLat(rs.getString("lat"));
                    p.setLng(rs.getString("lng"));
                    p.setName(rs.getString("name"));
                    p.setType(PlaceType.valueOf(rs.getString("type")));
                    p.setAddress(rs.getString("address"));
                    p.setPhone((rs.getString("phone") != null && !(rs.getString("phone").equals(""))) ? rs.getString("phone") : null);
                    p.setWebsite((rs.getString("website") != null && !(rs.getString("website").equals(""))) ? rs.getString("website") : null);
                    p.setRating(rs.getFloat("rating"));
                    p.setOpening_hours((rs.getString("opening_hours") != null && !(rs.getString("opening_hours").equals(""))) ? rs.getString("opening_hours").split(",") : null);
                    p.setPhoto(rs.getString("photo"));

                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TFException("使用搜尋名稱查詢景點失敗", ex);
        }
        return list;
    }

    private static final String SELECT_PLACE_BY_ID = "SELECT id, lat, lng, name, type, address, phone, website, rating, opening_hours, photo FROM places WHERE id = ?";

    Place selectPlaceById(String id) throws TFException {
        Place p = null;
        try (
                Connection connection = RDBConnection.getConnection();//1.2.取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_PLACE_BY_ID);//3.準備指令
                ) {
            pstmt.setString(1, id);//3.1傳入?的值
            try (
                    ResultSet rs = pstmt.executeQuery();) {
                while (rs.next()) {
                    p = new Place();
                    p.setId(rs.getInt("id"));
                    p.setLat(rs.getString("lat"));
                    p.setLng(rs.getString("lng"));
                    p.setName(rs.getString("name"));
                    p.setType(PlaceType.valueOf(rs.getString("type")));
                    p.setAddress(rs.getString("address"));
                    p.setPhone((rs.getString("phone") != null && !(rs.getString("phone").equals(""))) ? rs.getString("phone") : null);
                    p.setWebsite((rs.getString("website") != null && !(rs.getString("website").equals(""))) ? rs.getString("website") : null);
                    p.setRating(rs.getFloat("rating"));
                    p.setOpening_hours((rs.getString("opening_hours") != null && !(rs.getString("opening_hours").equals(""))) ? rs.getString("opening_hours").split(",") : null);
                    p.setPhoto(rs.getString("photo"));

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlaceDAO.class.getName()).log(Level.SEVERE, "使用景點代號查詢產品失敗", ex);
            throw new TFException("使用景點代號查詢產品失敗");
        }
        return p;
    }

    private static final String SELECT_PLACE_BY_TYPE_AND_LATLNG = "SELECT id, lat, lng, name, type, address, phone, website, rating, opening_hours, photo FROM places "
            + "WHERE lng<? AND  lng > ? AND lat>? AND lat<? AND type=?  " //東西南北
            + "LIMIT 100";

    List<Place> selectPlacesByTypeAndLatLng(String type, String[] latlng) throws TFException {
        List<Place> list = new ArrayList<>();
        try (
                Connection connection = RDBConnection.getConnection();//1.2.取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_PLACE_BY_TYPE_AND_LATLNG);//3.準備SQL指令
                ) {
            //3.1傳入?的值
            pstmt.setString(1, latlng[0]);
            pstmt.setString(2, latlng[1]);
            pstmt.setString(3, latlng[2]);
            pstmt.setString(4, latlng[3]);
            pstmt.setString(5, type);
            try (
                    ResultSet rs = pstmt.executeQuery();//4執行指令	
                    ) {
                while (rs.next()) {
                    Place p = new Place();
                    p.setId(rs.getInt("id"));
                    p.setLat(rs.getString("lat"));
                    p.setLng(rs.getString("lng"));
                    p.setName(rs.getString("name"));
                    p.setType(PlaceType.valueOf(rs.getString("type")));
                    p.setAddress(rs.getString("address"));
                    p.setPhone((rs.getString("phone") != null && !(rs.getString("phone").equals(""))) ? rs.getString("phone") : null);
                    p.setWebsite((rs.getString("website") != null && !(rs.getString("website").equals(""))) ? rs.getString("website") : null);
                    p.setRating(rs.getFloat("rating"));
                    p.setOpening_hours((rs.getString("opening_hours") != null && !(rs.getString("opening_hours").equals(""))) ? rs.getString("opening_hours").split(",") : null);
                    p.setPhoto(rs.getString("photo"));

                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new TFException("使用搜尋經緯度及類別查詢景點失敗", ex);
        }
        return list;
    }
}
