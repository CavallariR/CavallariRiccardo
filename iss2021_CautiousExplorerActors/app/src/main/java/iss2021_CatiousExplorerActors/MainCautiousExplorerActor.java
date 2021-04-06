package iss2021_CatiousExplorerActors;
import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;

public class MainCautiousExplorerActor {
    //Constructor
    public MainCautiousExplorerActor(){
        IssWsHttpJavaSupport support = IssWsHttpJavaSupport.createForWs("localhost:8091" );

        //while( ! support.isOpen() ) ActorBasicJava.delay(100);

        CautiousExplorerActor ra = new CautiousExplorerActor("cea", support);
        support.registerActor(ra);

        ra.send("startApp");

        System.out.println("MainCautiousExplorerActor | CREATED  n_Threads=" + Thread.activeCount());
    }


    public static void main(String args[]){
        try {
            System.out.println("MainCautiousExplorerActor | main start n_Threads=" + Thread.activeCount());
            new MainCautiousExplorerActor();
            //System.out.println("MainCautiousExplorerActor  | appl n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
