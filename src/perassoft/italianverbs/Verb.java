package perassoft.italianverbs;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class Verb extends ArrayList<String> {

	public static final int MAX_VERBS = 95;
	private static final String TERZA_PERSONA_PLURALE = "terza persona plurale";
	private static final String SECONDA_PERSONA_PLURALE = "seconda persona plurale";
	private static final String PRIMA_PERSONA_PLURALE = "prima persona plurale";
	private static final String TERZA_PERSONA_SINGOLARE = "terza persona singolare";
	private static final String SECONDA_PERSONA_SINGOLARE = "seconda persona singolare";
	private static final String PRIMA_PERSONA_SINGOLARE = "prima persona singolare";
	private static final String TRAPASSATO = "trapassato";
	private static final String TRAPASSATO_REMOTO = "trapassato remoto";
	private static final String FUTURO_SEMPLICE = "futuro semplice";
	private static final String FUTURO_ANTERIORE = "futuro anteriore";
	private static final String PASSATO = "passato";
	private static final String PASSATO_REMOTO = "passato remoto";
	private static final String TRAPASSATO_PROSSIMO = "trapassato prossimo";
	private static final String IMPERFETTO = "imperfetto";
	private static final String PASSATO_PROSSIMO = "passato prossimo";
	private static final String PRESENTE = "presente";
	private static final String GERUNDIO = "gerundio";
	private static final String PARTICIPIO = "participio";
	private static final String INFINITO = "infinito";
	private static final String IMPERATIVO = "imperativo";
	private static final String CONGIUNTIVO = "congiuntivo";
	private static final String CONDIZIONALE = "condizionale";
	private static final String INDICATIVO = "indicativo";
	
	public static String[] moods = {
			INDICATIVO, 
			CONDIZIONALE, 
			CONGIUNTIVO, 
			IMPERATIVO, 
			INFINITO, 
			PARTICIPIO, 
			GERUNDIO
			};
	public static String[][]tenses = {
			{PRESENTE, PASSATO_PROSSIMO,IMPERFETTO,TRAPASSATO_PROSSIMO,PASSATO_REMOTO,TRAPASSATO_REMOTO,FUTURO_SEMPLICE,FUTURO_ANTERIORE},
			{PRESENTE, PASSATO},
			{PRESENTE,PASSATO,IMPERFETTO,TRAPASSATO},
			{PRESENTE},
			{PRESENTE, PASSATO},
			{PRESENTE, PASSATO},
			{PRESENTE, PASSATO}
			
	};
	/**
	 * 
	 */
	private static final long serialVersionUID = -4709228867823054044L;
	private static ArrayList<Integer> visibleVerbIndexes = countVisibleVerbItems();

	private String name;

	public Verb(String name) {
		this.name = name;
	}

	public String getMood(int i) {
		if (i < 48)
			return INDICATIVO;
		if (i < 60)
			return CONDIZIONALE;
		if (i < 84)
			return CONGIUNTIVO;
		if (i < 89)
			return IMPERATIVO;
		if (i < 91)
			return INFINITO;
		if (i < 93)
			return PARTICIPIO;
		if (i < 95)
			return GERUNDIO;
		assert (false);
		return null;
	}

	public String toString() {
		return name;
	};

	String getTense(int i) {
		if (i < 6)
			return PRESENTE;
		if (i < 12)
			return PASSATO_PROSSIMO;
		if (i < 18)
			return IMPERFETTO;
		if (i < 24)
			return TRAPASSATO_PROSSIMO;
		if (i < 30)
			return PASSATO_REMOTO;
		if (i < 36)
			return TRAPASSATO_REMOTO;
		if (i < 42)
			return FUTURO_SEMPLICE;
		if (i < 48)
			return FUTURO_ANTERIORE;
		if (i < 54)
			return PRESENTE;
		if (i < 60)
			return PASSATO;
		if (i < 66)
			return PRESENTE;
		if (i < 72)
			return PASSATO;
		if (i < 78)
			return IMPERFETTO;
		if (i < 84)
			return TRAPASSATO;
		if (i < 89)
			return PRESENTE;
		if (i < 90)
			return PRESENTE;
		if (i < 91)
			return PASSATO;
		if (i < 92)
			return PRESENTE;
		if (i < 93)
			return PASSATO;
		if (i < 94)
			return PRESENTE;
		if (i < 95)
			return PASSATO;
		assert (false);
		return null;
	}

	static String getSettingTempo(int i) {
		if (i < 6)
			return "indicativo_presente_checkbox";
		if (i < 12)
			return "indicativo_passato_prossimo_checkbox";
		if (i < 18)
			return "indicativo_imperfetto_checkbox";
		if (i < 24)
			return "indicativo_trapassato_prossimo_checkbox";
		if (i < 30)
			return "indicativo_passato_remoto_checkbox";
		if (i < 36)
			return "indicativo_trapassato_remoto_checkbox";
		if (i < 42)
			return "indicativo_futuro_semplice_checkbox";
		if (i < 48)
			return "indicativo_futuro_anteriore_checkbox";
		if (i < 54)
			return "condizionale_presente_checkbox";
		if (i < 60)
			return "condizionale_passato_checkbox";
		if (i < 66)
			return "congiuntivo_presente_checkbox";
		if (i < 72)
			return "congiuntivo_passato_checkbox";
		if (i < 78)
			return "congiuntivo_imperfetto_checkbox";
		if (i < 84)
			return "congiuntivo_trapassato_checkbox";
		if (i < 89)
			return "imperativo_presente_checkbox";
		if (i < 90)
			return "infinito_presente_checkbox";
		if (i < 91)
			return "infinito_passato_checkbox";
		if (i < 92)
			return "participio_presente_checkbox";
		if (i < 93)
			return "participio_passato_checkbox";
		if (i < 94)
			return "gerundio_presente_checkbox";
		if (i < 95)
			return "gerundio_passato_checkbox";
		assert (false);
		return null;
	}

	String getPersona(int i) {
		if (i < 84) {
			int i1 = i % 6;
			if (i1 == 0)
				return PRIMA_PERSONA_SINGOLARE;
			if (i1 == 1)
				return SECONDA_PERSONA_SINGOLARE;
			if (i1 == 2)
				return TERZA_PERSONA_SINGOLARE;
			if (i1 == 3)
				return PRIMA_PERSONA_PLURALE;
			if (i1 == 4)
				return SECONDA_PERSONA_PLURALE;
			if (i1 == 5)
				return TERZA_PERSONA_PLURALE;
		}
		if (i < 85)
			return SECONDA_PERSONA_SINGOLARE;
		if (i < 86)
			return TERZA_PERSONA_SINGOLARE;
		if (i < 87)
			return PRIMA_PERSONA_PLURALE;
		if (i < 88)
			return SECONDA_PERSONA_PLURALE;
		if (i < 89)
			return TERZA_PERSONA_PLURALE;
		if (i < 95)
			return null;
		assert (false);
		return null;
	}

	public String getName() {
		return name;
	}

	public String getDescription(int question) {
		StringBuilder sb = new StringBuilder();
		sb.append("Verbo ");
		sb.append(name);
		sb.append(", modo ");
		sb.append(getMood(question));
		sb.append(", tempo ");
		sb.append(getTense(question));
		String persona = getPersona(question);
		if (persona != null) {
			sb.append(", ");
			sb.append(persona);
		}
		return sb.toString();
	}

	public static boolean isVisible(int verbIndex) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.getInstance());
		return (sharedPrefs.getBoolean(getSettingTempo(verbIndex), true));
	}

	public static ArrayList<Integer> getVisibleVerbItems() {
		return visibleVerbIndexes;
	}

	public static boolean adjustVisibleVerbs(Preference preference,
			Object newValue) {
		boolean modified = false;
		for (int i = 0; i < MAX_VERBS; i++)
			if (preference.getKey().equals(getSettingTempo(i))) {
				if (Boolean.TRUE.equals(newValue))
				{
					visibleVerbIndexes.add(i);
					modified = true;
				}
				else if (visibleVerbIndexes.size() > 1)
				{
					visibleVerbIndexes.remove((Object)i);
					modified = true;
				}
			}
		
		return modified;
	}

	public static ArrayList<Integer> countVisibleVerbItems() {
		visibleVerbIndexes = new ArrayList<Integer>();
		for (int i = 0; i < MAX_VERBS; i++)
			if (isVisible(i))
				visibleVerbIndexes.add(i);
		return visibleVerbIndexes;
	}

	public boolean verify(int question, ArrayList<String> answers) {
		String[] strings = get(question).split("\\,");
		for (int i = 0; i < strings.length; i++)
			strings[i] = strings[i].trim();
		for (String match : answers) {
			match = match.trim();
			for (String string : strings) {
				if (string.equalsIgnoreCase(match)) {
					return true;
				}
			}
		}
		return false;
	}
}
