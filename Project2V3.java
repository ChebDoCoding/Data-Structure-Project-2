/*
1. Jawit Poopradit 6480087
2. Possathorn Sujipisut 6480274
3. Piyakorn Rodthanong 6480569
4. Phasin Ploypicha 6480567
*/

//Package
package Project2;

//Basic Imports
import java.util.*;

//Jgraph Imports
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.*;

/*
How this program should work:
1. We get user input of the specs of the board
2. We create all the different boards (2^(2*2) - 2^ (5*5))
3. We connect all the boards
 - 3-1: We first get the names of all the boards connected to our current board
 - 3-2: We then take this name list and connect current board to the boards 
        with matching names with weight of 1

*/


////////////////////////////////////////////////////////////////////////////////

//Board Class
class Board{
    //Setup
    private boolean[][] status;
    private String name;
    private int sizeOfRow;
    
    //Constructor
    public Board(String n, int rowSize){
        //Set name and row size
        name = n;
        sizeOfRow = rowSize;
        
        boolean[][] board = new boolean[rowSize][rowSize];        
        
        //Check what the size of the board is
        //So that we can cut out the right amount of zeros
        int i = 0;
        switch(rowSize){
            case 2:
                i = 21;
                break;
            case 3:
                i = 16;
                break;
            case 4:
                i = 9;
                break;
            case 5:
                i = 0;
                break;
        }
        
        //Formating into binary
        String temp = String.format("%025d", Integer.parseInt(Integer.toBinaryString(Integer.parseInt(name))));
        
        //Set the board
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                if (temp.charAt(i) == '1'){
                    board[x][y] = true;
                } else{
                    board[x][y] = false;
                }
                
                i++;
            }
        }
        //Set status to board
        status = board;
    }
    
    ////////////////////////////////////////////////////////////////////////////  
    //Important functions
    
    //Get commands
    public boolean getStatus(int x, int y) {return status[x][y];}
    public boolean[][] getAllStatus() {return status;}
    public String getName() {return name;}
       
    //Toggle a certain light
    public boolean[][] toggle(int x, int y, boolean[][] currentBoard){
        //Set new Board
        boolean[][] newBoard = new boolean[sizeOfRow][sizeOfRow];
        for (int i = 0; i < sizeOfRow; i++) {
            System.arraycopy(currentBoard[i], 0, newBoard[i], 0, sizeOfRow);
        }
        
        //If node is outside range of graph then just return the board
        if (x < 0 || y < 0 || x >= currentBoard.length || y >= currentBoard[x].length) {
            return currentBoard;
        }
        
        //If not outside range then toggle and then return
        if (newBoard[x][y]){
            newBoard[x][y] = false;
        } else{
            newBoard[x][y] = true;
        }        
        return newBoard;
    }
    
    //Convert boolean 2D arary into string/name of board
    //This is for connecting board names
    public String convertName(boolean[][] input){
        //Converted name
        String convertedName = "";
        
        //Constructing the name
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                if (input[x][y]){
                    convertedName = convertedName + "1";
                } else{
                    convertedName = convertedName + "0";
                }
            }
        }
        
        //Formating
        //Setting the name into int
        int temp1 = Integer.parseInt(convertedName, 2);

        //Formating String into int into String with a total of 25 char
        String temp2 = String.format("%d", temp1);
        
        //Returning the name
        return temp2;        
    }
    
    //Used to check which light was toggled between moves
    public String checkLightX(boolean[][] list, String config){
        //How we are transfering the information
        String xThenY = "0000";
        
        //Plus config
        if (config.equals("+")){
            for (int x = 0; x < sizeOfRow; x++) {
                for (int y = 0; y < sizeOfRow; y++) {
                    boolean[][] dummy = new boolean[sizeOfRow][sizeOfRow];
                    for (int i = 0; i < sizeOfRow; i++) {
                        System.arraycopy(list[i], 0, dummy[i], 0, sizeOfRow);
                    }
                    //Current node
                    dummy = toggle(x,y,dummy);

                    //Connecting Nodes
                    //Below and Above
                    dummy = toggle(x, y - 1,dummy);
                    dummy = toggle(x, y + 1,dummy);

                    //Left and Right
                    dummy = toggle(x - 1, y,dummy);
                    dummy = toggle(x + 1, y,dummy);

                    //Check if the two arrays are equal
                    if (Arrays.equals(status,dummy)){
                        xThenY = String.format("%d%d", x,y);
                    }
                }
            }
        } 
        //Cross config
        else{
            for (int x = 0; x < sizeOfRow; x++) {
                for (int y = 0; y < sizeOfRow; y++) {
                    boolean[][] dummy = new boolean[sizeOfRow][sizeOfRow];
                    for (int i = 0; i < sizeOfRow; i++) {
                        System.arraycopy(list[i], 0, dummy[i], 0, sizeOfRow);
                    }

                    //Current node
                    dummy = toggle(x,y,dummy);

                    //Connecting Nodes
                    //Top right and Top left
                    dummy = toggle(x + 1, y + 1,dummy);
                    dummy = toggle(x - 1, y + 1,dummy);

                    //Bottom right and Bottom left
                    dummy = toggle(x + 1, y - 1,dummy);
                    dummy = toggle(x - 1, y - 1,dummy);

                    //Check if the two arrays are equal
                    if (Arrays.equals(status,dummy)){
                        xThenY = String.format("%d%d", x,y);
                    }
                }
            }
        }
        //Return
        return xThenY;
    }
    
    //////////////////////////////////////////////////////////////////////////// 
    //Get connecting board names in different configs
    
    //Return string name of connecting board in plus (+) config
    public ArrayList<String> getConnectingBoardNamesPlus(){
        //Create the list to return an create dummy board
        ArrayList<String> list = new ArrayList<String>();  
        
        //Get all boards connected to current board
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                boolean[][] dummy = new boolean[sizeOfRow][sizeOfRow];
                for (int i = 0; i < sizeOfRow; i++) {
                    System.arraycopy(status[i], 0, dummy[i], 0, sizeOfRow);
                }
                
                //Current node
                dummy = toggle(x,y,dummy);
                
                //Connecting Nodes
                //Below and Above
                dummy = toggle(x, y - 1,dummy);
                dummy = toggle(x, y + 1,dummy);
                
                //Left and Right
                dummy = toggle(x - 1, y,dummy);
                dummy = toggle(x + 1, y,dummy);
                
                //Add name to array list
                list.add(convertName(dummy));
            }
        }
        
        //return the list
        return list;
    }
    
    //Return string name of connecting board in cross (x) config
    public ArrayList<String> getConnectingBoardNamesCross(){
        //Create the list to return an create dummy board
        ArrayList<String> list = new ArrayList<String>();  
        
        //Get all boards connected to current board
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                boolean[][] dummy = new boolean[sizeOfRow][sizeOfRow];
                for (int i = 0; i < sizeOfRow; i++) {
                    System.arraycopy(status[i], 0, dummy[i], 0, sizeOfRow);
                }
                
                //Current node
                dummy = toggle(x,y,dummy);
                
                //Connecting Nodes
                //Top right and Top left
                dummy = toggle(x + 1, y + 1,dummy);
                dummy = toggle(x - 1, y + 1,dummy);
                
                //Bottom right and Bottom left
                dummy = toggle(x + 1, y - 1,dummy);
                dummy = toggle(x - 1, y - 1,dummy);
                
                //Add name to array list
                list.add(convertName(dummy));
            }
        }
        
        //return the list
        return list;
    }
    
    //////////////////////////////////////////////////////////////////////////// 
    //Printing graph
    
    public void print(){
        //States in Bits
        System.out.printf("\nCurrent state in bits: ");
        for (int i = 0; i < sizeOfRow; i++){
            for (int j = 0; j < sizeOfRow; j++){
                if (status[i][j]){
                    System.out.printf("1");
                } else{
                    System.out.printf("0");
                }
            } 
            System.out.printf(" ");
        }
        System.out.printf("\n");
        
        //Print the columns bar
        System.out.printf("       |");
        for (int i = 0; i < sizeOfRow; i++){
            System.out.printf(" col %2d |", i);
        }
        
        //Print the row number of the data
        for (int x = 0; x < sizeOfRow; x++){
            //New line and print row number
            System.out.printf("\n");
            System.out.printf("row %2d |", x);
            
            //For each entry, check if true or false and then print that
            for (int y = 0; y < sizeOfRow; y++){
                //Print data
                if (status[x][y]){
                    System.out.printf("   ▯▯   |");
                } else{
                    System.out.printf("   ▮▮   |");
                }
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////

//Graph Setup Class
class graphSetup{
    //Setup Graphs
    protected HashMap<String, Board>                AllBoardNodes; 
    protected ArrayList<String>                     BoardNames; 
    protected Graph<String, DefaultWeightedEdge>    mainGraph; 
    
    //Setup Variables
    int numOfRows;
    int numOfLights;
    String intialString = "";
    boolean inputSuccess = false;
    String lightConfig;   
    boolean[] currentBoardStatus; //For printing and to check if all off
    
    ////////////////////////////////////////////////////////////////////////////    
    //Constructor
    public graphSetup(){
        //Get number of rows
        Scanner scan = new Scanner(System.in);
        do {
            System.out.printf("Enter number of rows (>1 & <5): "); 
            String temp = scan.next();
            numOfRows = Integer.parseInt(temp);
            if (numOfRows > 1) {inputSuccess = true;}  
        } while (inputSuccess == false);
        numOfLights = numOfRows * numOfRows;
        currentBoardStatus = new boolean[numOfLights];
                
        //Ask user for which lights are already on        
        String temp;
        int stringLength;
        inputSuccess = false;
        System.out.printf("Enter intial state of each row\n"); 
        for (int i = 0; i < numOfRows; i++){
            inputSuccess = false;
            do{                
                System.out.printf("Row %d: ", i);
                temp = scan.next();
                stringLength = temp.length();
                //Check if it has the correct amount of chars
                if (stringLength == numOfRows){
                    //Check if it only has 1 or 0
                    int test = 0;
                    for (int j = 0; j < temp.length(); j++) {
                        if (temp.charAt(j) != '0' && temp.charAt(j) != '1' ){
                            //If detects something not 0 or 1 tag it
                            test = test + 1;
                        }    
                    }
                    //Add if successful
                    if (test == 0){
                        intialString = intialString + temp;
                        inputSuccess = true;
                    }
                }  
            } while (inputSuccess == false);
        }
        
        //Create the HashMap
        AllBoardNodes = new HashMap<String, Board>();
        
        //Create list of light node names based on graph size
        BoardNames =  createAllNodes(numOfLights);
                
        //Create a simple unweighted but directed graph
        mainGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        
        //Add all nodes into BoardNames
        Graphs.addAllVertices(mainGraph, BoardNames);
        
        //Add lightNames and Board object into AllBoardNodes
        for (String n : BoardNames){
            AllBoardNodes.put(n,new Board(n, numOfRows));
        }
        
        //Ask user for config
        inputSuccess = false;
        do {
            System.out.printf("Enter wire configuration (+ or x): "); 
            lightConfig = scan.next();
            
            //Set to + config
            if (lightConfig.equals("+")){
                //First Loop: For every item in AllBoardNodes
                for (String currentNodeName : BoardNames){
                    //Find node in searchBoard
                    Board tempBoard = searchBoard(currentNodeName);
                    
                    //Get the list of the names of the boards that are connected
                    ArrayList<String> connectingNames = tempBoard.getConnectingBoardNamesPlus();
                    
                    //Second Loop: Add an edge between current board 
                    //and all the other boards if not already exist
                    for (String otherNodes : connectingNames){
                        if (!mainGraph.containsEdge(currentNodeName, otherNodes)){
                            Graphs.addEdgeWithVertices(mainGraph, 
                                currentNodeName, otherNodes, 1);
                        }
                    }                    
                }
                
                //To get out of do while loop
                break;
            } 
            //Set to x config
            else if(lightConfig.equals("x")){
                //First Loop: For every item in AllBoardNodes
                for (String currentNodeName : BoardNames){
                    //Find node in searchBoard
                    Board tempBoard = searchBoard(currentNodeName);
                    
                    //Get the list of the names of the boards that are connected
                    ArrayList<String> connectingNames = tempBoard.getConnectingBoardNamesCross();
                    
                    //Second Loop: Add an edge between current board 
                    //and all the other boards if not already exist
                    for (String otherNodes : connectingNames){
                        if (!mainGraph.containsEdge(currentNodeName, otherNodes)){
                            Graphs.addEdgeWithVertices(mainGraph, 
                                currentNodeName, otherNodes, 1);
                        }
                    }                    
                }
                
                //To get out of do while loop
                break;
            }
        } while (inputSuccess == false);
        
        //Print intial settings
        System.out.printf("\nIntial Settings: %s",intialString);
        System.out.printf("\nNum of Rows: %d\nNum of Nodes: %d",numOfRows,numOfLights);
        System.out.printf("\nWire configuration: %s\n", lightConfig);
        
        //Formating and printing board
        String formatedIntialString = properFormating(intialString);
        printBoard(formatedIntialString);
                
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Node and Edge Makers
    
    //Node name makers
    //This creates the node name based in the binary
    public ArrayList<String> createAllNodes(int lightsTotal){
        //Make new ArrayList String
        ArrayList<String> list = new ArrayList<String>();
        
        //Make each node name and add to list
        for (int i = 0; i < 1 << lightsTotal; i++) {
            String boardString = String.format("%d", i);
            list.add(boardString);
        }
        
        //Return List
        return list;
    }
         
    ////////////////////////////////////////////////////////////////////////////
    //Board object related methods
    
    //Search for names
    public Board searchBoard(String name)
    {
	return AllBoardNodes.get(name);
    }
    
    //Print board
    public void printBoard(String name){
        Board temp = searchBoard(name);
        temp.print();
    }
    
    //Solving the board
    public void solve(){
        //Set the final board
        String finalBoard = "0";
        try
            {
            ShortestPathAlgorithm<String, DefaultWeightedEdge> shpath = 
                    new DijkstraShortestPath<>(mainGraph);
            
            // getPath throws exception if negative edge exists
            GraphPath<String, DefaultWeightedEdge> gpath = 
                    shpath.getPath(properFormating(intialString), finalBoard);
            if (gpath != null){     
                printGraphPath(gpath);
            } else{
                System.out.printf("\nThere is no solution");
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("ERROR");
        }	
    }

    //Copied from Examples
    public void printGraphPath(GraphPath<String, DefaultWeightedEdge> gpath){
        //Print total moves
        System.out.printf("\n\n%d moves to turn off all lights\n", gpath.getLength());
        
        //Get all nodes in shortest path
        List<String> allNodes = gpath.getVertexList(); 

        //Loop
        for (int i = 1; i < allNodes.size(); i++){
            //Get previous board and current board
            Board previousBoard = searchBoard(allNodes.get(i-1));
            Board currentBoard = searchBoard(allNodes.get(i));
            
            //Get node that changed
            String node = currentBoard.checkLightX(previousBoard.getAllStatus(),
                    lightConfig);
            String temp = String.valueOf(node.charAt(0)) + String.valueOf(node.charAt(1));
            int x = Integer.parseInt(temp);
            temp = String.valueOf(node.charAt(2)) + String.valueOf(node.charAt(3));
            int y = Integer.parseInt(temp);
            
            //Get previous node's status
            boolean from = previousBoard.getStatus(x, y);
            
            //Print current move
            System.out.printf("\n\n\n>>> Move %2d: ", i);
            
            //Print out the different status change
            if (from){
                System.out.printf("turn off row %2d, col %2d", x, y);
            } else{
                System.out.printf("turn on row %2d, col %2d", x, y);
            }
            
            //Print Board
            printBoard(allNodes.get(i));
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Misc Methods
    
    //Return the number of Rows
    public int getRows(){
        return numOfRows;
    }
    
    //Proper formating (for intial board set by user)
    public String properFormating(String name){
        //Convert String into int
        int temp1 = Integer.parseInt(name, 2);
        
        String temp2 = String.format("%d", temp1);
        
        //Return
        return temp2;
    }
}

////////////////////////////////////////////////////////////////////////////////

public class Project2V3 {
    public static void main(String[] args) {
        //Run the Startup
        System.out.printf("Welcome to the Boards Out puzzle solver\n");
        System.out.printf("Please enter the correct information below\n\n");
        graphSetup myGraph = new graphSetup();
        myGraph.solve();
    }    
}
