package tabu.Algorithm;

/**
 * @author Yu Mingzheng
 * @date 2023/3/13 16:24
 * @apiNote
 */
public class N1Operation extends Operation{


    public N1Operation(){
        this.position = -1;
        this.objValueChange = Double.MAX_VALUE;
        this.gChange = 0;

        type = 1;
    }

    public N1Operation(int position , double objChange , double gChange){
        this.position = position;
        this.objValueChange = objChange;
        this.gChange = gChange;

        this.type = 1;
    }
}
