package Objets;

import TIM.FenetreTIM;
import Objets.Ballon;

public class ThreadBallon extends Thread {
	FenetreTIM f;
	Ballon bal;
	long t;

	public int threadState = -2;// 0:jamais encore lancé mais lancable 1:
								// lancé -1: en pause -2:pas lancable(objet
								// dans menu)

	public ThreadBallon(FenetreTIM f, Ballon bal) {

		this.f = f;
		this.bal = bal;// Instance de Ballon que g�re notre thread
		t = System.currentTimeMillis();
	}

	public void run() {
		int ms = (int) (1000.0 / (f.fps));

		float coefAction = ms / 1000.0f;
		while (f.b ) {
			
			// coef qui pond�re les incr�mentations pour garantir le
			// m�me comportement � toute valeur de f.fps (valable seulement pour
			// f.speed=1)
			while (f.b && threadState == 1 ) {
				if (bal.estActif()) {
					if (System.currentTimeMillis() - t > ms / f.speed && bal.existe) {
						t = System.currentTimeMillis();

						// on effectue les effets
												// discontinus sur la
													// vitesse de l'instance

						PaireXY accel = bal.accel();// On r�cup�re les
													// modification
													// d'acc�l�ration que l'on
													// subit
						// (Force/masse r�duit � l'acc�l�ration car les deux
						// sont arbitraires ici)

						// Simple m�thode d'euler de r�solution de l'�quation
						// diff�rentielle du mouvement :
						bal.ay = accel.y;
						bal.ax = accel.x;
						bal.vx += bal.ax * (coefAction);
						bal.x += bal.vx * (coefAction);
						bal.vy += bal.ay * (coefAction);
						bal.y += bal.vy * (coefAction);
						float xi=bal.x,yi=bal.y;
						bal.majEffetsDiscontinus();	
						// idem pour la rotation :
						{

							bal.angle += bal.vangle * (coefAction);
						} 

					}

				}

				Thread.yield();
			}
			while (threadState == -1 ) {// boucle dans laquelle le
													// thread rentre lorsqu'il
													// est dans son �tat de
				synchronized(bal)
				{
				try {
							bal.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}							// pause (-1)
			
				}
			}
			

		}

	}
}
