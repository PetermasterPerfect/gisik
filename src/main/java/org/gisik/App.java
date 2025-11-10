package org.gisik;
import javax.swing.*;

public class App  extends JFrame {

    private JMenu createFileMenu() {
        JMenu menu;
        JMenuItem openItem, saveItem;
        menu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        menu.add(openItem);
        menu.add(saveItem);
        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu;
        menu = new JMenu("Edit");

        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu;
        menu = new JMenu("View");

        return menu;
    }

    private JMenu createLayerMenu() {
        JMenu menu;
        JMenuItem addLayerItem = new JMenuItem("Add layer");
        menu = new JMenu("Layer");
        menu.add(addLayerItem);
        return menu;
    }

    private void createMenu() {
        JMenuBar menuBar;
        JMenu fileMenu, editMenu, viewMenu, layerMenu;
        JMenuItem save, saveItem;
        JCheckBoxMenuItem cbMenuItem;

        menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createLayerMenu());

        this.setJMenuBar(menuBar);
    }

    public App() {
        createMenu();
        setTitle("gisik");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new App();
    }
}