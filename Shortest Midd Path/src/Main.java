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
		Graph graph = new Graph("./src/Data/VT_Road_Centerline.csv");
	
		// The following should implement the array-filling part of the Bellman Ford algorithm for all points on the graph, starting at node 53980
		// 53980 is the closest node to 75 Shannon St.
		Double[][] dpArray = graph.ShortestDistance(53980);

		System.out.println("You should take the following walking route to get from 75 Shannon to Green Peppers:\n");
		graph.ShortestPath(30783,dpArray);
		System.out.println("\n***NOTE: Some streeet names are repeated because the street is broken up into different sections in the data.***\n"); 

		// Testing Cases 
// 		graph.ShortestPath(53983, dpArray);
// 		graph.ShortestPath(5878, dpArray);
	}
	

}
