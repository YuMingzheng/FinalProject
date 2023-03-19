package tabu.Instance;

import tabu.Parameters.Parameters;
import java.util.Arrays;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 15:12
 * @apiNote
 */
public class Solution {
    public int iter;

    public Instance instance;
    /**
     * 0-1字符串，存储y_i的解
     */
    public int[] solutions;
    /**
     * 该解对应的obj. value
     */
    public double objValue;
    /**
     * 该解对应的g值，即人均救治概率
     */
    public double g;

    public Parameters param;


    public Solution(Instance instance , Parameters param){
        this.instance = instance;
        this.solutions = new int[this.instance.AEDNum];
        this.param = param;
    }

    public double calcObjValue(){
        this.objValue = 0.0;
        for (int i = 0; i < solutions.length; i++) {
            if(i < instance.fixedNum){
                this.objValue += solutions[i] * param.c_f;
            }else{
                this.objValue += solutions[i] * param.c_d;
            }
        }
        return this.objValue;
    }

    public double calcG(){
        this.g = 0.0;
        double total = 0.0;
        for (double[] ohca : instance.probability) {
            double maxTemp = 0.0;
            for (int i = 0; i < ohca.length; i++) {
                if(solutions[i] == 1 && ohca[i] >= maxTemp){
                    maxTemp = ohca[i];
                }
            }
            total += maxTemp;
        }
        this.g = total / instance.ohcaNum;
        return this.g;
    }

    public double calcG(int[] solutions){
        double g;
        double total = 0.0;
        for (double[] ohca : instance.probability) {
            double maxTemp = 0.0;
            for (int i = 0; i < ohca.length; i++) {
                if(solutions[i] == 1 && ohca[i] >= maxTemp){
                    maxTemp = ohca[i];
                }
            }
            total += maxTemp;
        }
        g = total / instance.ohcaNum;
        return g;
    }

    public void randomInitialSolution(){
        Arrays.fill(solutions , 0);
        for (int i = 0; i < solutions.length; i++) {
            if(param.random.nextDouble() < 0.5){
                solutions[i] = 0;
            }else{
                solutions[i] = 1;
            }
        }
    }

    public void initialSolution(){
        Arrays.fill(solutions , 0);
        while(calcG() < param.L){
            solutions[getMaxIncreaseLocation()] = 1;
        }
        iter = 0;

    }

    public int getMaxIncreaseLocation(){
        int result = -1;
        double maxG = 0.0;
        for (int i = 0; i < solutions.length; i++) {
            if(solutions[i] == 0){
                int[] s = solutions.clone();
                s[i] = 1;
                double _g = calcG(s);
                if(_g >= maxG){
                    maxG = _g;
                    result = i;
                }
            }
        }
        if(result==-1){
            System.out.println("找不到满足要求的初始解");
            System.exit(0);
            return -1;
        }else {
            return result;
        }
    }

    @Override
    public Solution clone(){
        Solution clone = new Solution(instance , param);
        clone.iter = iter;
        clone.solutions = solutions.clone();
        clone.objValue = objValue;
        clone.g = g;
        return clone;
    }

    @Override
    public String toString(){
        String s = "{该解对应的obj值：" + this.objValue + "；对应的g值：" + String.format("%.5f",this.g) + "；解集：";
        int _ = 0;
        for (int solution : this.solutions) {
            if( _ == instance.fixedNum){
                s+= "| " + solution + ", ";
            }else {
                s += solution + ", ";
            }
            _++;
        }
        s = s.substring(0 , s.length() - 2);
        s += "}";

        return s;
    }

    public static void main(String[] args) {
        Instance instance = new Instance();
//        instance.readInstanceFile("");
        instance.readInstanceFile2();
        Parameters parameters = new Parameters();

        Solution solution = new Solution(instance , parameters);

//        solution.randomInitialSolution();
//        solution.initialSolution();
        solution.calcObjValue();
        solution.calcG();
//        System.out.println(solution.calcG());

//        System.out.println(solution.getMaxIncreaseLocation());


        int i = 0;
    }

}
