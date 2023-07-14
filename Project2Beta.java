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

////////////////////////////////////////////////////////////////////////////////

//Light Class
class Light{
    //Setup
    private boolean status;
    private String name;
    
    //Constructor
    public Light(String n, boolean b){
        name = n;
        status = b;
    }
    
    ////////////////////////////////////////////////////////////////////////////  
    //Important functions
    
    //Get commands
    public boolean getStatus() {return status;}
    public String getName() {return name;}
    
    
    //Toggle the light
    public void toggle(){
        if (status == false){
            status = true;
        } else{
            status = false;
        }
    }
}

////////////////////////////////////////////////////////////////////////////////

//Graph Setup Class
class graphSetup{
    //Setup Graphs
    protected HashMap<String, Light>          AllLightNodes; 
    protected ArrayList<String>               LightNames; 
    protected Graph<String, DefaultEdge>      mainGraph; 
    
    //Setup Variables
    int numOfRows;
    int numOfNodes;
    char[] intialState;
    boolean inputSuccess = false;
    String lightConfig;   
    boolean[] currentLightStatus; //For printing and to check if all off
    
    ////////////////////////////////////////////////////////////////////////////    
    //Constructor
    public graphSetup(){
        //Get number of rows
        Scanner scan = new Scanner(System.in);
        do {
            System.out.printf("Enter number of rows (>1): "); 
            String temp = scan.next();
            numOfRows = Integer.parseInt(temp);
            if (numOfRows > 1) {inputSuccess = true;}  
        } while (inputSuccess == false);
        numOfNodes = numOfRows * numOfRows;
        currentLightStatus = new boolean[numOfNodes];
                
        //Ask user for which lights are already on
        String intialString = "";
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
        intialState = intialString.toCharArray(); 
        
        //Create the HashMap
        AllLightNodes = new HashMap<String, Light>();
        
        //Create list of light node names
        LightNames =  new ArrayList<String>();
        
        //Add each light into the graph based on the amount of rows
        for (int i = 0; i < numOfNodes; i++){
            //Get the intial state of the light
            String tempNumber = "" + intialState[i];
            
            //If the intial state of light is 0 or false, then set as false
            if (tempNumber.compareTo("0") == 0){
                AllLightNodes.put(Integer.toString(i), 
                        new Light(Integer.toString(i), false));
                
                //Also add the name to the list
                LightNames.add(Integer.toString(i));
                
                //Set current light status to false
                currentLightStatus[i] = false;
                
            } 
            //If the intial state of light is 1 or true, then set as true
            else{
                AllLightNodes.put(Integer.toString(i), 
                        new Light(Integer.toString(i), true));
                
                //Also add the name to the list
                LightNames.add(Integer.toString(i));
                
                //Set current light status to true
                currentLightStatus[i] = true;
            }            
        }
        
        //Create a simple unweighted but directed graph
        //We need directed graph for the cross (x) config because we need to
        //know which nodes to go to which node, while plus (+) doesn't
        mainGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(mainGraph, LightNames);
        
        //Ask user for config
        do {
            System.out.printf("Enter wire configuration (+ or x): "); 
            lightConfig = scan.next();
            if (lightConfig.equals("+")){
                //Set config to + (See Below)
                setConfigToPlus();
                break;
            } else if(lightConfig.equals("x")){
                //Set config to x (See Below)
                setConfigToCross();
                break;
            }
        } while (inputSuccess == false);
        
        //Print intial settings
        System.out.printf("\nIntial Settings:");
        System.out.printf("\nNum of Rows: %d\nNum of Nodes: %d",numOfRows,numOfNodes);
        System.out.printf("\nWire configuration: %s\n", lightConfig);
    }
    
    ////////////////////////////////////////////////////////////////////////////    
    //Set the graph to different configs
    
    //Plus config (+)
    public void setConfigToPlus(){
        //Main loop to set edges
        for (int i = 0; i < numOfNodes; i++){
            //Make an edge from current node to LEFT node if exist
            if (i % numOfRows != 0){
                mainGraph.addEdge(Integer.toString(i), Integer.toString(i-1));
            }
            
            //Make an edge from current node to RIGHT node if exist
            if ((i + 1) % numOfRows != 0){
                if ((i + 1) != numOfNodes){
                    mainGraph.addEdge(Integer.toString(i), Integer.toString(i+1));
                }
            }
            
            //Make an edge from current node to TOP node if exist
            if (i - numOfRows > -1){
                mainGraph.addEdge(Integer.toString(i), Integer.toString(i-numOfRows));
            }
            
            //Make an edge from current node to BOTTOM node if exist
            if (i + numOfRows < numOfNodes){
                mainGraph.addEdge(Integer.toString(i), Integer.toString(i+numOfRows));
            }           
        }
    }
    
    //Cross config (x)
    public void setConfigToCross(){
        //Setup
        int tempNumber;
        
        //Main loop to set edges
        for (int i = 0; i < numOfNodes; i++){
            System.out.printf("%d ", i);
            //Check if TOP nodes exist
            if (i - numOfRows > -1){
                tempNumber = i - numOfRows;
                
                //Check if on LEFT MOST NODE
                if (tempNumber % numOfRows != 0){
                    mainGraph.addEdge(Integer.toString(i), Integer.toString(tempNumber - 1));
                }
                
                //Check if on RIGHT MOST Node
                if ((i + 1) % numOfRows != 0){
                    if ((tempNumber + 1) != numOfNodes){
                        mainGraph.addEdge(Integer.toString(i), Integer.toString(tempNumber + 1));
                    }
                }     
            }
            
            //Check if BOTTOM nodes exist
            if (i + numOfRows < numOfNodes){
                tempNumber = i + numOfRows;
                
                //Check if on LEFT MOST Node
                if (tempNumber % numOfRows != 0){
                    mainGraph.addEdge(Integer.toString(i), Integer.toString(tempNumber - 1));
                }
                
                //Check if on RIGHT MOST Node
                if ((i + 1) % numOfRows != 0){
                    if ((tempNumber + 1) != numOfNodes){
                        mainGraph.addEdge(Integer.toString(i), Integer.toString(tempNumber + 1));
                    }
                }                    
            }          
        }        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Light object related methods
    
    //Search for names
    public Light searchLight(String name)
    {
	return AllLightNodes.get(name);
    }
    
    //Toggle the lights
    public void toggleLightNode(int nodeNumber){
        //Set current node and toggle it
        Light currentLightNode = searchLight(Integer.toString(nodeNumber));
        currentLightNode.toggle();
        
        //Toggle the currentLightStatus
        if (currentLightStatus[nodeNumber]){
            currentLightStatus[nodeNumber] = false;
        } else{
            currentLightStatus[nodeNumber] = true;
        }
        
        //Setup before entering loop
        Light tempLightNode;
        String currentNode = Integer.toString(nodeNumber);
        String tempNode;
        
        //Toggle all nodes that have an edge from current node to connected node
        for (int i = 0; i< numOfNodes; i++){
            tempNode = Integer.toString(i);
            if (mainGraph.containsEdge(currentNode,tempNode)){
                tempLightNode = searchLight(tempNode);
                tempLightNode.toggle();
                
                //Toggle the currentLightStatus
                if (currentLightStatus[i]){
                    currentLightStatus[i] = false;
                } else{
                    currentLightStatus[i] = true;
                }
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Printing related methods
    
    //Print graph
    public void printGraph(){
        //Print the current state in bits for user to copy and paste
        System.out.printf("\nCurrent state in bits: ");
        Light tempLightNode;        
        
        for (int i = 0; i < numOfNodes; i++){
            tempLightNode = searchLight(Integer.toString(i));
            if (tempLightNode.getStatus()){
                System.out.printf("1");
                currentLightStatus[i] = true;
            } else{
                System.out.printf("0");
                currentLightStatus[i] = false;
            }
        }
        System.out.printf("\n");
        
        //Print the columns bar
        System.out.printf("       |");
        for (int i = 0; i < numOfRows; i++){
            System.out.printf(" col %2d |", i);
        }
        
        //Print the row number of the data
        for (int i = 0; i < numOfNodes; i++){
            //If i is on the new row
            if (i % numOfRows == 0){
                //New line and print row number
                System.out.printf("\n");
                System.out.printf("row %2d |", i / numOfRows);
            }
            
            //Print data
            if (currentLightStatus[i]){
                System.out.printf("    On    |");
            } else{
                System.out.printf("    Off   |");
            }
        }                       
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Misc Methods
    
    //Check if all lights are off/program is completed
    public boolean isCompleted(){
        //Setup
        boolean isDone = true;
        
        //Loop to check each char
        for (int i = 0; i < numOfNodes; i++){
            if (currentLightStatus[i]){
                isDone = false;
            }
        }
        
        //Return
        return isDone;
    }
    
    //Solve the puzzle
    public void solve(){
        //create queue to add and poll flagged vertices
        Queue<Light> q = new LinkedList<>();
        boolean visited[] = new boolean[numOfNodes];
        for(int i = 0; i < numOfNodes; i++){
            Light tempLight = searchLight(Integer.toString(i));
            //this will flag all vertices with light status "on" and add it into queue
            if (tempLight.getStatus() == true){
                q.add(tempLight);
                visited[i] = true;
            }
        }
        while(!q.isEmpty()){
            //retrieve the node(vertice) from the queue
            Light tempLight = q.poll();
            //toggle (Turn off) the one light
            toggleLightNode(Integer.parseInt(tempLight.getName()));
            //print the move
            printGraph();
            for(int i = 0; i < numOfNodes; i++){
                String tempNode = Integer.toString(i);
                if (mainGraph.containsEdge(tempLight.getName(),tempNode)){
                    Light connectedVertice = searchLight(tempNode);
                    if(connectedVertice.getStatus() == true && !visited[i]){
                        q.add(connectedVertice);
                        visited[i] = true;
                    }
                    
                }
            }
        }
        //PROBLEM: IT FALL INTO INFINITE LOOP
        /*if(!isCompleted()){
            solve();
        }*/
    }
}

////////////////////////////////////////////////////////////////////////////////

public class Project2 {
    public static void main(String[] args) {
        //Run the Startup
        System.out.printf("Welcome to the Lights Out puzzle solver\n");
        System.out.printf("Please enter the correct information below\n\n");
        graphSetup myGraph = new graphSetup();
        myGraph.printGraph();
        
        myGraph.solve();
        /*while(!myGraph.isCompleted()){
            myGraph.solve();
        }*/
        System.out.printf("\nEnd?");
        //TESTING
        /*do{
        Scanner scan = new Scanner(System.in);
        System.out.printf("\n\nEnter node to toggle: ");
        String num = scan.next();
        int hi = Integer.parseInt(num);
        
        myGraph.toggleLightNode(hi);
        myGraph.printGraph();
        //System.out.printf("\n%b",myGraph.isCompleted());
        } while (true);*/
    }    
}