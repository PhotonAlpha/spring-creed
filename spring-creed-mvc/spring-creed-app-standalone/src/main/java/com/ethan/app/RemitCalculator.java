/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/10
 */
package com.ethan.app;

import java.util.Scanner;

public class RemitCalculator {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    // 手续费设定一次, 后续计算必定相同
    System.out.print("请输入汇款手续费($):");
    int handlingFee = sc.nextInt();

    // 一次汇款计算公式
    System.out.print("请输入一次性汇 汇款总额:");
    Integer stageOneTime = sc.nextInt();
    System.out.print("请输入一次性汇 汇款汇率:");
    Double remitRateOneTime = sc.nextDouble();

    double costFee = handlingFee * remitRateOneTime;
    double totalOneTime = stageOneTime * remitRateOneTime - costFee;

    String outPutStage1 = String.format("一次性汇款收入:%s (一次性汇款总额:%s - 手续费支出:%s) \n", totalOneTime, stageOneTime * remitRateOneTime, costFee);
    StringBuilder sb = new StringBuilder(outPutStage1);
    StringBuilder sbDetails = new StringBuilder();

    System.out.println("开始计算分批汇款计算,当计算总数达到一次性汇款额,自动结束计算.");
    // 多次汇款计算公式
    Integer totalAmount = stageOneTime;
    double totalDollar = 0;
    double totalInCome = 0;
    double totalCost = 0;
    int multiTimeCount = 0;
    boolean end;

    do {
      multiTimeCount++;
      System.out.print(String.format("请输入第%s批汇 汇款金额:", multiTimeCount));
      Integer stageMultiTime = sc.nextInt();

      System.out.print(String.format("请输入第%s批汇 汇款汇率:", multiTimeCount));
      Double remitRateMultiTime = sc.nextDouble();

      double currentIncome = stageMultiTime * remitRateMultiTime;
      double currentCostFee = handlingFee * remitRateMultiTime;

      totalInCome += currentIncome;
      totalCost += currentCostFee;
      totalDollar += stageMultiTime;

      String currentOutPutStage = String.format("第" + multiTimeCount + "批汇款收入:%s (当前汇款总额:%s - 手续费支出:%s) \n", currentIncome - currentCostFee, currentIncome, currentCostFee);
      sbDetails.append(currentOutPutStage);

      // 判断是否结束循环
      totalAmount -= stageMultiTime;
      end = totalAmount <= 0;

    } while (multiTimeCount > 0 && !end);

    String currentOutPutStage = String.format("分批汇款总收入:%s (汇款总额:%s 新 %s RMB - 手续费支出:%s) \n", totalInCome - totalCost, totalDollar, totalInCome, totalCost);
    sb.append(currentOutPutStage);


    System.out.println(sb.toString());
    System.out.println(sbDetails.toString());

    /**
     * 计算思路:
     * 手续费汇率:平均汇率
     * 总数: 一笔汇   当前汇率 * 汇款总额度          + 手续费 * 当前汇率
     *      分批汇   (当前阶段汇率 * 当前阶段汇款总数  + 手续费 * 当前阶段汇率) * 批次
     *
     * 输出结果形式:
     *    "一次汇款总额:{} (手续费支出:{}) 分批汇款总额:{} (手续费支出:{})"
     *   12000
     *    (e.g.) 1000  5.0610
     *           2000  5.0360
     *           5000  5.0330
     *          10000  4.990
     *          16000  5.0050
     *          20000  5.0050
     *
     *
     *  1. 12000 * 5.0160   + 18 * 5.0160
     *  2. 5000 * 5.0330 + 5000 * 5.0330 + 2000 * 5.0360  + 18 * 5.0330 + 18 * 5.0330 + 18 * 5.0360
     */



  }
}
