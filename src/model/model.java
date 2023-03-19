package model;

import com.csvreader.CsvReader;
import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Yu Mingzheng
 * @date 2023/1/16 13:10
 * @apiNote
 */
public class model {
    public static void main(String[] args) {
        Random random = new Random();
        random.setSeed(10);

        // 其他
        double alpha = 0.03f;
        int M = 99999;
        // 定义成本相关
        int c_f = 1;
        int c_d = 10;

        // 救治概率
        double L = 0.9;
        // 定义相关集合大小
        int I = 200;
        int I_f = 120;
        int I_d = I - I_f;
        int J = 2000;

        // 定义时间、距离、速度相关，单位均为国际单位制基本单位及其导出单位，即m、s、m/s
        double v1 = 20/9d; // 步行速度
        double v2 = 20d;   // 无人机速度
        double[][] d_ij = new double[I][J];
        double[][] t_ij = new double[I][J];

        for (int i = 0; i < I; i++) {
            for (int j = 0; j < J; j++) {
                if(i < I_f){
                    d_ij[i][j] = random.nextInt(1000);
                    t_ij[i][j] = d_ij[i][j] / v1;
                }else{
                    d_ij[i][j] = random.nextInt(5000);
                    t_ij[i][j] = d_ij[i][j] / v2;
                }
            }
        }

        // 定义救援概率p_{ij}
        double[][] p_ij = new double[I][J];
        for (int i = 0; i < I; i++) {
            for (int j = 0; j < J; j++) {
                if(t_ij[i][j] <= 18){
                    p_ij[i][j] = 1;
                }else if(18 < t_ij[i][j] && t_ij[i][j] <= 90){
                    p_ij[i][j] = Math.pow(Math.E , -alpha*(t_ij[i][j]-18));
                }else if(90 < t_ij[i][j]){
                    p_ij[i][j] = 0;
                }
            }
        }

        /**
         * 读取测试数据
         */
        try {
            CsvReader csvReader = new CsvReader("./input/instance_data_200_2000.csv");
            csvReader.readHeaders();
            int i = 0;
            while(csvReader.readRecord()){
                for (int j = 0; j < J; j++) {
                    p_ij[i][j] = Double.parseDouble(csvReader.get(j));
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            IloCplex model = new IloCplex();
            // 设置程序的运行时间
            model.setParam(IloCplex.DoubleParam.TiLim , 1800);

            // 定义决策变量
            // y_i
            IloIntVar[] y_i = new IloIntVar[I];
            for (int i = 0; i < I; i++) {
                y_i[i] = model.boolVar("y_" + i );

            }

            // z_j
            IloNumVar[] z_j = new IloNumVar[J];
            for (int j = 0; j < J; j++) {
                z_j[j] = model.numVar(0.0 ,1.0, IloNumVarType.Float, "z_" + j);
            }
            //u_ij
            IloNumVar[][] u_ij = new IloNumVar[I][J];
            for (int i = 0; i < I; i++) {
                for (int j = 0; j < J; j++) {
                    u_ij[i][j] = model.boolVar("u_"+i+"_"+j);
                }
            }

            // 目标函数
            IloNumExpr obj =model.numExpr();
            for (int i = 0; i < I; i++) {
                if(i < I_f){
                    obj = model.sum(obj , model.prod(c_f , y_i[i]));
                }else{
                    obj = model.sum(obj , model.prod(c_d , y_i[i]));
                }
            }
            model.addMinimize(obj);

            // 约束（2b）
            for (int j = 0; j < J; j++) {
                for (int i = 0; i < I; i++) {
                    IloNumExpr expr1 = model.numExpr();
                    expr1 = model.sum(z_j[j] , model.prod(-1 , model.prod(y_i[i] , p_ij[i][j])));
                    model.addGe(expr1 , 0);
                }
            }

            // 约束（2c）
            for (int j = 0; j < J; j++) {
                for (int i = 0; i < I; i++) {
                    IloNumExpr expr1 = model.numExpr();
                    expr1 = model.sum(model.sum(model.prod(y_i[i] , p_ij[i][j]) , model.prod(model.sum(1 , model.prod(-1 , u_ij[i][j])) , M)) , model.prod(-1 , z_j[j]));
                    model.addGe(expr1 , 0);
                }
            }

            // 约束（2d）
            for (int j = 0; j < J; j++) {
                IloNumExpr expr1 = model.numExpr();
                for (int i = 0; i < I; i++) {
                    expr1 = model.sum(expr1 , u_ij[i][j]);
                }
                model.addGe(expr1 , 1);
            }

            //约束（2e）
            IloNumExpr expr1 = model.numExpr();
            int[] cons = new int[J];
            for (int j = 0; j < J; j++) {
                cons[j] = 1;
            }
            expr1 = model.scalProd(z_j , cons);
            model.addGe(expr1 , J*L);


            model.exportModel("model.lp");
//            System.out.println(model.getModel());

            long start_cur = System.currentTimeMillis();
            boolean solve = model.solve();
            long end_cur = System.currentTimeMillis();

            if(solve){
                model.output().println("解的状态： " + model.getStatus());
                model.output().println("目标函数值： " + model.getObjValue());

                double[] y_i_sol = model.getValues(y_i);
                double[] z_j_sol = model.getValues(z_j);
                double[][] u_ij_sol = new double[I][J];
                for (int i = 0; i < I; i++) {
                    for (int j = 0; j < J; j++) {
                        u_ij_sol[i][j] = model.getValue(u_ij[i][j]);
                    }
                }

                System.out.println("y_i的解为" + Arrays.toString(y_i_sol));
                System.out.println("z_j的解为" + Arrays.toString(z_j_sol));
                System.out.println("=========================");

                System.out.println(String.format("耗时%.3f秒" , (double)(end_cur - start_cur) / 1000));

                int fixed = 0;
                int drone = 0;
                for (int i = 0; i < I; i++) {
                    if(i < I_f){
                        fixed += y_i_sol[i];
                    }else{
                        drone += y_i_sol[i];
                    }
                }
                System.out.println(String.format("使用到了%d个固定AED，%d个无人机AED",fixed , drone));
                double mean = 0.0;
                for (int j = 0; j < J; j++) {
                    mean += z_j_sol[j];
                }
                mean = mean / J;
                System.out.println(String.format("人均救援水平为%.3f，要求为%.3f" , mean , L));



            }else {
                model.output().println("未找到解法");
            }

            int _ = 0;


        } catch(IloException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            // TODO: handle exception
        }
    }
}
