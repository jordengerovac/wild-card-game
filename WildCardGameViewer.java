import javax.swing.*;

public class WildCardGameViewer {
    public WildCardGameViewer() throws Exception {
        JFrame frame = new WildCardGameFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Wild Card Game");
        frame.setSize(700, 550);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new WildCardGameViewer();
    }
}
