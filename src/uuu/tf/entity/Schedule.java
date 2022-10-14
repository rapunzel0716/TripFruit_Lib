package uuu.tf.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Schedule {

    private int id;//PKEY
    private String tripName = "用戶行程";
    private String cover = "cover1.jpg";
    private boolean share;
    private Member member;
    private List<TripDay> trip = new LinkedList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) throws TFException {
        if (tripName != null && tripName.length() > 0) {
            this.tripName = tripName;
        } else {
            throw new TFException("行程名稱不得為空值");
        }
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) throws TFException {
        if (cover != null && cover.length() > 0) {
            this.cover = cover;
        } else {
            throw new TFException("行程封面照片不得為空值");
        }
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getFirstDay() {
        return trip.get(0).getTripdate();
    }

    public LocalDate getLastDay() {
        return trip.get(trip.size() - 1)!=null?trip.get(trip.size() - 1).getTripdate():null;
    }

    public int getDayIndex(TripDay tripDay) {
        return trip.indexOf(tripDay);
    }

    public void addTripDay() throws TFException {

        TripDay tripDay = new TripDay();
        try {
            LocalDate tripdate = (trip.get(trip.size() - 1)).getTripdate();
            tripDay.setTripdate(tripdate.plusDays(1));
        } catch (TFException ex) {
            throw ex;
        }
        trip.add(tripDay);
    }

    public void addTripDay(LocalDate date) throws TFException {
        if (date == null) {
            date = LocalDate.now();
        }
        TripDay tripDay = new TripDay();
        try {
            if (trip.isEmpty()) {
                tripDay.setTripdate(date);
            } else {
                LocalDate tripdate = (trip.get(trip.size() - 1)).getTripdate();
                tripDay.setTripdate(tripdate.plusDays(1));
            }
        } catch (TFException ex) {
            throw ex;
        }
        trip.add(tripDay);
    }

    public void addTripDay(String date) throws TFException {
        addTripDay(this.stringToLocalDate(date));
    }

    public LocalDate stringToLocalDate(String date) throws TFException {
        if (date != null) {
            date = date.replace('/', '-');
        }
        try {
            return ((date != null) ? LocalDate.parse(date) : null);
        } catch (DateTimeParseException ex) {
            throw new TFException("行程日期格式不正確");
        }
    }

    public void addTripDay(TripDay tripDay) {
        trip.add(tripDay);
    }

    //依第幾天新增景點
    public void addPlaceToDate(int day, Place place, LocalTime startTime, int stay, int routeTime,String routeType) throws TFException {
        if (day < 1) {
            throw new TFException("加入景點的天數不得為0或負數");
        }
        trip.get(day - 1).addplace(place, startTime, stay, routeTime,routeType);
    }

    //依第幾天新增景點
    public void addPlaceToDate(int day, Place place, String startTime, int stay, int routeTime,String routeType) throws TFException {
        if (startTime == null) {
            throw new TFException("開始時間不得為null");
        }
        try {
            addPlaceToDate(day, place, LocalTime.parse(startTime), stay, routeTime,routeType);
        } catch (DateTimeParseException ex) {
            throw new TFException("加入景點的日期或時間格式不正確");
        }
    }

    //依日期新增景點
    public void addPlaceToDate(LocalDate date, Place place, LocalTime startTime, int stay, int routeTime,String routeType) throws TFException {
        if (date == null || startTime == null) {
            throw new TFException("加入景點的日期或時間不得為空值");
        }
        TripDay tripDay = this.dateToTripDay(date);

        int index = trip.indexOf(tripDay);
        if (index != -1) {
            tripDay = trip.get(index);
            tripDay.addplace(place, startTime, stay, routeTime,routeType);
        }
    }

    //依日期新增景點
    public void addPlaceToDate(String date, Place place, String startTime, int stay, int routeTime,String routeType) throws TFException {
        if (date == null || startTime == null) {
            throw new TFException("加入景點的日期或時間不得為空值");
        }
        if (date != null) {
            date = date.replace('/', '-');
        }
        try {
            LocalDate date2 = LocalDate.parse(date);
            LocalTime time = LocalTime.parse(startTime);
            this.addPlaceToDate(date2, place, time, stay, routeTime,routeType);
        } catch (DateTimeParseException ex) {
            throw new TFException("加入景點的日期或時間格式不正確");
        }
    }

    //獲取某日程的開始時間
    public LocalTime getDayStartTime(LocalDate date) throws TFException {
        if (date != null) {
            if (!this.dateGetTripDay(date).isEmpty()) {
                return this.dateGetTripDay(date).getCourse(0).getStarttime();
            } else {
                throw new TFException("此日期無行程");
            }
        } else {
            throw new TFException("日期不得為空值");
        }

    }

    //設置出發日期
    public void setDepartureDate(LocalDate date) throws TFException {
        if (date == null) {
            throw new TFException("設定出發日期不可為null");
        }
        for (int x = 0; x < trip.size(); x++) {
            if (x == 0) {
                trip.get(0).setTripdate(date);
            } else {
                LocalDate dateBefore = trip.get(x - 1).getTripdate();
                trip.get(x).setTripdate(dateBefore.plusDays(1));
            }
        }
    }
    
    //設定行程內的日期開始時間
    public void updateStarTime(LocalDate date,LocalTime time) throws TFException {
        if(date == null){
            throw new TFException("設定開始時間錯誤，日期不可為null");
        }
        this.dateGetTripDay(date).updateStarTime(time);
        
    }

    //獲取日程中的第幾項景點
    public Course getCourse(LocalDate date, int index) throws TFException {
        if (date != null) {
            TripDay day = this.dateGetTripDay(date);
            if (day == null) {
                return null;
            }
            return day.getCourse(index);
        } else {
            throw new TFException("date不可為NULL");
        }
    }

    /*
    //獲取第幾天中的最後一項景點
    public Course getLastCourse(int dateindex) throws TFException {
        TripDay day = this.trip.get(dateindex);
        if (day == null) {
            throw new TFException("此行程無此天");
        }
        return day.getLastCourse();
    }*/

    //刪除天數
    public int removeDate(LocalDate date) throws TFException {
        int index = trip.indexOf(this.dateToTripDay(date));
        if (index != -1 && trip.size() > 1) {
            for (int x = index; x < trip.size(); x++) {
                LocalDate tripdate = trip.get(x).getTripdate().minusDays(1);
                trip.get(x).setTripdate(tripdate);
            }
            trip.remove(index);
        }
        return index;
    }

    public int removeDate(String date) throws TFException {
        return removeDate(this.stringToLocalDate(date));
    }

    //刪除此天數所有行程
    public void clearCourse(LocalDate date) throws TFException {
        this.dateGetTripDay(date).clear();
    }

    //刪除某日程的景點
    public void removePlace(LocalDate date, Place p, LocalTime starttime) throws TFException {
        if (p == null) {
            throw new TFException("刪除景點時景點不可為空值");
        }
        if (starttime == null) {
            throw new TFException("刪除景點時開始時間不可為空值");
        }
        this.dateGetTripDay(date).removeplace(p, starttime);
    }

    //刪除某日程的景點
    public void removePlace(LocalDate date, Course course) throws TFException {
        this.dateGetTripDay(date).removeplace(course);
    }

    public void updateRouteTime(TripDay day, Course item, int routeTime,String routeType) throws TFException {
        int index = trip.indexOf(day);
        if (index == -1) {
            throw new TFException("更新路線時間錯誤，此行程無此日期");
        }
        trip.get(index).updateRouteTime(item, routeTime,routeType);
    }

    public void updateRouteTime(LocalDate day, Place p, LocalTime startTime, int routeTime,String routeType) throws TFException {
        if (day == null) {
            throw new TFException("設置路線時間錯誤，日期不可為null");
        }
        if (p == null) {
            throw new TFException("設置路線時間錯誤，景點不可為null");
        }
        if (startTime == null) {
            throw new TFException("設置路線時間錯誤，日期不可為null");
        }
        TripDay date = dateToTripDay(day);
        Course c = new Course();
        c.setPlace(p);
        c.setStarttime(startTime);

        this.updateRouteTime(date, c, routeTime,routeType);
    }

    public void updateStay(TripDay day, Course item, int stay) throws TFException {
        int index = trip.indexOf(day);
        if (index == -1) {
            throw new TFException("設置停留時間錯誤，此行程無此日期");
        }
        trip.get(index).updateStay(item, stay);
    }

    public void updateStay(LocalDate day, Place p, LocalTime startTime, int stay) throws TFException {
        if (day == null) {
            throw new TFException("設置停留時間錯誤，日期不可為null");
        }
        if (p == null) {
            throw new TFException("設置停留時間錯誤，景點不可為null");
        }
        if (startTime == null) {
            throw new TFException("設置停留時間錯誤，日期不可為null");
        }
        TripDay date = dateToTripDay(day);
        Course c = new Course();
        c.setPlace(p);
        c.setStarttime(startTime);

        this.updateStay(date, c, stay);
    }

    //日期格式轉TripDay
    private TripDay dateToTripDay(LocalDate date) throws TFException {
        if(date==null){
            throw new TFException("日期不可為null");
        }        
        TripDay day = new TripDay();
        try {
            day.setTripdate(date);
        } catch (TFException ex) {
            throw ex;
        }
        return day;
    }

    //以日期獲得集合內的TripDate    
    private TripDay dateGetTripDay(LocalDate date) throws TFException {
        TripDay day = new TripDay();
        try {
            day.setTripdate(date);
        } catch (TFException ex) {
            throw ex;
        }
        int index = trip.indexOf(day);
        if (index != -1) {
            return trip.get(index);
        } else {
            throw new TFException("此行程無此日期");
        }
    }

    public List<TripDay> getTripDayList() {
        return this.trip;
    }

    public int size() {
        return trip.size();
    }

    public boolean isEmpty() {
        return trip.isEmpty();
    }

    @Override
    public String toString() {
        return "行程id=" + id + ", 行程名稱=" + tripName + ", 行程封面=" + cover + ", 分享=" + share + ", 會員=" + member + ", 開始日期=" + this.getFirstDay() + ", 天數=" + this.size() + "\n" + trip;
    }

}
