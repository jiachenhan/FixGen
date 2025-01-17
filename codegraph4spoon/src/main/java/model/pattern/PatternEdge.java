package model.pattern;

import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PatternEdge implements Serializable {

    public enum EdgeType {AST, DATA_DEP, CONTROL_DEP, DEF_USE, ACTION, NULL};
    public PatternEdge.EdgeType type;
    private PatternNode source;
    private PatternNode target;
    private Map<Edge, CodeGraph> _edgeGraphInstances = new LinkedHashMap<>();
    private boolean isAbstract = false;

    public PatternEdge(PatternNode source, PatternNode target, EdgeType type) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
    }

    public void remove() {
        this.source.outEdges().remove(this);
        this.target.inEdges().remove(this);
    }

    public Map<Edge, CodeGraph> getInstance() {
        return _edgeGraphInstances;
    }

    public void addInstance(Edge edge, CodeGraph cg) {
        _edgeGraphInstances.put(edge, cg);
    }

    public static EdgeType getEdgeType(Edge.EdgeType cgEdgeType) {
        if (cgEdgeType == Edge.EdgeType.DATA_DEP)
            return EdgeType.DATA_DEP;
        else if (cgEdgeType == Edge.EdgeType.DEF_USE)
            return EdgeType.DEF_USE;
        else if (cgEdgeType == Edge.EdgeType.CONTROL_DEP)
            return EdgeType.CONTROL_DEP;
        else if (cgEdgeType == Edge.EdgeType.AST)
            return EdgeType.AST;
        else if (cgEdgeType == Edge.EdgeType.ACTION)
            return EdgeType.ACTION;
        else
            return EdgeType.NULL;
    }

    public PatternNode getSource() {
        return source;
    }

    public PatternNode getTarget() {
        return target;
    }

    public String getLabel() {
        switch (type) {
            case AST:
                return "AST";
            case CONTROL_DEP:
                return "Control Dep";
            case DATA_DEP:
                return "Data Dep";
            case DEF_USE:
                return "Define Use";
            case ACTION:
                return "Action";
            case NULL:
                return "null";
        }
        return "";
    }

    public int getInstanceNumber() {
        return _edgeGraphInstances.size();
    }

    public String toLabel() {
        StringBuilder label = new StringBuilder();
        for (Map.Entry<Edge, CodeGraph> entry : _edgeGraphInstances.entrySet()) {
            if (label.length() != 0)
                label.append("\n");
            Edge e = entry.getKey();
            CodeGraph cg = entry.getValue();
            String ins = String.format("%s:%d", cg.getGraphName(), cg.getElementId(e));
            label.append(ins);
        }
        return label.toString();
    }

    public boolean isAbstract() { return isAbstract; }

    public void setAbstract(boolean abs) { isAbstract = abs; }
}