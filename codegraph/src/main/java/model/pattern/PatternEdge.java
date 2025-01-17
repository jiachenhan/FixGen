package model.pattern;

import model.CodeGraph;
import model.graph.edge.Edge;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PatternEdge {
    public enum EdgeType {AST, DATA_DEP, CONTROL_DEP, DEF_USE, ACTION, NULL};
    public PatternEdge.EdgeType type;
    private PatternNode source;
    private PatternNode target;
    private Map<Edge, CodeGraph> _edgeGraphInstances = new LinkedHashMap<>();

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
}
