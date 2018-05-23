package fr.jeudelavie.modele.carte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import fr.jeudelavie.modele.Parametres;
import fr.jeudelavie.modele.cellule.EtreVivant;
import fr.jeudelavie.modele.cellule.GroupeReproduction;
import fr.jeudelavie.modele.cellule.TypeEtreVivant;
import lombok.Getter;

public class Case implements Observer {

	@Getter
	private final int x;

	@Getter
	private final int y;

	private final Map<Direction, Case> voisines = new HashMap<>();

	@Getter
	private final List<EtreVivant> listeEtreVivant = new ArrayList<>();

	protected Case() {
		this(0, 0);
	}

	public Case(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Map<Direction, Case> getMapVoisines() {
		return Collections.unmodifiableMap(this.voisines);
	}

	public Collection<Case> getVoisines() {
		return Collections.unmodifiableCollection(this.voisines.values());
	}

	public void setNordOuest(final Case c) {
		this.voisines.put(Direction.NORDOUEST, c);
	};

	public void setNord(final Case c) {
		this.voisines.put(Direction.NORD, c);
	};

	public void setNordEst(final Case c) {
		this.voisines.put(Direction.NORDEST, c);
	};

	public void setOuest(final Case c) {
		this.voisines.put(Direction.OUEST, c);
	};

	public void setEst(final Case c) {
		this.voisines.put(Direction.EST, c);
	};

	public void setSudOuest(final Case c) {
		this.voisines.put(Direction.SUDOUEST, c);
	};

	public void setSud(final Case c) {
		this.voisines.put(Direction.SUD, c);
	};

	public void setSudEst(final Case c) {
		this.voisines.put(Direction.SUDEST, c);
	};

	@Override
	public void update(final Observable o, final Object arg) {}

	public EtreVivant calculerProchaineGeneration(final Parametres params) {
		final List<EtreVivant> listeEtreVivantTmp = new ArrayList<>();
		listeEtreVivantTmp.addAll(this.listeEtreVivant);
		// reproduction avec voisines
		this.voisines.values().stream().filter(v -> CollectionUtils.isNotEmpty(v.getListeEtreVivant()))
				.forEach(v -> v.getListeEtreVivant().stream().forEach(e -> listeEtreVivantTmp.add(e)));

		final Map<TypeEtreVivant, Long> map = listeEtreVivantTmp.stream().map(e -> e.getType())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		final List<GroupeReproduction> grps = params.getListeGroupes().stream().filter(g -> g.isReproductible(map))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(grps)) return null;
		final GroupeReproduction grpGagnant = grps.get(new Random().nextInt(grps.size()));
		final TypeEtreVivant t = grpGagnant.seReproduire();

		final EtreVivant e;
		if (t != null) {
			e = new EtreVivant(this, t);
		} else {
			e = null;
		}
		return e;
	}

	public void ajouterEtreVivant(final EtreVivant e) {
		this.listeEtreVivant.add(e);
	}

	public int nbEtreVivant() {
		return this.listeEtreVivant.size();
	}
}
