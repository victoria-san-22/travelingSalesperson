/*
* AUTHOR: Victoria Santos
* FILE: PA11Main.java
* ASSIGNMENT: PA11 Traveling Salesperson
* COURSE: CSc 210 001; Spring 2022
* PURPOSE: This program is based on the famous NP-Complete Problem
* Traveling Salesperson. Given a number of locations to visit, it
* must attempt to find the shortest path one could take to visit
* every location provided. This program tries three approaches 
* to solve this problem: heuristic, recursive backtracking, and
* an (attempted) improved version on the recursive backtracking
* method. It also provides comparisons between each algorithm's
* trip cost and runtime to give users a better idea of how
* these approaches differ, especially in terms of correctness 
* vs. efficiency. 
* 
* USAGE: 
* java .mtx infile, String program command
*
* where infile is the name of an input file in the following format
*
* -------------------------------------- EXAMPLE INPUT ---------------------------------------------
* Input file: example.csv
* --------------------------------------------------------------------------------------------------
* | %%MatrixMarket matrix coordinate real general
* | %-------------------------------------------------------------------------------
* | % Driving distances between some cities in AZ.
* | % 1: Tucson
* | % 2: Phoenix
* | % 3: Prescott
* | % 4: Show Low
* | % 5: Flagstaff
* | %
* | % author: Michelle Strout
* | % kind: directed weighted graph
* | %-------------------------------------------------------------------------------
* | 5 5 20
* | 1 2 113.0
* | 2 1 113.0
* | 1 5 209.48
* | 5 1 209.48
* | 2 5 144.38
* | 5 2 144.38
* | 2 4 129.98
* | ....
* --------------------------------------------------------------------------------------------------
* 
* Input: program command
* ---------------------------
* | HEURISTIC
* | BACKTRACK
* | MINE
* | TIME
* ---------------------------
* 
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class PA11Main {


    /**
     * Main function for PA11. Uses String[] args to get
     * the given .mtx file and call the function corresponding
     * with the given command.
     * 
     * @param String[]
     *            args, with .mtx file and command
     * 
     * @return none
     */
    public static void main(String[] args) {
        DGraph graph = createGraph(args[0]);
        
        if (args[1].equals("HEURISTIC")) {
            Trip heuristicTrip = heuristic(graph);
            System.out.println(heuristicTrip.toString(graph));
        }
        else if (args[1].equals("BACKTRACK")) {
            Trip recursiveTrip = recursiveBacktrack(graph);
            System.out.println(recursiveTrip.toString(graph));
        }
        else if (args[1].equals("MINE")) {
            Trip myTrip = mine(graph);
            System.out.println(myTrip.toString(graph));
        }
        else if (args[1].equals("TIME")) {
            getTimes(graph);
        }
     

    }

    /**
     * Responsible for taking in the given .mtx file, processing it,
     * and using the data to create the DGraph. It utilizes a helper
     * function to sort the data and add the edges to the graph.
     * 
     * @param String
     *            fileName, the name of the .mtx file
     * 
     * @return the final Dgraph
     */
    public static DGraph createGraph(String fileName) {
        String header = "";
        DGraph graph;

        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                header = scanner.nextLine();
                if (header.charAt(0) != '%') {
                    break;
                }
            }
            String[] headerVals = header.split("\\s+");
            String numNodes = String.valueOf(headerVals[0]);
            graph = new DGraph(Integer.parseInt(numNodes));
            populateGraph(graph, scanner);

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return null;
        }
        return graph;
    }

    /**
     * Private method used to add each edge to the graph
     * 
     * @param Dgraph
     *            graph
     * @param Scanner
     *            scanner, which has partially read
     *            the file already
     * 
     * @return none
     */
    private static void populateGraph(DGraph graph, Scanner scanner) {
        while (scanner.hasNextLine()) {
            String curr = scanner.nextLine();
            String[] vals = curr.split("\\s+");
            int node1 = Integer.parseInt(vals[0]);
            int node2 = Integer.parseInt(vals[1]);
            Double weight = Double.parseDouble(vals[2]);
            graph.addEdge(node1, node2, weight);
        }
    }

    /**
     * Heuristic approach to finding a trip path. Is a greedy
     * algorithm, in that for each node, it will always take
     * the shortest path option at that moment. The function
     * looks for the closest neighbor for each node, then
     * adds it to the trip before checking to be checked next.
     * 
     * 
     * @param Dgraph
     *            graph
     * 
     * @return Trip trip, the shortest trip found according
     *         to the heuristic
     */
    public static Trip heuristic(DGraph graph) {
        Trip trip = new Trip(graph.getNumNodes());
        trip.chooseNextCity(1);
        int currentCity = 1;

        for (int k = 2; k <= graph.getNumNodes(); k++) {
            List<Integer> neighbors = graph.getNeighbors(currentCity);
            int closest = 0;
            double closestWeight = 0;
            for (int neighbor : neighbors) {
                double weight = graph.getWeight(currentCity, neighbor);
                if (trip.isCityAvailable(neighbor)) {
                    if (closestWeight == 0) {
                        closest = neighbor;
                        closestWeight = weight;
                    } else if (weight < closestWeight) {
                        closest = neighbor;
                        closestWeight = weight;
                    }
                }
            }
            trip.chooseNextCity(closest);
            currentCity = closest;
        }
        return trip;
    }
    
    /**
     * Recursive backtracking method; starts the recursive call with
     * the graph and two starting trips based on the graph. the
     * second trip should become the shortest one by the end of the
     * search. utilizes a helper to do the recursive work
     * 
     * @param Dgraph
     *            graph
     * 
     * @return minTrip, the shortest trip in the graph
     */
    public static Trip recursiveBacktrack(DGraph graph) {
        
        Trip currTrip = new Trip(graph.getNumNodes());
        currTrip.chooseNextCity(1);
        Trip minTrip = new Trip(graph.getNumNodes());
        recursiveBacktrackHelper(graph, currTrip, minTrip);
        return minTrip;
             
    }
    
    /**
     * Recursive backtracking helper; follows a trip path to the
     * end via currTrip, then checks if the cost of currTrip is
     * less than the current minTrip. if so, the minTrip becomes
     * currTrip. this cycle continues until no paths remain.
     * 
     * @param Dgraph
     *            graph
     * @param Trip
     *            currTrip, the trip for the path we're currently following
     * @param Trip
     *            minTrip, the least costly trip we've found so far
     * 
     * @return none
     */
    private static void recursiveBacktrackHelper(DGraph graph, Trip currTrip,
            Trip minTrip) {
        if (currTrip.citiesLeft().isEmpty()) {
            // System.out.println("empty");
            if (currTrip.tripCost(graph) < minTrip.tripCost(graph)) {
                // System.out.println("old mintrip = " +
                // minTrip.toString(graph));
                minTrip.copyOtherIntoSelf(currTrip);
                // System.out.println("new mintrip = " +
                // minTrip.toString(graph));
            }
            return;
        }

        if (currTrip.tripCost(graph) <= minTrip.tripCost(graph)) {
            // System.out.println("recursive portion");
            for (int city : currTrip.citiesLeft()) {
                // System.out.println(city);
                currTrip.chooseNextCity(city);

                recursiveBacktrackHelper(graph, currTrip, minTrip);

                currTrip.unchooseLastCity();
            }
        }
    }

    /**
     * Adjusted backtracking method; starts the recursive call with
     * the graph and two starting trips based on the graph. the
     * second trip should become the shortest one by the end of the
     * search. utilizes a helper to do the recursive work
     * 
     * @param Dgraph
     *            graph
     * 
     * @return minTrip, the shortest trip in the graph
     */
    public static Trip mine(DGraph graph) {

        Trip currTrip = new Trip(graph.getNumNodes());
        currTrip.chooseNextCity(1);
        Trip minTrip = new Trip(graph.getNumNodes());
        double minCost = Double.MAX_VALUE;
        mineHelper(graph, currTrip, minTrip, minCost);
        return minTrip;

    }

    /**
     * Adjusted backtracking helper; follows a trip path to the
     * end via currTrip, then checks if the cost of currTrip is
     * less than the current minTrip. if so, the minTrip becomes
     * currTrip. this cycle continues until no paths remain.
     * 
     * @param Dgraph
     *            graph
     * @param Trip
     *            currTrip, the trip for the path we're currently following
     * @param Trip
     *            minTrip, the least costly trip we've found so far
     * 
     * @return none
     */
    private static void mineHelper(DGraph graph, Trip currTrip, Trip minTrip,
            double minCost) {
        if (currTrip.citiesLeft().isEmpty()) {
            // System.out.println("empty");
            if (currTrip.tripCost(graph) < minTrip.tripCost(graph)) {
                // System.out.println("old mintrip = " +
                // minTrip.toString(graph));
                minTrip.copyOtherIntoSelf(currTrip);
                minCost = minTrip.tripCost(graph);
                // System.out.println("new mintrip = " +
                // minTrip.toString(graph));
            }
            return;
        }

        else if (currTrip.tripCost(graph) <= minTrip.tripCost(graph)
                && currTrip.tripCost(graph) < minCost) {
            // System.out.println("recursive portion");
            for (int city : currTrip.citiesLeft()) {
                // System.out.println(city);
                currTrip.chooseNextCity(city);

                recursiveBacktrackHelper(graph, currTrip, minTrip);

                currTrip.unchooseLastCity();
            }
        }
    }

    /**
     * Prints out the costs and runtimes of each algorithm
     * (heuristic, mine, recursive) for the given data.
     * Helps to illustrate how costs and runtimes vary
     * between the approaches.
     * 
     * @param Dgraph
     *            graph
     * 
     * @return none
     */
    private static void getTimes(DGraph graph) {
        long startTime = System.nanoTime();
        Trip heuristicTrip = heuristic(graph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("heuristic: cost = " + heuristicTrip.tripCost(graph)
                + ", " + duration + " milliseconds");

        startTime = System.nanoTime();
        Trip myTrip = recursiveBacktrack(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("mine: cost = " + myTrip.tripCost(graph) + ", "
                + duration + " milliseconds");

        startTime = System.nanoTime();
        Trip recursiveTrip = recursiveBacktrack(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("recursive: cost = " + recursiveTrip.tripCost(graph)
                + ", " + duration + " milliseconds");

    }

}
