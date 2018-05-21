package fr.jeudelavie.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import fr.jeudelavie.modele.carte.Carte;
import fr.jeudelavie.modele.cellule.EtreVivant;

public class CarteUI extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2393515701037720471L;

	public static final int LARGEUR_CASE = 10;

	public static final int HAUTEUR_CASE = 10;

	private final Carte carte;

	public CarteUI(final Carte carte) {
		this.carte = carte;
		setLayout(new BorderLayout());
	}

	private static Color getCouleur(final EtreVivant e) {
		if (e == null || e.getType() == null) {
			return Color.WHITE;
		} else {
			return e.getType().getCouleur();
		}
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		this.carte.visiter(c -> {
			final int x = c.getX() * LARGEUR_CASE;
			final int y = c.getY() * HAUTEUR_CASE;
			g.setColor(getCouleur(c.getEtreVivant()));
			g.fillRect(x, y, LARGEUR_CASE, HAUTEUR_CASE);
		});
	}

}
