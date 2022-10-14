package uuu.tf.entity;

import java.time.LocalTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Course {

    //private int scheduleId;//PKEY
    //private LocalDate date;//PKEY
    private LocalTime starttime;//PKEY  景點開始時間
    private int stay = 0;//景點停留時間(分)
    private Place place;//PKEY
    private int routeTime;//路線時間(s)
    private RouteType routeType = RouteType.CUSTOM;//路線設定類型

    public int getRouteTime() {
        return routeTime;
    }

    public String getRouteTimeString() {
        int hour = (routeTime / 60) / 60;
        int minute = (routeTime / 60)%60;
        

        String time = (hour == 0 ? "" : (hour + "時")) + (minute == 0 ? "" : (minute + "分"));
        if (time.equals("")) {
            return "0分";
        }

        return time;
    }

    public void setRouteTime(int routeTime) {
        this.routeTime = routeTime;
    }

    public LocalTime getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalTime starttime) throws TFException {
        if (starttime != null) {
            this.starttime = starttime;
        } else {
            throw new TFException("景點開始時間不得為null");
        }
    }

    public void setStarttime(String starttime) throws TFException {
        Course.this.setStarttime(LocalTime.parse(starttime));
    }

    public void setStarttime(Course preCouse) throws TFException {
        if (!(preCouse == null || preCouse.equals(this))) {
            this.starttime = preCouse.getArrivalTime();
        } else {
            throw new TFException("上一個景點不得為空或參考自己");
        }
    }

    public int getStay() {
        return stay;
    }

    public String getStayString() {
        int hour = stay / 60;
        int minute = stay % 60;

        String time = (hour == 0 ? "" : (hour + "時")) + (minute == 0 ? "" : (minute + "分"));

        if (time.equals("")) {
            return "0分";
        }

        return time;
    }

    public void setStay(int stay) throws TFException {
        if (stay >= 0) {
            this.stay = stay;
        } else {
            throw new TFException("景點停留時間不得為負值");
        }
    }

    //獲取離開景點時間
    public LocalTime getLeaveTime() throws TFException {
        if (starttime != null) {
            return starttime.plusMinutes(stay);
        } else {
            throw new TFException("請設置開始時間");
        }
    }

    //抵達下一景點
    public LocalTime getArrivalTime() throws TFException {
        int min = (int) Math.ceil(routeTime / 60);
        return this.getLeaveTime().plusMinutes(min);
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) throws TFException {
        if (place != null) {
            this.place = place;
        } else {
            throw new TFException("景點不得為空值");
        }
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) throws TFException {
        if (routeType != null) {
            this.routeType = routeType;
        } else {
            this.routeType = RouteType.CUSTOM;
        }
    }

    public void setRouteType(String routeType) throws TFException {
        if (routeType != null) {
            this.routeType = RouteType.valueOf(routeType);
        } else {
            this.routeType = RouteType.CUSTOM;
        }
    }
    
    @Override
    public String toString() {
        try {
            return "景點名稱=" + place.getName() + ", 開始時間=" + starttime + ", 停留時間=" + stay + ", 離開時間=" + getLeaveTime() + ", 路線類型=" + routeType + ", 路線時間=" + routeTime;
        } catch (TFException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.starttime);
        hash = 41 * hash + Objects.hashCode(this.place);
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
        final Course other = (Course) obj;
        if (!Objects.equals(this.starttime, other.starttime)) {
            return false;
        }
        if (!Objects.equals(this.place, other.place)) {
            return false;
        }
        return true;
    }

}
