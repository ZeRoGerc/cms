import ui.BaseDrawer;
import ui.ChaosDrawer;

public class Main {

    public static void main(String[] args) {
        BaseDrawer drawer = new ChaosDrawer();
        drawer.init();
        drawer.setTitle("Chaos");
        drawer.setVisible(true);
    }
}
