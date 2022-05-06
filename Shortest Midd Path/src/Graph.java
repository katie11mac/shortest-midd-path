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

	// ***GIVEN***
	// Object that contains an adjacency list of a road network and a dictionary from elements of the list 
	// to indices from 0 to |V|-1, since the roads are labeled in the data by arbitrary indices. 
	// Because we are considering a walking application, we construct the adjacency list so that 
	// if there is an edge {u,v}, then u appears in the list of v's neighbors, and v appears in the list of u's neighbors. 
	// This means that the adjacency matrix and the reverse adjacency matrix are the same. 
	// In other words, the adjacency matrix that is already written here is a reverse adjacency matrix.
	HashMap<Integer, ArrayList<Road>> adjList;
	HashMap<Integer,Integer> nodeDict;
	
	
	// ADDED ATTRIBUTE: If given the value of a vertex according to nodeDict, we can find it's node number in the graph. 
	//					Essentially the reverse of nodeDict, where the values are now the keys and the keys are now the values. 
	HashMap<Integer, Integer> inverseNodeDict; 


	// ***GIVEN*** 
	public Graph(String file) throws IOException{
		// We will store the information about the road graph in an adjacency list
		// We will use a HashMap to store the Adjacency List, since each vertex in the graph has a more or less random integer name.
		// Each element of the HashMap will be an ArrayList containing all roads (edges) connected to that vertex
		adjList = new HashMap<>();
		nodeDict = null;

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
		
		// ADDED METHOD: 
		// Fill in the inverseNodeDict with the same values of nodeDict, but now the keys of nodeDict are now the values of 
		// inverseNodeDict and the values of nodeDict are now the keys of inverseNodeDict. 
		// This allows us to easily node number in the graph if given the value of that node in nodeDict. 
		inverseNodeDict = new HashMap<>(); 
		int i = 0 ;
		for (Integer nodeNum: nodeDict.keySet()) {
			inverseNodeDict.put(i, nodeNum); 
			i ++; 
		}
		
	}


	// ***GIVEN*** get functions
	public HashMap<Integer, ArrayList<Road>> getAdjList(){
		return adjList;
	}
	public HashMap<Integer,Integer> getDict(){
		return nodeDict;
	}
	public HashMap<Integer, Integer> getInverseNodeDict(){
		return inverseNodeDict; 
	}


	//***GIVEN***
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
	 * Create the array storing the objective function values of subproblems used in Bellman Ford. 
	 * 
	 * @param startNode
	 * @return array dpArray filled with the minimum distance of each subproblem in Bellman Ford
	 */
    public Double[][] ShortestDistance(Integer startNode){
    	
    	// Initialize the array which will store the objective function values 
    	int numNodes = adjList.size(); 
    	int maxNumEdges = numNodes; // bc well be including 0 
		Double[][] dpArray = new Double[numNodes][maxNumEdges]; 
		
		// Initialize the nxn array with infinities 
		// 		Based on: https://javarevisited.blogspot.com/2015/09/how-to-loop-two-dimensional-array-in-java.html#ixzz7Rnl7ZLo1
		for (int row = 0; row < dpArray.length; row++) { 
			for (int col = 0; col < dpArray[row].length; col++) { 
				dpArray[row][col] = Double.POSITIVE_INFINITY;
			} 
		}

		// Need to convert the given startNode ID to its corresponding index in the dpArray using nodeDict
		int dictStartNode = nodeDict.get(startNode); 
//		System.out.println("NODE: " + startNode + " DICT NODE: " + dictStartNode); 
		
		// Distance from a the starting vertex to itself with zero edges is a distance of 0 
		dpArray[dictStartNode][0] = 0.0; 
		
		// Iterate through all the possible edges first 
		// Start at 1 because if you start at zero then you're looking for a path from s to some vertex 
		// with 0 edges. That is impossible, so there is no path meaning that it's distance will remain infinity. 
		for(int i = 1; i < maxNumEdges; i++) { 	
			
			// Iterate through every vertex (v is the index where the node's obj func values is stored at in dpArray) 
			for(int v = 0; v < numNodes; v++) {
				
				// Need to convert back to the node's ID 
				int nodeID = inverseNodeDict.get(v); 
				ArrayList<Road> nodeAdjList = adjList.get(nodeID); 
//				System.out.println("*********************************************"); 
//				System.out.println("ADJ LIST FOR " + nodeID + ": " + nodeAdjList); 
//				System.out.println("CURRENT VALUE: " + dpArray[v][i]); 
				
				dpArray[v][i] = dpArray[v][i-1]; // One of the final options for obj function 
				
				// Only want to go through the adj list (neighboring nodes) to save time
				for (Road road : nodeAdjList) {
					int u = nodeDict.get(road.getEnd()); // the node you will be visiting before v 
//					System.out.println("StartNode: " + nodeID + " (v: "+ v +  ") EndNode: " + road.getEnd() + " (u: " + u + ")"); 
//					System.out.println(dpArray[u][i-1] + " " + road.getMiles()); 
//					System.out.println("IS THIS VALUE LESS: " + (dpArray[u][i-1] + road.getMiles())); 
					
					// Want to check if the other final option is less than the final option currently stored 
					if(dpArray[u][i-1] + road.getMiles() < dpArray[v][i]) {
						dpArray[v][i] = dpArray[u][i-1] + road.getMiles(); 
//						System.out.println("NEW ENTRY FOR " + v + " " + i); 
//						System.out.println("THIS IS THE NEW VALUE: " + dpArray[v][i]);
//						System.out.println(); 
					} 
//					System.out.println(); 
				}
			}
		}
		 
//		System.out.println(Arrays.deepToString(dpArray));
		
		return dpArray;
    }

    /**
     * 
     * Work backwards through the array created in ShortestDistance and output the 
     * sequence of streets you should take to get from your starting point to your ending point.
     * 
     * @param endNode
     * @param dpArray
     */
    public void ShortestPath(Integer endNode, Double[][] dpArray){

    	// Need to get the corresponding index in the dpArray for the endNode 
    	int dictEndNode = nodeDict.get(endNode);
    	
    	// Using i and v to track our position in A as we backtrack 
    	// i is the max number of edges and v is the vertex we will currently be evaluating 
    	int i = (dpArray.length) - 1;  
    	int v = dictEndNode; 
    	ArrayList<Road> path = new ArrayList<Road>();
    	
//    	System.out.print("END NODE: " + endNode + " || DISTANCE: " + dpArray[dictEndNode][i]);
    	
    	// EDGE CASE: There is no path from startNode to endNode
    	if (dpArray[dictEndNode][i] == Double.POSITIVE_INFINITY) {
    		System.out.println("There is no path :("); 
    	}
    	
    	// While we have more than 0 edges allowed 
    	while (i > 0) { 
    		// First check if the optimal path is less than i edges. 
    		// If it is not, then we find what the ith edge is 
    		if (dpArray[v][i] != dpArray[v][i-1]) {
    			
    			ArrayList<Road> nodeAdjList = adjList.get(inverseNodeDict.get(v));
    			
    			// Want to go through the adj list (neighboring edges) to save sometime 
    			for (Road road : nodeAdjList) {
    				
    				int u = nodeDict.get(road.getEnd()); // The potential node we'd be visiting before v 
    				
    				if(dpArray[v][i] == dpArray[u][i-1] + road.getMiles()) {
    					path.add(0, road); // Add this road at the beginning of the list 
    					v = u; // Update the node that we are currently at 
    					break; 
    					// We can break out of loop because we've found the ith edge and don't need 
    					// to check any further edges 
    				}
    				
    			}
    			
    		}
    		
    		i--; 
    		
    	}
    	
//    	System.out.println(path); 
    	
    	// Print out the street names for the path 
    	for (Road road : path) {
    		System.out.println(road.getName()); 
    	}
    	
	}			
				

}