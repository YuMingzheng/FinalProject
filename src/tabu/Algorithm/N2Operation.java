package tabu.Algorithm;

import java.util.regex.PatternSyntaxException;

/**
 * @author Yu Mingzheng
 * @date 2023/3/13 16:24
 * @apiNote
 */
public class N2Operation extends Operation{

    public N2Operation(){
        this.position1 = -1;
        this.position2 = -1;
        this.objValueChange = Double.MAX_VALUE;
        this.gChange = 0;

        type = 2;
    }

    public N2Operation(int position1 , int position2 , double objC , double gC){
        this.position1 = position1;
        this.position2 = position2;
        this.objValueChange = objC;
        this.gChange = gC;

        type = 2;
    }
}
