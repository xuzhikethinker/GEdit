package edu.usu.cs.algorithms;

import edu.usu.cs.graph.Edge;
import edu.usu.cs.graph.Graph;
import edu.usu.cs.graph.Node;
import edu.usu.cs.graph.Path;
import edu.usu.cs.graph.PathContainer;
import edu.usu.cs.graph.PathException;

/**
 * Performs a topological sort on a graph data type.
 * @author Anthony Nichols
 * @author Karl Smith
 * @author Randy Secrist
 */
public class Topological extends Algorithm  {
	private Node[] nodes;
	private int[] path;
	private int pathLength;
	private PathContainer pc;
	private boolean returnFirst;
	private boolean ripMore = true;
	private Graph theGraph;
	private Visitor visitor;
	
	/**
	 * Constructs a default Topological Sort Algorithm.
	 */
	public Topological() {
		super();
	}
	
	/**
	 * Constructs a Topological Sort Algorith which will only
	 * find a single path.
	 * @param b Indicates that the algorithm should only return one Path.
	 */
	public Topological(boolean b) {
		this.returnFirst = b;
	}
	
	/**
	 * Constructs a Topological Sort Algorithm with a Visitor.
	 * @param v The visitor to use during traversal.
	 */
	public Topological(Visitor v) {
		this.visitor = v;
	}
	
	/**
	 * Constructs a Topological Sort Algorithm with a Visitor.
	 * @param v The visitor to use during traversal.
	 * @param b Indicates that the algorithm should only return one Path.
	 */
	public Topological(Visitor v, boolean b) {
		this.visitor = v;
		this.returnFirst = b;
	}

	/**
	 * Performs the actual work of this algorithm.
	 */
	public PathContainer doAlgorithm(Graph grf) {
		theGraph = grf;
		nodes = theGraph.getHeap();
		pc = new PathContainer();
		path = new int[theGraph.getArraysize()];
		pathLength = 0;
	
		loop();
		return pc;
	}

	private int[] getAvailable() {
	   int size = theGraph.getArraysize();
	   int[] valid = new int[theGraph.getCount()];
	   int validCount = 0;
	
	   for (int i = 0; i < size; i++) {
		   if (theGraph.getNode(i).getId() == -1) continue;
		    
		   boolean add = true;
		   
		   for (int j = 0; j < size; j++) {
			   if (nodes[j].getId() == -1) continue;
			   if (theGraph.isEdge(j, i)) {
				   if (nodes[j].getVisited() == Node.FALSE) add = false;
			   }
		   }
		   if (nodes[i].getVisited() == Node.FALSE) {
			   if (add) valid[validCount++] = i;
		   }
	   }
	   
	   int[] validCopy = new int[validCount];
	   System.arraycopy(valid, 0, validCopy, 0, validCount);
	
	   return validCopy;
	}

	public String getMenuName() {
		return "Topological Sort";
	}

	private boolean inList(int[] list, int check)
	{
		for (int i=0; i<list.length; i++)
		{
			if (list[i]==check)
				return true;
		}
		return false;    
	}


	private void loop() {
		int[] notVisited = getAvailable();
		if (notVisited.length == 0) {
			ripPath();
			return;
		}
		else {
		    if (ripMore) {
				for (int i = 0; i < notVisited.length; i++) {
					path[pathLength++] = notVisited[i];
					nodes[notVisited[i]].setVisited(Node.TRUE);  // set dirty flag
	
					//recurse (be sure to ALWAYS clean up after call)
					loop();
	
					nodes[notVisited[i]].setVisited(Node.FALSE);  // reset dirty flag
					pathLength--;
				}
		    }
		}
	}

	private void ripPath() {
		if (!ripMore)
			return;
		int size = theGraph.getArraysize();
		int maxSize = (2 * theGraph.getNodes().length) + theGraph.getEdges().length;
		Node[] nodePath = new Node[maxSize];
		Edge[] edgePath = new Edge[maxSize];

		int currentLocation = 0;

		for (int i = 0; i < pathLength; i++) {
			nodePath[currentLocation] = theGraph.getNode(path[i]);
						
			for (int j = 0; j < size; j++) {
				if (theGraph.getNode(j).getId() == -1)
					continue;
				if (theGraph.isEdge(path[i], j))
					edgePath[currentLocation++] = theGraph.getEdge(path[i], j);
			}

			if(currentLocation == 0) {
				currentLocation += 2;
			}
			else if(edgePath[currentLocation - 1] == null) {
				currentLocation += 2;
			}
			else {
				currentLocation++;
			}
		}

		Node[] np = new Node[currentLocation];
		Edge[] ep = new Edge[currentLocation];
		for(int i = 0; i < currentLocation; i++) {
			np[i] = nodePath[i];
			ep[i] = edgePath[i];
			if (visitor != null) {
				if (np[i] != null)
					visitor.visit(np[i]);
				if (ep[i] != null)
					visitor.visit(ep[i]);
			}
		}
		try {
			Path p = new Path(np, ep, pc.size());
			pc.addPath(p);
		}
		catch (PathException e) {
		}
		if (returnFirst)
			ripMore = false;
	}
	
	/**
	 * Executes this algorithm.
	 */
	protected PathContainer startAlgorithm(Graph theGraph)
	{
		if(theGraph.isCyclic()) {
			this.showWarningDialog("This graph contains a cycle.\n\nNo Topology Possible.");
			return null;
		}
		return doAlgorithm(theGraph);
	}

	/**
	 *graph must be  connected and directed to work
	 */
	public boolean works(Graph grf)
	{
		if(!grf.getDirected()) return false;
		if(grf.getCount() == 0) return false;
		return true;
	}    
}