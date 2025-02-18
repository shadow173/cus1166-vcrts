package vcrts;

import vcrts.gui.MainFrame;

public class App {
    public static void main(String[] args) {
        System.out.println("Launching VCRTS GUI Application...");
        // display the GUI frame
        new MainFrame().setVisible(true);
    }
}
