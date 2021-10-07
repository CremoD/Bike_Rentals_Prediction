package data_analysis;

import java.io.Serializable;

public class HourlyRental implements Serializable{
    private String start_date;
    private long count;

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
