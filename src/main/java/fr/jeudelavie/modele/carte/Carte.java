package fr.jeudelavie.modele.carte;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Carte {

	@Setter
	private Case[][] grille;

	private final int largeur;

	private final int hauteur;

	protected Carte() {
		this(0, 0);
	}

	public Carte(final int largeur, final int hauteur) {
		this.largeur = largeur;
		this.hauteur = hauteur;
	}

	public void visiter(final Consumer<Case> consumer) {
		Arrays.stream(this.grille).forEach(l -> Arrays.stream(l).forEach(consumer));
	}

	public void genererCarteVide() {
		// init instanciation carte
		this.grille = new Case[this.hauteur][this.largeur];

		IntStream.range(0, this.hauteur)
				.forEach(y -> IntStream.range(0, this.largeur).forEach(x -> this.grille[y][x] = new Case(x, y)));
		// init voisines + type
		visiter(c -> {
			// init voisine
			c.setNordOuest(calculVoisineCarte(c, Direction.NORDOUEST));
			c.setNord(calculVoisineCarte(c, Direction.NORD));
			c.setNordEst(calculVoisineCarte(c, Direction.NORDEST));
			c.setOuest(calculVoisineCarte(c, Direction.OUEST));
			c.setEst(calculVoisineCarte(c, Direction.EST));
			c.setSudOuest(calculVoisineCarte(c, Direction.SUDOUEST));
			c.setSud(calculVoisineCarte(c, Direction.SUD));
			c.setSudEst(calculVoisineCarte(c, Direction.SUDEST));
		});
	}

	/**
	 * Methode qui renvoie true si la case passee en paramettre est la derniere
	 * case d'une ligne de la carte (la plus a droite)
	 *
	 * @param c
	 * @return
	 */
	public boolean isDerniereCaseX(final Case c) {
		if (c == null) { return false; }
		return c.getX() == this.largeur - 1;
	}

	/**
	 * Methode qui renvoie true si la case passee en paramettre est la derniere
	 * case d'une colonne de la carte (la plus en bas)
	 *
	 * @param c
	 * @return
	 */
	public boolean isDerniereCaseY(final Case c) {
		if (c == null) { return false; }
		return c.getY() == this.hauteur - 1;
	}

	/**
	 * Methode qui renvoie la case voisine de la case c passee en parametre dans
	 * la direction dir passee en parametre determine a partir de la grille de
	 * la carte (this)
	 *
	 * @param c
	 * @param dir
	 * @return
	 */
	private Case calculVoisineCarte(final Case c, final Direction dir) {
		final int newX = getXFromDir(c.getX(), dir);
		final int newY = getYFromDir(c.getY(), dir);
		return this.grille[newY][newX];
	}

	private int getXFromDir(final int x, final Direction dir) {
		final int ouest = x - 1 > 0 ? x - 1 : this.largeur - 1;
		final int est = x + 1 < this.largeur - 1 ? x + 1 : 0;
		switch (dir) {
		case NORDOUEST:
			return ouest;
		case NORD:
			return x;
		case NORDEST:
			return est;
		case OUEST:
			return ouest;
		case EST:
			return est;
		case SUDOUEST:
			return ouest;
		case SUD:
			return x;
		case SUDEST:
			return est;
		default:
			return x;
		}
	}

	private int getYFromDir(final int y, final Direction dir) {
		final int nord = y - 1 > 0 ? y - 1 : this.hauteur - 1;
		final int sud = y + 1 < this.hauteur - 1 ? y + 1 : 0;
		switch (dir) {
		case NORDOUEST:
			return nord;
		case NORD:
			return nord;
		case NORDEST:
			return nord;
		case OUEST:
			return y;
		case EST:
			return y;
		case SUDOUEST:
			return sud;
		case SUD:
			return sud;
		case SUDEST:
			return sud;
		default:
			return y;
		}
	}

	public int getNbCases() {
		return this.largeur * this.hauteur;
	}

}
