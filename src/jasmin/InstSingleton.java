package jasmin;

import jasmin.translation.TranslateType;
import org.specs.comp.ollir.*;

import java.util.HashMap;
import java.util.concurrent.TransferQueue;

public class InstSingleton {

    public static String istore(int reg){
        if (reg > 3)
            return "istore " + reg + "\n";
        return "istore_" + reg + "\n";
    }

    public static String iload(int reg){
        if (reg > 3)
            return "iload " + reg + "\n";
        return "iload_" + reg + "\n";
    }

    public static String iconst(String number){
        int constant = Integer.parseInt(number);
        if (constant > 5)
            return "ldc " + number + "\n";
        return "const_" + number + "\n";
    }

    public static String aload(int reg){
        if (reg > 3)
            return "aload " + reg + "\n";
        return "aload_"+reg + "\n";
    }

    public static String anewarray(int regVar, String type){
        return iload(regVar) + "anewarray " + type + "\n";

    }

    public static String getfield(int classReg, Type type, String fieldName){
        return aload(classReg) + "getfield " + TranslateType.getJasminType(type) + " " + fieldName + "\n";
    }

    public static String getOp(OperationType opType){
        return switch (opType) {
            case MUL -> "imul \n";
            case ADD -> "iadd \n";
            case SUB -> "isub \n";
            case DIV -> "idiv \n";
            case AND -> "iand \n";
            case NOT -> "ineg \n";
            case OR -> "ior \n";

            default -> "\n";
        };
    }


    public static String getAccessArrayVar(int regArray, int regVar){
       return aload(regArray) + iload(regVar);
    }


    public static String getArrayLength(int arrayReg){
        return aload(arrayReg) + "arraylength \n";
    }


}