package fr.jeudelavie.modele.cellule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.jeudelavie.modele.carte.Case;
import lombok.Getter;

public class EtreVivant {

	@Getter
	private final Case caseCourrante;

	@Getter
	private final TypeEtreVivant type;

	private int numGeneration = 1;

	private static final int NB_GENERATION_MAX = 5;

	private final int dureeVie;

	public EtreVivant(final Case c, final TypeEtreVivant t) {
		this.caseCourrante = c;
		this.type = t;
		this.dureeVie = NB_GENERATION_MAX;
	}

	/** @return isMourrante */
	public boolean vieillir() {
		this.numGeneration++;
		return this.numGeneration > this.dureeVie;
	}

	public void mourir() {
		this.caseCourrante.getListeEtreVivant().remove(this);
	}

	@Override
	public String toString() {
		final Gson g = new GsonBuilder().create();
		g.toJson(this.caseCourrante);
		g.toJson(this.type);
		return g.toString();
	}
}
