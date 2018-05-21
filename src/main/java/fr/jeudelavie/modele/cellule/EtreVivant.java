package fr.jeudelavie.modele.cellule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.jeudelavie.modele.carte.Case;
import lombok.Getter;
import lombok.Setter;

public class EtreVivant {

	@Getter
	@Setter
	private Case caseCourrante;

	@Getter
	@Setter
	private TypeEtreVivant type;

	@Override
	public String toString() {
		final Gson g = new GsonBuilder().create();
		g.toJson(this.caseCourrante);
		g.toJson(this.type);
		return g.toString();
	}
}
