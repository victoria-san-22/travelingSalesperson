/*
* AUTHOR: Victoria Santos and 210 instructors
* FILE: DGraph.java
* ASSIGNMENT: PA11 Traveling Salesperson
* COURSE: CSc 210 001; Spring 2022
* PURPOSE: This program creates a directed graph given
* an .mtx file of nodes and weighted edges between them.
* It contains both the DGraph class and the Edge class.
* The DGraph implements an ArrayList of Edge objects
* to simulate a digraph in code.
* 
* 
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DGraph.java
 * 
 * Represents a directed graph. The nodes are represented as
 * integers ranging from 1 to num_nodes inclusive.
 * The weights are assumed to be >= zero.
 * 
 * Usage instructions:
 * 
 * Construct a DGraph
 * DGraph graph = new DGraph(numNodes);
 * 
 * Add an edge
 * graph.addEdge(v, w, weight);
 * 
 * Other useful methods:
 * graph.getWeight(v,w)
 * graph.getNumNodes()
 * List<Integer> list = graph.getNeighbors(v);
 * 
 */
public class DGraph {

    private List<Edge> graph;
    private int numNodes;
    
    /*
     * DONE
     * Constructs an instance of the DGraph class with # nodes numNodes.
     */
    public DGraph(int numbNodes) {
        graph = new ArrayList<Edge>();
        numNodes = numbNodes;
    }

    /**
     * DONE
     * Adds the directed edge (v,w) to the graph
     * 
     * @param v
     * @param w
     */
    public void addEdge(int v, int w, double distance) {
        Edge newEdge = new Edge(v, w, distance);
        if (!graph.contains(newEdge)) {
            graph.add(newEdge);
        }
    }

    /*
     * DONE
     * Returns the number of nodes in this graph.
     */
    public int getNumNodes() {
        return numNodes;
    }

    /*
     * DONE
     * Returns the weight for the given edge.
     * Returns -1 if there is no edge between the nodes v and w.
     */
    public double getWeight(int v, int w) {

        for (Edge edge : graph) {
            if (edge.node1 == v && edge.node2 == w) {
                return edge.weight;
            }
        }
        return -1;
    }

    /**
     * DONE
     * For a given node returns a sorted list of all its neighbors.
     * 
     * @param v
     *            Node identifier
     * @return A sorted list of v's neighbors.
     */
    public List<Integer> getNeighbors(int v) {
        List<Integer> neighbors = new ArrayList<Integer>();

        for (Edge edge : graph) {
            if (edge.node1 == v && !neighbors.contains(edge.node2)) {
                neighbors.add(edge.node2);
            } else if (edge.node2 == v && !neighbors.contains(edge.node1)) {
                neighbors.add(edge.node1);
            }
        }

        Collections.sort(neighbors);
        return neighbors;
    }


    /* --------------------------------------- */
    /*
     * You should not need to touch anything below this line,
     * except for maybe the name edges in the for each loop just below
     * in the toDotString method if you named your collection of edges
     * differently.
     */

    // Create a dot representation of the graph.
    public String toDotString() {
        String dot_str = "digraph {\n";
        // Iterate over the edges in order.
        Collections.sort(graph);
        for (Edge e : graph) {
            dot_str += e.toDotString() + "\n";
        }
        return dot_str + "}\n";
    }

    /* ---------------EDGE CLASS--------------- */

    /**
     * Immutable undirected edges.
     */
    public class Edge implements Comparable<Edge> {

        // Nodes in edge and weight on edge
        private final int node1;
        private final int node2;
        private final double weight;
        
        /**
         * Stores the given nodes with smaller id first.
         * 
         * @param node1
         * @param node2
         */
        public Edge(int node1, int node2, double weight) {
            assert weight >= 0.0;
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }

        /**
         * @return an directed edge string in dot format
         */
        public String toDotString() {
            return "" + node1 + " -> " + node2 + " [label=\"" + weight
                    + "\"];";
        }

        /**
         * Lexicographical ordering on edges (node1,node2).
         */
        public int compareTo(Edge other) {
            if (this.equals(other)) {
                return 0; // this and other are equal
            } else if ((node1 < other.node1)
                    || (node1 == other.node1 && node2 < other.node2)) {
                return -1; // this is less than other
            } else {
                return 1; // this is greater than other
            }
        }

        /**
         * Lexicographical ordering on edges (node1,node2).
         */
        public boolean equals(Object o) {
            if (!(o instanceof Edge)) {
                return false;
            }
            Edge other = (Edge) o;
            return (node1 == other.node1) && (node2 == other.node2);
        }

        /**
         * Know number of nodes when read in input file, so can give each edge a
         * unique hash code.
         */
        public int hashCode() {
            return getNumNodes() * node1 + node2;
        }
    }

}
