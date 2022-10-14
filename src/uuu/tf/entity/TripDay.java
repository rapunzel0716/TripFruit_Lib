/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rapunzel_PC
 */
public class TripDay {

    //private int scheduleId;//PKEY
    private LocalDate tripdate;//PKEY
    private List<Course> tripCourse = new LinkedList<>();//景點,路線時間(s)

    public LocalDate getTripdate() {
        return tripdate;
    }

    public void setTripdate(LocalDate tripdate) throws TFException {
        if (tripdate != null) {
            this.tripdate = tripdate;
        } else {
            throw new TFException("行程日期不得為空值");
        }
    }

    public void setTripdate(String tripdate) throws TFException {
        if (tripdate != null) {
            tripdate = tripdate.replace('/', '-');
        }
        try {
            setTripdate((tripdate != null) ? LocalDate.parse(tripdate) : null);
        } catch (DateTimeParseException ex) {
            throw new TFException("行程日期格式不正確");
        }
    }

    public void clear() {
        tripCourse.clear();
    }

    public Course getCourse(int index) {
        if(index <0)
            return null;
        if(index >= tripCourse.size())
            return null;
        return tripCourse.get(index);
    }

    public List<Course> getTripCourse() {
        return tripCourse;
    }

    public void addplace(Place place, LocalTime starTime, int stay, int routeTime,String routeType) throws TFException {
        if (place == null) {
            throw new IllegalArgumentException("加入景點不得為null");
        }
        if (starTime == null) {
            starTime = LocalTime.parse("08:00");
        }
        if (stay < 0) {
            stay = 0;
        }
        if (routeTime < 0) {
            routeTime = 0;
        }
        Course course = new Course();
        course.setPlace(place);
        if (tripCourse.isEmpty()) {
            course.setStarttime(starTime);//開始時間
        } else {
            Course lastCourse = tripCourse.get(tripCourse.size() - 1);
            course.setStarttime(lastCourse);
        }
        course.setStay(stay);//停留時間
        course.setRouteTime(routeTime);
        if(routeType!=null){
            course.setRouteType(RouteType.valueOf(routeType));
        }
        tripCourse.add(course);
    }

    public void addplace(Place place, String starTime, int stay, int routeTime,String routeType) throws TFException {
        if (starTime != null) {
            addplace(place, LocalTime.parse(starTime), stay, routeTime,routeType);
        } else {
            throw new TFException("開始時間不得為null");
        }
    }

    //加入第二個之後的景點
    public void addplace(Place place, int stay, int routeTime,String routeType) throws TFException {
        addplace(place, LocalTime.parse("08:00"), stay, routeTime,routeType);
    }

    public void removeplace(Course item) throws TFException {
        if (!tripCourse.contains(item)) {
            throw new TFException("此日程不包含此景點");
        }
        int index = tripCourse.indexOf(item);
        if (index != -1 && tripCourse.size() >= 2) {
            if (tripCourse.size() - 1 != index) {
                for (int x = 1; x < tripCourse.size(); x++) {
                    LocalTime time;
                    if (index == 0) {
                        if (x == 1) {
                            time = tripCourse.get(0).getStarttime();
                            tripCourse.get(1).setStarttime(time);
                        } else {
                            time = tripCourse.get(x - 1).getArrivalTime();
                            tripCourse.get(x).setStarttime(time);
                        }
                    } else {
                        if (index >= x) {
                            continue;
                        } else if (x == index + 1) {
                            time = tripCourse.get(x - 2).getArrivalTime();
                            tripCourse.get(x).setStarttime(time);
                        } else {
                            time = tripCourse.get(x - 1).getArrivalTime();
                            tripCourse.get(x).setStarttime(time);
                        }
                    }
                }
            }

        }
        tripCourse.remove(index);
    }
    
    public void removeplace(Place p ,LocalTime startTime) throws TFException {
        Course course = new Course();
        course.setPlace(p);
        course.setStarttime(startTime);
        removeplace(course);
    }

    public void updateRouteTime(Course item, int routeTime,String routeType) throws TFException {
        if (!tripCourse.contains(item)) {
            throw new TFException("設置路線時間錯誤，此日程不包含此景點");
        }
        if (routeTime < 0) {
            throw new TFException("設置路線時間不得為負值");
        }
        if(routeType == null){
            throw new TFException("設置路線類型不得為null");
        }
        int index = tripCourse.indexOf(item);
        for(int x=index; x<tripCourse.size();x++){
            if(x==index){
                tripCourse.get(x).setRouteTime(routeTime);
                tripCourse.get(x).setRouteType(routeType);
            }
            else{
                tripCourse.get(x).setStarttime(tripCourse.get(x-1).getArrivalTime());
            }
        }
        
    }
    
    public void updateStay(Course item, int stay) throws TFException {
        if (!tripCourse.contains(item)) {
            throw new TFException("設置停留時間錯誤，此日程不包含此景點");
        }
        if (stay < 0) {
            throw new TFException("設置停留時間不得為負值");
        }
        int index = tripCourse.indexOf(item);
        for(int x=index; x<tripCourse.size();x++){
            if(x==index)
                tripCourse.get(x).setStay(stay);
            else{
                //System.out.println(tripCourse.get(x));
                tripCourse.get(x).setStarttime(tripCourse.get(x-1).getArrivalTime());
                //System.out.println(tripCourse.get(x));
            }
        }
    }
    
    //設定開始時間
    public void updateStarTime(LocalTime time) throws TFException {
        if(time == null)
            throw new TFException("設定開始時間錯誤，傳入時間不可為null");
        if(tripCourse.isEmpty())
            throw new TFException("設定開始時間錯誤，此日期無行程");
        for(int i=0;i<tripCourse.size();i++){
            if(i==0){
                tripCourse.get(0).setStarttime(time);
            }
            else{
                LocalTime arrivalTime = tripCourse.get(i-1).getArrivalTime();
                tripCourse.get(i).setStarttime(arrivalTime);
            }
        }
        
    }
    /*
    public Course getLastCourse() {
        if(tripCourse.size() == 0){
            return null;
        }
        return tripCourse.get(tripCourse.size() - 1);
    }*/

    public boolean isEmpty() {
        return tripCourse.isEmpty();
    }

    public int size() {
        return tripCourse.size();
    }
    

    @Override
    public String toString() {
        return "日期=" + tripdate + ", 本日行程=" + tripCourse + "\n";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.tripdate);
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
        final TripDay other = (TripDay) obj;
        if (!Objects.equals(this.tripdate, other.tripdate)) {
            return false;
        }
        return true;
    }

}
