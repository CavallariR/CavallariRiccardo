package iss2021_resumablebw.wenv;

//import javafx.application.Application;
//import javafx.stage.Stage;
import iss2021_resumablebw.annotations.ArilRobotSpec;
import iss2021_resumablebw.consolegui.ConsoleGui;
import iss2021_resumablebw.interaction.IssOperations;
import iss2021_resumablebw.supports.IssCommSupport;
import iss2021_resumablebw.supports.RobotApplicationStarter;

@ArilRobotSpec
public class ClientResumableBoundaryWalker{
    private RobotInputController controller;
    private ConsoleGui GUI;

    //Constructor
    public ClientResumableBoundaryWalker(IssOperations rs){
        IssCommSupport rsComm = (IssCommSupport)rs;
        controller = new RobotInputController(rsComm, true, true );
        rsComm.registerObserver( controller );
        System.out.println("ClientResumableBoundaryWalker | CREATED with rsComm=" + rsComm);
    }

    public String openGUI(){
        GUI = new ConsoleGui(controller);
        return controller.waitBoundary();
    }

    public static void main(String[] args) {
        try {
            System.out.println("ClientResumableBoundaryWalker | main start n_Threads=" + Thread.activeCount());
            Object appl = RobotApplicationStarter.createInstance(ClientResumableBoundaryWalker.class);
            System.out.println("ClientResumableBoundaryWalker  | appl n_Threads=" + Thread.activeCount());
            String trip = ((ClientResumableBoundaryWalker)appl).openGUI();
            System.out.println("ClientResumableBoundaryWalker | trip="   );
            System.out.println( trip  );
            System.out.println("ClientResumableBoundaryWalker | main end n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
