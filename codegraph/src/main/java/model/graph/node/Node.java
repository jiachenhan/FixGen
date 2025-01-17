package model.graph.node;

import model.NodeComparator;
import model.graph.Scope;
import model.graph.edge.*;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.NameExpr;
import model.graph.node.stmt.StmtNode;
import org.eclipse.jdt.core.dom.*;
import utils.MappingStore;

import java.io.Serializable;
import java.util.*;

public abstract class Node implements NodeComparator {
    protected String _fileName;
    protected int _startLine;
    protected int _endLine;
    /**
     * original AST node in the JDT abstract tree model
     * NOTE: AST node does not support serialization
     */
    protected transient ASTNode _astNode;
    /**
     * parent node in the abstract syntax tree
     */
    protected Node _parent;
    /**
     * control dependency
     */
    protected Node _controlDependency = null;
    /**
     * data dependency
     */
    protected Set<Node> _dataDependency = new HashSet<>();

    public ArrayList<Edge> inEdges = new ArrayList<>();
    public ArrayList<Edge> outEdges = new ArrayList<>();

    private Scope _scope;

    /*********************************************************/
    /******* record matched information for change ***********/
    /*********************************************************/
    /**
     * bind the target node in the fixed version
     */
    private Node _bindingNode;
    
    /**
     * @param oriNode   : original abstract syntax tree node in the JDT model
     * @param fileName  : source file name
     * @param startLine : start line number of the node in the original source file
     * @param endLine   : end line number of the node in the original source file
     */
    public Node(ASTNode oriNode, String fileName, int startLine, int endLine) {
        this(oriNode, fileName, startLine, endLine, null);
    }

    /**
     * @param oriNode   : original abstract syntax tree node in the JDT model
     * @param fileName  : source file name (with absolute path)
     * @param startLine : start line number of the node in the original source file
     * @param endLine   : end line number of the node in the original source file
     * @param parent    : parent node in the abstract syntax tree
     */
    public Node(ASTNode oriNode, String fileName, int startLine, int endLine, Node parent) {
        _fileName = fileName;
        _startLine = startLine;
        _endLine = endLine;
        _astNode = oriNode;
        _parent = parent;
    }

    public String getFileName() {
        return _fileName;
    }

    public void addOutEdge(Edge edge) {
        outEdges.add(edge);
    }

    public void addInEdge(Edge edge) {
        inEdges.add(edge);
    }

    public void setParent(Node node) {
        _parent = node;
    }

    public void setControlDependency(Node controller) {
        _controlDependency = controller;
        if (controller != null) {
            new ControlEdge(controller, this);
        }
    }

    public void setDataDependency(Node controller) {
        if (controller != null) {
            if (controller.hasASTChildren()) {
                for (Edge out : controller.outEdges) {
                    if (out instanceof ASTEdge) {
                        setDataDependency(out.getTarget());  // not only for direct children
                    }
                }
            } else {
                if (controller instanceof NameExpr && !controller.getASTNode().getLocationInParent().getId().equals("name"))
                    new DataEdge(controller, this);
            }
        }
    }

    public void addDataDepNode(Node controller) {
        if (controller != null) {
            if (controller.hasASTChildren()) {
                for (Edge out : controller.outEdges) {
                    if (out instanceof ASTEdge && out.getTarget() instanceof NameExpr) {
                        // only for direct children
                        _dataDependency.add(controller);
                    }
                }
            } else {
                _dataDependency.add(controller);
            }
        }
    }

    public void setScope(Scope scope) { _scope = scope; }

    public boolean hasASTChildren() {
        for (Edge out : outEdges) {
            if (out instanceof ASTEdge)
                return true;
        }
        return false;
    }

    public Set<Node> getDependNodes() {
        Set<Node> all = new LinkedHashSet<>();
        for (Edge e : this.inEdges) {
            if (e.type == Edge.EdgeType.CONTROL_DEP
                    || e.type == Edge.EdgeType.DATA_DEP || e.type == Edge.EdgeType.DEF_USE)
                all.add(e.getSource());
        }
        return all;
    }

    public boolean isDependOn(Node node) {
        return this.getDependNodes().contains(node);
    }

    public Node getDirectControlNode() {
        return _controlDependency;
    }

    public LinkedHashSet<Node> getRecursiveControlNodes() {
        LinkedHashSet<Node> all = new LinkedHashSet<>();
        if (_controlDependency != null) {
            all.add(_controlDependency);
            all.addAll(_controlDependency.getRecursiveControlNodes());
        }
        return all;
    }

    public Scope getScope() {
        return _scope;
    }

    public Set<Node> getDirectDataDependentNodes() {
        Set<Node> all = new HashSet<>();
        all.addAll(_dataDependency);
        for (Node ch : getDirectASTChildren()) {
            all.addAll(ch.getDirectDataDependentNodes());
        }
        return all;
    }

    public LinkedHashSet<Node> getRecursiveDataDependentNodes() {
        LinkedHashSet<Node> all = new LinkedHashSet<>();
        for (Node ch : getDirectDataDependentNodes()) {
            all.add(ch);
            all.addAll(ch.getRecursiveDataDependentNodes());
        }
        return all;
    }


    public List<Node> getDirectASTChildren() {
        List<Node> ch = new ArrayList<>();
        for (Edge e : outEdges) {
            if (e instanceof ASTEdge)
                ch.add(e.getTarget());
        }
        return ch;
    }

    public ASTNode getASTNode() {
        return _astNode;
    }

    public int getStartSourceLine() {
        return _startLine;
    }
    public int getEndSourceLine() {
        return _endLine;
    }

    public abstract String toLabelString();

    public Node getParent() {
        return _parent;
    }

    public void setBindingNode(Node binding) {
        _bindingNode = binding;
        if (_bindingNode != null) {
            binding._bindingNode = this;
        }
    }

    public Node getBindingNode() {
        return _bindingNode;
    }

    public Set<Edge> getEdges() {
        Set<Edge> allEdges = new LinkedHashSet<>();
        allEdges.addAll(inEdges);
        allEdges.addAll(outEdges);
        return allEdges;
    }

    public Set<Node> getDataDependencySet() {
        return _dataDependency;
    }

    public boolean hasInEdge(Node src, Edge.EdgeType type) {
        for (Edge e: this.inEdges) {
            if (e.type == type && e.getSource().equals(src))
                return true;
        }
        return false;
    }

    public boolean hasOutEdge(Node tar, Edge.EdgeType type) {
        for (Edge e: this.outEdges) {
            if (e.type == type && e.getTarget().equals(tar))
                return true;
        }
        return false;
    }

    public void copyRelationFromDst(Node nodeInDst, MappingStore mappingStore) {
        for (Edge ie : nodeInDst.inEdges) {
            Node sourceInDst = ie.getSource();
            Node sourceInSrc = mappingStore.cgMap.inverse().get(sourceInDst);
            if (sourceInSrc == null) continue;
            if (this.hasInEdge(sourceInSrc, ie.type)) continue;
            if (ie.type == Edge.EdgeType.AST) {
                new ASTEdge(sourceInSrc, this);
                this.setParent(sourceInSrc);
            } else if (ie.type == Edge.EdgeType.CONTROL_DEP) {
//                new ControlEdge(sourceInSrc, this);
//                this.setControlDependency(sourceInSrc);
            } else if (ie.type == Edge.EdgeType.DATA_DEP) {
                new DataEdge(sourceInSrc, this);
                this.setDataDependency(sourceInSrc);
            } else if (ie.type == Edge.EdgeType.DEF_USE) {
                new DefUseEdge(sourceInSrc, this);
                this.setDataDependency(sourceInSrc);
            }
        }
        for (Edge oe : nodeInDst.outEdges) {
            Node targetInDst = oe.getTarget();
            Node targetInSrc = mappingStore.cgMap.inverse().get(targetInDst);
            if (this.hasOutEdge(targetInSrc, oe.type)) continue;
            if (targetInSrc != null) {
                if (oe.type == Edge.EdgeType.CONTROL_DEP) {
//                    new ControlEdge(this, targetInSrc);
//                    targetInSrc.setControlDependency(this);
                } else if (oe.type == Edge.EdgeType.DATA_DEP) {
                    new DataEdge(this, targetInSrc);
                    targetInSrc.setDataDependency(this);
                } else if (oe.type == Edge.EdgeType.DEF_USE) {
                    new DataEdge(this, targetInSrc);
                    targetInSrc.setDataDependency(this);
                }
            }
        }
    }
}

