package perassoft.italianverbs;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Verbs extends ArrayList<Verb>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464198266953132774L;
	public static Verbs Instance = new Verbs();
	public Verbs() {
		loadVerbs();
	}

	private void loadVerbs() {
		Verb verb = new Verb("essere");
		verb.add("io sono");
		verb.add("tu sei");
		verb.add("egli è");
		verb.add("noi siamo");
		verb.add("voi siete");
		verb.add("essi sono");
		
		
		verb.add("io sono stato");
		verb.add("tu sei stato");
		verb.add("egli è stato");
		verb.add("noi siamo stati");
		verb.add("voi siete stati");
		verb.add("essi sono stati");
		
		verb.add("io ero");
		verb.add("tu eri");
		verb.add("egli era");
		verb.add("noi eravamo");
		verb.add("voi eravate");
		verb.add("essi erano");
		
		verb.add("io ero stato");
		verb.add("tu eri stato");
		verb.add("egli era stato");
		verb.add("noi eravamo stati");
		verb.add("voi eravate stati");
		verb.add("essi erano stati");
		
		verb.add("io fui");
		verb.add("tu fosti");
		verb.add("egli fu");
		verb.add("noi fummo");
		verb.add("voi foste");
		verb.add("essi furono");
		
		verb.add("io fui stato");
		verb.add("tu fosti stato");
		verb.add("egli fu stato");
		verb.add("noi fummo stati");
		verb.add("voi foste stati");
		verb.add("essi furono stati");
		
		verb.add("io sarò");
		verb.add("tu sarai");
		verb.add("egli sarà");
		verb.add("noi saremo");
		verb.add("voi sarete");
		verb.add("essi saranno");
		
		verb.add("io sarò stato");
		verb.add("tu sarai stato");
		verb.add("egli sarà stato");
		verb.add("noi saremo stati");
		verb.add("voi sarete stati");
		verb.add("essi saranno stati");
		
		verb.add("io sarei");
		verb.add("tu saresti");
		verb.add("egli sarebbe");
		verb.add("noi saremmo");
		verb.add("voi sareste");
		verb.add("essi sarebbero");
		
		verb.add("io sarei stato");
		verb.add("tu saresti stato");
		verb.add("egli sarebbe stato");
		verb.add("noi saremmo stati");
		verb.add("voi sareste stati");
		verb.add("essi sarebbero stati");
		
		verb.add("che io sia");
		verb.add("che tu sia");
		verb.add("che egli sia");
		verb.add("che noi siamo");
		verb.add("che voi siate");
		verb.add("che essi siano");
		
		verb.add("che io sia stato");
		verb.add("che tu sia stato");
		verb.add("che egli sia stato");
		verb.add("che noi siamo stati");
		verb.add("che voi siate stati");
		verb.add("che essi siano stati");
		
		verb.add("che io fossi");
		verb.add("che tu fossi");
		verb.add("che egli fosse");
		verb.add("che noi fossimo");
		verb.add("che voi foste");
		verb.add("che essi fossero");
		
		verb.add("che io fossi stato");
		verb.add("che tu fossi stato");
		verb.add("che egli fosse stato");
		verb.add("che noi fossimo stati");
		verb.add("che voi foste stati");
		verb.add("che essi fossero stati");
		
		
		verb.add("sii");
		verb.add("sia");
		verb.add("siamo");
		verb.add("siate");
		verb.add("siano");
		
		verb.add("essere");
		
		verb.add("essere stato");
		verb.add("ente");
		verb.add("stato");
		verb.add("essendo");
		verb.add("essendo stato");
		
		add(verb);

	}

	
}
