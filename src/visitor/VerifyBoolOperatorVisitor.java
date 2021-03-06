package visitor;

import analysis.Analysis;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class VerifyBoolOperatorVisitor extends PreorderJmmVisitor<Analysis, Boolean> {


    public VerifyBoolOperatorVisitor(){
        addVisit("Less", this::visitLess);
        addVisit("And", this::visitAnd);
        addVisit("Not", this::visitNot);
    }

    /**
     * Verifies if right and left node are math expression.
     * If not, verifies if they are numbers.
     */
    public Boolean visitLess(JmmNode lessNode, Analysis analysis){
        JmmNode leftNode = lessNode.getChildren().get(0);
        JmmNode rightNode = lessNode.getChildren().get(1);

        if (leftNode.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(leftNode, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("int") && !returnValueMethod.equals("int[]"))
                analysis.addReport(leftNode, "\"" + leftNode + "\" invalid type: expecting an int or an int[].");
        }
        else if (leftNode.getChildren().size() > 0 && !leftNode.getKind().equals("ArrayAccess") && !Utils.isMathExpression(leftNode.getKind())){
            analysis.addReport(leftNode, "\"" + leftNode + "\" unexpected operator");
        }
        else if (rightNode.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(rightNode, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("int") && !returnValueMethod.equals("int[]"))
                analysis.addReport(rightNode, "\"" + rightNode + "\" invalid type: expecting an int or an int[].");
        }
        else if (rightNode.getChildren().size() > 0 && !rightNode.getKind().equals("ArrayAccess") && !Utils.isMathExpression(rightNode.getKind())) {
            analysis.addReport(rightNode, "\"" + rightNode + "\" unexpected operator");
        }
        else if (leftNode.getNumChildren() == 0 && rightNode.getNumChildren() == 0){
            // Identifiers or numbers.
            String parentMethodName = Utils.getParentMethodName(lessNode);
            String leftKind = Utils.getVariableType(leftNode, analysis, parentMethodName);
            String rightKind = Utils.getVariableType(rightNode, analysis, parentMethodName);

            if (!leftKind.equals("int") && !leftKind.equals("int[]")){
                analysis.addReport(rightNode, "\"" + leftNode + "\" invalid type");
            } else if (!rightKind.equals("int") && !rightKind.equals("int[]")) {
                analysis.addReport(leftNode, "\"" + leftNode + "\" invalid type");
            }
        }

        return true;
    }

    public Boolean visitAnd(JmmNode andNode, Analysis analysis){
        JmmNode leftNode = andNode.getChildren().get(0);
        JmmNode rightNode = andNode.getChildren().get(1);
        String parentMethodName = Utils.getParentMethodName(andNode);

        // Final elements and operations.
        // Check functions (test.bla() && true); where test is from the T class.
        if (leftNode.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(leftNode, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("boolean"))
                analysis.addReport(leftNode , "\"" + leftNode + "\" invalid type: expecting an boolean.");
        }
        else if (leftNode.getNumChildren() > 0) {
            if(!Utils.isBooleanExpression(leftNode.getKind())){
                analysis.addReport(leftNode, "\"" + leftNode + "\" invalid expression");
            }
        }
        else if (!Utils.getVariableType(leftNode, analysis, parentMethodName).equals("boolean")){
            analysis.addReport(leftNode, "\"" + leftNode + "\" invalid expression");
        }
        // Check functions (test.bla() && true); where test is from the T class.
        else if (rightNode.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(rightNode, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("boolean"))
                analysis.addReport(rightNode , "\"" + rightNode + "\" invalid type: expecting an boolean.");
        }
        else if (rightNode.getNumChildren() > 0) {
            if(!Utils.isBooleanExpression(rightNode.getKind())){
                analysis.addReport(rightNode, "\"" + rightNode + "\" invalid expression");
            }
        }
        else if(!Utils.getVariableType(rightNode, analysis, parentMethodName).equals("boolean")){
            analysis.addReport(rightNode, "\"" + rightNode + "\" invalid expression");
        }
        return true;
    }


    public Boolean visitNot(JmmNode notNode, Analysis analysis) {
        JmmNode child = notNode.getChildren().get(0);
        String parentMethodName = Utils.getParentMethodName(notNode);

        if (child.getKind().equals("Dot")){
            String returnValueMethod = Utils.getReturnValueMethod(child, analysis);
            if (!returnValueMethod.equals("undefined") && !returnValueMethod.equals("boolean"))
                analysis.addReport(child, "\"" + child + "\" invalid type: expecting an boolean.");
        }
        else if (!Utils.isBooleanExpression(child.getKind()) && !Utils.getVariableType(child, analysis, parentMethodName).equals("boolean"))
            analysis.addReport(child, "\"" + child + "\" invalid expression");

        return true;
    }

}
