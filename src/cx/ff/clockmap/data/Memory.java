package cx.ff.clockmap.data;

import java.util.HashMap;
import java.util.Map;

public class Memory {

    private HashMap<String, Clock> clocks = new HashMap<String, Clock>();

    public HashMap<String, Clock> getClocks() {
        return clocks;
    }

    public Memory(HashMap<String, Clock> clocks) {
        this.clocks = clocks;

    }

    public void printData() {

        for (Map.Entry<String, Clock> entry : clocks.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().values[0] + "|"
                    + entry.getValue().values[1] + "|"
                    + entry.getValue().values[2] + "|"
                    + entry.getValue().values[3] + "|"
                    + entry.getValue().values[4] + "|"
                    + entry.getValue().values[5] + "|"
                    + entry.getValue().values[6] + "|"
                    + entry.getValue().values[7] + "|"
                    + entry.getValue().values[8] + "|...");

        }

    }
}
