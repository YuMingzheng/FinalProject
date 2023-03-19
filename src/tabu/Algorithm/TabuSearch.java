package tabu.Algorithm;

import tabu.Instance.Instance;
import tabu.Instance.Solution;
import tabu.Parameters.Parameters;

import java.util.*;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 15:11
 * @apiNote
 */
public class TabuSearch {
    private Random random;
    /**
     * 搜索深度
     */
    private int alphaMax;
    /**
     * 禁忌步长
     */
    private int tabuTenure;
    /**
     * 概率向量
     */
    public double[] probVector;

    public double beta;

    public double gamma;

    private int[] tabuList;

    private double L;

    public Solution solution;
    public int fixedNum;
    public int droneNum;

    /**
     * 构造函数
     */
    public TabuSearch(){}

    public TabuSearch(Instance instance , Solution solution , Parameters parameters , double[] probVector){
        this.probVector = probVector.clone();

        this.solution = solution;

        this.random = parameters.random;
        this.alphaMax = parameters.alphaMax;
        this.tabuTenure = parameters.tabuTenure;
        this.L = parameters.L;

        this.tabuList = new int[instance.AEDNum];
        Arrays.fill(tabuList , 0);
        this.fixedNum = instance.fixedNum;
        this.droneNum = instance.droneNum;

        this.beta = parameters.beta;
        this.gamma = parameters.gamma;
    }

    public Solution search(){
        // 将初始解赋为全局最优解
        Solution bestSolution = solution.clone();
//        System.out.println("初始最优解 " + Arrays.toString(bestSolution.solutions));
        int alpha = 0;
        // 开始迭代
        while(alpha < alphaMax){
            solution.iter = alpha;
            if (alpha == 12) {
                int _= 0;
            }

//            System.out.print("第" + alpha + "次迭代，当前最优解：" + bestSolution.objValue);
            // 对当前初始解进行邻域搜索
            Operation operation1 = findBestOperation(solution  , bestSolution);
//            System.out.println("\n    当前算子位置 " + operation1);
//            System.out.println("    当前禁忌表 " + Arrays.toString(tabuList));
//            System.out.println("    当前解 " + solution);

            if(alpha == 12) {
                int _ = 0;
            }
            // 判断找到的邻域是不是被禁忌了
            if(operation1 != null && !isTabued(solution , alpha , bestSolution , operation1)){
                // 没有的话apply这个邻域操作
                applyOperation(operation1);
//                System.out.println("    邻域后 " + solution);
                if(operation1.type == 1){
                    updateProbVector(operation1.position , solution.solutions[operation1.position] == 0 ? 1: -1);
                }else if(operation1.type == 2){
                    if(operation1.position1 == 1){
                        updateProbVector(operation1.position2 , 1);
                    }else if(operation1.position1 == 0){
                        updateProbVector(operation1.position2 , -1);
                    }else if(operation1.position2 == 1){
                        updateProbVector(operation1.position1 , 1);
                    }else if(operation1.position2 == 0){
                        updateProbVector(operation1.position1 , -1);
                    }
                }

                if(solution.objValue < bestSolution.objValue){
                    bestSolution = solution.clone();
                }

                // 更新禁忌表
                if(operation1.type == 1){
                    this.tabuList[operation1.position] = alpha + this.tabuTenure;
                }else{
                    this.tabuList[operation1.position1] = alpha + this.tabuTenure;
                    this.tabuList[operation1.position2] = alpha + this.tabuTenure;
                }

            }else{
//                System.out.print("    被禁忌了 或者 没找到任何可行的邻域操作");
            }

            alpha++;

//            System.out.println("\n");


        }
//        System.out.println("=====================");
        System.out.println("找到的最优解" + bestSolution);
        return bestSolution;
    }

    public Operation findBestOperation(Solution nowSolution , Solution bestSolution){
        N1Operation n1BestOperation = new N1Operation();
        N2Operation n2BestOperation = new N2Operation();
        double bestObj1 = Double.MAX_VALUE;
        double bestObj2 = Double.MAX_VALUE;
        Solution solution = nowSolution.clone();


        double p = random.nextDouble();
        Solution temp;
        if(p < 0.5) {
            //先检查N1邻域
            for (int i = 0; i < solution.solutions.length; i++) {
                temp = solution.clone();
                solution.solutions[i] = 1 - solution.solutions[i];
                if (solution.calcG() >= L && solution.calcObjValue() < bestObj1 && !isTabued(temp, solution.iter, bestSolution, new N1Operation(i, solution.objValue - temp.objValue, solution.g - temp.g))) {
                    bestObj1 = solution.objValue;
                    n1BestOperation.position = i;
                    n1BestOperation.objValueChange = solution.objValue - temp.objValue;
                    n1BestOperation.gChange = solution.g - temp.g;
                    n1BestOperation.feasible = true;
                }
                solution = temp.clone();
            }
        }else {
            //再检查N2邻域
            for (int i = 0; i < fixedNum; i++) {
                for (int j = fixedNum; j < fixedNum + droneNum; j++) {
                    temp = solution.clone();

                    int exchangeTemp = solution.solutions[i];
                    solution.solutions[i] = solution.solutions[j];
                    solution.solutions[j] = exchangeTemp;

                    if (solution.calcG() >= L && solution.calcObjValue() < bestObj2 && !isTabued(temp, solution.iter, bestSolution, new N2Operation(i, j, solution.objValue - temp.objValue, solution.g - temp.g))) {
                        bestObj2 = solution.objValue;
                        n2BestOperation.position1 = i;
                        n2BestOperation.position2 = j;
                        n2BestOperation.objValueChange = solution.objValue - temp.objValue;
                        n2BestOperation.gChange = solution.g - temp.g;
                        n2BestOperation.feasible = true;
                    }

                    solution = temp.clone();
                }
            }
        }

        if(n1BestOperation.feasible && n2BestOperation.feasible){
            return bestObj1 < bestObj2 ? n1BestOperation : n2BestOperation;
        }else if(n1BestOperation.feasible && !n2BestOperation.feasible){
            return n1BestOperation;
        }else if(!n1BestOperation.feasible && n2BestOperation.feasible){
            return n2BestOperation;
        }else{
            return null;
        }

    }

    public void applyOperation(Operation operation){
        if(operation.type == 1){
            solution.solutions[operation.position] = 1 - solution.solutions[operation.position];
            solution.objValue += operation.objValueChange;
            solution.g += operation.gChange;
        }else if(operation.type == 2){
            int exchangeTemp = solution.solutions[operation.position1];
            solution.solutions[operation.position1] = solution.solutions[operation.position2];
            solution.solutions[operation.position2] = exchangeTemp;
            solution.objValue += operation.objValueChange;
            solution.g += operation.gChange;
        }
    }

    public boolean isTabued(Solution preSolution , int iteration , Solution bestSolution , Operation operation){

        if(operation.type == 1){
            return !((preSolution.objValue + operation.objValueChange < bestSolution.objValue) || (iteration >= tabuList[operation.position]));
        }else{
            return !(
                    (preSolution.objValue + operation.objValueChange < bestSolution.objValue)
                            || (iteration >= tabuList[operation.position1] && iteration >= tabuList[operation.position2])
            );
        }

    }

    /**
     *
     * @param index 哪个位置
     * @param how 1为增加、-1为减小
     */
    public void updateProbVector(int index , int how){
        if(how == -1){
            probVector[index] = beta + (1-beta)*probVector[index];
        }else{
            probVector[index] = (1-gamma)*probVector[index];
        }
    }

    public Solution probabilityBasedDisturb(){
        Solution s = solution.clone();
        for (int i = 0; i < solution.solutions.length; i++) {
            double p = random.nextDouble();
            if(s.solutions[i] == 0 && p >= probVector[i]){
                s.solutions[i] = 1;
            }
        }

        for (int i = 0; i < solution.solutions.length; i++) {
            double p = random.nextDouble();
            Solution s_ = s.clone();
            s_.solutions[i] = 0;
            if(s.solutions[i] == 1 & s.calcG(s_.solutions) >= L){
                s.solutions[i] = 0;
            }
        }
        solution = s.clone();
        solution.calcObjValue();
        solution.calcG();

        return solution;
    }

    public static void main(String[] args) {
        Instance instance = new Instance();
//        instance.readInstanceFile("");
        instance.readInstanceFile2();
        Parameters parameters = new Parameters();

        Solution solution = new Solution(instance , parameters);
//        solution.randomInitialSolution();
        solution.initialSolution();
//        solution.solutions[0] = 1;
//        solution.solutions[1] = 1;
//        solution.solutions[2] = 1;
//        solution.solutions[3] = 0;
//        solution.solutions[4] = 1;
//        solution.solutions[5] = 0;
//        solution.solutions[6] = 1;
//        solution.solutions[7] = 1;
//        solution.solutions[8] = 1;
//        solution.solutions[9] = 0;
//        solution.solutions[10] = 0;
//        solution.solutions[11] = 0;
//        solution.solutions[12] = 1;
//        solution.solutions[13] = 0;
//        solution.solutions[14] = 0;
//        solution.solutions[15] = 0;
//        solution.solutions[16] = 0;
//        solution.solutions[17] = 0;
//        solution.solutions[18] = 0;
//        solution.solutions[19] = 0;
        solution.calcObjValue();
        solution.calcG();

        double[] probVector = new double[instance.AEDNum];
        Arrays.fill(probVector , 0.5);
        TabuSearch ts = new TabuSearch(instance , solution , parameters , probVector);
        ts.search();



        int i=1;
    }

}