/*
===============================================================
RobotControllerMapper.java
implements the business logic by handling messages received on the cmdsocket-8091

===============================================================
*/
package iss2021_resumablebw.wenv;

import iss2021_resumablebw.interaction.IssObserver;
import iss2021_resumablebw.supports.IssCommSupport;
import org.json.JSONObject;

public class RobotInputController implements IssObserver {
private iss2021_resumablebw.wenv.RobotBoundaryLogic robotBehaviorLogic  ;
private IssCommSupport commSupport;  //IssArilRobotSupport
private boolean alreadyStarted = false;

    //public enum robotLang {cril, aril}    //todo

        public RobotInputController(IssCommSupport support, boolean usearil, boolean doMap){
        commSupport = support;
        robotBehaviorLogic = new iss2021_resumablebw.wenv.RobotBoundaryLogic(support, usearil, doMap);
     }

    //entry for the main program
    public String doBoundary(){
        alreadyStarted = true;
        return robotBehaviorLogic.doBoundaryInit();
    }

    public String waitBoundary(){
        return(robotBehaviorLogic.waitToStartBoundary());
    }

    @Override
    public void handleInfo(String infoJson) {
        handleInfo( new JSONObject(infoJson) );
    }

    public boolean getAlreadyStarted(){
            return alreadyStarted;
    }

    /*
    ENTRY
Hhandler of the messages sent by WENv over the cmdsocket-8091 to notify:
- the answer to a robot-command move {"endmove":"RESULT", "move":MOVE}
- the information emitted by a sonar { "sonarName": "sonarName", "distance": 1, "axis": "x" }
- a collision between the robot and an obstacle { "collision" : "false", "move": "moveForward"}
     */
    @Override
    public synchronized void  handleInfo(JSONObject infoJson) {
        System.out.println("RobotInputController | handleInfo:" + infoJson  );
        if( infoJson.has("endmove") )        handleEndMove(infoJson);
        else if( infoJson.has("sonarName") ) handleSonar(infoJson);
        else if( infoJson.has("collision") ) handleCollision(infoJson);
        else if( infoJson.has("robotcmd") ) handleButton(infoJson);
    }
    protected void handleButton( JSONObject buttoninfo ) {
        String btnCmd = (String) buttoninfo.getString("robotcmd");
        switch(btnCmd){
            case "RESUME"     : robotBehaviorLogic.doBoundaryGoon(); break;
            case "STOP" : robotBehaviorLogic.stopBoundary(); break;
            default           : System.out.println("RobotInputController | handleButton IMPOSSIBLE answer for buttoncmd=" + btnCmd);
        }
    }

    protected void handleSonar( JSONObject sonarinfo ){
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        //System.out.println("RobotInputController | handleSonar:" + sonarname + " distance=" + distance);
    }
    protected void handleCollision( JSONObject collisioninfo ){
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotInputController | handleCollision move=" + move  );
    }
    protected void handleEndMove(JSONObject endmove ){
        String answer = (String) endmove.get("endmove");
        String move   = (String) endmove.get("move");   //moveForward, ...
        System.out.println("RobotInputController | handleEndMove:" + move + " answer=" + answer);
        switch( answer ){
            case "true"       :  robotBehaviorLogic.boundaryStep( move, false );
                                  break;
            case "false"      : robotBehaviorLogic.boundaryStep( move, true  );break;
            case "halted"     : System.out.println("RobotInputController | handleEndMove to do halt" );break;
            case "notallowed" : System.out.println("RobotInputController | handleEndMove to do notallowed" );break;
            default           : System.out.println("RobotInputController | handleEndMove IMPOSSIBLE answer for move=" + move);
        }
    }

}
