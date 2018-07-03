package com.snoopinou.StrToASCII;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class Fenetre extends JFrame {

	HashMap<Integer, String> map = new HashMap<Integer, String>();
	
	JPanel contentPane = new JPanel();
	JTextField jtf = new JTextField();
	JTextArea area = new JTextArea();
	JButton button = new JButton("OK");
	JButton buttonCopy = new JButton("Copy to Clipboard");
	JButton buttonMarkdown = new JButton("Copy for Markdown (Discord etc...)");
	
	Font font = new Font("Arial", Font.PLAIN, 16);
	
	public Fenetre() {
		
		this.setTitle("String to ASCII Art");
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		initMap();
		initComp();		
		
		this.setVisible(true);
	}
	
	private void initMap() {
		
		LinkedList<Integer> empty = new LinkedList<Integer>();
		
		for(int i = 0; i < 4096; i++) {
			URL url = this.getClass().getResource("/resources/"+i+".txt");
			try {
				BufferedInputStream input = new BufferedInputStream(url.openStream());
				String str = new String(input.readAllBytes());

				map.put(i, str);
				
			} catch (IOException | NullPointerException e) {
				System.out.println("No existing "+i+".txt");
				empty.add(i);
			}
		}
		
		String str = map.get(63);
		for(int i : empty) {
			map.put(i, str);
		}
	}
	
	private void initComp() {
		
		contentPane.setLayout(new MigLayout(""));
		
		jtf.setHorizontalAlignment(JTextField.CENTER);
		jtf.setFont(font);
		
		area.setEditable(false);
		area.setText("");
		area.setFont(new Font(Font.MONOSPACED,Font.PLAIN,16));
		area.setBorder(new LineBorder(Color.BLACK));
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = convert(jtf.getText());
				area.setText(str);
			}
		});
		
		buttonCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(area.getText()), null);
				
			}
			
		});
		
		buttonMarkdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("```"+area.getText()+"```"), null);
			}
		});
		
		contentPane.add(jtf, "wrap, align center, spanx 2, grow, pushy 10");
		
		contentPane.add(button, "align center, pushx, wrap");
		contentPane.add(buttonCopy, "align center, wrap, pushx");
		contentPane.add(buttonMarkdown, "align center, pushx, wrap");
		
		contentPane.add(area, "wrap, grow, push, spanx 2");
		this.setContentPane(contentPane);
		
	}
	

	private String convert(String input) {
		
		String art = "";
		
		LinkedList<String> lines = new LinkedList<String>();
		
		for(int i = 0; i < input.length(); i++) { // Lettre dans le string
			char c = input.charAt(i);
			int ascii = (int)c;
			
			String str = map.get(ascii);
			
			int beginIndex = 0;
			int endIndex = 0;
			for(int j = 0; j < getLineNumber(str); j++) { // Ligne dans l'ascii art
				beginIndex = endIndex+2; // +2 pour eviter les retour ligne (13 et 10 en ASCII)
				endIndex = str.indexOf((char)13, beginIndex+1); // +1 pour avancer dans str
				
				if(endIndex == -1) { // Si plus de retour ligne, on va jusqu'a la fin de str
					endIndex = str.length(); 
				}
				if(beginIndex > str.length()) { // Si beginIndex depasse str (a cause du +2) on met a la fin
					beginIndex = str.length();
				}
				
				try {
					lines.set(j, lines.get(j)+str.substring(beginIndex, endIndex));
				}catch(IndexOutOfBoundsException e) { // Si aucune ligne creer auparavant
					lines.add(str.substring(beginIndex, endIndex));
				}
				
			}
		}
		for(String str : lines) {
			art += str+"\n"; // on ajoute ligne par ligne en mettant un retour ligne
		}

		return art;
	}
	
	private int getLineNumber(String str) {

		int nbre = 1;
		
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == Character.LINE_SEPARATOR) {
				nbre++;
			}
		}
		
		return nbre;
	}
	
	
	
}
