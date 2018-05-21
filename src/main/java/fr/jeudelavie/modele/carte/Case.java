package fr.jeudelavie.modele.carte;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.jeudelavie.modele.Parametres;
import fr.jeudelavie.modele.cellule.EtreVivant;
import fr.jeudelavie.modele.cellule.TypeEtreVivant;
import lombok.Getter;

public class Case implements Observer {

	@Getter
	private final int x;

	@Getter
	private final int y;

	private final Map<Direction, Case> voisines = new HashMap<>();

	@Getter
	private EtreVivant etreVivant;

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

	public TypeEtreVivant getTypeEtreVivant() {
		return this.etreVivant != null ? this.etreVivant.getType() : null;
	}

	public void setTypeEtreVivant(final TypeEtreVivant newType) {
		final TypeEtreVivant t = getTypeEtreVivant();
		if (t != newType) {
			if (newType == null) {
				setEtreVivant(null);
			} else {
				final EtreVivant e = new EtreVivant();
				e.setCaseCourrante(this);
				e.setType(newType);
				setEtreVivant(e);
			}
		}
	}

	public void setEtreVivant(final EtreVivant e) {
		this.etreVivant = e;
	}

	@Override
	public void update(final Observable o, final Object arg) {}

	public TypeEtreVivant calculerProchaineGeneration(final Parametres params) {
		final List<TypeEtreVivant> typesVoisines = this.voisines.values().stream()
				.filter(v -> v.etreVivant != null && v.etreVivant.getType() != null).map(v -> v.etreVivant.getType())
				.collect(Collectors.toList());

		final Map<TypeEtreVivant, Long> map = typesVoisines.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		final TypeEtreVivant typeActuel = this.etreVivant == null ? null : this.etreVivant.getType();

		final TypeEtreVivant nouveauType = params.seReproduire(typeActuel, map);

		return nouveauType;
	}
}
