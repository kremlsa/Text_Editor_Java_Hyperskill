package editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    JTextField searchField = new JTextField(15);
    JTextArea text = new JTextArea(10, 50);
    JScrollPane scroll;
    JMenuBar mb = new JMenuBar();
    JMenu menu = new JMenu("File");
    JMenuItem m1 = new JMenuItem("Open");
    JMenuItem m2 = new JMenuItem("Save");
    JMenuItem m3 = new JMenuItem("Exit");
    JMenu menu2 = new JMenu("Search");
    JMenuItem m21 = new JMenuItem("Start search");
    JMenuItem m22 = new JMenuItem("Previous search");
    JMenuItem m23 = new JMenuItem("Next match");
    JMenuItem m24 = new JMenuItem("Use regular expressions");
    JPanel contents = new JPanel();
    JPanel menus = new JPanel();
    JButton saveButton;
    JButton loadButton;
    JButton searchButton;
    JButton prevButton;
    JButton nextButton;
    JCheckBox patBox = new JCheckBox("Use regex", false);
    Map<Integer, Integer[]> map;
    int currId;
    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());


    public TextEditor() {
        super("editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        this.add(jfc);
        placeElements();
        setVisible(true);
    }

    public void placeElements() {
        jfc.setName("FileChooser");
        contents.setLayout(new FlowLayout(FlowLayout.LEFT));
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contents, BorderLayout.CENTER);

        patBox.setSize(80, 25);
        patBox.setName("UseRegExCheckbox");

        menu2.setName("MenuSearch");
        m21.setName("MenuStartSearch");
        m21.addActionListener(new SearchListener());
        m22.setName("MenuPreviousMatch");
        m22.addActionListener(new PrevListener());
        m23.setName("MenuNextMatch");
        m23.addActionListener(new NextListener());
        m24.setName("MenuUseRegExp");
        m24.addActionListener(new RegListener());
        menu2.add(m21);
        menu2.add(m22);
        menu2.add(m23);
        menu2.add(m24);

     /*   menu.setName("MenuFile");
        m1.setName("MenuOpen");
        m1.addActionListener(new LoadListener());
        m2.setName("MenuSave");
        m2.addActionListener(new SaveListener());
        m3.setName("MenuExit");
        m3.addActionListener(new ExitListener());
        menu.add(m1);
        menu.add(m2);
        menu.add(m3);

        mb.add(menu);
        mb.add(menu2);*/


        Icon iconS = new ImageIcon("resources/save.png");
        Icon iconL = new ImageIcon("resources/open.png");
        Icon iconSe = new ImageIcon("resources/search.png");
        Icon iconP = new ImageIcon("resources/prev.png");
        Icon iconN = new ImageIcon("resources/next.png");
        saveButton = new JButton(iconS);
        loadButton = new JButton(iconL);
        searchButton = new JButton(iconSe);
        prevButton = new JButton(iconP);
        nextButton = new JButton(iconN);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(new SaveListener());
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(new SearchListener());
        nextButton.setName("NextMatchButton");
        nextButton.addActionListener(new NextListener());
        prevButton.setName("PreviousMatchButton");
        prevButton.addActionListener(new PrevListener());
        loadButton.setName("OpenButton");
        loadButton.addActionListener(new LoadListener());
        text.setName("TextArea");
        searchField.setName("SearchField");
        contents.add(loadButton);
        contents.add(saveButton);
        contents.add(searchField);
        contents.add(searchButton);
        contents.add(prevButton);
        contents.add(nextButton);
        contents.add(patBox);
        scroll = new JScrollPane(text);
        scroll.setName("ScrollPane");
        //this.add(jfc, BorderLayout.CENTER);
        contents.add(scroll);

        JPanel menus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(menus, BorderLayout.NORTH);
        menu.setName("MenuFile");
        m1.setName("MenuOpen");
        m1.addActionListener(new LoadListener());
        m2.setName("MenuSave");
        m2.addActionListener(new SaveListener());
        m3.setName("MenuExit");
        m3.addActionListener(new ExitListener());
        menu.add(m1);
        menu.add(m2);
        menu.addSeparator();
        menu.add(m3);
        //mb.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        mb.add(menu);
        mb.add(menu2);
        menus.add(mb);
    }



    public void saveBody() {

        //int returnValue = jfc.showOpenDialog(null);
        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            String body = text.getText();
            Utils.save(fileName, body);
        }
    }

    public void searchBody() {
        map = new LinkedHashMap<>();
        String body = text.getText();
        Matcher searchMatcher;
        if (!patBox.isSelected()) {
            String regex = Pattern.quote(searchField.getText());
            searchMatcher = Pattern.compile(regex).matcher(body);
        } else {
            searchMatcher = Pattern.compile(searchField.getText()).matcher(body);
        }
        int count = 0;
        while (searchMatcher.find()) {
            Integer[] range = new Integer[2];
            range[0] = searchMatcher.start();
            range[1] = searchMatcher.end();
            map.put(count, range);
            count++;
        }
        if (map.size() > 0) {
            currId = 0;
            int startPos = map.get(0)[0];
            int endPos = map.get(0)[1];
            text.setCaretPosition(endPos);
            text.select(startPos, endPos);
            text.grabFocus();
        }
    }

    public void selectNext() {
        if (map.containsKey(currId + 1)) {
            currId = currId + 1;
        } else {
            currId = 0;
        }
        int startPos = map.get(currId)[0];
        int endPos = map.get(currId)[1];
        text.setCaretPosition(endPos);
        text.select(startPos, endPos);
        text.grabFocus();
        System.out.println(currId);
    }

    public void selectPrev() {
        if (map.containsKey(currId - 1)) {
            currId = currId - 1;
        } else {
            for (Map.Entry<Integer, Integer[]> entry : map.entrySet()) {
                currId = entry.getKey();
            }
        }
      /*  if (currId == 0) {
            for (Map.Entry<Integer, Integer[]> entry : map.entrySet()) {
                currId = entry.getKey();
            }
        }*/
        int startPos = map.get(currId)[0];
        int endPos = map.get(currId)[1];
        text.setCaretPosition(endPos);
        text.select(startPos, endPos);
        text.grabFocus();
    }

    public void selectReg() {
        if (patBox.isSelected()) {
            patBox.setSelected(false);
        } else {
            patBox.setSelected(true);
        }
    }


    public void loadBody() {

        int returnValue = jfc.showOpenDialog(null);
        //int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            String body = Utils.load(fileName);
            text.setText(body);
        }
    }

    public void closeProg() {
        this.dispose();
    }

    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            saveBody();
        }
    }

    class LoadListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            loadBody();
        }
    }

    class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            closeProg();
        }
    }

    class SearchListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            searchBody();
        }
    }

    class NextListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            selectNext();
        }
    }

    class PrevListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            selectPrev();
        }
    }

    class RegListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            selectReg();
        }
    }

}