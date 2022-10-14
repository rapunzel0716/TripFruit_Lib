/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.model;

import java.util.List;
import uuu.tf.entity.Schedule;
import uuu.tf.entity.TFException;

/**
 *
 * @author Rapunzel_PC
 */
public class SchedulesService {
    SchedulesDAO dao = new SchedulesDAO();

    public void insert(Schedule schedule) throws TFException {
        dao.insert(schedule);
    }
    
    public List<Schedule> searchSchedulesByMemberUid(int memberUid) throws TFException {
        return dao.selectSchedulesByMemberUid(memberUid);
    }
    
    public Schedule getScheduleById(int scheduleId) throws TFException{
        return dao.selectScheduleById(scheduleId);
    }

    public void delectScheduleById(int scheduleId) throws TFException {
        dao.deleteScheduleById(scheduleId);
    }

    public void copyScheduleById(int scheduleId) throws TFException {
        dao.copyByScheduleId(scheduleId);
    }
    
    public void updateSchedule(Schedule schedule)throws TFException {
        dao.updateSchedule(schedule);
    }
    
}
