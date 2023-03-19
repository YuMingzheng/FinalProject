package tabu.Instance;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 15:12
 * @apiNote
 */
public class Instance {

    public Point[] ohcaPoints;
    public Point[] AEDPoints;

    public int ohcaNum;
    public int fixedNum;
    public int droneNum;
    public int AEDNum;

    public double[][] distance;
    public double[][] time;
    public double[][] probability;

    /**
     * 步行速度
     */
    public double walkSpeed = 2000/9d;
    /**
     * 无人机速度
     */
    public double flySpeed = 2000d;

    public double alpha = 0.03f;

    public Instance(){

    }

    public void readInstanceFile(String fileName){
        fileName = "Instance1";
        try {
            CsvReader csvReader = new CsvReader("./input/Instance2/OHCA数据.csv");
            csvReader.readRecord();
            ohcaNum = Integer.parseInt(csvReader.get(0));
            ohcaPoints = new Point[ohcaNum];
            while(csvReader.readRecord()){
                ohcaPoints[Math.toIntExact(csvReader.getCurrentRecord())-1] = new Point(csvReader.get(0) , csvReader.get(1));
            }

            csvReader = new CsvReader("./input/Instance2/固定AED位置.csv");
            CsvReader csvReader1 = new CsvReader("./input/Instance2/无人机AED位置.csv");
            csvReader.readRecord();
            fixedNum = Integer.parseInt(csvReader.get(0));
            csvReader1.readRecord();
            droneNum = Integer.parseInt(csvReader1.get(0));
            AEDPoints = new Point[fixedNum + droneNum];
            while (csvReader.readRecord()){
                AEDPoints[Math.toIntExact(csvReader.getCurrentRecord())-1] = new Point(csvReader.get(0) , csvReader.get(1));
            }
            while (csvReader1.readRecord()){
                AEDPoints[fixedNum + Math.toIntExact(csvReader1.getCurrentRecord())-1] = new Point(csvReader1.get(0) , csvReader1.get(1));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        AEDNum = fixedNum + droneNum;
        calcDistanceMatrix();
    }

    public void readInstanceFile2(){
        AEDNum = 20;
        fixedNum = 10;
        droneNum = 10;
        ohcaNum = 100;

        probability = new double[ohcaNum][AEDNum];
        try {
            CsvReader csvReader = new CsvReader("./input/instance_data_T.csv");
            csvReader.readHeaders();
            int i = 0;
            while(csvReader.readRecord()){
                for (int j = 0; j < 20; j++) {
                    probability[i][j] = Double.parseDouble(csvReader.get(j));
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInstanceFile3(){
        AEDNum = 400;
        fixedNum = 300;
        droneNum = 100;
        ohcaNum = 5000;

        probability = new double[ohcaNum][AEDNum];
        try {
            CsvReader csvReader = new CsvReader("./input/instance_data_T_400_5000.csv");
            csvReader.readHeaders();
            int i = 0;
            while(csvReader.readRecord()){
                for (int j = 0; j < AEDNum; j++) {
                    probability[i][j] = Double.parseDouble(csvReader.get(j));
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 200*2000
     */
    public void readInstanceFile4(){
        AEDNum = 200;
        fixedNum = 120;
        droneNum = 80;
        ohcaNum = 2000;

        probability = new double[ohcaNum][AEDNum];
        try {
            CsvReader csvReader = new CsvReader("./input/instance_data_T_200_2000.csv");
            csvReader.readHeaders();
            int i = 0;
            while(csvReader.readRecord()){
                for (int j = 0; j < AEDNum; j++) {
                    probability[i][j] = Double.parseDouble(csvReader.get(j));
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     *  经纬度获取距离，单位为米
     **/
    public double getDistance(Point point1 , Point point2 ) {
        double EARTH_RADIUS = 6378.137;// 地球赤道半径
        double radLat1 = rad(point1.getLatitude());
        double radLat2 = rad(point2.getLatitude());
        double a = radLat1 - radLat2;
        double b = rad(point1.getLongitude()) - rad(point2.getLongitude());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    public void calcDistanceMatrix(){
        distance = new double[ohcaNum][AEDNum];
        time = new double[ohcaNum][AEDNum];
        probability = new double[ohcaNum][AEDNum];

        for (int i = 0; i < ohcaNum; i++) {
            for (int j = 0; j < AEDNum; j++) {
                distance[i][j] = getDistance(ohcaPoints[i] , AEDPoints[j]);
                if(j < this.fixedNum){
                    time[i][j] = distance[i][j] / walkSpeed;
                }else{
                    time[i][j] = distance[i][j] / flySpeed;
                }

                if(time[i][j] <= 18){
                    probability[i][j] = 1;
                }else if(18 < time[i][j] && time[i][j] <= 90){
                    probability[i][j] = Math.pow(Math.E , -alpha*(time[i][j] - 18));
                }else if(90 < time[i][j]){
                    probability[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        Instance instance = new Instance();
//        instance.readInstanceFile("");
        instance.readInstanceFile3();

        double max = -1;
        int loc = 1;
        for (int i = 0; i < 20; i++) {
            if(instance.probability[loc][i] >= max){
                max = instance.probability[loc][i];
            }
        }
        System.out.println(max);
        int i=1;
    }
}
