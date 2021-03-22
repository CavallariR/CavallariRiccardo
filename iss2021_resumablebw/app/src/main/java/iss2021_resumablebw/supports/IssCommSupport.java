/**
 IssCommSupport.java
 ==========================================================================

 ==========================================================================
 */
package iss2021_resumablebw.supports;

import iss2021_resumablebw.interaction.IssObserver;
import iss2021_resumablebw.interaction.IssOperations;

public interface IssCommSupport extends IssOperations {
    void registerObserver( IssObserver obs );
    void removeObserver( IssObserver obs );
    void close();
}
