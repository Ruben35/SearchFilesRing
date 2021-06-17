package utils;

import objects.NodeInformation;

import java.util.Comparator;

public class portSorter implements Comparator<NodeInformation> {
    @Override
    public int compare(NodeInformation o1, NodeInformation o2) {
        Integer oInt1 = new Integer(o1.getMyPort());
        Integer oInt2 = new Integer(o2.getMyPort());
        return oInt1.compareTo(oInt2);
    }
}
