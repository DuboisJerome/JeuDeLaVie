package fr.jeudelavie.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import fr.jeudelavie.modele.Parametres;
import fr.jeudelavie.modele.carte.Carte;
import fr.jeudelavie.modele.cellule.GroupeReproduction;
import fr.jeudelavie.modele.cellule.TypeEtreVivant;
import fr.jeudelavie.util.PublicObservable;
import lombok.Getter;

public class FenetrePrincipale extends JFrame implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = -3803521320772643218L;

	private static final FenetrePrincipale INSTANCE = new FenetrePrincipale();

	public static FenetrePrincipale getInstance() {
		return INSTANCE;
	}

	private Carte carte = null;

	private int nbGeneration = 0;

	private final JToolBar toolBar;

	private Parametres parametres;

	private DefaultListModel<TypeEtreVivant> modelListeTypeEtreVivant;

	private DefaultListModel<GroupeReproduction> modelListeGroupeReproduction;

	@Getter
	private final PublicObservable observableParametres = new PublicObservable();

	private CarteUI carteUI;

	private FenetrePrincipale() {
		setLayout(new BorderLayout());
		setBackground(Color.CYAN);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.toolBar = createJToolBar();
	}

	private JToolBar createJToolBar() {

		final int delayInitial = 200;
		final JButton btnStartAndStop = new JButton("Start");
		final Timer timer = new Timer(delayInitial, e -> {
			this.parametres.tourSuivant(FenetrePrincipale.this.carte);
			this.carteUI.repaint();
			btnStartAndStop.setText("Stop (" + this.nbGeneration + ")");
			++this.nbGeneration;
		});

		btnStartAndStop.addActionListener(e -> {
			if (timer.isRunning()) {
				timer.stop();
				btnStartAndStop.setText("Start (" + this.nbGeneration + ")");
			} else {
				timer.start();
				btnStartAndStop.setText("Stop (" + this.nbGeneration + ")");
			}
		});

		final JButton btnNouvellePartie = new JButton("Nouvelle partie");
		btnNouvellePartie.addActionListener(e -> {
			this.nbGeneration = 0;
			timer.stop();
			btnStartAndStop.setText("Start (" + this.nbGeneration + ")");
			nouvellePartie();
		});

		final SpinnerNumberModel modelTimer = new SpinnerNumberModel(delayInitial, 10, 1000, 1);
		final JSpinner inputVitesseTimer = new JSpinner(modelTimer);
		final JLabel labelTimer = new JLabel("Timer", SwingConstants.TRAILING);
		labelTimer.setLabelFor(inputVitesseTimer);
		inputVitesseTimer.addChangeListener(e -> timer.setDelay(modelTimer.getNumber().intValue()));

		final SpinnerNumberModel modelLongueur = new SpinnerNumberModel(150, 10, 1000, 1);
		final JSpinner inputLongueur = new JSpinner(modelLongueur);
		final JLabel labelLongueur = new JLabel("Longueur", SwingConstants.TRAILING);
		labelLongueur.setLabelFor(inputLongueur);
		inputLongueur.addChangeListener(e -> this.parametres.setLongueur(modelLongueur.getNumber().intValue()));

		final SpinnerNumberModel modelLargeur = new SpinnerNumberModel(150, 10, 1000, 1);
		final JSpinner inputLargeur = new JSpinner(modelLargeur);
		final JLabel labelLargeur = new JLabel("Largeur", SwingConstants.TRAILING);
		labelLargeur.setLabelFor(inputLargeur);
		inputLargeur.addChangeListener(e -> this.parametres.setLargeur(modelLargeur.getNumber().intValue()));

		this.modelListeTypeEtreVivant = new DefaultListModel<>();
		final JList<TypeEtreVivant> listeTypeEtreVivant = new JList<>(this.modelListeTypeEtreVivant);
		final JLabel labelListeTypeEtreVivant = new JLabel("Liste type être vivant", SwingConstants.TRAILING);
		labelListeTypeEtreVivant.setLabelFor(listeTypeEtreVivant);

		this.modelListeGroupeReproduction = new DefaultListModel<>();
		final JList<GroupeReproduction> listeGroupeReproduction = new JList<>(this.modelListeGroupeReproduction);
		final JLabel labelListeGroupeReproduction = new JLabel("Liste groupe reproduction", SwingConstants.TRAILING);
		labelListeGroupeReproduction.setLabelFor(listeGroupeReproduction);

		final JButton btnSauvegarder = new JButton("Sauvegarder");
		btnSauvegarder.addActionListener(e -> this.parametres.sauvegarder());

		final JFrame popup = new JFrame("Paramètres");
		popup.setLayout(new GridLayout(0, 2));
		popup.add(labelTimer);
		popup.add(inputVitesseTimer);
		popup.add(labelLongueur);
		popup.add(inputLongueur);
		popup.add(labelLargeur);
		popup.add(inputLargeur);
		popup.add(labelListeTypeEtreVivant);
		popup.add(listeTypeEtreVivant);
		popup.add(labelListeGroupeReproduction);
		popup.add(listeGroupeReproduction);
		popup.add(btnSauvegarder);
		popup.pack();

		final JButton btnParametres = new JButton("Paramètres");
		btnParametres.addActionListener(e -> popup.setVisible(true));

		final JToolBar tb = new JToolBar("Toolbar");
		tb.add(btnStartAndStop);
		tb.add(btnNouvellePartie);
		tb.add(btnParametres);

		return tb;
	}

	public void nouvellePartie() {
		this.parametres = new Parametres();
		this.parametres.getObservable().addObserver(this);
		this.observableParametres.addObserver(this.parametres);
		this.parametres.setJeuDeLaVie();
		this.carte = this.parametres.genererCarte();
		final int w = this.parametres.getLongueur() * CarteUI.LARGEUR_CASE;
		final int h = this.parametres.getLargeur() * CarteUI.HAUTEUR_CASE;
		final Dimension preferredSize = new Dimension(w, h);
		this.carteUI = new CarteUI(this.carte);
		this.carteUI.setPreferredSize(preferredSize);

		final Container p = getContentPane();
		p.removeAll();
		p.add(this.toolBar, BorderLayout.NORTH);
		p.add(this.carteUI, BorderLayout.CENTER);

		pack();

		setVisible(true);
	}

	@Override
	public void update(final Observable o, final Object arg) {
		if (this.parametres != null && o.equals(this.parametres.getObservable())) {
			// type etre vivant
			this.modelListeTypeEtreVivant.clear();
			this.parametres.getListeTypeEtreVivant().forEach(t -> this.modelListeTypeEtreVivant.addElement(t));
			// groupe reproduction
			this.modelListeGroupeReproduction.clear();
			this.parametres.getListeGroupes().forEach(t -> this.modelListeGroupeReproduction.addElement(t));
		}
	}
}
