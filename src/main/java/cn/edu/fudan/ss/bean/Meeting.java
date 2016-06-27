package cn.edu.fudan.ss.bean;
import cn.edu.fudan.ss.iterator.*;
import cn.edu.fudan.ss.observer.*;

import cn.edu.fudan.ss.dao.Dao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Timestamp;

public class Meeting implements Observer{
    private int id;
    private String title, content;
    private int roomId;
    private String sponsor;
    private Timestamp start;
    private int duration;
    private String[] employees;
    private Dao dao;
    private Iterator iterator;
    private int size;

    public Meeting() {
        dao = Dao.getInstance();
    }

    public Meeting(String title, int roomId,
                   String sponsor, String content,
                   Timestamp start, int duration,
                   String[] employees) {
        this.title = title;
        this.roomId = roomId;
        this.sponsor = sponsor;
        this.start = start;
        this.duration = duration;
        this.employees = employees;
        this.content = content;
    }

    public JSONObject toJSONObject() {
        JSONObject meeting = new JSONObject();
        size = 0;
        try {
            meeting.put("id", id);
            meeting.put("title", title);
            meeting.put("roomId", roomId);
            meeting.put("sponsor", sponsor);
            meeting.put("start", start);
            meeting.put("duration", duration);
            JSONArray employeeArray = new JSONArray();
            for (String employee: employees) {
                employeeArray.put(employee);
                size++;
            }
            meeting.put("employees", employeeArray);
            meeting.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return meeting;
    }

    public void insert() throws SQLException {
        String sqlInsert = "insert into meeting(title, roomId, start, duration, sponsor, content) " +
                "values('" + title + "', '" + roomId + "', '" + start + "', '" + duration
                           + "', '" + sponsor + "', '" + content + "')";
        dao.insert(sqlInsert);
        String sqlQuery = "select * from meeting where title='" + title + "' and (start between '" + start + "' and '" + start + "')";
        id = dao.findMeetingId(sqlQuery);
        for (String employee: employees) {
            sqlInsert = "insert into meeting_employee(employee, start, end, meetingId) " +
                    "values('" + employee + "', '" + ((int) (start.getTime() / 60000L)) + "', '"
                     + (((int) (start.getTime() / 60000L)) + duration) + "', '" + id + "')";
            dao.insert(sqlInsert);
        }
        sqlInsert = "insert into meeting_room(roomId, start, end, meetingId) " +
                "values('" + roomId + "', '" + ((int) (start.getTime() / 60000L)) + "', '"
                + (((int) (start.getTime() / 60000L)) + duration) + "','" + id + "')";
        dao.insert(sqlInsert);
    }
    
    public Iterator iterator(){
        return new EmployeeIterator();
    }
    
    public void update() {
        System.out.println("Employee has changed");
        // Do some action
    }
    
    private class EmployeeIterator implements Iterator{
        private int currentIndex = 0;
        
        public boolean hasNext() {
            if(currentIndex >= size) return false;
            else return true;
        }
        
        public Object next() {
            Object o = employees[currentIndex];
            currentIndex++;
            return o;
        }
    }
}
