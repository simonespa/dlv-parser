/**
 * Contiene una classe che riguarda prettamente la grafica del programma
 */
package gui;

import gui.event.EventListner;
import gui.event.MouseListner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Simone Spaccarotella
 *         <p>
 *         Questa classe istanzia la finestra principale del programma, dalla
 *         quale e' possibile effettuare tutte le operazioni richieste,
 *         accessibili tramite la barra dei menu.
 *         </p>
 *         Non essendoci la possibilit� di settare i parametri della finestra
 *         principale, questa classe possiede solo un costruttore, mentre tutti
 *         i suoi metodi sono privati
 *         <p>
 *
 */
public class Editor extends JFrame {

	// Variabili di stato della finestra: x (larghezza) - y (altezza)
	private int x = 0;
	private int y = 0;

	// Sono tutti i componenti grafici che verranno visualizzati nella finestra
	private JPanel panel; // pannello principale
	private JTabbedPane tabbed; // � il pannello sul quale verranno aggiunte le
	// schede
	// contenenti delle JTextArea
	private JMenuBar menuBar; // barra dei men�
	private JToolBar toolBarFile;
	private JToolBar toolBarTab;
	private JToolBar toolBarConsole;
	private JMenu mnuFile; // menu file
	private JMenu mnuTab; // menu tab
	private JMenu mnuConsole; // menu console
	private JTextArea console; // console sulla quale verranno visualizzati i
	// messaggi del parser
	private JScrollPane scroll; // � il contenitore della JTextArea console
	private JSplitPane verticalSplit; // � il pannello di livello superiore
	// che contiene tutti gli elementi
	// sopra elencati

	private EventListner event; // � un oggetto di tipo ascoltatore. E' stato
								// istanziato per memorizzare
								// lo stato di alcune variabili di utilit�

	/**
	 *
	 * <p>
	 * E' il costruttore di default, nonch� l'unico. Questa classe ha tutti i
	 * parametri cablati all'interno, non � pertanto possibile settare da
	 * programma il suo aspetto (cosa tra l'altro superflua per lo scopo), ma �
	 * possibile effettuare altre operazioni come quelle descritte in precedenza
	 * </p>
	 */
	public Editor() {
		super("DLV Parser");
		initFrame();
		setComponent();
		initMenu();
		initToolbar();
		panel.add(verticalSplit);
		// Aggiunge una cornice al pannello
		JPanel p = new JPanel(new GridLayout(1, 3));
		p.add(toolBarFile, 0);
		p.add(toolBarTab, 1);
		p.add(toolBarConsole, 2);
		panel.add(p, BorderLayout.NORTH);
		panel.add(new JPanel(), BorderLayout.SOUTH);
		panel.add(new JPanel(), BorderLayout.WEST);
		panel.add(new JPanel(), BorderLayout.EAST);
		// Aggiunge il pannello al "ContentPane" della finestra principale
		setContentPane(panel);
		// Rende la finestra visibile
		setVisible(true);
	}

	/*
	 * Inizializza le propriet� della finestra, tra cui: la dimensione, la
	 * posizione iniziale, il Look & Feel e alcune operazioni di default.
	 */
	private void initFrame() {
		/* Imposta il Look and Feel di Windows */
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);

		/* Acquisice la dimensione della risoluzione dello schermo */
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		x = dim.width / 2;
		y = dim.height / 2;

		setIconImage(Toolkit.getDefaultToolkit().getImage("ico/icon.png"));

		/*
		 * Imposta la finestra iniziale con una dimensione pari alla met� delle
		 * dimensioni della risoluzione dello schermo, e la posiziona al centro
		 * dello stesso. All'apertura, essa apparir� massimizzata, ma le sue
		 * dimensioni di default al ripristino saranno quelle descritte in
		 * precedenza
		 */
		setSize(x, y);
		setLocation(x - x / 2, y - y / 2);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*
	 * Istanzia i componenti grafici della classe
	 */
	private void setComponent() {
		panel = new JPanel(new BorderLayout());
		tabbed = new JTabbedPane();
		console = new JTextArea();
		console.setBackground(Color.LIGHT_GRAY);
		console.setEditable(false);
		scroll = new JScrollPane(console);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setWheelScrollingEnabled(true);
		verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scroll,
				tabbed);
		verticalSplit.setDividerLocation(300);
		verticalSplit.setOneTouchExpandable(true);
		verticalSplit.setDividerSize(20);
		menuBar = new JMenuBar();
		toolBarFile = new JToolBar();
		toolBarTab = new JToolBar();
		toolBarConsole = new JToolBar();
	}

	/*
	 * Costruisce la barra dei menu, posizionandola in calce al frame.
	 */
	private void initMenu() {
		event = new EventListner(verticalSplit, menuBar, toolBarFile,
				toolBarTab, toolBarConsole);

		// Istanzia gli item per i menu "File", "Tab" e "Console", setta alcune
		// propriet� e
		// registra i componenti all'ascoltatore "event"
		JMenuItem openFile = new JMenuItem("Open...");
		openFile.setToolTipText("Apri - apre il file di testo specificato");
		openFile.setMnemonic('O');
		openFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/openFile16.png")));
		openFile.addActionListener(event);
		JMenuItem newFile = new JMenuItem("New");
		newFile.setToolTipText("Nuovo - crea un nuovo file di testo");
		newFile.setMnemonic('N');
		newFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/newFile16.png")));
		newFile.addActionListener(event);
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.setToolTipText("Salva - salva il file corrente");
		saveFile.setMnemonic('S');
		saveFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/saveFile16.png")));
		saveFile.setEnabled(false);
		saveFile.addActionListener(event);
		JMenuItem doParsing = new JMenuItem("Do parsing");
		doParsing
				.setToolTipText("Parse - avvia l'operazione di parsing del file");
		doParsing.setMnemonic('D');
		doParsing.setIcon(new ImageIcon(Editor.class
				.getResource("ico/doParsing16.png")));
		doParsing.setEnabled(false);
		doParsing.addActionListener(event);
		JMenuItem exit = new JMenuItem("Exit");
		exit.setToolTipText("Esci - chiude il programma");
		exit.setMnemonic('E');
		exit.setIcon(new ImageIcon(Editor.class.getResource("ico/exit16.png")));
		exit.addActionListener(event);

		JMenuItem closeTab = new JMenuItem("Close Tab");
		closeTab.setToolTipText("Chiudi scheda - chiude la scheda corrente");
		closeTab.setMnemonic('T');
		closeTab.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeTab16.png")));
		closeTab.setEnabled(false);
		closeTab.addActionListener(event);
		JMenuItem closeOther = new JMenuItem("Close Other");
		closeOther
				.setToolTipText("Chiudi altre - chiude le altre schede tranne quella corrente");
		closeOther.setMnemonic('O');
		closeOther.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeOther16.png")));
		closeOther.setEnabled(false);
		closeOther.addActionListener(event);
		JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.setToolTipText("Chiudi tutto - chiude tutte le schede");
		closeAll.setMnemonic('A');
		closeAll.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeAll16.png")));
		closeAll.setEnabled(false);
		closeAll.addActionListener(event);

		JMenuItem clearConsole = new JMenuItem("Clear");
		clearConsole
				.setToolTipText("Pulisci - pulisce lo schermo della console");
		clearConsole.setMnemonic('r');
		clearConsole.setIcon(new ImageIcon(Editor.class
				.getResource("ico/clearConsole16.png")));
		clearConsole.setEnabled(false);
		clearConsole.addActionListener(event);
		JMenuItem logConsole = new JMenuItem("Log");
		logConsole.setToolTipText("Log - fa il log della console su file");
		logConsole.setMnemonic('L');
		logConsole.setIcon(new ImageIcon(Editor.class
				.getResource("ico/log16.png")));
		logConsole.setEnabled(false);
		logConsole.addActionListener(event);

		// Istanzia i menu
		mnuFile = new JMenu("File");
		mnuFile.setMnemonic('F');
		mnuTab = new JMenu("Tab");
		mnuTab.setMnemonic('T');
		mnuConsole = new JMenu("Console");
		mnuConsole.setMnemonic('C');

		// Aggiunge gli item ai menu
		mnuFile.add(openFile);
		mnuFile.add(newFile);
		mnuFile.add(saveFile);
		mnuFile.addSeparator();
		mnuFile.add(doParsing);
		mnuFile.addSeparator();
		mnuFile.add(exit);

		mnuTab.add(closeTab);
		mnuTab.add(closeOther);
		mnuTab.add(closeAll);

		mnuConsole.add(clearConsole);
		mnuConsole.add(logConsole);

		// Istanzia il Menu Bar e gli aggiunge i menu
		menuBar.add(mnuFile);
		menuBar.add(mnuTab);
		menuBar.add(mnuConsole);

		// Aggiunge il Menu Bar alla finestra principale
		setJMenuBar(menuBar);
	}

	private void initToolbar() {
		int x = 100;
		int y = 100;

		// Istanzia gli item della toolbar "File", "Tab" e "Console", setta
		// alcune propriet� e
		// registra i componenti all'ascoltatore "EventListner" e "MouseListner"
		JMenuItem openFile = new JMenuItem("Open...");
		openFile.setToolTipText("Apri - apre il file di testo specificato");
		openFile.setMnemonic('O');
		openFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/openFile24.png")));
		openFile.setSize(x, y);
		openFile.addActionListener(event);
		openFile.addMouseListener(new MouseListner());
		JMenuItem newFile = new JMenuItem("New");
		newFile.setToolTipText("Nuovo - crea un nuovo file di testo");
		newFile.setMnemonic('N');
		newFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/newFile24.png")));
		newFile.setSize(x, y);
		newFile.addActionListener(event);
		newFile.addMouseListener(new MouseListner());
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.setToolTipText("Salva - salva il file corrente");
		saveFile.setMnemonic('S');
		saveFile.setIcon(new ImageIcon(Editor.class
				.getResource("ico/saveFile24.png")));
		saveFile.setEnabled(false);
		saveFile.setSize(x, y);
		saveFile.addActionListener(event);
		saveFile.addMouseListener(new MouseListner());
		JMenuItem doParsing = new JMenuItem("Do parsing");
		doParsing
				.setToolTipText("Parse - avvia l'operazione di parsing del file");
		doParsing.setMnemonic('D');
		doParsing.setIcon(new ImageIcon(Editor.class
				.getResource("ico/doParsing24.png")));
		doParsing.setEnabled(false);
		doParsing.setSize(x, y);
		doParsing.addActionListener(event);
		doParsing.addMouseListener(new MouseListner());

		JMenuItem closeTab = new JMenuItem("Close Tab");
		closeTab.setToolTipText("Chiudi scheda - chiude la scheda corrente");
		closeTab.setMnemonic('T');
		closeTab.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeTab24.png")));
		closeTab.setEnabled(false);
		closeTab.addActionListener(event);
		closeTab.setSize(x, y);
		closeTab.addMouseListener(new MouseListner());
		JMenuItem closeOther = new JMenuItem("Close Other");
		closeOther
				.setToolTipText("Chiudi altre - chiude le altre schede tranne quella corrente");
		closeOther.setMnemonic('O');
		closeOther.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeOther24.png")));
		closeOther.setEnabled(false);
		closeOther.setSize(x, y);
		closeOther.addActionListener(event);
		closeOther.addMouseListener(new MouseListner());
		JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.setToolTipText("Chiudi tutto - chiude tutte le schede");
		closeAll.setMnemonic('A');
		closeAll.setIcon(new ImageIcon(Editor.class
				.getResource("ico/closeAll24.png")));
		closeAll.setEnabled(false);
		closeAll.setSize(x, y);
		closeAll.addActionListener(event);
		closeAll.addMouseListener(new MouseListner());

		JMenuItem clearConsole = new JMenuItem("Clear");
		clearConsole
				.setToolTipText("Pulisci - pulisce lo schermo della console");
		clearConsole.setMnemonic('r');
		clearConsole.setIcon(new ImageIcon(Editor.class
				.getResource("ico/clearConsole24.png")));
		clearConsole.setEnabled(false);
		clearConsole.setSize(x, y);
		clearConsole.addActionListener(event);
		clearConsole.addMouseListener(new MouseListner());
		JMenuItem logConsole = new JMenuItem("Log");
		logConsole.setToolTipText("Log - fa il log della console su file");
		logConsole.setMnemonic('L');
		logConsole.setIcon(new ImageIcon(Editor.class
				.getResource("ico/log24.png")));
		logConsole.setEnabled(false);
		logConsole.setSize(x, y);
		logConsole.addActionListener(event);
		logConsole.addMouseListener(new MouseListner());

		toolBarFile.add(openFile);
		toolBarFile.add(newFile);
		toolBarFile.add(saveFile);
		toolBarFile.add(doParsing);
		toolBarTab.add(closeTab);
		toolBarTab.add(closeOther);
		toolBarTab.add(closeAll);
		toolBarConsole.add(clearConsole);
		toolBarConsole.add(logConsole);
	}
}
