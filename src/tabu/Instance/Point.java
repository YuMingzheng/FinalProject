package tabu.Instance;

import java.util.Collections;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 16:32
 * @apiNote
 */
public class Point {
    private double latitude;
    private double longitude;

    public Point(double latitude , double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point(String latitude , String longitude){
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString(){
        return "纬度："+this.latitude + " 经度：" + this.longitude;
    }
}
