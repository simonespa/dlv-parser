/**
 * Contiene due classi che implementano l'ascoltatore degli eventi
 */
package gui.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import parser.ParseException;
import parser.ParserDLV;
import core.SemanticAnalyzer;
import core.exception.SemanticException;

/**
 * 
 * @author Simone Spaccarotella
 *         <p>
 *         Questa classe estende {@link ActionListener}, ed è l'ascoltatore
 *         degli eventi generati dal menu della finestra principale. Il metodo
 *         {@link actionPerformed(ActionEvent event)} controlla l'evento da chi
 *         è stato scatenato e agisce di conseguenza, richiamando i relativi
 *         metodi che implementano il comando adatto. Questi metodi, non possono
 *         essere richiamati dall'esterno, è per questo che sono stati
 *         dichiarati privati
 *         </p>
 * 
 */
public class EventListner implements ActionListener {

	/* componenti visuali su cui avranno effetto gli eventi scatenati */
	private JTabbedPane tabbed;
	private JTextArea console; // console sulla quale verrà riportato il log
								// della sessione
	private JMenuBar menuBar;
	private JToolBar toolBarFile;
	private JToolBar toolBarTab;
	private JToolBar toolBarConsole;

	/* variabili di stato della classe */
	private List<File> files; // lista di descrittori associati ad ogni tab
								// aperto
	private int counter; // contatore utilizzato per visualizzare i numeri di
							// riga nella console
	private ParserDLV parser; // oggetto ParserDLV che verrà utilizzato per
								// parserizzare i file aperti

	/**
	 * 
	 * @param split
	 * @param mnuBar
	 *            <p>
	 *            E' l'unico costruttore, al quale vengono passati un oggetto
	 *            {@link JSplitPane}, {@link JMenuBar}, e tre oggetti di tipo
	 *            {@link JToolBar}, in modo da poter avere pieno controllo sui
	 *            componenti grafici della finestra. Con l'oggetto split ad
	 *            esempio, è possibile accedere alla console (per poter stampare
	 *            i messaggi del parser) oppure ai vari tab aperti. Mentre con
	 *            l'oggetto mnuBar, è possibile accedere alle proprietà dei vari
	 *            menu, settandoli opportunamente sotto determinate condizioni
	 *            </p>
	 */
	public EventListner(JSplitPane split, JMenuBar mnuBar, JToolBar toolBar1,
			JToolBar toolBar2, JToolBar toolBar3) {
		tabbed = (JTabbedPane) split.getBottomComponent();
		JScrollPane s = (JScrollPane) split.getTopComponent();
		console = (JTextArea) s.getViewport().getView();
		menuBar = mnuBar;
		toolBarFile = toolBar1;
		toolBarTab = toolBar2;
		toolBarConsole = toolBar3;
		files = new LinkedList<File>();
		parser = new ParserDLV(System.in);
		counter = 0;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equalsIgnoreCase("Open...")) {
			openFile();
		} else if (command.equalsIgnoreCase("Save")) {
			saveFile();
		} else if (command.equalsIgnoreCase("New")) {
			newFile();
		} else if (command.equalsIgnoreCase("Do parsing")) {
			doParsing();
		} else if (command.equalsIgnoreCase("Exit")) {
			exit();
		} else if (command.equalsIgnoreCase("Close Tab")) {
			closeTab();
		} else if (command.equalsIgnoreCase("Close Other")) {
			closeOther();
		} else if (command.equalsIgnoreCase("Close All")) {
			closeAll();
		} else if (command.equalsIgnoreCase("Clear")) {
			clearConsole();
		} else if (command.equalsIgnoreCase("Log")) {
			log();
		}
	}

	/*
	 * Apre un nuovo file di testo
	 */
	private void openFile() {
		// Istanzia il "JFileChooser" come Open Dialog e imposta il filtro per i
		// file ".txt"
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento di testo .txt", "txt");
		chooser.setFileFilter(filter);
		chooser.showOpenDialog(null);

		// Istanzia il descrittore del file
		File file = chooser.getSelectedFile();

		// Se non è stato selezionato nessun file, esce dal metodo senza fare
		// nulla
		if (file == null)
			return;

		// Aggiunge il descrittore alla lista dei descrittori
		files.add(file);

		// Se esiste almeno un tab aperto, abilita i JMenuItem "Save" e "Do
		// Parsing"
		// e i tre JMenuItem nel menu "Tab"
		if (files.size() > 0) {
			menuBar.getMenu(0).getMenuComponent(2).setEnabled(true);
			menuBar.getMenu(0).getMenuComponent(4).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(0).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(1).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(2).setEnabled(true);
			toolBarFile.getComponentAtIndex(2).setEnabled(true);
			toolBarFile.getComponentAtIndex(3).setEnabled(true);
			toolBarTab.getComponentAtIndex(0).setEnabled(true);
			toolBarTab.getComponentAtIndex(1).setEnabled(true);
			toolBarTab.getComponentAtIndex(2).setEnabled(true);
		}

		// Crea una "JTextArea" all'interno di un "JScrollPane" e li aggiunge al
		// "JTabbedPane". In pratica crea una nuova area di lavoro su un tab
		// separato
		// per il file aperto
		JTextArea area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setWheelScrollingEnabled(true);
		tabbed.addTab(file.getName(), scroll);

		// Copia il contenuto del file all'interno del nuovo tab
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String text = in.readLine();
			while (text != null) {
				area.append(text);
				area.append("\n");
				text = in.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Chiusura dello stream di input
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Crea un nuovo file di testo
	 */
	private void newFile() {
		// Istanzia il "JFileChooser" come Save Dialog e imposta il filtro per i
		// file ".txt"
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento di testo .txt", "txt");
		chooser.setFileFilter(filter);
		chooser.showSaveDialog(null);

		// Istanzia il descrittore del file
		File file = chooser.getSelectedFile();

		// Se non è stato selezionato nessun file, esce dal metodo senza fare
		// nulla
		if (file == null)
			return;

		// Forza l'utente a creare un file ".txt"
		try {
			if (file.getName().contains(".txt")) {
				file.createNewFile();
			} else {
				file = new File(file.getAbsolutePath() + ".txt");
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Aggiunge il descrittore alla lista dei descrittori
		files.add(file);

		// Se esiste almeno un tab aperto, abilita i JMenuItem "Save" e "Do
		// Parsing"
		// e i tre JMenuItem nel menu "Tab"
		if (files.size() > 0) {
			menuBar.getMenu(0).getMenuComponent(2).setEnabled(true);
			menuBar.getMenu(0).getMenuComponent(4).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(0).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(1).setEnabled(true);
			menuBar.getMenu(1).getMenuComponent(2).setEnabled(true);
			toolBarFile.getComponentAtIndex(2).setEnabled(true);
			toolBarFile.getComponentAtIndex(3).setEnabled(true);
			toolBarTab.getComponentAtIndex(0).setEnabled(true);
			toolBarTab.getComponentAtIndex(1).setEnabled(true);
			toolBarTab.getComponentAtIndex(2).setEnabled(true);
		}

		// Crea una "JTextArea" all'interno di un "JScrollPane" e li aggiunge al
		// "JTabbedPane". In pratica crea una nuova area di lavoro su un tab
		// separato
		// per il file aperto
		JTextArea area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setWheelScrollingEnabled(true);
		tabbed.addTab(file.getName(), scroll);
	}

	/*
	 * Salva il file su disco
	 */
	private void saveFile() {
		// Acquisisce il contenuto dell'area di lavoro e lo salva in una
		// variabile di tipo "String"
		JScrollPane scroll = (JScrollPane) tabbed.getSelectedComponent();
		JTextArea area = (JTextArea) scroll.getViewport().getView();
		String text = area.getText();

		// Apre uno stream di output e copia il contenuto
		PrintWriter out = null;
		try {
			out = new PrintWriter(files.get(tabbed.getSelectedIndex()));
			out.print(text);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Effettua il parsing del programma
	 */
	private void doParsing() {
		File file = null;
		BufferedReader in = null;
		SemanticAnalyzer semantic = null;

		// Prende la data e l'ora di sistema e la stampa sulla console del
		// programma
		GregorianCalendar g = (GregorianCalendar) GregorianCalendar.getInstance();
		int day = g.get(GregorianCalendar.DAY_OF_MONTH);
		int month = g.get(GregorianCalendar.MONTH);
		int year = g.get(GregorianCalendar.YEAR);
		int hour = g.get(GregorianCalendar.HOUR_OF_DAY);
		int minute = g.get(GregorianCalendar.MINUTE);
		int second = g.get(GregorianCalendar.SECOND);

		try {
			// Prende lo stream da file per poi passarlo al costruttore del
			// parser
			file = files.get(tabbed.getSelectedIndex());
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			parser.ReInit(in);
			parser.dlp();

			// Apre nuovamente lo stream sullo stesso file per poterlo passare
			// al costruttore
			// dell'analizzatore semantico
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			semantic = new SemanticAnalyzer(in);
			semantic.startScan();

			// Se tutto è andato a buon fine viene stampato il messaggio di ok e
			// viene lanciato il comando dlv da riga di comando a cui viene
			// passato il contenuto del file
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - DLV parsed succesfully\n\n");
			in = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
					.exec("dlv/dlv " + file.getCanonicalPath()).getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				console.append(line + "\n");
			}
			console.append("\n\n");
		} catch (ParseException e) {
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - <Syntax Error> - " + e.getMessage()
					+ "\n\n");
		} catch (SemanticException e) {
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - <Semantic Error> - "
					+ e.getMessage() + "\n\n");
		} catch (IOException e) {
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - " + e.getMessage() + "\n\n");
		} catch (Exception e) {
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - " + e.getMessage() + "\n\n");
		} catch (Error e) {
			console.append(day + "/" + month + "/" + year + " - " + hour + ":"
					+ minute + ":" + second + "\n" + (++counter) + ": ["
					+ file.getName() + "] - <Lexical Error> - "
					+ e.getMessage() + "\n\n");
		} finally {
			// Chiude lo stream di input
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Abilita il tasto "Clear" nel menu "Console"
		menuBar.getMenu(2).getMenuComponent(0).setEnabled(true);
		menuBar.getMenu(2).getMenuComponent(1).setEnabled(true);
		toolBarConsole.getComponentAtIndex(0).setEnabled(true);
		toolBarConsole.getComponentAtIndex(1).setEnabled(true);
	}

	/*
	 * Esce dal programma
	 */
	private void exit() {
		System.exit(0);
	}

	/*
	 * Chiude il tab corrente
	 */
	private void closeTab() {
		int index = tabbed.getSelectedIndex();
		tabbed.remove(index);
		files.remove(index);
		if (files.size() == 0) {
			menuBar.getMenu(0).getMenuComponent(2).setEnabled(false);
			menuBar.getMenu(0).getMenuComponent(4).setEnabled(false);
			menuBar.getMenu(1).getMenuComponent(0).setEnabled(false);
			menuBar.getMenu(1).getMenuComponent(1).setEnabled(false);
			menuBar.getMenu(1).getMenuComponent(2).setEnabled(false);
			toolBarFile.getComponentAtIndex(2).setEnabled(false);
			toolBarFile.getComponentAtIndex(3).setEnabled(false);
			toolBarTab.getComponentAtIndex(0).setEnabled(false);
			toolBarTab.getComponentAtIndex(1).setEnabled(false);
			toolBarTab.getComponentAtIndex(2).setEnabled(false);
		}
	}

	/*
	 * Chiude tutti i tab, tranne quello corrente
	 */
	private void closeOther() {
		JScrollPane scroll = (JScrollPane) tabbed.getSelectedComponent();
		File f = files.get(tabbed.getSelectedIndex());
		tabbed.removeAll();
		files.clear();
		tabbed.addTab(f.getName(), scroll);
		files.add(f);
	}

	/*
	 * Chiude tutti i tab aperti
	 */
	private void closeAll() {
		tabbed.removeAll();
		files.clear();
		menuBar.getMenu(0).getMenuComponent(2).setEnabled(false);
		menuBar.getMenu(0).getMenuComponent(4).setEnabled(false);
		menuBar.getMenu(1).getMenuComponent(0).setEnabled(false);
		menuBar.getMenu(1).getMenuComponent(1).setEnabled(false);
		menuBar.getMenu(1).getMenuComponent(2).setEnabled(false);
		toolBarFile.getComponentAtIndex(2).setEnabled(false);
		toolBarFile.getComponentAtIndex(3).setEnabled(false);
		toolBarTab.getComponentAtIndex(0).setEnabled(false);
		toolBarTab.getComponentAtIndex(1).setEnabled(false);
		toolBarTab.getComponentAtIndex(2).setEnabled(false);
	}

	/*
	 * Svuota il contenuto della console
	 */
	private void clearConsole() {
		// Pulisce la console
		console.setText("");
		// Disabilita le opzioni nel menu "Console"
		menuBar.getMenu(2).getMenuComponent(0).setEnabled(false);
		menuBar.getMenu(2).getMenuComponent(1).setEnabled(false);
		toolBarConsole.getComponentAtIndex(0).setEnabled(false);
		toolBarConsole.getComponentAtIndex(1).setEnabled(false);
		// Azzera il contatore
		counter = 0;
	}

	/*
	 * Esegue il log del programma in un file posizionato nella cartella log del
	 * progetto
	 */
	private void log() {
		GregorianCalendar g = (GregorianCalendar) GregorianCalendar
				.getInstance();
		int day = g.get(GregorianCalendar.DAY_OF_MONTH);
		int month = g.get(GregorianCalendar.MONTH);
		int year = g.get(GregorianCalendar.YEAR);
		String consoleText = console.getText();
		JScrollPane s = (JScrollPane) tabbed.getComponentAt(tabbed.getSelectedIndex());
		JTextArea t = (JTextArea) s.getViewport().getView();
		String areaText = t.getText();
		PrintWriter out = null;
		try {
			File file = new File("log/" + day + "." + month + "." + year+ ".log");
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new PrintWriter(new FileOutputStream(file));
			out.append(areaText);
			out.append("\n\n");
			out.append(consoleText);
			out.append("--------------------------------------------------------------------------------------------");
			out.append("\n\n\n");
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

}