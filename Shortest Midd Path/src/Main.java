//package ShortestMiddPath;
/*
* Author: Shelby Kimmel
* Implements the Bellman Ford algorithm on data from https://geodata.vermont.gov/datasets/VTrans::vt-road-centerline

*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Math;
import java.io.File;


public class Main {


	public static void main(String[] args) throws IOException{

		// The following creates an adjacency list data structure that stores the vertices and edges in the graph. 
		// (I have written this part of the algorithm for you.)
		// You may have to adjust the file address in the following line to your computer
		Graph graph = new Graph("./src/Data/VT_Road_Centerline.csv");
		
		//System.out.println("Graph adjList: " + graph.getAdjList()); 
		//System.out.println("Graph nodeDict: " + graph.getDict()); 
		//System.out.println("Graph inverseNodeDict: " + graph.getInverseNodeDict()); 
		// The following should implement the array-filling part of the Bellman Ford algorithm 
		// for all points on the graph, starting at node 53980
		// 53980 is the closest node to 75 Shannon St. (You will need to modify it in the file Graph.java)
		Double[][] dpArray = graph.ShortestDistance(53980);

		System.out.println("You should take the following walking route to get from 75 Shannon to Green Peppers:");
		graph.ShortestPath(30783,dpArray);
		graph.ShortestPath(53983, dpArray);
		graph.ShortestPath(5878, dpArray);

	}

	/*
	 * What should the output of the path look like? 
	 * I'm not too picky, but something like, a list of street names, 
	 * separated by commas, that you could follow to get from 75 Shannon to your destination.
	 * 
	 * How do I find other places to test? 
	 * Go to Vermont Geodata Portal, and click on the road that your destination on. 
	 * You next need to figure out if your destination is closest to the start or end id. 
	 * The problem is that you don't know which end of the road is the start and which is the end, 
	 * so go to a neighboring road and see which start id or end id it has in common.
	 * 
	 * Is the adjacency list a reverse or regular list? 
	 * As discussed in the code, the graph is undirected, so the adjacency list is 
	 * both a regular and reverse list at the same time.
	 * 
	 * I'm getting repeated street names - what is going on? 
	 * As long as you understand why they are repeated, you can leave them in the output. 
	 * Looking at street names on the Vermont Geodata Portal at the link above might be helpful.
	 */



	

}