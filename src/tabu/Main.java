package tabu;

import tabu.Algorithm.N1Operation;
import tabu.Algorithm.Operation;
import tabu.Algorithm.TabuSearch;
import tabu.Instance.Instance;
import tabu.Instance.Solution;
import tabu.Parameters.Parameters;

import java.util.Arrays;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 15:11
 * @apiNote
 */
public class Main {
    public static void main(String[] args) {
        Instance instance = new Instance();
        instance.readInstanceFile2();

        Parameters parameters = new Parameters();

        Solution solution = new Solution(instance , parameters);
        solution.initialSolution();
        solution.calcObjValue();
        solution.calcG();

        double[] probVector = new double[instance.AEDNum];
        Arrays.fill(probVector , 0.5);

        int count = 0;
        Solution globalBestSolution = solution.clone();

        double objBest = Double.MAX_VALUE;
        while(count < parameters.maxIteration) {
            System.out.print("第" + count + "轮：");
            TabuSearch ts = new TabuSearch(instance, solution, parameters, probVector);
            solution = ts.search();

            if(solution.objValue < objBest){
                objBest = solution.objValue;
                globalBestSolution = solution.clone();
            }

            solution = ts.probabilityBasedDisturb();

            count ++;
        }

        System.out.println("================");
        System.out.println("找到的全局最优：" + globalBestSolution);


    }

}