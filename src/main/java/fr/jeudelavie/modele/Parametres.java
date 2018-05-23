package fr.jeudelavie.modele;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.jeudelavie.modele.carte.Carte;
import fr.jeudelavie.modele.carte.Case;
import fr.jeudelavie.modele.cellule.EtreVivant;
import fr.jeudelavie.modele.cellule.GroupeReproduction;
import fr.jeudelavie.modele.cellule.TypeEtreVivant;
import fr.jeudelavie.modele.operation.OperationCompare.OperateurCompare;
import fr.jeudelavie.util.PublicObservable;
import lombok.Getter;
import lombok.Setter;

public class Parametres implements Observer {

	@Getter
	@Setter
	public int longueur = 100;

	@Getter
	@Setter
	public int largeur = 100;

	@Getter
	private final List<GroupeReproduction> listeGroupes = new ArrayList<>();

	@Getter
	private final List<TypeEtreVivant> listeTypeEtreVivant = new ArrayList<>();

	@Getter
	transient private final PublicObservable observable = new PublicObservable();

	public void setJeuDeLaVie() {

		final TypeEtreVivant cellH = new TypeEtreVivant("Cellule Homme");
		cellH.setCouleur(Color.BLUE);
		ajouterTypeEtreVivant(cellH);

		final TypeEtreVivant cellF = new TypeEtreVivant("Cellule Femme");
		cellF.setCouleur(Color.PINK);
		ajouterTypeEtreVivant(cellF);

		final Map<TypeEtreVivant, Integer> map = new HashMap<>();
		map.put(cellF, Integer.valueOf(1));
		map.put(cellH, Integer.valueOf(1));
		// 0 && q == 3 => 1
		// 1 && q < 2 || q > 3 => 0
		final GroupeReproduction g = new GroupeReproduction("Groupe cellules", map);
		g.ajouterCondition(cellH, OperateurCompare.SUP_EGAL, 1);
		g.ajouterCondition(cellF, OperateurCompare.SUP_EGAL, 1);

		g.ajouterCondition(cellH, OperateurCompare.INF_EGAL, 4);
		g.ajouterCondition(cellF, OperateurCompare.INF_EGAL, 4);
		ajouterGroupeReproduction(g);
	}

	public void ajouterTypeEtreVivant(final TypeEtreVivant t) {
		this.listeTypeEtreVivant.add(t);
		this.observable.setChanged();
		this.observable.notifyObservers();
	}

	public void ajouterGroupeReproduction(final GroupeReproduction g) {
		this.listeGroupes.add(g);
		this.observable.setChanged();
		this.observable.notifyObservers();
	}

	public Carte genererCarte() {
		final Random r = new Random();
		final Carte carte = new Carte(this.longueur, this.largeur);
		carte.genererCarteVide();
		carte.stream().forEach(c -> {
			if (r.nextDouble() > 0.7) {
				final EtreVivant e = new EtreVivant(c,
						Parametres.this.listeTypeEtreVivant.get(r.nextInt(Parametres.this.listeTypeEtreVivant.size())));
				c.ajouterEtreVivant(e);
			}
		});
		return carte;
	}

	public int tourSuivant(final Carte carte) {
		final Map<Case, EtreVivant> map = new HashMap<>();
		carte.stream().forEach(c -> {
			final EtreVivant naissance = c.calculerProchaineGeneration(Parametres.this);
			if (naissance != null) {
				map.put(c, naissance);
			}
		});
		map.entrySet().parallelStream().parallel().forEach(e -> e.getKey().getListeEtreVivant().add(e.getValue()));
		carte.stream().forEach(c -> {
			final List<EtreVivant> listeAMourir = c.getListeEtreVivant().stream().filter(e -> e.vieillir())
					.collect(Collectors.toList());
			c.getListeEtreVivant().removeAll(listeAMourir);
		});
		return map.size();
	}

	public void sauvegarder() {
		final Gson g = new GsonBuilder().setPrettyPrinting().create();
		final List<String> lignes = new ArrayList<>();
		lignes.add(g.toJson(this));
		try {
			Files.write(Paths.get("test.json"), lignes);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(final Observable o, final Object arg) {
		// TODO Auto-generated method stub

	}
}
