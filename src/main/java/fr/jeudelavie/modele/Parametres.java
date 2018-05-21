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

import org.apache.commons.collections4.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.jeudelavie.modele.carte.Carte;
import fr.jeudelavie.modele.carte.Case;
import fr.jeudelavie.modele.cellule.ConditionReproduction;
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

		final TypeEtreVivant cell = new TypeEtreVivant("Cellule");

		final List<ConditionReproduction> listeConditions = new ArrayList<>();

		ConditionReproduction condition;
		// ancien type == none ou nul
		condition = new ConditionReproduction(null);
		condition.ajouterSousCondition(cell, OperateurCompare.EGAL, 3);

		listeConditions.add(condition);

		// ancien type == cell
		condition = new ConditionReproduction(cell);
		condition.ajouterSousCondition(cell, OperateurCompare.SUP_EGAL, 2);
		condition.ajouterSousCondition(cell, OperateurCompare.INF_EGAL, 3);

		listeConditions.add(condition);

		// 0 && q == 3 => 1
		// 1 && q < 2 || q > 3 => 0
		ajouterGroupeReproduction("Groupe cellules", cell, listeConditions);

		ajouterTypeEtreVivant(cell);
	}

	public void setJeuDeLaVie2() {

		final TypeEtreVivant cellH = new TypeEtreVivant("Cellule Homme");
		cellH.setCouleur(Color.BLUE);
		final TypeEtreVivant cellF = new TypeEtreVivant("Cellule Femme");
		cellF.setCouleur(Color.PINK);

		final List<ConditionReproduction> listeConditions = new ArrayList<>();

		// ancien type == none ou nul
		ConditionReproduction condition = new ConditionReproduction(null);
		condition.ajouterSousCondition(TypeEtreVivant.ALL, OperateurCompare.EGAL, 3);
		listeConditions.add(condition);

		condition = new ConditionReproduction(TypeEtreVivant.ANY);
		condition.ajouterSousCondition(TypeEtreVivant.ALL, OperateurCompare.SUP_EGAL, 2);
		condition.ajouterSousCondition(TypeEtreVivant.ALL, OperateurCompare.INF_EGAL, 3);
		listeConditions.add(condition);

		final Map<TypeEtreVivant, Integer> map = new HashMap<>();
		map.put(cellF, Integer.valueOf(1));
		map.put(cellH, Integer.valueOf(1));
		// 0 && q == 3 => 1
		// 1 && q < 2 || q > 3 => 0
		ajouterGroupeReproduction("Groupe cellules", map, listeConditions);

		ajouterTypeEtreVivant(cellH);
		ajouterTypeEtreVivant(cellF);
	}

	public void ajouterTypeEtreVivant(final TypeEtreVivant t) {
		this.listeTypeEtreVivant.add(t);
		this.observable.setChanged();
		this.observable.notifyObservers();
	}

	public void ajouterGroupeReproduction(final String nom, final TypeEtreVivant typeCree,
			final List<ConditionReproduction> conditions) {
		ajouterGroupeReproduction(new GroupeReproduction(nom, typeCree, conditions));
	}

	public void ajouterGroupeReproduction(final String nom, final Map<TypeEtreVivant, Integer> typesCreesPonderes,
			final List<ConditionReproduction> conditions) {
		ajouterGroupeReproduction(new GroupeReproduction(nom, typesCreesPonderes, conditions));
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
		carte.visiter(c -> {
			if (r.nextDouble() > 0.5) {
				final EtreVivant e = new EtreVivant();
				e.setCaseCourrante(c);
				e.setType(
						Parametres.this.listeTypeEtreVivant.get(r.nextInt(Parametres.this.listeTypeEtreVivant.size())));
				c.setEtreVivant(e);
			}
		});
		return carte;
	}

	public int tourSuivant(final Carte carte) {
		final Map<Case, TypeEtreVivant> map = new HashMap<>();
		carte.visiter(c -> {
			final TypeEtreVivant prev = c.getTypeEtreVivant();
			final TypeEtreVivant next = c.calculerProchaineGeneration(Parametres.this);
			if ((prev == null && next != null) || (prev != null && !prev.equals(next))) {
				map.put(c, next);
			}
		});
		map.entrySet().parallelStream().parallel().forEach(e -> e.getKey().setTypeEtreVivant(e.getValue()));
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

	/**
	 * @return Type d'etre vivant parmis les conditions à seuil positif
	 */
	public TypeEtreVivant seReproduire(final TypeEtreVivant ancienType, final Map<TypeEtreVivant, Long> map) {
		final List<TypeEtreVivant> typesDesConditions = this.listeGroupes.stream()
				.filter(g -> g.isReproductible(ancienType, map)).map(c -> c.seReproduire())
				.collect(Collectors.toList());
		return CollectionUtils.isEmpty(typesDesConditions)
				? null : typesDesConditions.get(new Random().nextInt(typesDesConditions.size()));
	}

	@Override
	public void update(final Observable o, final Object arg) {
		// TODO Auto-generated method stub

	}
}
