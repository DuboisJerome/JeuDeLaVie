package fr.jeudelavie.modele.cellule;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;

public class TypeEtreVivant {

	public static final TypeEtreVivant ANY = new TypeEtreVivant("ANY");
	public static final TypeEtreVivant ALL = new TypeEtreVivant("ALL");

	@Getter
	private final String nom;

	@Getter
	@Setter
	private Color couleur = Color.BLACK;

	public TypeEtreVivant(final String nom) {
		this.nom = nom;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TypeEtreVivant)) return false;
		final TypeEtreVivant autre = (TypeEtreVivant) obj;
		return this.nom.equals(autre.nom);
	}

	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}

	@Override
	public String toString() {
		return this.nom;
	}
}
