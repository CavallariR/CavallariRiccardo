package iss2021_CatiousExplorerActors;


import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;
import java.util.ArrayList;

public class CautiousExplorerActor extends ActorBasicJava {
    final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 350}";
    final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 350}";
    final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";


    private enum State {start, walking, obstacle, end };
    private IssWsHttpJavaSupport support;
    private int stepNum          = 0;
    private int turnNum          = 0;
    private int maxStep          = 1;
    private int movesNum         = 0;
    private RobotMovesInfo moves = new RobotMovesInfo(true);
    private ArrayList<String> explorationMoves = new ArrayList<String>();

    //private boolean tripStopped  = true;
    private boolean explorationTerminated = false;

    public CautiousExplorerActor(String name, IssWsHttpJavaSupport support) {
        super(name);
        this.support = support;
        //explore();
    }

    protected void explore(){
        doStep();
    }

    protected void explorationPattern(String move, String endmove) {
        if (move.equals("moveForward") && endmove.equals("true")) { //passo avanti con successo
            stepNum++;

            moves.updateMovesRep("w");
            explorationMoves.add("w");; //soluzione temporanea per il viaggio di ritorno
            moves.showRobotMovesRepresentation();

            if(stepNum < maxStep) {
                doStep();
            }
            else {
                stepNum = 0;
                turnLeft();
            }

        }
        else if(move.equals("turnLeft") && endmove.equals("true")){ //svolta a sinistra

            moves.updateMovesRep("l");
            explorationMoves.add("l"); //soluzione temporanea per il viaggio di ritorno
            moves.showRobotMovesRepresentation();
            turnNum++;
            if (turnNum < 4)
                doStep();
            else {
                explorationMoves.clear();
                maxStep++;
                turnNum = 0;
                stepNum = 0;
                doStep();
            }

        }

        else if(move.equals("moveForward") && endmove.equals("false")){
            explorationTerminated = true;
            movesNum = explorationMoves.size();
            moves.showRobotMovesRepresentation();
            System.out.println("CautiousExplorerActor | Obstacle found - Starting return");
            returnPath("","true");
        }
    }

    protected void returnPath(String move, String endmove){ //ritorno al contrario
        if (endmove.equals("true") && movesNum > 0) { //passo indietro con successo
            movesNum--;
            if(explorationMoves.get(movesNum).equals("w")){
                doBackStep();
            }
            else if(explorationMoves.get(movesNum).equals("l")){
                turnRight();
            }
            else if(explorationMoves.get(movesNum).equals("r")){
                turnLeft();
            }
        }
        else if(endmove.equals("false") || movesNum == 0){ //svolta a sinistra
            System.out.println("CautiousExplorerActor | Return completed");
            support.close();
        }
    }


    /*
    ==================================================================================
    INPUT HANDLING
    ==================================================================================
    */
    @Override
    protected void handleInput(String msg ) {     //called when a msg is in the queue
        System.out.println(msg);
        if( msg.equals("startApp"))  explore();
        else
            msgDriven( new JSONObject(msg) );
    }

    protected void msgDriven( JSONObject infoJson){
        if( infoJson.has("endmove") && !explorationTerminated)        explorationPattern(infoJson.getString("move"), infoJson.getString("endmove"));
        else if(infoJson.has("endmove") && explorationTerminated)     returnPath(infoJson.getString("move"), infoJson.getString("endmove"));
        else if( infoJson.has("sonarName") )                          handleSonar(infoJson);
        else if( infoJson.has("collision") )                          handleCollision(infoJson);
        //else if( infoJson.has("robotcmd") )  handleRobotCmd(infoJson);
    }

    protected void handleSonar( JSONObject sonarinfo ){
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        System.out.println("RobotApplication | handleSonar:" + sonarname + " distance=" + distance);
        /*try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    protected void handleCollision( JSONObject collisioninfo ){
        String move   = (String) collisioninfo.get("move");
        System.out.println("RobotApplication | handleCollision move=" + move  );
    }

    protected void handleRobotCmd( JSONObject robotCmd ){
        //no GUI in this app
    }

    //------------------------------------------------
    protected void doStep(){
        support.forward( forwardMsg);
        delay(1000); //to avoid too-rapid movement
    }
    protected void doBackStep(){
        support.forward(backwardMsg);
        delay(1000); //to avoid too-rapid movement
    }
    protected void turnLeft(){
        support.forward( turnLeftMsg );
        delay(500); //to avoid too-rapid movement
    }
    protected void turnRight(){
        support.forward( turnRightMsg );
        delay(500); //to avoid too-rapid movement
    }

}
