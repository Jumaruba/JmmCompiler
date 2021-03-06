package registerAllocation;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Method;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import registerAllocation.coloring.GraphColoring;
import registerAllocation.coloring.InterferenceGraph;
import registerAllocation.dataflow.DataflowAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

public class AllocateRegister {
    OllirResult ollirResult;
    ClassUnit classUnit;
    int maxRegisters;
    public AllocateRegister(OllirResult ollirResult, int maxRegisters){
        this.ollirResult = ollirResult;
        classUnit = ollirResult.getOllirClass();
        this.maxRegisters = maxRegisters;
    }

    /**
     * This method will build the var table for all the methods.
     */
    public void allocateRegistersClass() throws OptimizeException {
        for (var method: classUnit.getMethods()) {
            allocateRegisterMethod(method);
        }
    }

    /**
     * This method calls the data analysis and then allocate the registers
     * with graph coloring.
     * @param method Method for the allocation.
     */
    public Boolean allocateRegisterMethod(Method method) throws OptimizeException {
        DataflowAnalysis dataflowAnalysis = new DataflowAnalysis(method);
        dataflowAnalysis.build();
        HashMap<String, ArrayList<String>> analysisInterference = dataflowAnalysis.getInterference();
        InterferenceGraph interferenceGraph = new InterferenceGraph(analysisInterference);
        GraphColoring graphColoring = new GraphColoring(maxRegisters, interferenceGraph);
        if (!graphColoring.buildStack())
            throw new OptimizeException("Not possible to allocate registers");
        if (!graphColoring.coloring())
            throw new OptimizeException("Not possible to allocate registers");

        var varTable = method.getVarTable();
        for (var node: interferenceGraph.getNodeList()) {
            varTable.get(node.getValue()).setVirtualReg(node.getRegister() + method.getParams().size());
        }

        return true;
    }
}
