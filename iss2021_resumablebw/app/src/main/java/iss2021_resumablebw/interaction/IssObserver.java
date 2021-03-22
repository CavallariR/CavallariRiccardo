package iss2021_resumablebw.interaction;
import org.json.JSONObject;

public interface IssObserver {
    public void handleInfo(String info);
    public void handleInfo(JSONObject info);
}
