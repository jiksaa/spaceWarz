package be.ephec.controler;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import be.ephec.model.area.Area;
import be.ephec.model.area.Case;
import be.ephec.model.area.Coord;
import be.ephec.model.pions.AdmiralSpaceCraft;
import be.ephec.model.pions.DeathStar;
import be.ephec.model.pions.Pion;
import be.ephec.model.pions.SpaceCraft;
import be.ephec.model.pions.SpaceHunter;
import be.ephec.net.Client;
//import be.ephec.net.Coord;
import be.ephec.net.Server;
import be.ephec.view.MyJLabels;
import be.ephec.view.PlayingViewNew;
import be.ephec.view.launcher.Launcher;


public class Controler {
	
	/* DEBUG */
	private boolean debug = true; /* change true to see debug line */
	
	/*Model component*/
	private Area gameArea;
	private Area opponentArea;
	
	private AdmiralSpaceCraft adm;
	private DeathStar star;
	private SpaceCraft spc1;
	private SpaceCraft spc2;
	private SpaceHunter hunt1;
	private SpaceHunter hunt2;
	private int spaceshipCounter = 0;
	
	/*View component*/
	private PlayingViewNew gamingView;
	private Launcher gameLauncher;
	private int choice = -1;
	
	/*Controler fields*/
	private boolean gameReady = false;
	private boolean partieFinie = false;
	private boolean isServer;
	private String ipAdv;
	
	/* Socket */
	private int numPort = 10430;
	private Server server = null;
	private Client client = null;
	
	/* Fire Variable */
	
	private Case myTarget; // where I shoot
	private Case opponentTarget; // Where the opponent shoot
	

	
	
	
	
	public Controler(Area gameArea, Area opponentArea, AdmiralSpaceCraft adm,
			DeathStar star, SpaceCraft spc1, SpaceCraft spc2,
			SpaceHunter hunt1, SpaceHunter hunt2, PlayingViewNew gamingView,
			Launcher gameLauncher) {
		super();
		this.gameArea = gameArea;
		this.opponentArea = opponentArea;
		this.adm = adm;
		this.star = star;
		this.spc1 = spc1;
		this.spc2 = spc2;
		this.hunt1 = hunt1;
		this.hunt2 = hunt2;
		this.gamingView = gamingView;
		this.gameLauncher = gameLauncher;
		addListenersOnView();
	}

	/**
	 * Put the specialised Pion on the gameArea at the given position
	 * 
	 * @param pion Pion who will place into the Area
	 * @param x x position
	 * @param y y position
	 * @return Return 0 if the placement is OK or -1 if it's not possible to place the Pion at given position.
	 * 
	 */
	
	
	private int putAdmiralSpaceCraft( int x, int y){
		if(AdmiralSpaceCraft.getNbInstance() == 1) return -1;
		if(x > gameArea.getSide()-4) return -1;
		for(int i = x; i<x+4; i++){
			if(gameArea.getCase(i, y).isCannotBeUsed() == true && 
			   gameArea.getCase(i, y).getUsedBy() != null) return -1;
		}
		for(int i = x, j = 0; i<x+4; i++, j++){
			gameArea.getCase(i, y).setUsedBy(adm);
			gameArea.getCase(i ,y).setCannotBeUsed(true);
			adm.setCase(gameArea.getCase(i, y), j);
		}
		AdmiralSpaceCraft.setNbInstance(AdmiralSpaceCraft.getNbInstance()+1);
		return 0;
	}
	
	
	
	private int putSpaceHunter(int x, int y){
		if(SpaceHunter.getNbInstance() == 2) return -1;
		if(y > gameArea.getSide()-2) return -1;
		for(int i = y;i<y+2;i++){
			if(gameArea.getCase(x, i).isCannotBeUsed() == true && 
			   gameArea.getCase(x, i).getUsedBy() != null) return -1;
		}
		for(int i = y, j = 0; i<y+2; i++, j++){
			if(SpaceHunter.getNbInstance()== 0){
				gameArea.getCase(x, i).setUsedBy(hunt1);
				gameArea.getCase(x ,i).setCannotBeUsed(true);
				hunt1.setCase(gameArea.getCase(x, i), j);
			}
			else{
				gameArea.getCase(x, i).setUsedBy(hunt2);
				gameArea.getCase(x ,i).setCannotBeUsed(true);
				hunt2.setCase(gameArea.getCase(x, i), j);
			}
		}
		SpaceHunter.setNbInstance(SpaceHunter.getNbInstance()+1);
		return 0;
	}
	
	

	private int putSpaceCraft(int x, int y){
		if(SpaceCraft.getNbInstance() == 2) return -1;
		if(x<0 || x > gameArea.getSide()-2) return -1;
		if(y<0 || y > gameArea.getSide()-2) return -1;
		if(gameArea.getCase(x, y).isCannotBeUsed() == true && 
		   gameArea.getCase(x, y).getUsedBy() != null) return -1;
		if(gameArea.getCase(x+1, y).isCannotBeUsed() == true && 
		   gameArea.getCase(x+1, y).getUsedBy() != null) return -1;
		if(gameArea.getCase(x, y+1).isCannotBeUsed() == true && 
		   gameArea.getCase(x, y+1).getUsedBy() != null) return -1;
		
		if(SpaceCraft.getNbInstance() == 0){
			gameArea.getCase(x, y).setUsedBy(spc1);
			gameArea.getCase(x ,y).setCannotBeUsed(true);
			spc1.setCase(gameArea.getCase(x, y), 0);
	
			gameArea.getCase(x+1, y).setUsedBy(spc1);
			gameArea.getCase(x+1 ,y).setCannotBeUsed(true);
			spc1.setCase(gameArea.getCase(x+1, y), 1);
	
			gameArea.getCase(x, y+1).setUsedBy(spc1);
			gameArea.getCase(x ,y+1).setCannotBeUsed(true);
			spc1.setCase(gameArea.getCase(x, y+1), 2);
		}
		else{
			gameArea.getCase(x, y).setUsedBy(spc2);
			gameArea.getCase(x ,y).setCannotBeUsed(true);
			spc2.setCase(gameArea.getCase(x, y), 0);
	
			gameArea.getCase(x+1, y).setUsedBy(spc2);
			gameArea.getCase(x+1 ,y).setCannotBeUsed(true);
			spc2.setCase(gameArea.getCase(x+1, y), 1);
	
			gameArea.getCase(x, y+1).setUsedBy(spc2);
			gameArea.getCase(x ,y+1).setCannotBeUsed(true);
			spc2.setCase(gameArea.getCase(x, y+1), 2);
		}
		SpaceCraft.setNbInstance(SpaceCraft.getNbInstance()+1);
		return 0;
	}
	
	
	
	
	private int putDeathStar(int x, int y){
		if(DeathStar.getNbInstance() == 1) return -1;
		if(x<1 || x > gameArea.getSide()-2) return -1;
		if(y<1 || y > gameArea.getSide()-2) return -1;
		for(int i = x-1; i < x+2 ; i++){
			if(gameArea.getCase(i, y).isCannotBeUsed() == true && 
			   gameArea.getCase(i, y).getUsedBy() != null) return -1;
		}
		if(gameArea.getCase(x, y-1).isCannotBeUsed() == true && 
		   gameArea.getCase(x, y-1).getUsedBy() != null) return -1;
		if(gameArea.getCase(x, y+1).isCannotBeUsed() == true && 
		   gameArea.getCase(x, y+1).getUsedBy() != null) return -1;

		for(int i = x-1, j = 0; i < x+2 ; i++, j++){
			gameArea.getCase(i, y).setUsedBy(star);
			gameArea.getCase(i ,y).setCannotBeUsed(true);
			star.setCase(gameArea.getCase(i, y), j);
		}
		gameArea.getCase(x, y-1).setUsedBy(star);
		gameArea.getCase(x ,y-1).setCannotBeUsed(true);
		star.setCase(gameArea.getCase(x, y-1), 3);
		
		gameArea.getCase(x, y+1).setUsedBy(star);
		gameArea.getCase(x ,y+1).setCannotBeUsed(true);
		star.setCase(gameArea.getCase(x, y+1), 4);
		
		DeathStar.setNbInstance(DeathStar.getNbInstance()+1);
		
		return 0;
	}
	

	
	
	private Pion fireOnCase(int x, int y){
		
			
		// verifie si la coord recue est utilis�e
		if(gameArea.getCase(x, y).getUsedBy() != null){
			gameArea.getCase(x, y).getUsedBy().isTouch();
			gameArea.getCase(x, y).setTouch(true);
			return gameArea.getCase(x, y).getUsedBy();
		}
		gameArea.getCase(x, y).setTouch(true);
		return null;
	}
	
	/**
	 * 
	 * @param grille : true pour la grille du joueur, false pour la grille adverse
	 * @param c : coordonn�es du tir
	 * @param touch : true si touch�, false si dans l'eau
	 */
	private void modifyImage(boolean joueur, Coord coord, boolean touch){
		int l = coord.getX(); // R�cup�re la ligne et la colonne de l'image � modifier
		int c = coord.getY();
		if(joueur){
			if(touch) gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship_touched.png")));
			else gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/plouf.png")));
		} else {
			if(touch) gamingView.getTabOpponentLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship_touched.png")));
			else gamingView.getTabOpponentLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/plouf.png")));
		}	
	}
	
	private String touchOrExplod(Pion touchPion){
		if(touchPion.isDead()) return(touchPion.getName()+" a �t� touch� et a explos� !");
		return(touchPion.getName()+" a �t� touch� !");
	}
	
	private void resetPlacement(){
		for(int i = 0; i<gameArea.getSide();i++){
			for(int j = 0; j<gameArea.getSide();j++){
				gameArea.getCase(i, j).setUsedBy(null);
				gameArea.getCase(i, j).setCannotBeUsed(false);
			}
		}
	}
	


	
	


	
	private void addListenersOnView(){
		
		/*
		 * PlayingView Listeners
		 */
		gamingView.getQuitButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitGameEvent(e);
			}
		});
		
		gamingView.getResetButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetSpaceships();
			}
		});
		
		gamingView.getReadyButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readyEvent(e);
			}
		});
		
		for(int i=0;i<4;i++){
			gamingView.getTabVaisseaux()[0][i].addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt){
					selectCraftEvent(evt);
				}});
		}
		
		for(int l=0;l<gamingView.getL();l++){
			
			for(int c=0;c<gamingView.getC();c++){
				gamingView.getTabPlayerLabel()[l][c].addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						putPionViewEvent(evt);
					}
				});	
			}
		}
		
		for(int l=0;l<gamingView.getL();l++){
			
			for(int c=0;c<gamingView.getC();c++){
				gamingView.getTabOpponentLabel()[l][c].addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						do{
							fireEvent(evt); // R�cupere la coordonn�e du tir effectu�
							if(myTarget.isTouch()) JOptionPane.showMessageDialog(null, "Ce tir a d�j� �t� fait !");
						}while(myTarget.isTouch());  // on peut "recliquer" si la case avait d�j� �t� cliqu�e
					}
				});	
			}
		}
		
		/*
		 * Launcher Listeners
		 * 
		 */
		gameLauncher.getQuitButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				quitGameEvent(evt);				
			}
		});
		
		gameLauncher.getPlayServerButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientOrServer(true);
			}
		});
		
		gameLauncher.getPlayClientButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientOrServer(false);
			}
		});
		
	}
	
	
	private void clientOrServer(boolean b){	
		setServer(b);
		if(debug)System.out.println("debug isServer : "+b);
		if(getIpFromLauncher()){
			if(isServer){
				JOptionPane.showMessageDialog(null, "En attente de la connection adverse...");
				try {
					//Creation du serveur
					server = new Server(numPort);
					if(debug) System.out.println("Server active and client accepted");
					server.write(new String("Bienvenue sur le seveur"));				
				} catch (IOException e) {
					socketExceptionCatch(server);
				}

			} else {
				
				// Cr�ation du Client
				try {
					client = new Client(ipAdv,numPort);
					String welcomStr = client.read(String.class);
					if(debug) System.out.println("Connected to server !");
					if(debug) System.out.println(welcomStr);
				} catch (IOException | ClassNotFoundException e) {
					socketExceptionCatch(client);
				}
			}
			gameLauncher.dispose();
			gamingView.setVisible(true);
		}
		else JOptionPane.showMessageDialog(null, "Adresse Ip non valide");
	}
		
	private void setServer(boolean isServer){
		this.isServer = isServer;		
	}
	
	
	
	private boolean getIpFromLauncher(){
		ipAdv = gameLauncher.getIpAdv().getText();	
		try {
			@SuppressWarnings("unused")
			InetAddress ip = InetAddress.getByName(ipAdv);
		} catch (UnknownHostException e) {
			return false;
		}
		/*try {
			return ip.isReachable(10000);
		} catch (IOException e) {
			if(debug) System.out.println("not reachable");
			return false;
		}*/
		return true;
	}
	

	private void selectCraftEvent(MouseEvent evt1){
		int c = ((MyJLabels)evt1.getSource()).getColumn();
		choice = c;
	}

	
	
	private void quitGameEvent(ActionEvent e){
		gamingView.dispose();
		if(server != null && client != null){
			try {
				if(isServer)server.close();
				else client.close();
			} catch (IOException e1) {}
		}
		JOptionPane.showMessageDialog(null, "Merci d'avoir jou� � notre jeu !");
		System.exit(0);
	}
	
	
	
	private void resetSpaceships(){
		if(JOptionPane.showConfirmDialog(gamingView.getBackgroundLabel(), "Etes vous s�r de vouloir r�initialiser la position de tous les vaisseaux ?")==0){
			for(int c=0;c<gamingView.getC();c++){
				for(int l=0;l<gamingView.getL();l++){
					gamingView.getTabPlayerLabel()[l][c].setIcon(null);
				}
			}	
			resetPlacement();
			SpaceHunter.setNbInstance(0);
			SpaceCraft.setNbInstance(0);
			AdmiralSpaceCraft.setNbInstance(0);
			DeathStar.setNbInstance(0);
			spaceshipCounter = 0;
		}
	}
	
	private void readyEvent(ActionEvent e){
		if(spaceshipCounter==6){
			gameReady = true;
			JOptionPane.showMessageDialog(null, "�tat \"pr�t\" envoy� au serveur.. \n En attente de l'adversaire..");
			if(isServer){
				try {
					server.write(new String("Je suis pr�t !"));
					String str = server.read(String.class);
					if(debug) System.out.println(str);
				} catch (Exception e1) {
					socketExceptionCatch(server);
				}				
			}else {
				try {
					client.write(new String("Je suis pr�t !"));
					String str = client.read(String.class);
					if(debug) System.out.println(str);
				} catch (Exception e1) {
					socketExceptionCatch(client);
				}
			}
			if(debug) System.out.println("Les pions sont plac�s, le jeu peut commencer...");
		}else JOptionPane.showMessageDialog(null, "Il vous reste "+(6-spaceshipCounter)+" vaisseaux � placer !");
		
	}
	
	
	private void fireEvent(MouseEvent evt){
		int l = ((MyJLabels)evt.getSource()).getLine();
		int c = ((MyJLabels)evt.getSource()).getColumn();
		
		Coord myTargetPosition = new Coord(l,c); // cr�� une coordonn�e du clic
		myTarget.setPosition(myTargetPosition); // cr�e l'objet myTarget qui sera utilis�e dans fireAction
		
		/*if(fireOnCase(l, c)!=null) {
			if(debug) System.out.println("Touch� !");
			gamingView.getTabOpponentLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/touched.png")));
			fireOnCase(l, c).isTouch();
		}
		else{
			if(debug) System.out.println("Tir perdu dans les profondeurs de l'espace !");
			gamingView.getTabOpponentLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/plouf.png")));
		}*/	
	}
	
	private void fireAction(){
		if(isServer){
			//implement� par john
		} else {
			while(!partieFinie){
				try {
					opponentTarget = client.read(Case.class); // Lis la case que vise l'adversaire
					Pion touchPion = fireOnCase(opponentTarget.getPosition().getX(),opponentTarget.getPosition().getY()); // Sort le pion qui est touch� (peut-etre null)
					if(touchPion != null){ //  si un pion existe � cet endroit
						client.write(opponentTarget.getPosition()); // envoi la position du tir re�u
						client.write(touchPion.getName()); // .. et le nom du pion qui est touch�
						modifyImage(true, opponentTarget.getPosition(), true);
					} else {
						client.write(false);  // si pas de vaisseaux touch�
						modifyImage(true, opponentTarget.getPosition(), false); // indique dans la grille du joueur o� l'adversaire vient de tirer
					}
					if(!partieFinie){
						// Le do ... while est mis dans le mouse listener (pas sur que �a soit tip top correct...
						client.write(myTarget.getPosition());
						if(client.read(boolean.class)) { // Lit la r�ponse suite au tir
							JOptionPane.showMessageDialog(null, "Bravo ! Vous avez touch� : "+client.read(String.class));
							modifyImage(false, myTarget.getPosition(), true);
						} else {
							JOptionPane.showMessageDialog(null, "Votre tir s'est perdu dans les profondeurs de l'espace...");
							modifyImage(false, myTarget.getPosition(), false);
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}
	
	
	private void putPionViewEvent(MouseEvent evt) {
		int l = ((MyJLabels)evt.getSource()).getLine();
		int c = ((MyJLabels)evt.getSource()).getColumn();
		switch(choice){
		case 0 :
			if(SpaceHunter.getNbInstance()==0){
				if(putSpaceHunter(l, c)==0){
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceHunter/SpaceHunter_1_1.png")));
					gamingView.getTabPlayerLabel()[l][c+1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceHunter/SpaceHunter_1_2.png")));
					if(debug) System.out.println("hunt :"+hunt1.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else if(SpaceHunter.getNbInstance() == 1){
				if(putSpaceHunter(l, c)==0){
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceHunter/SpaceHunter_1_1.png")));
					gamingView.getTabPlayerLabel()[l][c+1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceHunter/SpaceHunter_1_2.png")));
					if(debug) System.out.println("hunt2 :"+hunt2.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else JOptionPane.showMessageDialog(null, "Nombre de vaisseau maximum atteind sur la grille");
			break;
		case 1 : 
			if(SpaceCraft.getNbInstance()== 0){
				if(putSpaceCraft(l, c) == 0){
					gamingView.getTabPlayerLabel()[l][c+1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_1.png")));
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_2.png")));
					gamingView.getTabPlayerLabel()[l+1][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_3.png")));
					if(debug) System.out.println("spcCraft "+spc1.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else if(SpaceCraft.getNbInstance() == 1){
				if(putSpaceCraft(l, c) == 0){
					gamingView.getTabPlayerLabel()[l][c+1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_1.png")));
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_2.png")));
					gamingView.getTabPlayerLabel()[l+1][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/SpaceCraft/SpaceCraft_1_3.png")));
					if(debug) System.out.println("spcCraft2 "+spc2.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else JOptionPane.showMessageDialog(null, "Nombre de vaisseau maximum atteind sur la grille");
			
			break;
		case 2 :
			if(AdmiralSpaceCraft.getNbInstance() == 0){
				if(putAdmiralSpaceCraft(l, c) == 0){
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/admiralSpaceCraft/admiralSpaceCraft_1_1.png")));
					gamingView.getTabPlayerLabel()[l+1][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/admiralSpaceCraft/admiralSpaceCraft_1_2.png")));
					gamingView.getTabPlayerLabel()[l+2][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/admiralSpaceCraft/admiralSpaceCraft_1_3.png")));
					gamingView.getTabPlayerLabel()[l+3][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/admiralSpaceCraft/admiralSpaceCraft_1_4.png")));
					if(debug) System.out.println("adm = "+adm.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else JOptionPane.showMessageDialog(null, "Nombre de vaisseau maximum atteind sur la grille");
			break;
		case 3 :
			if(DeathStar.getNbInstance() == 0){
				if( putDeathStar(l, c) == 0){
					gamingView.getTabPlayerLabel()[l][c+1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/DeathStar/deathStar_1_1.png")));
					gamingView.getTabPlayerLabel()[l-1][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/DeathStar/deathStar_1_2.png")));
					gamingView.getTabPlayerLabel()[l][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/DeathStar/deathStar_1_3.png")));
					gamingView.getTabPlayerLabel()[l+1][c].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/DeathStar/deathStar_1_4.png")));
					gamingView.getTabPlayerLabel()[l][c-1].setIcon(new ImageIcon(getClass().getClassLoader().getResource("img/spaceship/DeathStar/deathStar_1_5.png")));
					if(debug) System.out.println("star = "+star.getCase(0));
					spaceshipCounter++;
				}
				else JOptionPane.showMessageDialog(null, "Emplacement non valide");
			}
			else JOptionPane.showMessageDialog(null, "Nombre de vaisseau maximum atteind sur la grille");
			break;
		}
	}
	
	private void socketExceptionCatch(Server servSock){
		if(debug) System.out.println("flow error on server socket");
		try {
			servSock.close();
		} catch (IOException e) {
			if(debug) System.out.println("error socket closing");
		}
		System.exit(-1);
	}
	
	private void socketExceptionCatch(Client clientSock){
		if(debug) System.out.println("flow error on client socket");
		try {
			clientSock.close();
		} catch (IOException e) {
			if(debug) System.out.println("error client socket closing");
		}
		System.exit(-1);
	}
	
}
