/**
 * Contiene una classe di utilità, che implementa l'analisi semantica
 */
package core;

import java.io.BufferedReader;
import java.io.IOException;

import core.exception.SemanticException;

/**
 * 
 * @author Simone Spaccarotella
 * <p>
 * Questa classe implementa l'analisi semantica del programma.
 * </p>
 * 
 */
public class SemanticAnalyzer {

	private boolean maxintDefined;
	private BufferedReader input;

	/**
	 * <p>
	 * Costruttore parametrizzato. Riceve in input lo stream proveniente dal file che
	 * deve essere analizzato.
	 * </p> 
	 * @param in
	 */
	public SemanticAnalyzer(BufferedReader in) {
		maxintDefined = false;
		input = in;
	}

	private void check(String line) throws SemanticException {
		if (isConstraint(line)) {
		} else if (isFact(line)) {
		} else {
		}
	}

	private void defineMaxint() {
		maxintDefined = true;
	}

	private boolean isConstraint(String text) {
		return text.startsWith(":-") || text.startsWith(":~");
	}

	private boolean isFact(String text) {
		if (!text.contains(":-"))
			return true;
		else
			return false;
	}

	private boolean maxintDefined() {
		return maxintDefined;
	}

	/**
	 * <p>
	 * Esegue la scansione della stringa e richiama i metodi privati che implementano
	 * il controllo semantico.
	 * </p> 
	 * @throws SemanticException
	 * @throws IOException
	 */
	public void startScan() throws SemanticException, IOException {
		String line;
		while ((line = input.readLine()) != null) {
			if (!maxintDefined()) {
				if (line.contains("#maxint")) {
					defineMaxint();
					continue;
				}
			} else {
				if (line.contains("#maxint"))
					throw new SemanticException(
							"Cannot redefine \"#maxint\". Ignoring this definition");
			}
			check(line);
		}
		input.close();
	}

}
