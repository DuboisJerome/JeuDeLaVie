package fr.jeudelavie.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

	private static Color getCouleur(final Collection<EtreVivant> liste) {
		if (liste == null || liste.isEmpty()) {
			return Color.WHITE;
		} else {
			final List<Color> couleurs = liste.stream()
					.filter(e -> e != null && e.getType() != null && e.getType().getCouleur() != null)
					.map(e -> e.getType().getCouleur()).collect(Collectors.toList());
			int r = 0, g = 0, b = 0;
			for (final Color c : couleurs) {
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
			final int nbCouleurs = couleurs.size();
			return new Color(r / nbCouleurs, g / nbCouleurs, b / nbCouleurs);
		}
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		this.carte.stream().forEach(c -> {
			final int x = c.getX() * LARGEUR_CASE;
			final int y = c.getY() * HAUTEUR_CASE;
			g.setColor(getCouleur(c.getListeEtreVivant()));
			g.fillRect(x, y, LARGEUR_CASE, HAUTEUR_CASE);
		});
	}

}
