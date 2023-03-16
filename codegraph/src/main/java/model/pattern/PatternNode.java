package model.pattern;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;

import java.util.*;

public class PatternNode {
    Set<String> _comparedAttrs = new LinkedHashSet<>();
    int _freq = 0;
    private Map<Node, CodeGraph> _nodeGraphInstances = new LinkedHashMap<>();

    private Set<PatternEdge> _inEdges = new LinkedHashSet<>();
    private Set<PatternEdge> _outEdges = new LinkedHashSet<>();
    private Pattern _pattern = null;
    private String _locationInParent = "";
    private String _astType = "";

    public PatternNode(Node aNode, CodeGraph aGraph, String loc, String astType) {
        _nodeGraphInstances.put(aNode, aGraph);
        _astType = astType;
        _locationInParent = loc;
    }

    public String getLocationInParent() {
        return _locationInParent;
    }

    public String getASTType() {
        return _astType;
    }

    public void setComparedAttribute(String attr) {
        _comparedAttrs.add(attr);
    }

    public void addOutEdge(PatternEdge anEdge) {
        _outEdges.add(anEdge);
    }

    public void addInEdge(PatternEdge anEdge) {
        _inEdges.add(anEdge);
    }

    public void addInstance(Node iNode, CodeGraph iGraph) {
        _nodeGraphInstances.put(iNode, iGraph);
        if (_pattern != null) {
            _pattern.getNodeMapping().put(iNode, this);
        }
    }

    public Set<PatternEdge> inEdges() {
        return _inEdges;
    }

    public Set<PatternEdge> outEdges() {
        return _outEdges;
    }

    public Map<Node, CodeGraph> getInstance() {
        return _nodeGraphInstances;
    }

    public String toLabel() {
        StringBuilder label = new StringBuilder();
        if (!_locationInParent.equals("")) {
            label.append("##" + _locationInParent + "##");
        }
        if (isPatternStart()) {
            label.append("##ActionPoint##");
        }
        for (Map.Entry<Node, CodeGraph> entry : _nodeGraphInstances.entrySet()) {
            if (label.length() != 0)
                label.append("\n");
            String ins = entry.getValue().getGraphName() + "#" + entry.getKey().getStartSourceLine() + ":" + entry.getKey().toLabelString();
            label.append(ins);
        }
        return label.toString();
    }

    public void setPattern(Pattern pattern) {
        _pattern = pattern;
    }

    public boolean isPatternStart() {
        if (_pattern != null) {
            return _pattern.getStart() == this;
        }
        return false;
    }
}
