package utils;

import objects.NodeInformation;

import java.util.Comparator;

public class RMIportSorter implements Comparator<NodeInformation> {
    @Override
    public int compare(NodeInformation o1, NodeInformation o2) {
        Integer oInt1 = new Integer(o1.getRMIport());
        Integer oInt2 = new Integer(o2.getRMIport());
        return oInt1.compareTo(oInt2);
    }
}
