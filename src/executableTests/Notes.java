/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

V2_3 objectifs :
- Contrer le joueur gagnant ( score syscontrol 2 / 3+   ---  ?)
- Ignorer les drones en lock (facile)
- Barycentre gambling (facile -> si tous arrive, alors gambling)
- (Current bug) Choix des cibles : Closest worlds ? Indefended worlds ? (2 joueurs ?)

----- Verifier si la capture de monde par excedent en phase de greedy ne pose pas soucis

> meilleure utilisation des defenseurs / reatribution des defenseurs
> Anticipation du owner des mondes sacrifies / Conquis

+ Encapsuler le concept de threat, externaliser vers common



-------------------------------------------------------------------------------------------
 Quand un defenseur depasse la moitie de la distance, il doit tenter de squatter / participer a une attaque.
(au pire on est quand meme positif)

--> Quand def > 1/2 d nearest, alors il est relaché.
--> Quand un dr ennemi a 90% de vitesse max vers un monde, alors il est a destination de ce monde a T = d/100.

V2_5 objectifs :
--> prendre en compte les captures futures pour attaques opportunistes + defenseurs
--> prendre en compte les captures futures (y compris les notres) pour plannifier les attaque2
--> Utiliser un mode defense special en cas de domination de l'adversaire
--> ++?++ Detecter quand on a un bord. (et utiliser un mode defense spécial ?)

 */

package executableTests;

/**
 *
 * @author Jahan
 */
public class Notes {
    
}
