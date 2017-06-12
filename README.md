# SchoolProject2017


Ce serveur utilise 2 thread :

Recéption
Émission

Ces deux ont chancun leur fonction propre :

Récéption : Est chargé de stocké les données recu 
Émission  : Est chargé d'envoyer les données en bluetooth


### Attention !!

Il est obligatoire de configurer votre daemon SDP pour qu'il puisse proposer la connection en port série.

Pour ce faire il faut vérifier l'existence d'un service Seial Port
Pour afficher cette information il est nécéssaire de disposer des droits admin :

```
(sudo) sdptool browse local
```
une liste non exhaustive s'affiche, et chercher l'existence de cette partie :

![alt text](https://puu.sh/wi3Ad.jpg)

# Certaines informations (Version, RecHandle peuvent changer !)


Si cette ligne n'éxiste pas il faut la crée avec la commande :
```
(sudo) sdptool add [--channel=CHANNEL] service
```

La partie entre crochet n'est pas obligatoire, sauf si vous voulez utiliser un autre channel que le channel 1 !
