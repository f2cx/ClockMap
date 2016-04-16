package cx.ff.clockmap.vis;

import java.util.Comparator;

public class TreeNodeComparator implements Comparator<TreeNode> {

    @Override
    public int compare(TreeNode o1, TreeNode o2) {
        if (o1.getRadius() > o2.getRadius()) {
            return -1;
        } else {
            if (o1.getRadius() == o2.getRadius()) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
