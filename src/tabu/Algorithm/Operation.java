package tabu.Algorithm;

/**
 * @author Yu Mingzheng
 * @date 2023/3/13 16:55
 * @apiNote
 */
public abstract class Operation {
    /**
     * 记录邻域算子类型，即N1或N2
     */
    public int type;
    /**
     * N1算子变的哪个位置
     */
    public int position;
    /**
     * N2算子变的哪个位置之一
     */
    public int position1;
    /**
     * N2算子变的哪个位置之二
     */
    public int position2;
    /**
     * obj value变化
     */
    public double objValueChange;
    /**
     * g值变化
     */
    public double gChange;

    /**
     * 记录是否找到任何可行的邻域操作
     */
    public boolean feasible = false;

    @Override
    public String toString(){
        return "position " + position + " po1 " + position1 +" po2 " + position2 + " objChange " + objValueChange + " type " + type;
    }


}
