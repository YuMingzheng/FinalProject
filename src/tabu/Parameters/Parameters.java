package tabu.Parameters;

import java.util.Random;

/**
 * @author Yu Mingzheng
 * @date 2023/3/11 15:12
 * @apiNote
 */
public class Parameters {
    /**
     * 求解算例的路径
     */
    public String inputInstancePath;
    /**
     * 随机种子
     */
    public int seed = 10;
    /**
     * 最大迭代次数
     */
    public int maxIteration = 500;
    /**
     * 禁忌搜索深度
     */
    public int alphaMax = 100;
    /**
     * 预计人均救治概率
     */
    public double  L = 0.9;
    /**
     * 固定AED成本，c_f
     */
    public double c_f = 1;
    /**
     * 无人机AED成本，c_d
     */
    public double c_d = 10;
    /**
     * 随机数
     */
    public Random random;
    /**
     * 禁忌步长
     */
    public int tabuTenure = 9;

    public double beta = 0.5;

    public double gamma = 0.5;


    public Parameters(){
        this.inputInstancePath = "";
        this.random = new Random(seed);
    }


}
