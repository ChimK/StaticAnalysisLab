package dependenceAnalysis.util.dependenceAnalysis.util.cfg;

import dependenceAnalysis.util.cfg.Node;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Graph {

	/**
	 * A facade class to store graphs as DirectedMultiGraphs using the JGraphT framework.
	 */

	protected DirectedMultigraph<dependenceAnalysis.util.cfg.Node, DefaultEdge> graph;

	public Graph(){
        dependenceAnalysis.util.cfg.Node.sNextId = 1;
		graph = new DirectedMultigraph<dependenceAnalysis.util.cfg.Node, DefaultEdge>(new ClassBasedEdgeFactory<dependenceAnalysis.util.cfg.Node, DefaultEdge>(DefaultEdge.class));

	}

	public void addNode(dependenceAnalysis.util.cfg.Node n){
		graph.addVertex(n);
	}

	public void addEdge(dependenceAnalysis.util.cfg.Node a, dependenceAnalysis.util.cfg.Node b) {
		graph.addEdge(a,b);
	}

	/**
	 * Returns the immediate predecessors of a node.
	 * @param a
	 * @return
	 */
	public Set<dependenceAnalysis.util.cfg.Node> getPredecessors(dependenceAnalysis.util.cfg.Node a){
		Set<dependenceAnalysis.util.cfg.Node> preds = new HashSet<dependenceAnalysis.util.cfg.Node>();
		for(DefaultEdge de : graph.incomingEdgesOf(a)){
			preds.add(graph.getEdgeSource(de));
		}
		return preds;
	}

	/**
	 * Returns the immediate successors of a node.
	 * @param a
	 * @return
	 */
	public Set<dependenceAnalysis.util.cfg.Node> getSuccessors(dependenceAnalysis.util.cfg.Node a){

		Set<dependenceAnalysis.util.cfg.Node> succs = new HashSet<dependenceAnalysis.util.cfg.Node>();
		for(DefaultEdge de : graph.outgoingEdgesOf(a)){
			succs.add(graph.getEdgeTarget(de));
		}
		return succs;
	}

	/**
	 * Returns all of the nodes in the graph.
	 * @return
	 */
	public Set<dependenceAnalysis.util.cfg.Node> getNodes(){
		return graph.vertexSet();
	}

	/**
	 * Returns the entry node - the node with no predecessors.
	 * Assumes that there is only one such node in the graph.
	 * @return
	 */
	public dependenceAnalysis.util.cfg.Node getEntry(){
		for(dependenceAnalysis.util.cfg.Node n : getNodes()){
			if(graph.incomingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}

	/**
	 * Returns the exit node - the node with no successors.
	 * Assumes that there is only one such node in the graph.
	 * @return
	 */
	public dependenceAnalysis.util.cfg.Node getExit(){
		for(dependenceAnalysis.util.cfg.Node n : getNodes()){
			if(graph.outgoingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}

	/**
	 * Returns a representation of the graph in the GraphViz dot format. This can be written to a file and visualised using GraphViz.
	 * @return
	 */
	public String toString(){
		String dotString = "digraph cfg{\n";
		for (dependenceAnalysis.util.cfg.Node node : getNodes()) {
			for (dependenceAnalysis.util.cfg.Node succ: getSuccessors(node)) {
				dotString+=node.toString()+"->"+succ.toString()+"\n";
			}
		}
		dotString+="}";
		return dotString;
	}


	/**
	 * Return all transitive successors of m - i.e. any instructions
	 * that could eventually be reached from m.
	 * @param m
	 * @return
     */
	public Collection<dependenceAnalysis.util.cfg.Node> getTransitiveSuccessors(dependenceAnalysis.util.cfg.Node m){
		return transitiveSuccessors(m, new HashSet<dependenceAnalysis.util.cfg.Node>());
	}

	private Collection<dependenceAnalysis.util.cfg.Node> transitiveSuccessors(dependenceAnalysis.util.cfg.Node m, Set<dependenceAnalysis.util.cfg.Node> done){
		Collection<dependenceAnalysis.util.cfg.Node> successors = new HashSet<dependenceAnalysis.util.cfg.Node>();
		for(dependenceAnalysis.util.cfg.Node n : getSuccessors(m)){
			if(!done.contains(n)) {
				successors.add(n);
				done.add(n);
				successors.addAll(transitiveSuccessors(n, done));
			}
		}
		return successors;
	}

	/**
	 * For a given pair of nodes in a DAG, return the ancestor that is common to both nodes.
	 *
	 * Important: This operation presumes that the graph contains no cycles.
	 * @param x
	 * @param y
	 * @return
	 */
	public dependenceAnalysis.util.cfg.Node getLeastCommonAncestor(dependenceAnalysis.util.cfg.Node x, dependenceAnalysis.util.cfg.Node y) {
        dependenceAnalysis.util.cfg.Node current = x;
        while(!containsTransitiveSuccessors(current,x,y)){
            current = getPredecessors(current).iterator().next();
        }
        return current;
    }

	private boolean containsTransitiveSuccessors(dependenceAnalysis.util.cfg.Node x, dependenceAnalysis.util.cfg.Node x2, dependenceAnalysis.util.cfg.Node y) {
		Collection<Node> transitiveSuccessors = getTransitiveSuccessors(x);
        if(transitiveSuccessors.contains(x2) && transitiveSuccessors.contains(y))
        	return true;
        else
        	return false;
	}

}
