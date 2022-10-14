/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import uuu.tf.entity.Schedule;
import uuu.tf.entity.TFException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import uuu.tf.entity.Course;
import uuu.tf.entity.Place;
import uuu.tf.entity.PlaceType;
import uuu.tf.entity.RouteType;
import uuu.tf.entity.TripDay;

/**
 *
 * @author Rapunzel_PC
 */
class SchedulesDAO {

    private static final String INSERT_SCHEDULE = "INSERT INTO schedules "
            + "(id,tripname,cover,member_uid) "
            + "VALUES(?,?,?,?)";
    private static final String INSERT_TRIPDAY = "INSERT INTO trip_days "
            + "(schedule_id,date) "
            + "VALUES(?,?)";
    private static final String INSERT_COURSE = "INSERT INTO courses "
            + "(schedule_id,trip_days_date,place_id,start_time,stay,route_time,route_type) "
            + "VALUES(?,?,?,?,?,?,?)";

    public void insert(Schedule schedule) throws TFException {
        if (schedule == null) {
            throw new IllegalArgumentException("建立行程時資料不得為null");
        }
        if (schedule.getMember() == null) {
            throw new IllegalArgumentException("建立行程時會員不得為空值");
        }
        if (schedule.size() == 0) {
            throw new IllegalArgumentException("建立行程時天數不得為空值");
        }
        try (
                Connection connection = RDBConnection.getConnection();//1.2 建立連線
                PreparedStatement pstmt1 = connection.prepareStatement(INSERT_SCHEDULE, Statement.RETURN_GENERATED_KEYS); //3. 準備指令pstmt1
                PreparedStatement pstmt2 = connection.prepareStatement(INSERT_TRIPDAY); //3. 準備指令pstmt2
                PreparedStatement pstmt3 = connection.prepareStatement(INSERT_COURSE); //3. 準備指令pstmt3     
                ) {
            connection.setAutoCommit(false);//自動
            try {
                pstmt1.setInt(1, schedule.getId());
                pstmt1.setString(2, schedule.getTripName());
                pstmt1.setString(3, schedule.getCover());
                pstmt1.setInt(4, schedule.getMember().getUid());
                //4.執行指令pstmt1
                pstmt1.executeUpdate();
                //5. 處理rs
                try (ResultSet rs = pstmt1.getGeneratedKeys()) {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        schedule.setId(id);
                    }
                }
                //新增TripDay
                for (TripDay day : schedule.getTripDayList()) {
                    //3.1 傳入pstmt2中?的值
                    pstmt2.setInt(1, schedule.getId());
                    if (day.getTripdate() != null) {
                        pstmt2.setString(2, day.getTripdate().toString());
                    }
                    //4. 執行指令pstmt2
                    pstmt2.executeUpdate();
                    //新增Course
                    for (Course course : day.getTripCourse()) {
                        //3.1 傳入pstmt3中?的值
                        pstmt3.setInt(1, schedule.getId());
                        if (day.getTripdate() != null) {
                            pstmt3.setString(2, day.getTripdate().toString());
                        }
                        pstmt3.setInt(3, course.getPlace().getId());
                        if (course.getStarttime() != null) {
                            pstmt3.setString(4, course.getStarttime().toString());
                        }
                        pstmt3.setInt(5, course.getStay());
                        pstmt3.setInt(6, course.getRouteTime());
                        if (course.getRouteType() != null) {
                            pstmt3.setString(7, course.getRouteType().toString());
                        } else {
                            pstmt3.setString(7, RouteType.CUSTOM.toString());
                        }
                        pstmt3.executeUpdate();
                    }
                }
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            throw new TFException("建立行程失敗", ex);
        }
    }

    private static final String SELECT_OSCHEDULES_BY_MEMBER_ID = "SELECT schedules.id as sid,schedules.tripname, schedules.cover,schedules.member_uid,schedules.share, "
            + "trip_days.date,COUNT(trip_days.date) as days "
            + "FROM schedules "
            + "LEFT JOIN trip_days ON schedules.id = trip_days.schedule_id "
            + "WHERE schedules.member_uid = ? "
            + "GROUP BY schedules.id ";

    List<Schedule> selectSchedulesByMemberUid(int memberUid) throws TFException {
        List<Schedule> list = new ArrayList<>();
        try (
                Connection connection = RDBConnection.getConnection();//1.2 取得連線
                PreparedStatement pstmt = connection.prepareStatement(SELECT_OSCHEDULES_BY_MEMBER_ID);//3.準備指令
                ) {
            //3.1傳入?的值
            pstmt.setInt(1, memberUid);

            try (
                    ResultSet rs = pstmt.executeQuery();//4.執行指令
                    ) {
                while (rs.next()) {
                    //5.處理rs
                    Schedule s = new Schedule();
                    s.setId(rs.getInt("sid"));
                    s.setCover(rs.getString("cover"));
                    s.setTripName(rs.getString("tripname"));
                    s.setShare(rs.getBoolean("share"));
                    LocalDate startDate = LocalDate.parse(rs.getString("date"));
                    int days = rs.getInt("days");
                    for (int i = 0; i < days; i++) {
                        if (i == 0) {
                            s.addTripDay(startDate);
                        } else {
                            s.addTripDay();
                        }
                    }
                    list.add(s);
                }
            }

        } catch (SQLException ex) {
            throw new TFException("查詢客戶歷史行程發生錯誤", ex);
        }
        return list;
    }

    private static final String SELECT_SCHEDULEDAY_BY_ID = "SELECT schedules.id as sid,schedules.tripname, schedules.cover,schedules.member_uid, "
            + "trip_days.date "
            + "FROM schedules "
            + "LEFT JOIN trip_days ON schedules.id = trip_days.schedule_id "
            + "WHERE schedules.id = ?";
    private static final String SELECT_SCHEDULE_BY_ID = "SELECT schedules.id as sid,schedules.tripname, schedules.cover,schedules.member_uid,\n"
            + "trip_days.date, "
            + "places.id as pid,lat,lng,places.`name`,type,address,phone,website,rating,opening_hours,photo,courses.start_time,courses.stay,courses.route_time,courses.route_type "
            + "FROM schedules "
            + "LEFT JOIN trip_days ON schedules.id = trip_days.schedule_id "
            + "LEFT JOIN courses ON trip_days.schedule_id = courses.schedule_id AND trip_days.`date` = courses.trip_days_date "
            + "LEFT JOIN places ON courses.place_id = places.id "
            + "WHERE schedules.id = ? "
            + "ORDER BY trip_days.date,courses.start_time";

    Schedule selectScheduleById(int scheduleId) throws TFException {
        Schedule s = null;
        try (
                Connection connection = RDBConnection.getConnection();//1.2 取得連線
                PreparedStatement pstmt1 = connection.prepareStatement(SELECT_SCHEDULEDAY_BY_ID);//3.準備指令pstmt1
                PreparedStatement pstmt2 = connection.prepareStatement(SELECT_SCHEDULE_BY_ID);//3.準備指令pstmt2
                ) {
            connection.setAutoCommit(false);//自動
            try {
                //3.1傳入pstmt1問號
                pstmt1.setInt(1, scheduleId);
                //4.pstmt1執行指令
                try (ResultSet rs = pstmt1.executeQuery()) {
                    //5.處理pstmt1 rs
                    while (rs.next()) {
                        if (s == null) {
                            s = new Schedule();
                            s.setId(rs.getInt("sid"));
                            s.setTripName(rs.getString("tripname"));
                            s.setCover(rs.getString("cover"));
                        }
                        LocalDate startDate = LocalDate.parse(rs.getString("date"));
                        if (s.size() == 0) {
                            s.addTripDay(startDate);
                        } else {
                            s.addTripDay();
                        }
                    }
                }

                //3.1傳入pstmt2問號
                pstmt2.setInt(1, scheduleId);
                //4.pstmt2執行指令
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    //5.處理rs
                    while (rs2.next()) {
                        if (s != null) {
                            LocalDate Date = LocalDate.parse(rs2.getString("date"));
                            Place p = new Place();
                            int pid = rs2.getInt("pid");
                            if (pid != 0) {
                                p.setId(pid);
                                p.setLat(rs2.getString("lat"));
                                p.setLng(rs2.getString("lng"));
                                p.setName(rs2.getString("name"));
                                p.setType(PlaceType.valueOf(rs2.getString("type")));
                                p.setAddress(rs2.getString("address"));
                                p.setPhone((rs2.getString("phone") != null && !(rs2.getString("phone").equals(""))) ? rs2.getString("phone") : null);
                                p.setWebsite((rs2.getString("website") != null && !(rs2.getString("website").equals(""))) ? rs2.getString("website") : null);
                                p.setRating(rs2.getFloat("rating"));
                                p.setOpening_hours((rs2.getString("opening_hours") != null && !(rs2.getString("opening_hours").equals(""))) ? rs2.getString("opening_hours").split(",") : null);
                                p.setPhoto(rs2.getString("photo"));

                                LocalTime startTime = LocalTime.parse(rs2.getString("start_time"));
                                int stay = rs2.getInt("stay");
                                int routeTime = rs2.getInt("route_time");
                                String routeType = rs2.getString("route_type");
                                s.addPlaceToDate(Date, p, startTime, stay, routeTime,routeType);
                            }
                        }
                    }
                }
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            throw new TFException("查詢客戶歷史訂單發生錯誤", ex);
        }

        return s;
    }

    private static final String DELETE_COURSE_BY_SCHEDULE_ID = "DELETE FROM courses WHERE schedule_id=?";
    private static final String DELETE_TRIP_DAY_BY_SCHEDULE_ID = "DELETE FROM trip_days WHERE schedule_id=?";
    private static final String DELETE_SCHEDULE_BY_ID = "DELETE FROM schedules WHERE id=?";

    void deleteScheduleById(int scheduleId) throws TFException {
        try (
                Connection connection = RDBConnection.getConnection();//1.2 取得連線
                PreparedStatement pstmt1 = connection.prepareStatement(DELETE_COURSE_BY_SCHEDULE_ID);//3.準備指令pstmt1
                PreparedStatement pstmt2 = connection.prepareStatement(DELETE_TRIP_DAY_BY_SCHEDULE_ID);//3.準備指令pstmt2
                PreparedStatement pstmt3 = connection.prepareStatement(DELETE_SCHEDULE_BY_ID);//3.準備指令pstmt2
                ) {
            connection.setAutoCommit(false);//自動
            try {
                //3.1傳入pstmt1問號  刪除course
                pstmt1.setInt(1, scheduleId);
                //4.pstmt1執行指令
                pstmt1.executeUpdate();

                //3.1傳入pstmt2問號  刪除trip_day
                pstmt2.setInt(1, scheduleId);
                //4.pstmt2執行指令
                pstmt2.executeUpdate();

                //3.1傳入pstmt3問號  刪除schedule
                pstmt3.setInt(1, scheduleId);
                //4.pstmt1執行指令
                pstmt3.executeUpdate();

                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            throw new TFException("刪除會員行程發生錯誤", ex);
        }

    }

    private static final String COPY_SCHEDULE_BY_ID = "INSERT INTO schedules "
            + "(id,tripName,cover,member_uid) "
            + "SELECT 0,tripName,cover,member_uid "
            + "FROM schedules "
            + "WHERE id=?";
    private static final String COPY_TRIP_DAYS_BY_ID = "INSERT INTO trip_days "
            + "(schedule_id,date) "
            + "SELECT ?,date "
            + "FROM trip_days "
            + "WHERE schedule_id=?";
    private static final String COPY_COURSE_BY_ID = "INSERT INTO courses "
            + "(schedule_id,trip_days_date,place_id,start_time,stay,route_time,route_type) "
            + "SELECT ?,trip_days_date,place_id,start_time,stay,route_time,route_type "
            + "FROM courses "
            + "WHERE schedule_id=?";

    public void copyByScheduleId(int scheduleId) throws TFException {
        try (
                Connection connection = RDBConnection.getConnection();//1.2 取得連線
                PreparedStatement pstmt1 = connection.prepareStatement(COPY_SCHEDULE_BY_ID, Statement.RETURN_GENERATED_KEYS);//3.準備指令pstmt1 RETURN_GENERATED_KEYS將新增的資料傳回KEY值
                PreparedStatement pstmt2 = connection.prepareStatement(COPY_TRIP_DAYS_BY_ID);//3.準備指令pstmt2
                PreparedStatement pstmt3 = connection.prepareStatement(COPY_COURSE_BY_ID);//3.準備指令pstmt3
                ) {
            connection.setAutoCommit(false);//自動
            try {
                int id = 0;//準備放自動給號的值
                //3.1傳入pstmt1問號  
                pstmt1.setInt(1, scheduleId);
                //4.pstmt1執行指令
                pstmt1.executeUpdate();
                //5. 處理rs 獲得自動給號
                try (ResultSet rs = pstmt1.getGeneratedKeys()) {
                    while (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
                //3.1傳入pstmt2問號  
                pstmt2.setInt(1, id);//放入自動給號
                pstmt2.setInt(2, scheduleId);
                //4.pstmt2執行指令
                pstmt2.executeUpdate();
                //3.1傳入pstmt3問號  
                pstmt3.setInt(1, id);//放入自動給號
                pstmt3.setInt(2, scheduleId);
                //4.pstmt3執行指令
                pstmt3.executeUpdate();

                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new TFException("複製行程時發生錯誤", ex);
        }
    }

    private static final String DELETE_COURSE_BY_SCHEDULE = "DELETE FROM courses WHERE schedule_id=?";
    private static final String DELETE_TRIP_DAY_BY_SCHEDULE = "DELETE FROM trip_days WHERE schedule_id=?";
    private static final String UPDATE_SCHEDULE = "UPDATE schedules "
            + "SET tripName=?,cover=? "
            + "WHERE id=?";
    private static final String RE_INSERT_TRIPDAY = "INSERT INTO trip_days "
            + "(schedule_id,date) "
            + "VALUES(?,?)";
    private static final String RE_INSERT_COURSE = "INSERT INTO courses "
            + "(schedule_id,trip_days_date,place_id,start_time,stay,route_time,route_type) "
            + "VALUES(?,?,?,?,?,?,?)";

    void updateSchedule(Schedule schedule) throws TFException {
        try (
                Connection connection = RDBConnection.getConnection();//1.2 取得連線
                PreparedStatement pstmt1 = connection.prepareStatement(DELETE_COURSE_BY_SCHEDULE);//3.準備指令pstmt1
                PreparedStatement pstmt2 = connection.prepareStatement(DELETE_TRIP_DAY_BY_SCHEDULE);//3.準備指令pstmt2
                PreparedStatement pstmt3 = connection.prepareStatement(UPDATE_SCHEDULE);//3.準備指令pstmt3
                PreparedStatement pstmt4 = connection.prepareStatement(RE_INSERT_TRIPDAY); //3. 準備指令pstmt4
                PreparedStatement pstmt5 = connection.prepareStatement(RE_INSERT_COURSE); //3. 準備指令pstmt5
                ) {
            connection.setAutoCommit(false);//自動
            try {
                //3.1傳入pstmt1問號 
                pstmt1.setInt(1, schedule.getId());
                //4.pstmt1執行指令
                pstmt1.executeUpdate();

                //3.1傳入pstmt2問號
                pstmt2.setInt(1, schedule.getId());
                //4.pstmt2執行指令
                pstmt2.executeUpdate();

                //3.1傳入pstmt3問號
                pstmt3.setString(1, schedule.getTripName());
                pstmt3.setString(2, schedule.getCover());
                pstmt3.setInt(3, schedule.getId());
                //4.pstmt3執行指令
                pstmt3.executeUpdate();

                for (TripDay day : schedule.getTripDayList()) {
                    //3.1 傳入pstmt4中?的值
                    pstmt4.setInt(1, schedule.getId());
                    if (day.getTripdate() != null) {
                        pstmt4.setString(2, day.getTripdate().toString());
                    }
                    //4. 執行指令pstmt4
                    pstmt4.executeUpdate();

                    for (Course course : day.getTripCourse()) {
                        //3.1 傳入pstmt5中?的值
                        pstmt5.setInt(1, schedule.getId());
                        if (day.getTripdate() != null) {
                            pstmt5.setString(2, day.getTripdate().toString());
                        }
                        pstmt5.setInt(3, course.getPlace().getId());
                        if (course.getStarttime() != null) {
                            pstmt5.setString(4, course.getStarttime().toString());
                        }
                        pstmt5.setInt(5, course.getStay());
                        pstmt5.setInt(6, course.getRouteTime());
                        if (course.getRouteType() != null) {
                            pstmt5.setString(7, course.getRouteType().toString());
                        } else {
                            pstmt5.setString(7, RouteType.CUSTOM.toString());
                        }
                        pstmt5.executeUpdate();
                    }
                }

                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            throw new TFException("更新會員行程發生錯誤", ex);
        }
    }
}
