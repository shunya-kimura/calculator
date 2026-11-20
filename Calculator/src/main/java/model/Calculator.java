package model;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    public static double evaluate(String result){
        List<String> formula = conversion(result)
    }

    public static List<String> conversion(String result){
        List<String> formula = new ArrayList<>();
        String number = "";
        for (char num : result.toCharArray()){
            if(num >= 0 && num <=9 ) {
                number += num;
            } else if(num == '.') {
                number += num;
            } else {
                
            }
        }
        formula.add(number);
        return formula;
    }
}


    