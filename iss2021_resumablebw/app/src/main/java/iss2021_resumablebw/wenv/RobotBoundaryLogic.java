/*
===============================================================
RobotBoundaryLogic.java
implements the business logic  

===============================================================
*/
package iss2021_resumablebw.wenv;
import iss2021_resumablebw.interaction.MsgRobotUtil;
import iss2021_resumablebw.supports.IssCommSupport;
//import mapRoomKotlin.mapUtil;

public class RobotBoundaryLogic {
    private IssCommSupport rs ;

private int stepNum              = 1;
private boolean boundaryWalkDone = false ;
//private boolean boundaryWalkStarted = false;
private boolean usearil          = false;
private int moveInterval         = 1000;
private iss2021_resumablebw.wenv.RobotMovesInfo robotInfo;
private String trip = " ";
    //public enum robotLang {cril, aril}    //todo

    public RobotBoundaryLogic(IssCommSupport support, boolean usearil, boolean doMap){
        rs           = support;
        this.usearil = usearil;
        robotInfo    = new iss2021_resumablebw.wenv.RobotMovesInfo(doMap);
        robotInfo.showRobotMovesRepresentation();
    }

    public synchronized String waitToStartBoundary(){
        while( ! boundaryWalkDone ) {
            try {
                System.out.println("RobotBoundaryLogic | START ");
                wait();
                System.out.println("RobotBoundaryLogic | RESUMES " );
                rs.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return robotInfo.getMovesRepresentationAndClean();
    }

    public void doBoundaryGoon(){
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        delay(moveInterval ); //to reduce the robot move rate
    }

    public synchronized String doBoundaryInit(){
        System.out.println("RobotBoundaryLogic | doBoundary rs=" + rs + " usearil=" + usearil);
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        //The reply to the request is sent by WEnv after the wtime defined in issRobotConfig.txt  
        //delay(moveInterval ); //to reduce the robot move rate
        //System.out.println( mapUtil.getMapRep() );
        while( ! boundaryWalkDone ) {
            try {
                wait();
                System.out.println("RobotBoundaryLogic | RESUMES " );
                rs.close();
             } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return robotInfo.getMovesRepresentationAndClean();
    }

    public void stopBoundary(){
        rs.request( usearil ? MsgRobotUtil.hMsg : MsgRobotUtil.haltMsg  );
    }

    public void updateMovesRep (String move ){
        robotInfo.updateRobotMovesRepresentation(move);
    }

    //Business logic in RobotBoundaryLogic
    protected synchronized void boundaryStep( String move, boolean obstacle ){
         if (stepNum <= 4) {
            if( move.equals("turnLeft") ){
                updateMovesRep("l");
                //showRobotMovesRepresentation();
                if (stepNum == 4) {
                    boundaryWalkDone=true;
                    notify(); //to resume the main
                    return;
                }
                stepNum++;
                doBoundaryGoon();
                return;
            }
            //the move is moveForward
            if( obstacle ){
                rs.request( usearil ? MsgRobotUtil.lMsg : MsgRobotUtil.turnLeftMsg   );
                delay(moveInterval ); //to reduce the robot move rate
            }
            if( ! obstacle ){
                updateMovesRep("w");
                doBoundaryGoon();
            }
            robotInfo.showRobotMovesRepresentation();
        }else{ //stepNum > 4
            System.out.println("RobotBoundaryLogic | boundary ENDS"  );
        }
    }



    protected void delay( int dt ){
        try { Thread.sleep(dt); } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
