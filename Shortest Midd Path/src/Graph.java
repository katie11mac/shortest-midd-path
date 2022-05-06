//package ShortestMiddPath;

/*
* Author: Shelby Kimmel
* Creates a adjacency list object to store information about the graph of roads, and contains the main functions used to 
* run the Bellman Ford algorithm 

*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;
import java.io.File;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Graph {

	// Object that contains an adjacency list of a road network and a dictionary from elements of the list 
	// to indices from 0 to |V|-1, since the roads are labeled in the data by arbitrary indices. 
	// Because we are considering a walking application, we construct the adjacency list so that 
	// if there is an edge {u,v}, then u appears in the list of v's neighbors, and v appears in the list of u's neighbors. 
	// This means that the adjacency matrix and the reverse adjacency matrix are the same. 
	// In other words, the adjacency matrix that is already written here is a reverse adjacency matrix.
	HashMap<Integer, ArrayList<Road>> adjList;
	HashMap<Integer,Integer> nodeDict;
	HashMap<Integer, Integer> inverseNodeDict; // the values of nodeDict are now the keys and the keys are now the values 


	public Graph(String file) throws IOException{
		// We will store the information about the road graph in an adjacency list
		// We will use a HashMap to store the Adjacency List, since each vertex in the graph has a more or less random integer name.
		// Each element of the HashMap will be an ArrayList containing all roads (edges) connected to that vertex
		adjList = new HashMap<>();
		nodeDict = null;

	// so the adjlist's key = node and value is the list of roads connected to that road (the roads are the edges of the graph) 
	// i don't understand the point of creating a nodeDict if every node has a distinct number like why? 
		
		// Based on https://stackoverflow.com/questions/49599194/reading-csv-file-into-an-arrayliststudent-java
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(file));
		if ((line=br.readLine())==null){
			return;
		}
		while ((line = br.readLine())!=null) {
			String[] temp = line.split(",");
			//Assume all roads are two-way, and using ArcMiles as distance:
			this.addToAdjList(new Road(Integer.parseInt(temp[60]),Integer.parseInt(temp[61]),temp[9],Double.parseDouble(temp[31])));
			this.addToAdjList(new Road(Integer.parseInt(temp[61]),Integer.parseInt(temp[60]),temp[9],Double.parseDouble(temp[31])));
		}


		//For dynamic programming, we will have an array with indices 0 to |V|-1, 
		// where |V| is the number of vertices. Thus we need to associate each element of adjList with a number between 0 and |V|-1
		// We will use a Dictionary (HashMap) to do this.
		nodeDict = new HashMap<>();
		int j = 0;
		for (Integer nodeName: adjList.keySet()){
			nodeDict.put(nodeName, j);
			j++;
		}
		
		//
		inverseNodeDict = new HashMap<>(); 
		int i = 0 ;
		for (Integer nodeNum: nodeDict.keySet()) {
			inverseNodeDict.put(i, nodeNum); 
			i ++; 
		}
		
	}


	// get functions
	public HashMap<Integer, ArrayList<Road>> getAdjList(){
		return adjList;
	}
	public HashMap<Integer,Integer> getDict(){
		return nodeDict;
	}
	public HashMap<Integer, Integer> getInverseNodeDict(){
		return inverseNodeDict; 
	}


	public synchronized void addToAdjList(Road road) {
		//Adds the Road (edge) to the appropriate list of the adjacency list. 
		//This method is used by the constructor method
		//Based on https://stackoverflow.com/questions/12134687/how-to-add-element-into-arraylist-in-hashmap 
		Integer node = road.getStart();
    	ArrayList<Road> roadList = this.getAdjList().get(node);

    	// if node is not already in adjacency list, we create a list for it
    	if(roadList == null) {
    	    roadList = new ArrayList<Road>();
    	    roadList.add(road);
   		    this.getAdjList().put(node, roadList);
  	  	} 
  	  	else {
        	// add to appropriate list if item is not already in list
        	if(!roadList.contains(road)) roadList.add(road);
    	}
    	
    }

	
	/**
	 * 
	 * This method should create the array storing the objective function values of subproblems used in Bellman Ford. 
	 * 
	 * @param startNode
	 * @return
	 */
    public Double[][] ShortestDistance(Integer startNode){
    	
    	int numNodes = adjList.size(); 
    	int maxNumEdges = numNodes; // bc well be including 0 
		Double[][] dpArray = new Double[numNodes][maxNumEdges]; // Nodes are the rows and edges are the colums 
		
		// Initialize the nxn array with infinities 
		//Read more: https://javarevisited.blogspot.com/2015/09/how-to-loop-two-dimensional-array-in-java.html#ixzz7Rnl7ZLo1
		for (int row = 0; row < dpArray.length; row++) { 
			for (int col = 0; col < dpArray[row].length; col++) { 
				dpArray[row][col] = Double.POSITIVE_INFINITY;
			} 
		}

		
		// startNode does not line up with the value of the index for which they are stored in the 2D array 
		int dictStartNode = nodeDict.get(startNode); 
		System.out.println("NODE: " + startNode + " DICT NODE: " + dictStartNode); 
		
		//System.out.println("Dictionary Start Node: " + dictStartNode); 
		dpArray[dictStartNode][0] = 0.0; 
		
		
		//go through the edges 
		for(int i = 1; i < maxNumEdges; i++) { 	
			
			// going through every vertex 
			for(int v = 0; v < numNodes; v++) {
				//System.out.print(inverseNodeDict.get(v)); 
				// need to convert back to the actual node's id bc the index that its stored at is not the same 
				int nodeID = inverseNodeDict.get(v); 
				//System.out.println("NODE: " + nodeID + " v: " + v); 
				ArrayList<Road> nodeAdjList = adjList.get(nodeID); 
				System.out.println("*********************************************"); 
				System.out.println("ADJ LIST FOR " + nodeID + ": " + nodeAdjList); 
				
				System.out.println("CURRENT VALUE: " + dpArray[v][i]); 
				
//				if (i > nodeAdjList.size()) {
//					dpArray[v][i] = dpArray[v][i-1];
//					//System.out.println("THIS IS THE NEW VALUE: " + dpArray[v][i]);	
//				} else {
					
				dpArray[v][i] = dpArray[v][i-1]; 
				for (Road road : nodeAdjList) {
		
					int u = nodeDict.get(road.getEnd()); // value from 0 to 192 
					System.out.println("StartNode: " + nodeID + " (v: "+ v +  ") EndNode: " + road.getEnd() + " (u: " + u + ")"); 
					System.out.println(dpArray[u][i-1] + " " + road.getMiles()); 
					System.out.println("IS THIS VALUE LESS: " + (dpArray[u][i-1] + road.getMiles())); 
					
					//if(dpArray[u][i-1] + road.getMiles() < dpArray[v][i]) {
					// it keeps adding this road.getMiles() when the previous entry already holds the getMiles 
					if(dpArray[u][i-1] + road.getMiles() < dpArray[v][i]) {
						// INFINITY 
						// check what happens when dpArray[u][i-1]
						System.out.println("NEW ENTRY FOR " + v + " " + i); 
						//dpArray[v][i] = dpArray[u][i-1] + road.getMiles(); 
						dpArray[v][i] = dpArray[u][i-1] + road.getMiles(); 
						System.out.println("THIS IS THE NEW VALUE: " + dpArray[v][i]);
						System.out.println(); 
					} 
					System.out.println(); 
					
				}
			}
			
		}
		 
		System.out.println(Arrays.deepToString(dpArray));
		return dpArray;
    }

    /**
     * 
     * This method should work backwards through the array you created in ShortestDistance and output the 
     * sequence of streets you should take to get from your starting point to your ending point.
     * 
     * @param endNode
     * @param dpArray
     */
    public void ShortestPath(Integer endNode, Double[][] dpArray){

    	// need to get the corresponding index in the dpArray for the endNode 
    	int dictEndNode = nodeDict.get(endNode);
    	//System.out.println(endNode + " " + dictEndNode); 
    	
    	int i = (dpArray.length) - 1; 
    	//System.out.println("i is " + i); 
    	int v = dictEndNode; 
    	ArrayList<Road> path = new ArrayList<Road>(); // this could be an array instead to save space  
    	
    	System.out.print("END NODE: " + endNode + " || DISTANCE: ");
    	System.out.println(dpArray[dictEndNode][i]); // this is saying that the distance is two miles 
    	
    	
    	if (dpArray[dictEndNode][i] == Double.POSITIVE_INFINITY) {
    		System.out.println("No Path"); 
    	}
    	
    	// i think that there are some repeated roads because of the fact that the i is so high
    	// it will run until it hits 1
    	while (i > 0) { 
    		if (dpArray[v][i] != dpArray[v][i-1]) {
    			// want to go through the adjacency list instead to save sometime 
    			ArrayList<Road> nodeAdjList = adjList.get(inverseNodeDict.get(v)); 
    			//System.out.println("Node: " + v + " Adj List: " + nodeAdjList); 
    			
    			for (Road road : nodeAdjList) {
    				int u = nodeDict.get(road.getEnd());
    				if(dpArray[v][i] == dpArray[u][i-1] + road.getMiles()) {
    					path.add(0, road); // add this road at the beginning of the array
    					v = u; 
    					break; 
    				}
    			}
    		}
    		i--; 
    	}
    	System.out.println(path); 
    	System.out.println(); 
    	
	}			
				

}