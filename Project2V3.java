/*
1. Jawit Poopradit 6480087
2. Possathorn Sujipisut 6480274
3. Piyakorn Rodthanong 6480569
4. Phasin Ploypicha 6480567
*/

//Package
package Project2;

//Basic Imports
import java.io.*;
import java.util.*;

//Jgraph Imports
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.color.*;
import org.jgrapht.alg.spanning.*;

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
    public boolean[][] getStatus() {return status;}
    public String getName() {return name;}
       
    //Toggle a certain light
    public boolean[][] toggle(int x, int y, boolean[][] currentBoard){
        //Set new Board
        boolean[][] newBoard = currentBoard;
        
        //If node is outside range of graph then just return the board
        if (x < 0 || y < 0 || x >= currentBoard.length || y >= currentBoard[x].length) {
            return newBoard;
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
    
    //////////////////////////////////////////////////////////////////////////// 
    //Get connecting board names in different configs
    
    //Return string name of connecting board in plus (+) config
    public ArrayList<String> getConnectingBoardNamesPlus(){
        //Create the list to return an create dummy board
        ArrayList<String> list = new ArrayList<String>();        
        boolean[][] dummy;
        
        //Get all boards connected to current board
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                //Current node
                dummy = toggle(x,y,status);
                
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
        boolean[][] dummy;
        
        //Get all boards connected to current board
        for (int x = 0; x < sizeOfRow; x++) {
            for (int y = 0; y < sizeOfRow; y++) {
                //Current node
                dummy = toggle(x,y,status);
                
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
        String temp = String.format("%025d", Integer.parseInt(Integer.toBinaryString(Integer.parseInt(name))));
        System.out.printf("\nCurrent state in bits: %s\n",
                temp.substring(25 - (sizeOfRow * sizeOfRow)));
        
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
        
        for (int x = 0; x < sizeOfRow; x++){
            //New line and print row number
            System.out.printf("\n");
            System.out.printf("row %2d |", x);
            
            //For each entry, check if true or false and then print that
            for (int y = 0; y < sizeOfRow; y++){
                //Print data
                if (status[x][y]){
                    System.out.printf("  true |");
                } else{
                    System.out.printf("  false|");
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
                        if (mainGraph.containsEdge(currentNodeName, otherNodes)){
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
                        if (mainGraph.containsEdge(currentNodeName, otherNodes)){
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
    
    public void printBoard(String name){
        Board temp = searchBoard(name);
        temp.print();
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
        
        myGraph.printBoard("15");
    }    
}