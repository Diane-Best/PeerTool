package Interface;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jtattoo.plaf.mcwin.McWinLookAndFeel;

import Combination.Combine;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.dom4j.DocumentException;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class PetriFrame extends JFrame {

	private JPanel contentPane;
	private JPanel panel_1;
	private JLabel label1;
	private JTextField FilenameField;
	private Combine combine;
    private ArrayList<String> fileNames = new ArrayList<String>();
    private JFileChooser fileChooser;
    private JTextArea textArea;
    private JCheckBox Async;
    private JCheckBox Sync;
    private JCheckBox Async2;
    private JCheckBox Simple;
    private File[] file;
    private JButton deleteButton;
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PetriFrame frame = new PetriFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PetriFrame() {
		setTitle("Peer�ϳɹ���");
		try {
			UIManager.setLookAndFeel(new McWinLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 441, 378);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		
		label1 = new JLabel("���XML�ļ���");
		
		FilenameField = new JTextField();
		FilenameField.setColumns(10);
		
		JButton btnNewButton = new JButton("ѡ��");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
					        "Petri Net", "xml");
				fileChooser.setFileFilter(filter);
				fileChooser.setMultiSelectionEnabled(true);
				int returnVal = fileChooser.showOpenDialog(panel_1);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	file = new File[fileChooser.getSelectedFiles().length];
			    	file = fileChooser.getSelectedFiles();
			    	FilenameField.setText(file[0].getPath());
			    }
			}
		});
		
		JButton addButton = new JButton("���");
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				for(int i =0;i<file.length;i++){
		    	fileNames.add(file[i].getPath());
		    	textArea.append(file[i].getPath()+"\r\n");
				}
			}
		});
		
		JLabel lblxml = new JLabel("�����XML�ļ���");
		
		JScrollPane scrollPane = new JScrollPane();
		
		deleteButton = new JButton("���������ļ�");
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileNames.clear();
				textArea.setText("");
			}
		});

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(label1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(FilenameField, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton)
							.addGap(22))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblxml)
							.addContainerGap(315, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
							.addContainerGap())))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(117)
					.addComponent(addButton)
					.addGap(28)
					.addComponent(deleteButton)
					.addContainerGap(96, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(label1)
						.addComponent(FilenameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton))
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(33)
							.addComponent(lblxml))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(addButton)
								.addComponent(deleteButton))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
					.addGap(104))
		);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		panel_1.setLayout(gl_panel_1);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		
		Sync = new JCheckBox("ͬ�����");
		Sync.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(Sync.isSelected()){
					Simple.setEnabled(true);
				}
				else {
					Simple.setEnabled(false);
					Simple.setSelected(false);
				}
			}
		});
		
		Async = new JCheckBox("�첽���");

		Simple = new JCheckBox("��");
		Simple.setEnabled(false);
		
		Async2 = new JCheckBox("�첽���2");

		
		JButton runButton = new JButton("ȷ��");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		runButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					combine = new Combine(fileNames);
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(Sync.isSelected()){
					if(combine.analyzeSyncMatrix(file[0].getPath().substring(0, file[0].getPath().lastIndexOf("."))+"(sys).xml")==1)
						JOptionPane.showMessageDialog(null, "ͬ����ϳɹ�", "�ɹ�",JOptionPane.PLAIN_MESSAGE);
					else {
						JOptionPane.showMessageDialog(null, "ͬ�����ʧ��", "ʧ��",JOptionPane.ERROR_MESSAGE);
					}
				}
				if(Async.isSelected()){
					if(combine.analyzeAsyncMatrix(file[0].getPath().substring(0, file[0].getPath().lastIndexOf("."))+"(asys)1.xml")==1)
						JOptionPane.showMessageDialog(null, "�첽��ϳɹ�", "�ɹ�",JOptionPane.PLAIN_MESSAGE);
					else {
						JOptionPane.showMessageDialog(null, "�첽���ʧ��", "ʧ��",JOptionPane.ERROR_MESSAGE);
					}
				}
				if(Async2.isSelected()){
					if(combine.analyzeAsyncMatrix2(file[0].getPath().substring(0, file[0].getPath().lastIndexOf("."))+"(asys)2.xml")==1)
						JOptionPane.showMessageDialog(null, "�첽���2�ɹ�", "�ɹ�",JOptionPane.PLAIN_MESSAGE);
					else {
						JOptionPane.showMessageDialog(null, "�첽���2ʧ��", "ʧ��",JOptionPane.ERROR_MESSAGE);
					}
				}
				if(Simple.isSelected()){
					if(combine.simplify(file[0].getPath().substring(0, file[0].getPath().lastIndexOf("."))+"(sys_s).xml")==1)
						JOptionPane.showMessageDialog(null, "�򻯳ɹ�", "�ɹ�",JOptionPane.PLAIN_MESSAGE);
					else {
						JOptionPane.showMessageDialog(null, "��ʧ��", "ʧ��",JOptionPane.ERROR_MESSAGE);
					}	
				}
				
			}
		});
		
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap(91, Short.MAX_VALUE)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(Sync)
						.addComponent(Simple))
					.addGap(100)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(Async)
						.addComponent(Async2))
					.addGap(72))
				.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
					.addGap(171)
					.addComponent(runButton)
					.addContainerGap(187, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(14)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(Sync)
						.addComponent(Async))
					.addPreferredGap(ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(Simple)
						.addComponent(Async2))
					.addGap(18)
					.addComponent(runButton)
					.addGap(21))
		);
		panel_2.setLayout(gl_panel_2);
	}
}
